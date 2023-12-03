package android.media;

import android.content.Context;
import android.net.Uri;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MiuiWindowManager$LayoutParams;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import miui.app.constants.ThemeManagerConstants;
import miui.content.res.ThemeNativeUtils;
import miui.content.res.ThemeResources;
import miui.system.R;

/* loaded from: classes.dex */
public class ExtraRingtoneManager {
    private static final Uri ACTUAL_DEFAULT_RINGTONE_BASE_URI = Uri.parse("file://" + ThemeResources.THEME_MAGIC_PATH + "ringtones");
    private static ArrayList<SoundItem> sRingtoneList = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SoundItem {
        int buildInPathRes;
        Uri mActualDefaultRingtoneUri;
        Uri mDefaultRingtoneUri;
        int mRingtoneType;
        String mSettingType;

        public SoundItem(int i, Uri uri, Uri uri2, String str, int i2) {
            this.mRingtoneType = i;
            this.mActualDefaultRingtoneUri = uri;
            this.mDefaultRingtoneUri = uri2;
            this.mSettingType = str;
            this.buildInPathRes = i2;
        }
    }

    static {
        addSoundItem(1, "ringtone.mp3", Settings.System.DEFAULT_RINGTONE_URI, ThemeManagerConstants.COMPONENT_CODE_RINGTONE, R.string.def_ringtone);
        addSoundItem(2, "notification.mp3", Settings.System.DEFAULT_NOTIFICATION_URI, "notification_sound", R.string.def_notification_sound);
        addSoundItem(4, "alarm.mp3", Settings.System.DEFAULT_ALARM_ALERT_URI, "alarm_alert", R.string.def_alarm_alert);
        addSoundItem(4096, "calendar.mp3", MiuiSettings.System.DEFAULT_CALENDAR_ALERT_URI, "calendar_alert", R.string.def_calendar_alert);
        addSoundItem(8192, "notes.mp3", MiuiSettings.System.DEFAULT_NOTES_ALERT_URI, "notes_alert", R.string.def_notes_alert);
        addSoundItem(8, "sms_delivered_sound.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_RINGTONE_URI, "sms_delivered_sound", R.string.def_sms_delivered_sound);
        addSoundItem(16, "sms_received_sound.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI, "sms_received_sound", R.string.def_sms_received_sound);
        addSoundItem(64, "ringtone_slot_1.mp3", MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_1, "ringtone_sound_slot_1", R.string.def_ringtone_slot_1);
        addSoundItem(128, "ringtone_slot_2.mp3", MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_2, "ringtone_sound_slot_2", R.string.def_ringtone_slot_2);
        addSoundItem(MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE, "sms_received_slot_1.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_1, "sms_received_sound_slot_1", R.string.def_sms_received_sound_slot_1);
        addSoundItem(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD, "sms_received_slot_2.mp3", MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_2, "sms_received_sound_slot_2", R.string.def_sms_received_sound_slot_2);
        addSoundItem(256, "sms_delivered_slot_1.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_1, "sms_delivered_sound_slot_1", R.string.def_sms_delivered_sound_slot_1);
        addSoundItem(512, "sms_delivered_slot_2.mp3", MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_2, "sms_delivered_sound_slot_2", R.string.def_sms_delivered_sound_slot_2);
    }

    private static void addSoundItem(int i, String str, Uri uri, String str2, int i2) {
        sRingtoneList.add(new SoundItem(i, Uri.withAppendedPath(ACTUAL_DEFAULT_RINGTONE_BASE_URI, str), uri, str2, i2));
    }

    private static void copySound(Context context, String str, int i) {
        String path = getDefaultSoundInternalUri(i).getPath();
        if (TextUtils.isEmpty(str) || !new File(str).exists()) {
            ThemeNativeUtils.remove(path);
            return;
        }
        String path2 = ACTUAL_DEFAULT_RINGTONE_BASE_URI.getPath();
        if (!new File(path2).exists()) {
            ThemeNativeUtils.mkdirs(path2);
            ThemeNativeUtils.updateFilePermissionWithThemeContext(path2);
        }
        try {
            ThemeNativeUtils.copy(new File(str).getCanonicalPath(), path);
            ThemeNativeUtils.updateFilePermissionWithThemeContext(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Uri getDefaultSoundInternalUri(int i) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem next = it.next();
            if (i == next.mRingtoneType) {
                return next.mActualDefaultRingtoneUri;
            }
        }
        return null;
    }

    public static String getDefaultSoundName(Context context, int i) {
        return ExtraRingtone.getRingtoneTitle(context, getDefaultSoundSettingUri(context, i), true);
    }

    public static Uri getDefaultSoundSettingUri(Context context, int i) {
        String string;
        String settingForType = getSettingForType(i);
        if (settingForType == null || (string = Settings.System.getString(context.getContentResolver(), settingForType)) == null) {
            return null;
        }
        return Uri.parse(string);
    }

    public static int getDefaultSoundType(Uri uri) {
        if (uri == null) {
            return -1;
        }
        if (uri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
            return 1;
        }
        if (uri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
            return 2;
        }
        if (uri.equals(Settings.System.DEFAULT_ALARM_ALERT_URI)) {
            return 4;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_NOTES_ALERT_URI)) {
            return 8192;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_CALENDAR_ALERT_URI)) {
            return 4096;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_1)) {
            return 64;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_RINGTONE_URI_SLOT_2)) {
            return 128;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_RINGTONE_URI)) {
            return 16;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_1)) {
            return MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_SMS_RECEIVED_SOUND_URI_SLOT_2)) {
            return MiuiWindowManager$LayoutParams.EXTRA_FLAG_FINDDEVICE_KEYGUARD;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_RINGTONE_URI)) {
            return 8;
        }
        if (uri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_1)) {
            return 256;
        }
        return uri.equals(MiuiSettings.System.DEFAULT_SMS_DELIVERED_SOUND_URI_SLOT_2) ? 512 : -1;
    }

    public static Uri getRingtoneUri(Context context, int i) {
        return getDefaultSoundSettingUri(context, i);
    }

    private static String getSettingForType(int i) {
        Iterator<SoundItem> it = sRingtoneList.iterator();
        while (it.hasNext()) {
            SoundItem next = it.next();
            if ((next.mRingtoneType & i) != 0) {
                return next.mSettingType;
            }
        }
        return null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x0024, code lost:
    
        if (r7 != null) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0026, code lost:
    
        r7.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:18:0x0031, code lost:
    
        if (r7 == null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0034, code lost:
    
        return r0;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0039  */
    /* JADX WARN: Type inference failed for: r0v0 */
    /* JADX WARN: Type inference failed for: r0v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r0v2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String resolveSoundPath(android.content.Context r7, android.net.Uri r8) {
        /*
            r0 = 0
            android.content.ContentResolver r1 = r7.getContentResolver()     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            java.lang.String r7 = "_data"
            java.lang.String[] r3 = new java.lang.String[]{r7}     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            r4 = 0
            r5 = 0
            r6 = 0
            r2 = r8
            android.database.Cursor r7 = r1.query(r2, r3, r4, r5, r6)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L2c
            if (r7 == 0) goto L24
            boolean r8 = r7.moveToFirst()     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L35
            if (r8 == 0) goto L24
            r8 = 0
            java.lang.String r8 = r7.getString(r8)     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L35
            r0 = r8
            goto L24
        L22:
            r8 = move-exception
            goto L2e
        L24:
            if (r7 == 0) goto L34
        L26:
            r7.close()
            goto L34
        L2a:
            r8 = move-exception
            goto L37
        L2c:
            r8 = move-exception
            r7 = r0
        L2e:
            r8.printStackTrace()     // Catch: java.lang.Throwable -> L35
            if (r7 == 0) goto L34
            goto L26
        L34:
            return r0
        L35:
            r8 = move-exception
            r0 = r7
        L37:
            if (r0 == 0) goto L3c
            r0.close()
        L3c:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExtraRingtoneManager.resolveSoundPath(android.content.Context, android.net.Uri):java.lang.String");
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x0048  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x004d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static void saveDefaultSound(android.content.Context r3, int r4, android.net.Uri r5) {
        /*
            java.lang.String r0 = getSettingForType(r4)
            if (r0 != 0) goto L7
            return
        L7:
            int r1 = getDefaultSoundType(r5)
            if (r4 != r1) goto Le
            return
        Le:
            if (r5 == 0) goto L3d
            java.lang.String r1 = r5.getAuthority()
            java.lang.String r2 = "media"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L2c
            java.lang.String r1 = resolveSoundPath(r3, r5)
            if (r1 == 0) goto L3f
            java.io.File r5 = new java.io.File
            r5.<init>(r1)
            android.net.Uri r5 = android.net.Uri.fromFile(r5)
            goto L3f
        L2c:
            java.lang.String r1 = r5.getScheme()
            java.lang.String r2 = "file"
            boolean r1 = r2.equals(r1)
            if (r1 == 0) goto L3d
            java.lang.String r1 = r5.getPath()
            goto L3f
        L3d:
            java.lang.String r1 = ""
        L3f:
            copySound(r3, r1, r4)
            android.content.ContentResolver r3 = r3.getContentResolver()
            if (r5 == 0) goto L4d
            java.lang.String r4 = r5.toString()
            goto L4e
        L4d:
            r4 = 0
        L4e:
            android.provider.Settings.System.putString(r3, r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExtraRingtoneManager.saveDefaultSound(android.content.Context, int, android.net.Uri):void");
    }
}
