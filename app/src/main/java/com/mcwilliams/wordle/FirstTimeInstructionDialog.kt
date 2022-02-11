package com.mcwilliams.wordle

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
                Text("Guess the word in six tries.")

                Text(
                    "Each quess must be a valid five-letter word. Hit the " +
                            "enter button to submit."
                )

                Text(
                    text = "After each guess, the color of the tiles will change to " +
                            "show how close your quess was to the word."
                )

                Divider(modifier = Modifier.height(0.5.dp))

                Text("Examples")

                val exampleWord1 = listOf("W", "E", "A", "R", "Y")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    exampleWord1.forEachIndexed { index, s ->
                        if (index == 0) {
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                color = Color(93, 134, 81),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        s,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        s,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }

                Text("The letter W is in the word and in the correct spot.")

                val exampleWord2 = listOf("P", "I", "L", "L", "S")
                Row() {
                    exampleWord2.forEachIndexed { index, s ->

                        if (index == 1) {
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                color = Color(178, 160, 76),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        s,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        s,
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }
                        }
                    }
                }

                Text("The letter I is in the word but in the wrong spot.")

                val exampleWord3 = listOf("V", "A", "G", "U", "E")

                Row() {
                    exampleWord3.forEachIndexed { index, s ->
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(4.dp),
                            color = Color(58, 58, 60),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    s,
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Text("The letter U is not in the word in any spot.")

                Divider(modifier = Modifier.height(0.5.dp))

                Text("A new WORDLE will be available each day!")
            }
        }
    }
}