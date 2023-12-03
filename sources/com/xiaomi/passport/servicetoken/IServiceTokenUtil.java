package com.xiaomi.passport.servicetoken;

import android.content.Context;

/* loaded from: classes2.dex */
public interface IServiceTokenUtil {
    ServiceTokenFuture getServiceToken(Context context, String str);

    ServiceTokenFuture invalidateServiceToken(Context context, ServiceTokenResult serviceTokenResult);
}
