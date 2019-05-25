package com.example.eventorganizer.cycles

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import com.example.eventorganizer.*
import kotlinx.android.synthetic.main.activity_every_event_list.*
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
            val call2 = eventAPI.getInterested(login, pass, myElements[it].eventId)
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
                        if(!getSharedPreferences("logout", MODE_PRIVATE).getBoolean("logout",true)) {
                            if (myElements[it].interested) {
                                addOrRemoveAlarm(1,myElements[it])
                            } else {
                                addOrRemoveAlarm(2,myElements[it])
                            }
                        }
                    }
                }
            })
        }, {
            if (myElements[it].goingUserCount < myElements[it].limit) {
                val call2 = eventAPI.takePart(login, pass, myElements[it].eventId)
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
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val myintent = Intent(siema, BlankActivity::class.java)
                myintent.putExtra("eventId", myElements[it].eventId)
                myintent.putExtra("image", myElements[it].imagePath)
                myintent.putExtra("desc", myElements[it].description)
                myintent.putExtra("person", myElements[it].userId)
                startActivity(myintent)
            } else {
                var ratingFragment = supportFragmentManager.findFragmentById(R.id.fragment2) as BlankFragment
                ratingFragment.display(myElements[it].imagePath, myElements[it].description, myElements[it].userId)
            }
        })
        val mode = intent.getStringExtra("mode")
        recycler.adapter = myAdapter
        recycler.layoutManager = LinearLayoutManager(this)
        val call2 = eventAPI.getEvents(login, pass, mode)
        call2.enqueue(object : Callback<EventsResult> {
            override fun onFailure(call: Call<EventsResult>, t: Throwable) {
                Toast.makeText(siema, "${t.message}", Toast.LENGTH_LONG).show()
                textView7.text = "Błąd"
            }

            override fun onResponse(call: Call<EventsResult>, response: Response<EventsResult>) {
                val body = response.body()
                if (body != null) {
                    for (ele in body.eventList) {
                        myElements.add(ele)
                    }
                    myAdapter.notifyDataSetChanged()
                    textView7.text = ""
                }
            }
        })
    }

    private fun addOrRemoveAlarm(opt: Int, element: MyElement) {
        val alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Receiver::class.java)
        if(opt == 2) {
            val pendingUpdateIntent = PendingIntent.getBroadcast(applicationContext, element.eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMgr.cancel(pendingUpdateIntent)
        } else if(opt == 1) {
            intent.putExtra("name",element.title)
            val calendar = parseDate(element.date,element.time)
            val alarmIntent = PendingIntent.getBroadcast(applicationContext, element.eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmMgr.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,alarmIntent)
        }
    }

    private fun parseDate(dateArg: String, timeArg: String) : Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        val date = dateArg.split("-")
        val time = timeArg.split(":")
        calendar.set(Calendar.YEAR, date[0].toInt())
        if(date[1][0] == '0') {
            calendar.set(Calendar.MONTH,date[1].substring(1).toInt() - 1)
        } else {
            calendar.set(Calendar.MONTH,date[1].toInt() - 1)
        }
        if(date[2][0] == '0') {
            calendar.set(Calendar.DAY_OF_MONTH,date[2].substring(1).toInt())
        } else {
            calendar.set(Calendar.DAY_OF_MONTH,date[2].toInt())
        }
        if(time[0][0] == '0') {
            if(time[0][0] == '0') {
                calendar.set(Calendar.HOUR_OF_DAY,23)
            } else {
                calendar.set(Calendar.HOUR_OF_DAY,time[0].substring(1).toInt() - 1)
            }
        } else {
            calendar.set(Calendar.HOUR_OF_DAY,time[0].toInt() - 1)
        }
        if(time[1][0] == '0') {
            calendar.set(Calendar.MINUTE,time[1].substring(1).toInt())
        } else {
            calendar.set(Calendar.MINUTE,time[1].toInt())
        }
        return calendar
    }
}
