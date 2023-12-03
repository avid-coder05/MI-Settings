package com.android.settings.search;

import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.view.IWindowManager;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.Iterator;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
class DisplayUpdateHelper extends BaseSearchUpdateHelper {
    private static final String AUTOMATIC_BRIGHTNESS_RESOURCE = "automatic_brightness";
    private static final String DOZE_RESOURCE = "doze_title";
    private static final String INFINITY_DISPLAY_RESOURCE = "infinity_display_title";
    private static final String LIFT_TO_WAKE_RESOURCE = "lift_to_wake_title";
    private static final String NOTIFICATION_PULSE_RESOURCE = "notification_pulse_title";
    private static final String SCREEN_COLOR_AND_OPTIMIZE_RESOURCE = "screen_color_and_optimize";
    private static final String SCREEN_COLOR_AND_TEMPERATURE_RESOURCE = "screen_color_temperature_and_saturation";
    private static final String SCREEN_EFFECT_RESOURCE = "screen_effect";
    private static final String SCREEN_MONOCHROME_MODE_GLOBAL_RESOURCE = "screen_monochrome_mode_global_title";
    private static final String SCREEN_MONOCHROME_MODE_LOCAL_RESOURCE = "screen_monochrome_mode_local_title";
    private static final String SCREEN_MONOCHROME_MODE_RESOURCE = "screen_monochrome_mode_title";
    private static final String SCREEN_PAPER_MODE_RESOURCE = "screen_paper_mode_title";
    private static final String TITLE_FONT_CURRENT2_RESOURCE = "title_font_current2";
    private static final String TITLE_FONT_SIZE_RESOURCE = "title_font_size";
    private static final String TITLE_LAYOUT_CURRENT2_RESOURCE = "title_layout_current2";

    DisplayUpdateHelper() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        if (FeatureParser.getInteger("screen_effect_supported", 0) == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_EFFECT_RESOURCE);
        }
        if (!FeatureParser.getBoolean("support_screen_effect", false)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_COLOR_AND_OPTIMIZE_RESOURCE);
        } else if (!FeatureParser.getBoolean("support_screen_optimize", false)) {
            BaseSearchUpdateHelper.updatePath(context, arrayList, SCREEN_COLOR_AND_OPTIMIZE_RESOURCE, SCREEN_COLOR_AND_TEMPERATURE_RESOURCE);
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, SCREEN_COLOR_AND_OPTIMIZE_RESOURCE).iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateItemData(context, arrayList, it.next(), "name", context.getResources().getString(R.string.screen_color_temperature_and_saturation));
            }
        }
        if (!MiuiSettings.ScreenEffect.isScreenPaperModeSupported) {
            BaseSearchUpdateHelper.hideTreeByRootResource(context, arrayList, SCREEN_PAPER_MODE_RESOURCE);
        }
        if ((MiuiSettings.ScreenEffect.SCREEN_EFFECT_SUPPORTED & 8) == 0) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_MONOCHROME_MODE_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_MONOCHROME_MODE_GLOBAL_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SCREEN_MONOCHROME_MODE_LOCAL_RESOURCE);
        }
        int i = Build.VERSION.SDK_INT;
        if (i < 21 || i > 22) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, LIFT_TO_WAKE_RESOURCE);
        } else {
            SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            if (sensorManager == null || sensorManager.getDefaultSensor(23) == null) {
                BaseSearchUpdateHelper.hideByResource(context, arrayList, LIFT_TO_WAKE_RESOURCE);
            }
        }
        if (i > 19) {
            boolean z = true;
            String str = miui.os.Build.IS_DEBUGGABLE ? SystemProperties.get("debug.doze.component") : null;
            if (TextUtils.isEmpty(str)) {
                str = context.getResources().getString(17039950);
            }
            if (!TextUtils.isEmpty(str)) {
                Intent intent = new Intent();
                intent.setComponent(ComponentName.unflattenFromString(str));
                if (context.getPackageManager().queryIntentServices(intent, 0).size() > 0) {
                    z = false;
                }
            }
            if (z) {
                BaseSearchUpdateHelper.hideByResource(context, arrayList, DOZE_RESOURCE);
            }
        }
        if (FeatureParser.getBoolean("support_led_light", false)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, NOTIFICATION_PULSE_RESOURCE);
        }
        if (miui.os.Build.IS_TABLET) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, TITLE_FONT_CURRENT2_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, TITLE_LAYOUT_CURRENT2_RESOURCE);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, TITLE_FONT_SIZE_RESOURCE);
        }
        IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        BaseSearchUpdateHelper.insertSearchItem(arrayList, "com.android.settings05053", context.getResources().getString(R.string.automatic_brightness), "zidongtiaozhengliangdu", null, null, null, null, null, "com.android.settings", "com.android.settings.SubSettings", null, "settings_label-display_settings-brightness-automatic_brightness", "com.android.settings.display.BrightnessFragment", 0L);
    }
}
