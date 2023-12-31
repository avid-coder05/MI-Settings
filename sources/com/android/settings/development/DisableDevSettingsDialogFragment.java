package com.android.settings.development;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class DisableDevSettingsDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    static DisableDevSettingsDialogFragment newInstance() {
        return new DisableDevSettingsDialogFragment();
    }

    public static void show(DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment) {
        DisableDevSettingsDialogFragment disableDevSettingsDialogFragment = new DisableDevSettingsDialogFragment();
        disableDevSettingsDialogFragment.setTargetFragment(developmentSettingsDashboardFragment, 0);
        disableDevSettingsDialogFragment.show(developmentSettingsDashboardFragment.getActivity().getSupportFragmentManager(), "DisableDevSettingDlg");
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1591;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        Fragment targetFragment = getTargetFragment();
        if (!(targetFragment instanceof DevelopmentSettingsDashboardFragment)) {
            Log.e("DisableDevSettingDlg", "getTargetFragment return unexpected type");
        }
        DevelopmentSettingsDashboardFragment developmentSettingsDashboardFragment = (DevelopmentSettingsDashboardFragment) targetFragment;
        if (i != -1) {
            developmentSettingsDashboardFragment.onDisableDevelopmentOptionsRejected();
            return;
        }
        developmentSettingsDashboardFragment.onDisableDevelopmentOptionsConfirmed();
        ((PowerManager) getContext().getSystemService(PowerManager.class)).reboot(null);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getActivity()).setMessage(R.string.bluetooth_disable_a2dp_hw_offload_dialog_message).setTitle(R.string.bluetooth_disable_a2dp_hw_offload_dialog_title).setPositiveButton(R.string.bluetooth_disable_a2dp_hw_offload_dialog_confirm, this).setNegativeButton(R.string.bluetooth_disable_a2dp_hw_offload_dialog_cancel, this).create();
    }
}
