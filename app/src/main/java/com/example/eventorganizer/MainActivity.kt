package com.example.eventorganizer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.activity_main)

        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        if (!sharedPref.getBoolean("logout", true)) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun login(view: View) {
        loginButton.isEnabled = false
        loginButton.text = "Logowanie..."

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        var eventAPI = retrofit.create(EventAPI::class.java)

        val call = eventAPI.login(login.text.toString(), password1.text.toString())

        call.enqueue(object : Callback<LoginResult> {
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                displayAlertDialog("Błąd logowania")
                loginButton.isEnabled = true
                loginButton.text = "Zaloguj się"
            }

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if (body != null) {
                    if (body.result == "ok") {
                        loginDataAccepted(body.value)
                    } else {
                        displayAlertDialog("Błędny login lub hasło")
                    }
                }
                loginButton.isEnabled = true
                loginButton.text = "Zaloguj się"
            }
        })
    }

    fun register(view: View) {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }

    private fun loginDataAccepted(firstName: String) {
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString("login", login.text.toString())
        editor.putString("password", password1.text.toString())
        editor.putString("firstName", firstName)
        editor.putBoolean("logout", logout.isChecked.not())
        editor.putBoolean("first",true)
        editor.commit()
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
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
