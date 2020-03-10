package com.umpa2020.tracer.ranking

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.Messenger
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.umpa2020.tracer.MainActivity.Companion.MESSENGER_INTENT_KEY
import com.umpa2020.tracer.R
import com.umpa2020.tracer.locationBackground.LocationBackgroundService
import com.umpa2020.tracer.network.getRanking
import kotlinx.android.synthetic.main.fragment_ranking.view.*
import kotlinx.android.synthetic.main.fragment_ranking.view.test_button1 as test_button11

/**
 * main 화면의 ranking tab
 */
class RankingFragment : Fragment() {
    lateinit var strDate: String
    lateinit var obj: Location
    var mHandler: IncomingMessageHandler? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //TODO: return inflate~~~
        //TODO: Thread 사용하지 말고, 클래스로 빼서 getInfos 처럼 하면 배열이 받아온다는 걸 미리 알 수 있게
        //TODO: activity Created 로 이전
        val view: View = inflater.inflate(R.layout.fragment_ranking, container, false)

        mHandler = IncomingMessageHandler()


        getRanking().getExcuteDESCENDING(context!!, view)

        /**
         * 수진이가 xml 만들어주면 해당 기능 붙히기
         */
        view.test_button11.setOnClickListener {
            getRanking().getExcuteDESCENDING(context!!, view)
        }

        view.test_button3.setOnClickListener {
            getRanking().getFilterRange(context!!, view, obj)
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mHandler = IncomingMessageHandler()
        Intent(context, LocationBackgroundService::class.java).also {
            val messengerIncoming = Messenger(mHandler)
            it.putExtra(MESSENGER_INTENT_KEY, messengerIncoming)

            activity!!.startService(it)
        }
    }

    inner class IncomingMessageHandler : Handler() {
        override fun handleMessage(msg: Message) {

            super.handleMessage(msg)

            when (msg.what) {
                LocationBackgroundService.LOCATION_MESSAGE -> {
                    obj = msg.obj as Location
                }
            }
        }
    }

}
