package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment;

/* loaded from: classes2.dex */
public class WifiDppEnrolleeActivity extends MiuiWifiDppBaseActivity implements WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener {
    private void showQrCodeScannerFragment(String str) {
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = (WifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (wifiDppQrCodeScannerFragment != null) {
            if (wifiDppQrCodeScannerFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment2 = new WifiDppQrCodeScannerFragment(str);
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, wifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
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
        } else if (action.equals("android.settings.WIFI_DPP_ENROLLEE_QR_CODE_SCANNER")) {
            showQrCodeScannerFragment(intent.getStringExtra("ssid"));
        } else {
            Log.e("WifiDppEnrolleeActivity", "Launch with an invalid action");
            finish();
        }
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
    }
}
