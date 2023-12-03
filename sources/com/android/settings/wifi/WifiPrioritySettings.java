package com.android.settings.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiPrioritySettings extends AppCompatActivity {

    /* loaded from: classes2.dex */
    public static class WifiPriorityFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
        private int configuredApCount;
        private List<WifiConfiguration> mConfigs;
        PreferenceCategory mConfiguredAps;
        private WifiManager mWifiManager;

        /* JADX INFO: Access modifiers changed from: private */
        public boolean formerHasHigherPriority(WifiConfiguration wifiConfiguration, WifiConfiguration wifiConfiguration2) {
            int i;
            int i2;
            if (wifiConfiguration == null) {
                return false;
            }
            if (wifiConfiguration2 != null && (i = wifiConfiguration.priority) <= (i2 = wifiConfiguration2.priority)) {
                if (i < i2) {
                    return false;
                }
                String str = wifiConfiguration.SSID;
                String removeDoubleQuotes = str == null ? "" : removeDoubleQuotes(str);
                String str2 = wifiConfiguration2.SSID;
                String removeDoubleQuotes2 = str2 != null ? removeDoubleQuotes(str2) : "";
                if (removeDoubleQuotes.equals(removeDoubleQuotes2)) {
                    return wifiConfiguration.networkId > wifiConfiguration2.networkId;
                } else if ("CMCC-AUTO".equals(removeDoubleQuotes)) {
                    Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same true");
                    return true;
                } else if ("CMCC".equals(removeDoubleQuotes)) {
                    if ("CMCC-AUTO".equals(removeDoubleQuotes2)) {
                        Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same false");
                        return false;
                    }
                    Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same true");
                    return true;
                } else if ("CMCC-EDU".equals(removeDoubleQuotes)) {
                    if ("CMCC".equals(removeDoubleQuotes2) || "CMCC-AUTO".equals(removeDoubleQuotes2)) {
                        Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same false");
                        return false;
                    }
                    Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same true");
                    return true;
                } else if (!"CMCC".equals(removeDoubleQuotes2) && !"CMCC-AUTO".equals(removeDoubleQuotes2) && !"CMCC-EDU".equals(removeDoubleQuotes2)) {
                    return removeDoubleQuotes.compareTo(removeDoubleQuotes2) <= 0;
                } else {
                    Log.d("WifiPrioritySettings", "WifiSettingsExt formerHasHigherPriority() same false");
                    return false;
                }
            }
            return true;
        }

        private String removeDoubleQuotes(String str) {
            int length = str.length();
            if (length > 1 && str.charAt(0) == '\"') {
                int i = length - 1;
                if (str.charAt(i) == '\"') {
                    return str.substring(1, i);
                }
            }
            return str;
        }

        private void updateConfig(WifiConfiguration wifiConfiguration) {
            Log.e("WifiPrioritySettings", "updateConfig()");
            if (wifiConfiguration == null) {
                return;
            }
            WifiConfiguration wifiConfiguration2 = new WifiConfiguration(wifiConfiguration);
            wifiConfiguration2.networkId = wifiConfiguration.networkId;
            wifiConfiguration2.priority = wifiConfiguration.priority;
            this.mWifiManager.updateNetwork(wifiConfiguration2);
        }

        public void calculateInitPriority(List<WifiConfiguration> list) {
            int i;
            if (list == null) {
                return;
            }
            Iterator<WifiConfiguration> it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (it.next() == null) {
                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    wifiConfiguration.SSID = "ERROR";
                    wifiConfiguration.priority = 0;
                }
            }
            Collections.sort(list, new Comparator<WifiConfiguration>() { // from class: com.android.settings.wifi.WifiPrioritySettings.WifiPriorityFragment.1
                @Override // java.util.Comparator
                public int compare(WifiConfiguration wifiConfiguration2, WifiConfiguration wifiConfiguration3) {
                    return WifiPriorityFragment.this.formerHasHigherPriority(wifiConfiguration2, wifiConfiguration3) ? -1 : 1;
                }
            });
            int size = list.size();
            for (i = 0; i < size; i++) {
                WifiConfiguration wifiConfiguration2 = list.get(i);
                int i2 = size - i;
                if (wifiConfiguration2.priority != i2) {
                    wifiConfiguration2.priority = i2;
                    updateConfig(wifiConfiguration2);
                }
            }
        }

        public void initPage() {
            PreferenceCategory preferenceCategory;
            WifiManager wifiManager = this.mWifiManager;
            if (wifiManager == null) {
                Log.e("WifiPrioritySettings", "Fail to get Wifi Manager service");
                return;
            }
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            this.mConfigs = configuredNetworks;
            if (configuredNetworks == null || (preferenceCategory = this.mConfiguredAps) == null) {
                return;
            }
            preferenceCategory.removeAll();
            int size = this.mConfigs.size();
            this.configuredApCount = size;
            String[] strArr = new String[size];
            int i = 0;
            int i2 = 0;
            while (i2 < this.configuredApCount) {
                int i3 = i2 + 1;
                strArr[i2] = String.valueOf(i3);
                i2 = i3;
            }
            for (int i4 = 0; i4 < this.configuredApCount; i4++) {
                WifiConfiguration wifiConfiguration = this.mConfigs.get(i4);
                Log.e("WifiPrioritySettings", "Before sorting: ssid=" + wifiConfiguration.SSID + ", priority=" + wifiConfiguration.priority);
            }
            calculateInitPriority(this.mConfigs);
            String str = getResources().getString(R.string.wifi_priority_label) + ": ";
            while (i < this.configuredApCount) {
                WifiConfiguration wifiConfiguration2 = this.mConfigs.get(i);
                Log.e("WifiPrioritySettings", "After sorting: ssid=" + wifiConfiguration2.SSID + ", priority=" + wifiConfiguration2.priority);
                String str2 = wifiConfiguration2.SSID;
                String removeDoubleQuotes = str2 == null ? "" : removeDoubleQuotes(str2);
                DropDownPreference dropDownPreference = new DropDownPreference(getPrefContext());
                dropDownPreference.setOnPreferenceChangeListener(this);
                dropDownPreference.setKey(removeDoubleQuotes);
                dropDownPreference.setTitle(removeDoubleQuotes);
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                int i5 = i + 1;
                sb.append(i5);
                dropDownPreference.setSummary(sb.toString());
                dropDownPreference.setEntries(strArr);
                dropDownPreference.setEntryValues(strArr);
                dropDownPreference.setValueIndex(i);
                this.mConfiguredAps.addPreference(dropDownPreference);
                i = i5;
            }
            this.mWifiManager.saveConfiguration();
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mConfiguredAps = (PreferenceCategory) findPreference("configured_ap_list");
            this.mWifiManager = (WifiManager) getSystemService("wifi");
            initPage();
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.layout.wifi_priority_settings);
        }

        /* JADX WARN: Removed duplicated region for block: B:15:0x0067  */
        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public boolean onPreferenceChange(androidx.preference.Preference r9, java.lang.Object r10) {
            /*
                r8 = this;
                java.lang.String r0 = "WifiPrioritySettings"
                boolean r1 = r9 instanceof com.android.settingslib.miuisettings.preference.miuix.DropDownPreference
                r2 = 1
                if (r1 == 0) goto L9f
                com.android.settingslib.miuisettings.preference.miuix.DropDownPreference r9 = (com.android.settingslib.miuisettings.preference.miuix.DropDownPreference) r9
                r1 = 0
                java.lang.String r3 = r9.getValue()     // Catch: java.lang.NumberFormatException -> L1c
                int r3 = java.lang.Integer.parseInt(r3)     // Catch: java.lang.NumberFormatException -> L1c
                r4 = r10
                java.lang.String r4 = (java.lang.String) r4     // Catch: java.lang.NumberFormatException -> L1a
                int r4 = java.lang.Integer.parseInt(r4)     // Catch: java.lang.NumberFormatException -> L1a
                goto L27
            L1a:
                r4 = move-exception
                goto L1e
            L1c:
                r4 = move-exception
                r3 = r1
            L1e:
                java.lang.String r5 = "Error happens when modify priority manually"
                android.util.Log.e(r0, r5)
                r4.printStackTrace()
                r4 = r1
            L27:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder
                r5.<init>()
                java.lang.String r6 = "Priority old value="
                r5.append(r6)
                r5.append(r3)
                java.lang.String r6 = ", new value="
                r5.append(r6)
                r5.append(r4)
                java.lang.String r5 = r5.toString()
                android.util.Log.e(r0, r5)
                androidx.fragment.app.FragmentActivity r0 = r8.getActivity()
                android.content.res.Resources r5 = r8.getResources()
                int r6 = com.android.settings.R.string.wifi_priority_changed
                r7 = 2
                java.lang.Object[] r7 = new java.lang.Object[r7]
                java.lang.String r9 = r9.getValue()
                r7[r1] = r9
                java.lang.String r10 = (java.lang.String) r10
                r7[r2] = r10
                java.lang.String r9 = r5.getString(r6, r7)
                android.widget.Toast r9 = android.widget.Toast.makeText(r0, r9, r1)
                r9.show()
                if (r3 == r4) goto L9f
                int r3 = r3 - r2
                int r4 = r4 - r2
                java.util.List<android.net.wifi.WifiConfiguration> r9 = r8.mConfigs
                java.lang.Object r10 = r9.remove(r3)
                android.net.wifi.WifiConfiguration r10 = (android.net.wifi.WifiConfiguration) r10
                r9.add(r4, r10)
                int r9 = java.lang.Math.min(r4, r3)
                int r10 = java.lang.Math.max(r4, r3)
                java.util.List<android.net.wifi.WifiConfiguration> r0 = r8.mConfigs
                int r0 = r0.size()
            L82:
                if (r9 > r10) goto L96
                java.util.List<android.net.wifi.WifiConfiguration> r2 = r8.mConfigs
                java.lang.Object r2 = r2.get(r9)
                android.net.wifi.WifiConfiguration r2 = (android.net.wifi.WifiConfiguration) r2
                int r3 = r0 - r9
                r2.priority = r3
                r8.updateConfig(r2)
                int r9 = r9 + 1
                goto L82
            L96:
                android.net.wifi.WifiManager r9 = r8.mWifiManager
                r9.saveConfiguration()
                r8.updateUI()
                return r1
            L9f:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiPrioritySettings.WifiPriorityFragment.onPreferenceChange(androidx.preference.Preference, java.lang.Object):boolean");
        }

        public void updateUI() {
            for (int i = 0; i < this.mConfigs.size(); i++) {
                Preference preference = this.mConfiguredAps.getPreference(i);
                if (preference != null) {
                    String str = this.mConfigs.get(i).SSID;
                    preference.setTitle(str == null ? "" : removeDoubleQuotes(str));
                    preference.setSummary((getResources().getString(R.string.wifi_priority_label) + ": ") + (i + 1));
                }
            }
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, new WifiPriorityFragment()).commit();
        }
    }
}
