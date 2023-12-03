package com.android.settings.notification;

import android.app.AutomaticZenRule;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

/* loaded from: classes2.dex */
public class MiuiSilentModeReceiver extends BroadcastReceiver {
    private boolean addRule(Context context) {
        return addZenRule(SilentModeUtils.createAutoZenRuleFromDND(context), context) != null;
    }

    private String addZenRule(AutomaticZenRule automaticZenRule, Context context) {
        try {
            String addAutomaticZenRule = NotificationManager.from(context).addAutomaticZenRule(automaticZenRule);
            Log.d("MiuiSilentModeReceiver", "" + NotificationManager.from(context).getAutomaticZenRule(addAutomaticZenRule));
            return addAutomaticZenRule;
        } catch (Exception e) {
            Log.e("MiuiSilentModeReceiver", "Exception : " + e);
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00ce  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00e8  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x012b  */
    /* JADX WARN: Removed duplicated region for block: B:56:? A[RETURN, SYNTHETIC] */
    @Override // android.content.BroadcastReceiver
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onReceive(android.content.Context r18, android.content.Intent r19) {
        /*
            Method dump skipped, instructions count: 308
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.notification.MiuiSilentModeReceiver.onReceive(android.content.Context, android.content.Intent):void");
    }
}
