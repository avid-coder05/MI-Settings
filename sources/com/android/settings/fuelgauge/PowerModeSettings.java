package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class PowerModeSettings extends Activity {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;

    private void createPowerModeDialog() {
        int i = R.string.power_mode;
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_high_performance", false)) {
            i = R.string.high_performance_title;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(i);
        builder.setSingleChoiceItems(this.mEntries, findIndexOfValue(SystemProperties.get("persist.sys.aries.power_profile", "middle")), new DialogInterface.OnClickListener() { // from class: com.android.settings.fuelgauge.PowerModeSettings.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (i2 < PowerModeSettings.this.mEntryValues.length && i2 >= 0) {
                    String charSequence = PowerModeSettings.this.mEntryValues[i2].toString();
                    SystemProperties.set("persist.sys.aries.power_profile", charSequence);
                    Settings.System.putString(PowerModeSettings.this.getContentResolver(), "power_mode", charSequence);
                    PowerModeSettings.this.sendBroadcast(new Intent("miui.intent.action.POWER_MODE_CHANGE"));
                }
                dialogInterface.dismiss();
                PowerModeSettings.this.finish();
            }
        });
        builder.setNeutralButton(17039360, (DialogInterface.OnClickListener) null);
        builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.fuelgauge.PowerModeSettings.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                PowerModeSettings.this.finish();
            }
        });
    }

    public int findIndexOfValue(String str) {
        CharSequence[] charSequenceArr;
        if (str == null || (charSequenceArr = this.mEntryValues) == null) {
            return -1;
        }
        for (int length = charSequenceArr.length - 1; length >= 0; length--) {
            if (this.mEntryValues[length].equals(str)) {
                return length;
            }
        }
        return -1;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mEntries = getResources().getTextArray(R.array.power_mode_entries);
        this.mEntryValues = MiuiSettings.System.POWER_MODE_VALUES;
        createPowerModeDialog();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
