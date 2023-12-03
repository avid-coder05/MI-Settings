package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class RemoteBugreportActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra("android.app.extra.bugreport_notification_type", -1);
        if (intExtra == 2) {
            new AlertDialog.Builder(this).setMessage(R.string.sharing_remote_bugreport_dialog_message).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.RemoteBugreportActivity.2
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else if (intExtra == 1 || intExtra == 3) {
            new AlertDialog.Builder(this).setTitle(R.string.share_remote_bugreport_dialog_title).setMessage(intExtra == 1 ? R.string.share_remote_bugreport_dialog_message : R.string.share_remote_bugreport_dialog_message_finished).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.RemoteBugreportActivity.5
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    RemoteBugreportActivity.this.finish();
                }
            }).setNegativeButton(R.string.decline_remote_bugreport_action, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).setPositiveButton(R.string.share_remote_bugreport_action, new DialogInterface.OnClickListener() { // from class: com.android.settings.RemoteBugreportActivity.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    RemoteBugreportActivity.this.sendBroadcastAsUser(new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED"), UserHandle.SYSTEM, "android.permission.DUMP");
                    RemoteBugreportActivity.this.finish();
                }
            }).create().show();
        } else {
            Log.e("RemoteBugreportActivity", "Incorrect dialog type, no dialog shown. Received: " + intExtra);
        }
    }
}
