package com.example.eventorganizer

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_add_event.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class AddEventActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE = 1

    val nameDefault = "Moje wydarzenie"
    val dateDefault = "2022-01-23"
    val timeDefault = "12:55"
    val placeDefault = "Wroclaw"
    val limitDefault = 4

    var taken = false
    lateinit var imageTaken: Bitmap
    lateinit var eventAPI: EventAPI
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        context = this

        val retrofit = Retrofit.Builder()
            .baseUrl("http://eventapi.dx.am")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        eventAPI = retrofit.create(EventAPI::class.java)

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        var tex = year.toString() + "-" + (if (month + 1 < 10) "0" else "") + "${month + 1}-" +
                (if (day < 10) "0" else "") + "$day"
        dateButton.text = tex
        val h = c.get(Calendar.HOUR_OF_DAY)
        val m = c.get(Calendar.MINUTE)
        tex = (if (h < 10) "0" else "") + "$h:" +
                (if (m < 10) "0" else "") + "$m"
        timeButton.text = tex
    }

    fun add(view: View) {
        addButton.isEnabled = false
        addButton.text = "Tworzenie..."
        if (taken.not()) {
            addEvent("")
            return
        }

        val file = convertBitmapToArray()
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("myfile", file.name, reqFile)

        val call1 = eventAPI.uploadImage(body)

        call1.enqueue(object : Callback<LoginResult> {
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                displayAlertDialog("Błąd podczasz tworzenia: " + t.message, false)
                addButton.isEnabled = true
                addButton.text = "Utwórz"
            }

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if (body != null) {
                    if (body.result == "ok") {
                        addEvent(body.value)
                    } else {
                        displayAlertDialog(body.result, false)
                        addButton.isEnabled = true
                        addButton.text = "Utwórz"
                    }
                }
            }
        })
    }

    fun timeButtonClick(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->
            val tex = (if (h < 10) "0" else "") + "$h:" +
                    (if (m < 10) "0" else "") + "$m"
            timeButton.text = tex
        }), hour, minute, true)

        tpd.show()
    }

    fun dateButtonClick(view: View) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, yearr, monthOfYear, dayOfMonth ->
            val tex = yearr.toString() + "-" + (if (monthOfYear + 1 < 10) "0" else "") + "${monthOfYear + 1}-" +
                    (if (dayOfMonth < 10) "0" else "") + "$dayOfMonth"
            dateButton.text = tex
        }, year, month, day)

        dpd.show()
    }

    fun addEvent(abc: String) {
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val login: String = sharedPref.getString("login", "defaultLog")
        val pass: String = sharedPref.getString("password", "defaultPass")

        var nameValue = name.text.toString()
        if (nameValue == "")
            nameValue = nameDefault

        var dateValue = dateButton.text.toString()
        if (dateValue == "")
            dateValue = dateDefault

        var timeValue = timeButton.text.toString()
        if (timeValue == "")
            timeValue = timeDefault

        var placeValue = place.text.toString()
        if (placeValue == "")
            placeValue = placeDefault

        var limitValue = limitDefault
        if (limit.text.toString() != "")
            limitValue = limit.text.toString().toInt()

        val call2 = eventAPI.addEvent(
            login, pass,
            nameValue,
            dateValue,
            timeValue,
            placeValue,
            description.text.toString(),
            limitValue,
            abc
        )
        call2.enqueue(object : Callback<LoginResult> {
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                displayAlertDialog("Błąd podczasz tworzenia", false)
            }

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if (body != null) {
                    if (body.result == "ok") {
                        displayAlertDialog("Utworzyłeś wydarzenie", true)
                    } else {
                        displayAlertDialog(body.result, false)
                    }
                }
            }
        })
    }

    fun convertBitmapToArray(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val filename = "$timeStamp.jpg"
        val file = File(context.cacheDir, filename)
        file.createNewFile()

        if (::imageTaken.isInitialized) {
            val bos = ByteArrayOutputStream()
            imageTaken.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapdata = bos.toByteArray()
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        }
        return file
    }

    fun takePhoto(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                this.imageTaken = data.extras.get("data") as Bitmap
                val scaledImageBitmap = Bitmap.createScaledBitmap(this.imageTaken, 150, 150, false)
                imageView.setImageBitmap(scaledImageBitmap)
                taken = true
            }
        }
    }

    private fun displayAlertDialog(info: String, isRegistered: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(info)
            .setCancelable(false)
            .setPositiveButton("OK") { dialog, _ ->
                if (isRegistered) {
                    finish()
                } else {
                    dialog.cancel()
                }
            }
        val alert = builder.create()
        alert.show()
    }

}

