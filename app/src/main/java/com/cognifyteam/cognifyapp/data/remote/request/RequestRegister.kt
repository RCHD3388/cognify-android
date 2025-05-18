package com.cognifyteam.cognifyapp.data.remote.request
import java.io.Serializable


data class RequestRegister(
    var firebaseId: String,
    var email: String,
    var role: String,
):Serializable

data class RequestGetUser(
    var firebaseId: String,
):Serializable