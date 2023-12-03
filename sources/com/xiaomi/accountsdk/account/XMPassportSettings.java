package com.xiaomi.accountsdk.account;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import java.io.File;
import miui.accounts.ExtraAccountManager;

/* loaded from: classes2.dex */
public class XMPassportSettings {
    @SuppressLint({"StaticFieldLeak"})
    private static volatile Application sApplication;
    @SuppressLint({"StaticFieldLeak"})
    private static volatile Context sGlobalContext;

    private static Context getGlobalContext() {
        return sApplication != null ? sApplication : sGlobalContext;
    }

    public static String getUserAgent() {
        return XMPassportUserAgent.getUserAgent(sApplication);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isStaging() {
        boolean exists = new File("/data/system/xiaomi_account_preview").exists();
        Context globalContext = getGlobalContext();
        return (globalContext == null || ExtraAccountManager.XIAOMI_ACCOUNT_PACKAGE_NAME.equals(globalContext.getPackageName())) ? exists : exists || globalContext.getSharedPreferences("staging_sp", 0).getBoolean("is_staging", false);
    }
}
