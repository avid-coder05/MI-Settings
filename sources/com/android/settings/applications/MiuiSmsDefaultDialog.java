package com.android.settings.applications;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import com.android.internal.telephony.SmsApplication;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public final class MiuiSmsDefaultDialog extends Activity {
    private int mCurrentDialog;
    private SmsApplication.SmsApplicationData mNewSmsApplicationData;

    private boolean buildDialog(String str) {
        if (TelephonyManager.from(this).getPhoneType() == 0) {
            return false;
        }
        SmsApplication.SmsApplicationData smsApplicationData = SmsApplication.getSmsApplicationData(str, this);
        this.mNewSmsApplicationData = smsApplicationData;
        if (smsApplicationData == null) {
            return false;
        }
        showDialog(1);
        ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this, true);
        return defaultSmsApplication == null || !SmsApplication.getSmsApplicationData(defaultSmsApplication.getPackageName(), this).mPackageName.equals(this.mNewSmsApplicationData.mPackageName);
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String stringExtra = getIntent().getStringExtra(FunctionColumns.PACKAGE);
        setResult(0);
        if (buildDialog(stringExtra)) {
            return;
        }
        finish();
    }

    @Override // android.app.Activity
    @Deprecated
    protected Dialog onCreateDialog(int i) {
        AlertDialog show;
        this.mCurrentDialog = i;
        if (i == 1) {
            show = new AlertDialog.Builder(this).setTitle(R.string.preferred_sms_title).setMessage(getString(R.string.preferred_sms_warning_message, new Object[]{this.mNewSmsApplicationData.getApplicationName(this)})).setPositiveButton(R.string.preferred_sms_warning_use_system, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SmsApplication.setDefaultApplication("com.android.mms", MiuiSmsDefaultDialog.this);
                }
            }).setNegativeButton(R.string.next_button_label, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    MiuiSmsDefaultDialog.this.mCurrentDialog = 2;
                    MiuiSmsDefaultDialog.this.showDialog(2);
                }
            }).show();
            show.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.3
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    if (MiuiSmsDefaultDialog.this.mCurrentDialog != 2) {
                        MiuiSmsDefaultDialog.this.finish();
                    }
                }
            });
        } else if (i != 2) {
            return null;
        } else {
            show = new AlertDialog.Builder(this).setTitle(R.string.preferred_sms_title).setMessage(R.string.preferred_sms_settings).setPositiveButton(R.string.preferred_sms_warning_use_system, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.5
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SmsApplication.setDefaultApplication("com.android.mms", MiuiSmsDefaultDialog.this);
                }
            }).setNegativeButton(17039379, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.4
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    SmsApplication.setDefaultApplication(MiuiSmsDefaultDialog.this.mNewSmsApplicationData.mPackageName, MiuiSmsDefaultDialog.this);
                    MiuiSmsDefaultDialog.this.setResult(-1);
                }
            }).show();
            show.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.applications.MiuiSmsDefaultDialog.6
                @Override // android.content.DialogInterface.OnDismissListener
                public void onDismiss(DialogInterface dialogInterface) {
                    MiuiSmsDefaultDialog.this.finish();
                }
            });
        }
        return show;
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
