package micloud.compat.independent.utils;

import android.content.Context;

/* loaded from: classes2.dex */
class RelocationCacheCompat_Base implements IRelocationCacheCompat {
    @Override // micloud.compat.independent.utils.IRelocationCacheCompat
    public void cacheHostList(Context context, String str) {
        context.getSharedPreferences("pref_relocation_cache", 0).edit().putString("pref_micloud_hosts_v2", str).commit();
    }

    @Override // micloud.compat.independent.utils.IRelocationCacheCompat
    public void cacheXiaomiAccountName(Context context, String str) {
        context.getSharedPreferences("pref_relocation_cache", 0).edit().putString("pref_micloud_accountname_v2", str).commit();
    }

    @Override // micloud.compat.independent.utils.IRelocationCacheCompat
    public String getCachedHostList(Context context) {
        return context.getSharedPreferences("pref_relocation_cache", 0).getString("pref_micloud_hosts_v2", "");
    }

    @Override // micloud.compat.independent.utils.IRelocationCacheCompat
    public String getCachedXiaomiAccountName(Context context) {
        return context.getSharedPreferences("pref_relocation_cache", 0).getString("pref_micloud_accountname_v2", "");
    }
}
