package com.mcwilliams.wordle

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WordleApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}