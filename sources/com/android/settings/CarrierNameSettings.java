package com.android.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import miui.os.Build;
import miuix.androidbasewidget.widget.StateEditText;

/* loaded from: classes.dex */
public class CarrierNameSettings extends BaseEditFragment {
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.settings.CarrierNameSettings.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SIM_STATE_CHANGED")) {
                CarrierNameSettings carrierNameSettings = CarrierNameSettings.this;
                carrierNameSettings.mSubInfos = carrierNameSettings.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
                CarrierNameSettings.this.updateCarriersState();
            }
        }
    };
    private StateEditText[] mCarrierView;
    private Context mContext;
    private boolean mHasMobileDataFeature;
    private ViewGroup mParent;
    private TelephonyManager mPhone;
    private int mPhoneCount;
    private List<SubscriptionInfo> mSubInfos;
    private SubscriptionManager mSubscriptionManager;
    private Bundle savedData;

    private void updateCarrier(StateEditText stateEditText, int i, SubscriptionInfo subscriptionInfo) {
        boolean z = false;
        if (this.mPhoneCount > 1) {
            stateEditText.setLabel(this.mContext.getString(R.string.status_bar_settings_carrier_sim, Integer.valueOf(i + 1)));
        }
        if (this.mHasMobileDataFeature && subscriptionInfo != null && this.mPhone.hasIccCard(i)) {
            z = true;
        }
        stateEditText.setMaxEms(50);
        stateEditText.setEnabled(z);
        if (subscriptionInfo != null) {
            stateEditText.setHint(subscriptionInfo.getDisplayName());
        } else {
            stateEditText.setHint(R.string.status_bar_carrier_settings_no_sim_card);
        }
        if (!z) {
            stateEditText.setText("");
        } else if (i < 0 || i >= this.mPhoneCount) {
        } else {
            String string = this.savedData.getString("custom_carrier" + i, MiuiSettings.System.getString(this.mContext.getContentResolver(), "status_bar_custom_carrier" + i, ""));
            stateEditText.setText(string);
            if (string != null) {
                stateEditText.setSelection(string.length());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCarriersState() {
        if (this.mSubInfos != null) {
            for (int i = 0; i < this.mPhoneCount; i++) {
                StateEditText[] stateEditTextArr = this.mCarrierView;
                if (stateEditTextArr != null && i < stateEditTextArr.length && stateEditTextArr[i] != null) {
                    SubscriptionInfo subscriptionInfo = null;
                    int size = this.mSubInfos.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        SubscriptionInfo subscriptionInfo2 = this.mSubInfos.get(i2);
                        if (subscriptionInfo2 != null && subscriptionInfo2.getSimSlotIndex() == i) {
                            subscriptionInfo = subscriptionInfo2;
                        }
                    }
                    updateCarrier(this.mCarrierView[i], i, subscriptionInfo);
                }
            }
        }
    }

    @Override // com.android.settings.BaseFragment
    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.status_bar_settings_carrier, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.carriers_container);
        this.mParent = viewGroup2;
        if (Build.IS_TABLET) {
            ((ViewGroup.MarginLayoutParams) viewGroup2.getLayoutParams()).setMarginStart(this.mParent.getResources().getDimensionPixelSize(R.dimen.pad_custom_carrier_page_margin_start));
            ((ViewGroup.MarginLayoutParams) this.mParent.getLayoutParams()).setMarginEnd(this.mParent.getResources().getDimensionPixelSize(R.dimen.pad_custom_carrier_page_margin_end));
        }
        this.mCarrierView = new StateEditText[this.mPhoneCount];
        for (int i = 0; i < this.mPhoneCount; i++) {
            View inflate2 = layoutInflater.inflate(R.layout.status_bar_settings_custom_carrier, this.mParent, false);
            this.mParent.addView(inflate2);
            this.mCarrierView[i] = (StateEditText) inflate2.findViewById(R.id.edit_text);
        }
        return inflate;
    }

    @Override // com.android.settings.BaseEditFragment
    public String getTitle() {
        return getString(R.string.custom_carrier_title);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getAppCompatActivity() != null) {
            this.mContext = getAppCompatActivity();
            getAppCompatActivity().getWindow().setSoftInputMode(16);
        }
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        this.mPhone = telephonyManager;
        this.mHasMobileDataFeature = telephonyManager.isDataCapable();
        this.mSubscriptionManager = (SubscriptionManager) this.mContext.getSystemService("telephony_subscription_service");
        this.mPhoneCount = this.mPhone.getActiveModemCount();
        if (bundle != null) {
            this.savedData = bundle.deepCopy();
        } else {
            this.savedData = new Bundle();
        }
        if (!this.mHasMobileDataFeature || this.mPhoneCount == 0 || this.mContext == null) {
            finish();
        }
    }

    @Override // com.android.settings.BaseEditFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        super.onDestroyView();
    }

    @Override // com.android.settings.BaseEditFragment
    public void onSave(Bundle bundle) {
        if (this.mContext != null) {
            for (int i = 0; i < this.mPhoneCount; i++) {
                Settings.System.putString(this.mContext.getContentResolver(), "status_bar_custom_carrier" + i, this.mCarrierView[i].getText().toString());
            }
        }
        super.onSave(bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (int i = 0; i < this.mPhoneCount; i++) {
                StateEditText[] stateEditTextArr = this.mCarrierView;
                if (stateEditTextArr[i] != null && stateEditTextArr[i].getText() != null) {
                    bundle.putString("custom_carrier" + i, this.mCarrierView[i].getText().toString());
                }
            }
        }
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        this.mSubInfos = this.mSubscriptionManager.getCompleteActiveSubscriptionInfoList();
        updateCarriersState();
    }
}
