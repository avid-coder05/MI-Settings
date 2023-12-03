package miui.cta.invoker;

import android.app.Activity;
import android.util.Log;
import miui.cta.CTAManager;
import miui.extension.invoker.Invoker;

/* loaded from: classes3.dex */
public class CTAActivityInvoker implements Invoker {
    private static final String TAG = "ActivityInvoker";

    public final void invoke(String str, Object... objArr) {
        if ("onCreate".equals(str)) {
            CTAManager.showAgreementIfNeed((Activity) objArr[0]);
            return;
        }
        Log.w(TAG, "Action is not supported: " + str);
    }
}
