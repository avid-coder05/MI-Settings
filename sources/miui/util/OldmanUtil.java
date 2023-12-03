package miui.util;

import miui.os.Build;
import miui.os.SystemProperties;

/* loaded from: classes4.dex */
public class OldmanUtil {
    public static final boolean IS_ELDER_MODE;

    static {
        IS_ELDER_MODE = SystemProperties.getInt(Build.USER_MODE, 0) == 1;
    }
}
