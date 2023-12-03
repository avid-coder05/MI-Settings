package com.android.settings.wireless;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;

/* loaded from: classes2.dex */
public class MiuiNFCController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    public static final String KEY_MIUI_NFC = "miui_nfc";
    private static final String TAG = "MiuiNFCController";
    private Context mContext;
    private final IntentFilter mIntentFilter;
    private NfcAdapter mNfcAdapter;
    private Preference mPreference;
    private final BroadcastReceiver mReceiver;

    public MiuiNFCController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wireless.MiuiNFCController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Log.d(MiuiNFCController.TAG, "onReceive: " + intent.getAction() + "; mPreference: " + MiuiNFCController.this.mPreference);
                String action = intent.getAction();
                if (MiuiNFCController.this.mPreference == null) {
                    return;
                }
                if ("android.nfc.action.ADAPTER_STATE_CHANGED".equals(action) && intent.getIntExtra("android.nfc.extra.ADAPTER_STATE", 1) == 3) {
                    MiuiNFCController.this.mPreference.setSummary(R.string.accessibility_feature_state_on);
                } else {
                    MiuiNFCController.this.mPreference.setSummary(R.string.accessibility_feature_state_off);
                }
            }
        };
        this.mContext = context;
        lifecycle.addObserver(this);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (!isNfcAvailable()) {
            this.mIntentFilter = null;
            return;
        }
        this.mIntentFilter = new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED");
        Log.i(TAG, "MiuiNFCController: mNfcAdapter = " + this.mNfcAdapter);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(KEY_MIUI_NFC);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isNfcAvailable() ? 0 : 3;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_MIUI_NFC;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        if (this.mNfcAdapter == null) {
            return this.mContext.getResources().getString(R.string.accessibility_feature_state_off);
        }
        return this.mContext.getResources().getString(this.mNfcAdapter.getAdapterState() == 3 ? R.string.accessibility_feature_state_on : R.string.accessibility_feature_state_off);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    public boolean isNfcAvailable() {
        return this.mNfcAdapter != null && SettingsFeatures.isNeedShowMiuiNFC();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (isNfcAvailable()) {
            this.mContext.registerReceiver(this.mReceiver, this.mIntentFilter);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (isNfcAvailable()) {
            try {
                this.mContext.unregisterReceiver(this.mReceiver);
            } catch (IllegalArgumentException unused) {
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
