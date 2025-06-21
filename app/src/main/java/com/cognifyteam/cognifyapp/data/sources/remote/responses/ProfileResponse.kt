package com.cognifyteam.cognifyapp.data.remote.responses

// Ganti import Gson dengan Moshi
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

// Anotasi @JsonClass(generateAdapter = true) memberi tahu Moshi untuk
// menggunakan codegen yang sudah Anda pasang (kapt) agar performanya cepat.
@JsonClass(generateAdapter = true)
data class ProfileResponse(
    @Json(name = "message") // Ganti @SerializedName dengan @Json
    val message: String,

    @Json(name = "data")
    val data: UserDto
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "firebaseId") // Ganti @SerializedName dengan @Json
    val firebaseId: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "role")
    val role: String
)

// Kelas ini merepresentasikan struktur paling luar (Tingkat 1)
@JsonClass(generateAdapter = true)
data class BaseResponse<T>(
    @Json(name = "status")
    val status: String,

    @Json(name = "code")
    val code: Int,

    @Json(name = "data")
    val data: T // Menunjuk ke objek data di Tingkat 2
)

// Kelas ini merepresentasikan objek "data" di Tingkat 2
@JsonClass(generateAdapter = true)
data class ProfileData(
    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val user: UserDto // Menunjuk ke objek data di Tingkat 3 (data user)
)

// Kelas ini merepresentasikan objek "data" di Tingkat 3 (informasi user)
// TIDAK ADA PERUBAHAN DI SINI
@JsonClass(generateAdapter = true)
data class UserDto2(
    @Json(name = "firebaseId")
    val firebaseId: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "role")
    val role: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "updatedAt")
    val updatedAt: String
)