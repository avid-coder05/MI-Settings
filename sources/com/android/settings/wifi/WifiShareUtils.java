package com.android.settings.wifi;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/* loaded from: classes2.dex */
public class WifiShareUtils {
    public static Account getXiaomiAccount(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType("com.xiaomi");
        if (accountsByType.length > 0) {
            return accountsByType[0];
        }
        return null;
    }

    public static boolean isWifiShareTurnOn(Context context) {
        return false;
    }
}
