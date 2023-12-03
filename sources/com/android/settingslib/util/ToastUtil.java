package com.android.settingslib.util;

import android.content.Context;
import android.widget.Toast;
import java.lang.ref.WeakReference;

/* loaded from: classes2.dex */
public class ToastUtil {
    private static WeakReference<Toast> sLastToast;

    public static void show(Context context, int i, int i2) {
        show(context, context.getResources().getText(i), i2);
    }

    public static void show(Context context, CharSequence charSequence, int i) {
        WeakReference<Toast> weakReference = sLastToast;
        if (weakReference != null && weakReference.get() != null) {
            sLastToast.get().cancel();
        }
        Toast makeText = Toast.makeText(context, charSequence, i);
        makeText.show();
        sLastToast = new WeakReference<>(makeText);
    }
}
