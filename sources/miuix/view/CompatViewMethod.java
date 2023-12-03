package miuix.view;

import android.app.Activity;
import android.os.Build;
import android.view.View;

/* loaded from: classes5.dex */
public class CompatViewMethod {
    public static void setActivityTranslucent(Activity activity, boolean z) {
        if (Build.VERSION.SDK_INT >= 30) {
            activity.setTranslucent(z);
        }
    }

    public static void setForceDarkAllowed(View view, boolean z) {
        if (Build.VERSION.SDK_INT >= 29) {
            view.setForceDarkAllowed(z);
        }
    }
}
