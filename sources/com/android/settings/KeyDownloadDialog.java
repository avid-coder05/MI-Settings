package com.android.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class KeyDownloadDialog {
    private String mConfirmString;
    private String mContent;
    private Context mContext;
    private AlertDialog.Builder mDialogBuilder;
    private IOnClickListener mListener;
    private String mResourceName;
    private String mTitle;

    /* loaded from: classes.dex */
    public interface IOnClickListener {
        void onDismiss();

        void onNegativeBtnClick();

        void onPositiveBtnClick();

        void onReTipBtnClick();
    }

    public KeyDownloadDialog(Context context, String str, String str2, String str3, String str4, IOnClickListener iOnClickListener) {
        this.mContext = context;
        this.mTitle = str;
        this.mContent = str2;
        this.mResourceName = str3;
        this.mConfirmString = str4;
        this.mListener = iOnClickListener;
    }

    private void showDialog(View view) {
        AlertDialog.Builder onDismissListener = new AlertDialog.Builder(this.mContext).setTitle(this.mTitle).setView(view).setPositiveButton(this.mConfirmString, new DialogInterface.OnClickListener() { // from class: com.android.settings.KeyDownloadDialog.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (KeyDownloadDialog.this.mListener != null) {
                    KeyDownloadDialog.this.mListener.onPositiveBtnClick();
                }
            }
        }).setNegativeButton(MiuiShortcut$Key.getResourceForKey("launch_noapp_dialog_cancel", this.mContext), new DialogInterface.OnClickListener() { // from class: com.android.settings.KeyDownloadDialog.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (KeyDownloadDialog.this.mListener != null) {
                    KeyDownloadDialog.this.mListener.onNegativeBtnClick();
                }
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.settings.KeyDownloadDialog.2
            @Override // android.content.DialogInterface.OnDismissListener
            public void onDismiss(DialogInterface dialogInterface) {
                if (KeyDownloadDialog.this.mListener != null) {
                    KeyDownloadDialog.this.mListener.onDismiss();
                }
            }
        });
        this.mDialogBuilder = onDismissListener;
        onDismissListener.show();
    }

    public void show() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.knock_dialog_view, (ViewGroup) null);
        TextView textView = (TextView) inflate.findViewById(R.id.re_download);
        textView.setText(MiuiShortcut$Key.getResourceForKey("launch_noapp_dialog_install", this.mContext));
        if (TextUtils.isEmpty(this.mResourceName)) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            textView.setText(this.mResourceName);
            textView.getPaint().setFlags(8);
            textView.getPaint().setAntiAlias(true);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.KeyDownloadDialog.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (KeyDownloadDialog.this.mListener != null) {
                        KeyDownloadDialog.this.mListener.onReTipBtnClick();
                    }
                }
            });
        }
        ((TextView) inflate.findViewById(R.id.content)).setText(this.mContent);
        showDialog(inflate);
    }
}
