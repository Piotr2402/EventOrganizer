package com.example.eventorganizer

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface EventAPI {

    @POST("/users/login/")
    @FormUrlEncoded
    fun login(@Field("login") login : String, @Field("password") password: String) : Call<LoginResult>

    @POST("/users/register/")
    @FormUrlEncoded
    fun registration(@Field("firstName") firstName: String, @Field("lastName") lastName : String,
                 @Field("login") login : String, @Field("password") password: String ) : Call<LoginResult>

    @Multipart
    @POST("/events/add/")
    fun addEvent(@Part("title") title: String, @Part("date") date : String,
                 @Part("place") place: String, @Part("limit") limit : Int,
                 @Part("login") login : String, @Part("password") password: String,
                 @Part("myfile") file: RequestBody
    ) : Call<LoginResult>


}