package com.android.settings.deviceinfo.aboutphone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class BaseBatteryController extends BasePreferenceController implements LifecycleObserver, OnCreate, OnDestroy {
    public static final String ACTION_QUICK_CHARGE_TYPE = "miui.intent.action.ACTION_QUICK_CHARGE_TYPE";
    private BroadcastReceiver mBatteryInfoReceiver;
    protected ValuePreference mPreference;

    public BaseBatteryController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mBatteryInfoReceiver = new BroadcastReceiver() { // from class: com.android.settings.deviceinfo.aboutphone.BaseBatteryController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TextUtils.equals(intent.getAction(), "android.intent.action.BATTERY_CHANGED")) {
                    BaseBatteryController.this.onBatteryChange(intent);
                } else if (BaseBatteryController.ACTION_QUICK_CHARGE_TYPE.equals(intent.getAction())) {
                    BaseBatteryController.this.wireQuickCharge(intent);
                }
            }
        };
        lifecycle.addObserver(this);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
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

    public void onBatteryChange(Intent intent) {
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction(ACTION_QUICK_CHARGE_TYPE);
        intentFilter.setPriority(1001);
        this.mContext.registerReceiver(this.mBatteryInfoReceiver, intentFilter);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mContext.unregisterReceiver(this.mBatteryInfoReceiver);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public void wireQuickCharge(Intent intent) {
    }
}
