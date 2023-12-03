package com.android.settings.sound.coolsound;

import android.content.Context;
import android.content.Intent;
import com.android.settings.MiuiUtils;
import miui.os.Build;

/* loaded from: classes2.dex */
public class CoolSoundUtils {
    private static Boolean supportCoolSound;

    public static boolean isSupportCoolAlarm(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.ALARM_RINGTONE_PICKER");
        return MiuiUtils.getInstance().canFindActivity(context, intent);
    }

    public static boolean isSupportCoolSound(Context context) {
        if (supportCoolSound == null) {
            Intent intent = new Intent();
            intent.setAction("miui.intent.action.COOL_SOUND_PHONE");
            supportCoolSound = Boolean.valueOf(MiuiUtils.getInstance().canFindActivity(context, intent) && !Build.IS_TABLET);
        }
        return supportCoolSound.booleanValue();
    }

    public static int transferToRingtoneType(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 4) {
                        return i != 5 ? -1 : 2;
                    }
                    return 4096;
                }
                return 128;
            }
            return 64;
        }
        return 1;
    }
}
