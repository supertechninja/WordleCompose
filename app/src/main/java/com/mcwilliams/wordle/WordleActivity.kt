package com.mcwilliams.wordle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Divider
import androidx.compose.material.TopAppBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.mcwilliams.wordle.models.GuessLetter
import com.mcwilliams.wordle.ui.theme.WordleTheme
import dagger.hilt.android.AndroidEntryPoint
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.PartySystem


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WordleViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordleTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Wordle",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            },
                            backgroundColor = MaterialTheme.colorScheme.surface
                        )
                    }
                ) {
                    val guessWordCount by viewModel.currentWordGuess.observeAsState(initial = 0)
                    val guessLetterCount by viewModel.currentCharGuess.observeAsState(initial = 0)
                    val wordleWord by viewModel.wordleWord.observeAsState()
                    val wordleGuesses by viewModel.wordleGuesses.observeAsState(initial = emptyList())

                    val keyboard by viewModel.keyboard.observeAsState(initial = emptyList())

                    val konfettiState by viewModel.konfettiState.observeAsState(null)
                    var showSuccessDialog by remember { mutableStateOf(false) }

                    val validationError by viewModel.wordError.observeAsState(false)

                    val shareString by viewModel.shareString.observeAsState()

                    val isFirstLaunch by viewModel.showFirstLaunchInstructions.observeAsState()

                    wordleWord?.let { todaysWord ->
                        Column {
                            BoxWithConstraints(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = MaterialTheme.colorScheme.surface)
                            ) {
                                val maxHeight = this.maxHeight
                                val gridHeight = maxHeight.times(.67f)
                                val keyboardHeight = maxHeight.times(.33f)

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(gridHeight)
                                        .align(Alignment.TopCenter)
                                        .padding(top = 4.dp)
                                ) {
                                    wordleGuesses.forEach { guess ->
                                        BoxWithConstraints(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val spacing = this.maxWidth.times(.08f) / 5
                                            val width = this.maxWidth.times(.90f) / 5
                                            val height = gridHeight.times(.80f) / 5

                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                guess.forEachIndexed { index, guessLetter ->
                                                    Surface(
                                                        modifier = Modifier
                                                            .size(if (height < width) height else width)
                                                            .padding(
                                                                horizontal = spacing,
                                                                vertical = 4.dp
                                                            ),
                                                        color =
                                                        if (guessLetter.isInWord && guessLetter.isInProperPlace)
                                                            Color(93, 134, 81)
                                                        else if (guessLetter.isInWord)
                                                            Color(178, 160, 76)
                                                        else
                                                            MaterialTheme.colorScheme.surface,
                                                        border = BorderStroke(
                                                            1.dp,
                                                            MaterialTheme.colorScheme.onSurface
                                                        )
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            BasicTextField(
                                                                value = guessLetter.value,
                                                                onValueChange = {
                                                                    guessLetter.value = it
                                                                },
                                                                readOnly = true,
                                                                textStyle = TextStyle(
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontSize = 26.sp,
                                                                    textAlign = TextAlign.Center
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(keyboardHeight)
                                        .align(Alignment.BottomCenter)
                                ) {
                                    keyboard.forEach { letters ->
                                        BoxWithConstraints(
                                            Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            val spacing = this.maxWidth.times(.05f) / 10
                                            val width = this.maxWidth.times(.95f) / 10

                                            Row(
                                                horizontalArrangement = Arrangement.Center,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                letters.forEach { letter ->
                                                    val haptic = LocalHapticFeedback.current

                                                    Surface(
                                                        modifier = Modifier
                                                            .width(
                                                                if (letter.char == "ENTER" || letter.char == "BACK") width.times(
                                                                    2
                                                                ) else width
                                                            )
                                                            .height(70.dp)
                                                            .padding(
                                                                horizontal = spacing,
                                                                vertical = 4.dp
                                                            )
                                                            .clickable {
                                                                haptic.performHapticFeedback(
                                                                    HapticFeedbackType.LongPress
                                                                )
                                                                if (letter.char == "BACK") {
                                                                    removePreviousInput(
                                                                        guessWordCount,
                                                                        guessLetterCount,
                                                                        viewModel,
                                                                        wordleGuesses
                                                                    )
                                                                } else {
                                                                    inputChar(
                                                                        guessWordCount,
                                                                        guessLetterCount,
                                                                        letter.char,
                                                                        viewModel,
                                                                        wordleGuesses
                                                                    )
                                                                }
                                                            },
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = if (letter.isInProperPlace && letter.isInProperPlace) {
                                                            Color(93, 134, 81)
                                                        } else if (letter.isInWord) {
                                                            Color(178, 160, 76)
                                                        } else if (letter.used) {
                                                            Color.DarkGray
                                                        } else {
                                                            MaterialTheme.colorScheme.primary
                                                        }
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = letter.char,
                                                                textAlign = TextAlign.Center,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                color =
                                                                if (letter.isInProperPlace
                                                                    || letter.isInProperPlace
                                                                    || letter.used
                                                                ) Color.White
                                                                else MaterialTheme.colorScheme.onPrimary,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                konfettiState?.let {
                                    if (it is KonfettiState.Started) {
                                        KonfettiView(
                                            modifier = Modifier.fillMaxSize(),
                                            parties = (konfettiState as KonfettiState.Started).party,
                                            updateListener = object :
                                                OnParticleSystemUpdateListener {
                                                override fun onParticleSystemEnded(
                                                    system: PartySystem,
                                                    activeSystems: Int
                                                ) {
                                                    if (activeSystems == 0) {
                                                        viewModel.ended()
                                                        showSuccessDialog = true
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }

                            if (showSuccessDialog) {
                                AlertDialog(onDismissRequest = {
                                    viewModel.reset()
                                    showSuccessDialog = false
                                },
                                    dismissButton = {
                                        TextButton(onClick = {
                                            viewModel.reset()
                                            showSuccessDialog = false
                                        }) {
                                            Text(text = "No Thanks")
                                        }
                                    },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            shareString?.let { share ->
                                                val shareIntent = Intent(Intent.ACTION_SEND)
                                                shareIntent.type = "text/plain"
                                                shareIntent.putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    share
                                                )
                                                startActivity(
                                                    Intent.createChooser(
                                                        shareIntent,
                                                        "Share via"
                                                    )
                                                )
                                            }

                                            viewModel.reset()
                                            showSuccessDialog = false
                                        }) {
                                            Text(text = "Share")
                                        }
                                    }, title = {
                                        Text(text = "Congratulations")
                                    }, text = {
                                        Text(text = "Would you like to share? \nCome back for tomorrows word")
                                    })
                            }

                            if (validationError) {
                                AlertDialog(
                                    onDismissRequest = { },
                                    title = {
                                        Text("Invalid Word")
                                    },
                                    text = { Text("Please try again") },
                                    confirmButton = {
                                        TextButton(onClick = { viewModel.resetCurrentRow() }) {
                                            Text(text = "Try Again")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    if (isFirstLaunch == true) {
                        FirstLaunchDialog(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDate()
    }
}

private fun removePreviousInput(
    guessWordCount: Int,
    guessLetterCount: Int,
    viewModel: WordleViewModel,
    wordleGuesses: List<List<GuessLetter>>,
) {
    val guess = GuessLetter(TextFieldValue(""))
    val wordleGridCopy = wordleGuesses.toMutableList()
    val guesses = wordleGridCopy[guessWordCount].toMutableList()
    guesses.removeAt(guessLetterCount - 1)
    guesses.add(guessLetterCount - 1, guess)
    wordleGridCopy[guessWordCount] = guesses.toList()

    viewModel.sendCharDelete(wordleGridCopy.toList())
}

fun inputChar(
    guessWordCount: Int,
    guessLetterCount: Int,
    guessLetter: String,
    viewModel: WordleViewModel,
    wordleGuesses: List<List<GuessLetter>>,
) {
    val guess = GuessLetter(TextFieldValue(guessLetter))
    val wordleGridCopy = wordleGuesses.toMutableList()
    val guesses = wordleGridCopy[guessWordCount].toMutableList()
    guesses.removeAt(guessLetterCount)
    guesses.add(guessLetterCount, guess)
    wordleGridCopy[guessWordCount] = guesses.toList()
    viewModel.updateWordleGuesses(wordleGridCopy.toList())
}

