package com.mcwilliams.wordle.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcwilliams.wordle.models.GuessLetter
import com.mcwilliams.wordle.models.KeyState
import com.mcwilliams.wordle.models.keyboardInit
import com.mcwilliams.wordle.models.wordleGrid
import dagger.hilt.android.lifecycle.HiltViewModel
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class WordleViewModel @Inject constructor(
    private val wordleRepository: WordleRepository
) : ViewModel() {

    val _wordleGuesses: MutableLiveData<List<List<GuessLetter>>> = MutableLiveData()
    val wordleGuesses: LiveData<List<List<GuessLetter>>> = _wordleGuesses

    val _keyboard: MutableLiveData<List<List<KeyState>>> = MutableLiveData()
    val keyboard: LiveData<List<List<KeyState>>> = _keyboard

    val currentCharGuess: MutableLiveData<Int> = MutableLiveData(0)
    val currentWordGuess: MutableLiveData<Int> = MutableLiveData(0)

    val wordleWord: MutableLiveData<String> = MutableLiveData()

    val flipRow: MutableLiveData<Int?> = MutableLiveData(null)

    private val _state = MutableLiveData<KonfettiState>(KonfettiState.Idle)
    val konfettiState: LiveData<KonfettiState> = _state

    val shareString: MutableLiveData<String> = MutableLiveData()

    val wordError: MutableLiveData<Boolean> = MutableLiveData()

    val showFirstLaunchInstructions : MutableLiveData<Boolean> = MutableLiveData()

    init {
        _wordleGuesses.postValue(wordleGrid)

        _keyboard.postValue(keyboardInit)

        wordleWord.postValue(wordleRepository.getTodaysWordleWord())

        wordleRepository.isFirstLaunch?.let { firstLaunch ->
            showFirstLaunchInstructions.postValue(firstLaunch)
        }
    }

    fun updateWordleGuesses(guesses: List<List<GuessLetter>>) {
        _wordleGuesses.postValue(guesses)
        if (currentCharGuess.value!! == 4) {
            if(validateGuess(guesses)) {
                updateKeyboard(guesses)

                flipRow.postValue(currentWordGuess.value)
                currentWordGuess.postValue(currentWordGuess.value?.inc())
                currentCharGuess.postValue(0)
            } else {
                val guessCopy = guesses.toMutableList()
                guessCopy[currentWordGuess.value!!] = wordleGrid[0]
                _wordleGuesses.postValue(guessCopy)
                currentCharGuess.postValue(0)
            }
        } else {
            currentCharGuess.postValue(currentCharGuess.value?.inc())
        }
    }

    private fun updateKeyboard(guesses: List<List<GuessLetter>>) {
        val guess = guesses[currentWordGuess.value!!]
        keyboard.value?.let { keyRowsList: List<List<KeyState>> ->
            val keyboardCopy = keyRowsList.toMutableList()
            keyboardCopy.forEach { keyRows ->
                keyRows.forEach { key ->
                    guess.forEach { guessLetter ->
                        if (guessLetter.value.text == key.char) {
                            key.used = true
                            key.isInWord = guessLetter.isInWord
                            key.isInProperPlace = guessLetter.isInProperPlace
                        }
                    }
                }
            }
            _keyboard.postValue(keyboardCopy)
        }
    }

    fun validateGuess(guesses: List<List<GuessLetter>>) : Boolean {
        val wordleGridCopy = guesses.toMutableList()
        val guess = wordleGridCopy[currentWordGuess.value!!].toMutableList()
        var wasGuessValid = false
        //Validate if word existing in accepted list
        if(wordleRepository.validateGuess(guess.map { it.value.text }.joinToString(""))) {
            guess.forEachIndexed { index, guessLetter ->
                guessLetter.isInWord =
                    wordleWord.value!!.contains(guessLetter.value.text, ignoreCase = true)
                guessLetter.isInProperPlace =
                    wordleWord.value!!.toCharArray()[index].toString()
                        .equals(guessLetter.value.text, ignoreCase = true)
            }
            wordleGridCopy[currentWordGuess.value!!] = guess.toList()
            _wordleGuesses.postValue(wordleGridCopy)

            wasGuessValid = true

            //Check if each character in word matches
            if (doAllLettersMatchWord(guess)) {
                _state.postValue(KonfettiState.Started(explode()))
                buildShareString(wordleGridCopy, currentWordGuess.value!!)
            }
        } else {
            wasGuessValid = false
            wordError.postValue(true)
        }
        return wasGuessValid
    }

    fun resetFlip(){
        flipRow.postValue(null)
    }

    private fun buildShareString(wordleGridCopy: MutableList<List<GuessLetter>>, value: Int) {
        var shareStr = "Wordle ${wordleRepository.todayWordleCount} ${value + 1}/6 \n\n"

        wordleGridCopy.forEachIndexed { index, guess ->
            if (index <= value) {
                guess.forEach { letter ->
                    if (letter.isInProperPlace && letter.isInWord) {
                        shareStr += "\uD83D\uDFE9"
                    } else if (letter.isInWord && !letter.isInProperPlace) {
                        shareStr += "\uD83D\uDFE8"
                    } else {
                        shareStr += "⬛"
                    }
                }
                shareStr += "\n"
            }
        }
        shareString.postValue(shareStr)
    }

    private fun doAllLettersMatchWord(guess: MutableList<GuessLetter>): Boolean {
        var guessString = ""
        guess.forEachIndexed { index, guessLetter ->
            guessString += guessLetter.value.text
        }

        return guessString.equals(wordleWord.value, ignoreCase = true)
    }

    fun sendCharDelete(guesses: List<List<GuessLetter>>) {
        _wordleGuesses.postValue(guesses)

        currentCharGuess.postValue(currentCharGuess.value?.dec())
    }

    fun ended() {
        _state.postValue(KonfettiState.Idle)
    }

    fun reset() {
        _wordleGuesses.postValue(wordleGrid)

        keyboard.value?.let { keyRowsList: List<List<KeyState>> ->
            val keyboardCopy = keyRowsList.toMutableList()
            keyboardCopy.forEach { keyRows ->
                keyRows.forEach { key ->
                    key.used = false
                    key.isInWord = false
                    key.isInProperPlace = false
                }
            }
            _keyboard.postValue(keyboardCopy)
        }

        currentCharGuess.postValue(0)
        currentWordGuess.postValue(0)
    }

    fun refreshDate() {
        wordleRepository.refreshDate()
    }

    //Reset the row after invalid input
    fun resetCurrentRow() {
        wordError.postValue(false)
    }

    fun setNotFirstTime() {
        wordleRepository.setFirstLaunch()
        showFirstLaunchInstructions.postValue(false)
    }
}

sealed class KonfettiState {
    class Started(val party: List<Party>) : KonfettiState()
    object Idle : KonfettiState()
}

fun explode(): List<Party> {
    return listOf(
        Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3),
        )
    )
}
