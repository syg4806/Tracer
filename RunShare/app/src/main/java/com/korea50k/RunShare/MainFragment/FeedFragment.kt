package com.korea50k.RunShare.MainFragment/*

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.korea50k.RunShare.Activities.RankFragment.FeedPagerAdapter
import com.korea50k.RunShare.dataClass.Rank_Users
import kotlinx.android.synthetic.main.fragment_feed.*
import android.R
import android.widget.*
import kotlinx.android.synthetic.main.fragment_feed.view.*
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import android.opengl.ETC1.getWidth






class FeedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater!!.inflate(com.korea50k.RunShare.R.layout.fragment_feed, container, false)

        val choice_btn = view.findViewById<View>(com.korea50k.RunShare.R.id.feed_choiceoption_button) as Button
        choice_btn.setOnClickListener{
            Toast.makeText(context, "눌림", Toast.LENGTH_SHORT).show()
            val popupMenu: PopupMenu = PopupMenu(context,choice_btn)
            popupMenu.menuInflater.inflate(com.korea50k.RunShare.R.menu.popup_menu_feed,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when(item.itemId) {
                    com.korea50k.RunShare.R.id.popupmenu_map -> {
                        item.isChecked = !item.isChecked
                        true
                        Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()

                    }
                    com.korea50k.RunShare.R.id.popupmenu_feed ->{
                        item.isChecked = !item.isChecked
                        true
                        Toast.makeText(context, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                    }

                }
                true
            })
            popupMenu.show()


        }
/*
        val player_btn = view.findViewById<View>(R.id.rank_player_button) as Button
        player_btn.setOnClickListener{

        }

*/
        /*var indicatorWidth = 0 //indicator너비 초기화

        val fragmentAdapter = FeedPagerAdapter(activity!!.supportFragmentManager, 2) //프래그먼트 붙임
        view.rank_pager.adapter = fragmentAdapter

        /////////수정해야할 부분

        /*view.tab_rank.setupWithViewPager(view.rank_pager)

        //동적으로 indicator 가로 너비 정함
        view.tab_rank.post(Runnable {

            indicatorWidth =  view.tab_rank.getWidth() /  2 //TabLayout 너비의 절반 만큼 크기 정함

            //새로운 너비를 indicator에 할당
            val indicatorParams = view.indicator.getLayoutParams() as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            view.indicator.setLayoutParams(indicatorParams)




        })*/

        /*view.rank_pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener { //뷰페이저 안의 페이저가 변화되었을 때 호출

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
        })*/

        return view
    }

}
        */