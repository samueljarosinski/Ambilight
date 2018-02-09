package io.github.samueljarosinski.ambilight.hue.lights;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.philips.lighting.hue.sdk.wrapper.connection.BridgeConnectionType;
import com.philips.lighting.hue.sdk.wrapper.domain.Bridge;
import com.philips.lighting.hue.sdk.wrapper.domain.device.DeviceConfiguration;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightPoint;
import com.philips.lighting.hue.sdk.wrapper.domain.device.light.LightState;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor;
import com.philips.lighting.hue.sdk.wrapper.utilities.HueColor.RGB;

public class LightsController {

    private static final int TRANSITION_TIME = 1;

    @NonNull
    private final Bridge bridge;

    @NonNull
    private final LightState lightState;

    public LightsController (@NonNull Bridge bridge) {
        this.bridge = bridge;
        lightState = new LightState().setTransitionTime(TRANSITION_TIME);
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    public void setColor (@ColorInt int color) {
        RGB rgb = new RGB(Color.red(color), Color.green(color), Color.blue(color));

        for (LightPoint light : bridge.getBridgeState().getLights()) {
            if (!light.getLightState().isOn()) {
                continue;
            }

            DeviceConfiguration configuration = light.getConfiguration();
            HueColor hueColor = new HueColor(rgb, configuration.getModelIdentifier(), configuration.getSwVersion());
            lightState.setXYWithColor(hueColor);

            light.updateState(lightState, BridgeConnectionType.LOCAL, new LightStateUpdateResponseCallback(light, lightState));
        }
    }

}
