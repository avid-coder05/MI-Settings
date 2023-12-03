package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiTipActivity extends AppCompatActivity {
    private int mDialogType;

    private String getCancelString() {
        int i = this.mDialogType;
        return i != 0 ? i != 1 ? "" : getString(R.string.wifi_connect_cmcc_dialog_cancel) : getString(R.string.wifi_off_airplane_on_cancel);
    }

    private String getCheckboxString() {
        int i = this.mDialogType;
        return i != 0 ? i != 1 ? "" : getString(R.string.wifi_connect_cmcc_dialog_not_remind) : getString(R.string.wifi_off_airplane_on_not_remind);
    }

    public static boolean getCmccConnectedTipValue(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("donot_remind_wifi_cmcc_connected_dialog", false);
    }

    private String getConfirmString() {
        int i = this.mDialogType;
        return i != 0 ? i != 1 ? "" : getString(R.string.wifi_connect_cmcc_dialog_confirm) : getString(R.string.wifi_off_airplane_on_confirm);
    }

    private String getMessage() {
        int i = this.mDialogType;
        return i != 0 ? i != 1 ? "" : getString(R.string.wifi_connect_cmcc_dialog_content) : getString(R.string.wifi_off_airplane_on_content);
    }

    public static String getPreferenceKey(int i) {
        return i != 0 ? i != 1 ? "" : "donot_remind_wifi_cmcc_connected_dialog" : "donot_remind_wifi_off_airplane_on_dialog";
    }

    private String getTitleString() {
        int i = this.mDialogType;
        return i != 0 ? i != 1 ? "" : getString(R.string.wifi_connect_cmcc_dialog_title) : getString(R.string.wifi_off_airplane_on_title);
    }

    public static void setCmccConnectedTipValue(Context context, boolean z) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("donot_remind_wifi_cmcc_connected_dialog", z).commit();
    }

    private void showWifiTipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getTitleString());
        builder.setMessage(getMessage());
        builder.setCheckBox(true, getCheckboxString());
        builder.setNegativeButton(getCancelString(), new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiTipActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (WifiTipActivity.this.mDialogType == 1) {
                    ((WifiManager) WifiTipActivity.this.getSystemService("wifi")).disconnect();
                }
                dialogInterface.dismiss();
                WifiTipActivity.this.finish();
            }
        });
        builder.setPositiveButton(getConfirmString(), new DialogInterface.OnClickListener() { // from class: com.android.settings.wifi.WifiTipActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (((AlertDialog) dialogInterface).isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(WifiTipActivity.this).edit().putBoolean(WifiTipActivity.getPreferenceKey(WifiTipActivity.this.mDialogType), true).commit();
                }
                dialogInterface.dismiss();
                WifiTipActivity.this.finish();
            }
        });
        builder.create().show();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDialogType = getIntent().getIntExtra("extra_dialog_type", 0);
        if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(getPreferenceKey(this.mDialogType), false)) {
            finish();
        } else {
            showWifiTipDialog();
        }
    }
}
