package com.android.settings.wireless;

import android.content.Context;
import android.nfc.NfcAdapter;
import com.android.settings.RegionUtils;
import com.android.settings.nfc.PaymentBackend;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;

/* loaded from: classes2.dex */
public class MiuiNfcPaymentPreferenceController extends AbstractPreferenceController {
    private NfcAdapter mNfcAdapter;
    private PaymentBackend mPaymentBackend;

    public MiuiNfcPaymentPreferenceController(Context context) {
        super(context);
        this.mNfcAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
        this.mPaymentBackend = new PaymentBackend(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "nfc_payment";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<PaymentBackend.PaymentAppInfo> paymentAppInfos;
        return (SettingsFeatures.isNeedShowMiuiNFC() || !this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc") || this.mNfcAdapter == null || (paymentAppInfos = this.mPaymentBackend.getPaymentAppInfos()) == null || paymentAppInfos.isEmpty() || RegionUtils.IS_MEXICO_TELCEL) ? false : true;
    }
}
