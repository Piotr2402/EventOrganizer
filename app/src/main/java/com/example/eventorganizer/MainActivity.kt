package com.example.eventorganizer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        //api
        var eventAPI = retrofit.create(EventAPI::class.java)

        val call = eventAPI.login("root", "root1")

        call.enqueue( object : Callback<LoginResult> {
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                result.text = "Error"
            }

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if(body != null) {
                    result.text  = body!!.result
                }
            }

        })
    }
}
