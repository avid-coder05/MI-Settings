package com.android.settings.bluetooth;

import android.content.Context;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class MiuiMiscBluetoothPreference extends Preference {
    private Preference.OnPreferenceClickListener mPreferenceClickListener;

    public MiuiMiscBluetoothPreference(Context context, int i) {
        super(context, true);
        this.mPreferenceClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.bluetooth.MiuiMiscBluetoothPreference.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(androidx.preference.Preference preference) {
                return false;
            }
        };
        setLayoutResource(R.layout.preference_bt_rarely_used);
        setIcon(R.drawable.ic_bt_rarely_used);
        setTitle(context.getString(R.string.bt_rarely_used_device_title, Integer.valueOf(i)));
        setKey("misc_devices");
        setOrder(100);
        setOnPreferenceClickListener(this.mPreferenceClickListener);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.Preference, java.lang.Comparable
    public int compareTo(androidx.preference.Preference preference) {
        if (preference instanceof BluetoothDevicePreference) {
            return 1;
        }
        return super.compareTo(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDeviceCount(int i) {
        setTitle(getContext().getString(R.string.bt_rarely_used_device_title, Integer.valueOf(i)));
    }
}
