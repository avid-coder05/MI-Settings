package com.android.settings.stat.commonswitch;

import android.content.Context;
import com.android.settings.display.ScreenEnhanceEngineStatusCheck;
import com.android.settings.stat.commonswitch.SwitchStat;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class ScreenEnhanceEngineSwitch extends SwitchStat {
    @Override // com.android.settings.stat.commonswitch.SwitchStat
    List<SwitchStat.Info> getInfoList(Context context) {
        ArrayList arrayList = new ArrayList();
        if (ScreenEnhanceEngineStatusCheck.isSrForVideoSupport()) {
            arrayList.add(new SwitchStat.Info("screen_enhance_engine_sr_for_video_status", ScreenEnhanceEngineStatusCheck.getSrForVideoStatus(context)));
        }
        if (ScreenEnhanceEngineStatusCheck.isSrForImageSupport()) {
            arrayList.add(new SwitchStat.Info("screen_enhance_engine_sr_for_image_status", ScreenEnhanceEngineStatusCheck.getSrForImageStatus(context)));
        }
        if (ScreenEnhanceEngineStatusCheck.isAiSupport(context)) {
            arrayList.add(new SwitchStat.Info("screen_enhance_engine_ai_display_status", ScreenEnhanceEngineStatusCheck.getAiStatus(context)));
        }
        if (ScreenEnhanceEngineStatusCheck.isS2hSupport()) {
            arrayList.add(new SwitchStat.Info("screen_enhance_engine_s2h_status", ScreenEnhanceEngineStatusCheck.getS2hStatus(context)));
        }
        if (ScreenEnhanceEngineStatusCheck.isMemcSupport()) {
            arrayList.add(new SwitchStat.Info("screen_enhance_engine_memc_status", ScreenEnhanceEngineStatusCheck.getMemcStatus(context)));
        }
        return arrayList;
    }
}
