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
public class DemoModeWarningDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private boolean mDialogClicked;

    public static void show(Fragment fragment) {
        if (fragment.getActivity() != null || fragment.isAdded()) {
            FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
            if (supportFragmentManager.findFragmentByTag("DemoModeWarningDialog") == null) {
                DemoModeWarningDialog demoModeWarningDialog = new DemoModeWarningDialog();
                demoModeWarningDialog.setTargetFragment(fragment, 0);
                demoModeWarningDialog.show(supportFragmentManager, "DemoModeWarningDialog");
            }
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        DemoModeDialogHost demoModeDialogHost = (DemoModeDialogHost) getTargetFragment();
        if (demoModeDialogHost == null) {
            return;
        }
        if (i != -1) {
            demoModeDialogHost.onEnableDemoModeDismissed();
            return;
        }
        this.mDialogClicked = true;
        demoModeDialogHost.onEnableDemoModeConfirmed();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.demo_mode).setMessage(R.string.open_demo_mode_warning_message).setPositiveButton(R.string.open_demo_mode, this).setNegativeButton(R.string.close_demo_mode, this).create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        if (this.mDialogClicked) {
            this.mDialogClicked = false;
            return;
        }
        DemoModeDialogHost demoModeDialogHost = (DemoModeDialogHost) getTargetFragment();
        if (demoModeDialogHost != null) {
            demoModeDialogHost.onEnableDemoModeDismissed();
        }
    }
}
