package io.github.samueljarosinski.ambilight.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import timber.log.Timber
import java.io.Serializable

data class PermissionResult(val code: Int, val data: Intent?) : Serializable

typealias PermissionResultListener = (PermissionResult) -> Unit

object ScreenCapturePermissionRequester {

    private var permissionResultListener: PermissionResultListener? = null

    var permissionResult: PermissionResult? = null
        internal set(value) {
            when (value?.code) {
                Activity.RESULT_OK -> {
                    notifyResultListener()
                }

                Activity.RESULT_CANCELED -> {
                    field = null

                    Timber.w("Screen capture permission was denied.")
                }

                else -> {
                    field = null

                    Timber.w("Unhandled result code ${permissionResult?.code}.")
                }
            }
        }

    fun requestScreenCapturePermission(context: Context, resultListener: PermissionResultListener) {
        Timber.d("Requesting screen capture permission.")

        permissionResultListener = resultListener

        if (permissionResult != null) {
            Timber.d("Permission already granted.")

            notifyResultListener()
        } else {
            openScreenshotPermissionRequester(context)
        }
    }

    private fun notifyResultListener() {
        Timber.d("Notifying about permission result.")

        permissionResultListener?.invoke(permissionResult!!)
        permissionResultListener = null
    }

    private fun openScreenshotPermissionRequester(context: Context) {
        context.startActivity(ActivityScreenCapturePermission.createStartIntent(context))
    }

}
