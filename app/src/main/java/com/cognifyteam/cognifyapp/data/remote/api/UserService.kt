package com.cognifyteam.cognifyapp.data.remote.api
import com.cognifyteam.cognifyapp.data.remote.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
interface UserService {
//    @POST("login")
//    suspend fun login(@Body loginRequest: RequestLogin): LoginResponse
//
//    @POST("register")
//    suspend fun register(@Body registerRequest: RequestRegister): RegisterResponse

    @GET("/users/user")
    suspend fun getUser(@Body firebaseId: String): UserResponse

}