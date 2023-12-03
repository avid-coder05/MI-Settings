package com.android.settings.wireless;

import android.content.Context;
import android.nfc.NfcAdapter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.RegionUtils;
import com.android.settings.nfc.NfcEnabler;
import com.android.settings.nfc.NfcPreferenceController;
import com.android.settings.nfc.SecureNfcEnabler;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.MainSwitchPreference;
import com.miui.enterprise.RestrictionsHelper;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class MiuiNfcSwitchController extends AbstractPreferenceController implements LifecycleObserver, OnResume, OnPause {
    private NfcAdapter mNfcAdapter;
    private NfcEnabler mNfcEnabler;
    private SecureNfcEnabler mSecureNfcEnabler;

    public MiuiNfcSwitchController(Context context, Lifecycle lifecycle) {
        super(context);
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        MainSwitchPreference mainSwitchPreference = (MainSwitchPreference) preferenceScreen.findPreference(NfcPreferenceController.KEY_TOGGLE_NFC);
        DropDownPreference dropDownPreference = (DropDownPreference) preferenceScreen.findPreference("nfc_payment");
        SwitchPreference switchPreference = (SwitchPreference) preferenceScreen.findPreference("nfc_secure_settings");
        if (mainSwitchPreference == null) {
            return;
        }
        if (dropDownPreference == null) {
            Log.e("MiuiNfcSwitchController", "nfcPayment:null");
        }
        if (switchPreference == null) {
            Log.e("MiuiNfcSwitchController", "getListPreference called! secureNfc:null");
        }
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
        this.mNfcAdapter = defaultAdapter;
        if (defaultAdapter == null) {
            preferenceScreen.removePreference(mainSwitchPreference);
            if (dropDownPreference != null) {
                preferenceScreen.removePreference(dropDownPreference);
            }
            if (switchPreference != null) {
                preferenceScreen.removePreference(switchPreference);
                return;
            }
            return;
        }
        boolean z = defaultAdapter.isSecureNfcSupported() && RegionUtils.IS_JP;
        if (z) {
            this.mSecureNfcEnabler = new SecureNfcEnabler(this.mContext, switchPreference);
        } else {
            this.mSecureNfcEnabler = null;
            if (switchPreference != null) {
                preferenceScreen.removePreference(switchPreference);
            }
        }
        if (RestrictionsHelper.hasNFCRestriction(this.mContext)) {
            mainSwitchPreference.setEnabled(false);
            if (z) {
                switchPreference.setEnabled(false);
                this.mSecureNfcEnabler = null;
            }
            dropDownPreference.setEnabled(false);
        }
        this.mNfcEnabler = new NfcEnabler(this.mContext, mainSwitchPreference, dropDownPreference, true);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return NfcPreferenceController.KEY_TOGGLE_NFC;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.pause();
        }
        SecureNfcEnabler secureNfcEnabler = this.mSecureNfcEnabler;
        if (secureNfcEnabler != null) {
            secureNfcEnabler.pause();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        NfcEnabler nfcEnabler = this.mNfcEnabler;
        if (nfcEnabler != null) {
            nfcEnabler.resume();
        }
        SecureNfcEnabler secureNfcEnabler = this.mSecureNfcEnabler;
        if (secureNfcEnabler != null) {
            secureNfcEnabler.resume();
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
    }
}
