package com.example.eventorganizer.cycles

data class MyElement(
    val eventId: Int,

    val title: String,
    val userId: String,
    val date: String,
    val time: String,
    val place: String,
    val description: String,
    val limit: Int,
    val imagePath: String,

    var goingUserCount: Int,
    var interestedUserCount: Int,

    var interested: Boolean,
    var takingPart: Boolean
)