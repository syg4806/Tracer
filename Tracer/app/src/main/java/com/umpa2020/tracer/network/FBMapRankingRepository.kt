package com.umpa2020.tracer.network

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.umpa2020.tracer.dataClass.RankingData
import com.umpa2020.tracer.util.Logg

/**
 * 한 맵에 대한 랭킹만 가져오는
 * Repository
 */

class FBMapRankingRepository {
  val db = FirebaseFirestore.getInstance()

  fun getMapRanking(mapTitle: String, listener: MapRankingListener) {
    val arrRankingData: ArrayList<RankingData> = arrayListOf()
    var rankingData = RankingData()
    // 베스트 타임이 랭킹 가지고 있는 것 중에서 이것이 베스트 타임인가를 나타내주는 1,0 값입니다.
    // 그래서 한 사용자의 베스트 타임만 가져오고 또 그것들 중에서 오름차순해서 순위 나타냄
    db.collection("rankingMap").document(mapTitle).collection("ranking")
      .whereEqualTo("bestTime", 1)
      .orderBy("challengerTime", Query.Direction.ASCENDING)
      .get()
      .addOnSuccessListener { result ->
        Logg.d("ssmm11 result is empty? = ${result.isEmpty}")
        for (document in result) {
          rankingData = document.toObject(RankingData::class.java)
          arrRankingData.add(rankingData)
        }
        listener.getMapRank(arrRankingData)
      }
  }
}