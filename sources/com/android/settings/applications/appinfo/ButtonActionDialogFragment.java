package com.android.settings.applications.appinfo;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ButtonActionDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    int mId;

    /* loaded from: classes.dex */
    public interface AppButtonsDialogListener {
        void handleDialogClick(int i);
    }

    private AlertDialog createDialog(int i) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }
        if (i == 0 || i == 1) {
            return new AlertDialog.Builder(activity).setMessage(R.string.app_disable_dlg_text).setPositiveButton(R.string.app_disable_dlg_positive, this).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
        }
        if (i != 2) {
            return null;
        }
        return new AlertDialog.Builder(activity).setTitle(R.string.force_stop_dlg_title).setMessage(R.string.force_stop_dlg_text).setPositiveButton(R.string.dlg_ok, this).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
    }

    public static ButtonActionDialogFragment newInstance(int i) {
        ButtonActionDialogFragment buttonActionDialogFragment = new ButtonActionDialogFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("id", i);
        buttonActionDialogFragment.setArguments(bundle);
        return buttonActionDialogFragment;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 558;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        ((AppButtonsDialogListener) getTargetFragment()).handleDialogClick(this.mId);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        int i = getArguments().getInt("id");
        this.mId = i;
        AlertDialog createDialog = createDialog(i);
        if (createDialog != null) {
            return createDialog;
        }
        throw new IllegalArgumentException("unknown id " + this.mId);
    }
}
