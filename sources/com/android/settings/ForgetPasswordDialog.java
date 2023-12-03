package com.android.settings;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class ForgetPasswordDialog extends AlertDialog {
    private TextView mContentOne;
    private TextView mContentThree;
    private Context mContext;
    private Button mOkBtn;

    public ForgetPasswordDialog(Context context) {
        super(context);
        this.mContext = context;
        initCustomTitle();
    }

    private void initCustomTitle() {
        View inflate = LayoutInflater.from(this.mContext).inflate(R.layout.forget_password_dialog, (ViewGroup) null, false);
        TextView textView = (TextView) inflate.findViewById(R.id.forget_password_dialog_content_one);
        this.mContentOne = textView;
        textView.setText(Html.fromHtml(this.mContext.getResources().getString(R.string.forget_password_dialog_content_one)));
        this.mContentThree = (TextView) inflate.findViewById(R.id.forget_password_dialog_content_three);
        try {
            String string = this.mContext.getResources().getString(R.string.forget_password_dialog_content_three);
            SpannableString spannableString = new SpannableString(string);
            Drawable drawable = "POCO".equals(Build.BRAND) ? this.mContext.getResources().getDrawable(R.drawable.miui_keyguard_forget_password_poco) : this.mContext.getResources().getDrawable(R.drawable.miui_keyguard_forget_password_mi);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, 1);
            int indexOf = string.indexOf(42);
            spannableString.setSpan(imageSpan, indexOf, indexOf + 1, 18);
            this.mContentThree.setText(spannableString);
        } catch (Exception unused) {
        }
        Button button = (Button) inflate.findViewById(R.id.ok);
        this.mOkBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ForgetPasswordDialog.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ForgetPasswordDialog.this.dismiss();
            }
        });
        setCustomTitle(inflate);
    }
}
