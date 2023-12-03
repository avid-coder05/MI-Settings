package miuix.autodensity;

import android.os.Build;
import java.io.File;

/* loaded from: classes5.dex */
public class RootUtil {
    private static boolean sDeviceRooted = checkDeviceRooted();

    private static boolean checkDeviceRooted() {
        String str = Build.TAGS;
        boolean z = true;
        boolean z2 = str != null && str.contains("test-keys");
        if (!z2) {
            String[] strArr = {"/system/bin/su", "/system/xbin/su"};
            for (int i = 0; i < 2; i++) {
                if (new File(strArr[i]).exists()) {
                    break;
                }
            }
        }
        z = z2;
        if (z) {
            DebugUtil.printDensityLog("Current device is rooted");
        }
        return z;
    }

    public static boolean isDeviceRooted() {
        return sDeviceRooted;
    }
}
