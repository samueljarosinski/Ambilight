package io.github.samueljarosinski.ambilight.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.core.content.getSystemService
import timber.log.Timber

class ActivityScreenCapturePermission : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        if (!intent.hasExtra(EXTRA_PERMISSION_REQUEST)) {
            Timber.w("Intent is null or has no $EXTRA_PERMISSION_REQUEST, finishing and removing task.")

            finishAndRemoveTask()

            return
        }

        Timber.d("Sending screen capture permission request intent.")

        startActivityForResult(
            getSystemService<MediaProjectionManager>()?.createScreenCaptureIntent(),
            REQUEST_CODE_SCREEN_CAPTURE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCREEN_CAPTURE) {
            Timber.d("Received permission result: $resultCode $data")

            if (resultCode == RESULT_OK && data != null) {
                Timber.d("Screen capture permission was granted!")

                ScreenCapturePermissionRequester.permissionResult = PermissionResult(resultCode, data)
            } else {
                Timber.d("Screen capture permission was denied!")
            }
        }

        finish()
    }

    override fun onPause() {
        super.onPause()

        overridePendingTransition(0, 0)
    }

    companion object {

        private const val REQUEST_CODE_SCREEN_CAPTURE = 7272
        private const val EXTRA_PERMISSION_REQUEST = "EXTRA_PERMISSION_REQUEST"

        internal fun createStartIntent(context: Context): Intent =
            Intent(context, ActivityScreenCapturePermission::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(EXTRA_PERMISSION_REQUEST, true)
    }
}
