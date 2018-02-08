package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import android.content.Intent
import io.github.samueljarosinski.ambilight.permission.ScreenCapturePermissionRequester
import timber.log.Timber

class AmbilightManager {

    internal fun start(context: Context) {
        Timber.d("Starting Ambilight.")

        ScreenCapturePermissionRequester.requestScreenCapturePermission(context) { permissionResult ->
            context.startService(AmbilightService.createStartIntent(context, permissionResult))
        }
    }

    internal fun stop(context: Context) {
        Timber.d("Stopping Ambilight.")

        context.stopService(Intent(context, AmbilightService::class.java))
    }

}
