package com.example.eventorganizer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v7.app.AlertDialog


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        if(!sharedPref.getBoolean("logout",true)) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun login(view: View) {

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var eventAPI = retrofit.create(EventAPI::class.java)

        val call = eventAPI.login( login.text.toString(), password1.text.toString())

        call.enqueue( object : Callback<LoginResult> {
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                displayAlertDialog("Błąd logowania")
            }
            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if(body != null) {
                    if(body.result == "ok") {
                        loginDataAccepted()
                    } else {
                        displayAlertDialog("Błędny login lub hasło")
                    }
                }
            }
        })
    }

    fun register(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }


    private fun loginDataAccepted() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val editor = sharedPref.edit()
        if(logout.isChecked) {
            editor.putBoolean("logout", false)
        } else {
            editor.putBoolean("logout",true)
        }
        editor.commit()
        finish()

    }

    private fun displayAlertDialog(info: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(info)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.cancel()
            }
        val alert = builder.create()
        alert.show()
    }
}
