package com.android.settings.password;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class SetupSkipDialog extends InstrumentedDialogFragment implements DialogInterface.OnClickListener {
    private int getPasswordSkipMessageRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_password_skip_biometrics_message : z ? R.string.lock_screen_password_skip_face_message : z2 ? R.string.lock_screen_password_skip_fingerprint_message : R.string.lock_screen_password_skip_message;
    }

    private int getPasswordSkipTitleRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_password_skip_biometrics_title : z ? R.string.lock_screen_password_skip_face_title : z2 ? R.string.lock_screen_password_skip_fingerprint_title : R.string.lock_screen_password_skip_title;
    }

    private int getPatternSkipMessageRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_pattern_skip_biometrics_message : z ? R.string.lock_screen_pattern_skip_face_message : z2 ? R.string.lock_screen_pattern_skip_fingerprint_message : R.string.lock_screen_pattern_skip_message;
    }

    private int getPatternSkipTitleRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_pattern_skip_biometrics_title : z ? R.string.lock_screen_pattern_skip_face_title : z2 ? R.string.lock_screen_pattern_skip_fingerprint_title : R.string.lock_screen_pattern_skip_title;
    }

    private int getPinSkipMessageRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_pin_skip_biometrics_message : z ? R.string.lock_screen_pin_skip_face_message : z2 ? R.string.lock_screen_pin_skip_fingerprint_message : R.string.lock_screen_pin_skip_message;
    }

    private int getPinSkipTitleRes(boolean z, boolean z2) {
        return (z && z2) ? R.string.lock_screen_pin_skip_biometrics_title : z ? R.string.lock_screen_pin_skip_face_title : z2 ? R.string.lock_screen_pin_skip_fingerprint_title : R.string.lock_screen_pin_skip_title;
    }

    public static SetupSkipDialog newInstance(boolean z, boolean z2, boolean z3, boolean z4, boolean z5, boolean z6) {
        SetupSkipDialog setupSkipDialog = new SetupSkipDialog();
        Bundle bundle = new Bundle();
        bundle.putBoolean("frp_supported", z);
        bundle.putBoolean("lock_type_pattern", z2);
        bundle.putBoolean("lock_type_alphanumeric", z3);
        bundle.putBoolean("for_fingerprint", z4);
        bundle.putBoolean("for_face", z5);
        bundle.putBoolean("for_biometrics", z6);
        setupSkipDialog.setArguments(bundle);
        return setupSkipDialog;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 573;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        FragmentActivity activity = getActivity();
        if (i != -2) {
            if (i != -1) {
                return;
            }
            activity.setResult(11);
            activity.finish();
            return;
        }
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.requestFocus();
            ((InputMethodManager) activity.getSystemService("input_method")).showSoftInput(currentFocus, 1);
        }
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return onCreateDialogBuilder().create();
    }

    public AlertDialog.Builder onCreateDialogBuilder() {
        int pinSkipTitleRes;
        int pinSkipMessageRes;
        Bundle arguments = getArguments();
        boolean z = arguments.getBoolean("for_face");
        boolean z2 = arguments.getBoolean("for_fingerprint");
        boolean z3 = arguments.getBoolean("for_biometrics");
        if (!z && !z2 && !z3) {
            return new AlertDialog.Builder(getContext()).setPositiveButton(R.string.skip_anyway_button_label, this).setNegativeButton(R.string.go_back_button_label, this).setTitle(R.string.lock_screen_intro_skip_title).setMessage(arguments.getBoolean("frp_supported") ? R.string.lock_screen_intro_skip_dialog_text_frp : R.string.lock_screen_intro_skip_dialog_text);
        }
        boolean z4 = z || z3;
        boolean z5 = z2 || z3;
        if (arguments.getBoolean("lock_type_pattern")) {
            pinSkipTitleRes = getPatternSkipTitleRes(z4, z5);
            pinSkipMessageRes = getPatternSkipMessageRes(z4, z5);
        } else if (arguments.getBoolean("lock_type_alphanumeric")) {
            pinSkipTitleRes = getPasswordSkipTitleRes(z4, z5);
            pinSkipMessageRes = getPasswordSkipMessageRes(z4, z5);
        } else {
            pinSkipTitleRes = getPinSkipTitleRes(z4, z5);
            pinSkipMessageRes = getPinSkipMessageRes(z4, z5);
        }
        return new AlertDialog.Builder(getContext()).setPositiveButton(R.string.skip_lock_screen_dialog_button_label, this).setNegativeButton(R.string.cancel_lock_screen_dialog_button_label, this).setTitle(pinSkipTitleRes).setMessage(pinSkipMessageRes);
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, "skip_dialog");
    }
}
