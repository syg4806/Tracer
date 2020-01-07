package com.korea50k.RunShare.Activities.FeedFragment


import android.content.Intent
import android.content.res.AssetManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager

import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.dataClass.ConvertJson
import com.korea50k.RunShare.dataClass.FeedMapData
import com.korea50k.RunShare.dataClass.RankMapData
import kotlinx.android.synthetic.main.fragment_feed_map_nocomment.view.*
import kotlinx.android.synthetic.main.recycler_feed_map.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class fragment_feed_map : Fragment() {
    var mJsonString = "";
    var list = arrayListOf<FeedMapData>() // 그대로 냅둬야 할것

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View =  inflater!!.inflate(R.layout.recycler_feed_map, container, false)

        var Test = FeedMapData()
        var Test1 = FeedMapData()
        var Test2 = FeedMapData()
        var Test3 = FeedMapData()
        var Test4 = FeedMapData()


        Test.Uname = "tester1"

        list.add(Test) //서버에서 데이터 받아오게 되면 지워야할것 일시적으로 만들어준거기떄문에
        Test1.Uname = "tester2"
        list.add(Test1) // 생성자 만들어주어야 함  이유 : 서버에서 받아온 데이터 바로받아주려고
        Test2.Uname = "tester3"
        list.add(Test2)
        Test3.Uname = "tester4"
        list.add(Test3)
        Test4.Uname = "tester5"
        list.add(Test4)



        /*val assetManager = resources.assets

        //TODO:서버에서 데이터 가져와서 해야함
        val inputStream= assetManager.open("datajson")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        var rankMapDatas = ConvertJson.JsonToRankMapDatas(jsonString)

        //리사이클러 뷰 클릭 리스너 부분
        val mAdapter = RankRecyclerViewAdapter_Map(activity!!, rankMapDatas){ rankmapdata ->
            //TODO Intent로 새로운 xml 열기
            Toast.makeText(context, "맵 이름 :  ${rankmapdata.name}, 실행 수 : ${rankmapdata.execute}", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, RankRecyclerClickActivity::class.java)
            intent.putExtra("mapName", rankmapdata.name)

            startActivity(intent)
        }
        view.rank_recycler_map!!.adapter = mAdapter*/

        val mAdapter = FeedRecyclerViewAdapter_Map(view.context, list)
        view.feed_recycler_map!!.adapter = mAdapter
        val lm = LinearLayoutManager(context)
        view.feed_recycler_map.layoutManager = lm
        view.feed_recycler_map.setHasFixedSize(true)


        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    rankDownload("kjb")
                    //TODO:피드에서 이미지 적용해볼 소스코드

                    /* val url =
                         URL("https://runsharetest.s3.ap-northeast-2.amazonaws.com/kjb/ImageTitle.png")
                     val conn = url.openConnection()
                     conn.connect()
                     val bis = BufferedInputStream(conn.getInputStream())
                     val bm = BitmapFactory.decodeStream(bis)
                     bis.close()*/
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                //imageTest.setImageBitmap(bm)
            }
        }

        var Start : SaveTask = SaveTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/rankDownload.php")


        return view
    }

    private fun rankDownload(Id: String) {
        RetrofitClient.retrofitService.rankDownload(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                    Toast.makeText(context, "DB 다운로드 성공" + result,Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {

                }
            }

            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
                Toast.makeText(context, "서버 작업 실패", Toast.LENGTH_SHORT).show()
                Log.d("ssmm11", t.message);
                t.printStackTrace()
            }
        })
    }

    private inner class GetData : AsyncTask<String, Void, String>() {
        internal var errorString: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

       /* override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null) {
            } else {
                mJsonString = result
                var rankMapDatas = ConvertJson.JsonToRankMapDatas(mJsonString)

                val mAdapter = RankRecyclerViewAdapter_Map(activity!!, rankMapDatas){ rankmapdata ->
                    //TODO Intent로 새로운 xml 열기
                    val intent = Intent(context, RankRecyclerClickActivity::class.java)
                    startActivity(intent)
                }
                view?.rank_recycler_map!!.adapter = mAdapter
                val lm = LinearLayoutManager(context)
                view?.rank_recycler_map!!.layoutManager = lm
                view?.rank_recycler_map!!.setHasFixedSize(true)
            }
        }*/

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            Log.d("ssmm11", "받아온 url = " +serverURL)
            try {
                val url = URL(serverURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.setReadTimeout(5000)
                httpURLConnection.setConnectTimeout(5000)
                httpURLConnection.connect()

                val responseStatusCode = httpURLConnection.getResponseCode()
                Log.d("ssmm11", "response code - $responseStatusCode")

                val inputStream: InputStream
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream()
                } else {
                    inputStream = httpURLConnection.getErrorStream()
                }

                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()

                var line : String? = ""
                while (line != null) {
                    line = bufferedReader.readLine()
                    sb.append(line)
                }

                bufferedReader.close()
                return sb.toString().trim { it <= ' ' }

            } catch (e: Exception) {
                Log.d("ssmm11", "InsertData: Error ", e)
                errorString = e.toString()
                return null
            }
        }
    }

/*
    fun jsonRead(){
        for (i in 0 until jArray.length()) {
            val obj = jArray.getJSONObject(i)
            val name = obj.getString("sampleMapName")
            val execute = obj.getString("sampleExecute")
            val like = obj.getString("sampleLike")
        }
    }
*/

}
