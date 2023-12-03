package com.android.settings.connecteddevice.usb;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import com.android.settings.R;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MtpConnectionActivity extends Activity {
    private AlertDialog mCheckBoxDialog;
    private Context mContext;
    private BroadcastReceiver mDisconnectedReceiver = new BroadcastReceiver() { // from class: com.android.settings.connecteddevice.usb.MtpConnectionActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (!"android.hardware.usb.action.USB_STATE".equals(intent.getAction()) || intent.getBooleanExtra(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, false) || MtpConnectionActivity.this.isDestroyed()) {
                return;
            }
            MtpConnectionActivity.this.mCheckBoxDialog.dismiss();
        }
    };
    private DialogInterface.OnClickListener onClickListener;
    private DialogInterface.OnDismissListener onDismissListener;

    /* JADX INFO: Access modifiers changed from: private */
    public void showTips() {
        startActivity(new Intent("android.intent.action.VIEW", Uri.parse(getResources().getConfiguration().locale.getCountry().equals("CN") ? "https://cdn.cnbj1.fds.api.mi-img.com/usb-solution/index.html" : "https://cdn.cnbj1.fds.api.mi-img.com/usb-solution/index2.html")));
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.d("MtpConnectionActivity", "display Mtp Connection Failed dialog");
        Context applicationContext = getApplicationContext();
        this.mContext = applicationContext;
        if (Settings.System.getInt(applicationContext.getContentResolver(), "mtp_connection_not_remind", 0) == 1) {
            finish();
            return;
        }
        if (getActionBar() != null) {
            getActionBar().hide();
        }
        getWindow().getDecorView().setAlpha(0.0f);
        this.onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.MtpConnectionActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == -2) {
                    MtpConnectionActivity.this.showTips();
                }
                Settings.System.putInt(MtpConnectionActivity.this.mContext.getContentResolver(), "mtp_connection_not_remind", MtpConnectionActivity.this.mCheckBoxDialog.isChecked() ? 1 : 0);
                dialogInterface.dismiss();
                MtpConnectionActivity.this.finish();
            }
        };
        this.onDismissListener = new DialogInterface.OnDismissListener() { // from class: com.android.settings.connecteddevice.usb.MtpConnectionActivity.3
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                Settings.System.putInt(MtpConnectionActivity.this.mContext.getContentResolver(), "mtp_connection_not_remind", MtpConnectionActivity.this.mCheckBoxDialog.isChecked() ? 1 : 0);
                MtpConnectionActivity.this.finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(getString(R.string.mtp_connection_failed_title)).setMessage(getString(R.string.mtp_connection_failed)).setCheckBox(false, getString(R.string.mtp_connection_not_remind)).setCancelable(true).setPositiveButton(getString(R.string.mtp_solution_button_cancel), this.onClickListener).setNegativeButton(getString(R.string.mtp_solution_button_ok), this.onClickListener).setOnDismissListener(this.onDismissListener);
        AlertDialog create = builder.create();
        this.mCheckBoxDialog = create;
        create.show();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mCheckBoxDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mCheckBoxDialog.dismiss();
    }

    @Override // android.app.Activity
    public void onStart() {
        super.onStart();
        registerReceiver(this.mDisconnectedReceiver, new IntentFilter("android.hardware.usb.action.USB_STATE"));
    }

    @Override // android.app.Activity
    protected void onStop() {
        unregisterReceiver(this.mDisconnectedReceiver);
        super.onStop();
    }
}
