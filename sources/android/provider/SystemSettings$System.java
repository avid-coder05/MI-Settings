package android.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import com.miui.system.internal.R;
import java.util.Iterator;
import miui.os.SystemProperties;
import miui.text.ChinesePinyinConverter;
import miui.util.FeatureParser;
import miui.util.Utf8TextUtils;

/* loaded from: classes.dex */
public class SystemSettings$System {
    public static final String DARKEN_WALLPAPER_UNDER_DARK_MODE = "darken_wallpaper_under_dark_mode";
    private static final String E10_DEVICE = "beryllium";
    private static final String INDIA = "INDIA";
    public static final String LOCK_WALLPAPER_PROVIDER_AUTHORITY = "lock_wallpaper_provider_authority";
    public static final String PERSIST_SYS_DEVICE_NAME = "persist.sys.device_name";
    public static final String RO_MARKET_NAME = "ro.product.marketname";
    public static final String STATUS_BAR_WINDOW_LOADED = "status_bar_window_loaded";

    public static boolean getBoolean(ContentResolver contentResolver, String str, boolean z) {
        return Settings.System.getInt(contentResolver, str, z ? 1 : 0) != 0;
    }

    private static int getDefaultNameRes() {
        return FeatureParser.getBoolean("is_redmi", false) ? R.string.device_redmi : FeatureParser.getBoolean("is_poco", false) ? R.string.device_poco : FeatureParser.getBoolean("is_hongmi", false) ? R.string.device_hongmi : FeatureParser.getBoolean("is_xiaomi", false) ? E10_DEVICE.equals(SystemProperties.get("ro.product.device")) ? SystemProperties.get("ro.boot.hwc", "").contains(INDIA) ? R.string.device_poco_india : R.string.device_poco_global : R.string.device_xiaomi : FeatureParser.getBoolean("is_pad", false) ? R.string.device_pad : R.string.miui_device_name;
    }

    public static String getDeviceName(Context context) {
        String str = SystemProperties.get(RO_MARKET_NAME, (String) null);
        if (str == null || str.length() == 0) {
            str = context.getString(getDefaultNameRes());
        }
        return SystemProperties.get(PERSIST_SYS_DEVICE_NAME, str);
    }

    public static void setDeviceName(Context context, String str) {
        SystemProperties.set(PERSIST_SYS_DEVICE_NAME, str);
        setNetHostName(context);
    }

    public static void setNetHostName(Context context) {
        String truncateByte;
        String str = SystemProperties.get("net.hostname");
        StringBuilder sb = new StringBuilder();
        sb.append(Build.MODEL);
        sb.append("-");
        Iterator it = ChinesePinyinConverter.getInstance().get(getDeviceName(context)).iterator();
        while (it.hasNext()) {
            sb.append(((ChinesePinyinConverter.Token) it.next()).target);
        }
        String replace = sb.toString().replace(" ", "");
        if (replace.equals(str) || (truncateByte = Utf8TextUtils.truncateByte(replace, 20)) == null) {
            return;
        }
        SystemProperties.set("net.hostname", truncateByte);
    }
}
