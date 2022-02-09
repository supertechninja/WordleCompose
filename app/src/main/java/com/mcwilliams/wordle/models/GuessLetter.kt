package com.mcwilliams.wordle.models

import androidx.compose.ui.text.input.TextFieldValue

class GuessLetter(
    var value: TextFieldValue = TextFieldValue(""),
    var isInWord: Boolean = false,
    var isInProperPlace: Boolean = false
)

val wordleGrid = listOf(
    listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    ),
    listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    ),
    listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    ), listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    ), listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    ), listOf(
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
        GuessLetter(),
    )
)