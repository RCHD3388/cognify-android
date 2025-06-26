package com.cognifyteam.cognifyapp.data.models

import androidx.room.Entity

// Entity untuk tabel penghubung (junction table)
@Entity(primaryKeys = ["followerId", "followingId"])
data class FollowsCrossRef(
    val followerId: String,
    val followingId: String
)