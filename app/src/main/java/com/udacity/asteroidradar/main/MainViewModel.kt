package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.parseStringToAsteroidList
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    private val _potd = MutableLiveData<PictureOfDay>()
    val potd: LiveData<PictureOfDay>
        get() = _potd

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        getPicOfTheDay()
        //getAsteroidProperties()
    }

    private fun getPicOfTheDay() {
        viewModelScope.launch {
            val potdResult = AsteroidApi.retrofitService.getPictureOfTheDay(Constants.API_KEY)
            Log.d("TAG", potdResult.url)
            if (potdResult.mediaType != "video") {
                _potd.value = potdResult
            }
            getAsteroidProperties()
        }
    }

    suspend fun getAsteroidProperties() {
        viewModelScope.launch {
            try {
                val startDate = Calendar.getInstance().time
                val endDate = Calendar.getInstance()
                endDate.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val fmt = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val formattedStartDate = fmt.format(startDate)
                val formattedEndDate = fmt.format(endDate.time)
                Log.d("TAG", formattedStartDate)
                Log.d("TAG", formattedEndDate)
                val listResult = AsteroidApi.retrofitService.getAsteroids(Constants.API_KEY, formattedStartDate, formattedEndDate)
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(listResult.body()!!))
                Log.d("TAG", _asteroids.value.toString())
/*                if (listResult.isSuccessful && listResult.body() != null) {
                    val result = parseStringToAsteroidList(listResult.body()!!)
                    val aList = parseAsteroidsJsonResult(JSONObject(result.toString()))
                    Log.d("TAG", aList.toString())
                }*/
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }
}