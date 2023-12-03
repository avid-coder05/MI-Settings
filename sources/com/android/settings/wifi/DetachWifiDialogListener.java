package com.android.settings.wifi;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.ViewTreeObserver;

/* loaded from: classes2.dex */
public class DetachWifiDialogListener implements DialogInterface.OnDismissListener {
    private static final String TAG = DetachWifiDialogListener.class.getSimpleName();
    private Activity mActivity;

    public DetachWifiDialogListener(Activity activity) {
        this.mActivity = activity;
    }

    public void clearOnDetach(Dialog dialog) {
        if (dialog.getWindow() == null) {
            return;
        }
        dialog.getWindow().getDecorView().getViewTreeObserver().addOnWindowAttachListener(new ViewTreeObserver.OnWindowAttachListener() { // from class: com.android.settings.wifi.DetachWifiDialogListener.1
            @Override // android.view.ViewTreeObserver.OnWindowAttachListener
            public void onWindowAttached() {
                Log.d(DetachWifiDialogListener.TAG, "dialog Attached to Window");
            }

            @Override // android.view.ViewTreeObserver.OnWindowAttachListener
            public void onWindowDetached() {
                Log.d(DetachWifiDialogListener.TAG, "dialog Detached to Window");
                if (DetachWifiDialogListener.this.mActivity != null) {
                    DetachWifiDialogListener.this.mActivity.finish();
                    DetachWifiDialogListener.this.mActivity = null;
                }
            }
        });
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        Log.d(TAG, "Dialog onDismiss");
    }
}
