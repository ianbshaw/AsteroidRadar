package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.api.AsteroidProperty

class MainViewModel : ViewModel() {

    private val _response = MutableLiveData<String>()

    val response: LiveData<String>
        get() = _response

    private val _status = MutableLiveData<AsterApiStatus>()
    val status: LiveData<MarsApiStatus>
        get() = _status

    private val _properties = MutableLiveData<List<AsteroidProperty>>()
    val properties: LiveData<List<AsteroidProperty>>
        get() = _properties

    private val _navigateToSelectedProperty = MutableLiveData<AsteroidProperty>()
    val navigateToSelectedProperty: LiveData<AsteroidProperty>
        get() = _navigateToSelectedProperty
}