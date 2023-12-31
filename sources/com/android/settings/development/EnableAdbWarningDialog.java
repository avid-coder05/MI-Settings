package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class EnableAdbWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    public static void show(Fragment fragment) {
        FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
        if (supportFragmentManager.findFragmentByTag("EnableAdbDialog") == null) {
            EnableAdbWarningDialog enableAdbWarningDialog = new EnableAdbWarningDialog();
            enableAdbWarningDialog.setTargetFragment(fragment, 0);
            enableAdbWarningDialog.show(supportFragmentManager, "EnableAdbDialog");
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1222;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        AdbDialogHost adbDialogHost = (AdbDialogHost) getTargetFragment();
        if (adbDialogHost == null) {
            return;
        }
        if (i == -1) {
            adbDialogHost.onEnableAdbDialogConfirmed();
        } else {
            adbDialogHost.onEnableAdbDialogDismissed();
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.adb_warning_title).setMessage(R.string.adb_warning_message).setPositiveButton(17039379, this).setNegativeButton(17039369, this).create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        AdbDialogHost adbDialogHost = (AdbDialogHost) getTargetFragment();
        if (adbDialogHost == null) {
            return;
        }
        adbDialogHost.onEnableAdbDialogDismissed();
    }
}
