package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import com.android.settings.Utils;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class CallsDefaultSubscriptionController extends DefaultSubscriptionController {
    public CallsDefaultSubscriptionController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected int getDefaultSubscriptionId() {
        return SubscriptionManager.getDefaultVoiceSubscriptionId();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected SubscriptionInfo getDefaultSubscriptionInfo() {
        return this.mManager.getActiveSubscriptionInfo(getDefaultSubscriptionId());
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return Utils.isProviderModelEnabled(this.mContext) ? MobileNetworkUtils.getPreferredStatus(this.mContext, this.mManager, true) : super.getSummary();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController
    protected void setDefaultSubscription(int i) {
        this.mManager.setDefaultVoiceSubscriptionId(i);
    }

    @Override // com.android.settings.network.telephony.DefaultSubscriptionController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
