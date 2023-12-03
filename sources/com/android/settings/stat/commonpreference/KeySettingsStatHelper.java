package com.android.settings.stat.commonpreference;

import android.content.Context;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes2.dex */
public class KeySettingsStatHelper {
    public static String GESTURE_BACK_TAP_KEY = "backtap";
    public static String GESTURE_PAGE_KEY = "visitgesture";
    public static String GESTURE_V_PAGE_KEY = "visitknockV";
    public static String KEY_PAGE_KEY = "visitkey";
    public static String PAGE_CHANGED = "changed";
    public static String PAGE_INIT = "init";
    public static String PAGE_VISIT = "visit";
    public static String STAT_KEY = "shortcutsettings";
    private static volatile KeySettingsStatHelper sInstance;
    private Context mContext;

    /* loaded from: classes2.dex */
    public static class Info {
        String key;
        String value;

        public Info() {
        }

        public Info(String str, String str2) {
            this.key = str;
            this.value = str2;
        }

        public void setKey(String str) {
            this.key = str;
        }

        public void setValue(String str) {
            this.value = str;
        }
    }

    private KeySettingsStatHelper(Context context) {
        this.mContext = context;
    }

    public static KeySettingsStatHelper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (KeySettingsStatHelper.class) {
                if (sInstance == null) {
                    sInstance = new KeySettingsStatHelper(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    public void traceShortcutSettingsEvent(ArrayList<Info> arrayList) {
        HashMap hashMap = new HashMap();
        Iterator<Info> it = arrayList.iterator();
        while (it.hasNext()) {
            Info next = it.next();
            hashMap.put(next.key, next.value);
        }
        OneTrackInterfaceUtils.track(STAT_KEY, hashMap);
    }

    public void traceUserSetting(Map<String, String> map, String str) {
        Info info = new Info();
        if (map.size() != 0) {
            ArrayList<Info> arrayList = new ArrayList<>();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                info.setKey(entry.getKey());
                info.setValue(entry.getValue());
                arrayList.add(info);
            }
            traceShortcutSettingsEvent(arrayList);
            info.setKey(str);
            info.setValue(PAGE_CHANGED);
        } else {
            info.setKey(str);
            info.setValue(PAGE_VISIT);
        }
        traceVisitPageEvent(info);
    }

    public void traceVisitPageEvent(Info info) {
        HashMap hashMap = new HashMap();
        hashMap.put(info.key, info.value);
        OneTrackInterfaceUtils.track(STAT_KEY, hashMap);
    }
}
