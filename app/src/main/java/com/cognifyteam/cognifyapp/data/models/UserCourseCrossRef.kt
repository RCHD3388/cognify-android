package com.cognifyteam.cognifyapp.data.models

import androidx.room.Entity
@Entity(primaryKeys = ["firebaseId", "courseId"])
data class UserCourseCrossRef(val firebaseId: String, val courseId: String)