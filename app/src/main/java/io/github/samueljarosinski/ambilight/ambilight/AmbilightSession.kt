package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import io.github.samueljarosinski.ambilight.permission.PermissionResult
import timber.log.Timber

internal class AmbilightSession(context: Context, permissionResult: PermissionResult) {

    private val screenImageProvider: ScreenImageProvider = ScreenImageProvider(context, permissionResult)
    private val colorExtractor: ColorExtractor = ColorExtractor { color -> Timber.v("#%06X", 0xFFFFFF and color) }

    init {
        Timber.d("Starting session.")

        screenImageProvider.start(colorExtractor::extract)
    }

    fun stop() {
        Timber.d("Stopping session.")

        screenImageProvider.stop()
    }

}
