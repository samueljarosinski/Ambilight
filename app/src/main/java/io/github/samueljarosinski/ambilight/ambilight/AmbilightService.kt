package io.github.samueljarosinski.ambilight.ambilight

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import io.github.samueljarosinski.ambilight.R
import timber.log.Timber

class AmbilightService : Service(), OnSessionCallback {

    private val receiver = AmbilightBroadcastReceiver()
    private var ambilightSession: AmbilightSession? = null
    private var isRunning: Boolean = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (isRunning) {
            Timber.d("Starting Ambilight requested but is already running, ignoring.")

            return START_REDELIVER_INTENT
        }

        isRunning = true

        Timber.d("Starting ${getString(R.string.label_service_ambilight)}.")

        startForeground(NOTIFICATION_ID, createNotification(R.string.label_hue_connecting))

        receiver.register(context = this)

        ambilightSession = AmbilightSession(context = this, onSessionCallback = this)

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        ambilightSession?.stop()

        receiver.unregister(context = this)

        isRunning = false

        Timber.d("Stopping ${getString(R.string.label_service_ambilight)}.")

        super.onDestroy()
    }

    override fun onAmbilightSessionStarted() {
        getSystemService<NotificationManager>()
            ?.notify(NOTIFICATION_ID, createNotification(R.string.label_ambilight_active))
    }

    override fun onNoBridgesFound() {
        handler.post { Toast.makeText(this, R.string.msg_no_bridges_found, Toast.LENGTH_LONG).show() }
    }

    override fun onNotAuthenticated() {
        handler.post { Toast.makeText(this, R.string.msg_not_authenticated, Toast.LENGTH_LONG).show() }
    }

    override fun onAmbilightSessionError() = stopSelf()

    private fun createNotification(@StringRes titleResId: Int): Notification {
        val stopIntent = AmbilightBroadcastReceiver.STOP_INTENT
        val stopPendingIntent =
            PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        val stopAction = NotificationCompat.Action(
            R.drawable.ic_e27_waca_off, getText(R.string.action_stop), stopPendingIntent
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_e27_waca_on)
            .setContentTitle(getText(titleResId))
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .addAction(stopAction)
            .setShowWhen(false)
            .setLocalOnly(true)
            .build()
    }

    override fun onBind(intent: Intent): IBinder? = null

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID_AMBILIGHT_SERVICE"
        const val NOTIFICATION_ID = 131

        internal fun createStartIntent(context: Context) = Intent(context, AmbilightService::class.java)
    }
}
