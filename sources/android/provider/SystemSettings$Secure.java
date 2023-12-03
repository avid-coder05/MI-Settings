package android.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.Settings;

/* loaded from: classes.dex */
public class SystemSettings$Secure {
    public static final String PRIVACY_MODE_ENABLED = "privacy_mode_enabled";
    public static final String SCREEN_BUTTONS_STATE = "screen_buttons_state";

    public static Cursor checkPrivacyAndReturnCursor(Context context) {
        if (1 == Settings.Secure.getInt(context.getContentResolver(), PRIVACY_MODE_ENABLED, 0)) {
            return new MatrixCursor(new String[]{"_id"});
        }
        return null;
    }

    public static boolean isCtaSupported(ContentResolver contentResolver) {
        return false;
    }
}
