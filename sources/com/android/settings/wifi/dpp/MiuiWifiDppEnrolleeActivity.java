package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment;

/* loaded from: classes2.dex */
public class MiuiWifiDppEnrolleeActivity extends MiuiWifiDppBaseActivity implements MiuiWifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener {
    private void showQrCodeScannerFragment(String str, boolean z) {
        MiuiWifiDppQrCodeScannerFragment miuiWifiDppQrCodeScannerFragment = (MiuiWifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (miuiWifiDppQrCodeScannerFragment != null) {
            if (miuiWifiDppQrCodeScannerFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        MiuiWifiDppQrCodeScannerFragment miuiWifiDppQrCodeScannerFragment2 = new MiuiWifiDppQrCodeScannerFragment(str, z);
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, miuiWifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
        beginTransaction.commit();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1596;
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppBaseActivity
    protected void handleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            finish();
        } else if (action.equals("miui.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER") || action.equals("android.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER")) {
            showQrCodeScannerFragment(intent.getStringExtra("ssid"), intent.getBooleanExtra("is_slave", false));
        } else {
            Log.e("MiuiWifiDppEnrolleeActivity", "Launch with an invalid action");
            finish();
        }
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
    }
}
