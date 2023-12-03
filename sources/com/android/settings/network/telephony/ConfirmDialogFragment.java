package com.android.settings.network.telephony;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/* loaded from: classes2.dex */
public class ConfirmDialogFragment extends BaseDialogFragment implements DialogInterface.OnClickListener {

    /* loaded from: classes2.dex */
    public interface OnConfirmListener {
        void onConfirm(int i, boolean z);
    }

    private void informCaller(boolean z) {
        OnConfirmListener onConfirmListener = (OnConfirmListener) getListener(OnConfirmListener.class);
        if (onConfirmListener == null) {
            return;
        }
        onConfirmListener.onConfirm(getTagInCaller(), z);
    }

    public static <T> void show(Activity activity, Class<T> cls, int i, String str, String str2, String str3, String str4) {
        ConfirmDialogFragment confirmDialogFragment = new ConfirmDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", str);
        bundle.putCharSequence("msg", str2);
        bundle.putString("pos_button_string", str3);
        bundle.putString("neg_button_string", str4);
        BaseDialogFragment.setListener(activity, null, cls, i, bundle);
        confirmDialogFragment.setArguments(bundle);
        confirmDialogFragment.show(activity.getFragmentManager(), "ConfirmDialogFragment");
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        informCaller(false);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        informCaller(i == -1);
    }

    @Override // android.app.DialogFragment
    public final Dialog onCreateDialog(Bundle bundle) {
        String string = getArguments().getString("title");
        String string2 = getArguments().getString("msg");
        String string3 = getArguments().getString("pos_button_string");
        String string4 = getArguments().getString("neg_button_string");
        Log.i("Showing dialog with title = %s", string);
        AlertDialog.Builder negativeButton = new AlertDialog.Builder(getContext()).setTitle(string).setPositiveButton(string3, this).setNegativeButton(string4, this);
        if (!TextUtils.isEmpty(string2)) {
            negativeButton.setMessage(string2);
        }
        AlertDialog show = negativeButton.show();
        show.setCanceledOnTouchOutside(false);
        return show;
    }
}
