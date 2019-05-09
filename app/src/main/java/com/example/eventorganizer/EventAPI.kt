package com.example.eventorganizer

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface EventAPI {

    @POST("/users/login/")
    @FormUrlEncoded
    fun login(@Field("login") login : String, @Field("password") password: String) : Call<LoginResult>
}