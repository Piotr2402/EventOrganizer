package com.example.eventorganizer.cycles

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.eventorganizer.EventAPI
import com.example.eventorganizer.EventsResult
import com.example.eventorganizer.LoginResult
import com.example.eventorganizer.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MyAllEventsActivity : AppCompatActivity() {
    private lateinit var recycler: RecyclerView
    val myElements = ArrayList<MyElement>()
    private lateinit var myAdapter: MyAdapter
    lateinit var eventAPI: EventAPI
    lateinit var login: String
    lateinit var pass: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_every_event_list)

        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        login = sharedPref.getString("login", "defaultLog")
        pass = sharedPref.getString("password", "defaultPass")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        eventAPI = retrofit.create(EventAPI::class.java)

        recycler = findViewById(R.id.recyclerView)
        recycler.setHasFixedSize(true)
        val siema = this
        myAdapter = MyAdapter(myElements, {
            val call2 = eventAPI.getInterested(login, pass, it)
            call2.enqueue(object : Callback<LoginResult> {
                override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                    Toast.makeText(siema, "${t.message}", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                    val body = response.body()
                    if (body != null) {
                            myElements[it].interestedUserCount += if (myElements[it].interested) -1 else 1
                            myElements[it].interested = myElements[it].interested.not()
                        myAdapter.notifyItemChanged(it)
                    }
                }
            })
        }, {
            if (myElements[it].goingUserCount < myElements[it].limit) {
                val call2 = eventAPI.takePart(login, pass, it)
                call2.enqueue(object : Callback<LoginResult> {
                    override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                        Toast.makeText(siema, "${t.message}", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                        val body = response.body()
                        if (body != null) {
                            myElements[it].goingUserCount += if (myElements[it].takingPart) -1 else 1
                            myElements[it].takingPart = myElements[it].takingPart.not()
                            myAdapter.notifyItemChanged(it)
                        }
                    }
                })
            }
        }, {

        })
        recycler.adapter = myAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        val call2 = eventAPI.getEvents(login, pass)
        call2.enqueue(object : Callback<EventsResult> {
            override fun onFailure(call: Call<EventsResult>, t: Throwable) {
                Toast.makeText(siema, "siema${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<EventsResult>, response: Response<EventsResult>) {
                val body = response.body()
                if (body != null) {
                    for (ele in body.eventList) {
                        //Toast.makeText(siema, "${ele}", Toast.LENGTH_LONG).show()
                        myElements.add(ele)
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
        })
    }
}
