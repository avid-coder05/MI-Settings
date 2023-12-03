package android.media;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.system.internal.R;
import miui.content.res.ThemeResources;
import miui.content.res.ThemeRuntimeManager;
import miui.os.Build;
import miui.os.FileUtils;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class ExtraRingtone {
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};

    public static String getRingtoneTitle(Context context, Uri uri, boolean z) {
        return Build.IS_MIUI ? getRingtoneTitleMIUI(context, uri, z) : getRingtoneTitleAndroid(context, uri);
    }

    private static String getRingtoneTitleAndroid(Context context, Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        String title = ringtone != null ? ringtone.getTitle(context) : null;
        if (title == null) {
            int identifier = context.getResources().getIdentifier("ringtone_unknown", "string", ThemeResources.FRAMEWORK_PACKAGE);
            return identifier > 0 ? context.getString(identifier) : "";
        }
        return title;
    }

    private static String getRingtoneTitleMIUI(Context context, Uri uri, boolean z) {
        String title = getTitle(context, uri, true);
        return (uri != null && z && YellowPageContract.Settings.DIRECTORY.equals(uri.getAuthority())) ? context.getString(R.string.android_ringtone_default_with_actual, title) : title;
    }

    public static String getSystemLocalizationFileName(Context context, String str) {
        if (TextUtils.isEmpty(str) || !str.startsWith(ThemeRuntimeManager.BUILTIN_ROOT_PATH)) {
            return null;
        }
        Resources resources = context.getResources();
        int i = -1;
        try {
            i = resources.getIdentifier(FileUtils.getName(str).toLowerCase(), "string", "miui.system");
        } catch (Exception unused) {
        }
        if (i > 0) {
            return resources.getString(i);
        }
        return null;
    }

    /* JADX WARN: Removed duplicated region for block: B:35:0x0078 A[DONT_GENERATE] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getTitle(android.content.Context r9, android.net.Uri r10, boolean r11) {
        /*
            android.content.ContentResolver r0 = r9.getContentResolver()
            r6 = -1
            r1 = 0
            r7 = 0
            if (r10 == 0) goto L83
            java.lang.String r2 = r10.getAuthority()
            java.lang.String r3 = "settings"
            boolean r3 = r3.equals(r2)
            if (r3 == 0) goto L25
            if (r11 == 0) goto L83
            int r10 = android.media.ExtraRingtoneManager.getDefaultSoundType(r10)
            android.net.Uri r10 = android.media.ExtraRingtoneManager.getRingtoneUri(r9, r10)
            java.lang.String r9 = getTitle(r9, r10, r7)
            return r9
        L25:
            java.lang.String r11 = "media"
            boolean r11 = r11.equals(r2)
            r8 = 1
            if (r11 == 0) goto L3a
            java.lang.String[] r2 = android.media.ExtraRingtone.MEDIA_COLUMNS
            r3 = 0
            r4 = 0
            r5 = 0
            r1 = r10
            android.database.Cursor r1 = r0.query(r1, r2, r3, r4, r5)
            r11 = r8
            goto L3b
        L3a:
            r11 = r7
        L3b:
            java.lang.String r0 = ""
            if (r1 == 0) goto L52
            int r2 = r1.getCount()     // Catch: java.lang.Throwable -> L50
            if (r2 != r8) goto L52
            r1.moveToFirst()     // Catch: java.lang.Throwable -> L50
            r10 = 2
            java.lang.String r10 = r1.getString(r10)     // Catch: java.lang.Throwable -> L50
            if (r10 != 0) goto L76
            goto L54
        L50:
            r9 = move-exception
            goto L7d
        L52:
            if (r11 == 0) goto L56
        L54:
            r10 = r0
            goto L76
        L56:
            java.lang.String r11 = r10.getPath()     // Catch: java.lang.Throwable -> L50
            java.lang.String r11 = getSystemLocalizationFileName(r9, r11)     // Catch: java.lang.Throwable -> L50
            if (r11 != 0) goto L75
            java.lang.String r10 = r10.getLastPathSegment()     // Catch: java.lang.Throwable -> L50
            if (r10 == 0) goto L6d
            java.lang.String r11 = "_&_"
            int r11 = r10.indexOf(r11)     // Catch: java.lang.Throwable -> L50
            goto L6e
        L6d:
            r11 = r6
        L6e:
            if (r11 <= 0) goto L76
            java.lang.String r10 = r10.substring(r7, r11)     // Catch: java.lang.Throwable -> L50
            goto L76
        L75:
            r10 = r11
        L76:
            if (r1 == 0) goto L7b
            r1.close()
        L7b:
            r1 = r10
            goto L83
        L7d:
            if (r1 == 0) goto L82
            r1.close()
        L82:
            throw r9
        L83:
            if (r1 != 0) goto L8c
            int r10 = miui.system.R.string.android_ringtone_silent
            java.lang.String r9 = r9.getString(r10)
            goto La7
        L8c:
            int r10 = r1.length()
            if (r10 != 0) goto L99
            int r10 = miui.system.R.string.android_ringtone_unknown
            java.lang.String r9 = r9.getString(r10)
            goto La7
        L99:
            java.lang.String r9 = "."
            int r9 = r1.lastIndexOf(r9)
            if (r9 != r6) goto La2
            goto La6
        La2:
            java.lang.String r1 = r1.substring(r7, r9)
        La6:
            r9 = r1
        La7:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ExtraRingtone.getTitle(android.content.Context, android.net.Uri, boolean):java.lang.String");
    }
}
