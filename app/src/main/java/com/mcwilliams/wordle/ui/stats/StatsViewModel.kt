package com.mcwilliams.wordle.ui.stats

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mcwilliams.wordle.models.GameStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(val statsRepository: StatsRepository) : ViewModel() {
    val gameState: MutableLiveData<GameStatistics> = MutableLiveData()

    init {
        gameState.postValue(statsRepository.retrieveStats())
    }

    fun refreshStats(){
        gameState.postValue(statsRepository.retrieveStats())
    }

    fun saveCurrentStats(guessWordCount: Int, isGuessCorrect: Boolean) {
        statsRepository.saveGameStats(guessWordCount, isGuessCorrect)
    }
}