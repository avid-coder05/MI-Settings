package com.android.settings.network.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

/* loaded from: classes2.dex */
public class Enabled5GPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private static final String KEY_HIDE_ENABLED_5G_BOOL = "hide_enabled_5g_bool";
    private static final String TAG = "Enable5g";
    private PersistableBundle mCarrierConfig;
    private CarrierConfigManager mCarrierConfigManager;
    private final BroadcastReceiver mDefaultDataChangedReceiver;
    Preference mPreference;
    private ContentObserver mPreferredNetworkModeObserver;
    private ContentObserver mSubsidySettingsObserver;
    private TelephonyManager mTelephonyManager;

    public Enabled5GPreferenceController(Context context, String str) {
        super(context, str);
        this.mDefaultDataChangedReceiver = new BroadcastReceiver() { // from class: com.android.settings.network.telephony.Enabled5GPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (Enabled5GPreferenceController.this.mPreference != null) {
                    Log.d(Enabled5GPreferenceController.TAG, "DDS is changed");
                    Enabled5GPreferenceController enabled5GPreferenceController = Enabled5GPreferenceController.this;
                    enabled5GPreferenceController.updateState(enabled5GPreferenceController.mPreference);
                }
            }
        };
        this.mPreferredNetworkModeObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.network.telephony.Enabled5GPreferenceController.2
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                if (Enabled5GPreferenceController.this.mPreference != null) {
                    Log.d(Enabled5GPreferenceController.TAG, "mPreferredNetworkModeObserver#onChange");
                    Enabled5GPreferenceController enabled5GPreferenceController = Enabled5GPreferenceController.this;
                    enabled5GPreferenceController.updateState(enabled5GPreferenceController.mPreference);
                }
            }
        };
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
    }

    private boolean isNrNetworkModeType(long j) {
        return checkSupportedRadioBitmask(j, PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED);
    }

    boolean checkSupportedRadioBitmask(long j, long j2) {
        Log.d(TAG, "supportedRadioBitmask: " + j);
        return (j2 & j) > 0;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        init(i);
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (configForSubId == null || this.mTelephonyManager == null) {
            return 2;
        }
        return SubscriptionManager.isValidSubscriptionId(i) && !configForSubId.getBoolean(KEY_HIDE_ENABLED_5G_BOOL) && (((this.mTelephonyManager.getAllowedNetworkTypes() & PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED) > 0L ? 1 : ((this.mTelephonyManager.getAllowedNetworkTypes() & PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED) == 0L ? 0 : -1)) > 0) && (SubscriptionManager.getDefaultDataSubscriptionId() == i) ? 0 : 2;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public Enabled5GPreferenceController init(int i) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && this.mSubId == i) {
            return this;
        }
        this.mSubId = i;
        this.mCarrierConfig = this.mCarrierConfigManager.getConfigForSubId(i);
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        return this;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isNrNetworkModeType(MobileNetworkUtils.getRafFromNetworkType(Settings.Global.getInt(this.mContext.getContentResolver(), "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE)));
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("preferred_network_mode" + this.mSubId), true, this.mPreferredNetworkModeObserver);
        this.mContext.registerReceiver(this.mDefaultDataChangedReceiver, new IntentFilter("android.intent.action.ACTION_DEFAULT_DATA_SUBSCRIPTION_CHANGED"));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mPreferredNetworkModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mPreferredNetworkModeObserver);
        }
        BroadcastReceiver broadcastReceiver = this.mDefaultDataChangedReceiver;
        if (broadcastReceiver != null) {
            this.mContext.unregisterReceiver(broadcastReceiver);
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        long rafFromNetworkType;
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            int i = Settings.Global.getInt(this.mContext.getContentResolver(), "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE);
            if (23 != i) {
                long rafFromNetworkType2 = MobileNetworkUtils.getRafFromNetworkType(i);
                rafFromNetworkType = z ? rafFromNetworkType2 | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED : rafFromNetworkType2 & (-524289);
            } else {
                rafFromNetworkType = MobileNetworkUtils.getRafFromNetworkType(11);
            }
            Settings.Global.putInt(this.mContext.getContentResolver(), "preferred_network_mode" + this.mSubId, MobileNetworkUtils.getNetworkTypeFromRaf((int) rafFromNetworkType));
            if (this.mTelephonyManager.setPreferredNetworkTypeBitmask(rafFromNetworkType)) {
                Log.d(TAG, "setPreferredNetworkTypeBitmask");
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        SwitchPreference switchPreference = (SwitchPreference) preference;
        switchPreference.setVisible(isAvailable());
        switchPreference.setChecked(isNrNetworkModeType(MobileNetworkUtils.getRafFromNetworkType(Settings.Global.getInt(this.mContext.getContentResolver(), "preferred_network_mode" + this.mSubId, TelephonyManager.DEFAULT_PREFERRED_NETWORK_MODE))));
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
