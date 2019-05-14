package com.example.eventorganizer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View


class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_activity)

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
                    startActivity(intent)
                    finish()
                }
            }
        }
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Chcesz się wylogować?").setPositiveButton("Tak", dialogClickListener)
            .setNegativeButton("Nie", dialogClickListener).show()
    }

    fun onAllEventsButtonClicked(view : View)
    {
        val intent = Intent(this, AllEventsActivity::class.java)
        startActivity(intent)
    }

}