package com.mcwilliams.wordle

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import org.threeten.bp.LocalDate
import java.io.BufferedReader
import java.io.InputStreamReader
import java.sql.Date
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.log
import kotlin.math.roundToInt

@Singleton
class WordleRepository @Inject constructor(private val context: Context) {
    var wordleWords: List<String>

    var today = Calendar.getInstance()

    init {
        wordleWords = loadWordleWords()
    }

    fun getTodaysWordleWord(): String {
        //Calculate days from Feb 1st to now (Feb 1, 2022) 227 was the words index in the list on Feb 1
        val startDate = Calendar.getInstance()
        startDate.set(2022, 1, 1)

        val today = Calendar.getInstance(Locale.ROOT)

        return wordleWords[227 + countDaysBetweenTwoCalendar(startDate, today)]
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

    private fun countDaysBetweenTwoCalendar(calendarStart: Calendar, calendarEnd: Calendar): Int {
        val millionSeconds = calendarEnd.timeInMillis - calendarStart.timeInMillis
        val days = TimeUnit.MILLISECONDS.toDays(millionSeconds) //this way not round number
        val daysRounded = (millionSeconds / (1000.0 * 60 * 60 * 24)).roundToInt()
        Log.d("TAG", "countDaysBetweenTwoCalendar: $daysRounded")
        return daysRounded
    }
}