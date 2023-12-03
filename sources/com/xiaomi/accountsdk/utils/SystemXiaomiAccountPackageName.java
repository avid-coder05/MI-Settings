package com.xiaomi.accountsdk.utils;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.text.TextUtils;
import java.util.HashSet;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes2.dex */
public class SystemXiaomiAccountPackageName {
    public static String getValid(Context context) {
        HashSet hashSet = new HashSet();
        for (AuthenticatorDescription authenticatorDescription : AccountManager.get(context).getAuthenticatorTypes()) {
            if (TextUtils.equals(authenticatorDescription.type, "com.xiaomi")) {
                hashSet.add(authenticatorDescription.packageName);
            }
        }
        if (hashSet.contains(ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME)) {
            return ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME;
        }
        if (hashSet.contains("com.xiaomi.controlscreen.account")) {
            return "com.xiaomi.controlscreen.account";
        }
        return null;
    }
}
