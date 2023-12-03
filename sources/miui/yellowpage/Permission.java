package miui.yellowpage;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import miui.provider.ExtraSettings;
import miui.yellowpage.Tag;

/* loaded from: classes4.dex */
public class Permission {
    private static final String ACTION_LOCATION_SETTING = "com.miui.yellowpage.intent.action.LOCATION_SETTING";
    private static final String ACTION_USER_NOTICE = "com.miui.yellowpage.intent.action.USER_NOTICE";
    private static final String ALLOW_NETWORKING_TEMPORARILY = "pref_allow_networking_temporarily";
    private static final String LOCATION_MODE = "location_mode";
    private static final int LOCATION_MODE_HIGH_ACCURACY = 3;

    private Permission() {
    }

    public static Intent createLocationSettingIntent() {
        return new Intent(ACTION_LOCATION_SETTING);
    }

    public static Intent createUserNoticeIntent() {
        return new Intent(ACTION_USER_NOTICE);
    }

    public static void enableLocation(Context context) {
        if (Build.VERSION.SDK_INT > 18) {
            Settings.Secure.putInt(context.getContentResolver(), LOCATION_MODE, 3);
        } else {
            Settings.Secure.setLocationProviderEnabled(context.getContentResolver(), "network", true);
        }
    }

    public static boolean locationingAllowed(Context context) {
        return Settings.Secure.isLocationProviderEnabled(context.getContentResolver(), "network");
    }

    public static boolean mipubUploadNotified(Context context) {
        return ExtraSettings.System.getBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_USER_NOTICE_MIPUB_UPLOAD), false);
    }

    public static boolean networkingAllowed(Context context) {
        return networkingAllowedPermanently(context) || networkingAllowedTemporarily(context);
    }

    public static boolean networkingAllowedPermanently(Context context) {
        return ExtraSettings.System.getBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_USER_NOTICE_UPDATE_YP_ONLINE), false);
    }

    private static boolean networkingAllowedTemporarily(Context context) {
        return ExtraSettings.System.getBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(ALLOW_NETWORKING_TEMPORARILY), false);
    }

    public static boolean rollingAdsAllowed(Context context) {
        return ExtraSettings.System.getBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_ROLLING_ADS), true);
    }

    public static void setMipubUploadNotified(Context context, boolean z) {
        ExtraSettings.System.putBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_USER_NOTICE_MIPUB_UPLOAD), z);
    }

    public static void setNetworkingAllowedPermanently(Context context, boolean z) {
        ExtraSettings.System.putBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_USER_NOTICE_UPDATE_YP_ONLINE), z);
    }

    public static void setNetworkingAllowedTemporarily(Context context, boolean z) {
        ExtraSettings.System.putBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(ALLOW_NETWORKING_TEMPORARILY), z);
    }

    public static void setRollingAdsAllowed(Context context, boolean z) {
        ExtraSettings.System.putBoolean(context.getContentResolver(), YellowPageUtils.formatPreferenceKey(Tag.TagPreference.SHOW_ROLLING_ADS), z);
    }
}
