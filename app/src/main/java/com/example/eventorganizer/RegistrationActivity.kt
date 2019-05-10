package com.example.eventorganizer

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.registration_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistrationActivity : AppCompatActivity() {

    private lateinit var eventAPI : EventAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_activity)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        eventAPI = retrofit.create(EventAPI::class.java)
    }

    fun register(view: View) {
        if(firstName.text.toString() == "" ||  lastName.text.toString() == "" || nick.text.toString() == "" ||
            password.text.toString() == "" || repeatedPassword.text.toString() == "") {
            displayAlertDialog("Nie podano wszystkich danych",false)
        } else {
            if(password.text.toString() != repeatedPassword.text.toString()) {
                displayAlertDialog("Podane hasła różnią się",false)
            } else {
                val call = eventAPI.registration(firstName.text.toString(),lastName.text.toString(),nick.text.toString(),password.text.toString())
                call.enqueue( object : Callback<LoginResult> {
                    override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                        displayAlertDialog("Błąd rejestracji",false)
                    }
                    override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                        val body = response.body()
                        if(body != null) {
                            if(body.result == "ok") {
                                displayAlertDialog("Rejestracja powiodła się",true)
                            } else {
                                displayAlertDialog("Użytkownik o danej nazwie już istnieje", false)
                            }
                        }
                    }
                })
            }
        }
    }

    private fun displayAlertDialog(info: String, isRegistered: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(info)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                if(isRegistered) {
                    finish()
                } else {
                    dialog.cancel()
                }
            }
        val alert = builder.create()
        alert.show()
    }
}