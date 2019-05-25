package com.example.eventorganizer


import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_blank.*


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
        val params = textView6.layoutParams as ConstraintLayout.LayoutParams
        if (image != "") {
            activity!!.findViewById<ImageView>(R.id.imageView3).visibility = View.VISIBLE
            params.verticalBias = 0.7f
            textView6.requestLayout()
            Picasso.with(context).load(image)
                .into(activity!!.findViewById<ImageView>(R.id.imageView3))
        } else {
            activity!!.findViewById<ImageView>(R.id.imageView3).visibility = View.GONE
            params.verticalBias = 0.2f
            textView6.requestLayout()
        }


        activity!!.findViewById<TextView>(R.id.textView5).text = desc
        activity!!.findViewById<TextView>(R.id.textView6).text = "Organizujący: $person"
    }

}
