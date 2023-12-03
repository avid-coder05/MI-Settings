package com.android.settings.deviceinfo.imei;

import android.content.Context;
import android.content.res.Resources;
import android.telephony.MiuiTelephonyManagerStub;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Log;
import com.android.settings.R;
import java.util.List;

/* loaded from: classes.dex */
public class ImeiInfoDialogController {
    private final ImeiInfoDialogFragment mDialog;
    private final int mSlotId;
    private final SubscriptionInfo mSubscriptionInfo;
    private final TelephonyManager mTelephonyManager;
    static final int ID_PRL_VERSION_VALUE = R.id.prl_version_value;
    private static final int ID_MIN_NUMBER_LABEL = R.id.min_number_label;
    static final int ID_MIN_NUMBER_VALUE = R.id.min_number_value;
    static final int ID_MEID_NUMBER_VALUE = R.id.meid_number_value;
    static final int ID_IMEI_VALUE = R.id.imei_value;
    static final int ID_IMEI_SV_VALUE = R.id.imei_sv_value;
    static final int ID_CDMA_SETTINGS = R.id.cdma_settings;
    static final int ID_GSM_SETTINGS = R.id.gsm_settings;
    static final int ID_MEID_SETTINGS = R.id.meid_settings;

    public ImeiInfoDialogController(ImeiInfoDialogFragment imeiInfoDialogFragment, int i) {
        this.mDialog = imeiInfoDialogFragment;
        this.mSlotId = i;
        Context context = imeiInfoDialogFragment.getContext();
        SubscriptionInfo activeSubscriptionInfoForSimSlotIndex = ((SubscriptionManager) context.getSystemService(SubscriptionManager.class)).getActiveSubscriptionInfoForSimSlotIndex(i);
        this.mSubscriptionInfo = activeSubscriptionInfoForSimSlotIndex;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (activeSubscriptionInfoForSimSlotIndex != null) {
            this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(activeSubscriptionInfoForSimSlotIndex.getSubscriptionId());
        } else if (isValidSlotIndex(i, telephonyManager)) {
            this.mTelephonyManager = telephonyManager;
        } else {
            this.mTelephonyManager = null;
        }
    }

    private static CharSequence getTextAsDigits(CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            return "";
        }
        if (TextUtils.isDigitsOnly(charSequence)) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            spannableStringBuilder.setSpan(new TtsSpan.DigitsBuilder(charSequence.toString()).build(), 0, spannableStringBuilder.length(), 33);
            return spannableStringBuilder;
        }
        return charSequence;
    }

    private boolean isValidSlotIndex(int i, TelephonyManager telephonyManager) {
        return i >= 0 && i < telephonyManager.getPhoneCount();
    }

    private void updateDialogForCdmaPhone() {
        Resources resources = this.mDialog.getContext().getResources();
        if (TextUtils.isEmpty(getMeid())) {
            this.mDialog.removeViewFromScreen(ID_MEID_SETTINGS);
        } else {
            this.mDialog.setText(ID_MEID_NUMBER_VALUE, getMeid());
        }
        ImeiInfoDialogFragment imeiInfoDialogFragment = this.mDialog;
        int i = ID_MIN_NUMBER_VALUE;
        SubscriptionInfo subscriptionInfo = this.mSubscriptionInfo;
        imeiInfoDialogFragment.setText(i, subscriptionInfo != null ? this.mTelephonyManager.getCdmaMin(subscriptionInfo.getSubscriptionId()) : "");
        if (resources.getBoolean(R.bool.config_msid_enable)) {
            this.mDialog.setText(ID_MIN_NUMBER_LABEL, resources.getString(R.string.status_msid_number));
        }
        this.mDialog.setText(ID_PRL_VERSION_VALUE, getCdmaPrlVersion());
        this.mDialog.setText(ID_IMEI_VALUE, getTextAsDigits(MiuiTelephonyManagerStub.getImeiForSlot(this.mSlotId)));
        this.mDialog.setText(ID_IMEI_SV_VALUE, getTextAsDigits(this.mTelephonyManager.getDeviceSoftwareVersion(this.mSlotId)));
    }

    private void updateDialogForGsmPhone() {
        if (TextUtils.isEmpty(getMeid())) {
            this.mDialog.removeViewFromScreen(ID_MEID_SETTINGS);
        } else {
            this.mDialog.setText(ID_MEID_NUMBER_VALUE, getMeid());
        }
        this.mDialog.setText(ID_IMEI_VALUE, getTextAsDigits(MiuiTelephonyManagerStub.getImeiForSlot(this.mSlotId)));
        this.mDialog.setText(ID_IMEI_SV_VALUE, getTextAsDigits(this.mTelephonyManager.getDeviceSoftwareVersion(this.mSlotId)));
        this.mDialog.removeViewFromScreen(ID_CDMA_SETTINGS);
    }

    String getCdmaPrlVersion() {
        return this.mSubscriptionInfo != null ? this.mTelephonyManager.getCdmaPrlVersion() : "";
    }

    String getMeid() {
        List<String> meidList = miui.telephony.TelephonyManager.getDefault().getMeidList();
        if (meidList == null || meidList.size() <= 0) {
            return null;
        }
        return meidList.get(this.mSlotId < meidList.size() ? this.mSlotId : 0);
    }

    boolean isCdmaLteEnabled() {
        return this.mTelephonyManager.isLteCdmaEvdoGsmWcdmaEnabled();
    }

    public void populateImeiInfo() {
        TelephonyManager telephonyManager = this.mTelephonyManager;
        if (telephonyManager == null) {
            Log.w("ImeiInfoDialog", "TelephonyManager for this slot is null. Invalid slot? id=" + this.mSlotId);
        } else if (telephonyManager.getCurrentPhoneTypeForSlot(this.mSlotId) == 2) {
            updateDialogForCdmaPhone();
        } else {
            updateDialogForGsmPhone();
        }
    }
}
