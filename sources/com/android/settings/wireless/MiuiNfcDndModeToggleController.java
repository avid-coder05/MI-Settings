package com.android.settings.wireless;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;

/* loaded from: classes2.dex */
public class MiuiNfcDndModeToggleController extends AbstractPreferenceController implements LifecycleObserver, Preference.OnPreferenceChangeListener {
    private NfcAdapter mNfcAdapter;

    public MiuiNfcDndModeToggleController(Context context, Lifecycle lifecycle) {
        super(context);
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference("toggle_nfc_dnd_mode");
        if (switchPreference == null) {
            return;
        }
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
        this.mNfcAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            preferenceScreen.removePreference(switchPreference);
            return;
        }
        switchPreference.setOnPreferenceChangeListener(this);
        switchPreference.setChecked(Settings.Secure.getInt(this.mContext.getContentResolver(), "nfc_dnd_mode", 0) != 0);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "toggle_nfc_dnd_mode";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (SettingsFeatures.isNeedShowMiuiNFC()) {
            return false;
        }
        return SettingsFeatures.hasNfcDispatchOptimFeature(this.mContext);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        ((SwitchPreference) preference).setChecked(booleanValue);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "nfc_dnd_mode", booleanValue ? 1 : 0);
        return true;
    }
}
