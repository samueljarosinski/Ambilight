package io.github.samueljarosinski.ambilight.ambilight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class AmbilightBroadcastReceiver : BroadcastReceiver() {

    internal fun register(context: Context) = context.registerReceiver(this, STOP_INTENT_FILTER)
    internal fun unregister(context: Context) = context.unregisterReceiver(this)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_STOP_AMBILIGHT_SERVICE) {
            AmbilightManager.stop(context)
        }
    }

    companion object {
        private const val ACTION_STOP_AMBILIGHT_SERVICE = "ACTION_STOP_AMBILIGHT_SERVICE"
        private val STOP_INTENT_FILTER = IntentFilter(ACTION_STOP_AMBILIGHT_SERVICE)
        internal val STOP_INTENT = Intent(ACTION_STOP_AMBILIGHT_SERVICE)
    }
}
