package com.android.settings.network;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.GenericSwitchController;
import com.android.settings.wifi.WifiEnabler;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class WifiSwitchPreferenceController extends AbstractPreferenceController implements LifecycleObserver {
    private RestrictedSwitchPreference mPreference;
    private WifiEnabler mWifiEnabler;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "main_toggle_wifi";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.pause();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.resume(this.mContext);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (restrictedSwitchPreference != null) {
            this.mWifiEnabler = new WifiEnabler(this.mContext, new GenericSwitchController(restrictedSwitchPreference), FeatureFactory.getFactory(this.mContext).getMetricsFeatureProvider());
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        WifiEnabler wifiEnabler = this.mWifiEnabler;
        if (wifiEnabler != null) {
            wifiEnabler.teardownSwitchController();
        }
    }
}
