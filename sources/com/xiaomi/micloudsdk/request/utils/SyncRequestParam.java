package com.xiaomi.micloudsdk.request.utils;

import android.os.SystemClock;
import android.text.TextUtils;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/* loaded from: classes2.dex */
public class SyncRequestParam {
    private static volatile long sLastManualSyncTimeInMillis = -1;
    private static AtomicInteger sManualSyncCounter = new AtomicInteger(0);

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void addParam(Map<String, String> map, Map<String, String> map2) {
        long j;
        long j2 = sLastManualSyncTimeInMillis;
        boolean z = true;
        if (j2 != -1) {
            j = SystemClock.uptimeMillis() - j2;
            if (j <= 300000) {
                z = false;
            }
        } else {
            j = 0;
        }
        int i = sManualSyncCounter.get();
        if (TextUtils.isEmpty(map.get("backend"))) {
            map.put("backend", Boolean.toString(z));
        }
        if (TextUtils.isEmpty(map2.get("_backend"))) {
            map2.put("_backend", Boolean.toString(z));
        }
        if (TextUtils.isEmpty(map2.get("_delta"))) {
            map2.put("_delta", Long.toString(j));
        }
        if (TextUtils.isEmpty(map2.get("_count"))) {
            map2.put("_count", Integer.toString(i));
        }
    }
}
