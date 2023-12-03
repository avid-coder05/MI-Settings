package micloud.compat.independent.utils;

import android.content.Context;

/* loaded from: classes2.dex */
interface IRelocationCacheCompat {
    void cacheHostList(Context context, String str);

    void cacheXiaomiAccountName(Context context, String str);

    String getCachedHostList(Context context);

    String getCachedXiaomiAccountName(Context context);
}
