package com.umpa2020.tracer.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.umpa2020.tracer.dataClass.RouteData
import com.umpa2020.tracer.main.trace.running.RunningSaveActivity
import com.umpa2020.tracer.util.Wow
import java.io.File
import java.io.FileOutputStream

class ViewerMap : OnMapReadyCallback {
  lateinit var mMap: GoogleMap    //racingMap 인스턴스
  var TAG = "what u wanna say?~~!~!"       //로그용 태그
  var context: Context
  lateinit var routeData: RouteData
  var loadRoute: MutableList<MutableList<LatLng>> = mutableListOf()
  var smf: SupportMapFragment

  //Running
  constructor(smf: SupportMapFragment, context: Context, routeData: RouteData) {
    this.smf = smf
    this.context = context
    this.routeData = routeData
    smf.getMapAsync(this)
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    loadRoute = loadRoute()
    drawRoute(loadRoute)
  }

  fun CaptureMapScreen() {
    var callback = GoogleMap.SnapshotReadyCallback {
      try {
        var saveFolder = File(context.filesDir, "mapdata") // 저장 경로
        if (!saveFolder.exists()) {       //폴더 없으면 생성
          saveFolder.mkdir()
        }
        val path = "racingMap" + saveFolder.list().size + ".bmp"        //파일명 생성하는건데 수정필요
        //비트맵 크기에 맞게 잘라야함
        var myfile = File(saveFolder, path)                //로컬에 파일저장
        var out = FileOutputStream(myfile)
        it.compress(Bitmap.CompressFormat.PNG, 90, out)
        (context as RunningSaveActivity).save(myfile.path)
      } catch (e: Exception) {
      }
    }

    mMap.snapshot(callback)
  }

  /*fun loadRoute(): ArrayList<Vector<LatLng>> {
      var routes = ArrayList<Vector<LatLng>>()
      for (i in runningData.lats.indices) {
          var latlngs = Vector<LatLng>()
          for (j in runningData.lats[i].indices) {
              latlngs.add(LatLng(runningData.lats[i][j], runningData.lngs[i][j]))
          }
          routes.add(latlngs)
      }
      return routes
  }*/ // 윤권

  fun loadRoute(): MutableList<MutableList<LatLng>> {
    var routes: MutableList<MutableList<LatLng>> = mutableListOf()
    for (i in routeData.latlngs.indices) {
      var latlngs: MutableList<LatLng> = mutableListOf()
      for (j in routeData.latlngs[i].indices) {
        latlngs.add(routeData.latlngs[i][j])
      }
      routes.add(latlngs)
    }
    return routes

    routes = routeData.latlngs
    return routes
  }

  fun drawRoute(routes: MutableList<MutableList<LatLng>>) { //로드 된 경로 그리기
    for (i in routes.indices) {
      var polyline =
        mMap.addPolyline(
          PolylineOptions()
            .addAll(routes[i])
            .color(Color.RED)
            .startCap(RoundCap() as Cap)
            .endCap(RoundCap())
        )        //경로를 그릴 폴리라인 집합
    }

    var min = LatLng(Wow.minDoubleLat(routeData.latlngs), Wow.minDoubleLng(routeData.latlngs))
    var max = LatLng(Wow.maxDoubleLat(routeData.latlngs), Wow.maxDoubleLng(routeData.latlngs))
    print_log(min.toString() + max.toString())
    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(LatLngBounds(min, max), 1080, 300, 50))
  }

  fun print_log(text: String) {
    Log.d(TAG, text.toString())
  }
}