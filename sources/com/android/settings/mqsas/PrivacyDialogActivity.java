package com.android.settings.mqsas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PrivacyDialogActivity extends Activity {
    private AlertDialog mAlertDialog;
    private String mDgt;
    private int mType;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.privacy_dialog_activity);
        Intent intent = getIntent();
        this.mType = intent.getIntExtra("type", 0);
        this.mDgt = intent.getStringExtra("dgt");
        showPrivacyDialog();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.mAlertDialog.dismiss();
        super.onDestroy();
    }

    public void showPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mqs_privacy_dialog_title);
        builder.setMessage(R.string.mqs_privacy_dialog_content);
        builder.setCheckBox(true, getResources().getString(R.string.mqs_privacy_automatically_upload));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.mqsas.PrivacyDialogActivity.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                PrivacyDialogActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.mqsas.PrivacyDialogActivity.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                MQSEventManagerDelegate.getInstance().dialogButtonChecked(0, PrivacyDialogActivity.this.mType, PrivacyDialogActivity.this.mDgt, false);
            }
        });
        builder.setNegativeButton(R.string.mqs_privacy_dialog_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.mqsas.PrivacyDialogActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MQSEventManagerDelegate.getInstance().dialogButtonChecked(1, PrivacyDialogActivity.this.mType, PrivacyDialogActivity.this.mDgt, ((AlertDialog) dialogInterface).isChecked());
            }
        });
        builder.setPositiveButton(R.string.mqs_privacy_dialog_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.mqsas.PrivacyDialogActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MQSEventManagerDelegate.getInstance().dialogButtonChecked(2, PrivacyDialogActivity.this.mType, PrivacyDialogActivity.this.mDgt, ((AlertDialog) dialogInterface).isChecked());
            }
        });
        builder.setCancelable(false);
        AlertDialog create = builder.create();
        this.mAlertDialog = create;
        create.show();
    }
}
