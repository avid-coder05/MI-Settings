package miui.content.res;

import android.content.res.AssetManager;
import java.io.IOException;
import miui.theme.ThemeFileUtils;
import miui.theme.ThemePermissionUtils;

/* loaded from: classes3.dex */
public class ThemeNativeUtils {
    static {
        System.loadLibrary("themeutils_jni");
    }

    public static boolean copy(String str, String str2) {
        return ThemeFileUtils.copy(str, str2);
    }

    public static void deleteContents(String str) {
        ThemeFileUtils.deleteContents(str);
    }

    public static boolean isContainXXXhdpiResource(AssetManager assetManager) {
        return nIsContainXXXhdpiResource(assetManager);
    }

    public static void link(String str, String str2) throws IOException {
        ThemeFileUtils.link(str, str2);
    }

    public static boolean mkdirs(String str) {
        return ThemeFileUtils.mkdirs(str);
    }

    private static native boolean nIsContainXXXhdpiResource(AssetManager assetManager);

    private static native void nTerminateAtlas();

    public static boolean remove(String str) {
        return ThemeFileUtils.remove(str);
    }

    public static void terminateAtlas() {
        nTerminateAtlas();
    }

    public static boolean updateFilePermissionWithThemeContext(String str) {
        return ThemePermissionUtils.updateFilePermissionWithThemeContext(str);
    }

    public static boolean updateFilePermissionWithThemeContext(String str, boolean z) {
        return ThemePermissionUtils.updateFilePermissionWithThemeContext(str, z);
    }

    public static void write(String str, String str2) throws IOException {
        ThemeFileUtils.write(str, str2);
    }
}
