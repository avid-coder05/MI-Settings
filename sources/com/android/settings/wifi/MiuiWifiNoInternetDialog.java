package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public final class MiuiWifiNoInternetDialog extends AppCompatActivity implements DialogInterface.OnClickListener {
    private ConnectivityManager mCM;
    private Network mNetwork;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private String mNetworkName;

    private void createDialog() {
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || !intent.getAction().equals("android.net.action.PROMPT_UNVALIDATED") || !"netId".equals(intent.getScheme())) {
            Log.e("WifiNoInternetDialog", "Unexpected intent " + intent + ", exiting");
            finish();
            return;
        }
        Network network = (Network) intent.getParcelableExtra("android.net.extra.NETWORK");
        this.mNetwork = network;
        if (network == null) {
            Log.e("WifiNoInternetDialog", "Can't determine network from '" + intent.getData() + "' , exiting");
            finish();
            return;
        }
        NetworkRequest build = new NetworkRequest.Builder().clearCapabilities().build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.wifi.MiuiWifiNoInternetDialog.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network2, NetworkCapabilities networkCapabilities) {
                if (MiuiWifiNoInternetDialog.this.mNetwork.equals(network2) && networkCapabilities.hasCapability(16)) {
                    Log.d("WifiNoInternetDialog", "Network " + MiuiWifiNoInternetDialog.this.mNetwork + " validated");
                    MiuiWifiNoInternetDialog.this.finish();
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network2) {
                if (MiuiWifiNoInternetDialog.this.mNetwork.equals(network2)) {
                    Log.d("WifiNoInternetDialog", "Network " + MiuiWifiNoInternetDialog.this.mNetwork + " disconnected");
                    MiuiWifiNoInternetDialog.this.finish();
                }
            }
        };
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mCM = connectivityManager;
        connectivityManager.registerNetworkCallback(build, this.mNetworkCallback);
        NetworkInfo networkInfo = this.mCM.getNetworkInfo(this.mNetwork);
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            String extraInfo = networkInfo.getExtraInfo();
            this.mNetworkName = extraInfo;
            if (extraInfo != null) {
                this.mNetworkName = extraInfo.replaceAll("^\"|\"$", "");
            }
            createDialog();
            return;
        }
        Log.d("WifiNoInternetDialog", "Network " + this.mNetwork + " is not connected: " + networkInfo);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        ConnectivityManager.NetworkCallback networkCallback = this.mNetworkCallback;
        if (networkCallback != null) {
            this.mCM.unregisterNetworkCallback(networkCallback);
            this.mNetworkCallback = null;
        }
        super.onDestroy();
    }
}
