package com.umpa2020.tracer.network

import com.google.firebase.firestore.DocumentSnapshot
import com.umpa2020.tracer.dataClass.*
import com.umpa2020.tracer.util.UserInfo
import kotlinx.coroutines.tasks.await

/**
 * 유저 히스토리 관련 클래스
 * 사용법 - FBUserHistoryRepository().관련기능함수()
 *
 * 1. 유저가 뛴 맵 history 저장
 * 2. 유저가 만든 맵 history 저장
 * 3. 유저가 challenge 한 맵 history 저장
 */

class FBUsersRepository : BaseFB() {
  var globalStartAfter: DocumentSnapshot? = null

  suspend fun listUserAchievement(userId: String): ArrayList<Int> {
    val list = arrayListOf<Int>()

    val ref = usersCollectionRef.whereEqualTo(USER_ID, userId).get().await()
      .documents.first().reference
    for (i in 1..3) {
      list.add(
        ref.collection(TROPHIES).whereEqualTo(RANKING, i).get().await()
          .documents.size
      )
    }
    return list
  }

  fun createUserAchievement(trophyData: TrophyData) {
    usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES).add(trophyData)
  }

  fun updateUserAchievement(rankingDatas: MutableList<RankingData>, mapId: String) {
    var ranking = 0L
    run loop@{
      rankingDatas.forEachIndexed { i, rankingData ->
        if (i > 3)
          return@loop
        if (rankingData.challengerId == UserInfo.autoLoginKey) {
          ranking = i + 1L
        }
      }
    }


    if (ranking != 0L) {
      usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES)
        .whereEqualTo(MAP_ID, mapId)
        .get()
        .addOnSuccessListener {
          if (it.isEmpty) {
            usersCollectionRef.document(UserInfo.autoLoginKey).collection(TROPHIES)
              .add(TrophyData(mapId, ranking))
          } else {
            val before = it.documents.first().getLong(RANKING)!!
            if (before != ranking) {
              it.documents.first().reference.update(RANKING, ranking)
            }
          }
        }
    }
  }

  fun createUserInfo(data: Users) {
    usersCollectionRef.document(data.userId).set(data)
    FBAchievementRepository().createAchievement(data.userId)
  }

  fun createUserHistory(activityData: ActivityData) {
    usersCollectionRef.document(UserInfo.autoLoginKey).collection(ACTIVITIES).add(activityData)
    FBAchievementRepository().incrementDistance(UserInfo.autoLoginKey, activityData.distance!!)
  }


  suspend fun listUserMakingActivity(userId: String, limit: Long): List<ActivityData>? {
    return (if (globalStartAfter == null) db.collection(USERS).document(userId).collection(ACTIVITIES)
    else usersCollectionRef.document(userId).collection(ACTIVITIES).orderBy(TIME).startAfter(globalStartAfter!!))
      .limit(limit).get().await().apply {
        if (documents.isEmpty())
          return null
        globalStartAfter = last()
      }.toObjects(ActivityData::class.java)
  }

  suspend fun listUserRoute(uid: String, limit: Long): List<MapInfo>? {
    val infoDatas =
      if (globalStartAfter == null) {
        mapsCollectionRef.whereEqualTo(MAKER_ID, uid).whereEqualTo(CHALLENGE, false).orderBy(TIME)
      } else {
        mapsCollectionRef.whereEqualTo(MAKER_ID, uid).startAfter(globalStartAfter!!).orderBy(TIME)
      }.limit(limit).get().await().apply {
        if (documents.size == 0)
          return null
        globalStartAfter = documents.last()
      }.toObjects(MapInfo::class.java)

    val playedMapIdList = FBMapRepository().listPlayed()
    val likedMapIdList = FBMapRepository().listLikedMap()
    infoDatas.filter { playedMapIdList.contains(it.mapId) }.forEach { it.played = true }
    infoDatas.filter { likedMapIdList.contains(it.mapId) }.forEach { it.liked = true }
    return infoDatas
  }

  /**
   * uid가 mapid를 like 했는지 Boolean으로 반환
   */
  suspend fun isPlayed(uid: String, mapId: String): Boolean {
    return !usersCollectionRef.document(uid).collection(ACTIVITIES)
      .whereEqualTo(MAP_ID, mapId)
      .get().await().isEmpty
  }

  fun userWithdrawal() {
    usersCollectionRef.document(UserInfo.autoLoginKey).update(USER_STATE, false)
  }
}
