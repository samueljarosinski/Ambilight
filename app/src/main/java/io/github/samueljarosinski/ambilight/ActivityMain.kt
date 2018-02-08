package io.github.samueljarosinski.ambilight

import android.app.Activity
import android.os.Bundle
import io.github.samueljarosinski.ambilight.ambilight.AmbilightManager

class ActivityMain : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        overridePendingTransition(0, 0)

        AmbilightManager().start(this)

        finish()
    }

    override fun onPause() {
        super.onPause()

        overridePendingTransition(0, 0)
    }

}
