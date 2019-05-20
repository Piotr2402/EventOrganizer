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
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("title") title: String,
        @Field("date") date: String,
        @Field("time") time: String,
        @Field("place") place: String,
        @Field("description") description: String,
        @Field("limit") limit: Int,
        @Field("picture") picture: String
    ): Call<LoginResult>

    @POST("/events/show/")
    @FormUrlEncoded
    fun getEvents(
        @Field("login") login: String,
        @Field("password") password: String
    ): Call<EventsResult>

    @POST("/users/getInterested/")
    @FormUrlEncoded
    fun getInterested(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("eventId") eventId: Int
    ): Call<LoginResult>

    @POST("/users/takePart/")
    @FormUrlEncoded
    fun takePart(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("eventId") eventId: Int
    ): Call<LoginResult>
}