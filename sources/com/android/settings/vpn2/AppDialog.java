package com.android.settings.vpn2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import com.android.settings.R;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
class AppDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private final String mLabel;
    private final Listener mListener;
    private final PackageInfo mPackageInfo;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onForget(DialogInterface dialogInterface);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppDialog(Context context, Listener listener, PackageInfo packageInfo, String str) {
        super(context);
        this.mListener = listener;
        this.mPackageInfo = packageInfo;
        this.mLabel = str;
    }

    protected void createButtons() {
        Context context = getContext();
        setButton(-2, context.getString(R.string.vpn_forget), this);
        setButton(-1, context.getString(R.string.vpn_done), this);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == -2) {
            this.mListener.onForget(dialogInterface);
        }
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, androidx.appcompat.app.AppCompatDialog, android.app.Dialog
    public void onCreate(Bundle bundle) {
        setTitle(this.mLabel);
        setMessage(getContext().getString(R.string.vpn_version, this.mPackageInfo.versionName));
        createButtons();
        super.onCreate(bundle);
    }
}
