package com.xiaomi.passport.servicetoken;

import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.passport.servicetoken.ServiceTokenResult;
import java.io.IOException;
import miui.payment.PaymentManager;

/* loaded from: classes2.dex */
public final class AMAuthTokenConverter {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static String buildAMAuthToken(ServiceTokenResult serviceTokenResult) {
        if (serviceTokenResult == null) {
            return null;
        }
        String str = serviceTokenResult.security;
        return str == null ? serviceTokenResult.serviceToken : String.format("%s%s%s", serviceTokenResult.serviceToken, ",", str);
    }

    public static ServiceTokenResult fromAMBundle(Bundle bundle, String str) {
        ServiceTokenResult.ErrorCode errorCode;
        if (bundle == null) {
            return new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_UNKNOWN).build();
        }
        if (bundle.containsKey("authtoken")) {
            ServiceTokenResult parseAMAuthToken = parseAMAuthToken(str, bundle.getString("authtoken"), false);
            return parseAMAuthToken != null ? parseAMAuthToken : new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR).errorMessage("invalid auth token").build();
        }
        Intent intent = (Intent) bundle.getParcelable(PaymentManager.KEY_INTENT);
        if (intent != null) {
            return new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_USER_INTERACTION_NEEDED).intent(intent).build();
        }
        if (bundle.containsKey("errorCode")) {
            int i = bundle.getInt("errorCode");
            String string = bundle.getString("errorMessage");
            switch (i) {
                case 1:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_REMOTE_EXCEPTION;
                    break;
                case 2:
                default:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_UNKNOWN;
                    break;
                case 3:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_IOERROR;
                    break;
                case 4:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_CANCELLED;
                    break;
                case 5:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR;
                    break;
                case 6:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR;
                    break;
                case 7:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR;
                    break;
                case 8:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR;
                    break;
                case 9:
                    errorCode = ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR;
                    break;
            }
            return new ServiceTokenResult.Builder(str).errorCode(errorCode).errorMessage(i + "#" + string).build();
        }
        return new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_UNKNOWN).build();
    }

    public static ServiceTokenResult fromAMException(String str, Exception exc) {
        ServiceTokenResult.Builder errorCode = new ServiceTokenResult.Builder(str).errorCode(exc instanceof OperationCanceledException ? ServiceTokenResult.ErrorCode.ERROR_CANCELLED : exc instanceof IOException ? ServiceTokenResult.ErrorCode.ERROR_IOERROR : exc instanceof AuthenticatorException ? ServiceTokenResult.ErrorCode.ERROR_AUTHENTICATOR_ERROR : exc instanceof SecurityException ? ServiceTokenResult.ErrorCode.ERROR_OLD_MIUI_ACCOUNT_MANAGER_PERMISSION_ISSUE : ServiceTokenResult.ErrorCode.ERROR_UNKNOWN);
        StringBuilder sb = new StringBuilder();
        sb.append("error#");
        sb.append(exc != null ? exc.getMessage() : "");
        return errorCode.errorMessage(sb.toString()).errorStackTrace(Log.getStackTraceString(exc)).build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ServiceTokenResult parseAMAuthToken(String str, String str2, boolean z) {
        String str3;
        String str4 = null;
        if (TextUtils.isEmpty(str2)) {
            return null;
        }
        String[] split = str2.split(",");
        if (str != null && str.startsWith("weblogin:")) {
            str3 = split[0];
            if (TextUtils.isEmpty(str3)) {
                return null;
            }
        } else if (split.length != 2 || TextUtils.isEmpty(split[0]) || TextUtils.isEmpty(split[1])) {
            return null;
        } else {
            String str5 = split[0];
            str4 = split[1];
            str3 = str5;
        }
        return new ServiceTokenResult.Builder(str).errorCode(ServiceTokenResult.ErrorCode.ERROR_NONE).serviceToken(str3).security(str4).peeked(z).build();
    }
}
