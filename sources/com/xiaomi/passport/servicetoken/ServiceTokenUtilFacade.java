package com.xiaomi.passport.servicetoken;

/* loaded from: classes2.dex */
public final class ServiceTokenUtilFacade {
    private static final ServiceTokenUtilFacade sInstance = new ServiceTokenUtilFacade();

    private ServiceTokenUtilFacade() {
    }

    public static ServiceTokenUtilFacade getInstance() {
        return sInstance;
    }

    public IServiceTokenUtil buildMiuiServiceTokenUtil() {
        return new ServiceTokenUtilMiui();
    }
}
