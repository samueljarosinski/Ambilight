package io.github.samueljarosinski.ambilight.ambilight

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import io.github.samueljarosinski.ambilight.R
import io.github.samueljarosinski.ambilight.permission.PermissionResult
import timber.log.Timber

class AmbilightService : Service() {

    private val receiver = AmbilightBroadcastReceiver()
    private lateinit var ambilightSession: AmbilightSession
    private var isRunning: Boolean = false

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isRunning) {
            Timber.d("Starting Ambilight requested but is already running, ignoring.")

            return START_REDELIVER_INTENT
        }

        val permissionResult = intent.getSerializableExtra(EXTRA_PERMISSION_RESULT) as PermissionResult
        permissionResult.data ?: throw IllegalStateException("Permission result data is missing.")

        isRunning = true

        Timber.d("Starting %s.", getString(R.string.label_service_ambilight))

        startForeground(NOTIFICATION_ID, createNotification())

        receiver.register(context = this)

        ambilightSession = AmbilightSession(this, permissionResult)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        ambilightSession.stop()

        receiver.unregister(context = this)

        isRunning = false

        Timber.d("Stopping ${getString(R.string.label_service_ambilight)}.")

        super.onDestroy()
    }

    private fun createNotification(): Notification {
        val stopIntent = AmbilightBroadcastReceiver.STOP_INTENT
        val stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val stopAction = NotificationCompat.Action(R.drawable.ic_e27_waca_off, getText(R.string.action_stop), stopPendingIntent)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_e27_waca_on)
            .setContentTitle(getText(R.string.label_ambilight_active))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .addAction(stopAction)
            .setShowWhen(false)
            .setLocalOnly(true)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {

        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_AMBILIGHT_SERVICE"
        const val NOTIFICATION_ID = 131

        private const val EXTRA_PERMISSION_RESULT = "EXTRA_PERMISSION_RESULT"

        internal fun createStartIntent(context: Context, permissionResult: PermissionResult): Intent {
            return Intent(context, AmbilightService::class.java).putExtra(EXTRA_PERMISSION_RESULT, permissionResult)
        }

    }

}
