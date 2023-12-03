package com.xiaomi.accountsdk.account;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.SystemSettings$System;
import android.text.TextUtils;
import android.util.Base64;
import com.xiaomi.accountsdk.utils.AccountLog;
import com.xiaomi.accountsdk.utils.SystemPropertiesReflection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/* loaded from: classes2.dex */
public class XMPassportUserAgent {
    private static volatile String sUserAgentCache;
    private static volatile String sUserAgentForReplacement;
    private static volatile Set<String> sExtendedUASet = new LinkedHashSet();
    private static ThreadLocal<Set<String>> sCurrentThreadExtendedUA = new ThreadLocal<>();
    private static ThreadLocal<String> sUserAgentCacheLocal = new ThreadLocal<>();
    private static ThreadLocal<String> sWebViewUserAgentCacheLocal = new ThreadLocal<>();

    /* loaded from: classes2.dex */
    private static class UserAgentBuilder {
        private final Context context;
        private final Set<String> extendedUASet;
        private final boolean isWebView;
        private final String majorUserAgent;

        private UserAgentBuilder(Context context, String str, Set<String> set, boolean z) {
            this.context = context;
            this.majorUserAgent = str;
            this.extendedUASet = set;
            this.isWebView = z;
        }

        private String getAppVersion(Context context) {
            if (context == null) {
                return null;
            }
            try {
                return String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
            } catch (PackageManager.NameNotFoundException unused) {
                AccountLog.i("XMPassportUserAgent", context.getPackageName() + " NameNotFound");
                return null;
            }
        }

        public static String getDeviceName() {
            String str = "";
            try {
                str = SystemPropertiesReflection.get(SystemSettings$System.RO_MARKET_NAME, "");
                if (TextUtils.isEmpty(str)) {
                    str = Build.MODEL;
                }
            } catch (Exception unused) {
                AccountLog.w("XMPassportUserAgent", "fail to get marketname or model");
            }
            return (TextUtils.isEmpty(str) || str.length() <= 30) ? str : str.substring(0, 30);
        }

        private String getStrippedPackageName(Context context) {
            String packageName = context == null ? "unknown" : context.getPackageName();
            String[] split = packageName.split("\\.");
            if (split.length > 2) {
                return split[split.length - 2] + "." + split[split.length - 1];
            }
            return packageName;
        }

        public String build() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.majorUserAgent);
            sb.append(" ");
            sb.append("APP/");
            sb.append(getStrippedPackageName(this.context));
            String appVersion = getAppVersion(this.context);
            if (!TextUtils.isEmpty(appVersion)) {
                sb.append(" ");
                sb.append("APPV/");
                sb.append(appVersion);
            }
            if (this.isWebView) {
                sb.append(" ");
                sb.append("XiaoMi/HybridView/");
            }
            String deviceName = getDeviceName();
            if (!TextUtils.isEmpty(deviceName)) {
                sb.append(" ");
                sb.append("MK/");
                sb.append(Base64.encodeToString(deviceName.getBytes(), 2));
            }
            for (String str : this.extendedUASet) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(" ");
                    sb.append(str);
                }
            }
            return sb.toString();
        }
    }

    private static String getDefaultUA() {
        return System.getProperty("http.agent");
    }

    public static synchronized String getUserAgent(Context context) {
        synchronized (XMPassportUserAgent.class) {
            if (TextUtils.isEmpty(sUserAgentCache)) {
                String defaultUA = TextUtils.isEmpty(sUserAgentForReplacement) ? getDefaultUA() : sUserAgentForReplacement;
                if (sCurrentThreadExtendedUA.get() != null) {
                    HashSet hashSet = new HashSet();
                    hashSet.addAll(sExtendedUASet);
                    hashSet.addAll(sCurrentThreadExtendedUA.get());
                    sUserAgentCacheLocal.set(new UserAgentBuilder(context, defaultUA, hashSet, false).build());
                    return sUserAgentCacheLocal.get();
                }
                sUserAgentCache = new UserAgentBuilder(context, defaultUA, sExtendedUASet, false).build();
            }
            return sUserAgentCache;
        }
    }
}
