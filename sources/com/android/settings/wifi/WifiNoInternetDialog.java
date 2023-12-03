package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiNoInternetDialog extends AppCompatActivity implements DialogInterface.OnClickListener {
    private String mAction;
    CheckBox mAlwaysAllow;
    private boolean mButtonClicked;
    private ConnectivityManager mCM;
    private Network mNetwork;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private String mNetworkName;

    private boolean isKnownAction(Intent intent) {
        return "android.net.action.PROMPT_UNVALIDATED".equals(intent.getAction()) || "android.net.action.PROMPT_LOST_VALIDATION".equals(intent.getAction()) || "android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(intent.getAction());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$0(DialogInterface dialogInterface) {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDialog$1(DialogInterface dialogInterface) {
        if (isFinishing()) {
            return;
        }
        finish();
    }

    private void showDialog() {
        String string;
        String string2;
        String string3;
        String string4;
        if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
            string = this.mNetworkName;
            string2 = String.format(getString(R.string.no_internet_access_text), string);
            string3 = getString(R.string.yes);
            string4 = getString(R.string.no);
        } else if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            string = this.mNetworkName;
            string2 = String.format(getString(R.string.partial_connectivity_text), string);
            string3 = getString(R.string.yes);
            string4 = getString(R.string.no);
        } else {
            string = getString(R.string.lost_internet_access_title);
            string2 = getString(R.string.lost_internet_access_text);
            string3 = getString(R.string.lost_internet_access_switch);
            string4 = getString(R.string.lost_internet_access_cancel);
        }
        AlertDialog.Builder onDismissListener = new AlertDialog.Builder(this).setMessage(string).setMessage(string2).setPositiveButton(string3, this).setNegativeButton(string4, this).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.wifi.WifiNoInternetDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                WifiNoInternetDialog.this.lambda$showDialog$0(dialogInterface);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wifi.WifiNoInternetDialog$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                WifiNoInternetDialog.this.lambda$showDialog$1(dialogInterface);
            }
        });
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_no_wifi_checkbox, (ViewGroup) null);
        this.mAlwaysAllow = (CheckBox) inflate.findViewById(R.id.alwaysUse);
        if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction) || "android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
            this.mAlwaysAllow.setText(getString(R.string.no_internet_access_remember));
        } else {
            this.mAlwaysAllow.setText(getString(R.string.lost_internet_access_persist));
        }
        onDismissListener.setView(inflate);
        onDismissListener.create().show();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        boolean z;
        String str;
        String str2;
        if (i == -2 || i == -1) {
            boolean isChecked = this.mAlwaysAllow.isChecked();
            this.mButtonClicked = true;
            if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
                z = i == -1;
                str = z ? "Connect" : "Ignore";
                this.mCM.setAcceptUnvalidated(this.mNetwork, z, isChecked);
                str2 = "NO_INTERNET";
            } else if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                z = i == -1;
                str = z ? "Connect" : "Ignore";
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, z, isChecked);
                str2 = "PARTIAL_CONNECTIVITY";
            } else {
                z = i == -1;
                str = z ? "Switch away" : "Get stuck";
                if (isChecked) {
                    Settings.Global.putString(getContentResolver(), "network_avoid_bad_wifi", z ? "1" : "0");
                } else if (z) {
                    this.mCM.setAvoidUnvalidated(this.mNetwork);
                }
                str2 = "LOST_INTERNET";
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            sb.append(": ");
            sb.append(str);
            sb.append(" network=");
            sb.append(this.mNetwork);
            sb.append(isChecked ? " and remember" : "");
            Log.d("WifiNoInternetDialog", sb.toString());
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || !isKnownAction(intent)) {
            Log.e("WifiNoInternetDialog", "Unexpected intent " + intent + ", exiting");
            finish();
            return;
        }
        this.mAction = intent.getAction();
        Network network = (Network) intent.getParcelableExtra("android.net.extra.NETWORK");
        this.mNetwork = network;
        if (network == null) {
            Log.e("WifiNoInternetDialog", "Can't determine network from intent extra, exiting");
            finish();
            return;
        }
        NetworkRequest build = new NetworkRequest.Builder().clearCapabilities().build();
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.wifi.WifiNoInternetDialog.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onCapabilitiesChanged(Network network2, NetworkCapabilities networkCapabilities) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network2) && networkCapabilities.hasCapability(16)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " validated");
                    WifiNoInternetDialog.this.finish();
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network2) {
                if (WifiNoInternetDialog.this.mNetwork.equals(network2)) {
                    Log.d("WifiNoInternetDialog", "Network " + WifiNoInternetDialog.this.mNetwork + " disconnected");
                    WifiNoInternetDialog.this.finish();
                }
            }
        };
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mCM = connectivityManager;
        connectivityManager.registerNetworkCallback(build, this.mNetworkCallback);
        NetworkInfo networkInfo = this.mCM.getNetworkInfo(this.mNetwork);
        NetworkCapabilities networkCapabilities = this.mCM.getNetworkCapabilities(this.mNetwork);
        if (networkInfo != null && networkInfo.isConnectedOrConnecting() && networkCapabilities != null) {
            String ssid = networkCapabilities.getSsid();
            this.mNetworkName = ssid;
            if (ssid != null) {
                this.mNetworkName = android.net.wifi.WifiInfo.sanitizeSsid(ssid);
            }
            showDialog();
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
        if (isFinishing() && !this.mButtonClicked) {
            if ("android.net.action.PROMPT_PARTIAL_CONNECTIVITY".equals(this.mAction)) {
                this.mCM.setAcceptPartialConnectivity(this.mNetwork, false, false);
            } else if ("android.net.action.PROMPT_UNVALIDATED".equals(this.mAction)) {
                this.mCM.setAcceptUnvalidated(this.mNetwork, false, false);
            }
        }
        super.onDestroy();
    }
}
