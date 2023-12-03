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
import com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment;
import com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment;
import com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment;
import com.android.settings.wifi.dpp.WifiNetworkConfig;

/* loaded from: classes2.dex */
public class MiuiWifiDppConfiguratorActivity extends MiuiWifiDppBaseActivity implements WifiNetworkConfig.Retriever, MiuiWifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener, MiuiWifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener, MiuiWifiNetworkListFragment.OnChooseNetworkListener {
    private boolean mIsTest;
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

    private void showAddDeviceFragment(boolean z) {
        MiuiWifiDppAddDeviceFragment miuiWifiDppAddDeviceFragment = (MiuiWifiDppAddDeviceFragment) this.mFragmentManager.findFragmentByTag("add_device_fragment");
        if (miuiWifiDppAddDeviceFragment != null) {
            if (miuiWifiDppAddDeviceFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        MiuiWifiDppAddDeviceFragment miuiWifiDppAddDeviceFragment2 = new MiuiWifiDppAddDeviceFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, miuiWifiDppAddDeviceFragment2, "add_device_fragment");
        if (z) {
            beginTransaction.addToBackStack(null);
        }
        beginTransaction.commit();
    }

    private void showChooseSavedWifiNetworkFragment(boolean z) {
        MiuiWifiDppChooseSavedWifiNetworkFragment miuiWifiDppChooseSavedWifiNetworkFragment = (MiuiWifiDppChooseSavedWifiNetworkFragment) this.mFragmentManager.findFragmentByTag("choose_saved_wifi_network_fragment");
        if (miuiWifiDppChooseSavedWifiNetworkFragment != null) {
            if (miuiWifiDppChooseSavedWifiNetworkFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        MiuiWifiDppChooseSavedWifiNetworkFragment miuiWifiDppChooseSavedWifiNetworkFragment2 = new MiuiWifiDppChooseSavedWifiNetworkFragment();
        if (this.mIsTest) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("test", true);
            miuiWifiDppChooseSavedWifiNetworkFragment2.setArguments(bundle);
        }
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, miuiWifiDppChooseSavedWifiNetworkFragment2, "choose_saved_wifi_network_fragment");
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
        MiuiWifiDppQrCodeScannerFragment miuiWifiDppQrCodeScannerFragment = (MiuiWifiDppQrCodeScannerFragment) this.mFragmentManager.findFragmentByTag("qr_code_scanner_fragment");
        if (miuiWifiDppQrCodeScannerFragment != null) {
            if (miuiWifiDppQrCodeScannerFragment.isVisible()) {
                return;
            }
            this.mFragmentManager.popBackStackImmediate();
            return;
        }
        MiuiWifiDppQrCodeScannerFragment miuiWifiDppQrCodeScannerFragment2 = new MiuiWifiDppQrCodeScannerFragment();
        FragmentTransaction beginTransaction = this.mFragmentManager.beginTransaction();
        beginTransaction.replace(R.id.fragment_container, miuiWifiDppQrCodeScannerFragment2, "qr_code_scanner_fragment");
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
    protected void handleIntent(Intent intent) {
        String action = intent != null ? intent.getAction() : null;
        if (action == null) {
            finish();
            return;
        }
        char c = 65535;
        boolean z = true;
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
                Uri data = intent.getData();
                String uri = data != null ? data.toString() : null;
                this.mIsTest = intent.getBooleanExtra("test", false);
                this.mWifiDppQrCode = WifiQrCode.getValidWifiDppQrCodeOrNull(uri);
                this.mWifiDppRemoteBandSupport = intent.getIntArrayExtra("android.provider.extra.EASY_CONNECT_BAND_LIST");
                boolean isWifiDppEnabled = WifiDppUtils.isWifiDppEnabled(this);
                if (!isWifiDppEnabled) {
                    Log.e("MiuiWifiDppConfiguratorActivity", "ACTION_PROCESS_WIFI_EASY_CONNECT_URI for a device that doesn't support Wifi DPP - use WifiManager#isEasyConnectSupported");
                }
                if (this.mWifiDppQrCode == null) {
                    Log.e("MiuiWifiDppConfiguratorActivity", "ACTION_PROCESS_WIFI_EASY_CONNECT_URI with null URI!");
                }
                if (this.mWifiDppQrCode != null && isWifiDppEnabled) {
                    WifiNetworkConfig connectedWifiNetworkConfigOrNull = getConnectedWifiNetworkConfigOrNull();
                    if (connectedWifiNetworkConfigOrNull == null || !connectedWifiNetworkConfigOrNull.isSupportWifiDpp(this)) {
                        showChooseSavedWifiNetworkFragment(false);
                    } else {
                        this.mWifiNetworkConfig = connectedWifiNetworkConfigOrNull;
                        showAddDeviceFragment(false);
                    }
                    z = false;
                    break;
                }
                break;
            case 1:
                WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(intent);
                if (validConfigOrNull != null) {
                    this.mWifiNetworkConfig = validConfigOrNull;
                    showQrCodeGeneratorFragment();
                    z = false;
                    break;
                }
                break;
            case 2:
                WifiNetworkConfig validConfigOrNull2 = WifiNetworkConfig.getValidConfigOrNull(intent);
                if (validConfigOrNull2 != null) {
                    this.mWifiNetworkConfig = validConfigOrNull2;
                    showQrCodeScannerFragment();
                    z = false;
                    break;
                }
                break;
            default:
                Log.e("MiuiWifiDppConfiguratorActivity", "Launch with an invalid action");
                break;
        }
        if (z) {
            finish();
        }
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment.OnChooseNetworkListener
    public void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig) {
        this.mWifiNetworkConfig = new WifiNetworkConfig(wifiNetworkConfig);
        showAddDeviceFragment(true);
    }

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppAddDeviceFragment.OnClickChooseDifferentNetworkListener
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

    @Override // com.android.settings.wifi.dpp.MiuiWifiDppQrCodeScannerFragment.OnScanWifiDppSuccessListener
    public void onScanWifiDppSuccess(WifiQrCode wifiQrCode) {
        Intent intent = new Intent("scan_dpp_success");
        intent.setPackage("com.android.settings");
        Bundle bundle = new Bundle();
        bundle.putSerializable("wifi_qr_code", wifiQrCode);
        bundle.putSerializable("wifi_net_work_config", this.mWifiNetworkConfig);
        intent.putExtras(bundle);
        sendBroadcast(intent);
        finish();
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
