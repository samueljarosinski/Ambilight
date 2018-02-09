package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import io.github.samueljarosinski.ambilight.hue.HueController
import io.github.samueljarosinski.ambilight.permission.PermissionResult
import timber.log.Timber

internal class AmbilightSession(
    context: Context,
    permissionResult: PermissionResult,
    private val onSessionCallback: OnSessionCallback
) {

    private val hueController = HueController()
    private val screenImageProvider: ScreenImageProvider = ScreenImageProvider(context, permissionResult)
    private val colorExtractor: ColorExtractor = ColorExtractor(HueController.MIN_UPDATE_DELAY, hueController::setColor)

    init {
        hueController.start(object : HueController.OnHueConnectionListener {
            override fun onConnected() {
                start()
            }

            override fun onConnectionError() {
                onSessionCallback.onAmbilightSessionError()
            }

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
}
