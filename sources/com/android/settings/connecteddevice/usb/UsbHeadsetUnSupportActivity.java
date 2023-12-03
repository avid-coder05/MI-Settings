package com.android.settings.connecteddevice.usb;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class UsbHeadsetUnSupportActivity extends Activity {
    private AlertDialog mDialog;

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.usb_headset_not_support_tile);
        builder.setMessage(R.string.usb_headset_not_support_message);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.connecteddevice.usb.UsbHeadsetUnSupportActivity.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                UsbHeadsetUnSupportActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.usb_headset_not_support_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.connecteddevice.usb.UsbHeadsetUnSupportActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                UsbHeadsetUnSupportActivity.this.finish();
            }
        });
        AlertDialog create = builder.create();
        this.mDialog = create;
        create.show();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initDialog();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }
}
