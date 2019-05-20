package com.example.eventorganizer.cuboid

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.eventorganizer.R

class AllEventsActivity : AppCompatActivity(), EventListFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_events)
    }

    override fun onListFragmentInteraction(item: EventContent.EventItem?) {
        val intent = Intent(this, EventDetailsActivity:: class.java)
        intent.putExtra("event",item)
        startActivity(intent)

    }
}
