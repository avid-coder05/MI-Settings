package com.android.settings.privacypassword;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.ArrayList;
import miui.accounts.ExtraAccountManager;
import miui.security.SecurityManager;

/* loaded from: classes2.dex */
public class XiaomiAccountUtils {
    public static String getLoginedAccountMd5(Context context) {
        String str = loginedXiaomiAccount(context).name;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return PrivacyPasswordUtils.md5Hex(str.getBytes());
    }

    public static boolean isLoginXiaomiAccount(Context context) {
        return loginedXiaomiAccount(context) != null;
    }

    public static void loginAccount(Activity activity, Bundle bundle, AccountManagerCallback<Bundle> accountManagerCallback) {
        SecurityManager securityManager = (SecurityManager) activity.getSystemService("security");
        if (!PrivacyPasswordUtils.appCheckAccess(securityManager, ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME)) {
            PrivacyPasswordUtils.verifyAccountCountDownTimer(securityManager, ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME);
        }
        AccountManager.get(activity).addAccount("com.xiaomi", "passportapi", null, bundle, activity, accountManagerCallback, null);
    }

    public static Account loginedXiaomiAccount(Context context) {
        ArrayList arrayList = new ArrayList();
        for (Account account : AccountManager.get(context).getAccountsByType("com.xiaomi")) {
            arrayList.add(account);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return (Account) arrayList.get(0);
    }
}
