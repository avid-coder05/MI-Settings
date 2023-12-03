package com.android.settings.gestures;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class GestureNavigationBackSensitivityDialog extends InstrumentedDialogFragment {
    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(SeekBar seekBar, DialogInterface dialogInterface, int i) {
        getArguments().putInt("back_sensitivity", seekBar.getProgress());
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1748;
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        View inflate = getActivity().getLayoutInflater().inflate(R.layout.dialog_back_gesture_sensitivity, (ViewGroup) null);
        final SeekBar seekBar = (SeekBar) inflate.findViewById(R.id.back_sensitivity_seekbar);
        seekBar.setProgress(getArguments().getInt("back_sensitivity"));
        return new AlertDialog.Builder(getContext()).setTitle(R.string.back_sensitivity_dialog_title).setMessage(R.string.back_sensitivity_dialog_message).setView(inflate).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() { // from class: com.android.settings.gestures.GestureNavigationBackSensitivityDialog$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                GestureNavigationBackSensitivityDialog.this.lambda$onCreateDialog$0(seekBar, dialogInterface, i);
            }
        }).create();
    }
}
