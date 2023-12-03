package com.android.settings.wifi.dpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiDppConfig;

/* loaded from: classes2.dex */
public class MiuiDppBroadcastReceiver extends BroadcastReceiver {
    private byte authMissingParam;
    private String cSignKey;
    private byte capab;
    private byte configEventType;
    private String connector;
    private WifiDppConfig dppConfig;
    private int dppEventType;
    private String iBootstrapData;
    private boolean initiator;
    private String netAccessKey;
    private int netAcessKeyExpiry;
    private int netID;
    private String passWord;
    private String psk;
    private String ssid;

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int intExtra = intent.getIntExtra("dppEventType", 0);
        this.dppEventType = intExtra;
        if (intExtra == 4) {
            WifiDppConfig wifiDppConfig = (WifiDppConfig) intent.getParcelableExtra("dppEventData");
            this.dppConfig = wifiDppConfig;
            WifiDppConfig.DppResult dppResult = wifiDppConfig.getDppResult();
            this.ssid = dppResult.ssid;
            this.passWord = dppResult.passphrase;
            this.configEventType = dppResult.configEventType;
            this.connector = dppResult.connector;
            this.netAcessKeyExpiry = dppResult.netAccessKeyExpiry;
            this.netID = dppResult.netID;
            this.initiator = dppResult.initiator;
            this.capab = dppResult.capab;
            this.authMissingParam = dppResult.authMissingParam;
            this.iBootstrapData = dppResult.iBootstrapData;
            this.cSignKey = dppResult.cSignKey;
            this.netAccessKey = dppResult.netAccessKey;
            this.psk = dppResult.psk;
        }
    }
}
