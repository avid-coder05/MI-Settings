package miuix.provider;

import android.content.ContentResolver;
import android.provider.Settings;

/* loaded from: classes5.dex */
public class ExtraSettings$Secure {
    public static int getInt(ContentResolver contentResolver, String str) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(contentResolver, str);
    }
}
