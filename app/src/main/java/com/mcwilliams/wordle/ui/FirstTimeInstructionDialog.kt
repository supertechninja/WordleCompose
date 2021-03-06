package com.mcwilliams.wordle.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun FirstLaunchDialog(viewModel: WordleViewModel) {
    Dialog(onDismissRequest = {
        viewModel.setNotFirstTime()
    }) {
        Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text ="Guess the word in six tries.")

                Text(text = "Each guess must be a valid five-letter word. Words are auto-submitted at 5 letters.")

                Text(text = "After each guess, the color of the tiles will change to " +
                            "show how close your guess was to the word.")

                Divider(modifier = Modifier.height(0.5.dp), color = MaterialTheme.colorScheme.onSurface)

                Text(text = "Examples")

                examplesList.forEachIndexed { examplesIndex, examples ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        examples.first.forEachIndexed { letterIndex, char ->
                            var colorSquare = MaterialTheme.colorScheme.surface
                            if (examplesIndex == 0 && letterIndex == 0) {
                                colorSquare = Color(93, 134, 81)
                            } else if(examplesIndex == 1 && letterIndex == 1){
                                colorSquare = Color(178, 160, 76)
                            } else if(examplesIndex == 2){
                                colorSquare = Color(58, 58, 60)
                            }

                            LetterSquare(colorSquare, char)
                        }
                    }

                    Text(text = examples.second)
                }

                Divider(modifier = Modifier.height(0.5.dp), color = MaterialTheme.colorScheme.onSurface)

                Text(text = "A new WORDLE will be available each day!")
            }
        }
    }
}

@Composable
fun LetterSquare(color: Color, letter: String) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .padding(4.dp),
        color = color,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = letter,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

val examplesList = listOf(
    listOf("W", "E", "A", "R", "Y") to "The letter W is in the word and in the correct spot.",
    listOf("P", "I", "L", "L", "S") to "The letter I is in the word but in the wrong spot.",
    listOf("V", "A", "G", "U", "E") to "The letter U is not in the word in any spot."
)