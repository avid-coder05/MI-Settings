package com.android.settings.network;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import java.util.List;

/* loaded from: classes.dex */
public class MobilePlanPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnSaveInstanceState {
    private ConnectivityManager mCm;
    private final MobilePlanPreferenceHost mHost;
    private final boolean mIsSecondaryUser;
    private String mMobilePlanDialogMessage;
    private TelephonyManager mTm;
    private final UserManager mUserManager;

    /* loaded from: classes.dex */
    public interface MobilePlanPreferenceHost {
        void showMobilePlanMessageDialog();
    }

    public MobilePlanPreferenceController(Context context, MobilePlanPreferenceHost mobilePlanPreferenceHost) {
        super(context);
        this.mHost = mobilePlanPreferenceHost;
        this.mCm = (ConnectivityManager) context.getSystemService("connectivity");
        this.mTm = (TelephonyManager) context.getSystemService("phone");
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mIsSecondaryUser = !r1.isAdminUser();
    }

    private void onManageMobilePlanClick() {
        Resources resources = this.mContext.getResources();
        NetworkInfo activeNetworkInfo = this.mCm.getActiveNetworkInfo();
        if (this.mTm.hasIccCard() && activeNetworkInfo != null) {
            Intent intent = new Intent("android.intent.action.CARRIER_SETUP");
            List carrierPackageNamesForIntent = this.mTm.getCarrierPackageNamesForIntent(intent);
            if (carrierPackageNamesForIntent != null && !carrierPackageNamesForIntent.isEmpty()) {
                if (carrierPackageNamesForIntent.size() != 1) {
                    Log.w("MobilePlanPrefContr", "Multiple matching carrier apps found, launching the first.");
                }
                intent.setPackage((String) carrierPackageNamesForIntent.get(0));
                this.mContext.startActivity(intent);
                return;
            }
            String mobileProvisioningUrl = this.mTm.getMobileProvisioningUrl();
            if (TextUtils.isEmpty(mobileProvisioningUrl)) {
                String simOperatorName = this.mTm.getSimOperatorName();
                if (TextUtils.isEmpty(simOperatorName)) {
                    String networkOperatorName = this.mTm.getNetworkOperatorName();
                    if (TextUtils.isEmpty(networkOperatorName)) {
                        this.mMobilePlanDialogMessage = resources.getString(R.string.mobile_unknown_sim_operator);
                    } else {
                        this.mMobilePlanDialogMessage = resources.getString(R.string.mobile_no_provisioning_url, networkOperatorName);
                    }
                } else {
                    this.mMobilePlanDialogMessage = resources.getString(R.string.mobile_no_provisioning_url, simOperatorName);
                }
            } else {
                Intent makeMainSelectorActivity = Intent.makeMainSelectorActivity("android.intent.action.MAIN", "android.intent.category.APP_BROWSER");
                makeMainSelectorActivity.setData(Uri.parse(mobileProvisioningUrl));
                makeMainSelectorActivity.setFlags(272629760);
                try {
                    this.mContext.startActivity(makeMainSelectorActivity);
                } catch (ActivityNotFoundException e) {
                    Log.w("MobilePlanPrefContr", "onManageMobilePlanClick: startActivity failed" + e);
                }
            }
        } else if (this.mTm.hasIccCard()) {
            this.mMobilePlanDialogMessage = resources.getString(R.string.mobile_connect_to_internet);
        } else {
            this.mMobilePlanDialogMessage = resources.getString(R.string.mobile_insert_sim_card);
        }
        if (TextUtils.isEmpty(this.mMobilePlanDialogMessage)) {
            return;
        }
        Log.d("MobilePlanPrefContr", "onManageMobilePlanClick: message=" + this.mMobilePlanDialogMessage);
        MobilePlanPreferenceHost mobilePlanPreferenceHost = this.mHost;
        if (mobilePlanPreferenceHost != null) {
            mobilePlanPreferenceHost.showMobilePlanMessageDialog();
        } else {
            Log.d("MobilePlanPrefContr", "Missing host fragment, cannot show message dialog.");
        }
    }

    public String getMobilePlanDialogMessage() {
        return this.mMobilePlanDialogMessage;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "manage_mobile_plan";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.mHost == null || !"manage_mobile_plan".equals(preference.getKey())) {
            return false;
        }
        this.mMobilePlanDialogMessage = null;
        onManageMobilePlanClick();
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (!this.mIsSecondaryUser && !Utils.isWifiOnly(this.mContext) && !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_config_mobile_networks", UserHandle.myUserId())) && this.mContext.getResources().getBoolean(R.bool.config_show_mobile_plan);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle != null) {
            this.mMobilePlanDialogMessage = bundle.getString("mManageMobilePlanMessage");
        }
        Log.d("MobilePlanPrefContr", "onCreate: mMobilePlanDialogMessage=" + this.mMobilePlanDialogMessage);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        if (TextUtils.isEmpty(this.mMobilePlanDialogMessage)) {
            return;
        }
        bundle.putString("mManageMobilePlanMessage", this.mMobilePlanDialogMessage);
    }

    public void setMobilePlanDialogMessage(String str) {
        this.mMobilePlanDialogMessage = str;
    }
}
