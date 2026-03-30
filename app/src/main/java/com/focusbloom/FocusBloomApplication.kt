package com.focusbloom

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusBloomApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}