package com.korea50k.tracer.start


import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.SupportMapFragment
import com.korea50k.tracer.MainActivity
import com.korea50k.tracer.R
import com.korea50k.tracer.dataClass.NoticeState
import com.korea50k.tracer.dataClass.Privacy
import hollowsoft.slidingdrawer.OnDrawerCloseListener
import hollowsoft.slidingdrawer.OnDrawerOpenListener
import hollowsoft.slidingdrawer.OnDrawerScrollListener
import hollowsoft.slidingdrawer.SlidingDrawer
import kotlinx.android.synthetic.main.activity_running.*
import kotlinx.android.synthetic.main.activity_running.btn_stop

class RunningActivity : AppCompatActivity(), OnDrawerScrollListener, OnDrawerOpenListener,
    OnDrawerCloseListener {
    var TAG = "what u wanna say?~~!~!"       //로그용 태그
    lateinit var manageRunning: ManageRunning
    lateinit var drawer: SlidingDrawer
    var B_RUNNIG = true
    var ns = NoticeState.NOTHING
    private var doubleBackToExitPressedOnce1 = false

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce1) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce1 = true
        val li = layoutInflater
        val layout: View =
            li.inflate(R.layout.custom_toast_start, findViewById<ViewGroup>(R.id.custom_toast_layout_start))

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 850)
        toast.view = layout //setting the view of custom toast layout

        toast.show()

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce1 = false }, 2000)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_running)

        init()
        manageRunning.startRunning(this)
        btn_stop.setOnLongClickListener {
            if (manageRunning.map.distance < 200) {
                //noticeMessage("거리가 200m 미만일때\n\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.SIOP)
                showChoicePopup("거리가 200m 미만일때\n정지하시면 저장이 불가능합니다. \n\n정지하시겠습니까?", NoticeState.SIOP)
            } else
                manageRunning.stopRunning()
            true
        }
    }

    fun init() {
        val smf = supportFragmentManager.findFragmentById(R.id.map_viewer) as SupportMapFragment
        manageRunning = ManageRunning(smf, this)

        drawer = findViewById(R.id.drawer)
        drawer.setOnDrawerScrollListener(this)
        drawer.setOnDrawerOpenListener(this)
        drawer.setOnDrawerCloseListener(this)
    }

    /*
    fun noticeMessage(text: String, ns: NoticeState) {
        runningNotificationLayout.visibility = View.VISIBLE
        runningNotificationTextView.text = text
        this.ns = ns

    }

     */

    fun onClick(view: View) {
        when (view.id) {

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
            R.id.btn_stop -> stop()

            /*
            R.id.runningNotificationYes -> {
                when (ns) {
                    NoticeState.NOTHING -> {
                    }
                    NoticeState.PAUSE -> {
                        runningNotificationLayout.visibility = View.GONE
                        manageRunning.pauseRunning()
                    }
                    NoticeState.SIOP -> {
                        var newIntent = Intent(this, MainActivity::class.java)
                        newIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivity(newIntent)
                    }
                }
                ns = NoticeState.NOTHING

            }
            R.id.runningNotificationNo -> {
                ns = NoticeState.NOTHING
                runningNotificationLayout.visibility = View.GONE
            }
             */
        }
    }

    fun pause() {
        btn_pause.text = "RESTART"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_pressed, 0, 0, 0)
        B_RUNNIG = false
    }

    fun stop() {    //타이머 멈추는거 만들어야함
        val li = layoutInflater
        val layout: View =
            li.inflate(R.layout.custom_toast_stop, findViewById<ViewGroup>(R.id.custom_toast_layout_stop))

        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 850)
        toast.view = layout //setting the view of custom toast layout

        toast.show()
    }

    fun restart() { //TODO:Start with new polyline
        btn_pause.text = "PAUSE"
        //btn_pause.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_icon_pressed, 0, 0, 0)
        B_RUNNIG = true
        manageRunning.restartRunning()
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

    /**
     * 팝업 띄우는 함수
     * */
    private fun showChoicePopup(text: String, ns: NoticeState){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.running_activity_yesnopopup, null)
        val textView : TextView = view.findViewById(R.id.runningActivityPopUpTextView)
        textView.text = text

        val alertDialog = AlertDialog.Builder(this) //alertDialog 생성
            .setTitle("선택해주세요.")
            .create()

        //Yes 버튼 눌렀을 때
        val yesButton = view.findViewById<Button>(R.id.runningActivityYesButton)
        yesButton.setOnClickListener{
            Toast.makeText(this, "Yes 클릭", Toast.LENGTH_SHORT).show()
            when (ns) {
                NoticeState.NOTHING -> {
                }
                NoticeState.PAUSE -> {
                    runningNotificationLayout.visibility = View.GONE
                    manageRunning.pauseRunning()
                }
                NoticeState.SIOP -> {
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
        val recordButton = view.findViewById<Button>(R.id.runningActivityNoButton)
        recordButton.setOnClickListener{
            Toast.makeText(this, "No 클릭", Toast.LENGTH_SHORT).show()
            this.ns = NoticeState.NOTHING
            alertDialog.dismiss()
        }

        alertDialog.setView(view)
        alertDialog.show() //팝업 띄우기

        this.ns = ns
    }
}