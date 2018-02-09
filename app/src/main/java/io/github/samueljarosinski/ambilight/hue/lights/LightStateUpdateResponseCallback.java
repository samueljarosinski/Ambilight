package io.github.samueljarosinski.ambilight.hue.lights;

import android.support.annotation.NonNull;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeResponseCallback;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.HueError;
import com.philips.lighting.hue.sdk.wrapper.domain.ReturnCode;
import com.philips.lighting.hue.sdk.wrapper.domain.clip.ClipResponse;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import java.util.List;
import timber.log.Timber;

class LightStateUpdateResponseCallback extends BridgeResponseCallback {

    @NonNull
    private final LightPoint lightPoint;

    @NonNull
    private final LightState lightState;

    LightStateUpdateResponseCallback (@NonNull LightPoint lightPoint, @NonNull LightState lightState) {
        this.lightPoint = lightPoint;
        this.lightState = lightState;
    }

    @Override
    public void handleCallback (
            @NonNull Bridge bridge, @NonNull ReturnCode returnCode, @NonNull List<ClipResponse> clipResponses,
            @NonNull List<HueError> errorList) {

        if (returnCode == ReturnCode.CANCELED) {
            Timber.w("Changing light cancelled for %s, retrying.", lightPoint.getIdentifier());

            lightPoint.updateState(lightState, BridgeConnectionType.LOCAL, this);
        } else if (returnCode != ReturnCode.SUCCESS) {
            Timber.e("Error changing light %s: %s.", lightPoint.getIdentifier(), returnCode);

            for (HueError error : errorList) {
                Timber.e(error.toString());
            }
        }
    }

}
