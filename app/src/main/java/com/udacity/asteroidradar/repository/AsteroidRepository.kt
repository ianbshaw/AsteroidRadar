package com.udacity.asteroidradar.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.main.AsteroidFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDate

class AsteroidRepository(private val database: AsteroidDatabase) {

    @RequiresApi(Build.VERSION_CODES.O)
    private val startDate = LocalDate.now()
    @RequiresApi(Build.VERSION_CODES.O)
    private val endDate = startDate.plusDays(7)

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val todaysAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getTodaysAsteroids(startDate.toString())) {
        it.asDomainModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val weeksAsteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidDao.getWeeksAsteroids(startDate.toString(), endDate.toString())) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(startDate: String, endDate: String) {
        withContext(Dispatchers.IO) {

            val key = Constants.API_KEY

            val jsonResult = AsteroidApi.retrofitService.getAsteroids(AsteroidFilter.ALL,
                key, startDate, endDate)

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

            try {
                database.asteroidDao.insertAll(*listAsteroidDatabase.toTypedArray())
            } catch (e: Exception) {
                Log.e("TAG", e.message!!)
            }


        }
    }

}