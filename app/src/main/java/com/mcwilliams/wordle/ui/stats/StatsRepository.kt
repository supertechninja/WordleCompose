package com.mcwilliams.wordle.ui.stats

import android.content.Context
import android.content.SharedPreferences
import com.mcwilliams.wordle.models.GameStatistics
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatsRepository @Inject constructor(private val context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "appPreferences",
        Context.MODE_PRIVATE
    )

    fun retrieveStats(): GameStatistics {
        return GameStatistics(
            gamesPlayed = preferences.getInt("gamesPlayed", 0),
            wins = preferences.getInt("gamesWon", 0),
            currentStreak = preferences.getInt("currentStreak", 0),
            maxStreak = preferences.getInt("maxStreak", 0),
            guessDistribution = listOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 5 to 0, 6 to 0),
            nextGame = LocalDateTime.now()
        )
    }

    fun saveGameStats(guessWordCount: Int, isGuessCorrect: Boolean) {
        preferences.edit().putInt("gamesPlayed", preferences.getInt("gamesPlayed", 0).inc()).apply()

        if(isGuessCorrect){
            preferences.edit().putInt("gamesWon", preferences.getInt("gamesWon", 0).inc()).apply()
            preferences.edit().putInt("currentStreak", preferences.getInt("currentStreak", 0).inc()).apply()
        }

        if(preferences.getInt("maxStreak", 0) < preferences.getInt("currentStreak", 0)) {
            preferences.edit().putInt("maxStreak", preferences.getInt("currentStreak", 0)).apply()
        }
    }
}