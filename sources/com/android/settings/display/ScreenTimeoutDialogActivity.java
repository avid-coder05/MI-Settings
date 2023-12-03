package com.android.settings.display;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.R;
import java.util.ArrayList;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ScreenTimeoutDialogActivity extends Activity {
    private long mCurrentTimeOut;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;

    private void createTimeOutDialog() {
        int findIndexOfValue = findIndexOfValue(String.valueOf(this.mCurrentTimeOut));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.screen_timeout);
        builder.setSingleChoiceItems(this.mEntries, findIndexOfValue, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ScreenTimeoutDialogActivity.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i < ScreenTimeoutDialogActivity.this.mEntryValues.length && i >= 0) {
                    try {
                        Settings.System.putInt(ScreenTimeoutDialogActivity.this.getContentResolver(), "screen_off_timeout", Integer.parseInt(ScreenTimeoutDialogActivity.this.mEntryValues[i].toString()));
                    } catch (NumberFormatException unused) {
                    }
                }
                dialogInterface.dismiss();
                ScreenTimeoutDialogActivity.this.finish();
            }
        });
        builder.setNeutralButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settings.display.ScreenTimeoutDialogActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                ScreenTimeoutDialogActivity.this.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.display.ScreenTimeoutDialogActivity.3
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialogInterface) {
                ScreenTimeoutDialogActivity.this.finish();
            }
        });
        builder.show();
    }

    private void disableUnusableTimeouts() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService("device_policy");
        long maximumTimeToLock = devicePolicyManager != null ? devicePolicyManager.getMaximumTimeToLock(null) : 0L;
        if (maximumTimeToLock == 0) {
            return;
        }
        CharSequence[] charSequenceArr = this.mEntries;
        CharSequence[] charSequenceArr2 = this.mEntryValues;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i = 0; i < charSequenceArr2.length; i++) {
            if (Long.parseLong(charSequenceArr2[i].toString()) <= maximumTimeToLock) {
                arrayList.add(charSequenceArr[i]);
                arrayList2.add(charSequenceArr2[i]);
            }
        }
        if (arrayList.size() == charSequenceArr.length && arrayList2.size() == charSequenceArr2.length) {
            return;
        }
        this.mEntries = (CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]);
        this.mEntryValues = (CharSequence[]) arrayList2.toArray(new CharSequence[arrayList2.size()]);
        if (this.mCurrentTimeOut > maximumTimeToLock) {
            this.mCurrentTimeOut = maximumTimeToLock;
        }
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
        this.mEntries = getResources().getTextArray(R.array.screen_timeout_entries);
        this.mEntryValues = getResources().getTextArray(R.array.screen_timeout_values);
        this.mCurrentTimeOut = Settings.System.getLong(getContentResolver(), "screen_off_timeout", 30000L);
        disableUnusableTimeouts();
        createTimeOutDialog();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        setVisible(true);
    }
}
