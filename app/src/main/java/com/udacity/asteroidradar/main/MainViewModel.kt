package com.udacity.asteroidradar.main

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val asteroidFilter = MutableLiveData(AsteroidFilter.ALL)

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    private val _potd = MutableLiveData<PictureOfDay>()
    val potd: LiveData<PictureOfDay>
        get() = _potd

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedProperty: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    init {
        getPicOfTheDay()
    }

    var asteroids = Transformations.switchMap(asteroidFilter) {
        when (it!!) {
            AsteroidFilter.WEEK -> asteroidRepository.weeksAsteroids
            AsteroidFilter.DAY -> asteroidRepository.todaysAsteroids
            else -> asteroidRepository.asteroids
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPicOfTheDay() {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids(LocalDate.now().toString(), LocalDate.now().plusDays(7).toString())
            val potdResult = AsteroidApi.retrofitService.getPictureOfTheDay(Constants.API_KEY)
            //Log.d("TAG", potdResult.url)
            if (potdResult.mediaType != "video") {
                _potd.value = potdResult
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun updateFilter(filter: AsteroidFilter) {
        asteroidFilter.value = filter
    }
}

enum class AsteroidFilter {
    ALL, WEEK, DAY, SAVE
}