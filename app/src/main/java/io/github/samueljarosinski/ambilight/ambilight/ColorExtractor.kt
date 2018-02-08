package io.github.samueljarosinski.ambilight.ambilight

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.graphics.Palette

typealias ColorExtractedListener = (Int) -> Unit

internal class ColorExtractor(private val onColorExtractedListener: ColorExtractedListener) {

    private var previousColor: Int = Color.WHITE
    private var lastUpdateTime: Long = 0

    fun extract(bitmap: Bitmap) {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastUpdateTime >= MIN_UPDATE_DELAY) {
            val color = Palette.from(bitmap).generate().getDominantColor(previousColor)

            if (color != previousColor) {
                onColorExtractedListener(color)

                previousColor = color
                lastUpdateTime = currentTime
            }
        }

        bitmap.recycle()
    }

    companion object {
        private const val MIN_UPDATE_DELAY = 100
    }

}
