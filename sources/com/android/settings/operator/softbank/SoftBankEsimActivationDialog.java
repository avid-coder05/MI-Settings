package com.android.settings.operator.softbank;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import com.android.settings.R;
import com.android.settingslib.util.OneTrackInterfaceUtils;

/* loaded from: classes2.dex */
public class SoftBankEsimActivationDialog {
    public static void show(final Activity activity) {
        AlertDialog create = new AlertDialog.Builder(activity).create();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        create.getWindow().setGravity(17);
        builder.setTitle(R.string.dialog_softbank_esime_title);
        builder.setMessage(R.string.dialog_softbank_esime_context);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.dialog_esim_button_positive, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.softbank.SoftBankEsimActivationDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                OneTrackInterfaceUtils.track("provision_wifi_skip", null);
                Intent intent = new Intent();
                intent.putExtra("eSim", 0);
                activity.setResult(-1, intent);
                activity.finish();
            }
        });
        builder.setNegativeButton(R.string.dialog_esim_button_negative, new DialogInterface.OnClickListener() { // from class: com.android.settings.operator.softbank.SoftBankEsimActivationDialog.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
}
