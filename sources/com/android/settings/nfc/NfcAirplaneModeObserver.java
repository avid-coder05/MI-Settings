package com.android.settings.nfc;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.provider.Settings;
import androidx.preference.Preference;

/* loaded from: classes2.dex */
public class NfcAirplaneModeObserver extends ContentObserver {
    static final Uri AIRPLANE_MODE_URI = Settings.Global.getUriFor("airplane_mode_on");
    private int mAirplaneMode;
    private final Context mContext;
    private final NfcAdapter mNfcAdapter;
    private final Preference mPreference;

    private void updateNfcPreference() {
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "airplane_mode_on", this.mAirplaneMode);
        if (i == this.mAirplaneMode) {
            return;
        }
        this.mAirplaneMode = i;
        if (i != 1) {
            this.mPreference.setEnabled(true);
            return;
        }
        this.mNfcAdapter.disable();
        this.mPreference.setEnabled(NfcPreferenceController.isToggleableInAirplaneMode(this.mContext));
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean z, Uri uri) {
        super.onChange(z, uri);
        updateNfcPreference();
    }
}
