package com.android.settings.wifi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import androidx.preference.PreferenceManager;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiConnectionDialog extends AppCompatActivity implements DialogInterface.OnClickListener {
    private WifiConfiguration mConfig;
    private int mDialogType;

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setCancelable(false);
        builder.setTitle(getDialogTitle());
        builder.setMessage(getMessage());
        builder.setCheckBox(!isRemind(this), getString(R.string.wifi_datanetwork_switch_not_remind));
        builder.setNegativeButton(getString(17039369), this);
        builder.setPositiveButton(getString(17039379), this);
        builder.show();
    }

    private CharSequence getDialogTitle() {
        return (2 == this.mDialogType && this.mConfig == null) ? getString(R.string.wifi_switch_to_gsm_title) : getString(R.string.wifi_available_title);
    }

    private CharSequence getMessage() {
        int i = this.mDialogType;
        if (1 == i) {
            return isWifiAutoConnectAsk(this) ? getString(R.string.msg_wlan_signal_found, new Object[]{this.mConfig.SSID}) : getString(R.string.wifi_signal_found_msg);
        } else if (2 == i) {
            return getString(this.mConfig != null ? R.string.wifi_signal_found_msg : R.string.wifi_switch_to_gsm_message);
        } else {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isRemind(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "wifi_dialog_remind_type", 0) == 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isRemindExpired(Context context) {
        return SystemClock.elapsedRealtime() - PreferenceManager.getDefaultSharedPreferences(context).getLong("donot_remind_switch_to_wifi_dialog", 2147483647L) >= 3600000;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isWifiAutoConnectAsk(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "wifi_connect_type", 0) == 2;
    }

    private void setIsRemind(boolean z) {
        Settings.System.putInt(getContentResolver(), "wifi_dialog_remind_type", z ? 1 : 0);
    }

    private void setWifiConnectionValue(long j) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("donot_remind_switch_to_wifi_dialog", j).commit();
    }

    private void showWifiList() {
        startActivity(new Intent("android.settings.WIFI_SETTINGS"));
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            int i2 = this.mDialogType;
            if (1 != i2 || this.mConfig == null) {
                if (2 == i2) {
                    if (this.mConfig != null) {
                        showWifiList();
                    } else {
                        TelephonyManager.from(this).setDataEnabled(true);
                    }
                }
            } else if (isWifiAutoConnectAsk(this)) {
                ((WifiManager) getSystemService("wifi")).connect(this.mConfig.networkId, null);
                setWifiConnectionValue(2147483647L);
            } else {
                showWifiList();
            }
            setIsRemind(!((AlertDialog) dialogInterface).isChecked());
        } else if (i == -2) {
            if (1 == this.mDialogType && this.mConfig != null && isWifiAutoConnectAsk(this)) {
                setWifiConnectionValue(SystemClock.elapsedRealtime());
            } else if (2 == this.mDialogType && this.mConfig != null) {
                Intent intent = new Intent("miui.intent.action.SELECT_WIFI_AP");
                intent.putExtra("extra_best_ap", (Parcelable) null);
                startActivity(intent);
            }
        }
        dialogInterface.dismiss();
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        getWindow().requestFeature(1);
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.alpha = 0.0f;
        getWindow().setAttributes(attributes);
        super.onCreate(bundle);
        String action = getIntent().getAction();
        if ("miui.intent.action.SWITCH_TO_WIFI".equals(action)) {
            this.mDialogType = 1;
            WifiConfiguration wifiConfiguration = (WifiConfiguration) getIntent().getParcelableExtra("extra_best_ap");
            this.mConfig = wifiConfiguration;
            if (wifiConfiguration == null) {
                Log.e("SwitchToWifiDialog", "config is null");
                finish();
                return;
            }
        } else if (!"miui.intent.action.SELECT_WIFI_AP".equals(action)) {
            Log.e("SwitchToWifiDialog", "unknown action: " + action);
            finish();
            return;
        } else {
            this.mDialogType = 2;
            this.mConfig = (WifiConfiguration) getIntent().getParcelableExtra("extra_best_ap");
        }
        createDialog();
    }
}
