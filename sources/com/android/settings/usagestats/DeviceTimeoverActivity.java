package com.android.settings.usagestats;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.usagestats.controller.DeviceUsageController;
import com.android.settings.usagestats.utils.CommonUtils;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class DeviceTimeoverActivity extends Activity {
    private AlertDialog alert;

    private void displayAlert(int i) {
        DialogInterface.OnDismissListener onDismissListener = new DialogInterface.OnDismissListener() { // from class: com.android.settings.usagestats.DeviceTimeoverActivity.1
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                DeviceTimeoverActivity.this.finish();
            }
        };
        String format = String.format(getResources().getString(R.string.usagestats_device_timeout_des), Integer.valueOf(i / 60), Integer.valueOf(i % 60));
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.usagestats_device_timeout_title);
        builder.setNegativeButton(getString(R.string.usagestats_device_timeout_negative), new DialogInterface.OnClickListener() { // from class: com.android.settings.usagestats.DeviceTimeoverActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (DeviceTimeoverActivity.this.isFinishing()) {
                    return;
                }
                dialogInterface.dismiss();
                DeviceTimeoverActivity.this.finish();
                DeviceUsageController.prolongMonitor(DeviceTimeoverActivity.this.getApplicationContext(), 60);
            }
        });
        builder.setMessage(format);
        builder.setPositiveButton(getString(R.string.usagestats_device_timeout_positive), new DialogInterface.OnClickListener() { // from class: com.android.settings.usagestats.DeviceTimeoverActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (DeviceTimeoverActivity.this.isFinishing()) {
                    return;
                }
                dialogInterface.dismiss();
                DeviceTimeoverActivity.this.finish();
                DeviceUsageController.stopMonitor(DeviceTimeoverActivity.this.getApplicationContext());
            }
        });
        builder.setCancelable(false);
        builder.setOnDismissListener(onDismissListener);
        AlertDialog create = builder.create();
        this.alert = create;
        create.setCanceledOnTouchOutside(false);
        this.alert.setCancelable(false);
        if (isFinishing()) {
            return;
        }
        this.alert.show();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("finish") && intent.getBooleanExtra("finish", false)) {
            CommonUtils.logE("LR-DeviceTimeoverActivity", "====FinishDirectly====");
            finish();
            return;
        }
        int limitedTimeToday = DeviceUsageController.getLimitedTimeToday(this);
        CommonUtils.log("LR-DeviceTimeoverActivity", "Display DeviceTimeoverActivity， limitedTime=" + limitedTimeToday);
        displayAlert(limitedTimeToday);
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        AlertDialog alertDialog = this.alert;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.alert.dismiss();
        finish();
        DeviceUsageController.stopMonitor(getApplicationContext());
    }
}
