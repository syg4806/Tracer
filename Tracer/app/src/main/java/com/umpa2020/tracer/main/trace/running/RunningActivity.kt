package com.umpa2020.tracer.main.trace.running

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.SupportMapFragment
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.NoticeState
import com.umpa2020.tracer.dataClass.Privacy
import com.umpa2020.tracer.main.MainActivity
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_running.*

class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "RunningActivity"       //로그용 태그
    lateinit var manageRunning: ManageRunning
    lateinit var drawer: SlidingDrawer
    var B_RUNNIG = true
    var ns = NoticeState.NOTHING
    private var doubleBackToExitPressedOnce1 = false

    // 버튼 에니메이션
    private var fabOpen: Animation? = null // Floating Animation Button
    private var startButton: Button? = null
    private var stopButton: Button? = null
    private var pauseButton: Button? = null

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce1) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce1 = true


        val text = "뒤로 버튼을 한번 더 누르면 종료됩니다."
        val duration = Toast.LENGTH_LONG

        val toast = Toast.makeText(applicationContext, text, duration)
        toast.show()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_running)

        supportActionBar?.title = "RUNNING"

        init()

        btn_stop!!.setOnLongClickListener {
            if (manageRunning.runningMap.distance < 200) {
                showChoicePopup("거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.SIOP)
            } else
                manageRunning.stopRunning()
            true
        }
    }
    
    private fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        manageRunning = ManageRunning(smf, this)

        drawer = findViewById(R.id.drawer)
        drawer.setOnDrawerScrollListener(this)
        drawer.setOnDrawerOpenListener(this)
        drawer.setOnDrawerCloseListener(this)

        fabOpen = AnimationUtils.loadAnimation(applicationContext, R.anim.running_btn_open) // 애니매이션 초기화

        startButton = findViewById(R.id.btn_start)
        stopButton = findViewById(R.id.btn_stop)
        pauseButton = findViewById(R.id.btn_pause)
    }


    /**
     *  버튼 하나에서 두개로 퍼지는 애니메니션
     */
    private fun anim() {
        btn_start.visibility = View.INVISIBLE
        btn_stop.startAnimation(fabOpen)
        btn_pause.startAnimation(fabOpen)
        btn_stop.isClickable = true
        btn_pause.isClickable = true
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start -> {
                anim()
                manageRunning.startRunning(this)
            }
            R.id.btn_pause -> {
                if (manageRunning.privacy == Privacy.RACING) {
                    //noticeMessage("일시정지를 하게 되면\n\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?", NoticeState.PAUSE)
                    showChoicePopup("일시정지를 하게 되면\n경쟁 모드 업로드가 불가합니다.\n\n일시정지를 하시겠습니까?", NoticeState.PAUSE)
                } else {
                    if (B_RUNNIG)
                        manageRunning.pauseRunning()
                    else
                        restart()

                }

            }
            R.id.btn_stop -> {
                val text = "종료를 원하시면 길게 눌러주세요"
                val duration = Toast.LENGTH_LONG

                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
        }
    }

    fun pause() {
        btn_pause.text = "재시작"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
        B_RUNNIG = false
    }

    private fun restart() { //TODO:Start with new polyline
        btn_pause.text = "일시정지"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
        B_RUNNIG = true
        manageRunning.restartRunning()
    }

    /**
     * 팝업 띄우는 함수
     * */
    private fun showChoicePopup(text: String, ns: NoticeState) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.running_activity_yesnopopup, null)
        val textView: TextView = view.findViewById(R.id.runningActivityPopUpTextView)
        textView.text = text

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("선택해주세요.")
            .create()

        //Yes 버튼 눌렀을 때
        val yesButton = view.findViewById<Button>(R.id.runningActivityYesButton)
        yesButton.setOnClickListener {
            Log.d("ssmm11", "what is ns = " + ns)

            when (ns) {
                NoticeState.NOTHING -> {
                }
                NoticeState.PAUSE -> {
                    runningNotificationLayout.visibility = View.GONE
                    manageRunning.pauseRunning()
                }
                NoticeState.SIOP -> {
                    manageRunning.stopRunning()
                    var newIntent = Intent(this, MainActivity::class.java)
                    newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(newIntent)
                }

            }

            this.ns = NoticeState.NOTHING
            alertDialog.dismiss()
        }


        //No 기록용 버튼 눌렀을 때
        val recordButton = view.findViewById<Button>(com.umpa2020.tracer.R.id.runningActivityNoButton)
        recordButton.setOnClickListener {
            this.ns = NoticeState.NOTHING
            alertDialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기

        this.ns = ns
    }

    // 화면 안보일
    override fun onStop() {
        super.onStop()
        Log.d("screen", "onStop()")
    }

    override fun onResume() {
        super.onResume()
        // 브로드 캐스트 등록 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(myBroadcastReceiver, IntentFilter("custom-event-name"))
    }

    override fun onPause() {
        super.onPause()
        Log.d("screen", "onPause()")
        //        브로드 캐스트 해제 - 전역 context로 수정해야함
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myBroadcastReceiver)
    }

    /**
     *  백그라운드에서 메시지 받는 거
     */
    // 서버에서 보내주는 데이터를 받는 브로드캐스트 - 나중엔 클래스화 요구??
    private val myBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("$TAG receiver", "받는다.")

            // getParcelableExtra<T> : T = Location
            val message = intent?.getParcelableExtra<Location>("message")
            Log.d("$TAG receiver", "Got message : $message")
            val currentLocation = message as Location
            manageRunning.runningMap.setLocation(currentLocation)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("screen", "onDestroy()")
    }

    override fun onScrollStarted() {
        Log.d(TAG, "onScrollStarted()")
    }

    override fun onScrollEnded() {
        Log.d(TAG, "onScrollEnded()")
    }

    override fun onDrawerOpened() {
        //runningHandle.background = getDrawable(R.drawable.close_selector)
        runningHandle.text = "▼"
        Log.d(TAG, "onDrawerOpened()")
    }

    override fun onDrawerClosed() {
        //runningHandle.background = getDrawable(R.drawable.extend_selector)
        runningHandle.text = "▲"
        Log.d(TAG, "onDrawerClosed()")
    }

    fun print_log(text: String) {
        Log.d(TAG, text.toString())
    }

}