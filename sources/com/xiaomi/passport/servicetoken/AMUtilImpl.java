package com.xiaomi.passport.servicetoken;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import com.xiaomi.accountsdk.utils.AccountLog;

/* loaded from: classes2.dex */
final class AMUtilImpl implements IAMUtil {
    private final AMKeys amKeys;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AMUtilImpl(AMKeys aMKeys) {
        if (aMKeys == null) {
            throw new IllegalArgumentException("amKeys == null");
        }
        this.amKeys = aMKeys;
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public AccountManagerFuture<Bundle> getAuthToken(Context context, String str, Account account) {
        return AccountManager.get(context).getAuthToken(account, str, (Bundle) null, (Activity) null, (AccountManagerCallback<Bundle>) null, (Handler) null);
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public String getCUserId(Context context, Account account) {
        try {
            return AccountManager.get(context).getUserData(account, this.amKeys.getAmUserDataKeyCUserId());
        } catch (SecurityException e) {
            AccountLog.d("AMUtilImpl", "getSlh", e);
            return null;
        }
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public String getPh(Context context, String str, Account account) {
        try {
            return AccountManager.get(context).getUserData(account, this.amKeys.getAmUserDataKeyPh(str));
        } catch (SecurityException e) {
            AccountLog.d("AMUtilImpl", "getSlh", e);
            return null;
        }
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public String getSlh(Context context, String str, Account account) {
        try {
            return AccountManager.get(context).getUserData(account, this.amKeys.getAmUserDataKeySlh(str));
        } catch (SecurityException e) {
            AccountLog.d("AMUtilImpl", "getSlh", e);
            return null;
        }
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public Account getXiaomiAccount(Context context) {
        Account[] accountsByType = AccountManager.get(context).getAccountsByType(this.amKeys.getType());
        if (accountsByType == null || accountsByType.length <= 0) {
            return null;
        }
        return accountsByType[0];
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public void invalidateAuthToken(Context context, String str) {
        AccountManager.get(context).invalidateAuthToken(this.amKeys.getType(), str);
    }

    @Override // com.xiaomi.passport.servicetoken.IAMUtil
    public String peekAuthToken(Context context, String str, Account account) {
        try {
            return AccountManager.get(context).peekAuthToken(account, str);
        } catch (SecurityException e) {
            AccountLog.d("AMUtilImpl", "peedAuthToken", e);
            return null;
        }
    }
}
