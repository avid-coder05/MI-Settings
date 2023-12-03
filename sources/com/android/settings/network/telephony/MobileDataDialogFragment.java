package com.android.settings.network.telephony;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MobileDataDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private int mSubId;
    private SubscriptionManager mSubscriptionManager;
    private int mType;
    private WifiPickerTrackerHelper mWifiPickerTrackerHelper;

    public static MobileDataDialogFragment newInstance(int i, int i2) {
        MobileDataDialogFragment mobileDataDialogFragment = new MobileDataDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("dialog_type", i);
        bundle.putInt("subId", i2);
        mobileDataDialogFragment.setArguments(bundle);
        return mobileDataDialogFragment;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1582;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.mType;
        if (i2 == 0) {
            MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, false, false);
            WifiPickerTrackerHelper wifiPickerTrackerHelper = this.mWifiPickerTrackerHelper;
            if (wifiPickerTrackerHelper == null || wifiPickerTrackerHelper.isCarrierNetworkProvisionEnabled(this.mSubId)) {
                return;
            }
            this.mWifiPickerTrackerHelper.setCarrierNetworkEnabled(false);
        } else if (i2 != 1) {
            throw new IllegalArgumentException("unknown type " + this.mType);
        } else {
            this.mSubscriptionManager.setDefaultDataSubId(this.mSubId);
            MobileNetworkUtils.setMobileDataEnabled(getContext(), this.mSubId, true, true);
            WifiPickerTrackerHelper wifiPickerTrackerHelper2 = this.mWifiPickerTrackerHelper;
            if (wifiPickerTrackerHelper2 == null || wifiPickerTrackerHelper2.isCarrierNetworkProvisionEnabled(this.mSubId)) {
                return;
            }
            this.mWifiPickerTrackerHelper.setCarrierNetworkEnabled(true);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mSubscriptionManager = (SubscriptionManager) getContext().getSystemService(SubscriptionManager.class);
        this.mWifiPickerTrackerHelper = new WifiPickerTrackerHelper(getSettingsLifecycle(), getContext(), null);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        Context context = getContext();
        this.mType = arguments.getInt("dialog_type");
        int i = arguments.getInt("subId");
        this.mSubId = i;
        int i2 = this.mType;
        if (i2 != 0) {
            if (i2 != 1) {
                throw new IllegalArgumentException("unknown type " + this.mType);
            }
            SubscriptionInfo activeSubscriptionInfo = this.mSubscriptionManager.getActiveSubscriptionInfo(i);
            SubscriptionInfo activeSubscriptionInfo2 = this.mSubscriptionManager.getActiveSubscriptionInfo(SubscriptionManager.getDefaultDataSubscriptionId());
            String string = activeSubscriptionInfo2 == null ? getContext().getResources().getString(R.string.sim_selection_required_pref) : SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo2, getContext()).toString();
            String string2 = activeSubscriptionInfo == null ? getContext().getResources().getString(R.string.sim_selection_required_pref) : SubscriptionUtil.getUniqueSubscriptionDisplayName(activeSubscriptionInfo, getContext()).toString();
            return new AlertDialog.Builder(context).setTitle(context.getString(R.string.sim_change_data_title, string2)).setMessage(context.getString(R.string.sim_change_data_message, string2, string)).setPositiveButton(context.getString(R.string.sim_change_data_ok, string2), this).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
        }
        return new AlertDialog.Builder(context).setMessage(R.string.data_usage_disable_mobile).setPositiveButton(17039370, this).setNegativeButton(17039360, (DialogInterface.OnClickListener) null).create();
    }
}
