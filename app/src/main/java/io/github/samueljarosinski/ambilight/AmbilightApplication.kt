package io.github.samueljarosinski.ambilight

import android.annotation.TargetApi
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.philips.lighting.hue.sdk.wrapper.HueLog
import com.philips.lighting.hue.sdk.wrapper.Persistence
import io.github.samueljarosinski.ambilight.ambilight.AmbilightService
import timber.log.Timber
import timber.log.Timber.DebugTree

class AmbilightApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }

        Persistence.setStorageLocation(filesDir.absolutePath, Build.MODEL)
        HueLog.setConsoleLogLevel(HueLog.LogLevel.INFO, HueLog.LogComponent.ALL)

        createNotificationChannels()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(NotificationChannel(
                AmbilightService.NOTIFICATION_CHANNEL_ID,
                getString(R.string.label_service_ambilight),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                lockscreenVisibility = Notification.VISIBILITY_SECRET
                setShowBadge(false)
            })
        }
    }

    companion object {
        init {
            System.loadLibrary("huesdk")
        }
    }

}
