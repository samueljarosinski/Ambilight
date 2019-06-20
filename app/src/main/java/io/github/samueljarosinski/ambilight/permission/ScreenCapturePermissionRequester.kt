package io.github.samueljarosinski.ambilight.permission

import android.content.Context
import android.content.Intent
import timber.log.Timber

data class PermissionResult(val code: Int, val data: Intent)

typealias OnPermissionGrantedListener = () -> Unit

object ScreenCapturePermissionRequester {

    private var onPermissionGrantedListener: OnPermissionGrantedListener? = null

    var permissionResult: PermissionResult? = null
        internal set(value) {
            field = value

            if (value != null) {
                notifyResultListener()
            }
        }

    fun requestScreenCapturePermission(context: Context, resultListener: OnPermissionGrantedListener) {
        Timber.d("Requesting screen capture permission.")

        onPermissionGrantedListener = resultListener

        if (permissionResult != null) {
            Timber.d("Permission already granted.")

            notifyResultListener()
        } else {
            context.startActivity(ActivityScreenCapturePermission.createStartIntent(context))
        }
    }

    private fun notifyResultListener() {
        Timber.d("Notifying about permission result.")

        onPermissionGrantedListener?.invoke()
        onPermissionGrantedListener = null
    }
}
