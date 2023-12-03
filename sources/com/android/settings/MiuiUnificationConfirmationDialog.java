package com.android.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.android.settings.security.MiuiSecurityAndPrivacySettings;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiUnificationConfirmationDialog extends DialogFragment {
    public static MiuiUnificationConfirmationDialog newInstance(boolean z) {
        MiuiUnificationConfirmationDialog miuiUnificationConfirmationDialog = new MiuiUnificationConfirmationDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("compliant", z);
        miuiUnificationConfirmationDialog.setArguments(bundle);
        return miuiUnificationConfirmationDialog;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        final MiuiSecurityAndPrivacySettings miuiSecurityAndPrivacySettings = (MiuiSecurityAndPrivacySettings) getParentFragment();
        final boolean z = getArguments().getBoolean("compliant");
        return new AlertDialog.Builder(getActivity()).setTitle(R.string.lock_settings_profile_unification_dialog_title).setMessage(z ? R.string.lock_settings_profile_unification_dialog_body : R.string.lock_settings_profile_unification_dialog_uncompliant_body).setPositiveButton(z ? R.string.lock_settings_profile_unification_dialog_confirm : R.string.lock_settings_profile_unification_dialog_uncompliant_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiUnificationConfirmationDialog.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (z) {
                    miuiSecurityAndPrivacySettings.launchConfirmDeviceLockForUnification();
                } else {
                    miuiSecurityAndPrivacySettings.unifyUncompliantLocks();
                }
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create();
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        ((MiuiSecurityAndPrivacySettings) getParentFragment()).updateUnificationPreference();
    }

    public void show(MiuiSecurityAndPrivacySettings miuiSecurityAndPrivacySettings) {
        FragmentManager childFragmentManager = miuiSecurityAndPrivacySettings.getChildFragmentManager();
        if (childFragmentManager.findFragmentByTag("unification_dialog") == null) {
            show(childFragmentManager, "unification_dialog");
        }
    }
}
