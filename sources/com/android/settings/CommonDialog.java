package com.android.settings;

import android.app.Activity;
import android.content.DialogInterface;
import java.lang.ref.WeakReference;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class CommonDialog {
    protected Activity mActivity;
    private DialogClickListener mBaseDialogClickListener;
    private DialogInterface.OnClickListener mClickListener;
    private AlertDialog mDialog;
    private String mMessage;
    private String mTitle;

    /* loaded from: classes.dex */
    private static class DialogClickListener implements DialogInterface.OnClickListener {
        private CommonDialog mCommonDialog;
        private WeakReference<CommonDialog> mDialogRef;
        private boolean mWeakRefEnabled;

        private DialogClickListener(CommonDialog commonDialog, boolean z) {
            if (z) {
                this.mDialogRef = new WeakReference<>(commonDialog);
            } else {
                this.mCommonDialog = commonDialog;
            }
            this.mWeakRefEnabled = z;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            CommonDialog commonDialog = this.mCommonDialog;
            if (this.mWeakRefEnabled) {
                commonDialog = this.mDialogRef.get();
            }
            if (commonDialog != null) {
                commonDialog.onClick(dialogInterface, i);
            }
        }
    }

    protected CommonDialog(Activity activity) {
        this.mActivity = activity;
    }

    public CommonDialog(Activity activity, DialogInterface.OnClickListener onClickListener) {
        this(activity);
        this.mClickListener = onClickListener;
    }

    private void onBuild(AlertDialog alertDialog) {
        alertDialog.setCancelable(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClick(DialogInterface dialogInterface, int i) {
        DialogInterface.OnClickListener onClickListener = this.mClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(dialogInterface, i);
        }
    }

    protected void onPrepareBuild(AlertDialog.Builder builder) {
    }

    public void setTitle(String str) {
        this.mTitle = str;
    }

    public void show() {
        if (this.mDialog == null) {
            this.mBaseDialogClickListener = new DialogClickListener(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mActivity);
            onPrepareBuild(builder);
            AlertDialog create = builder.create();
            this.mDialog = create;
            onBuild(create);
        }
        this.mDialog.setTitle(this.mTitle);
        this.mDialog.setMessage(this.mMessage);
        this.mDialog.show();
    }
}
