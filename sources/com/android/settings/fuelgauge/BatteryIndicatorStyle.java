package com.android.settings.fuelgauge;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class BatteryIndicatorStyle extends Activity {
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private ContentResolver mResolver;

    private void createBatteryIndicatorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.battery_indicator_style);
        builder.setSingleChoiceItems(this.mEntries, findIndexOfValue(String.valueOf(Settings.System.getInt(this.mResolver, "battery_indicator_style", 1))), new DialogInterface.OnClickListener() { // from class: com.android.settings.fuelgauge.BatteryIndicatorStyle.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < BatteryIndicatorStyle.this.mEntryValues.length && i >= 0) {
                    Settings.System.putInt(BatteryIndicatorStyle.this.mResolver, "battery_indicator_style", Integer.parseInt(BatteryIndicatorStyle.this.mEntryValues[i].toString()));
                }
                dialogInterface.dismiss();
                BatteryIndicatorStyle.this.finish();
            }
        });
        builder.setNeutralButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.fuelgauge.BatteryIndicatorStyle.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                BatteryIndicatorStyle.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.fuelgauge.BatteryIndicatorStyle.3
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                BatteryIndicatorStyle.this.finish();
            }
        });
        builder.show();
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
        requestWindowFeature(1);
        getWindow().setBackgroundDrawableResource(17170445);
        this.mEntries = getResources().getTextArray(R.array.battery_indicator_style_entries);
        this.mEntryValues = getResources().getTextArray(R.array.battery_indicator_style_values);
        this.mResolver = getContentResolver();
        createBatteryIndicatorDialog();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
