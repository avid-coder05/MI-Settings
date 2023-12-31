package com.android.settingslib.drawer;

import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
public final class CategoryKey {
    public static final Map<String, String> KEY_COMPAT_MAP;

    static {
        HashMap hashMap = new HashMap();
        KEY_COMPAT_MAP = hashMap;
        hashMap.put("com.android.settings.category.wireless", "com.android.settings.category.ia.wireless");
        hashMap.put("com.android.settings.category.device", "com.android.settings.category.ia.system");
        hashMap.put("com.android.settings.category.personal", "com.android.settings.category.ia.system");
        hashMap.put("com.android.settings.category.system", "com.android.settings.category.ia.system");
    }
}
