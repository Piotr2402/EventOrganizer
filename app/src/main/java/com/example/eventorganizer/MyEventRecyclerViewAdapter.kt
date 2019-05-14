package com.example.eventorganizer

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


import com.example.eventorganizer.EventListFragment.OnListFragmentInteractionListener
import com.example.eventorganizer.EventContent.EventItem

import kotlinx.android.synthetic.main.fragment_event.view.*

/**
 * [RecyclerView.Adapter] that can display a [EventItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyEventRecyclerViewAdapter(
    private val mValues: List<EventItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as EventItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_event, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.mTitleView.text = item.title
        holder.mEventPlace.text = item.place
        holder.mEventImage.setImageResource(item.imageID)


        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mTitleView: TextView = mView.event_title
        val mEventPlace: TextView = mView.event_place
        val mEventImage: ImageView = mView.event_image

        override fun toString(): String {
            return super.toString() + " '" + mTitleView.text + "'"
        }
    }
}
