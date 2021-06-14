package com.udacity.asteroidradar

import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val API_QUERY_DATE_FORMAT = "YYYY-MM-dd"
    const val DEFAULT_END_DATE_DAYS = 7
    const val BASE_URL = "https://api.nasa.gov/"
    const val API_KEY = "6fk1mdLgLPWyLKZgcfqIfX2bd0Pk67YX6K5zLAZ0"

    private val startDate = Calendar.getInstance().time
    private val endDate = Calendar.getInstance().add(Calendar.DAY_OF_YEAR, DEFAULT_END_DATE_DAYS)
    private val fmt = SimpleDateFormat(API_QUERY_DATE_FORMAT, Locale.getDefault())
    val F_START_DATE = fmt.format(startDate)
    val F_END_DATE = fmt.format(endDate)
}