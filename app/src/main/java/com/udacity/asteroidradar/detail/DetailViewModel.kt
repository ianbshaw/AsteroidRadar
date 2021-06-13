package com.udacity.asteroidradar.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid

class DetailViewModel(asteroid: Asteroid, app: Application) : AndroidViewModel(app) {

    private val _selectedAsteroid = MutableLiveData<Asteroid>()
    val selectedAsteroid : LiveData<Asteroid>
        get() = _selectedAsteroid

    init {
        _selectedAsteroid.value = asteroid
    }

    //val displayAsteroid
}