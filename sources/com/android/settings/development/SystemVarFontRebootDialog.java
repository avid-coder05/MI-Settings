package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class SystemVarFontRebootDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private boolean mConfirmed;

    public static void show(Fragment fragment) {
        if (fragment.getActivity() != null || fragment.isAdded()) {
            FragmentManager supportFragmentManager = fragment.getActivity().getSupportFragmentManager();
            if (supportFragmentManager.findFragmentByTag("SystemVarFontRebootDialog") == null) {
                SystemVarFontRebootDialog systemVarFontRebootDialog = new SystemVarFontRebootDialog();
                systemVarFontRebootDialog.setTargetFragment(fragment, 0);
                systemVarFontRebootDialog.show(supportFragmentManager, "SystemVarFontRebootDialog");
            }
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -1) {
            this.mConfirmed = true;
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.var_font_switch_dialog_title).setMessage(R.string.var_font_switch_dialog_summary).setPositiveButton(R.string.button_text_reboot_now, this).setNegativeButton(R.string.button_text_cancel, this).create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        Fragment targetFragment = getTargetFragment();
        SystemVarFontDialogHost systemVarFontDialogHost = targetFragment instanceof SystemVarFontDialogHost ? (SystemVarFontDialogHost) targetFragment : null;
        if (!this.mConfirmed) {
            if (systemVarFontDialogHost != null) {
                systemVarFontDialogHost.onSystemVarFontDialogDismissed();
                return;
            }
            return;
        }
        this.mConfirmed = false;
        if (systemVarFontDialogHost != null) {
            systemVarFontDialogHost.onSystemVarFontDialogConfirmed();
            ((PowerManager) getContext().getSystemService(PowerManager.class)).reboot(null);
        }
    }
}
