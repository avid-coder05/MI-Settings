package com.android.settings.bluetooth;

import android.content.res.Resources;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.bluetooth.plugin.DeviceNickName;
import java.util.ArrayList;
import java.util.Arrays;
import miui.os.Build;

/* loaded from: classes.dex */
public class FitSplitUtils {
    private FitSplitUtils() {
    }

    public static boolean inLargeScreen() {
        return (Resources.getSystem().getConfiguration().screenLayout & 15) == 3;
    }

    public static final boolean isFitSplit() {
        if (Build.IS_TABLET) {
            return true;
        }
        try {
            if (!new ArrayList(Arrays.asList("cetus", "zizhan")).contains(SystemProperties.get("ro.product.device")) || inLargeScreen()) {
                return DeviceNickName.getSurpportSplit();
            }
            return false;
        } catch (Exception unused) {
            Log.d("splitUtil: ", "not exist surpport-split function");
            return false;
        }
    }
}
