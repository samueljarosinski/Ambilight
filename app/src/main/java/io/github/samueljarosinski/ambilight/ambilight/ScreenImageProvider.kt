package io.github.samueljarosinski.ambilight.ambilight

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.PixelFormat
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.ImageReader.OnImageAvailableListener
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.view.Display
import android.view.WindowManager
import io.github.samueljarosinski.ambilight.permission.PermissionResult
import timber.log.Timber

typealias OnScreenImageAvailableListener = (Bitmap) -> Unit

private data class ImageSize(val width: Int, val height: Int) {

    companion object {

        private const val MAX_IMAGE_AREA = 112 * 112

        fun fromDisplay(display: Display): ImageSize {
            val displaySize = Point()
            display.getRealSize(displaySize)
            val (x, y) = displaySize

            val displayArea = x * y
            val scale = Math.sqrt(MAX_IMAGE_AREA / displayArea.toDouble())

            val width = Math.ceil(x * scale).toInt()
            val height = Math.ceil(y * scale).toInt()

            return ImageSize(width, height)
        }
    }
}

internal class ScreenImageProvider(context: Context, permissionResult: PermissionResult) : OnImageAvailableListener {

    private val imageSize = ImageSize.fromDisplay(context.getDefaultDisplay())
    private val handler = Handler()
    private var mediaProjection: MediaProjection?
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var onScreenImageAvailableListener: OnScreenImageAvailableListener? = null

    init {
        val mediaProjectionManager = context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectionManager.getMediaProjection(permissionResult.code, permissionResult.data)
    }

    fun start(onScreenImageAvailableListener: OnScreenImageAvailableListener) {
        Timber.d("Starting capturing images ${imageSize.width}x${imageSize.height}.")

        virtualDisplay = createVirtualDisplay()

        this.onScreenImageAvailableListener = onScreenImageAvailableListener
    }

    fun stop() {
        Timber.d("Stopping capturing images.")

        mediaProjection?.stop()
        mediaProjection = null

        virtualDisplay?.release()
        virtualDisplay = null

        imageReader = null

        onScreenImageAvailableListener = null
    }

    override fun onImageAvailable(imageReader: ImageReader) {
        if (virtualDisplay != null) {
            imageReader.acquireLatestImage()?.let {
                onScreenImageAvailableListener?.invoke(it.toBitmap(imageSize.width, imageSize.height))
            }
        }
    }

    private fun createVirtualDisplay(): VirtualDisplay {
        imageReader = ImageReader.newInstance(imageSize.width, imageSize.height, PixelFormat.RGBA_8888, MAX_IMAGES).apply {
            setOnImageAvailableListener(this@ScreenImageProvider, handler)
        }

        return mediaProjection!!.createVirtualDisplay(
            VIRTUAL_DISPLAY_NAME, imageSize.width, imageSize.height, 1,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader!!.surface, null, null
        )
    }

    companion object {
        private const val VIRTUAL_DISPLAY_NAME = "Ambilight"
        private const val MAX_IMAGES = 2
    }

}

private fun Context.getDefaultDisplay(): Display {
    return (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
}

private fun Image.toBitmap(width: Int, height: Int): Bitmap {
    val plane = planes[0]
    val bitmap = Bitmap.createBitmap(plane.rowStride / plane.pixelStride, height, Config.ARGB_8888)
    bitmap.copyPixelsFromBuffer(plane.buffer)
    close()
    return bitmap.cropped(width, height)
}

private fun Bitmap.cropped(width: Int, height: Int): Bitmap {
    if (this.width == width && this.height == height) {
        return this
    }

    val croppedBitmap: Bitmap = try {
        Bitmap.createBitmap(this, 0, 0, width, height)
    } catch (error: OutOfMemoryError) {
        this
    }

    if (croppedBitmap != this) {
        recycle()
    }

    return croppedBitmap
}

private operator fun Point.component1() = this.x
private operator fun Point.component2() = this.y
