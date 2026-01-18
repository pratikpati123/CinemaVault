package com.example.cinemavault

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The main Application class for the CinemaVault app.
 * This class is annotated with [@HiltAndroidApp] to enable Hilt for dependency injection.
 */
@HiltAndroidApp
class CinemaVaultApp : Application() {

}
