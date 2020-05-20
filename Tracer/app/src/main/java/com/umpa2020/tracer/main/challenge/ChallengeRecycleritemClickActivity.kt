package com.umpa2020.tracer.main.challenge

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.RouteGPX
import com.umpa2020.tracer.extensions.Y_M_D
import com.umpa2020.tracer.extensions.format
import com.umpa2020.tracer.extensions.gpxToClass
import com.umpa2020.tracer.extensions.image
import com.umpa2020.tracer.gpx.WayPointType
import com.umpa2020.tracer.main.start.challengeracing.ChallengeRacingActivity
import com.umpa2020.tracer.main.start.racing.RacingActivity
import com.umpa2020.tracer.main.start.racing.RacingSelectPeopleActivity
import com.umpa2020.tracer.map.TraceMap
import com.umpa2020.tracer.network.BaseFB.Companion.MAP_ID
import com.umpa2020.tracer.network.FBChallengeRepository
import com.umpa2020.tracer.network.FBMapRepository
import com.umpa2020.tracer.network.FBStorageRepository
import com.umpa2020.tracer.util.Logg
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.activity_challenge_map_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 하나의 대회를 선택하면 해당 대회의 정보를
 * 자세히 보여주는 액티비티, 추 후에 뛸 수 있도록 연동
 */
class ChallengeRecycleritemClickActivity : AppCompatActivity(), OnSingleClickListener, CoroutineScope by MainScope(), OnMapReadyCallback {
  lateinit var routeGPX: RouteGPX
  lateinit var traceMap: TraceMap
  lateinit var challengeId: String
  var challengeEnabled =true
  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_challenge_map_detail)
    val smf =
      supportFragmentManager.findFragmentById(R.id.challengeDetailLocation) as SupportMapFragment
    smf.getMapAsync(this)
    challengeId = intent.getStringExtra("challengeId")!!
    val job =
    launch {
      FBChallengeRepository().getChallengeData(challengeId).run {
        challengeDetailImageView.image(FBStorageRepository().downloadFile(imagePath!!))
        challengeDetailCompetitionName.text = name
        challengeDetailCompetitionDate.text = date!!.format(Y_M_D)
        challengeDetailCompetitionPeriod.text = from!!.format(Y_M_D) + " ~ " + to!!.format(Y_M_D)
        challengeDetailAddress.text = address
        challengeDetailHost.text = host
        challengeDetailInformation.text = intro
        challengeEnabled=enabled
      }
      FBStorageRepository().getFile(FBMapRepository().getMapInfo(challengeId)?.routeGPXPath!!).gpxToClass().let {
        traceMap.drawRoute(it.trkList.toList(), it.wptList.filter { it.type == WayPointType.START_POINT || it.type == WayPointType.FINISH_POINT })
      }
      initButton()
    }
  }

  private fun initButton() {
    challengeDetailButton.setOnClickListener(this)
  }

  override fun onSingleClick(v: View?) {
    when (v?.id) {
      R.id.challengeDetailButton -> {
        if(challengeEnabled) {
          startActivity(Intent(this, ChallengeRacingActivity::class.java).apply {
            putExtra(MAP_ID, challengeId)
          })
        }else{
          val nextIntent = Intent(this, RacingSelectPeopleActivity::class.java)
          nextIntent.putExtra(MAP_ID, challengeId)
          //routegpx 넘겨주기
          nextIntent.putExtra(RacingActivity.ROUTE_GPX, routeGPX)
          startActivity(nextIntent)
        }
      }
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    traceMap = TraceMap(googleMap) //구글맵
  }
}

