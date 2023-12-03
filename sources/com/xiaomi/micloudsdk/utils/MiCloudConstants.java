package com.xiaomi.micloudsdk.utils;

import java.io.File;

/* loaded from: classes2.dex */
public class MiCloudConstants {
    public static final boolean USE_PREVIEW = new File("/data/system/xiaomi_account_preview").exists();
    public static final boolean USE_MEMBER_DAILY = new File("/data/system/micloud_member_daily").exists();

    /* loaded from: classes2.dex */
    public static class URL {
        private static final String CURRENT_VERSION;
        public static final String URL_GALLERY_BASE;
        public static final String URL_MICLOUD_FAMILY_INFO;
        public static final String URL_MICLOUD_MEMBER_STATUS_QUERY;
        private static final String URL_MICLOUD_STATUS_BASE;
        public static final String URL_MICLOUD_STATUS_QUERY;
        public static final String URL_RELOCATION_BASE;
        public static final String URL_RICH_MEDIA_BASE;

        static {
            boolean z = MiCloudConstants.USE_PREVIEW;
            String str = z ? "http://statusapi.micloud.preview.n.xiaomi.net" : "http://statusapi.micloud.xiaomi.net";
            URL_MICLOUD_STATUS_BASE = str;
            String str2 = str + "/mic/status/v2";
            CURRENT_VERSION = str2;
            URL_MICLOUD_STATUS_QUERY = str2 + "/user/overview";
            URL_MICLOUD_MEMBER_STATUS_QUERY = str2 + "/user/level";
            URL_MICLOUD_FAMILY_INFO = str2 + "/user/family/quota/used";
            URL_GALLERY_BASE = z ? "http://micloud.preview.n.xiaomi.net" : "http://galleryapi.micloud.xiaomi.net";
            URL_RICH_MEDIA_BASE = z ? "http://api.micloud.preview.n.xiaomi.net" : "http://fileapi.micloud.xiaomi.net";
            URL_RELOCATION_BASE = z ? "http://relocationapi.micloud.preview.n.xiaomi.net" : "http://relocationapi.micloud.xiaomi.net";
        }
    }
}
