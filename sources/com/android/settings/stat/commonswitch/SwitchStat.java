package com.android.settings.stat.commonswitch;

import android.content.Context;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.List;

/* loaded from: classes2.dex */
public abstract class SwitchStat {

    /* loaded from: classes2.dex */
    public class Info {
        String key;
        boolean value;

        public Info(String str, boolean z) {
            this.key = str;
            this.value = z;
        }
    }

    Info getInfo(Context context) {
        return null;
    }

    List<Info> getInfoList(Context context) {
        return null;
    }

    public void track(Context context) {
        List<Info> infoList = getInfoList(context);
        if (infoList != null && !infoList.isEmpty()) {
            for (Info info : infoList) {
                MiStatInterfaceUtils.trackSwitchEvent(info.key, info.value);
                OneTrackInterfaceUtils.trackSwitchEvent(info.key, info.value);
            }
        }
        Info info2 = getInfo(context);
        if (info2 != null) {
            MiStatInterfaceUtils.trackSwitchEvent(info2.key, info2.value);
            OneTrackInterfaceUtils.trackSwitchEvent(info2.key, info2.value);
        }
    }
}
