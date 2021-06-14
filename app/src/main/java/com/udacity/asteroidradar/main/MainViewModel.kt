package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _potd = MutableLiveData<PictureOfDay>()
    val potd: LiveData<PictureOfDay>
        get() = _potd

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPicOfTheDay()
        //getAsteroidProperties()
    }

   // val asteroids = asteroidRepository.asteroids

    private fun getPicOfTheDay() {
        viewModelScope.launch {
            //asteroidRepository.refreshAsteroids()
            val potdResult = AsteroidApi.retrofitService.getPictureOfTheDay(Constants.API_KEY)
            //Log.d("TAG", potdResult.url)
            if (potdResult.mediaType != "video") {
                _potd.value = potdResult
            }
            getAsteroidProperties(AsteroidApiFilter.SHOW_ALL)
        }
    }

    private fun getAsteroidProperties(filter: AsteroidApiFilter) {
        viewModelScope.launch {
            try {
                val startDate = Calendar.getInstance().time
                val endDate = Calendar.getInstance()
                endDate.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val fmt = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val formattedStartDate = fmt.format(startDate)
                val formattedEndDate = fmt.format(endDate.time)
                //Log.d("TAG", formattedStartDate)
                //Log.d("TAG", formattedEndDate)
                val listResult =
                    AsteroidApi.retrofitService.getAsteroids(filter.value ,Constants.API_KEY,
                        formattedStartDate, formattedEndDate)
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(listResult.body()!!))
                //Log.d("TAG", _asteroids.value.toString())

            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        getAsteroidProperties(filter)
    }
}