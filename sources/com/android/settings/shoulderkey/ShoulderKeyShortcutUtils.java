package com.android.settings.shoulderkey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.recommend.PageIndexManager;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ShoulderKeyShortcutUtils {
    private static ShoulderKeyShortcutUtils mShoulderKeyShortcutUtils;
    private Context mContext;
    private boolean mDialogAlreadyShown;
    private boolean mDoNotShowDialogAgain;
    private DialogInterface.OnClickListener mOnPositiveButtonClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.shoulderkey.ShoulderKeyShortcutUtils.1
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (((AlertDialog) dialogInterface).isChecked()) {
                Settings.Secure.putIntForUser(ShoulderKeyShortcutUtils.this.mContext.getContentResolver(), "do_not_show_shoulder_key_shortcut_prompt", 1, -2);
                ShoulderKeyShortcutUtils.this.mDoNotShowDialogAgain = true;
            }
            ShoulderKeyShortcutUtils.this.mDialogAlreadyShown = false;
            Intent intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent.putExtra(":android:show_fragment", "com.android.settings.shoulderkey.ShortcutSettings");
            intent.putExtra(":settings:show_fragment_title", ShoulderKeyShortcutUtils.this.mContext.getText(R.string.shoulder_key_shortcut_settings));
            intent.addFlags(343932928);
            ShoulderKeyShortcutUtils.this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
        }
    };
    private DialogInterface.OnClickListener mOnNegativeButtonClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.shoulderkey.ShoulderKeyShortcutUtils.2
        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            if (((AlertDialog) dialogInterface).isChecked()) {
                Settings.Secure.putIntForUser(ShoulderKeyShortcutUtils.this.mContext.getContentResolver(), "do_not_show_shoulder_key_shortcut_prompt", 1, -2);
                ShoulderKeyShortcutUtils.this.mDoNotShowDialogAgain = true;
            }
            ShoulderKeyShortcutUtils.this.mDialogAlreadyShown = false;
        }
    };

    private ShoulderKeyShortcutUtils(Context context) {
        this.mContext = context;
        this.mDoNotShowDialogAgain = Settings.Secure.getIntForUser(context.getContentResolver(), "do_not_show_shoulder_key_shortcut_prompt", 0, -2) == 1;
    }

    public static ShoulderKeyShortcutUtils getInstance(Context context) {
        if (mShoulderKeyShortcutUtils == null) {
            mShoulderKeyShortcutUtils = new ShoulderKeyShortcutUtils(context);
        }
        return mShoulderKeyShortcutUtils;
    }

    private void showAlertDialog() {
        AlertDialog create = new AlertDialog.Builder(this.mContext, R.style.AlertDialog_Theme_DayNight).setTitle(R.string.set_shoulder_key_shortcut_title).setMessage(R.string.set_shoulder_key_shortcut_message).setCheckBox(false, this.mContext.getText(R.string.set_shoulder_key_shortcut_checkbox_message)).setCancelable(false).setPositiveButton(R.string.set_shoulder_key_shortcut_positive_button, this.mOnPositiveButtonClickListener).setNegativeButton(R.string.set_shoulder_key_shortcut_negative_button, this.mOnNegativeButtonClickListener).create();
        create.getWindow().setType(PageIndexManager.PAGE_FONT_SIZE_WEIGHT_SETTINGS);
        create.show();
    }

    public void showPrompt() {
        if (this.mDoNotShowDialogAgain || this.mDialogAlreadyShown) {
            return;
        }
        showAlertDialog();
        this.mDialogAlreadyShown = true;
    }
}
