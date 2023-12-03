package com.android.settings.privacypassword;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import com.android.settings.FingerprintHelper;
import com.android.settings.R;

/* loaded from: classes2.dex */
public class FullScreenDialog extends AlertDialog {
    private Context mContext;
    private FingerprintHelper mFingerprintHelper;

    public FullScreenDialog(Context context, int i, FingerprintHelper fingerprintHelper) {
        super(context, i);
        this.mContext = context;
        this.mFingerprintHelper = fingerprintHelper;
    }

    @Override // android.app.Dialog, android.content.DialogInterface
    public void dismiss() {
        super.dismiss();
        FingerprintHelper fingerprintHelper = this.mFingerprintHelper;
        if (fingerprintHelper != null) {
            fingerprintHelper.cancelIdentify();
        }
    }

    @Override // android.app.Dialog
    protected void onStart() {
        getWindow().setLayout(-1, -1);
        getWindow().setBackgroundDrawable(new ColorDrawable(this.mContext.getResources().getColor(R.color.fod_dialog_window_background)));
        getWindow().addFlags(4);
        getWindow().getDecorView().setSystemUiVisibility(4866);
    }
}
