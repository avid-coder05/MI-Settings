package com.android.settings.operator.kddi;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.android.settings.R;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import miui.telephony.TelephonyManagerEx;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class KDDIDataConnectionDialog {
    public static void setDataEnabled(Activity activity) {
        if (Build.VERSION.SDK_INT >= 28) {
            Settings.Global.putInt(activity.getContentResolver(), "device_provisioning_mobile_data", 1);
            Settings.Global.putInt(activity.getContentResolver(), "device_first_using_data", 1);
        }
        TelephonyManagerEx.getDefault().enableDataConnectivity();
    }

    public static void showDataConnectionDialog(final Activity activity) {
        AlertDialog create = new AlertDialog.Builder(activity).create();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        create.getWindow().setGravity(17);
        builder.setTitle(R.string.dialog_wifi_title);
        builder.setMessage(R.string.dialog_wifi_context);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_wifi_button_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.kddi.KDDIDataConnectionDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                KDDIDataConnectionDialog.setDataEnabled(activity);
                Settings.System.putInt(activity.getContentResolver(), "pref_set_mobile_data_show", 0);
                if (TelephonyManager.getDefault().getDataState() != 2) {
                    activity.startActivityForResult(new Intent(activity, MiuiMobileDataUsedActivity.class), 1);
                    return;
                }
                MiStatInterfaceUtils.trackEvent("provision_wifi_skip");
                OneTrackInterfaceUtils.track("provision_wifi_skip", null);
                activity.setResult(-1);
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_wifi_button_negative, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.kddi.KDDIDataConnectionDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                MiStatInterfaceUtils.trackEvent("provision_wifi_skip");
                OneTrackInterfaceUtils.track("provision_wifi_skip", null);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
