package com.xiaomi.onetrack.api;

import com.xiaomi.onetrack.Configuration;

/* loaded from: classes2.dex */
public class a {
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v14 */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v3, types: [int] */
    public static int a(Configuration configuration) {
        if (configuration == null) {
            return 0;
        }
        boolean isGAIDEnable = configuration.isGAIDEnable();
        boolean z = isGAIDEnable;
        if (configuration.isIMSIEnable()) {
            z = isGAIDEnable | true;
        }
        boolean z2 = z;
        if (configuration.isIMEIEnable()) {
            z2 = (z ? 1 : 0) | true;
        }
        ?? r0 = z2;
        if (configuration.isExceptionCatcherEnable()) {
            r0 = (z2 ? 1 : 0) | true;
        }
        return configuration.isOverrideMiuiRegionSetting() ? r0 | 16 : r0;
    }
}
