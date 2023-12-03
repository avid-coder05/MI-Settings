package com.android.settings.wfd;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.os.Bundle;
import android.util.Log;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiDisplayStatusActivity extends AppCompatActivity {
    private AlertDialog mDialog;
    private DisplayManager mDisplayManager;
    private final BroadcastReceiver mStatusReceiver = new BroadcastReceiver() { // from class: com.android.settings.wfd.WifiDisplayStatusActivity.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            Log.d("WifiDisplayStatus", "Receive ACTION_WIFI_DISPLAY_STATUS_CHANGED");
            WifiDisplayStatus parcelableExtra = intent.getParcelableExtra("android.hardware.display.extra.WIFI_DISPLAY_STATUS");
            if (WifiDisplayStatusActivity.this.mDialog == null || !WifiDisplayStatusActivity.this.mDialog.isShowing()) {
                return;
            }
            if (parcelableExtra == null || parcelableExtra.getActiveDisplayState() == 0) {
                WifiDisplayStatusActivity.this.mDialog.dismiss();
            }
        }
    };

    private void showWifiDisplayStatusDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(getString(R.string.wfd_status_dialog_title));
        builder.setMessage(getString(R.string.wfd_status_dialog_message));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.wfd.WifiDisplayStatusActivity.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("WifiDisplayStatus", "Dialog onDismiss");
                WifiDisplayStatusActivity.this.finish();
            }
        });
        builder.setPositiveButton(getString(R.string.wfd_status_dialog_disconnect), new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplayStatusActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("WifiDisplayStatus", "Dialog positive button onClick");
                WifiDisplayStatusActivity.this.mDisplayManager.disconnectWifiDisplay();
            }
        });
        builder.setNegativeButton(getString(R.string.dlg_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.WifiDisplayStatusActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("WifiDisplayStatus", "Dialog negative button onClick");
                WifiDisplayStatusActivity.this.mDialog.dismiss();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        Log.d("WifiDisplayStatus", "onCreate");
        super.onCreate(bundle);
        this.mDisplayManager = (DisplayManager) getSystemService("display");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        Log.d("WifiDisplayStatus", "onStart");
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
        registerReceiver(this.mStatusReceiver, intentFilter);
        showWifiDisplayStatusDialog();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        Log.d("WifiDisplayStatus", "onStop");
        super.onStop();
        unregisterReceiver(this.mStatusReceiver);
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null && alertDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        this.mDialog = null;
    }
}
