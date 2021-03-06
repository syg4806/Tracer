package com.umpa2020.tracer.main.start.racing

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.umpa2020.tracer.R
import com.umpa2020.tracer.constant.Constants.Companion.RACE_RESULT
import com.umpa2020.tracer.constant.Constants.Companion.RANKING_DATA
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.BaseActivity
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class RacingFinishActivity : BaseActivity(), OnSingleClickListener {

  var activity = this
  lateinit var racerData: MapInfo
  lateinit var arrRankingData: MutableList<RankingData>
  lateinit var progressbar: ProgressBar
  lateinit var rankingData: RankingData
  lateinit var mapId: String

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()

    val result = intent.extras!!.getBoolean(RACE_RESULT)

    if (!intent.hasExtra(RANKING_DATA)) {
      // Racing Activity 에서 넘겨준 infoData를 받아서 활용
      racerData = intent.getParcelableExtra("InfoData") as MapInfo
      mapId = racerData.mapId
      val routeGPXUri = intent.getStringExtra(RacingActivity.ROUTE_GPX)
      val racerGPX = Uri.parse(routeGPXUri).gpxToClass()
      val racerSpeeds = racerGPX.getSpeed()
      rankingData = RankingData(
        racerData.makerId,
        UserInfo.autoLoginKey,
        UserInfo.nickname,
        racerData.time,
        false,
        racerSpeeds.max(),
        racerSpeeds.average(),
        null
      )
      launch {

        //성공했다면 랭킹에 등록
        val rankingDataReference = if (result)
          FBRacingRepository().createRankingData(racerData, rankingData, Uri.parse(routeGPXUri))
        else null
        arrRankingData = FBMapRepository().listMapRanking(mapId)

        // 유저 히스토리 등록
        FBUsersRepository().createUserHistory(
          ActivityData(
            mapId, Date().time, racerData.distance, racerData.time,
            if (result) BaseFB.ActivityMode.RACING_SUCCESS else BaseFB.ActivityMode.RACING_FAIL,
            rankingDataReference
          )
        )
        FBMapRepository().getMapInfo(racerData.mapId).let {
          FBAchievementRepository().incrementPlays(it!!.makerId)
        }
        FBUsersRepository().updateUserAchievement(arrRankingData, mapId)

        updateRankingUI(arrRankingData)
        progressbar.dismiss()
      }
    } else {
      launch {
        rankingData = intent.getParcelableExtra<RankingData>(RANKING_DATA)
        mapId = intent.getStringExtra(MAP_ID)
        arrRankingData = FBMapRepository().listMapRanking(mapId)
        updateRankingUI(arrRankingData)
      }
    }
    OKButton.setOnClickListener(this)
    otherPeopleProfileSelect.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v!!.id) {
      // 다 봤다는 표시 - 그래도 앞에 있던 액티비티들을 끄고, 메인 엑티비티 실행
      R.id.OKButton -> {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
      }

      // 리스트 선택화면으로 넘어감
      R.id.otherPeopleProfileSelect -> {
        val intent = Intent(this, AllRankingActivity::class.java)
        intent.putParcelableArrayListExtra("arrRankingData", ArrayList(arrRankingData))
        startActivityForResult(intent, 100)
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == 100) {
      if (resultCode == 100) {
        val getId = data!!.getStringExtra("userId")
        val getNickname = data.getStringExtra("userNickname")
        RacingFinishAnalysisOtherNickname.text = getNickname

        progressbar.show()

        launch {
          FBRacingRepository().getOtherData(mapId, getId!!).let {
            setOtherData(it)
          }
          // TODO 회원탈퇴 방식 getProfileImage에서 검사를 안하기 때문에 새로 검사를 해야하는 함수 작성 필요
          FBProfileRepository().getProfileImage(getId).let {
            if (it == null) {
              RacingFinishAnalysisOtherNickname.text = getString(R.string.unregister_user)
            } else {
              RacingFinishAnalysisOtherProfile.image(it)
            }
            progressbar.dismiss()
          }
        }
      }
    }
    super.onActivityResult(requestCode, resultCode, data)
  }

  private fun setMyUiData(
    rankingData: RankingData,
    resultRankText: Int,
    renewal: Boolean
  ) {
    launch {
      // 나의 기록
      FBProfileRepository().getProfileImage(UserInfo.autoLoginKey)?.let { racingFinishProfileImageView.image(it) }
      //RacingFinishMyNickName.text = UserInfo.nickname

      if (resultRankText == 0) {
        resultRankTextView.text = getString(R.string.fail)
      } else {
        if (renewal) {
          getString(R.string.renewal).show()
          resultRankTextView.text = resultRankText.toRank()
        } else
          resultRankTextView.text = resultRankText.toRank()
      }

      RacingFinishMyLapTime.text = rankingData.challengerTime!!.format(m_s)

      FBProfileRepository().getProfileImage(UserInfo.autoLoginKey)?.let { RacingFinishAnalysisMyProfile.image(it) }
      RacingFinishAnalysisMyNickname.text = UserInfo.nickname

      racerLapTimeTextView.text = rankingData.challengerTime!!.format(m_s)
      racerMaxSpeedTextView.text = rankingData.maxSpeed!!.toDouble().prettyDistance
      racerAvgSpeedTextView.text = rankingData.averageSpeed!!.toDouble().prettyDistance
      progressbar.dismiss()
    }
  }

  private fun setOtherData(rankingData: RankingData) {
   launch {
      FBProfileRepository().getProfileImage(rankingData.challengerId!!)?.let { RacingFinishAnalysisOtherProfile.image(it) }

    }
    otherLapTimeTextView.text = rankingData.challengerTime!!.format(m_s)
    otherMaxSpeedTextView.text = rankingData.maxSpeed!!.toDouble().prettyDistance
    otherAvgSpeedTextView.text = rankingData.averageSpeed!!.toDouble().prettyDistance
  }

  private suspend fun updateRankingUI(rankingDatas: MutableList<RankingData>) {
    var resultRank = 1
    var renewal = false
    rankingDatas.forEach {
      if (rankingData.challengerTime!! > it.challengerTime!!) {
        if (UserInfo.autoLoginKey == it.challengerId) {
          renewal = true
        }
        resultRank++
      }

      arrRankingData = rankingDatas
      setMyUiData(rankingData, resultRank, renewal)

      if (arrRankingData.size >= 1) {
        FBProfileRepository().getProfileImage(arrRankingData[0].challengerId!!)
          ?.let { racingFinishProfileFirst.image(it) }
        racingFinishNicknameFirst.text = arrRankingData[0].challengerNickname
        racingFinishLapTimeFirst.text = arrRankingData[0].challengerTime!!.format(m_s)

        FBProfileRepository().getProfileImage(arrRankingData[0].challengerId!!)
          ?.let { RacingFinishAnalysisOtherProfile.image(it) }

        RacingFinishAnalysisOtherNickname.text = arrRankingData[0].challengerNickname
        otherLapTimeTextView.text = arrRankingData[0].challengerTime!!.format(m_s)
        otherMaxSpeedTextView.text = arrRankingData[0].maxSpeed!!.toDouble().prettyDistance
        otherAvgSpeedTextView.text = arrRankingData[0].averageSpeed!!.toDouble().prettyDistance
      }

      if (arrRankingData.size >= 2) {
        FBProfileRepository().getProfileImage(arrRankingData[1].challengerId!!)
          ?.let { racingFinishProfileSecond.image(it) }
        racingFinishNicknameSecond.text = arrRankingData[1].challengerNickname
        racingFinishLapTimeSecond.text = arrRankingData[1].challengerTime!!.format(m_s)
      }

      if (arrRankingData.size >= 3) {
        FBProfileRepository().getProfileImage(arrRankingData[2].challengerId!!)
          ?.let { racingFinishProfileThird.image(it) }
        racingFinishNicknameThird.text = arrRankingData[2].challengerNickname
        racingFinishLapTimeThird.text = arrRankingData[2].challengerTime!!.format(m_s)
      }
    }
  }

}
