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
import kotlinx.coroutines.launch
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
        getAsteroidProperties()
    }

    private fun getAsteroidProperties() {
        viewModelScope.launch {
            try {
                val potdResult = AsteroidApi.retrofitService.getPictureOfTheDay()
                _potd.value = potdResult
                val startDate = Calendar.getInstance().time
                val endDate = Calendar.getInstance()
                endDate.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val fmt = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val formattedStartDate = fmt.format(startDate)
                val formattedEndDate = fmt.format(endDate.time)
                Log.d("TAG", formattedStartDate)
                Log.d("TAG", formattedEndDate)
                val listResult = AsteroidApi.retrofitService.getAsteroids("6fk1mdLgLPWyLKZgcfqIfX2bd0Pk67YX6K5zLAZ0", formattedStartDate, formattedEndDate)
                Log.d("TAG", listResult.toString())
                _asteroids.value = parseAsteroidsJsonResult(listResult)
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }
}