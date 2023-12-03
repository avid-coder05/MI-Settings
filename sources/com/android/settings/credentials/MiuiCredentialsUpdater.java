package com.android.settings.credentials;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemProperties;
import android.text.TextUtils;
import java.io.File;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiCredentialsUpdater {
    private static volatile MiuiCredentialsUpdater sInstance;

    /* JADX WARN: Code restructure failed: missing block: B:26:0x007a, code lost:
    
        if (r1.equalsIgnoreCase("in") != false) goto L6;
     */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0083 A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0085  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getBootHWC() {
        /*
            java.lang.String r0 = "ro.boot.hwc"
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            java.lang.String r2 = "india"
            if (r1 != 0) goto L1b
            java.lang.String r1 = r0.toLowerCase()
            boolean r1 = r1.contains(r2)
            if (r1 == 0) goto L1b
        L19:
            r0 = r2
            goto L7d
        L1b:
            java.lang.String r1 = android.os.Build.DEVICE
            java.lang.String r3 = "monet"
            boolean r3 = r3.equalsIgnoreCase(r1)
            java.lang.String r4 = "ro.boot.hwversion"
            if (r3 == 0) goto L41
            java.lang.String r1 = android.os.SystemProperties.get(r4)
            java.lang.String r2 = "1.12.4"
            boolean r2 = r2.equalsIgnoreCase(r1)
            if (r2 != 0) goto L3d
            java.lang.String r2 = "1.19.2"
            boolean r1 = r2.equalsIgnoreCase(r1)
            if (r1 == 0) goto L7d
        L3d:
            java.lang.String r0 = "qorvo"
            goto L7d
        L41:
            java.lang.String r3 = "ginkgo"
            boolean r1 = r3.equalsIgnoreCase(r1)
            if (r1 == 0) goto L67
            java.lang.String r1 = android.os.SystemProperties.get(r4)
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L7d
            java.lang.String r2 = "3"
            boolean r2 = r1.startsWith(r2)
            if (r2 != 0) goto L63
            java.lang.String r2 = "4"
            boolean r1 = r1.startsWith(r2)
            if (r1 == 0) goto L7d
        L63:
            java.lang.String r0 = "pa2"
            goto L7d
        L67:
            java.lang.String r1 = "ro.miui.build.region"
            java.lang.String r1 = android.os.SystemProperties.get(r1)
            boolean r3 = android.text.TextUtils.isEmpty(r1)
            if (r3 != 0) goto L7d
            java.lang.String r3 = "in"
            boolean r1 = r1.equalsIgnoreCase(r3)
            if (r1 == 0) goto L7d
            goto L19
        L7d:
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 == 0) goto L85
            r0 = 0
            goto L89
        L85:
            java.lang.String r0 = r0.toLowerCase()
        L89:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.credentials.MiuiCredentialsUpdater.getBootHWC():java.lang.String");
    }

    public static String getCarrierName() {
        if (Build.IS_CM_CUSTOMIZATION) {
            return "cm";
        }
        if (Build.IS_CT_CUSTOMIZATION) {
            return "ct";
        }
        return null;
    }

    public static String getCertNumber() {
        String str = SystemProperties.get("ro.product.cert");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String getFactoryId() {
        String str = SystemProperties.get("ro.ril.factory_id");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.toLowerCase();
    }

    public static String getGlobalCertNumber() {
        return SystemProperties.get("ro.product.cert");
    }

    public static MiuiCredentialsUpdater getInstance() {
        if (sInstance == null) {
            synchronized (MiuiCredentialsUpdater.class) {
                if (sInstance == null) {
                    sInstance = new MiuiCredentialsUpdater();
                }
            }
        }
        return sInstance;
    }

    public static String getVendorFactoryId() {
        String str = SystemProperties.get("ro.vendor.ril.factory_id");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return str.toLowerCase();
    }

    public static boolean isIniaRegion() {
        String str = SystemProperties.get("ro.boot.hwc");
        return !TextUtils.isEmpty(str) && str.toLowerCase().contains("india");
    }

    public static boolean supportVerification() {
        return !TextUtils.isEmpty(SystemProperties.get("ro.product.cert"));
    }

    public Drawable getCacheDrawable(Context context) {
        File file = new File(context.getCacheDir(), "credentials.png");
        if (file.exists()) {
            return new BitmapDrawable(context.getResources(), BitmapFactory.decodeFile(file.getAbsolutePath()));
        }
        return null;
    }
}
