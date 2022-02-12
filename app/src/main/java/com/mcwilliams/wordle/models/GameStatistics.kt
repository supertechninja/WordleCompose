package com.mcwilliams.wordle.models

import java.time.LocalDateTime

data class GameStatistics(
    val gamesPlayed : Int = 0,
    val wins : Int = 0,
    val currentStreak : Int = 0,
    val maxStreak : Int = 0,
    val guessDistribution : List<Pair<Int, Int>>,
    val nextGame: LocalDateTime
)