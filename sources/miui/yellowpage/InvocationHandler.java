package miui.yellowpage;

import android.content.Context;
import android.os.Bundle;

/* loaded from: classes4.dex */
public abstract class InvocationHandler {
    public static Bundle invoke(Context context, String str) {
        return invoke(context, str, null, null);
    }

    public static Bundle invoke(Context context, String str, String str2) {
        return invoke(context, str, str2, null);
    }

    public static Bundle invoke(Context context, String str, String str2, Bundle bundle) {
        try {
            Bundle call = context.getContentResolver().call(YellowPageContract.INVOACTION_URI, str, str2, bundle);
            if (call != null) {
                return call;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new Bundle();
    }
}
