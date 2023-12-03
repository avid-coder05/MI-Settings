package com.android.settings.nfc;

import android.content.Context;
import android.os.UserManager;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.RegionUtils;

/* loaded from: classes2.dex */
public class SecureNfcEnabler extends BaseNfcEnabler implements Preference.OnPreferenceChangeListener {
    private final SwitchPreference mPreference;
    private final UserManager mUserManager;

    public SecureNfcEnabler(Context context, SwitchPreference switchPreference) {
        super(context);
        this.mPreference = switchPreference;
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        if (isNfcAvailable()) {
            switchPreference.setChecked(this.mNfcAdapter.isSecureNfcEnabled());
        }
        switchPreference.setOnPreferenceChangeListener(this);
    }

    private boolean isToggleable() {
        return !this.mUserManager.isGuestUser();
    }

    @Override // com.android.settings.nfc.BaseNfcEnabler
    protected void handleNfcStateChanged(int i) {
        if (i == 1) {
            if (RegionUtils.IS_JP_KDDI) {
                this.mPreference.setSummary(R.string.kddi_nfc_disabled_summary);
            } else {
                this.mPreference.setSummary(R.string.nfc_disabled_summary);
            }
            this.mPreference.setEnabled(false);
        } else if (i == 2) {
            this.mPreference.setEnabled(false);
        } else if (i != 3) {
            if (i != 4) {
                return;
            }
            this.mPreference.setEnabled(false);
        } else {
            if (RegionUtils.IS_JP_KDDI) {
                this.mPreference.setSummary(R.string.kddi_nfc_secure_toggle_summary);
            } else {
                this.mPreference.setSummary(R.string.nfc_secure_toggle_summary);
            }
            SwitchPreference switchPreference = this.mPreference;
            switchPreference.setChecked(switchPreference.isChecked());
            this.mPreference.setEnabled(isToggleable());
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        boolean z = false;
        this.mPreference.setEnabled(false);
        if (isNfcAvailable() && !(z = this.mNfcAdapter.enableSecureNfc(booleanValue))) {
            this.mPreference.setChecked(this.mNfcAdapter.isSecureNfcEnabled());
        }
        this.mPreference.setEnabled(true);
        return z;
    }
}
