package com.android.settings.stat.commonswitch;

import android.content.Context;

/* loaded from: classes2.dex */
public class SwitchStatHelper {
    public static void traceSwitchEvent(Context context) {
        new IntelligentServiceSwitch().track(context);
        new BrightnessModeSwitch().track(context);
        new TalkbackSwitch().track(context);
        new ScreenEnhanceEngineSwitch().track(context);
        new HapticSwitch().track(context);
        new HapticSeekBarLevel().track(context);
        new PaperModeSwitch().track(context);
    }
}
