package com.korea50k.RunShare.Activities.RankFragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.activity_rank_recycler_click.*
import java.io.BufferedInputStream
import java.net.URL

class RankRecyclerClickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank_recycler_click)

        val intent =  getIntent()
        val MapTitle = intent.extras?.getString("MapTitle")
        val MapImage = intent.extras?.getString("MapImage")
        mapName_TextView.setText(MapTitle)

        Log.d("ssmm11", "맵 이미지 = " + MapImage)
        class SetImageTask : AsyncTask<Void, Void, String>(){
            override fun onPreExecute() {
                super.onPreExecute()
            }
            lateinit var bm: Bitmap

            override fun doInBackground(vararg params: Void?): String? {
                try {
                    val url =
                        URL(MapImage)
                    val conn = url.openConnection()
                    conn.connect()
                    val bis = BufferedInputStream(conn.getInputStream())
                    bm = BitmapFactory.decodeStream(bis)
                    bis.close()
                } catch (e : java.lang.Exception) {
                    Log.d("ssmm11", "이미지 다운로드 실패 " +e.toString())
                }
                return null
            }

            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                //TODO:피드에서 이미지 적용해볼 소스코드
                rank_root_preview.setImageBitmap(bm)
                //imageTest.setImageBitmap(bm)
            }
        }
        var Start = SetImageTask()
        Start.execute()
    }
}