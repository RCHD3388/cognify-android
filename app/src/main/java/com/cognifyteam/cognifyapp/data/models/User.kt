package com.cognifyteam.cognifyapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User (
    val firebaseId: String,
    val name: String
): Parcelable{
    companion object
}