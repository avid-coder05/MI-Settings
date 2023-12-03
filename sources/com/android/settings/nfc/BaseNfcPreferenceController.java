package com.android.settings.nfc;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Handler;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class BaseNfcPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause {
    private int mAirplaneMode;
    private AirplaneModeObserver mAirplaneModeObserver;
    private NfcAdapter mNfcAdapter;
    protected BaseNfcEnabler mNfcEnabler;
    protected Preference mPreference;

    /* loaded from: classes2.dex */
    private final class AirplaneModeObserver extends ContentObserver {
        private final Uri AIRPLANE_MODE_URI;

        private AirplaneModeObserver() {
            super(new Handler());
            this.AIRPLANE_MODE_URI = Settings.Global.getUriFor("airplane_mode_on");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            BaseNfcPreferenceController.this.updateNfcPreference();
        }

        public void register() {
            ((AbstractPreferenceController) BaseNfcPreferenceController.this).mContext.getContentResolver().registerContentObserver(this.AIRPLANE_MODE_URI, false, this);
        }

        public void unregister() {
            ((AbstractPreferenceController) BaseNfcPreferenceController.this).mContext.getContentResolver().unregisterContentObserver(this);
        }
    }

    public static boolean isToggleableInAirplaneMode(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "airplane_mode_toggleable_radios");
        return string != null && string.contains("nfc");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNfcPreference() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", this.mAirplaneMode);
        if (i == this.mAirplaneMode) {
            return;
        }
        this.mAirplaneMode = i;
        boolean z = i != 1;
        if (z) {
            this.mNfcAdapter.enable();
        } else {
            this.mNfcAdapter.disable();
        }
        this.mPreference.setEnabled(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (!isAvailable()) {
            this.mNfcEnabler = null;
            return;
        }
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
        if (isToggleableInAirplaneMode(this.mContext)) {
            return;
        }
        this.mAirplaneModeObserver = new AirplaneModeObserver();
        updateNfcPreference();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mNfcAdapter != null;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        AirplaneModeObserver airplaneModeObserver = this.mAirplaneModeObserver;
        if (airplaneModeObserver != null) {
            airplaneModeObserver.unregister();
        }
        BaseNfcEnabler baseNfcEnabler = this.mNfcEnabler;
        if (baseNfcEnabler != null) {
            baseNfcEnabler.pause();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        AirplaneModeObserver airplaneModeObserver = this.mAirplaneModeObserver;
        if (airplaneModeObserver != null) {
            airplaneModeObserver.register();
        }
        BaseNfcEnabler baseNfcEnabler = this.mNfcEnabler;
        if (baseNfcEnabler != null) {
            baseNfcEnabler.resume();
        }
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateNonIndexableKeys(List<String> list) {
        if (isAvailable()) {
            list.add(getPreferenceKey());
        }
    }
}
