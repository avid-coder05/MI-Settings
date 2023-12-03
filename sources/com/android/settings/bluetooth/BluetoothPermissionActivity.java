package com.android.settings.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.preference.Preference;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class BluetoothPermissionActivity extends AppCompatActivity implements DialogInterface.OnClickListener, Preference.OnPreferenceChangeListener, DialogInterface.OnDismissListener {
    private BluetoothDevice mDevice;
    private AlertDialog mDialog;
    private View mView;
    private TextView messageView;
    private int mRequestType = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.bluetooth.BluetoothPermissionActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL") && intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2) == BluetoothPermissionActivity.this.mRequestType) {
                if (BluetoothPermissionActivity.this.mDevice.equals((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"))) {
                    BluetoothPermissionActivity.this.dismissDialog();
                }
            }
        }
    };
    private boolean mReceiverRegistered = false;

    private View createConnectionDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(R.layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(R.id.message);
        this.messageView = textView;
        textView.setText(getString(R.string.bluetooth_connect_access_dialog_content, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    private View createMapDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(R.layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(R.id.message);
        this.messageView = textView;
        textView.setText(getString(R.string.bluetooth_message_access_dialog_content, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    private View createPhonebookDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        View inflate = getLayoutInflater().inflate(R.layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(R.id.message);
        this.messageView = textView;
        textView.setText(getString(R.string.bluetooth_phonebook_access_dialog_content, new Object[]{createRemoteName, createRemoteName}));
        return this.mView;
    }

    private View createSapDialogView() {
        String createRemoteName = Utils.createRemoteName(this, this.mDevice);
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TelephonyManager.class);
        View inflate = getLayoutInflater().inflate(R.layout.bluetooth_access, (ViewGroup) null);
        this.mView = inflate;
        TextView textView = (TextView) inflate.findViewById(R.id.message);
        this.messageView = textView;
        textView.setText(getString(R.string.bluetooth_sim_card_access_dialog_content, new Object[]{createRemoteName, createRemoteName, telephonyManager.getLine1Number()}));
        return this.mView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissDialog() {
        finish();
    }

    private void onNegative() {
        Log.d("BluetoothPermissionActivity", "onNegative");
        sendReplyIntentToReceiver(false, true);
    }

    private void onPositive() {
        Log.d("BluetoothPermissionActivity", "onPositive");
        sendReplyIntentToReceiver(true, true);
        finish();
    }

    private void showDialog(String str, int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(str);
        Log.i("BluetoothPermissionActivity", "showDialog() Request type: " + this.mRequestType + " this: " + this);
        if (i == 1) {
            builder.setView(createConnectionDialogView());
        } else if (i == 2) {
            builder.setView(createPhonebookDialogView());
        } else if (i == 3) {
            builder.setView(createMapDialogView());
        } else if (i == 4) {
            builder.setView(createSapDialogView());
        }
        builder.setPositiveButton(getString(i == 1 ? R.string.bluetooth_connect_access_dialog_positive : R.string.allow), this);
        builder.setNegativeButton(getString(i == 1 ? R.string.bluetooth_connect_access_dialog_negative : R.string.request_manage_bluetooth_permission_dont_allow), this);
        builder.setOnDismissListener(this);
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        Log.i("BluetoothPermissionActivity", "Back button pressed! ignoring");
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            onNegative();
        } else if (i != -1) {
        } else {
            onPositive();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addPrivateFlags(524288);
        Intent intent = getIntent();
        if (!intent.getAction().equals("android.bluetooth.device.action.CONNECTION_ACCESS_REQUEST")) {
            Log.e("BluetoothPermissionActivity", "Error: this activity may be started only with intent ACTION_CONNECTION_ACCESS_REQUEST");
            finish();
            return;
        }
        this.mDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        this.mRequestType = intent.getIntExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", 2);
        Log.i("BluetoothPermissionActivity", "onCreate() Request type: " + this.mRequestType);
        int i = this.mRequestType;
        if (i == 1) {
            showDialog(getString(R.string.bluetooth_connect_access_dialog_title), this.mRequestType);
        } else if (i == 2) {
            showDialog(getString(R.string.bluetooth_phonebook_access_dialog_title), this.mRequestType);
        } else if (i == 3) {
            showDialog(getString(R.string.bluetooth_message_access_dialog_title), this.mRequestType);
        } else if (i != 4) {
            Log.e("BluetoothPermissionActivity", "Error: bad request type: " + this.mRequestType);
            finish();
            return;
        } else {
            showDialog(getString(R.string.bluetooth_sim_card_access_dialog_title), this.mRequestType);
        }
        registerReceiver(this.mReceiver, new IntentFilter("android.bluetooth.device.action.CONNECTION_ACCESS_CANCEL"));
        this.mReceiverRegistered = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        if (this.mReceiverRegistered) {
            unregisterReceiver(this.mReceiver);
            this.mReceiverRegistered = false;
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        finish();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        return true;
    }

    @VisibleForTesting
    void sendReplyIntentToReceiver(boolean z, boolean z2) {
        Intent intent = new Intent("android.bluetooth.device.action.CONNECTION_ACCESS_REPLY");
        Log.i("BluetoothPermissionActivity", "sendReplyIntentToReceiver() Request type: " + this.mRequestType + " mReturnPackage");
        intent.putExtra("android.bluetooth.device.extra.CONNECTION_ACCESS_RESULT", z ? 1 : 2);
        intent.putExtra("android.bluetooth.device.extra.ALWAYS_ALLOWED", z2);
        intent.putExtra("android.bluetooth.device.extra.DEVICE", this.mDevice);
        intent.putExtra("android.bluetooth.device.extra.ACCESS_REQUEST_TYPE", this.mRequestType);
        sendBroadcast(intent, "android.permission.BLUETOOTH_CONNECT");
    }
}
