package com.example.eventorganizer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.example.eventorganizer.cycles.MyAllEventsActivity
import com.example.eventorganizer.cycles.MyElement
import kotlinx.android.synthetic.main.menu_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log


class MenuActivity : AppCompatActivity() {

    private var logout = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)
        title2.text = "Witaj " + getSharedPreferences("logout", MODE_PRIVATE).getString("firstName", "")
        logout = getSharedPreferences("logout", MODE_PRIVATE).getBoolean("logout",true)
        if(!logout && getSharedPreferences("logout", MODE_PRIVATE).getBoolean("first",false)) {
            loadAlarms()
        }
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("first",false)
        editor.commit()
    }

    override fun onBackPressed() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putBoolean("logout",true)
                    editor.commit()
                    val intent = Intent(this, MainActivity::class.java)
                    clearAlarms()
                    startActivity(intent)
                    finish()
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Chcesz się wylogować?").setPositiveButton("Tak", dialogClickListener)
            .setNegativeButton("Nie", dialogClickListener).show()
    }

    fun onAllEventsButtonClicked(view : View) {
        val intent = Intent(this, MyAllEventsActivity::class.java)
        intent.putExtra("mode", "all")
        startActivity(intent)
    }

    fun onOrganisedEventsButtonClicked(view : View) {
        val intent = Intent(this, MyAllEventsActivity::class.java)
        intent.putExtra("mode", "organised")
        startActivity(intent)
    }

    fun onPartEventsButtonClicked(view : View) {
        val intent = Intent(this, MyAllEventsActivity::class.java)
        intent.putExtra("mode", "takingPart")
        startActivity(intent)
    }

    fun addEvent(view : View) {
        val intent = Intent(this, AddEventActivity::class.java)
        startActivity(intent)
    }

    private fun loadAlarms() {
        val intent = Intent(this, Receiver::class.java)
        val alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val eventAPI = retrofit.create(EventAPI::class.java)
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val login = sharedPref.getString("login", "defaultLog")
        val pass = sharedPref.getString("password", "defaultPass")

        val call = eventAPI.getEvents(login,pass,"interested")
        call.enqueue(object : Callback<EventsResult> {
            override fun onFailure(call: Call<EventsResult>, t: Throwable) {
               println("${t.message}")
            }

            override fun onResponse(call: Call<EventsResult>, response: Response<EventsResult>) {
                val body = response.body()
                if (body != null) {
                    for (element in body.eventList) {
                        intent.putExtra("name",element.title)
                        val calendar = parseDate(element.date,element.time)
                        val calendar2 = Calendar.getInstance()
                        calendar2.timeInMillis = System.currentTimeMillis()
                        if(calendar2.timeInMillis < calendar.timeInMillis) {
                            val alarmIntent = PendingIntent.getBroadcast(applicationContext, element.eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                            alarmMgr.setExact(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,alarmIntent)
                        }
                    }
                }
            }
        })
    }

    private fun clearAlarms() {
        val alarmMgr: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, Receiver::class.java)
        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val eventAPI = retrofit.create(EventAPI::class.java)
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val login = sharedPref.getString("login", "defaultLog")
        val pass = sharedPref.getString("password", "defaultPass")

        val call = eventAPI.getEvents(login,pass,"interested")
        call.enqueue(object : Callback<EventsResult> {
            override fun onFailure(call: Call<EventsResult>, t: Throwable) {
                println("${t.message}")
            }
            override fun onResponse(call: Call<EventsResult>, response: Response<EventsResult>) {
                val body = response.body()
                if (body != null) {
                    for (ele in body.eventList) {
                        val pendingUpdateIntent = PendingIntent.getBroadcast(applicationContext, ele.eventId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                        alarmMgr.cancel(pendingUpdateIntent)
                    }
                }
            }
        })
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