package com.android.settings.deviceinfo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import com.android.settings.credentials.MiuiCredentialsUpdater;
import java.text.DecimalFormat;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiSARCustActivity extends Activity {
    private AlertDialog mAlertDialog;

    private void dismissDialog() {
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mAlertDialog.dismiss();
        this.mAlertDialog = null;
    }

    public static float getDeviceBodySar() {
        return FeatureParser.getFloat("device_body_sar", 0.0f).floatValue();
    }

    public static float getDeviceHeadSar() {
        return FeatureParser.getFloat("device_head_sar", 0.0f).floatValue();
    }

    public static float getDeviceSar() {
        return FeatureParser.getFloat("device_sar", 0.0f).floatValue();
    }

    private boolean showRegulatoryInfoPanel(Context context) {
        if (SystemProperties.getBoolean("ro.miui.google.csp", false)) {
            if (MiuiCredentialsUpdater.isIniaRegion() || "in".equalsIgnoreCase(SystemProperties.get("ro.miui.build.region"))) {
                float deviceBodySar = getDeviceBodySar();
                float deviceHeadSar = getDeviceHeadSar();
                DecimalFormat decimalFormat = new DecimalFormat("0.000");
                if (deviceBodySar != 0.0f && deviceHeadSar != 0.0f) {
                    dismissDialog();
                    this.mAlertDialog = new AlertDialog.Builder(context).setTitle("SAR").setMessage("India SAR 1g limit: 1.6W/Kg\nHead SAR: " + decimalFormat.format(deviceHeadSar) + "W/Kg\nBody SAR: " + decimalFormat.format(deviceBodySar) + "W/Kg(Distance 15mm)").setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.deviceinfo.MiuiSARCustActivity.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            MiuiSARCustActivity.this.finish();
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.deviceinfo.MiuiSARCustActivity.1
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialogInterface) {
                            MiuiSARCustActivity.this.finish();
                        }
                    }).setCancelable(false).show();
                    return true;
                }
                float deviceSar = getDeviceSar();
                if (deviceSar != 0.0f) {
                    dismissDialog();
                    this.mAlertDialog = new AlertDialog.Builder(context).setTitle("SAR").setMessage("India SAR 1g limit: 1.6W/Kg\nSAR value: " + deviceSar + "W/Kg").setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.deviceinfo.MiuiSARCustActivity.4
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            MiuiSARCustActivity.this.finish();
                        }
                    }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.deviceinfo.MiuiSARCustActivity.3
                        @Override // android.content.DialogInterface.OnDismissListener
                        public void onDismiss(DialogInterface dialogInterface) {
                            MiuiSARCustActivity.this.finish();
                        }
                    }).setCancelable(false).show();
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setLayout(-1, -2);
        getWindow().setGravity(17);
        if (showRegulatoryInfoPanel(this)) {
            return;
        }
        finish();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }
}
