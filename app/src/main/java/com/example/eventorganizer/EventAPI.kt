package com.example.eventorganizer

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface EventAPI {

    @POST("/users/login/")
    @FormUrlEncoded
    fun login(@Field("login") login: String, @Field("password") password: String): Call<LoginResult>

    @POST("/users/register/")
    @FormUrlEncoded
    fun registration(
        @Field("firstName") firstName: String, @Field("lastName") lastName: String,
        @Field("login") login: String, @Field("password") password: String
    ): Call<LoginResult>

    @POST("/events/upload/")
    @Multipart
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<LoginResult>

    @POST("/events/add/")
    @FormUrlEncoded
    fun addEvent(
        @Field("title") title: String, @Field("date") date: String,
        @Field("place") place: String, @Field("limit") limit: Int,
        @Field("login") login: String, @Field("password") password: String,
        @Field("picture") picture: String
    ): Call<LoginResult>


}