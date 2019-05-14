package com.example.eventorganizer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class EventDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val event : EventContent.EventItem = intent.extras!!.getSerializable("event") as EventContent.EventItem

        val detailsFragment = EventDetailFragment.newInstance(event)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.detail_layout, detailsFragment)
            .commit()
    }
}
