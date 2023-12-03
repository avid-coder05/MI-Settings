package com.android.settings;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.os.Build;
import miui.util.FeatureParser;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class AgpsSettings extends AppCompatActivity {
    private static String sNotSet;
    private ContentResolver mContentResolver;
    private AgpsSettingsFragment mFragment;

    /* loaded from: classes.dex */
    public static class AgpsSettingsFragment extends SettingsPreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
        private String mAssistedType;
        private ContentResolver mContentResolver;
        private boolean mFirstTime;
        private String mNetworkType;
        private EditTextPreference mPort;
        private String mResetType;
        private EditTextPreference mServer;

        private void SetValue(Bundle bundle) {
            String string = bundle.getString(YellowPageContract.Permission.HOST);
            String string2 = bundle.getString("port");
            String string3 = bundle.getString("providerid");
            String string4 = bundle.getString("network");
            String string5 = bundle.getString("resettype");
            if (string == null || string.length() <= 0) {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_supl_host", getResources().getString(R.string.location_agps_def_supl_host));
            } else {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_supl_host", string);
            }
            if (string2 != null) {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_supl_port", string2);
            } else {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_supl_port", getResources().getString(R.string.location_agps_def_supl_port));
            }
            if (string3 != null && string3.length() > 0) {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_position_mode", string3);
            }
            if (string4 != null && string4.length() > 0) {
                Settings.Global.putString(this.mContentResolver, "assisted_gps_network", string4);
            }
            if (string5 == null || string5.length() <= 0) {
                return;
            }
            Settings.Global.putString(this.mContentResolver, "assisted_gps_reset_type", string5.compareTo("HOT") == 0 ? "2" : string5.compareTo("WARM") == 0 ? "1" : "0");
        }

        private String checkNotSet(String str) {
            return (str == null || str.equals(AgpsSettings.sNotSet)) ? "" : str;
        }

        private String checkNull(String str) {
            return TextUtils.isEmpty(str) ? AgpsSettings.sNotSet : str;
        }

        private void fillUi(boolean z) {
            if (this.mFirstTime || z) {
                this.mFirstTime = false;
                this.mServer.setText(getSuplServer());
                this.mPort.setText(getSuplPort());
            }
            EditTextPreference editTextPreference = this.mServer;
            editTextPreference.setSummary(checkNull(editTextPreference.getText()));
            EditTextPreference editTextPreference2 = this.mPort;
            editTextPreference2.setSummary(checkNull(editTextPreference2.getText()));
            setPrefAgpsType();
            setPrefAgpsNetwork();
            setPrefAgpsResetType();
        }

        private String getPrefAgpsResetType() {
            String string = Settings.Global.getString(this.mContentResolver, "assisted_gps_reset_type");
            if (string != null) {
                string = string.compareTo("2") == 0 ? "HOT" : string.compareTo("1") == 0 ? "WARM" : "COLD";
            }
            return string != null ? string : getResources().getString(R.string.location_agps_def_reset_type);
        }

        private String getPrefAgpsType() {
            String string = Settings.Global.getString(this.mContentResolver, "assisted_gps_position_mode");
            return string != null ? string : getResources().getString(R.string.location_agps_def_location_mode);
        }

        private String getPrefNetwork() {
            String string = Settings.Global.getString(this.mContentResolver, "assisted_gps_network");
            return string != null ? string : getResources().getString(R.string.location_agps_def_network_mode);
        }

        private String getSuplPort() {
            String string = Settings.Global.getString(this.mContentResolver, "assisted_gps_supl_port");
            return string != null ? string : getResources().getString(R.string.location_agps_def_supl_port);
        }

        private String getSuplServer() {
            String string = Settings.Global.getString(this.mContentResolver, "assisted_gps_supl_host");
            String string2 = getResources().getString(R.string.location_agps_def_supl_host);
            if (string == null) {
                string = getPropertyFromGpsConfig("SUPL_HOST");
            }
            if (Build.IS_GLOBAL_BUILD && (string == null || string.equals(string2))) {
                string = "";
            }
            return string != null ? string : string2;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Code restructure failed: missing block: B:14:0x0053, code lost:
        
            if (r5 == null) goto L16;
         */
        /* JADX WARN: Removed duplicated region for block: B:32:0x00cc A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void restoreAgpsParam() {
            /*
                r7 = this;
                java.lang.String r0 = "AGPSSettings"
                android.os.Bundle r1 = new android.os.Bundle
                r1.<init>()
                r2 = 0
                java.util.Properties r3 = new java.util.Properties     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                r3.<init>()     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                java.io.File r4 = new java.io.File     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                java.lang.String r5 = "/etc/gps.conf"
                r4.<init>(r5)     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                r5.<init>(r4)     // Catch: java.lang.Throwable -> L39 java.io.IOException -> L3c
                r3.load(r5)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> Lc8
                java.lang.String r4 = "host"
                java.lang.String r6 = "SUPL_HOST"
                java.lang.String r6 = r3.getProperty(r6, r2)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> Lc8
                r1.putString(r4, r6)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> Lc8
                java.lang.String r4 = "port"
                java.lang.String r6 = "SUPL_PORT"
                java.lang.String r2 = r3.getProperty(r6, r2)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> Lc8
                r1.putString(r4, r2)     // Catch: java.io.IOException -> L37 java.lang.Throwable -> Lc8
            L33:
                r5.close()     // Catch: java.lang.Exception -> L56
                goto L56
            L37:
                r2 = move-exception
                goto L3f
            L39:
                r7 = move-exception
                goto Lca
            L3c:
                r3 = move-exception
                r5 = r2
                r2 = r3
            L3f:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> Lc8
                r3.<init>()     // Catch: java.lang.Throwable -> Lc8
                java.lang.String r4 = "Could not open GPS configuration file /etc/gps.conf, e="
                r3.append(r4)     // Catch: java.lang.Throwable -> Lc8
                r3.append(r2)     // Catch: java.lang.Throwable -> Lc8
                java.lang.String r2 = r3.toString()     // Catch: java.lang.Throwable -> Lc8
                android.util.Log.e(r0, r2)     // Catch: java.lang.Throwable -> Lc8
                if (r5 == 0) goto L56
                goto L33
            L56:
                java.lang.String r2 = "MSB"
                r7.mAssistedType = r2
                java.lang.String r3 = "HOME"
                r7.mNetworkType = r3
                java.lang.String r3 = "HOT"
                r7.mResetType = r3
                java.lang.String r4 = "providerid"
                r1.putString(r4, r2)
                java.lang.String r2 = r7.mNetworkType
                java.lang.String r4 = "network"
                r1.putString(r4, r2)
                java.lang.String r2 = r7.mResetType
                java.lang.String r4 = "resettype"
                r1.putString(r4, r2)
                r7.SetValue(r1)
                r2 = 1
                r7.fillUi(r2)
                java.lang.String r2 = "location"
                java.lang.Object r2 = r7.getSystemService(r2)
                android.location.LocationManager r2 = (android.location.LocationManager) r2
                java.lang.String r5 = r7.mResetType
                int r3 = r5.compareTo(r3)
                if (r3 != 0) goto L95
                java.lang.String r7 = "2"
                r1.putString(r4, r7)
                goto Laa
            L95:
                java.lang.String r7 = r7.mResetType
                java.lang.String r3 = "WARM"
                int r7 = r7.compareTo(r3)
                if (r7 != 0) goto La5
                java.lang.String r7 = "1"
                r1.putString(r4, r7)
                goto Laa
            La5:
                java.lang.String r7 = "0"
                r1.putString(r4, r7)
            Laa:
                java.lang.String r7 = "gps"
                java.lang.String r3 = "agps_parms_changed"
                boolean r7 = r2.sendExtraCommand(r7, r3, r1)
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "sendExtraCommand ret="
                r1.append(r2)
                r1.append(r7)
                java.lang.String r7 = r1.toString()
                android.util.Log.d(r0, r7)
                return
            Lc8:
                r7 = move-exception
                r2 = r5
            Lca:
                if (r2 == 0) goto Lcf
                r2.close()     // Catch: java.lang.Exception -> Lcf
            Lcf:
                throw r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.AgpsSettings.AgpsSettingsFragment.restoreAgpsParam():void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void saveAgpsParams() {
            Bundle bundle = new Bundle();
            bundle.putString(YellowPageContract.Permission.HOST, checkNotSet(this.mServer.getText()));
            bundle.putString("port", checkNotSet(this.mPort.getText()));
            bundle.putString("providerid", this.mAssistedType);
            bundle.putString("network", this.mNetworkType);
            SetValue(bundle);
            if (this.mResetType.compareTo("HOT") == 0) {
                bundle.putString("resettype", "2");
            } else if (this.mResetType.compareTo("WARM") == 0) {
                bundle.putString("resettype", "1");
            } else {
                bundle.putString("resettype", "0");
            }
            Log.d("AGPSSettings", "sendExtraCommand ret=" + ((LocationManager) getSystemService("location")).sendExtraCommand("gps", "agps_parms_changed", bundle));
        }

        private void setPrefAgpsNetwork() {
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("agps_network");
            dropDownPreference.setOnPreferenceChangeListener(this);
            String[] stringArray = getResources().getStringArray(R.array.agps_network_entries);
            String prefNetwork = getPrefNetwork();
            this.mNetworkType = prefNetwork;
            if (prefNetwork.equals("ALL")) {
                dropDownPreference.setValue("1");
                dropDownPreference.setSummary(stringArray[1]);
                return;
            }
            dropDownPreference.setValue("0");
            dropDownPreference.setSummary(stringArray[0]);
        }

        private void setPrefAgpsResetType() {
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("agps_reset_type");
            dropDownPreference.setOnPreferenceChangeListener(this);
            String[] stringArray = getResources().getStringArray(R.array.agps_reset_type_entries);
            String prefAgpsResetType = getPrefAgpsResetType();
            this.mResetType = prefAgpsResetType;
            if (prefAgpsResetType.equals("COLD")) {
                dropDownPreference.setValue("2");
                dropDownPreference.setSummary(stringArray[2]);
            } else if (prefAgpsResetType.equals("WARM")) {
                dropDownPreference.setValue("1");
                dropDownPreference.setSummary(stringArray[1]);
            } else {
                dropDownPreference.setValue("0");
                dropDownPreference.setSummary(stringArray[0]);
            }
        }

        private void setPrefAgpsType() {
            DropDownPreference dropDownPreference = (DropDownPreference) findPreference("agps_pref");
            dropDownPreference.setOnPreferenceChangeListener(this);
            String[] stringArray = getResources().getStringArray(R.array.agps_si_mode_entries);
            PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefAgpsType = getPrefAgpsType();
            this.mAssistedType = prefAgpsType;
            if (prefAgpsType.equals("MSB")) {
                dropDownPreference.setValue("0");
                dropDownPreference.setSummary(stringArray[0]);
            } else if (prefAgpsType.equals("MSA")) {
                dropDownPreference.setValue("1");
                dropDownPreference.setSummary(stringArray[1]);
            } else {
                dropDownPreference.setValue("2");
                dropDownPreference.setSummary("");
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:14:0x003b, code lost:
        
            if (r2 == null) goto L16;
         */
        /* JADX WARN: Removed duplicated region for block: B:25:0x0042 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.String getPropertyFromGpsConfig(java.lang.String r5) {
            /*
                r4 = this;
                r4 = 0
                java.util.Properties r0 = new java.util.Properties     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                r0.<init>()     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                java.io.File r1 = new java.io.File     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                java.lang.String r2 = "/etc/gps.conf"
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                r2.<init>(r1)     // Catch: java.lang.Throwable -> L1f java.io.IOException -> L23
                r0.load(r2)     // Catch: java.io.IOException -> L1d java.lang.Throwable -> L3f
                java.lang.String r4 = r0.getProperty(r5, r4)     // Catch: java.io.IOException -> L1d java.lang.Throwable -> L3f
            L19:
                r2.close()     // Catch: java.lang.Exception -> L3e
                goto L3e
            L1d:
                r5 = move-exception
                goto L25
            L1f:
                r5 = move-exception
                r2 = r4
                r4 = r5
                goto L40
            L23:
                r5 = move-exception
                r2 = r4
            L25:
                java.lang.String r0 = "AGPSSettings"
                java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L3f
                r1.<init>()     // Catch: java.lang.Throwable -> L3f
                java.lang.String r3 = "Could not open GPS configuration file /etc/gps.conf, e="
                r1.append(r3)     // Catch: java.lang.Throwable -> L3f
                r1.append(r5)     // Catch: java.lang.Throwable -> L3f
                java.lang.String r5 = r1.toString()     // Catch: java.lang.Throwable -> L3f
                android.util.Log.e(r0, r5)     // Catch: java.lang.Throwable -> L3f
                if (r2 == 0) goto L3e
                goto L19
            L3e:
                return r4
            L3f:
                r4 = move-exception
            L40:
                if (r2 == 0) goto L45
                r2.close()     // Catch: java.lang.Exception -> L45
            L45:
                throw r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.AgpsSettings.AgpsSettingsFragment.getPropertyFromGpsConfig(java.lang.String):java.lang.String");
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.mFirstTime = bundle == null;
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.agps_settings);
            this.mContentResolver = getActivity().getContentResolver();
            this.mServer = (EditTextPreference) findPreference("server_addr");
            this.mPort = (EditTextPreference) findPreference("server_port");
            fillUi(false);
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override // androidx.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object obj) {
            DropDownPreference dropDownPreference;
            String key;
            String[] stringArray;
            if ((preference instanceof DropDownPreference) && (key = (dropDownPreference = (DropDownPreference) preference).getKey()) != null) {
                String obj2 = obj.toString();
                int intValue = Integer.valueOf(obj2).intValue();
                if (key.compareTo("agps_network") == 0) {
                    stringArray = getResources().getStringArray(R.array.agps_network_entries);
                    if (intValue == 0) {
                        this.mNetworkType = "HOME";
                    } else if (intValue == 1) {
                        this.mNetworkType = "ALL";
                    }
                    if (intValue == 1) {
                        Toast.makeText(getActivity(), R.string.location_agps_roaming_help, 0).show();
                    }
                } else if (key.compareTo("agps_reset_type") == 0) {
                    stringArray = getResources().getStringArray(R.array.agps_reset_type_entries);
                    if (intValue == 0) {
                        this.mResetType = "HOT";
                    } else if (intValue == 1) {
                        this.mResetType = "WARM";
                    } else {
                        this.mResetType = "COLD";
                    }
                } else if (key.compareTo("agps_pref") == 0) {
                    stringArray = getResources().getStringArray(R.array.agps_si_mode_entries);
                    if (intValue == 0) {
                        this.mAssistedType = "MSB";
                    } else if (intValue == 1) {
                        this.mAssistedType = "MSA";
                    }
                }
                dropDownPreference.setValue(obj2);
                dropDownPreference.setSummary(stringArray[intValue]);
            }
            return true;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override // android.content.SharedPreferences.OnSharedPreferenceChangeListener
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String str) {
            Preference findPreference = findPreference(str);
            if (findPreference != null) {
                findPreference.setSummary(checkNull(sharedPreferences.getString(str, "")));
            }
        }
    }

    public static boolean isAgpsEnabled() {
        return FeatureParser.getBoolean("support_agps", false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        this.mFragment = new AgpsSettingsFragment();
        if (bundle == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.preference_container, this.mFragment).commit();
        }
        this.mContentResolver = getContentResolver();
        sNotSet = getResources().getString(R.string.supl_not_set);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, 1, 0, getResources().getString(R.string.menu_save)).setIcon(17301582);
        menu.add(0, 2, 0, getResources().getString(R.string.menu_restore)).setIcon(17301589);
        return true;
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            this.mFragment.saveAgpsParams();
            return true;
        } else if (itemId != 2) {
            return super.onOptionsItemSelected(menuItem);
        } else {
            this.mFragment.restoreAgpsParam();
            return true;
        }
    }
}
