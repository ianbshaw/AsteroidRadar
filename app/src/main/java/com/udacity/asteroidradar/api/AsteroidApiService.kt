package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.main.AsteroidFilter
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import kotlinx.coroutines.Deferred
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface AsteroidApiService {
    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(@Query("api_key") apiKey: String): PictureOfDay

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(@Query("filter") type: AsteroidFilter,
                             @Query("api_key") apiKey: String,
                             @Query("start_date") startDate: String,
                             @Query("end_date") endDate: String): String
}

object AsteroidApi {
    val retrofitService : AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}
