package com.android.settings.wireless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.UserHandle;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.ThreadUtils;

/* loaded from: classes2.dex */
public class TetherEntryController extends AbstractPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private boolean mHasRegister;
    private IntentFilter mIntentFilter;
    private RestrictedPreference mPreference;
    private final BroadcastReceiver mReceiver;

    public TetherEntryController(Context context) {
        super(context);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wireless.TetherEntryController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TetherEntryController.this.mPreference != null) {
                    TetherEntryController.this.updateValue();
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        this.mIntentFilter = intentFilter;
        intentFilter.addAction("android.net.conn.TETHER_STATE_CHANGED");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateValue() {
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.wireless.TetherEntryController.2
            @Override // java.lang.Runnable
            public void run() {
                TetherEntryController.this.mPreference.setSummary2Value(true);
                RestrictedPreference restrictedPreference = TetherEntryController.this.mPreference;
                TetherEntryController tetherEntryController = TetherEntryController.this;
                restrictedPreference.setSummary(tetherEntryController.isWifiTetherEnabled(((AbstractPreferenceController) tetherEntryController).mContext) ? R.string.wireless_on : R.string.wireless_off);
            }
        });
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (RestrictedPreference) preferenceScreen.findPreference("wifi_tether_settings");
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_tether_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return SettingsFeatures.getWifiTetherPlacement(this.mContext.getApplicationContext()) == 2 && !(UserHandle.myUserId() != 0);
    }

    public boolean isWifiTetherEnabled(Context context) {
        return ((WifiManager) context.getSystemService("wifi")).getWifiApState() == 13;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (this.mHasRegister) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mHasRegister = false;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mPreference != null) {
            this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
            this.mHasRegister = true;
            this.mPreference.checkRestrictionAndSetDisabled("no_config_tethering");
            updateValue();
        }
    }
}
