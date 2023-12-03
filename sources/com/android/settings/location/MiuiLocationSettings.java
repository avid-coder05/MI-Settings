package com.android.settings.location;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.AgpsSettings;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.RadioButtonPreference;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiLocationSettings extends DashboardFragment implements Preference.OnPreferenceChangeListener, RadioButtonPreference.OnClickListener {
    private Preference mAGpsParas;
    private boolean mAgpsEnabled;
    private CheckBoxPreference mAgpsRoaming;
    private CheckBoxPreference mAssistedGps;
    private RadioButtonPreference mBatterySaving;
    private int mCurrentMode;
    private SharedPreferences.Editor mEditor;
    private boolean mHasGpsFeature;
    private RadioButtonPreference mHighAccuracy;
    private LocationManager mLocationManager;
    private Preference mLocationMode;
    private UserHandle mManagedProfile;
    private RestrictedSwitchPreference mManagedProfileSwitch;
    private BroadcastReceiver mModeChangeReceiver;
    private RadioButtonPreference mSensorsOnly;
    private SharedPreferences mSharedSP;
    private RestrictedSwitchPreference mSwitch;
    private UserManager mUm;
    private boolean mValidListener;
    private boolean mActive = false;
    private Preference.OnPreferenceClickListener mManagedProfileSwitchClickListener = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.location.MiuiLocationSettings.5
        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            boolean isChecked = MiuiLocationSettings.this.mManagedProfileSwitch.isChecked();
            MiuiLocationSettings.this.mUm.setUserRestriction("no_share_location", !isChecked, MiuiLocationSettings.this.mManagedProfile);
            MiuiLocationSettings.this.mManagedProfileSwitch.setSummary(isChecked ? R.string.switch_on_text : R.string.switch_off_text);
            return true;
        }
    };

    /* renamed from: com.android.settings.location.MiuiLocationSettings$4  reason: invalid class name */
    /* loaded from: classes.dex */
    class AnonymousClass4 implements Comparator<Preference> {
        @Override // java.util.Comparator
        public int compare(Preference preference, Preference preference2) {
            return preference.getTitle().toString().compareTo(preference2.getTitle().toString());
        }
    }

    private void addLocationServices(Context context, PreferenceScreen preferenceScreen, boolean z) {
    }

    private void changeManagedProfileLocationAccessStatus(boolean z) {
        RestrictedSwitchPreference restrictedSwitchPreference = this.mManagedProfileSwitch;
        if (restrictedSwitchPreference == null) {
            return;
        }
        restrictedSwitchPreference.setOnPreferenceClickListener(null);
        RestrictedLockUtils.EnforcedAdmin shareLocationEnforcedAdmin = getShareLocationEnforcedAdmin(this.mManagedProfile.getIdentifier());
        boolean isManagedProfileRestrictedByBase = isManagedProfileRestrictedByBase();
        if (!isManagedProfileRestrictedByBase && shareLocationEnforcedAdmin != null) {
            this.mManagedProfileSwitch.setDisabledByAdmin(shareLocationEnforcedAdmin);
            this.mManagedProfileSwitch.setChecked(false);
            return;
        }
        this.mManagedProfileSwitch.setEnabled(z);
        int i = R.string.switch_off_text;
        if (z) {
            this.mManagedProfileSwitch.setChecked(!isManagedProfileRestrictedByBase);
            if (!isManagedProfileRestrictedByBase) {
                i = R.string.switch_on_text;
            }
            this.mManagedProfileSwitch.setOnPreferenceClickListener(this.mManagedProfileSwitchClickListener);
        } else {
            this.mManagedProfileSwitch.setChecked(false);
        }
        this.mManagedProfileSwitch.setSummary(i);
    }

    private PreferenceScreen createPreferenceHierarchy() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            preferenceScreen.removeAll();
        }
        addPreferencesFromResource(R.xml.miui_location_settings);
        PreferenceScreen preferenceScreen2 = getPreferenceScreen();
        AbstractPreferenceController use = use(RecentLocationAccessPreferenceController.class);
        if (use != null) {
            use.displayPreference(preferenceScreen2);
            if (use.isAvailable()) {
                use.updateState(preferenceScreen2.findPreference(use.getPreferenceKey()));
            } else {
                removePreference(use.getPreferenceKey());
            }
        }
        setupManagedProfileCategory(preferenceScreen2);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("location_toggle");
        this.mSwitch = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceChangeListener(this);
        this.mLocationMode = preferenceScreen2.findPreference("location_mode");
        boolean hasSystemFeature = getPackageManager().hasSystemFeature("android.hardware.location.gps");
        this.mHasGpsFeature = hasSystemFeature;
        if (hasSystemFeature) {
            this.mHighAccuracy = (RadioButtonPreference) preferenceScreen2.findPreference("high_accuracy");
            this.mBatterySaving = (RadioButtonPreference) preferenceScreen2.findPreference("battery_saving");
            this.mSensorsOnly = (RadioButtonPreference) preferenceScreen2.findPreference("sensors_only");
            this.mHighAccuracy.setOnClickListener(this);
            this.mBatterySaving.setOnClickListener(this);
            this.mSensorsOnly.setOnClickListener(this);
        } else {
            preferenceScreen2.removePreference(this.mLocationMode);
            this.mLocationMode = null;
        }
        this.mAgpsEnabled = AgpsSettings.isAgpsEnabled();
        this.mAssistedGps = (CheckBoxPreference) preferenceScreen2.findPreference("assisted_gps");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen2.findPreference("agps_roaming");
        this.mAgpsRoaming = checkBoxPreference;
        boolean z = false;
        checkBoxPreference.setChecked(MiuiUtils.getInstance().getAgpsRoaming(this.mLocationManager) == 1);
        this.mAGpsParas = preferenceScreen2.findPreference("assisted_gps_params");
        if (!this.mAgpsEnabled) {
            preferenceScreen2.removePreference(this.mAssistedGps);
            preferenceScreen2.removePreference(this.mAGpsParas);
            preferenceScreen2.removePreference(this.mAgpsRoaming);
        } else if (FeatureParser.getBoolean("support_agps_paras", false)) {
            preferenceScreen2.removePreference(this.mAgpsRoaming);
        } else if (FeatureParser.getBoolean("support_agps_roaming", false)) {
            preferenceScreen2.removePreference(this.mAGpsParas);
        }
        CheckBoxPreference checkBoxPreference2 = this.mAssistedGps;
        if (checkBoxPreference2 != null) {
            checkBoxPreference2.setChecked(Settings.Global.getInt(getContentResolver(), "assisted_gps_enabled", 0) == 1);
        }
        UserHandle userHandle = this.mManagedProfile;
        if (userHandle != null && this.mUm.hasUserRestriction("no_share_location", userHandle)) {
            z = true;
        }
        addLocationServices(getActivity(), preferenceScreen2, z);
        setHasOptionsMenu(true);
        refreshLocationMode();
        return preferenceScreen2;
    }

    private boolean isManagedProfileRestrictedByBase() {
        UserHandle userHandle = this.mManagedProfile;
        if (userHandle == null) {
            return false;
        }
        return this.mUm.hasBaseUserRestriction("no_share_location", userHandle);
    }

    private boolean isRestricted() {
        return ((UserManager) getActivity().getSystemService("user")).hasUserRestriction("no_share_location");
    }

    private void setupManagedProfileCategory(PreferenceScreen preferenceScreen) {
        UserHandle managedProfile = Utils.getManagedProfile(this.mUm);
        this.mManagedProfile = managedProfile;
        if (managedProfile == null) {
            removePreference("managed_profile_location_switch");
            this.mManagedProfileSwitch = null;
            return;
        }
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference("managed_profile_location_switch");
        this.mManagedProfileSwitch = restrictedSwitchPreference;
        restrictedSwitchPreference.setOnPreferenceClickListener(null);
    }

    private void updateRadioButtons(RadioButtonPreference radioButtonPreference) {
        RadioButtonPreference radioButtonPreference2 = this.mHighAccuracy;
        if (radioButtonPreference2 == null) {
            return;
        }
        if (radioButtonPreference == null) {
            radioButtonPreference2.setChecked(false);
            this.mBatterySaving.setChecked(false);
            this.mSensorsOnly.setChecked(false);
        } else if (radioButtonPreference == radioButtonPreference2) {
            radioButtonPreference2.setChecked(true);
            this.mBatterySaving.setChecked(false);
            this.mSensorsOnly.setChecked(false);
        } else if (radioButtonPreference == this.mBatterySaving) {
            radioButtonPreference2.setChecked(false);
            this.mBatterySaving.setChecked(true);
            this.mSensorsOnly.setChecked(false);
        } else if (radioButtonPreference == this.mSensorsOnly) {
            radioButtonPreference2.setChecked(false);
            this.mBatterySaving.setChecked(false);
            this.mSensorsOnly.setChecked(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MiuiLocationSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.miui_location_settings;
    }

    RestrictedLockUtils.EnforcedAdmin getShareLocationEnforcedAdmin(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_share_location", i);
        return checkIfRestrictionEnforced == null ? RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_config_location", i) : checkIfRestrictionEnforced;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mModeChangeReceiver = new BroadcastReceiver() { // from class: com.android.settings.location.MiuiLocationSettings.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (Log.isLoggable("MiuiLocationSettings", 3)) {
                    Log.d("MiuiLocationSettings", "Received location mode change intent: " + intent);
                }
                MiuiLocationSettings.this.refreshLocationMode();
            }
        };
        this.mLocationManager = (LocationManager) getActivity().getSystemService("location");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("location_last_mode", 0);
        this.mSharedSP = sharedPreferences;
        this.mEditor = sharedPreferences.edit();
        UserManager userManager = (UserManager) getSystemService("user");
        this.mUm = userManager;
        this.mManagedProfile = Utils.getManagedProfile(userManager);
    }

    public void onModeChanged(int i, boolean z) {
        RestrictedLockUtils.EnforcedAdmin shareLocationEnforcedAdmin = getShareLocationEnforcedAdmin(UserHandle.myUserId());
        boolean hasBaseUserRestriction = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_share_location", UserHandle.myUserId());
        boolean z2 = false;
        boolean z3 = i != 0;
        if (hasBaseUserRestriction || shareLocationEnforcedAdmin == null) {
            this.mSwitch.setEnabled(!z);
        } else {
            this.mSwitch.setDisabledByAdmin(shareLocationEnforcedAdmin);
        }
        if (z3 != this.mSwitch.isChecked()) {
            if (this.mValidListener) {
                this.mSwitch.setOnPreferenceChangeListener(null);
            }
            this.mSwitch.setChecked(z3);
            if (this.mValidListener) {
                this.mSwitch.setOnPreferenceChangeListener(this);
            }
        }
        Preference preference = this.mLocationMode;
        if (preference == null) {
            return;
        }
        if (i == 0) {
            preference.setSummary(R.string.location_mode_location_off_title);
            updateRadioButtons(null);
        } else if (i == 1) {
            preference.setSummary(R.string.location_mode_sensors_only_title);
            updateRadioButtons(this.mSensorsOnly);
        } else if (i == 2) {
            preference.setSummary(R.string.location_mode_battery_saving_title);
            updateRadioButtons(this.mBatterySaving);
        } else if (i == 3) {
            preference.setSummary(R.string.location_mode_high_accuracy_title);
            updateRadioButtons(this.mHighAccuracy);
        }
        if (i != 0) {
            this.mEditor.putInt("last_mode", i);
            this.mEditor.commit();
        }
        this.mLocationMode.setEnabled(z3 && !z);
        if (i != 0 && !z) {
            z2 = true;
        }
        this.mHighAccuracy.setEnabled(z2);
        this.mBatterySaving.setEnabled(z2);
        this.mSensorsOnly.setEnabled(z2);
        changeManagedProfileLocationAccessStatus(z2);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        try {
            getActivity().unregisterReceiver(this.mModeChangeReceiver);
        } catch (RuntimeException e) {
            Log.e("MiuiLocationSettings", "Error", e);
        }
        super.onPause();
        this.mActive = false;
        this.mValidListener = false;
        this.mSwitch.setOnPreferenceChangeListener(null);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference.getKey().equals("location_toggle")) {
            if (((Boolean) obj).booleanValue()) {
                setLocationMode(this.mSharedSP.getInt("last_mode", this.mHasGpsFeature ? 3 : 2));
            } else {
                setLocationMode(0);
            }
            return false;
        }
        return false;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Properties properties;
        FileInputStream fileInputStream;
        ContentResolver contentResolver = getContentResolver();
        CheckBoxPreference checkBoxPreference = this.mAssistedGps;
        if (preference != checkBoxPreference) {
            CheckBoxPreference checkBoxPreference2 = this.mAgpsRoaming;
            if (preference == checkBoxPreference2) {
                if (checkBoxPreference2.isChecked()) {
                    new AlertDialog.Builder(getActivity()).setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.location.MiuiLocationSettings.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MiuiUtils.getInstance().setAgpsRoaming(MiuiLocationSettings.this.mLocationManager, 0);
                            MiuiLocationSettings.this.mAgpsRoaming.setChecked(false);
                        }
                    }).setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.location.MiuiLocationSettings.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MiuiUtils.getInstance().setAgpsRoaming(MiuiLocationSettings.this.mLocationManager, 1);
                            MiuiLocationSettings.this.mAgpsRoaming.setChecked(true);
                        }
                    }).setTitle(getString(R.string.agps_roaming_dia_title)).setMessage(getString(R.string.agps_roaming_dia)).create().show();
                    return true;
                }
                MiuiUtils.getInstance().setAgpsRoaming(this.mLocationManager, 0);
                return true;
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
        if (checkBoxPreference.isChecked() && (Settings.Global.getString(contentResolver, "assisted_gps_supl_host") == null || Settings.Global.getString(contentResolver, "assisted_gps_supl_port") == null)) {
            FileInputStream fileInputStream2 = null;
            try {
                try {
                    try {
                        properties = new Properties();
                        fileInputStream = new FileInputStream(new File("/etc/gps.conf"));
                    } catch (IOException e) {
                        e = e;
                    }
                } catch (Throwable th) {
                    th = th;
                }
            } catch (Exception unused) {
            }
            try {
                properties.load(fileInputStream);
                Settings.Global.putString(contentResolver, "assisted_gps_supl_host", properties.getProperty("SUPL_HOST", null));
                Settings.Global.putString(contentResolver, "assisted_gps_supl_port", properties.getProperty("SUPL_PORT", null));
                fileInputStream.close();
            } catch (IOException e2) {
                e = e2;
                fileInputStream2 = fileInputStream;
                Log.e("LocationSettings", "Could not open GPS configuration file /etc/gps.conf, e=" + e);
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                Settings.Global.putInt(contentResolver, "assisted_gps_enabled", this.mAssistedGps.isChecked() ? 1 : 0);
                return true;
            } catch (Throwable th2) {
                th = th2;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (Exception unused2) {
                    }
                }
                throw th;
            }
        }
        Settings.Global.putInt(contentResolver, "assisted_gps_enabled", this.mAssistedGps.isChecked() ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        setLocationMode(radioButtonPreference == this.mHighAccuracy ? 3 : radioButtonPreference == this.mBatterySaving ? 2 : radioButtonPreference == this.mSensorsOnly ? 1 : 0);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mActive = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.location.MODE_CHANGED");
        getActivity().registerReceiver(this.mModeChangeReceiver, intentFilter);
        this.mValidListener = true;
        createPreferenceHierarchy();
    }

    public void refreshLocationMode() {
        if (this.mActive) {
            int i = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
            this.mCurrentMode = i;
            if (Log.isLoggable("MiuiLocationSettings", 4)) {
                Log.i("MiuiLocationSettings", "Location mode has been changed");
            }
            onModeChanged(i, isRestricted());
        }
    }

    public void setLocationMode(int i) {
        if (isRestricted()) {
            if (Log.isLoggable("MiuiLocationSettings", 4)) {
                Log.i("MiuiLocationSettings", "Restricted user, not setting location mode");
            }
            int i2 = Settings.Secure.getInt(getContentResolver(), "location_mode", 0);
            if (this.mActive) {
                onModeChanged(i2, true);
                return;
            }
            return;
        }
        Intent intent = new Intent("com.android.settings.location.MODE_CHANGING");
        intent.putExtra("CURRENT_MODE", this.mCurrentMode);
        intent.putExtra("NEW_MODE", i);
        getActivity().sendBroadcast(intent, "android.permission.WRITE_SECURE_SETTINGS");
        Settings.Secure.putInt(getContentResolver(), "location_mode", i);
        refreshLocationMode();
    }
}
