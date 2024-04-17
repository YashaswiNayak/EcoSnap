package com.example.ecosnap

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("auth/register")
    fun register(@Body user: User): Call<ResponseBody>

    @POST("auth/login")
    fun login(@Body credentials: LoginCredentials): Call<LoginResponse>

    @GET("profile/profileDetails")
    fun getUserDetails(@Header("authorization") token: String): Call<UserDetails>

    @POST("post/create")
    fun createPost(@Header("authorization") token: String, @Body post: Post): Call<PostResponse>

    @GET("post/fetchPosts")
    fun fetchPosts(@Header("authorization") token: String): Call<List<PostResponse>>

    @POST("post/addComment")
    fun addComment(@Header("authorization") token: String, @Body comment: CommentFields): Call<ResponseBody>

    @POST("post/optIn")
    fun optIn(@Header("authorization") token: String, @Body id: OptRes): Call<ResponseBody>

    @POST("post/optOut")
    fun optOut(@Header("authorization") token: String, @Body id: OptRes?): Call<ResponseBody>

    @GET("post/fetchUserPosts")
    fun fetchUserPosts(@Header("authorization") token: String): Call<List<PostResponse>>

    @GET("post/fetchComments")
    fun fetchComments(@Header("authorization") token: String, @Query("postId") postId: String): Call<List<CommentResponse>>

    @POST("post/donate")
    fun donate(@Header("authorization") token: String, @Body donation: Donation): Call<ResponseBody>

}
