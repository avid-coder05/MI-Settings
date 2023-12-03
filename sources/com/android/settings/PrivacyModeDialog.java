package com.android.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.security.ChooseLockSettingsHelper;
import com.android.settings.recommend.PageIndexManager;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PrivacyModeDialog extends Activity {

    /* loaded from: classes.dex */
    private class CloseDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        private Context mContext;
        private Dialog mDialog;

        CloseDialogReceiver(Context context, Dialog dialog) {
            this.mContext = context;
            this.mDialog = dialog;
            IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
            context.registerReceiver(this, intentFilter);
        }

        @Override // android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialogInterface) {
            this.mContext.unregisterReceiver(this);
            PrivacyModeDialog.this.finish();
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            this.mDialog.cancel();
        }
    }

    @Override // android.app.Activity
    @Deprecated
    protected Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext(), R.style.AlertDialog_Theme_DayNight);
            builder.setTitle(R.string.privacy_mode_dialog_title);
            builder.setMessage(R.string.privacy_mode_dialog_message);
            builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.PrivacyModeDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    new ChooseLockSettingsHelper(PrivacyModeDialog.this).setPrivacyModeEnabled(true);
                }
            });
            builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            create.setOnDismissListener(new CloseDialogReceiver(getApplicationContext(), create));
            create.getWindow().setType(PageIndexManager.PAGE_SUBSCREEN_SETTINGS);
            return create;
        }
        return super.onCreateDialog(i);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        showDialog(1);
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
