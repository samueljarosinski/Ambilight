package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import android.content.Intent
import io.github.samueljarosinski.ambilight.permission.ScreenCapturePermissionRequester
import timber.log.Timber

object AmbilightManager {

    internal fun start(context: Context) {
        Timber.d("Starting Ambilight.")

        ScreenCapturePermissionRequester.requestScreenCapturePermission(context) {
            context.startService(AmbilightService.createStartIntent(context))
        }
    }

    internal fun stop(context: Context) {
        Timber.d("Stopping Ambilight.")

        context.stopService(Intent(context, AmbilightService::class.java))
    }
}
