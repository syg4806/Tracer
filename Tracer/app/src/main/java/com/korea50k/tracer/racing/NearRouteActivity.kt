package com.korea50k.tracer.racing

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.SphericalUtil
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.InfoData
import com.korea50k.tracer.dataClass.NearMap
import com.korea50k.tracer.dataClass.RouteData
import kotlinx.android.synthetic.main.activity_near_route.*
import kotlinx.android.synthetic.main.recycler_nearactivity_item.*


class NearRouteActivity : AppCompatActivity() {

    lateinit var racerData: InfoData
    lateinit var makerData: InfoData
    /*
    lateinit var locationCallback: LocationCallback
    var cur_loc = LatLng(0.0,0.0)          //현재위치
    var latLngs : MutableList<LatLng> = mutableListOf(LatLng(0.0,0.0))
    var markerlatlngs: MutableList<LatLng> = mutableListOf(LatLng(0.0,0.0))
    var nearMaps: ArrayList<NearMap> = arrayListOf()

    /*lateinit var distanceArray: ArrayList<Double>
    lateinit var mapTitleArray: ArrayList<String>*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_route)

        val db = FirebaseFirestore.getInstance()


        // 초기화 한 데이터 삭제 나중에 수정할게요
        latLngs.removeAt(0)
        markerlatlngs.removeAt(0)

        locationCallback = object : LocationCallback() {        //위치요청 결과가 들어오면 실행되는 코드
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.let {
                    for ((i, location) in it.locations.withIndex()) {
                        var lat = location.latitude     //결과로 가져온 location에서 정보추출
                        var lng = location.longitude
                        cur_loc = LatLng(lat, lng)             //새로받은 정보로 현재위치 설정
                    }
                }
            }
        }
/*
        db.collection("mapRoute")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var altitude = document.get("altitude") as List<Double>
                    var receiveRouteDatas = document.get("latlngs") as List<Object>
                    var receiveMarkerDatas = document.get("markerlatlngs") as List<Object>

                    for (receiveRouteData in receiveRouteDatas) {
                        val location = receiveRouteData as Map<String, Any>
                        val latLng = LatLng(location["latitude"] as Double,
                        location["longitude"] as Double)
                        latLngs.add(latLng)
                    }

                    for (receiveMarkerData in receiveMarkerDatas) {
                        val location = receiveMarkerData as Map<String, Any>
                        val latLng = LatLng(location["latitude"] as Double,
                            location["longitude"] as Double)
                        markerlatlngs.add(latLng)
                    }
                    var routeData = RouteData(altitude, latLngs,markerlatlngs)
                    Log.d("ssmm11" , routeData.toString())

                    Log.d("ssmm11", routeData.latlngs.toString())
                    //Log.d("ssmm11", receiveRouteData.latlngs.toString())
                    //var receive_loc = LatLng(receiveRouteData[0].latitude, receiveRouteData[0].longitude)
                    //var distance = SphericalUtil.computeDistanceBetween(currentLocation, receive_loc)

                    var receive_loc = LatLng(routeData.latlngs[0].latitude, routeData.latlngs[0].longitude)
                    var temp = document.id.split("||")
                    var nearMap = NearMap(temp[0], SphericalUtil.computeDistanceBetween(cur_loc, receive_loc))
                    nearMaps.add(nearMap)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ssmm11", "Error getting documents.", exception)
            }

        db.collection("mapInfo")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var receiveInfoData = document.toObject(InfoData::class.java)
                    Log.d("ssmm11", receiveInfoData.toString())
                }
            }
            .addOnFailureListener { exception ->
                Log.w("ssmm11", "Error getting documents.", exception)
            }

 */
    }


     */
    var cur_loc = LatLng(0.0,0.0)          //현재위치

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_near_route)

        //startFragment에서 넘긴 현재 위치를 intent로 받음
        val intent = getIntent()
        var current =intent.extras?.getParcelable<Location>("currentLocation")

        Log.d("jsj", "near 넘어와서 getintent 한 값 " + current.toString())

        //결과로 가져온 location에서 정보추출 / 이건 위도 경도 형태로 받아오는 형식
        //Location 형태로 받아오고 싶다면 아래처럼
        //var getintentLocation = current
        var lat = current!!.latitude
        var lng = current!!.longitude
        cur_loc = LatLng(lat, lng)

        Log.d("jsj", lat.toString() + lng.toString())


        //TODO. 서버에서 데이터 받아옴
        val datas = ArrayList<NearMap>()
        datas.add(NearMap("test1", 50.0))
        datas.add(NearMap("test2", 100.0))
        datas.add(NearMap("test3", 10.0))
        datas.add(NearMap("test4", 20.0))
        datas.add(NearMap("test5", 60.0))
        datas.add(NearMap("test6", 50.0))
        datas.add(NearMap("test7", 70.0))
        datas.add(NearMap("test8", 10.0))
        datas.add(NearMap("test9", 50.0))

        near_recycler_map.adapter = NearRecyclerViewAdapter(datas)
        near_recycler_map.layoutManager = LinearLayoutManager(this)



    }
}
