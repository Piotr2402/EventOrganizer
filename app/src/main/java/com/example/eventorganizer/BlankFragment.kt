package com.example.eventorganizer


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso


class BlankFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity?.intent != null) {
            val index = activity!!.intent.getIntExtra("eventId", -1)
            if (index >= 0) {
                val image = activity!!.intent.getStringExtra("image")
                val desc = activity!!.intent.getStringExtra("desc")
                val person = activity!!.intent.getStringExtra("person")
                display(image, desc, person)
            }
        }
    }

    fun display(image: String, desc: String, person: String) {
        if (image != "")
            Picasso.with(context).load(image)
                .into(activity!!.findViewById<ImageView>(R.id.imageView3))
        activity!!.findViewById<TextView>(R.id.textView5).text = desc
        activity!!.findViewById<TextView>(R.id.textView6).text = "Organizujący: $person"
    }

}
