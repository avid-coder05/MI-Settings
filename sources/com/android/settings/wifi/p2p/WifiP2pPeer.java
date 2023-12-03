package com.android.settings.wifi.p2p;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class WifiP2pPeer extends RadioButtonPreference {
    private static final int[] STATE_SECURED = {R.attr.state_encrypted};
    public WifiP2pDevice device;
    final int mRssi;
    private ImageView mSignal;

    public WifiP2pPeer(Context context, WifiP2pDevice wifiP2pDevice) {
        super(context);
        this.device = wifiP2pDevice;
        setLayoutResource(R.layout.wifi_direct_accesspoint_preference);
        setWidgetLayoutResource(R.layout.preference_widget_wifi_signal);
        this.mRssi = 60;
        if (TextUtils.isEmpty(this.device.deviceName)) {
            setTitle(this.device.deviceAddress);
        } else {
            setTitle(this.device.deviceName);
        }
        setSummary(context.getResources().getStringArray(R.array.wifi_p2p_status)[this.device.status]);
        setIcon(context.getDrawable(R.drawable.wifi_signal));
    }

    private void updateSignalLevel() {
        Drawable icon = getIcon();
        int level = getLevel();
        if (icon instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) icon;
            int numberOfLayers = layerDrawable.getNumberOfLayers() - 1;
            for (int i = numberOfLayers; i >= 0; i--) {
                layerDrawable.findDrawableByLayerId(layerDrawable.getId(i)).setAlpha(255);
            }
            while (numberOfLayers >= level) {
                layerDrawable.findDrawableByLayerId(layerDrawable.getId(numberOfLayers)).setAlpha(63);
                numberOfLayers--;
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(Preference preference) {
        if (preference instanceof WifiP2pPeer) {
            WifiP2pDevice wifiP2pDevice = this.device;
            int i = wifiP2pDevice.status;
            WifiP2pDevice wifiP2pDevice2 = ((WifiP2pPeer) preference).device;
            int i2 = wifiP2pDevice2.status;
            if (i != i2) {
                return i < i2 ? -1 : 1;
            }
            String str = wifiP2pDevice.deviceName;
            return str != null ? str.compareToIgnoreCase(wifiP2pDevice2.deviceName) : wifiP2pDevice.deviceAddress.compareToIgnoreCase(wifiP2pDevice2.deviceAddress);
        }
        return 1;
    }

    int getLevel() {
        int i = this.mRssi;
        if (i == Integer.MAX_VALUE) {
            return -1;
        }
        return MiuiWifiManager.calculateSignalLevel(i, 5);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        ImageView imageView = (ImageView) view.findViewById(16908294);
        this.mSignal = imageView;
        if (this.mRssi == Integer.MAX_VALUE) {
            imageView.setImageDrawable(null);
        } else {
            imageView.setImageResource(R.drawable.wifi_signal);
        }
        view.findViewById(R.id.preference_detail).setVisibility(8);
        view.findViewById(R.id.encryption).setVisibility(0);
        view.findViewById(R.id.wifi_band).setVisibility(8);
        updateSignalLevel();
    }
}
