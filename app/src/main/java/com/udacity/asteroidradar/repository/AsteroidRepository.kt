package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val startDate = Calendar.getInstance().time
            val endDate = Calendar.getInstance()
            val fmt = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val formattedStartDate = fmt.format(startDate)
            val formattedEndDate = fmt.format(endDate.time)

            val listResult =
                AsteroidApi.retrofitService.getAsteroids(AsteroidApiFilter.SHOW_ALL.value ,Constants.API_KEY,
                    formattedStartDate, formattedEndDate)

            val asteroids = parseAsteroidsJsonResult(JSONObject( listResult.body()!!))

            val list = AsteroidApi.retrofitService.getContainer(asteroids).await()

            database.asteroidDao.insertAll(*list.asDatabaseModel())
        }
    }

}