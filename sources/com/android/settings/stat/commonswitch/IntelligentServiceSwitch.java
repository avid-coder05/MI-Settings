package com.android.settings.stat.commonswitch;

import android.content.Context;
import android.provider.MiuiSettings;
import com.android.settings.stat.commonswitch.SwitchStat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class IntelligentServiceSwitch extends SwitchStat {
    private static boolean isIntelligentRingtoneEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "intelligent_recognition_service", true);
    }

    private static boolean isIntelligentRingtoneSlot2Enable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "intelligent_recognition_service_slot2", true);
    }

    @Override // com.android.settings.stat.commonswitch.SwitchStat
    List<SwitchStat.Info> getInfoList(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SwitchStat.Info("intelligent_recognition_service", isIntelligentRingtoneEnable(context)));
        arrayList.add(new SwitchStat.Info("intelligent_recognition_service_slot2", isIntelligentRingtoneSlot2Enable(context)));
        return arrayList;
    }
}
