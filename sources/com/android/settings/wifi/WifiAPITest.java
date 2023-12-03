package com.android.settings.wifi;

import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.widget.EditText;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class WifiAPITest extends SettingsPreferenceFragment implements Preference.OnPreferenceClickListener {
    private Preference mWifiDisableNetwork;
    private Preference mWifiDisconnect;
    private Preference mWifiEnableNetwork;
    private WifiManager mWifiManager;
    private int netid;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 89;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.layout.wifi_api_test);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference findPreference = preferenceScreen.findPreference("disconnect");
        this.mWifiDisconnect = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        Preference findPreference2 = preferenceScreen.findPreference("disable_network");
        this.mWifiDisableNetwork = findPreference2;
        findPreference2.setOnPreferenceClickListener(this);
        Preference findPreference3 = preferenceScreen.findPreference("enable_network");
        this.mWifiEnableNetwork = findPreference3;
        findPreference3.setOnPreferenceClickListener(this);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference == this.mWifiDisconnect) {
            this.mWifiManager.disconnect();
            return true;
        } else if (preference == this.mWifiDisableNetwork) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Input");
            builder.setMessage("Enter Network ID");
            final EditText editText = new EditText(getPrefContext());
            builder.setView(editText);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiAPITest.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Editable text = editText.getText();
                    try {
                        WifiAPITest.this.netid = Integer.parseInt(text.toString());
                        WifiAPITest.this.mWifiManager.disableNetwork(WifiAPITest.this.netid);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiAPITest.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder.show();
            return true;
        } else if (preference == this.mWifiEnableNetwork) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
            builder2.setTitle("Input");
            builder2.setMessage("Enter Network ID");
            final EditText editText2 = new EditText(getPrefContext());
            builder2.setView(editText2);
            builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiAPITest.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    Editable text = editText2.getText();
                    try {
                        WifiAPITest.this.netid = Integer.parseInt(text.toString());
                        WifiAPITest.this.mWifiManager.enableNetwork(WifiAPITest.this.netid, false);
                    } catch (NumberFormatException unused) {
                    }
                }
            });
            builder2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiAPITest.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            builder2.show();
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        return false;
    }
}
