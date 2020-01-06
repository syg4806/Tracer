package com.korea50k.RunShare.MainFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.korea50k.RunShare.Activities.RankFragment.RankPagerAdapter
import android.widget.*
import kotlinx.android.synthetic.main.fragment_rank.view.*
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import com.korea50k.RunShare.R
import kotlinx.android.synthetic.main.fragment_rank.*


class RankFragment : Fragment() {
    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(com.korea50k.RunShare.R.layout.fragment_rank, container, false)

        val choice_btn = view.findViewById<View>(com.korea50k.RunShare.R.id.rank_choiceoption_button) as Button
        choice_btn.setOnClickListener{
            count++//누를때마다 증가
            Toast.makeText(context, "눌림", Toast.LENGTH_SHORT).show()
            Log.d("asdf", count.toString())


            if(count % 2 == 1){
                view.rank_ExecuteLikeChoice_linearlaoyut.visibility = View.VISIBLE
                choice_btn.setBackgroundResource(R.drawable.ic_up_button)
            }
            if(count % 2 == 0){
                view.rank_ExecuteLikeChoice_linearlaoyut.visibility = View.GONE
                choice_btn.setBackgroundResource(R.drawable.ic_down_button)
            }
        }

        var indicatorWidth = 0 //indicator너비 초기화

        val fragmentAdapter = RankPagerAdapter(activity!!.supportFragmentManager, 2) //프래그먼트 붙임
        view.rank_pager.adapter = fragmentAdapter
        view.tab_rank.setupWithViewPager(view.rank_pager)

        //동적으로 indicator 가로 너비 정함
        view.tab_rank.post(Runnable {

            indicatorWidth =  view.tab_rank.getWidth() /  2 //TabLayout 너비의 절반 만큼 크기 정함

            //새로운 너비를 indicator에 할당
            val indicatorParams = view.indicator.getLayoutParams() as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            view.indicator.setLayoutParams(indicatorParams)
        })

        view.rank_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener { //뷰페이저 안의 페이저가 변화되었을 때 호출

            override fun onPageScrolled(i: Int, positionOffset: Float, positionOffsetPx: Int) {
                val params = view.indicator.getLayoutParams() as FrameLayout.LayoutParams

                //Multiply positionOffset with indicatorWidth to get translation
                val translationOffset = (positionOffset + i) * indicatorWidth //indicator 너비 만큼 위치 슬라이드 이동
                params.leftMargin = translationOffset.toInt()
                view.indicator.setLayoutParams(params)
            }

            override fun onPageSelected(i: Int) {

            }

            override fun onPageScrollStateChanged(i: Int) {

            }
        })

        return view
    }

}