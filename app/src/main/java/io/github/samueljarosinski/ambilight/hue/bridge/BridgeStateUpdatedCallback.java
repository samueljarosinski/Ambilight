package io.github.samueljarosinski.ambilight.hue.bridge;

import android.support.annotation.NonNull;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeStateUpdatedEvent;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.uicallback.BridgeStateUpdatedNonUICallback;
import io.github.samueljarosinski.ambilight.hue.bridge.BridgeController.OnBridgeConnectedListener;
import timber.log.Timber;

class BridgeStateUpdatedCallback extends BridgeStateUpdatedNonUICallback {

    @NonNull
    private final OnBridgeConnectedListener onBridgeConnectedListener;

    BridgeStateUpdatedCallback (@NonNull OnBridgeConnectedListener onBridgeConnectedListener) {
        super(null);
        this.onBridgeConnectedListener = onBridgeConnectedListener;
    }

    @Override
    public void onBridgeStateUpdated (@NonNull Bridge bridge, @NonNull BridgeStateUpdatedEvent bridgeStateUpdatedEvent) {
        if (bridgeStateUpdatedEvent == BridgeStateUpdatedEvent.INITIALIZED) {
            Timber.d("Connected to bridge %s.", bridge.getName());

            onBridgeConnectedListener.onBridgeConnected(bridge);
        }
    }

}
