package com.umpa2020.tracer.main.challenge

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umpa2020.tracer.R
import com.umpa2020.tracer.dataClass.ChallengeData
import com.umpa2020.tracer.util.OnSingleClickListener
import kotlinx.android.synthetic.main.recycler_challengefragment_item.view.*

class ChallengeRecyclerViewAdapter(var challenge: ArrayList<ChallengeData>) :
  RecyclerView.Adapter<ChallengeRecyclerViewAdapter.ItemHolder>(){


  var context: Context? = null


  override fun onBindViewHolder(holder: ItemHolder, position: Int) {
    val singleItem1 = challenge[position]
    val ChallengeData: ChallengeData = challenge.get(position)

    holder.icons.setImageResource(ChallengeData.iconsBar!!)
    holder.recename.text = ChallengeData.nametxt
    holder.racedate.text = ChallengeData.datetxt
    holder.raceweek.text = ChallengeData.dateweek

    holder.itemView.setOnClickListener(object : OnSingleClickListener {
      override fun onSingleClick(v: View?) {
        val nextIntent = Intent(context, ChallengeRecycleritemClickActivity::class.java)
        nextIntent.putExtra("challenge", singleItem1.nametxt) //????????
        context!!.startActivity(nextIntent)
      }
    })

  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
    val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_challengefragment_item, parent, false)
    context = parent.context
    return ItemHolder(itemHolder)
  }

  override fun getItemCount(): Int {
    return challenge.size
  }




  inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view){
    var icons = view.challenge_map
    var recename = view.challenge_race_name
    var racedate = view.challenge_date
    var raceweek = view.challenge_week
  }

}