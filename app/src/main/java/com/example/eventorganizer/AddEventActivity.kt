package com.example.eventorganizer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
    val dateDefault = "22-01-2022"
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
    }

    fun add(view: View) {
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
                displayAlertDialog("Błąd podczasz tworzenia" + t.message, false)
            }

            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val body = response.body()
                if (body != null) {
                    if (body.result == "ok") {
                        addEvent(body.value);
                    } else {
                        displayAlertDialog(body.result, false)
                    }
                }
            }
        })
    }

    fun addEvent(abc: String) {
        val sharedPref = getSharedPreferences("logout", MODE_PRIVATE)
        val login: String = sharedPref.getString("login", "defaultLog")
        val pass: String = sharedPref.getString("password", "defaultPass")

        var nameValue = name.text.toString()
        if (nameValue == "")
            nameValue = nameDefault

        var dateValue = date.text.toString()
        if (dateValue == "")
            dateValue = dateDefault

        var placeValue = place.text.toString()
        if (placeValue == "")
            placeValue = placeDefault

        var limitValue = limitDefault
        if (limit.text.toString() != "")
            limitValue = limit.text.toString().toInt()

        val call2 =
            eventAPI.addEvent(nameValue, dateValue, placeValue, limitValue, login, pass, abc)
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

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            imageTaken.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
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

