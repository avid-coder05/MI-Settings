package com.android.settings.emergency.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import com.android.settings.R;
import com.android.settings.emergency.ui.ReflectUtil;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class FullScreenDialog extends AlertDialog {
    private Context mContext;

    public FullScreenDialog(Context context, int i) {
        super(context, i);
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AlertDialog, android.app.Dialog
    public void onStart() {
        super.onStart();
        Window window = getWindow();
        window.setLayout(-1, -1);
        window.addFlags(4);
        window.setBackgroundDrawable(new ColorDrawable(this.mContext.getResources().getColor(R.color.sos_dialog_window_background)));
        window.getDecorView().setSystemUiVisibility(4866);
        if (Build.VERSION.SDK_INT > 30) {
            try {
                ReflectUtil.callAnyObjectMethod(WindowManager.LayoutParams.class, window.getAttributes(), "setBlurBehindRadius", new Class[]{Integer.TYPE}, 75);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
