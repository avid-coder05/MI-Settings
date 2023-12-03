package com.android.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ConflictDialog extends DialogFragment {
    public static final boolean SUPPORT_FOD = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    private CheckedCallback mCallback;

    public static boolean aodEnabled(Context context) {
        return FeatureParser.getBoolean("support_aod", false) && Settings.Secure.getInt(context.getContentResolver(), "doze_always_on", 0) == 1;
    }

    public static boolean showColorInversionDialogIfNeeded(Context context, FragmentManager fragmentManager, boolean z, CheckedCallback checkedCallback) {
        boolean z2 = true;
        if (z) {
            boolean aodEnabled = aodEnabled(context);
            if (aodEnabled || SUPPORT_FOD) {
                Bundle bundle = new Bundle(1);
                if (aodEnabled && SUPPORT_FOD) {
                    bundle.putInt("typeName", 3);
                } else if (!aodEnabled) {
                    if (SUPPORT_FOD) {
                        bundle.putInt("typeName", 2);
                    }
                    ConflictDialog conflictDialog = new ConflictDialog();
                    conflictDialog.setCallback(checkedCallback);
                    conflictDialog.setArguments(bundle);
                    conflictDialog.show(fragmentManager, "conflict");
                    return z2;
                } else {
                    bundle.putInt("typeName", 1);
                }
                z2 = false;
                ConflictDialog conflictDialog2 = new ConflictDialog();
                conflictDialog2.setCallback(checkedCallback);
                conflictDialog2.setArguments(bundle);
                conflictDialog2.show(fragmentManager, "conflict");
                return z2;
            }
            return true;
        }
        return true;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i = getArguments() != null ? getArguments().getInt("typeName") : -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (i == 2) {
            builder.setMessage(R.string.close_fod);
            builder.setPositiveButton(R.string.got_it, (DialogInterface.OnClickListener) null);
        } else if (i == 1 || i == 3) {
            builder.setMessage(i == 1 ? R.string.close_aod : R.string.close_fod_aod);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.ConflictDialog.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    if (ConflictDialog.this.mCallback != null) {
                        ConflictDialog.this.mCallback.onCheckResult(false);
                    }
                }
            });
            builder.setPositiveButton(R.string.to_close, new DialogInterface.OnClickListener() { // from class: com.android.settings.ConflictDialog.2
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i2) {
                    if (ConflictDialog.this.mCallback != null) {
                        ConflictDialog.this.mCallback.onCheckResult(true);
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.MAIN");
                    intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
                    intent.putExtra(":settings:show_fragment", "com.android.settings.AodAndLockScreenSettings");
                    intent.putExtra(":android:no_headers", true);
                    ConflictDialog.this.getContext().startActivity(intent);
                }
            });
        }
        return builder.create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        CheckedCallback checkedCallback = this.mCallback;
        if (checkedCallback != null) {
            checkedCallback.onCheckResult(false);
        }
    }

    public void setCallback(CheckedCallback checkedCallback) {
        this.mCallback = checkedCallback;
    }
}
