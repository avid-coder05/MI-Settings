package com.android.settings.stat.commonpreference;

import android.content.Context;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class PreferenceStat {

    /* loaded from: classes2.dex */
    public class Info {
        String key;
        String value;

        public Info(String str, Integer num) {
            this.key = str;
            if (num != null) {
                this.value = num.toString();
            }
        }

        public Info(String str, String str2) {
            this.key = str;
            this.value = str2;
        }
    }

    Info getInfo(Context context) {
        return null;
    }

    abstract List<Info> getInfoList(Context context);

    public void track(Context context) {
        List<Info> infoList = getInfoList(context);
        if (infoList != null && !infoList.isEmpty()) {
            for (Info info : infoList) {
                MiStatInterfaceUtils.trackPreferenceValue(info.key, info.value);
                OneTrackInterfaceUtils.trackPreferenceValue(info.key, info.value);
            }
        }
        Info info2 = getInfo(context);
        if (info2 != null) {
            MiStatInterfaceUtils.trackPreferenceValue(info2.key, info2.value);
            OneTrackInterfaceUtils.trackPreferenceValue(info2.key, info2.value);
        }
    }
}
