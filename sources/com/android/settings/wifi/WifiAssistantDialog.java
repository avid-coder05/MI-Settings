package com.android.settings.wifi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.SlaveWifiUtils;
import java.util.HashMap;
import java.util.Map;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiAssistantDialog extends AppCompatActivity implements DialogInterface.OnDismissListener, View.OnClickListener {
    private ConnectivityManager mConnManager;
    private AlertDialog mDialog;
    private Network mNetwork;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private WifiManager mWifiManager;
    private int mUserCommand = -1;
    private Map<Integer, Integer> mCommandMapping = new HashMap(4);

    private void buildButton(LinearLayout linearLayout, int i, int i2, int i3) {
        Button button = (Button) linearLayout.findViewById(i);
        button.setVisibility(0);
        button.setText(i2);
        button.setOnClickListener(this);
        this.mCommandMapping.put(Integer.valueOf(i), Integer.valueOf(i3));
    }

    private void createDialog(int i) {
        boolean z = (i & 4) != 0;
        boolean z2 = (i & 8) != 0;
        this.mCommandMapping.clear();
        Log.d("WifiAssistantDialog", "WifiAssistantDialog -> createDialog* , candidateType: " + i + ", hasCandidateWifi: " + z + ", hasCandidateData: " + z2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.wifi_assistant_confirm_connection_title);
        LinearLayout linearLayout = (LinearLayout) View.inflate(this, R.layout.wifi_assistant_no_network, null);
        builder.setView(linearLayout);
        TextView textView = (TextView) linearLayout.findViewById(R.id.message);
        if (z && z2) {
            builder.setMessage(R.string.wifi_assistant_explicitly_confirm_wifi_message);
            buildButton(linearLayout, R.id.wifi_candidate, R.string.wifi_assistant_switch_data, 3);
            buildButton(linearLayout, R.id.wifi_positive, R.string.wifi_assistant_switch_wifi, 2);
            buildButton(linearLayout, R.id.wifi_negative, R.string.wifi_assistant_keep_connection, 1);
        } else if (z && !isSlaveWifiConnected()) {
            builder.setMessage(R.string.wifi_assistant_explicitly_confirm_wifi_message);
            buildButton(linearLayout, R.id.wifi_positive, R.string.wifi_assistant_switch_wifi, 2);
            buildButton(linearLayout, R.id.wifi_negative, R.string.wifi_assistant_keep_connection, 1);
        } else if (z2) {
            builder.setMessage(getCurrentWifiSsid());
            buildButton(linearLayout, R.id.wifi_positive, R.string.wifi_assistant_switch_data, 3);
            buildButton(linearLayout, R.id.wifi_negative, R.string.wifi_assistant_keep_connection, 1);
        } else {
            builder.setMessage(R.string.wifi_assistant_explicitly_confirm_disconnect);
            buildButton(linearLayout, R.id.wifi_positive, R.string.wifi_assistant_disconnect, 4);
            buildButton(linearLayout, R.id.wifi_negative, R.string.wifi_assistant_keep_connection, 1);
        }
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.setCanceledOnTouchOutside(false);
        this.mDialog.show();
    }

    private String getCurrentWifiSsid() {
        return AccessPoint.removeDoubleQuotes(this.mWifiManager.getConnectionInfo().getSSID());
    }

    private boolean isSlaveWifiConnected() {
        SlaveWifiUtils.getInstance(this).getWifiSlaveConnectionInfo();
        return false;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0056, code lost:
    
        if (r4 != 4) goto L18;
     */
    @Override // android.view.View.OnClickListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onClick(android.view.View r4) {
        /*
            r3 = this;
            int r4 = r4.getId()
            java.util.Map<java.lang.Integer, java.lang.Integer> r0 = r3.mCommandMapping
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            java.lang.Object r4 = r0.get(r4)
            java.lang.Integer r4 = (java.lang.Integer) r4
            int r4 = r4.intValue()
            r3.mUserCommand = r4
            java.lang.String r0 = "WifiAssistantDialog"
            if (r4 > 0) goto L32
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r1 = "onClick, unknown command: "
            r4.append(r1)
            int r3 = r3.mUserCommand
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            android.util.Log.e(r0, r3)
            return
        L32:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r1 = "onClick, command: "
            r4.append(r1)
            int r1 = r3.mUserCommand
            r4.append(r1)
            java.lang.String r4 = r4.toString()
            android.util.Log.d(r0, r4)
            int r4 = r3.mUserCommand
            r0 = 1
            r1 = 0
            if (r4 == r0) goto L69
            r0 = 2
            if (r4 == r0) goto L61
            r0 = 3
            if (r4 == r0) goto L59
            r0 = 4
            if (r4 == r0) goto L61
            goto L70
        L59:
            android.net.ConnectivityManager r4 = r3.mConnManager
            android.net.Network r0 = r3.mNetwork
            r4.setAvoidUnvalidated(r0)
            goto L70
        L61:
            android.net.ConnectivityManager r4 = r3.mConnManager
            android.net.Network r0 = r3.mNetwork
            r4.setAcceptUnvalidated(r0, r1, r1)
            goto L70
        L69:
            android.net.ConnectivityManager r4 = r3.mConnManager
            android.net.Network r2 = r3.mNetwork
            r4.setAcceptUnvalidated(r2, r0, r1)
        L70:
            miuix.appcompat.app.AlertDialog r3 = r3.mDialog
            r3.dismissWithoutAnimation()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.wifi.WifiAssistantDialog.onClick(android.view.View):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null || !TextUtils.equals(intent.getAction(), "android.net.action.PROMPT_UNVALIDATED")) {
            Log.e("WifiAssistantDialog", "UnExpected intent: " + intent);
            finish();
            return;
        }
        int intExtra = intent.getIntExtra("netId", -1);
        int intExtra2 = intent.getIntExtra("candidate", 0);
        if (intExtra < 0) {
            Log.e("WifiAssistantDialog", "Invalid network id: " + intExtra + " | " + intExtra2);
            finish();
            return;
        }
        Log.d("WifiAssistantDialog", "onCreate, network id: " + intExtra + " | " + intExtra2);
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        this.mWifiManager = wifiManager;
        Network currentNetwork = wifiManager.getCurrentNetwork();
        if (currentNetwork == null || intExtra != Integer.parseInt(currentNetwork.toString())) {
            Log.e("WifiAssistantDialog", "Can't determine network: " + intExtra);
            finish();
            return;
        }
        this.mNetwork = currentNetwork;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mConnManager = connectivityManager;
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(this.mNetwork);
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.wifi.WifiAssistantDialog.1
                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    if (WifiAssistantDialog.this.mNetwork.equals(network) && networkCapabilities.hasCapability(16)) {
                        Log.d("WifiAssistantDialog", "Network " + WifiAssistantDialog.this.mNetwork + " validated");
                        WifiAssistantDialog.this.finish();
                    }
                }

                @Override // android.net.ConnectivityManager.NetworkCallback
                public void onLost(Network network) {
                    if (WifiAssistantDialog.this.mNetwork.equals(network)) {
                        Log.d("WifiAssistantDialog", "Network " + WifiAssistantDialog.this.mNetwork + " disconnected");
                        WifiAssistantDialog.this.finish();
                    }
                }
            };
            this.mConnManager.registerNetworkCallback(new NetworkRequest.Builder().clearCapabilities().build(), this.mNetworkCallback);
            createDialog(intExtra2);
            return;
        }
        Log.d("WifiAssistantDialog", "Network " + this.mNetwork + " is not connected: " + networkInfo);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        ConnectivityManager.NetworkCallback networkCallback = this.mNetworkCallback;
        if (networkCallback != null) {
            this.mConnManager.unregisterNetworkCallback(networkCallback);
            this.mNetworkCallback = null;
        }
        super.onDestroy();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        StringBuilder sb = new StringBuilder();
        sb.append("onDismiss: ");
        AlertDialog alertDialog = this.mDialog;
        sb.append(alertDialog != null ? Boolean.valueOf(alertDialog.isChecked()) : null);
        sb.append(" | ");
        sb.append(this.mUserCommand);
        Log.d("WifiAssistantDialog", sb.toString());
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismissWithoutAnimation();
        }
        super.onStop();
    }
}
