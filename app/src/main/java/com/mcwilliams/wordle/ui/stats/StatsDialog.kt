package com.mcwilliams.wordle.ui.stats

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun StatsDialog(statsViewModel: StatsViewModel, shareString: String, onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val statistics by statsViewModel.gameState.observeAsState()

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                statistics?.let { stats ->
                    Text("STATISTICS")

                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterHorizontally
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stats.gamesPlayed.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Played",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "${((stats.wins / stats.gamesPlayed) * 100)}",
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Win %",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stats.currentStreak.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Current \nStreak",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stats.maxStreak.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                "Max \nStreak",
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text("GUESS DISTRIBUTION")

                    //TODO Add Guess Distribution
                }


                FilledTonalButton(onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "text/plain"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        shareString
                    )
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            "Share via"
                        )
                    )

                    onDismissRequest()
                }) {
                    Text(text = "Share")
                }
            }
        }
    }
}