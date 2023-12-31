package com.android.settings.network.telephony.cdma;

import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.slices.SliceBackgroundWorker;

/* loaded from: classes2.dex */
public class CdmaSubscriptionPreferenceController extends CdmaBasePreferenceController implements Preference.OnPreferenceChangeListener {
    private static final String TYPE_NV = "NV";
    private static final String TYPE_RUIM = "RUIM";
    ListPreference mPreference;

    public CdmaSubscriptionPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    boolean deviceSupportsNvAndRuim() {
        boolean z;
        boolean z2;
        String rilSubscriptionTypes = getRilSubscriptionTypes();
        if (TextUtils.isEmpty(rilSubscriptionTypes)) {
            z = false;
            z2 = false;
        } else {
            z = false;
            z2 = false;
            for (String str : rilSubscriptionTypes.split(",")) {
                String trim = str.trim();
                if (trim.equalsIgnoreCase(TYPE_NV)) {
                    z = true;
                } else if (trim.equalsIgnoreCase(TYPE_RUIM)) {
                    z2 = true;
                }
            }
        }
        return z && z2;
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return (MobileNetworkUtils.isCdmaOptions(this.mContext, i) && deviceSupportsNvAndRuim()) ? 0 : 2;
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected String getRilSubscriptionTypes() {
        return SystemProperties.get("ril.subscription.types");
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        int parseInt = Integer.parseInt((String) obj);
        try {
            this.mTelephonyManager.setCdmaSubscriptionMode(parseInt);
            Settings.Global.putInt(this.mContext.getContentResolver(), "subscription_mode", parseInt);
            return true;
        } catch (IllegalStateException unused) {
            return false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ListPreference listPreference = (ListPreference) preference;
        setVisible(listPreference, getAvailabilityStatus() == 0);
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "subscription_mode", 0);
        if (i != -1) {
            listPreference.setValue(Integer.toString(i));
        }
    }

    @Override // com.android.settings.network.telephony.cdma.CdmaBasePreferenceController, com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
