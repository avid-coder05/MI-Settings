package miui.mipub;

import android.content.ContentResolver;
import android.provider.Settings;

@Deprecated
/* loaded from: classes3.dex */
public class MipubUtils {
    private static final String TAG = "MipubUtils";

    @Deprecated
    public static boolean hasFollowedMipubs(ContentResolver contentResolver) {
        return Settings.System.getInt(contentResolver, "has_followed_mipub", 0) == 1;
    }

    @Deprecated
    public static void setHasFollowedMipubs(ContentResolver contentResolver, boolean z) {
        Settings.System.putInt(contentResolver, "has_followed_mipub", z ? 1 : 0);
    }
}
