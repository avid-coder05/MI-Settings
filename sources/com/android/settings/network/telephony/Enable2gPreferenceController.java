package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PersistableBundle;
import android.telephony.CarrierConfigManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;

/* loaded from: classes2.dex */
public class Enable2gPreferenceController extends TelephonyTogglePreferenceController {
    private static final long BITMASK_2G = 32843;
    private static final String LOG_TAG = "Enable2gPreferenceController";
    private CarrierConfigManager mCarrierConfigManager;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private TelephonyManager mTelephonyManager;

    public Enable2gPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        if (this.mTelephonyManager == null) {
            Log.w(LOG_TAG, "Telephony manager not yet initialized");
            this.mTelephonyManager = (TelephonyManager) this.mContext.getSystemService(TelephonyManager.class);
        }
        return SubscriptionManager.isUsableSubscriptionId(i) && configForSubId != null && !configForSubId.getBoolean("hide_enable_2g_bool") && this.mTelephonyManager.isRadioInterfaceCapabilitySupported("CAPABILITY_USES_ALLOWED_NETWORK_TYPES_BITMASK") ? 0 : 2;
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

    public Enable2gPreferenceController init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        return this;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return (this.mTelephonyManager.getAllowedNetworkTypesForReason(3) & BITMASK_2G) != 0;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        long j;
        if (SubscriptionManager.isUsableSubscriptionId(this.mSubId)) {
            long allowedNetworkTypesForReason = this.mTelephonyManager.getAllowedNetworkTypesForReason(3);
            if (((allowedNetworkTypesForReason & BITMASK_2G) != 0) == z) {
                return false;
            }
            if (z) {
                j = allowedNetworkTypesForReason | BITMASK_2G;
                Log.i(LOG_TAG, "Enabling 2g. Allowed network types: " + j);
            } else {
                j = allowedNetworkTypesForReason & (-32844);
                Log.i(LOG_TAG, "Disabling 2g. Allowed network types: " + j);
            }
            this.mTelephonyManager.setAllowedNetworkTypesForReason(3, j);
            this.mMetricsFeatureProvider.action(this.mContext, 1761, z);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
