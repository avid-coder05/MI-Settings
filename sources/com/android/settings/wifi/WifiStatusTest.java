package com.android.settings.wifi;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.wifi.AccessPoint;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;

/* loaded from: classes2.dex */
public class WifiStatusTest extends Activity {
    private TextView mBSSID;
    private TextView mHiddenSSID;
    private TextView mHttpClientTest;
    private String mHttpClientTestResult;
    private TextView mIPAddr;
    private TextView mMACAddr;
    private TextView mNetworkId;
    private TextView mNetworkState;
    private TextView mPingHostname;
    private String mPingHostnameResult;
    private TextView mRSSI;
    private TextView mRxLinkSpeed;
    private TextView mSSID;
    private TextView mScanList;
    private TextView mSupplicantState;
    private TextView mTxLinkSpeed;
    private TextView mWifiChannel;
    private TextView mWifiFrequency;
    private WifiManager mWifiManager;
    private TextView mWifiState;
    private IntentFilter mWifiStateFilter;
    private Button pingTestButton;
    private Button updateButton;
    private final BroadcastReceiver mWifiStateReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.WifiStatusTest.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                WifiStatusTest.this.handleWifiStateChanged(intent.getIntExtra("wifi_state", 4));
            } else if (intent.getAction().equals("android.net.wifi.STATE_CHANGE")) {
                WifiStatusTest.this.handleNetworkStateChanged((NetworkInfo) intent.getParcelableExtra("networkInfo"));
            } else if (intent.getAction().equals("android.net.wifi.SCAN_RESULTS")) {
                WifiStatusTest.this.handleScanResultsAvailable();
            } else if (intent.getAction().equals("android.net.wifi.supplicant.CONNECTION_CHANGE")) {
            } else {
                if (intent.getAction().equals("android.net.wifi.supplicant.STATE_CHANGE")) {
                    WifiStatusTest.this.handleSupplicantStateChanged((SupplicantState) intent.getParcelableExtra("newState"), intent.hasExtra("supplicantError"), intent.getIntExtra("supplicantError", 0));
                } else if (intent.getAction().equals("android.net.wifi.RSSI_CHANGED")) {
                    WifiStatusTest.this.handleSignalChanged(intent.getIntExtra("newRssi", 0));
                } else if (intent.getAction().equals("android.net.wifi.NETWORK_IDS_CHANGED")) {
                } else {
                    Log.e("WifiStatusTest", "Received an unknown Wifi Intent");
                }
            }
        }
    };
    View.OnClickListener mPingButtonHandler = new View.OnClickListener() { // from class: com.android.settings.wifi.WifiStatusTest.2
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            WifiStatusTest.this.updatePingState();
        }
    };
    View.OnClickListener updateButtonHandler = new View.OnClickListener() { // from class: com.android.settings.wifi.WifiStatusTest.3
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            android.net.wifi.WifiInfo connectionInfo = WifiStatusTest.this.mWifiManager.getConnectionInfo();
            WifiStatusTest wifiStatusTest = WifiStatusTest.this;
            wifiStatusTest.setWifiStateText(wifiStatusTest.mWifiManager.getWifiState());
            WifiStatusTest.this.mBSSID.setText(connectionInfo.getBSSID());
            WifiStatusTest.this.mWifiChannel.setText(String.valueOf(WifiStatusTest.this.getCurrentChannel(connectionInfo)));
            WifiStatusTest.this.mHiddenSSID.setText(String.valueOf(connectionInfo.getHiddenSSID()));
            int ipAddress = connectionInfo.getIpAddress();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(ipAddress & 255);
            stringBuffer.append('.');
            int i = ipAddress >>> 8;
            stringBuffer.append(i & 255);
            stringBuffer.append('.');
            int i2 = i >>> 8;
            stringBuffer.append(i2 & 255);
            stringBuffer.append('.');
            stringBuffer.append((i2 >>> 8) & 255);
            WifiStatusTest.this.mIPAddr.setText(stringBuffer);
            WifiStatusTest.this.mTxLinkSpeed.setText(String.valueOf(connectionInfo.getTxLinkSpeedMbps()) + " Mbps");
            WifiStatusTest.this.mRxLinkSpeed.setText(String.valueOf(connectionInfo.getRxLinkSpeedMbps()) + " Mbps");
            WifiStatusTest.this.mMACAddr.setText(connectionInfo.getMacAddress());
            WifiStatusTest.this.mNetworkId.setText(String.valueOf(connectionInfo.getNetworkId()));
            WifiStatusTest.this.mRSSI.setText(String.valueOf(connectionInfo.getRssi()));
            WifiStatusTest.this.mSSID.setText(connectionInfo.getSSID());
            WifiStatusTest.this.setSupplicantStateText(connectionInfo.getSupplicantState());
        }
    };

    private int getChannelByFrequency(int i) {
        switch (i) {
            case 2412:
                return 1;
            case 2417:
                return 2;
            case 2422:
                return 3;
            case 2427:
                return 4;
            case 2432:
                return 5;
            case 2437:
                return 6;
            case 2442:
                return 7;
            case 2447:
                return 8;
            case 2452:
                return 9;
            case 2457:
                return 10;
            case 2462:
                return 11;
            case 2467:
                return 12;
            case 2472:
                return 13;
            case 2484:
                return 14;
            case 5180:
                return 36;
            case 5190:
                return 38;
            case 5200:
                return 40;
            case 5210:
                return 42;
            case 5220:
                return 44;
            case 5230:
                return 46;
            case 5240:
                return 48;
            case 5260:
                return 52;
            case 5280:
                return 56;
            case 5300:
                return 60;
            case 5320:
                return 64;
            case 5745:
                return 149;
            case 5765:
                return 153;
            case 5785:
                return 157;
            case 5805:
                return 161;
            case 5825:
                return 165;
            default:
                return -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCurrentChannel(android.net.wifi.WifiInfo wifiInfo) {
        for (ScanResult scanResult : this.mWifiManager.getScanResults()) {
            if (scanResult.BSSID.equalsIgnoreCase(wifiInfo.getBSSID()) && scanResult.SSID.equalsIgnoreCase(wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1))) {
                this.mWifiFrequency.setText(String.valueOf(scanResult.frequency) + "MHz");
                return getChannelByFrequency(scanResult.frequency);
            }
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleNetworkStateChanged(NetworkInfo networkInfo) {
        if (this.mWifiManager.isWifiEnabled()) {
            android.net.wifi.WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            this.mNetworkState.setText(AccessPoint.getSummary(this, connectionInfo.getSSID(), networkInfo.getDetailedState(), connectionInfo.getNetworkId() == -1, null));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleScanResultsAvailable() {
        List<ScanResult> scanResults = this.mWifiManager.getScanResults();
        StringBuffer stringBuffer = new StringBuffer();
        if (scanResults != null) {
            for (int size = scanResults.size() - 1; size >= 0; size--) {
                ScanResult scanResult = scanResults.get(size);
                if (scanResult != null && !TextUtils.isEmpty(scanResult.SSID)) {
                    stringBuffer.append(scanResult.SSID + " ");
                }
            }
        }
        this.mScanList.setText(stringBuffer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSignalChanged(int i) {
        this.mRSSI.setText(String.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleSupplicantStateChanged(SupplicantState supplicantState, boolean z, int i) {
        if (z) {
            this.mSupplicantState.setText("ERROR AUTHENTICATING");
        } else {
            setSupplicantStateText(supplicantState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWifiStateChanged(int i) {
        setWifiStateText(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void httpClientTest() {
        HttpURLConnection httpURLConnection;
        HttpURLConnection httpURLConnection2 = null;
        try {
            try {
                httpURLConnection = (HttpURLConnection) new URL("https://www.google.com").openConnection();
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException unused) {
        }
        try {
            if (httpURLConnection.getResponseCode() == 200) {
                this.mHttpClientTestResult = "Pass";
            } else {
                this.mHttpClientTestResult = "Fail: Code: " + httpURLConnection.getResponseMessage();
            }
            httpURLConnection.disconnect();
        } catch (IOException unused2) {
            httpURLConnection2 = httpURLConnection;
            this.mHttpClientTestResult = "Fail: IOException";
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
        } catch (Throwable th2) {
            th = th2;
            httpURLConnection2 = httpURLConnection;
            if (httpURLConnection2 != null) {
                httpURLConnection2.disconnect();
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void pingHostname() {
        try {
            if (Runtime.getRuntime().exec("ping -c 1 -w 100 www.google.com").waitFor() == 0) {
                this.mPingHostnameResult = "Pass";
            } else {
                this.mPingHostnameResult = "Fail: Host unreachable";
            }
        } catch (UnknownHostException unused) {
            this.mPingHostnameResult = "Fail: Unknown Host";
        } catch (IOException unused2) {
            this.mPingHostnameResult = "Fail: IOException";
        } catch (InterruptedException unused3) {
            this.mPingHostnameResult = "Fail: InterruptedException";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSupplicantStateText(SupplicantState supplicantState) {
        if (SupplicantState.FOUR_WAY_HANDSHAKE.equals(supplicantState)) {
            this.mSupplicantState.setText("FOUR WAY HANDSHAKE");
        } else if (SupplicantState.ASSOCIATED.equals(supplicantState)) {
            this.mSupplicantState.setText("ASSOCIATED");
        } else if (SupplicantState.ASSOCIATING.equals(supplicantState)) {
            this.mSupplicantState.setText("ASSOCIATING");
        } else if (SupplicantState.COMPLETED.equals(supplicantState)) {
            this.mSupplicantState.setText("COMPLETED");
        } else if (SupplicantState.DISCONNECTED.equals(supplicantState)) {
            this.mSupplicantState.setText("DISCONNECTED");
        } else if (SupplicantState.DORMANT.equals(supplicantState)) {
            this.mSupplicantState.setText("DORMANT");
        } else if (SupplicantState.GROUP_HANDSHAKE.equals(supplicantState)) {
            this.mSupplicantState.setText("GROUP HANDSHAKE");
        } else if (SupplicantState.INACTIVE.equals(supplicantState)) {
            this.mSupplicantState.setText("INACTIVE");
        } else if (SupplicantState.INVALID.equals(supplicantState)) {
            this.mSupplicantState.setText("INVALID");
        } else if (SupplicantState.SCANNING.equals(supplicantState)) {
            this.mSupplicantState.setText("SCANNING");
        } else if (SupplicantState.UNINITIALIZED.equals(supplicantState)) {
            this.mSupplicantState.setText("UNINITIALIZED");
        } else {
            this.mSupplicantState.setText("BAD");
            Log.e("WifiStatusTest", "supplicant state is bad");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setWifiStateText(int i) {
        String string;
        if (i == 0) {
            string = getString(R.string.wifi_state_disabling);
        } else if (i == 1) {
            string = getString(R.string.wifi_state_disabled);
        } else if (i == 2) {
            string = getString(R.string.wifi_state_enabling);
        } else if (i == 3) {
            string = getString(R.string.wifi_state_enabled);
        } else if (i != 4) {
            Log.e("WifiStatusTest", "wifi state is bad");
            string = "BAD";
        } else {
            string = getString(R.string.wifi_state_unknown);
        }
        this.mWifiState.setText(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updatePingState() {
        final Handler handler = new Handler();
        Resources resources = getResources();
        int i = R.string.radioInfo_unknown;
        this.mPingHostnameResult = resources.getString(i);
        this.mHttpClientTestResult = getResources().getString(i);
        this.mPingHostname.setText(this.mPingHostnameResult);
        this.mHttpClientTest.setText(this.mHttpClientTestResult);
        final Runnable runnable = new Runnable() { // from class: com.android.settings.wifi.WifiStatusTest.4
            @Override // java.lang.Runnable
            public void run() {
                WifiStatusTest.this.mPingHostname.setText(WifiStatusTest.this.mPingHostnameResult);
                WifiStatusTest.this.mHttpClientTest.setText(WifiStatusTest.this.mHttpClientTestResult);
            }
        };
        new Thread() { // from class: com.android.settings.wifi.WifiStatusTest.5
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                WifiStatusTest.this.pingHostname();
                handler.post(runnable);
            }
        }.start();
        new Thread() { // from class: com.android.settings.wifi.WifiStatusTest.6
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                WifiStatusTest.this.httpClientTest();
                handler.post(runnable);
            }
        }.start();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mWifiStateFilter = intentFilter;
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.SCAN_RESULTS");
        this.mWifiStateFilter.addAction("android.net.wifi.supplicant.STATE_CHANGE");
        this.mWifiStateFilter.addAction("android.net.wifi.RSSI_CHANGED");
        this.mWifiStateFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
        setContentView(R.layout.wifi_status_test);
        Button button = (Button) findViewById(R.id.update);
        this.updateButton = button;
        button.setOnClickListener(this.updateButtonHandler);
        this.mWifiState = (TextView) findViewById(R.id.wifi_state);
        this.mNetworkState = (TextView) findViewById(R.id.network_state);
        this.mSupplicantState = (TextView) findViewById(R.id.supplicant_state);
        this.mRSSI = (TextView) findViewById(R.id.rssi);
        this.mBSSID = (TextView) findViewById(R.id.bssid);
        this.mSSID = (TextView) findViewById(R.id.ssid);
        this.mHiddenSSID = (TextView) findViewById(R.id.hidden_ssid);
        this.mIPAddr = (TextView) findViewById(R.id.ipaddr);
        this.mMACAddr = (TextView) findViewById(R.id.macaddr);
        this.mNetworkId = (TextView) findViewById(R.id.networkid);
        this.mTxLinkSpeed = (TextView) findViewById(R.id.tx_link_speed);
        this.mRxLinkSpeed = (TextView) findViewById(R.id.rx_link_speed);
        this.mScanList = (TextView) findViewById(R.id.scan_list);
        this.mWifiChannel = (TextView) findViewById(R.id.wifi_channel);
        this.mWifiFrequency = (TextView) findViewById(R.id.wifi_frequency);
        this.mPingHostname = (TextView) findViewById(R.id.pingHostname);
        this.mHttpClientTest = (TextView) findViewById(R.id.httpClientTest);
        Button button2 = (Button) findViewById(R.id.ping_test);
        this.pingTestButton = button2;
        button2.setOnClickListener(this.mPingButtonHandler);
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.mWifiStateReceiver);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        registerReceiver(this.mWifiStateReceiver, this.mWifiStateFilter);
    }
}
