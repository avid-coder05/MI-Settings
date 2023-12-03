package com.android.settings;

import android.content.Context;
import android.telephony.TelephonyManager;
import miui.telephony.SubscriptionManager;

/* loaded from: classes.dex */
public class MiuiOptionUtils$Mobile {
    public static int touchDataState(Context context, int i) {
        Object[] objArr = SubscriptionManager.getDefault().getSubscriptionInfoList().size() > 0 ? 1 : null;
        if (i == -1 || objArr == null) {
            return TelephonyManager.from(context).isDataEnabled() ? 1 : 0;
        }
        TelephonyManager.from(context).setDataEnabled(i != 0);
        return i;
    }
}
