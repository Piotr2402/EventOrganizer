package com.example.eventorganizer.cycles

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.eventorganizer.R
import com.squareup.picasso.Picasso

class MyAdapter(
    private val mDataList: ArrayList<MyElement>,
    val getInterestedClickListener: (Int) -> Unit,
    val takePartClickListener: (Int) -> Unit,
    val clickListener: (Int) -> Unit
) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = mDataList[position].title
        holder.details.text = "${mDataList[position].date} ${mDataList[position].time} ${mDataList[position].place}"
        holder.cardView.isClickable = true
        holder.cardView.setOnClickListener {
            clickListener(position)
        }

        holder.interested.isClickable = true
        holder.interested.setOnClickListener {
            getInterestedClickListener(position)
        }
        holder.interested.textOff = "Zainteresowanych: ${mDataList[position].interestedUserCount}"
        holder.interested.textOn = "Zainteresowanych: ${mDataList[position].interestedUserCount}"

            holder.takingPart.isClickable = true
        holder.takingPart.setOnClickListener {
            takePartClickListener(position)
        }
        holder.takingPart.textOff = "Weź udział (${mDataList[position].goingUserCount}/${mDataList[position].limit})"
        holder.takingPart.textOn = "Zrezygnuj (${mDataList[position].goingUserCount}/${mDataList[position].limit})"

        if (mDataList[position].imagePath != "") {
            holder.imageView.visibility = View.VISIBLE
            holder.updateWithUrl(mDataList[position].imagePath)
        } else {
            holder.imageView.visibility = View.GONE
        }

        holder.interested.isChecked = mDataList[position].interested
        holder.takingPart.isChecked = mDataList[position].takingPart
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var cardView: CardView
        internal var title: TextView
        internal var details: TextView
        internal var imageView: ImageView
        internal var interested: ToggleButton
        internal var takingPart: ToggleButton

        init {
            cardView = itemView.findViewById<View>(R.id.cardzzz) as CardView
            title = itemView.findViewById<View>(R.id.textView3) as TextView
            details = itemView.findViewById<View>(R.id.textView4) as TextView
            imageView = itemView.findViewById<View>(R.id.imageView2) as ImageView
            interested = itemView.findViewById<View>(R.id.toggleButton) as ToggleButton
            takingPart = itemView.findViewById<View>(R.id.toggleButton2) as ToggleButton
        }

        fun updateWithUrl(url: String) {
            Picasso.with(itemView.context).load(url).resize(100, 100).centerCrop().into(imageView)
        }
    }
}