package com.xiaomi.account.openauth;

import java.io.File;

/* loaded from: classes2.dex */
public class XiaomiOAuthConstants {
    public static final String OAUTH2_API_HOST;
    public static final String OAUTH2_API_URL_BASE;
    public static final String OAUTH2_HOST;
    private static final boolean USE_PREVIEW;

    static {
        boolean exists = new File("/data/system/oauth_staging_preview").exists();
        USE_PREVIEW = exists;
        OAUTH2_HOST = exists ? "http://account.preview.n.xiaomi.net" : "https://account.xiaomi.com";
        OAUTH2_API_URL_BASE = exists ? "http://open.account.preview.n.xiaomi.net" : "https://open.account.xiaomi.com";
        OAUTH2_API_HOST = exists ? "open.account.preview.n.xiaomi.net" : "open.account.xiaomi.com";
    }
}
