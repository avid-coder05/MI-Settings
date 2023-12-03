package com.android.settings.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.AnnotationSpan;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiWifiWakeupPreferenceController extends TogglePreferenceController implements LifecycleObserver, OnPause, OnResume {
    private static final int DEFAULT_DISABLE_TIME = 15;
    private static final String KEY_ENABLE_WIFI_WAKEUP = "enable_wifi_wakeup";
    private Fragment mFragment;
    boolean mIsWifiWakeupEnabled;
    private final IntentFilter mLocationFilter;
    LocationManager mLocationManager;
    private final BroadcastReceiver mLocationReceiver;
    SwitchPreference mPreference;
    WifiManager mWifiManager;

    public MiuiWifiWakeupPreferenceController(Context context) {
        super(context, KEY_ENABLE_WIFI_WAKEUP);
        this.mIsWifiWakeupEnabled = false;
        this.mLocationReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.MiuiWifiWakeupPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                MiuiWifiWakeupPreferenceController miuiWifiWakeupPreferenceController = MiuiWifiWakeupPreferenceController.this;
                miuiWifiWakeupPreferenceController.updateState(miuiWifiWakeupPreferenceController.mPreference);
            }
        };
        this.mLocationFilter = new IntentFilter("android.location.MODE_CHANGED");
        this.mLocationManager = (LocationManager) context.getSystemService("location");
        this.mWifiManager = (WifiManager) context.getSystemService(WifiManager.class);
    }

    private boolean getWifiScanningEnabled() {
        return this.mWifiManager.isScanAlwaysAvailable();
    }

    private boolean getWifiWakeupEnabled() {
        return this.mIsWifiWakeupEnabled;
    }

    private void showLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setTitle(R.string.wifi_wakeup_title);
        builder.setMessage(R.string.wifi_wakeup_location_summary);
        builder.setPositiveButton(R.string.wifi_wakeup_positive_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiWakeupPreferenceController.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiuiWifiWakeupPreferenceController.this.mFragment.startActivityForResult(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"), 600);
            }
        });
        builder.setNegativeButton(R.string.wifi_wakeup_negative_button, (DialogInterface.OnClickListener) null);
        builder.setCancelable(true);
        builder.create().show();
    }

    private void showScanningDialog() {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.wifi_wakeup_title).setMessage(R.string.wifi_wakeup_scan_summary).setPositiveButton(R.string.wifi_wakeup_positive_button, new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.MiuiWifiWakeupPreferenceController.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ((WifiManager) ((AbstractPreferenceController) MiuiWifiWakeupPreferenceController.this).mContext.getSystemService(WifiManager.class)).setScanAlwaysAvailable(true);
                Toast.makeText(((AbstractPreferenceController) MiuiWifiWakeupPreferenceController.this).mContext, ((AbstractPreferenceController) MiuiWifiWakeupPreferenceController.this).mContext.getString(R.string.wifi_settings_scanning_required_enabled), 0).show();
                MiuiWifiWakeupPreferenceController.this.setWifiWakeupEnabled(true);
                MiuiWifiWakeupPreferenceController miuiWifiWakeupPreferenceController = MiuiWifiWakeupPreferenceController.this;
                miuiWifiWakeupPreferenceController.updateState(miuiWifiWakeupPreferenceController.mPreference);
            }
        }).setNegativeButton(R.string.wifi_wakeup_negative_button, (DialogInterface.OnClickListener) null).setCancelable(true).create().show();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return MiuiWifiAssistFeatureSupport.getWifiWakeupStatus(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    CharSequence getNoLocationSummary() {
        return AnnotationSpan.linkify(this.mContext.getText(R.string.wifi_wakeup_summary_no_location), new AnnotationSpan.LinkInfo("link", null));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return this.mContext.getResources().getQuantityString(R.plurals.miui_wifi_wakeup_summary, 15, 15);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return getWifiWakeupEnabled() && getWifiScanningEnabled() && this.mLocationManager.isLocationEnabled();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public void onActivityResult(int i, int i2) {
        if (i == 600 && this.mLocationManager.isLocationEnabled() && getWifiScanningEnabled()) {
            setWifiWakeupEnabled(true);
            updateState(this.mPreference);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mLocationReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.registerReceiver(this.mLocationReceiver, this.mLocationFilter);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            if (this.mFragment == null) {
                throw new IllegalStateException("No fragment to start activity");
            }
            if (!this.mLocationManager.isLocationEnabled()) {
                showLocationDialog();
                return false;
            } else if (!getWifiScanningEnabled()) {
                showScanningDialog();
                return false;
            }
        }
        setWifiWakeupEnabled(z);
        return true;
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    public void setWifiWakeupEnabled(boolean z) {
        this.mWifiManager.setAutoWakeupEnabled(z);
        this.mIsWifiWakeupEnabled = z;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(preference);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
