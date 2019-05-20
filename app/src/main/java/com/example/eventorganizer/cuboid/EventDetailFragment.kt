package com.example.eventorganizer.cuboid

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventorganizer.R
import kotlinx.android.synthetic.main.fragment_event.*

class EventDetailFragment : Fragment() {

    companion object {
        fun newInstance(event: EventContent.EventItem): EventDetailFragment
        {
            val args = Bundle()
            args.putSerializable("eventItem",event)
            val fragment = EventDetailFragment()
            fragment.arguments = args

            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onStart() {
        super.onStart()
        val event = arguments!!.getSerializable("eventItem")as EventContent.EventItem
        event_image.setImageResource(event.imageID)
        event_title.text = event.title
        event_place.text = event.place
    }
}