package com.mcwilliams.wordle.models

class KeyState(
    var char: String,
    var used: Boolean = false,
    var isInWord: Boolean = false,
    var isInProperPlace: Boolean = false
)

val keyboardInit = listOf(
    listOf(
        KeyState("Q"),
        KeyState("W"),
        KeyState("E"),
        KeyState("R"),
        KeyState("T"),
        KeyState("Y"),
        KeyState("U"),
        KeyState("I"),
        KeyState("O"),
        KeyState("P")
    ),
    listOf(
        KeyState("A"),
        KeyState("S"),
        KeyState("D"),
        KeyState("F"),
        KeyState("G"),
        KeyState("H"),
        KeyState("J"),
        KeyState("K"),
        KeyState("L")
    ),
    listOf(
        KeyState("ENTER"),
        KeyState("Z"),
        KeyState("X"),
        KeyState("C"),
        KeyState("V"),
        KeyState("B"),
        KeyState("N"),
        KeyState("M"),
        KeyState("BACK")
    )
)