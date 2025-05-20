package com.cognifyteam.cognifyapp.data.sources.remote.auth

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class UserJson(
    val firebaseId:String,
    var name:String,
)