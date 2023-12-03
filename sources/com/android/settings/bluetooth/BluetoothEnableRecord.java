package com.android.settings.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import miui.os.Build;

/* loaded from: classes.dex */
public class BluetoothEnableRecord extends DashboardFragment {
    private static String TRACK_BT_RECORD = "track_bt_record";
    private Context mContext;
    PreferenceGroup mDeviceListGroup;
    LocalBluetoothManager mLocalManager;
    private View mRootView;

    private int getScreenHeight() {
        WindowManager windowManager = (WindowManager) getActivity().getSystemService("window");
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private void readyInfo() {
        Drawable drawable;
        try {
            int i = 0;
            for (String str : Settings.Global.getString(getContentResolver(), "Settings.Global.ENABLE_BLUETOOTH_RECORD").split(",")) {
                String[] split = str.split("#");
                if (split.length == 3) {
                    Log.d("BluetoothEnableRecord", "initPackageInfo: " + split[0] + ", " + split[1] + ", " + split[2]);
                    try {
                        drawable = this.mContext.getPackageManager().getApplicationIcon(split[0]);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        drawable = ContextCompat.getDrawable(this.mContext, 17301632);
                    }
                    String appNameByPackageName = getAppNameByPackageName(this.mContext, split[0]);
                    Preference preference = new Preference(this.mContext);
                    preference.setLayoutResource(R.xml.preference_bt_recode);
                    preference.setSummary(split[1] + " " + split[2]);
                    preference.setTitle(appNameByPackageName);
                    preference.setKey(SettingsProvider.ARGS_KEY + i);
                    preference.setOrder(i);
                    preference.setIcon(drawable);
                    this.mDeviceListGroup.addPreference(preference);
                    i++;
                } else {
                    Log.e("BluetoothEnableRecord", "Shouldn't be here!");
                }
            }
        } catch (Exception e2) {
            Log.d("BluetoothEnableRecord", "error when readyInfo", e2);
        }
    }

    private void refresh() {
        ViewGroup viewGroup = (ViewGroup) this.mRootView.findViewById(R.id.prefs_container);
        ViewGroup viewGroup2 = (ViewGroup) this.mRootView.findViewById(R.id.blank_screen);
        if (this.mDeviceListGroup.getPreferenceCount() != 0) {
            viewGroup.setVisibility(0);
            viewGroup2.setVisibility(4);
            return;
        }
        viewGroup.setVisibility(4);
        viewGroup2.setVisibility(0);
    }

    public String getAppNameByPackageName(Context context, String str) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager.getApplicationLabel(packageManager.getApplicationInfo(str, 128)).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return str;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "BluetoothEnableRecord";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.enable_bluetooth_recode;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LocalBluetoothManager localBtManager = Utils.getLocalBtManager(getActivity());
        this.mLocalManager = localBtManager;
        if (localBtManager == null) {
            Log.e("BluetoothEnableRecord", "Bluetooth is not supported on this device");
            return;
        }
        this.mContext = getActivity().getApplicationContext();
        this.mDeviceListGroup = (PreferenceGroup) findPreference("bluetooth_enable_recode");
        readyInfo();
        MiStatInterfaceUtils.trackPreferenceValue(TRACK_BT_RECORD, "on");
        OneTrackInterfaceUtils.trackSwitchEvent(TRACK_BT_RECORD, true);
        HashMap hashMap = new HashMap();
        hashMap.put("btrecord_param", Boolean.TRUE);
        if ("CN".equals(Build.getRegion())) {
            OneTrackInterfaceUtils.track("btrecord_event", hashMap);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.no_record_icon, viewGroup, false);
        this.mRootView = inflate;
        ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
        ViewGroup viewGroup2 = (ViewGroup) this.mRootView.findViewById(R.id.blank_screen);
        int screenHeight = getScreenHeight();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewGroup2.getLayoutParams();
        layoutParams.bottomMargin = (screenHeight - layoutParams.height) / 2;
        viewGroup2.setLayoutParams(layoutParams);
        refresh();
        return this.mRootView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothEnableRecord", "set scan mode connectable");
            defaultAdapter.setScanMode(21);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(null);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            Log.d("BluetoothEnableRecord", "set scan mode connectable and discoverable");
            defaultAdapter.setScanMode(23);
        }
        LocalBluetoothManager localBluetoothManager = this.mLocalManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.setForegroundActivity(getActivity());
        }
    }
}
