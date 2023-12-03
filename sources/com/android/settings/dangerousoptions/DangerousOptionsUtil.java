package com.android.settings.dangerousoptions;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.sysprop.DisplayProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MiuiWindowManager$LayoutParams;
import com.android.settings.R;
import com.android.settings.recommend.PageIndexManager;
import com.android.settingslib.accessibility.AccessibilityUtils;
import miui.app.constants.ThemeManagerConstants;

/* loaded from: classes.dex */
public final class DangerousOptionsUtil {
    private static final int NOTIFY_ID = R.string.dangerous_option;
    private static boolean switchAccessServiceOn = false;
    private static boolean switchSelectToSpeakServiceOn = false;
    private static boolean highTextContrast = false;
    private static final SparseIntArray TITLE_IDS = new SparseIntArray() { // from class: com.android.settings.dangerousoptions.DangerousOptionsUtil.1
        {
            put(32, R.string.switch_access_service_on);
            put(64, R.string.switch_select_to_speak_on);
            put(MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE, R.string.debug_layout);
            put(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD, R.string.show_hw_screen_updates);
            put(4096, R.string.strict_mode);
            put(8192, R.string.immediately_destroy_activities);
            put(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION, R.string.show_screen_updates);
            put(128, R.string.accessibility_toggle_high_text_contrast_preference_title);
        }
    };

    private static void cancelNotification(Context context) {
        if (context == null) {
            return;
        }
        ((NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).cancel(NOTIFY_ID);
    }

    public static void checkDangerousOptions(Context context, boolean z) {
        if (isDangerousOptionsHintEnabled(context)) {
            if (z) {
                context.startService(new Intent(context, DangerousOptionsWarningService.class));
            } else {
                sendNotificationIfNeeded(context);
            }
        }
    }

    private static int getResId(int i) {
        for (int i2 = 0; i2 < 32; i2++) {
            int i3 = 1 << i2;
            if ((i & i3) != 0) {
                return TITLE_IDS.get(i3);
            }
        }
        return 0;
    }

    private static boolean getSelectToSpeakServiceState(Context context) {
        return AccessibilityUtils.getEnabledServicesFromSettings(context).contains(new ComponentName("com.google.android.marvin.talkback", "com.google.android.accessibility.selecttospeak.SelectToSpeakService"));
    }

    private static boolean getShowScreenUpdatesState() {
        try {
            IBinder service = ServiceManager.getService("SurfaceFlinger");
            if (service != null) {
                Parcel obtain = Parcel.obtain();
                Parcel obtain2 = Parcel.obtain();
                obtain.writeInterfaceToken("android.ui.ISurfaceComposer");
                service.transact(PageIndexManager.PAGE_ACCESSIBILITY_PHYSICAL, obtain, obtain2, 0);
                obtain2.readInt();
                obtain2.readInt();
                int readInt = obtain2.readInt();
                obtain2.readInt();
                obtain2.readInt();
                obtain2.recycle();
                obtain.recycle();
                return readInt != 0;
            }
        } catch (RemoteException e) {
            Log.e("DangerousOptionsUtil", "getShowScreenUpdatesState err:" + e);
        }
        return false;
    }

    private static boolean getSwitchAccessServiceState(Context context) {
        return AccessibilityUtils.getEnabledServicesFromSettings(context).contains(new ComponentName("com.google.android.marvin.talkback", "com.android.switchaccess.SwitchAccessService"));
    }

    public static boolean isDangerousOptionsHintEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "dangerous_option_hint", 0) == 1 && UserHandle.myUserId() == 0;
    }

    public static void sendNotificationIfNeeded(Context context) {
        if (isDangerousOptionsHintEnabled(context)) {
            try {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION);
                NotificationChannel notificationChannel = new NotificationChannel("DangerousOptionsHintChannel", context.getString(R.string.dangerous_option), 2);
                notificationChannel.enableVibration(false);
                notificationChannel.enableLights(false);
                notificationChannel.setShowBadge(false);
                notificationChannel.setVibrationPattern(new long[]{0});
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
                String tryBuildContentMsg = tryBuildContentMsg(context);
                if (TextUtils.isEmpty(tryBuildContentMsg)) {
                    notificationManager.cancel(NOTIFY_ID);
                } else {
                    Notification.Builder builder = new Notification.Builder(context, "DangerousOptionsHintChannel");
                    builder.setContentTitle(context.getString(R.string.dangerous_option_hint_title));
                    builder.setContentText(tryBuildContentMsg);
                    builder.setSmallIcon(R.drawable.ic_warning_smallicon);
                    Intent intent = new Intent(shouldJumpToAccessibility() ? "android.settings.ACCESSIBILITY_SETTINGS" : "android.settings.APPLICATION_DEVELOPMENT_SETTINGS");
                    intent.setFlags(536870912);
                    builder.setContentIntent(PendingIntent.getActivity(context, 0, intent, 0));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        notificationManager.notify(NOTIFY_ID, builder.build());
                    }
                }
            } catch (Exception e) {
                Log.e("DangerousOptionsUtil", "sendNotificationIfNeeded: ", e);
            }
        }
    }

    private static boolean shouldJumpToAccessibility() {
        return switchAccessServiceOn || switchSelectToSpeakServiceOn || highTextContrast;
    }

    public static void stopDangerousOptionsHint(Context context) {
        cancelNotification(context);
        context.stopService(new Intent(context, DangerousOptionsWarningService.class));
    }

    private static String tryBuildContentMsg(Context context) {
        boolean booleanValue = ((Boolean) DisplayProperties.debug_layout().orElse(Boolean.FALSE)).booleanValue();
        boolean z = SystemProperties.getBoolean("debug.hwui.show_dirty_regions", false);
        boolean z2 = SystemProperties.getBoolean("persist.sys.strictmode.visual", false);
        boolean z3 = Settings.Global.getInt(context.getContentResolver(), "always_finish_activities", 0) != 0;
        highTextContrast = Settings.Secure.getInt(context.getContentResolver(), "high_text_contrast_enabled", 0) == 1;
        switchAccessServiceOn = getSwitchAccessServiceState(context);
        switchSelectToSpeakServiceOn = getSelectToSpeakServiceState(context);
        int i = (booleanValue ? MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE : 0) | 0 | (z ? MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD : 0) | (z2 ? 4096 : 0) | (z3 ? 8192 : 0) | (getShowScreenUpdatesState() ? MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_CALL_SCREEN_PROJECTION : 0) | (switchSelectToSpeakServiceOn ? 64 : 0) | (switchAccessServiceOn ? 32 : 0) | (highTextContrast ? 128 : 0);
        int resId = getResId(i);
        if (resId > 0) {
            return context.getString((i & (i + (-1))) != 0 ? R.string.more_dangerous_option_hint : R.string.dangerous_option_hint, context.getString(resId));
        }
        return "";
    }
}
