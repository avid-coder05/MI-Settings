package com.xiaomi.micloudsdk.request.utils;

import android.content.Context;
import com.xiaomi.micloudsdk.data.IAuthToken;
import micloud.compat.independent.request.RequestEnvBuilderCompat;

/* loaded from: classes2.dex */
public class RequestContext {
    private static Context sContext;
    private static String sRegion;
    private static RequestEnv sRequestEnv = RequestEnvBuilderCompat.build();

    /* loaded from: classes2.dex */
    public interface RequestEnv {
        String getAccountName();

        String getUserAgent();

        void invalidateAuthToken();

        IAuthToken queryAuthToken();

        String queryEncryptedAccountName();
    }

    public static Context getContext() {
        Context context = sContext;
        if (context != null) {
            return context;
        }
        throw new IllegalStateException("sContext=null! Please call Request.init(Context) at Application onCreate");
    }

    public static String getRegion() {
        return sRegion;
    }

    public static RequestEnv getRequestEnv() {
        RequestEnv requestEnv = sRequestEnv;
        if (requestEnv != null) {
            return requestEnv;
        }
        throw new IllegalStateException("RequestEnv has not been initialized yet, please call Request.setRequestEnv(RequestEnv) first!");
    }

    public static String getUserAgent() {
        RequestEnv requestEnv = sRequestEnv;
        if (requestEnv != null) {
            return requestEnv.getUserAgent();
        }
        throw new IllegalStateException("RequestEnv has not been initialized yet, please call Request.setRequestEnv(RequestEnv) first!");
    }

    public static void init(Context context) {
        sContext = context;
    }

    public static void setRegion(String str) {
        sRegion = str;
    }
}
