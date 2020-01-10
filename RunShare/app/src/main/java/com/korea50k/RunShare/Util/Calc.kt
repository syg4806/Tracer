package com.korea50k.RunShare.Util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import java.util.*

class Calc(){
    companion object {
        fun minDouble(list: Array<Vector<Double>>): Double {
            var min = list[0][0]
            for (i in list.indices) {
                if (list[i].min()!! < min) {
                    min = list[i].min()!!
                }
            }
            return min
        }

        fun maxDouble(list: Array<Vector<Double>>): Double {
            var max = list[0][0]
            for (i in list.indices) {
                if (list[i].max()!! > max) {
                    max = list[i].max()!!
                }
            }
            return max
        }

        fun getDistance(locations: Vector<LatLng>): Double {  //점들의 집합에서 거리구하기
            var distance = 0.0
            var i = 0
            while (i < locations.size - 1) {
                distance += SphericalUtil.computeDistanceBetween(locations[i], locations[i + 1])
                i++
            }
            return distance
        }
    }
}