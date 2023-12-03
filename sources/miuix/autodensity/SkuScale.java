package miuix.autodensity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import miuix.core.util.SystemProperties;
import miuix.internal.util.DeviceHelper;

/* loaded from: classes5.dex */
public class SkuScale {
    private static final String PRIMARY_SCALE;
    private static final String SECONDARY_SCALE;
    private static float sPrimaryScaleValue;
    private static float sSecondaryScaleValue;

    static {
        String str = SystemProperties.get("ro.miui.autodensity.primaryscale", null);
        PRIMARY_SCALE = str;
        String str2 = SystemProperties.get("ro.miui.autodensity.secondaryscale", null);
        SECONDARY_SCALE = str2;
        sPrimaryScaleValue = 0.0f;
        sSecondaryScaleValue = 0.0f;
        if (!TextUtils.isEmpty(str)) {
            sPrimaryScaleValue = parseSkuScale(str);
        }
        if (!TextUtils.isEmpty(str2)) {
            sSecondaryScaleValue = parseSkuScale(str2);
        }
        if (sSecondaryScaleValue == 0.0f) {
            sSecondaryScaleValue = sPrimaryScaleValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float getSkuScale(Context context) {
        float f = sPrimaryScaleValue;
        return (DeviceHelper.isFoldDevice() && DeviceHelper.isTablet(context)) ? sSecondaryScaleValue : f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasSkuScale() {
        return sPrimaryScaleValue != 0.0f;
    }

    private static float parseSkuScale(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            Log.w("AutoDensity", "catch error: sku scale is not a number", e);
            return 0.0f;
        }
    }
}
