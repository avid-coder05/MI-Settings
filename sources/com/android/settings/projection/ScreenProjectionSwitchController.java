package com.android.settings.projection;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.wifi.SlaveWifiUtils;
import com.milink.api.v1.MiLinkClientScanListCallback;
import com.milink.api.v1.MilinkClientManager;

/* loaded from: classes2.dex */
public class ScreenProjectionSwitchController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnStart, OnStop, OnDestroy {
    public static final String TAG = "ScreenProjectionSwitchController";
    private boolean isHotSpotOn;
    private ContentObserver mContentObserver;
    private ContentResolver mContentResolver;
    private Handler mHandler;
    private MilinkClientManager mMananger;
    private CheckBoxPreference mScreenProjection;
    private WifiManager.SoftApCallback mSoftApCallback;
    private final WifiManager mWifiManager;

    public ScreenProjectionSwitchController(Context context, String str) {
        super(context, str);
        this.isHotSpotOn = false;
        this.mSoftApCallback = new WifiManager.SoftApCallback() { // from class: com.android.settings.projection.ScreenProjectionSwitchController.1
            public void onStateChanged(int i, int i2) {
                ScreenProjectionSwitchController.this.setWifiApState(i);
            }
        };
        this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        this.mHandler = new Handler();
    }

    private boolean dealScreenProjectionStateChange(boolean z) {
        if (z && this.isHotSpotOn) {
            ToastUtil.show(this.mContext, R.string.close_hotspot_hint, 1);
            this.mContext.getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.projection.ScreenProjectionSwitchController.3
                @Override // java.lang.Runnable
                public void run() {
                    ScreenProjectionSwitchController.this.updateSwitchState();
                }
            }, 500L);
            return false;
        } else if (z && new SlaveWifiUtils(this.mContext).isSlaveWifiEnabled()) {
            ToastUtil.show(this.mContext, R.string.close_slave_wifi_hint, 1);
            this.mContext.getMainThreadHandler().postDelayed(new Runnable() { // from class: com.android.settings.projection.ScreenProjectionSwitchController.4
                @Override // java.lang.Runnable
                public void run() {
                    ScreenProjectionSwitchController.this.updateSwitchState();
                }
            }, 500L);
            return false;
        } else {
            if (z) {
                Log.i(TAG, "MilinkClientManager showScanList");
                this.mMananger.showScanList(new MiLinkClientScanListCallback() { // from class: com.android.settings.projection.ScreenProjectionSwitchController.5
                    @Override // com.milink.api.v1.MiLinkClientScanListCallback
                    public void onConnectFail(String str, String str2) {
                        Log.d(ScreenProjectionSwitchController.TAG, "onConnectFail:" + str + "/" + str2);
                    }

                    @Override // com.milink.api.v1.MiLinkClientScanListCallback
                    public void onConnectSuccess(String str, String str2) {
                        Log.d(ScreenProjectionSwitchController.TAG, "onConnectSuccess:" + str + "/" + str2);
                    }

                    @Override // com.milink.api.v1.MiLinkClientScanListCallback
                    public void onSelectDevice(String str, String str2, String str3) {
                        Log.d(ScreenProjectionSwitchController.TAG, "onSelectDevice:" + str + "/" + str2 + "/" + str3);
                    }
                }, 1);
            } else {
                Log.i(TAG, "MilinkClientManager disconnectWifiDisplay");
                this.mMananger.disconnectWifiDisplay();
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiApState(int i) {
        this.isHotSpotOn = i == 13;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSwitchState() {
        this.mScreenProjection.setChecked(MiuiSettings.Secure.getBoolean(this.mContentResolver, "screen_project_in_screening", false));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preferenceScreen.findPreference(getPreferenceKey());
            this.mScreenProjection = checkBoxPreference;
            checkBoxPreference.setOnPreferenceChangeListener(this);
            this.mContentResolver = this.mContext.getContentResolver();
            Uri uriFor = Settings.Secure.getUriFor("screen_project_in_screening");
            ContentObserver contentObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.projection.ScreenProjectionSwitchController.2
                @Override // android.database.ContentObserver
                public void onChange(boolean z) {
                    super.onChange(z);
                    ScreenProjectionSwitchController.this.updateSwitchState();
                }
            };
            this.mContentObserver = contentObserver;
            this.mContentResolver.registerContentObserver(uriFor, false, contentObserver);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        ContentObserver contentObserver;
        if (!isAvailable() || (contentObserver = this.mContentObserver) == null) {
            return;
        }
        this.mContentResolver.unregisterContentObserver(contentObserver);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference.getKey().equals(getPreferenceKey())) {
            dealScreenProjectionStateChange(((Boolean) obj).booleanValue());
            return true;
        }
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (isAvailable()) {
            MilinkClientManager milinkClientManager = new MilinkClientManager(this.mContext);
            this.mMananger = milinkClientManager;
            milinkClientManager.open();
            this.mWifiManager.registerSoftApCallback(new HandlerExecutor(this.mHandler), this.mSoftApCallback);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        MilinkClientManager milinkClientManager;
        if (!isAvailable() || (milinkClientManager = this.mMananger) == null) {
            return;
        }
        milinkClientManager.close();
        this.mMananger = null;
        this.mWifiManager.unregisterSoftApCallback(this.mSoftApCallback);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (isAvailable()) {
            updateSwitchState();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
