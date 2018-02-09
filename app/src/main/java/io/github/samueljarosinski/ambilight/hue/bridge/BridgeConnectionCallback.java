package io.github.samueljarosinski.ambilight.hue.bridge;

import android.support.annotation.NonNull;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnection;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateCacheType;
import com.philips.lighting.hue.sdk.wrapper.connection.ConnectionEvent;
import com.philips.lighting.hue.sdk.wrapper.connection.HeartbeatManager;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.uicallback.BridgeConnectionNonUICallback;
import java.util.List;
import io.github.samueljarosinski.ambilight.hue.HueController;
import io.github.samueljarosinski.ambilight.hue.bridge.BridgeController.OnBridgeConnectedListener;
import timber.log.Timber;

class BridgeConnectionCallback extends BridgeConnectionNonUICallback {

    @NonNull
    private final OnBridgeConnectedListener onBridgeConnectedListener;

    BridgeConnectionCallback (@NonNull OnBridgeConnectedListener onBridgeConnectedListener) {
        this.onBridgeConnectedListener = onBridgeConnectedListener;
    }

    @Override
    public void onConnectionEvent (@NonNull BridgeConnection bridgeConnection, @NonNull ConnectionEvent connectionEvent) {
        Timber.v("Bridge connection even: %s", connectionEvent);

        if (connectionEvent == ConnectionEvent.AUTHENTICATED) {
            Timber.d("Starting heartbeat.");

            HeartbeatManager heartbeatManager = bridgeConnection.getHeartbeatManager();
            heartbeatManager.startHeartbeat(BridgeStateCacheType.LIGHTS_AND_GROUPS, HueController.HEARTBEAT_INTERVAL);
        }
    }

    @Override
    public void onConnectionError (@NonNull BridgeConnection bridgeConnection, @NonNull List<HueError> errors) {
        for (HueError error : errors) {
            Timber.e("Connection error: %s", error.toString());
        }

        onBridgeConnectedListener.onConnectionError();
    }

}
