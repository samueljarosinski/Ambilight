package io.github.samueljarosinski.ambilight.hue;

import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import java.util.concurrent.TimeUnit;
import io.github.samueljarosinski.ambilight.hue.bridge.BridgeController;
import io.github.samueljarosinski.ambilight.hue.bridge.BridgeController.OnBridgeConnectedListener;
import io.github.samueljarosinski.ambilight.hue.lights.LightsController;
import timber.log.Timber;

public class HueController {

    public static final String APP_NAME = "Ambilight";
    public static final String DEVICE_NAME = Build.MODEL;
    public static final int HEARTBEAT_INTERVAL = (int) TimeUnit.SECONDS.toMillis(10);
    public static final int MIN_UPDATE_DELAY = 100;

    @NonNull
    private final BridgeController bridgeController;

    @Nullable
    private LightsController lightsController;

    @Nullable
    private OnHueConnectionListener onHueConnectionListener;

    public HueController () {
        bridgeController = new BridgeController();
    }

    public void start (@Nullable OnHueConnectionListener onHueConnectionListener) {
        Timber.d("Starting Hue.");

        this.onHueConnectionListener = onHueConnectionListener;

        bridgeController.connect(new OnBridgeConnectedListener() {

            @Override
            public void onBridgeConnected (@NonNull Bridge bridge) {
                lightsController = new LightsController(bridge);

                if (HueController.this.onHueConnectionListener != null) {
                    HueController.this.onHueConnectionListener.onConnected();
                }
            }

            @Override
            public void onConnectionError () {
                if (HueController.this.onHueConnectionListener != null) {
                    HueController.this.onHueConnectionListener.onConnectionError();
                }
            }
        });
    }

    public void stop () {
        Timber.d("Stopping Hue.");

        onHueConnectionListener = null;
        lightsController = null;
        bridgeController.disconnect();
    }

    public void setColor (@ColorInt int color) {
        if (lightsController != null) {
            lightsController.setColor(color);
        }
    }

    public interface OnHueConnectionListener {
        void onConnected ();
        void onConnectionError ();
    }

}
