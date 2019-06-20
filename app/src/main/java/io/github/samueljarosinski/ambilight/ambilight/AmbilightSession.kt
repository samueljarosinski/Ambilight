package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import io.github.samueljarosinski.ambilight.hue.HueController
import io.github.samueljarosinski.ambilight.hue.MIN_UPDATE_DELAY
import io.github.samueljarosinski.ambilight.hue.OnHueConnectionListener
import timber.log.Timber

internal class AmbilightSession(
    context: Context,
    private val onSessionCallback: OnSessionCallback
) {

    private val hueController = HueController()
    private val screenImageProvider = ScreenImageProvider(context)
    private val colorExtractor = ColorExtractor(MIN_UPDATE_DELAY, hueController::setColor)

    init {
        hueController.start(object : OnHueConnectionListener {
            override fun onNoBridgesFound() = onSessionCallback.onNoBridgesFound()
            override fun onNotAuthenticated() = onSessionCallback.onNotAuthenticated()
            override fun onConnectionError() = onSessionCallback.onAmbilightSessionError()
            override fun onConnected() = start()
        })
    }

    private fun start() {
        Timber.d("Starting session.")

        screenImageProvider.start(colorExtractor::extract)
        onSessionCallback.onAmbilightSessionStarted()
    }

    fun stop() {
        Timber.d("Stopping session.")

        screenImageProvider.stop()
        hueController.stop()
    }
}

internal interface OnSessionCallback {
    fun onAmbilightSessionStarted()
    fun onAmbilightSessionError()
    fun onNotAuthenticated()
    fun onNoBridgesFound()
}
