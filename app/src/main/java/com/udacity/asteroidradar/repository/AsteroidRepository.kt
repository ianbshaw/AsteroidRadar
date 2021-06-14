package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.AsteroidHelper
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {

            val jsonResult = AsteroidApi.retrofitService.getAsteroids(AsteroidApiFilter.SHOW_ALL.value,
                Constants.API_KEY, startDate, endDate)

            val asteroids = parseAsteroidsJsonResult(JSONObject(jsonResult))
            val listAsteroidDatabase = mutableListOf<DatabaseAsteroid>()

            for (asteroid in asteroids) {
                val databaseAsteroid = DatabaseAsteroid(asteroid.id,
                    asteroid.codename,asteroid.closeApproachDate,asteroid.absoluteMagnitude,
                    asteroid.estimatedDiameter, asteroid.relativeVelocity, asteroid.distanceFromEarth,
                    asteroid.isPotentiallyHazardous
                )
                listAsteroidDatabase.add(databaseAsteroid)
            }

            database.asteroidDao.insertAll(*listAsteroidDatabase.toTypedArray())

            /*try {
                val list = NetworkAsteroidContainer(asteroids)

                database.asteroidDao.insertAll(*list.asDatabaseModel())
            }catch (e: Exception){
                println(e.message)
            }*/

        }
    }

}