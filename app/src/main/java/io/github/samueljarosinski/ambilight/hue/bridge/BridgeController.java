package io.github.samueljarosinski.ambilight.hue.bridge;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscovery.BridgeDiscoveryOption;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.BridgeBuilder;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridge;
import com.philips.lighting.hue.sdk.wrapper.knownbridges.KnownBridges;
import java.util.Collections;
import java.util.List;
import io.github.samueljarosinski.ambilight.hue.HueController;
import timber.log.Timber;

public class BridgeController {

    @Nullable
    private Bridge bridge;

    @Nullable
    private BridgeDiscovery bridgeDiscovery;

    @Nullable
    private OnBridgeConnectedListener onBridgeConnectedListener;

    public void connect (@NonNull OnBridgeConnectedListener onBridgeConnectedListener) {
        this.onBridgeConnectedListener = onBridgeConnectedListener;

        KnownBridge knownBridge = getLastConnectedBridge();

        if (knownBridge == null || TextUtils.isEmpty(knownBridge.getIpAddress())) {
            startBridgeDiscovery();
        } else {
            connectToBridge(knownBridge.getIpAddress(), knownBridge.getUniqueId());
        }
    }

    public void disconnect () {
        stopBridgeDiscovery();
        disconnectFromBridge();

        onBridgeConnectedListener = null;
    }

    @Nullable
    private KnownBridge getLastConnectedBridge () {
        List<KnownBridge> knownBridges = KnownBridges.getAll();
        Timber.v("Found %s known bridge(s).", knownBridges.size());

        if (knownBridges.isEmpty()) {
            return null;
        }

        return Collections.max(knownBridges, (a, b) -> a.getLastConnected().compareTo(b.getLastConnected()));
    }

    private void startBridgeDiscovery () {
        disconnectFromBridge();

        Timber.d("Starting bridge discovery.");

        bridgeDiscovery = new BridgeDiscovery();
        bridgeDiscovery.search(BridgeDiscoveryOption.ALL, new BridgeDiscoveryCallback(this::connectToBridge));
    }

    private void stopBridgeDiscovery () {
        if (bridgeDiscovery != null) {
            Timber.d("Stopping bridge discovery.");

            bridgeDiscovery.stop();
            bridgeDiscovery = null;
        }
    }

    private void connectToBridge (@NonNull String ipAddress, @NonNull String bridgeId) {
        stopBridgeDiscovery();
        disconnectFromBridge();

        if (onBridgeConnectedListener == null) {
            return;
        }

        bridge = new BridgeBuilder(HueController.APP_NAME, HueController.DEVICE_NAME)
                .setConnectionType(BridgeConnectionType.LOCAL)
                .setIpAddress(ipAddress)
                .setBridgeId(bridgeId)
                .addBridgeStateUpdatedCallback(new BridgeStateUpdatedCallback(onBridgeConnectedListener))
                .setBridgeConnectionCallback(new BridgeConnectionCallback(onBridgeConnectedListener))
                .build();

        if (bridge != null) {
            Timber.d("Connecting to bridge with IP %s.", ipAddress);

            bridge.connect();
        }
    }

    private void disconnectFromBridge () {
        if (bridge != null) {
            Timber.d("Disconnecting from bridge.");

            bridge.disconnect();
            bridge = null;
        }
    }

    public interface OnBridgeConnectedListener {
        void onBridgeConnected (@NonNull Bridge bridge);
        void onConnectionError ();
    }

}
