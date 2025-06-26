package com.cognifyteam.cognifyapp.data.models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

// Untuk mendapatkan daftar user yang DI-FOLLOW oleh user utama
data class UserWithFollowing(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "firebaseId", // Kolom di UserEntity (sebagai follower)
        entityColumn = "firebaseId",   // Kolom di UserEntity (sebagai yang di-follow)
        associateBy = Junction(
            value = FollowsCrossRef::class,
            parentColumn = "followerId", // Kolom di FollowsCrossRef yang menunjuk ke user utama
            entityColumn = "followingId" // Kolom di FollowsCrossRef yang menunjuk ke user yang di-follow
        )
    )
    val following: List<UserEntity>
)

// Untuk mendapatkan daftar user yang MENGIKUTI user utama (followers)
data class UserWithFollowers(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "firebaseId", // Kolom di UserEntity (sebagai yang di-follow)
        entityColumn = "firebaseId",   // Kolom di UserEntity (sebagai follower)
        associateBy = Junction(
            value = FollowsCrossRef::class,
            parentColumn = "followingId", // Kolom di FollowsCrossRef yang menunjuk ke user utama
            entityColumn = "followerId"   // Kolom di FollowsCrossRef yang menunjuk ke user yang me-follow
        )
    )
    val followers: List<UserEntity>
)