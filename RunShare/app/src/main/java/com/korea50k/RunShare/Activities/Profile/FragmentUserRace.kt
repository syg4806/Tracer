package com.korea50k.RunShare.Activities.Profile


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.Activities.RankFragment.RankRecyclerViewAdapterMap
import com.korea50k.RunShare.Activities.RankFragment.RecyclerItemClickListener
import com.korea50k.RunShare.R
import com.korea50k.RunShare.RetrofitClient
import com.korea50k.RunShare.Util.ConvertJson
import com.korea50k.RunShare.Util.S3
import com.korea50k.RunShare.Util.SharedPreValue
import com.korea50k.RunShare.dataClass.UserMapImageData
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList


class FragmentUserRace : Fragment(), RankRecyclerViewAdapterMap.OnLoadMoreListener {
    lateinit var UserMapImageDatas : ArrayList<UserMapImageData>
    lateinit var rankrecyclerviewadapterMap: ProfileMapRecyclerViewAdapter

    var mJsonString = ""
    lateinit var mAdapter : ProfileMapRecyclerViewAdapter
    lateinit var itemList : ArrayList<UserMapImageData>
    lateinit var onLoadMoreListener : RankRecyclerViewAdapterMap.OnLoadMoreListener
    var start = 0
    var end = 15
    lateinit var jArray : JSONArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        class SaveTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    profileMapImageDownload(SharedPreValue.getNicknameData(context!!)!!)
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "랭크 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)

                //TODO:피드에서 이미지 적용해볼 소스코드
            }
        }
        var Start : SaveTask = SaveTask()
        Start.execute()

        val task = GetData()
        task.execute("http://15.164.50.86/profileMapImageDownload.php?Id="+SharedPreValue.getNicknameData(context!!)!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view: View =  inflater!!.inflate(R.layout.fragment_user_race, container, false)

        itemList = java.util.ArrayList()
        var mRecyclerView = view.findViewById<RecyclerView>(R.id.user_race_recyclerview)
        val mLayoutManager = GridLayoutManager(context,3)
        mRecyclerView.layoutManager = mLayoutManager
        mAdapter = ProfileMapRecyclerViewAdapter(this)
        mAdapter.setGridLayoutManager(mLayoutManager)
        mAdapter.setRecyclerView(mRecyclerView)

        mRecyclerView.adapter = mAdapter

        mRecyclerView.addOnItemTouchListener(
            RecyclerItemClickListener(context!!, mRecyclerView,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        Log.d("ranking","click listener")
                        //TODO:자 이제 여기서 넘기기로
                      /*  val intent = Intent(context, RankRecyclerClickActivity::class.java)
                        intent.putExtra("MapTitle", view.rank_cardView_name.text)
                        startActivity(intent)*/
                    }
                })
        )
        return view
    }

    private fun profileMapImageDownload(Id: String) {
        RetrofitClient.retrofitService.profileMapImageDownload(Id).enqueue(object :
            retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
                try {
                    val result: String? = response.body().toString()
                } catch (e: Exception) {
                }
            }
            override fun onFailure( call: Call<ResponseBody>,t: Throwable) {
                Log.d("ssmm11", t.message);
                t.printStackTrace()
            }
        })
    }

    override fun onLoadMore() {
        Log.d("ssmm11", "onLoadMore")
        //mAdapter.setProgressMore(true)
        if ( mAdapter.itemCount < jArray.length()) {
            Handler().postDelayed({
                itemList.clear()

                start = mAdapter.itemCount
                end = start + 15
                val task = GetData()
                task.execute("http://15.164.50.86/profileMapImageDownload.php")

                mAdapter.addItemMore(itemList)
                mAdapter.setMoreLoading(false)
            }, 100)
        }
    }

    fun loadData() {
        itemList.clear()
        mAdapter.addAll(UserMapImageDatas)
    }

    private inner class GetData() : AsyncTask<String, Void, String>() {
        internal var errorString: String? = null

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (result == null) {
            } else {
                mJsonString = result

                // just size data , not use
                // please don't remove that
                val jObject = JSONObject(mJsonString)
                jArray = jObject.getJSONArray("JsonData")

                UserMapImageDatas = ConvertJson.JsonToUserMapImageData(mJsonString, start, end-1)
                loadData()
            }
        }

        override fun doInBackground(vararg params: String): String? {
            val serverURL = params[0]
            try {
                val url = URL(serverURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.setReadTimeout(5000)
                httpURLConnection.setConnectTimeout(5000)
                httpURLConnection.connect()

                val responseStatusCode = httpURLConnection.getResponseCode()

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
}



