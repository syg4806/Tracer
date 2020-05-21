package com.umpa2020.tracer.main.start.racing

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ActivityData
import com.umpa2020.tracer.dataClass.MapInfo
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.extensions.*
import com.umpa2020.tracer.main.MainActivity
import com.umpa2020.tracer.network.*
import com.umpa2020.tracer.util.OnSingleClickListener
import com.umpa2020.tracer.util.ProgressBar
import com.umpa2020.tracer.util.UserInfo
import kotlinx.android.synthetic.main.activity_racing_finish.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.*


class RacingFinishActivity : AppCompatActivity(), OnSingleClickListener {

  var activity = this
  lateinit var racerData: MapInfo
  lateinit var arrRankingData: MutableList<RankingData>
  lateinit var progressbar: ProgressBar

  var racerSpeeds = mutableListOf<Double>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_racing_finish)

    progressbar = ProgressBar(this)
    progressbar.show()

    // Racing Activity 에서 넘겨준 infoData를 받아서 활용
    racerData = intent.getParcelableExtra("InfoData") as MapInfo
    val result = intent.extras!!.getBoolean("Result")

    val routeGPXUri = intent.getStringExtra(RacingActivity.ROUTE_GPX)
    val racerGPX = Uri.parse(routeGPXUri).gpxToClass()

    racerSpeeds = racerGPX.getSpeed()
    val rankingData = RankingData(
      racerData.makerId,
      UserInfo.autoLoginKey,
      UserInfo.nickname,
      racerData.time,
      false,
      racerSpeeds.max().toString(),
      racerSpeeds.average().toString(),
      null
    )

    MainScope().launch {
      // 유저 히스토리 등록
      FBUsersRepository().createUserHistory(
        ActivityData(racerData.mapId, Date().time, racerData.distance, racerData.time, if (result) BaseFB.ActivityMode.RACING_SUCCESS else BaseFB.ActivityMode.RACING_FAIL)
      )

      FBMapRepository().getMapInfo(racerData.mapId).let {
        FBAchievementRepository().incrementPlays(it!!.makerId)
      }

      //성공했다면 랭킹에 등록
      if (result)
        FBRacingRepository().createRankingData(racerData, rankingData, Uri.parse(routeGPXUri))

      arrRankingData = FBMapRepository().listMapRanking(racerData.mapId)
      FBUsersRepository().updateUserAchievement(arrRankingData, racerData.mapId)
      updateRankingUI(arrRankingData)
      progressbar.dismiss()
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

  @SuppressLint("ShowToast")
  private fun setMyUiData(
    racerSpeeds: MutableList<Double>,
    resultRankText: Int,
    renewal: Boolean
  ) {
    MainScope().launch {
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

      RacingFinishMyLapTime.text = racerData.time.format(m_s)

      FBProfileRepository().getProfileImage(UserInfo.autoLoginKey)?.let { RacingFinishAnalysisMyProfile.image(it) }
      RacingFinishAnalysisMyNickname.text = UserInfo.nickname

      racerLapTimeTextView.text = racerData.time.format(m_s)
      racerMaxSpeedTextView.text = racerSpeeds.max()!!.prettyDistance
      racerAvgSpeedTextView.text = racerSpeeds.average().prettyDistance
      progressbar.dismiss()
    }
  }

  private suspend fun updateRankingUI(rankingDatas: MutableList<RankingData>) {
    var resultRank = 1
    var renewal = false
    rankingDatas.forEach {
      if (racerData.time > it.challengerTime!!) {
        if (UserInfo.autoLoginKey == it.challengerId) {
          renewal = true
        }
        resultRank++
      }

      arrRankingData = rankingDatas
      setMyUiData(racerSpeeds, resultRank, renewal)

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
