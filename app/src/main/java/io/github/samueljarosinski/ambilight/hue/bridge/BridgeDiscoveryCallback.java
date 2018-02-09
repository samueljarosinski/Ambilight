package io.github.samueljarosinski.ambilight.hue.bridge;

import android.support.annotation.NonNull;
import com.philips.lighting.hue.sdk.wrapper.discovery.BridgeDiscoveryResult;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.uicallback.BridgeDiscoveryNonUICallback;
import java.util.List;
import timber.log.Timber;

class BridgeDiscoveryCallback extends BridgeDiscoveryNonUICallback {

    @NonNull
    private final OnBridgeDiscoveredListener onBridgeDiscoveredListener;

    BridgeDiscoveryCallback (@NonNull OnBridgeDiscoveredListener onBridgeDiscoveredListener) {
        this.onBridgeDiscoveredListener = onBridgeDiscoveredListener;
    }

    @Override
    public void onFinished (@NonNull List<BridgeDiscoveryResult> results, @NonNull ReturnCode returnCode) {
        //noinspection EnumSwitchStatementWhichMissesCases
        switch (returnCode) {
            case SUCCESS:
                Timber.d("Found %d bridge(s) in the network.", results.size());

                BridgeDiscoveryResult discoveryResult = results.get(0);
                onBridgeDiscoveredListener.onBridgeDiscovered(discoveryResult.getIP(), discoveryResult.getUniqueID());

                break;

            case STOPPED:
                Timber.d("Bridge discovery stopped.");

                break;

            default:
                Timber.e("Error doing bridge discovery: %s", returnCode);
        }
    }

    interface OnBridgeDiscoveredListener {
        void onBridgeDiscovered (@NonNull String ipAddress, @NonNull String uniqueId);
    }

}
