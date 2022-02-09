package com.mcwilliams.wordle

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

    private val _state = MutableLiveData<KonfettiState>(KonfettiState.Idle)
    val konfettiState: LiveData<KonfettiState> = _state

    init {
        _wordleGuesses.postValue(wordleGrid)

        _keyboard.postValue(keyboardInit)

        wordleWord.postValue(wordleRepository.getTodaysWordleWord())
    }

    fun updateWordleGuesses(guesses: List<List<GuessLetter>>) {
        _wordleGuesses.postValue(guesses)
        if (currentCharGuess.value!! == 4) {
            validateGuess(guesses)
            updateKeyboard(guesses)

            currentWordGuess.postValue(currentWordGuess.value?.inc())
            currentCharGuess.postValue(0)
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

    fun validateGuess(guesses: List<List<GuessLetter>>) {
        val wordleGridCopy = guesses.toMutableList()
        val guess = wordleGridCopy[currentWordGuess.value!!].toMutableList()


        guess.forEachIndexed { index, guessLetter ->
            guessLetter.isInWord =
                wordleWord.value!!.contains(guessLetter.value.text, ignoreCase = true)
            guessLetter.isInProperPlace =
                wordleWord.value!!.toCharArray()[index].toString()
                    .equals(guessLetter.value.text, ignoreCase = true)
        }
        wordleGridCopy[currentWordGuess.value!!] = guess.toList()
        _wordleGuesses.postValue(wordleGridCopy)

        //Check if each character word matches
        if (doAllLettersMatchWord(guess)) {
            _state.postValue(KonfettiState.Started(explode()))
        }
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
