package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Query("select * from databaseasteroid where closeApproachDate = :date order by closeApproachDate desc")
    fun getTodaysAsteroids(date: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid where closeApproachDate between :sDate and :eDate")
    fun getWeeksAsteroids(sDate: String, eDate: String): LiveData<List<DatabaseAsteroid>>

    @Query("select * from databaseasteroid order by closeApproachDate desc")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidDatabase: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
                AsteroidDatabase::class.java, "asteroids").build()
        }
    }
    return INSTANCE
}