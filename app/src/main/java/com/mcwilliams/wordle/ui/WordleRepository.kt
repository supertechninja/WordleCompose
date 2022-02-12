package com.mcwilliams.wordle.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.mcwilliams.wordle.R
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class WordleRepository @Inject constructor(private val context: Context) {
    var wordleWords: List<String>

    var today = Calendar.getInstance()

    var todayWordleCount = 0

    var isFirstLaunch: Boolean? = null

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "appPreferences",
        Context.MODE_PRIVATE
    )

    init {
        wordleWords = loadWordleWords()

        if (preferences.getBoolean("isFirstLaunch", true)) {
            isFirstLaunch = true
            preferences.edit().putBoolean("isFirstLaunch", false).apply()
        } else {
            isFirstLaunch = false
        }

    }

    fun getTodaysWordleWord(): String {
        //Calculate days from Feb 1st to now (Feb 1, 2022) 227 was the words index in the list on Feb 1
        val startDate = Calendar.getInstance()
        startDate.set(2022, 1, 1)

        val today = Calendar.getInstance(Locale.ROOT)

        todayWordleCount = 227 + countDaysBetweenTwoCalendar(startDate, today)

        return wordleWords[todayWordleCount]
    }

    private fun loadWordleWords(): List<String> {
        val reader =
            BufferedReader(InputStreamReader(context.resources.openRawResource(R.raw.wordle_words)))
        val listOfWords = mutableListOf<String>()
        var mLine = reader.readLine()
        while (mLine != null) {
            listOfWords.add(mLine) // process line
            mLine = reader.readLine()
        }
        reader.close()
        return listOfWords
    }

    fun refreshDate() {
        today = Calendar.getInstance()
    }

    private fun countDaysBetweenTwoCalendar(
        calendarStart: Calendar,
        calendarEnd: Calendar
    ): Int {
        val millionSeconds = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(millionSeconds) //this way not round number
        val daysRounded = (millionSeconds / (1000.0 * 60 * 60 * 24)).roundToInt()
        Log.d("TAG", "countDaysBetweenTwoCalendar: $daysRounded")
        return daysRounded
    }

    fun validateGuess(guessString: String): Boolean {
        Log.d("TAG", "validateGuess: $guessString")
        return wordleWords.contains(guessString.lowercase(Locale.ROOT))
    }

    fun setFirstLaunch() {
        preferences.edit().putBoolean("isFirstLaunch", false).apply()
    }
}