package com.xiaomi.account.openauth;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import miui.yellowpage.Tag;

/* loaded from: classes2.dex */
public class XiaomiOAuthResults {
    private final Bundle contentBundle;
    private final Error errorResult;
    private final Success successResult;

    /* loaded from: classes2.dex */
    public static class Error {
        public final int errorCode;
        public final String errorMessage;

        public Error(int i, String str) {
            this.errorCode = i;
            this.errorMessage = str;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Error parseBundle(Bundle bundle) {
            return new Error(XiaomiOAuthResults.getIntCompatibly(bundle, "extra_error_code", "error"), XiaomiOAuthResults.getStringCompatibly(bundle, "extra_error_description", "error_description"));
        }

        public String toString() {
            return "errorCode=" + this.errorCode + ",errorMessage=" + this.errorMessage;
        }
    }

    /* loaded from: classes2.dex */
    private static class Success {
        public final String accessToken;
        public final String code;
        public final String expiresIn;
        public final String info;
        public final String macAlgorithm;
        public final String macKey;
        public final String scopes;
        public final String state;
        public final String tokenType;

        public Success(String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9) {
            this.accessToken = str;
            this.expiresIn = str2;
            this.scopes = str3;
            this.state = str4;
            this.tokenType = str5;
            this.macKey = str6;
            this.macAlgorithm = str7;
            this.code = str8;
            this.info = str9;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static Success parseBundle(Bundle bundle) {
            return new Success(XiaomiOAuthResults.getStringCompatibly(bundle, "access_token", "extra_access_token"), XiaomiOAuthResults.getIntOrStringCompatibly(bundle, "expires_in", "extra_expires_in"), XiaomiOAuthResults.getStringCompatibly(bundle, "scope", "extra_scope"), XiaomiOAuthResults.getStringCompatibly(bundle, "state", "extra_state"), XiaomiOAuthResults.getStringCompatibly(bundle, "token_type", "extra_token_type"), XiaomiOAuthResults.getStringCompatibly(bundle, "mac_key", "extra_mac_key"), XiaomiOAuthResults.getStringCompatibly(bundle, "mac_algorithm", "extra_mac_algorithm"), XiaomiOAuthResults.getStringCompatibly(bundle, Tag.TagWebService.CommonResult.RESULT_CODE, "extra_code"), XiaomiOAuthResults.getStringCompatibly(bundle, "info", "info"));
        }

        public String toString() {
            return "accessToken=" + this.accessToken + ",expiresIn=" + this.expiresIn + ",scope=" + this.scopes + ",state=" + this.state + ",tokenType=" + this.tokenType + ",macKey=" + this.macKey + ",macAlogorithm=" + this.macAlgorithm + ",code=" + this.code + ",info=" + this.info;
        }
    }

    private XiaomiOAuthResults(Bundle bundle, Error error) {
        this.contentBundle = bundle;
        this.successResult = null;
        this.errorResult = error;
    }

    private XiaomiOAuthResults(Bundle bundle, Success success) {
        this.contentBundle = bundle;
        this.successResult = success;
        this.errorResult = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getIntCompatibly(Bundle bundle, String str, String str2) {
        try {
            return Integer.parseInt(getIntOrStringCompatibly(bundle, str, str2));
        } catch (NumberFormatException unused) {
            Log.w("XiaomiOAuthResults", "error, return 0 instead:");
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getIntOrStringCompatibly(Bundle bundle, String str, String str2) {
        Object obj;
        String[] strArr = {str, str2};
        for (int i = 0; i < 2; i++) {
            String str3 = strArr[i];
            if (!TextUtils.isEmpty(str3) && bundle.containsKey(str3) && (obj = bundle.get(str3)) != null) {
                return obj instanceof Integer ? ((Integer) obj).toString() : obj.toString();
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getStringCompatibly(Bundle bundle, String str, String str2) {
        return bundle.containsKey(str) ? bundle.getString(str) : bundle.getString(str2);
    }

    public static XiaomiOAuthResults parseBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        return getIntCompatibly(bundle, "extra_error_code", "error") != 0 ? new XiaomiOAuthResults(bundle, Error.parseBundle(bundle)) : new XiaomiOAuthResults(bundle, Success.parseBundle(bundle));
    }

    public String getAccessToken() {
        Success success = this.successResult;
        if (success != null) {
            return success.accessToken;
        }
        return null;
    }

    public String toString() {
        Success success = this.successResult;
        if (success != null) {
            return success.toString();
        }
        Error error = this.errorResult;
        if (error != null) {
            return error.toString();
        }
        throw new IllegalStateException("should not be here.");
    }
}
