package com.korea50k.RunShare.DataClass

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

//경로, 시간, 거리
class RunningData() : Serializable {
    var lats: DoubleArray= DoubleArray(0)
    var lngs: DoubleArray= DoubleArray(0)
    var alts: DoubleArray=DoubleArray(0)
    var speed: String = ""
    var distance: String = ""
    var time: String = ""
    var map_title: String = ""
    var cal: String = ""
    var bitmap: String=""

}