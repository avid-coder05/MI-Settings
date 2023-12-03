package com.android.settings.development;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class FiveGNrcaPreferenceController extends BasePreferenceController implements LifecycleObserver, OnResume, OnPause {
    private static final String NRCA_SWITCH_KEY = "nrca_switch";
    private static final int PHONE_COUNT_MAX = 2;
    private static final String SIM_NRCA_CATEGORY_KEY = "_nrca_category_key";
    private static final String TAG = "FiveGNrcaPreferenceController";
    private boolean mAirplaneMode;
    private Context mContext;
    private final BroadcastReceiver mNrcaReceiver;
    private NrcaSwitchView[] mNrcaSwitchView;
    private PreferenceCategory[] mSwitchCategory;

    public FiveGNrcaPreferenceController(Context context, String str) {
        super(context, str);
        this.mSwitchCategory = new PreferenceCategory[2];
        this.mNrcaSwitchView = new NrcaSwitchView[2];
        this.mAirplaneMode = false;
        this.mNrcaReceiver = new BroadcastReceiver() { // from class: com.android.settings.development.FiveGNrcaPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                String action = intent.getAction();
                if (!action.equals("android.intent.action.AIRPLANE_MODE")) {
                    if (action.equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                        FiveGNrcaPreferenceController.this.updatePreference();
                        return;
                    }
                    return;
                }
                FiveGNrcaPreferenceController.this.mAirplaneMode = intent.getBooleanExtra("state", false);
                Log.d(FiveGNrcaPreferenceController.TAG, "ACTION_AIRPLANE_MODE_CHANGED: " + String.valueOf(FiveGNrcaPreferenceController.this.mAirplaneMode));
                FiveGNrcaPreferenceController.this.updatePreference();
            }
        };
        this.mContext = context;
        this.mAirplaneMode = Settings.System.getInt(context.getContentResolver(), "airplane_mode_on", -1) == 1;
    }

    private int getPhoneCount() {
        return TelephonyManager.getDefault().getPhoneCount();
    }

    private SubscriptionInfo getSubscriptionInfoBySlot(int i) {
        List<SubscriptionInfo> availableSubscriptionInfoList = SubscriptionManager.getDefault().getAvailableSubscriptionInfoList();
        if (availableSubscriptionInfoList == null) {
            return null;
        }
        for (SubscriptionInfo subscriptionInfo : availableSubscriptionInfoList) {
            if (subscriptionInfo.getSlotId() == i) {
                return subscriptionInfo;
            }
        }
        return null;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        this.mContext.registerReceiver(this.mNrcaReceiver, intentFilter);
        Log.d(TAG, "register broadcastreceiver");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreference() {
        for (int i = 0; i < getPhoneCount(); i++) {
            boolean hasIccCard = TelephonyManager.getDefault().hasIccCard(i);
            this.mSwitchCategory[i].setVisible(hasIccCard);
            Log.d(TAG, "updatePreference: isSimInsert = " + hasIccCard);
            if (hasIccCard) {
                SubscriptionInfo subscriptionInfoBySlot = getSubscriptionInfoBySlot(i);
                if (subscriptionInfoBySlot != null) {
                    String charSequence = getPhoneCount() == 1 ? subscriptionInfoBySlot.getDisplayName().toString() : String.format(this.mContext.getString(R.string.sim_nrca_title), Integer.valueOf(i + 1), subscriptionInfoBySlot.getDisplayName().toString());
                    this.mSwitchCategory[i].setTitle(charSequence);
                    Log.d(TAG, "mSwitchCategoryTitle: " + charSequence);
                }
                NrcaSwitchView[] nrcaSwitchViewArr = this.mNrcaSwitchView;
                if (nrcaSwitchViewArr[i] == null) {
                    nrcaSwitchViewArr[i] = new NrcaSwitchView(this.mSwitchCategory[i], this.mContext, i);
                }
                this.mNrcaSwitchView[i].updateNrcaButtonUI();
            }
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        for (int i = 0; i < 2; i++) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("sim" + i + SIM_NRCA_CATEGORY_KEY);
            preferenceScreen.addPreference(preferenceCategory);
            SwitchPreference switchPreference = new SwitchPreference(this.mContext);
            switchPreference.setTitle(R.string.nrca_switch_title);
            switchPreference.setKey(NRCA_SWITCH_KEY + i);
            preferenceCategory.addPreference(switchPreference);
            this.mSwitchCategory[i] = preferenceCategory;
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

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        Log.d(TAG, "onPause");
        this.mContext.unregisterReceiver(this.mNrcaReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        Log.d(TAG, "onResume");
        registerBroadcastReceiver();
        updatePreference();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
