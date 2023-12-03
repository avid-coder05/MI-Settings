package com.android.settings.nfc;

import androidx.preference.Preference;
import com.android.settings.nfc.PaymentBackend;
import com.android.settingslib.miuisettings.preference.ListPreference;

/* loaded from: classes2.dex */
public class NfcForegroundPreference extends ListPreference implements PaymentBackend.Callback, Preference.OnPreferenceChangeListener {
    private final PaymentBackend mPaymentBackend;

    @Override // com.android.settings.nfc.PaymentBackend.Callback
    public void onPaymentAppsChanged() {
        refresh();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setValue((String) obj);
        refresh();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public boolean persistString(String str) {
        this.mPaymentBackend.setForegroundMode(Integer.parseInt(str) != 0);
        return true;
    }

    void refresh() {
        if (this.mPaymentBackend.isForegroundMode()) {
            setValue("1");
        } else {
            setValue("0");
        }
        setSummary(getEntry());
    }
}
