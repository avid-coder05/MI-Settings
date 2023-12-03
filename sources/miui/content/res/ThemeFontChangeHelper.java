package miui.content.res;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.MiuiConfiguration;
import android.text.TextUtils;
import android.util.Log;
import miui.ddm.DdmHandleAppName;
import miui.reflect.Field;

/* loaded from: classes3.dex */
public class ThemeFontChangeHelper {
    private static int sWebViewThemeChanged = -1;

    private static MiuiConfiguration getExtraConfig(Configuration configuration) {
        try {
            return (MiuiConfiguration) Field.of(Configuration.class, "extraConfig", MiuiConfiguration.class).get(configuration);
        } catch (Exception e) {
            Log.w("ThemeFontChangeHelper", "getExtraConfig from Configuration failed", e);
            return null;
        }
    }

    public static void markWebViewCreated(Context context) {
        MiuiConfiguration extraConfig = getExtraConfig(context.getResources().getConfiguration());
        if (extraConfig.checkFontChange(sWebViewThemeChanged)) {
            return;
        }
        sWebViewThemeChanged = extraConfig.themeChanged;
    }

    public static void quitProcessIfNeed(Configuration configuration) {
        if (configuration == null || TextUtils.equals("system_process", DdmHandleAppName.getAppName()) || TextUtils.equals("com.miui.miservice", DdmHandleAppName.getAppName()) || !getExtraConfig(configuration).checkFontChange(sWebViewThemeChanged)) {
            return;
        }
        Log.d("ThemeFontChangeHelper", "app has checked webview-font change and process will restart.");
        System.exit(0);
    }
}
