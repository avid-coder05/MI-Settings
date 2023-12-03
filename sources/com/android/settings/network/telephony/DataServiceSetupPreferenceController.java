package com.android.settings.network.telephony;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.telephony.CarrierConfigManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class DataServiceSetupPreferenceController extends TelephonyBasePreferenceController {
    private CarrierConfigManager mCarrierConfigManager;
    private String mSetupUrl;
    private TelephonyManager mTelephonyManager;

    public DataServiceSetupPreferenceController(Context context, String str) {
        super(context, str);
        this.mCarrierConfigManager = (CarrierConfigManager) context.getSystemService(CarrierConfigManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSetupUrl = Settings.Global.getString(this.mContext.getContentResolver(), "setup_prepaid_data_service_url");
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        PersistableBundle configForSubId = this.mCarrierConfigManager.getConfigForSubId(i);
        return (i == -1 || configForSubId == null || configForSubId.getBoolean("hide_carrier_network_settings_bool") || !this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled() || TextUtils.isEmpty(this.mSetupUrl)) ? 2 : 0;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            if (!TextUtils.isEmpty(this.mSetupUrl)) {
                String subscriberId = this.mTelephonyManager.getSubscriberId();
                if (subscriberId == null) {
                    subscriberId = "";
                }
                this.mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(TextUtils.expandTemplate(this.mSetupUrl, subscriberId).toString())));
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public void init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
