package com.android.settings.wifi.dpp;

import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import com.android.settings.R;
import com.android.settings.wifi.dpp.WifiDppAddDeviceFragment;
import com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;
import com.android.settings.wifi.dpp.WifiNetworkListFragment;

/* loaded from: classes2.dex */
public class WifiDppConfiguratorActivity extends MiuiWifiDppBaseActivity implements WifiNetworkConfig.Retriever, WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener, WifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener, WifiNetworkListFragment.OnChooseNetworkListener {
    private WifiQrCode mWifiDppQrCode;
    private int[] mWifiDppRemoteBandSupport;
    private WifiNetworkConfig mWifiNetworkConfig;

    private WifiNetworkConfig getConnectedWifiNetworkConfigOrNull() {
        WifiInfo connectionInfo;
        WifiManager wifiManager = (WifiManager) getSystemService(WifiManager.class);
        if (wifiManager.isWifiEnabled() && (connectionInfo = wifiManager.getConnectionInfo()) != null) {
            int networkId = connectionInfo.getNetworkId();
            for (WifiConfiguration wifiConfiguration : wifiManager.getConfiguredNetworks()) {
                if (wifiConfiguration.networkId == networkId) {
                    return WifiNetworkConfig.getValidConfigOrNull(WifiDppUtils.getSecurityString(wifiConfiguration), wifiConfiguration.getPrintableSsid(), wifiConfiguration.preSharedKey, wifiConfiguration.hiddenSSID, wifiConfiguration.networkId, false);
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: handleActionProcessWifiEasyConnectUriIntent  reason: merged with bridge method [inline-methods] */
    public void lambda$handleIntent$0(Intent intent) {
        Uri data = intent.getData();
        this.mWifiDppQrCode = WifiQrCode.getValidWifiDppQrCodeOrNull(data == null ? null : data.toString());
        this.mWifiDppRemoteBandSupport = intent.getIntArrayExtra("android.provider.extra.EASY_CONNECT_BAND_LIST");
        boolean isWifiDppEnabled = WifiDppUtils.isWifiDppEnabled(this);
        if (!isWifiDppEnabled) {
            Log.e("WifiDppConfiguratorActivity", "ACTION_PROCESS_WIFI_EASY_CONNECT_URI for a device that doesn't support Wifi DPP - use WifiManager#isEasyConnectSupported");
        }
        if (this.mWifiDppQrCode == null) {
            Log.e("WifiDppConfiguratorActivity", "ACTION_PROCESS_WIFI_EASY_CONNECT_URI with null URI!");
        }
        if (this.mWifiDppQrCode == null || !isWifiDppEnabled) {
            finish();
            return;
        }
        WifiNetworkConfig connectedWifiNetworkConfigOrNull = getConnectedWifiNetworkConfigOrNull();
        if (connectedWifiNetworkConfigOrNull == null || !connectedWifiNetworkConfigOrNull.isSupportWifiDpp(this)) {
            showChooseSavedWifiNetworkFragment(false);
            return;
        }
        this.mWifiNetworkConfig = connectedWifiNetworkConfigOrNull;
        showAddDeviceFragment(false);
    }

    private void showAddDeviceFragment(boolean z) {
        WifiDppAddDeviceFragment wifiDppAddDeviceFragment = (WifiDppAddDeviceFragment) this.mFragmentManager.findFragmentByTag("add_device_fragment");
        if (wifiDppAddDeviceFragment != null) {
            if (wifiDppAddDeviceFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        WifiDppAddDeviceFragment wifiDppAddDeviceFragment2 = new WifiDppAddDeviceFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, wifiDppAddDeviceFragment2, "add_device_fragment");
        if (z) {
            beginTransaction.addToBackStack(null);
        }
        beginTransaction.commit();
    }

    private void showChooseSavedWifiNetworkFragment(boolean z) {
        WifiDppChooseSavedWifiNetworkFragment wifiDppChooseSavedWifiNetworkFragment = (WifiDppChooseSavedWifiNetworkFragment) this.mFragmentManager.findFragmentByTag("choose_saved_wifi_network_fragment");
        if (wifiDppChooseSavedWifiNetworkFragment != null) {
            if (wifiDppChooseSavedWifiNetworkFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        WifiDppChooseSavedWifiNetworkFragment wifiDppChooseSavedWifiNetworkFragment2 = new WifiDppChooseSavedWifiNetworkFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, wifiDppChooseSavedWifiNetworkFragment2, "choose_saved_wifi_network_fragment");
        if (z) {
            beginTransaction.addToBackStack(null);
        }
        beginTransaction.commit();
    }

    private void showQrCodeGeneratorFragment() {
        WifiDppQrCodeGeneratorFragment wifiDppQrCodeGeneratorFragment = (WifiDppQrCodeGeneratorFragment) this.mFragmentManager.findFragmentByTag("qr_code_generator_fragment");
        if (wifiDppQrCodeGeneratorFragment != null) {
            if (wifiDppQrCodeGeneratorFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        WifiDppQrCodeGeneratorFragment wifiDppQrCodeGeneratorFragment2 = new WifiDppQrCodeGeneratorFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, wifiDppQrCodeGeneratorFragment2, "qr_code_generator_fragment");
        beginTransaction.commit();
    }

    private void showQrCodeScannerFragment() {
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment = (WifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (wifiDppQrCodeScannerFragment != null) {
            if (wifiDppQrCodeScannerFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        WifiDppQrCodeScannerFragment wifiDppQrCodeScannerFragment2 = new WifiDppQrCodeScannerFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, wifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
        beginTransaction.commit();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WifiQrCode getWifiDppQrCode() {
        return this.mWifiDppQrCode;
    }

    @Override // com.android.settings.wifi.dpp.WifiNetworkConfig.Retriever
    public WifiNetworkConfig getWifiNetworkConfig() {
        return this.mWifiNetworkConfig;
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppBaseActivity
    protected void handleIntent(final Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            finish();
            return;
        }
        char c = 65535;
        boolean z = false;
        switch (action.hashCode()) {
            case -902592152:
                if (action.equals("android.settings.PROCESS_WIFI_EASY_CONNECT_URI")) {
                    c = 0;
                    break;
                }
                break;
            case 360935630:
                if (action.equals("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR")) {
                    c = 1;
                    break;
                }
                break;
            case 1361318585:
                if (action.equals("android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_SCANNER")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                WifiDppUtils.showLockScreen(this, new Runnable() { // from class: com.android.settings.wifi.dpp.WifiDppConfiguratorActivity$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        WifiDppConfiguratorActivity.this.lambda$handleIntent$0(intent);
                    }
                });
                break;
            case 1:
                WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(intent);
                if (validConfigOrNull != null) {
                    this.mWifiNetworkConfig = validConfigOrNull;
                    showQrCodeGeneratorFragment();
                    break;
                }
                z = true;
                break;
            case 2:
                WifiNetworkConfig validConfigOrNull2 = WifiNetworkConfig.getValidConfigOrNull(intent);
                if (validConfigOrNull2 != null) {
                    this.mWifiNetworkConfig = validConfigOrNull2;
                    showQrCodeScannerFragment();
                    break;
                }
                z = true;
                break;
            default:
                Log.e("WifiDppConfiguratorActivity", "Launch with an invalid action");
                z = true;
                break;
        }
        if (z) {
            finish();
        }
    }

    @Override // com.android.settings.wifi.dpp.WifiNetworkListFragment.OnChooseNetworkListener
    public void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig) {
        this.mWifiNetworkConfig = new WifiNetworkConfig(wifiNetworkConfig);
        showAddDeviceFragment(true);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener
    public void onClickChooseDifferentNetwork() {
        showChooseSavedWifiNetworkFragment(true);
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppBaseActivity, com.android.settings.core.InstrumentedActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFragmentManager = getSupportFragmentManager();
        if (bundle != null) {
            this.mWifiDppQrCode = WifiQrCode.getValidWifiDppQrCodeOrNull(bundle.getString("key_qr_code"));
            this.mWifiNetworkConfig = WifiNetworkConfig.getValidConfigOrNull(bundle.getString("key_wifi_security"), bundle.getString("key_wifi_ssid"), bundle.getString("key_wifi_preshared_key"), bundle.getBoolean("key_wifi_hidden_ssid"), bundle.getInt("key_wifi_network_id"), bundle.getBoolean("key_is_hotspot"));
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        WifiQrCode wifiQrCode = this.mWifiDppQrCode;
        if (wifiQrCode != null) {
            bundle.putString("key_qr_code", wifiQrCode.getQrCode());
        }
        WifiNetworkConfig wifiNetworkConfig = this.mWifiNetworkConfig;
        if (wifiNetworkConfig != null) {
            bundle.putString("key_wifi_security", wifiNetworkConfig.getSecurity());
            bundle.putString("key_wifi_ssid", this.mWifiNetworkConfig.getSsid());
            bundle.putString("key_wifi_preshared_key", this.mWifiNetworkConfig.getPreSharedKey());
            bundle.putBoolean("key_wifi_hidden_ssid", this.mWifiNetworkConfig.getHiddenSsid());
            bundle.putInt("key_wifi_network_id", this.mWifiNetworkConfig.getNetworkId());
            bundle.putBoolean("key_is_hotspot", this.mWifiNetworkConfig.isHotspot());
        }
        super.onSaveInstanceState(bundle);
    }

    @Override // com.android.settings.wifi.dpp.WifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
        this.mWifiDppQrCode = wifiQrCode;
        showAddDeviceFragment(true);
    }

    boolean setWifiDppQrCode(WifiQrCode wifiQrCode) {
        if (wifiQrCode != null && "DPP".equals(wifiQrCode.getScheme())) {
            this.mWifiDppQrCode = new WifiQrCode(wifiQrCode.getQrCode());
            return true;
        }
        return false;
    }

    boolean setWifiNetworkConfig(WifiNetworkConfig wifiNetworkConfig) {
        if (WifiNetworkConfig.isValidConfig(wifiNetworkConfig)) {
            this.mWifiNetworkConfig = new WifiNetworkConfig(wifiNetworkConfig);
            return true;
        }
        return false;
    }
}
