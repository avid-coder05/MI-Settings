package miui.content.res;

import android.util.Log;
import com.miui.internal.content.res.ThemeDefinition;
import com.miui.internal.content.res.ThemeToolUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes3.dex */
public class ThemeCompatibility {
    private static final Map<String, List<ThemeDefinition.NewDefaultValue>> COMPATIBILITY_DEFAULTVALUE;
    private static final Map<String, List<ThemeDefinition.FallbackInfo>> COMPATIBILITY_FALLBACKS;
    private static final String DISABLE_MIUI_THEME_MECHANISM = "/data/system/theme_config/theme_disable";
    private static final Map<String, List<ThemeDefinition.FallbackInfo>> MIUI_OPTIMIZATION_FALLBACK;
    private static final boolean sThemeEnabled;

    static {
        boolean z = !new File(DISABLE_MIUI_THEME_MECHANISM).exists();
        sThemeEnabled = z;
        COMPATIBILITY_FALLBACKS = new HashMap();
        COMPATIBILITY_DEFAULTVALUE = new HashMap();
        MIUI_OPTIMIZATION_FALLBACK = new HashMap();
        if (!z) {
            Log.d("ThemeCompatibility", "theme disabled flag has been checked!!!");
            return;
        }
        Iterator<ThemeDefinition.CompatibilityInfo> it = ThemeCompatibilityLoader.loadConfig().iterator();
        while (it.hasNext()) {
            ThemeDefinition.NewDefaultValue newDefaultValue = (ThemeDefinition.CompatibilityInfo) it.next();
            ThemeDefinition.CompatibilityType compatibilityType = ((ThemeDefinition.CompatibilityInfo) newDefaultValue).mCompatibilityType;
            if (compatibilityType == ThemeDefinition.CompatibilityType.FALLBACK) {
                ThemeDefinition.FallbackInfo fallbackInfo = (ThemeDefinition.FallbackInfo) newDefaultValue;
                String str = fallbackInfo.mResPkgName;
                Map<String, List<ThemeDefinition.FallbackInfo>> map = COMPATIBILITY_FALLBACKS;
                List<ThemeDefinition.FallbackInfo> list = map.get(str);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(str, list);
                }
                list.add(fallbackInfo);
            } else if (compatibilityType == ThemeDefinition.CompatibilityType.NEW_DEF_VALUE) {
                ThemeDefinition.NewDefaultValue newDefaultValue2 = newDefaultValue;
                String str2 = newDefaultValue2.mResPkgName;
                Map<String, List<ThemeDefinition.NewDefaultValue>> map2 = COMPATIBILITY_DEFAULTVALUE;
                List<ThemeDefinition.NewDefaultValue> list2 = map2.get(str2);
                if (list2 == null) {
                    list2 = new ArrayList<>();
                    map2.put(str2, list2);
                }
                list2.add(newDefaultValue2);
            }
        }
        List<ThemeDefinition.FallbackInfo> list3 = COMPATIBILITY_FALLBACKS.get(ThemeResources.MIUI_PACKAGE);
        if (list3 != null) {
            for (ThemeDefinition.FallbackInfo fallbackInfo2 : list3) {
                String combineFallbackInfoKey = combineFallbackInfoKey(fallbackInfo2.mResType, fallbackInfo2.mResOriginalName);
                Map<String, List<ThemeDefinition.FallbackInfo>> map3 = MIUI_OPTIMIZATION_FALLBACK;
                List<ThemeDefinition.FallbackInfo> list4 = map3.get(combineFallbackInfoKey);
                if (list4 == null) {
                    list4 = new ArrayList<>();
                    map3.put(combineFallbackInfoKey, list4);
                }
                list4.add(fallbackInfo2);
            }
        }
    }

    private static String combineFallbackInfoKey(ThemeDefinition.ResourceType resourceType, String str) {
        int indexOf = str.indexOf(".");
        if (indexOf < 0) {
            indexOf = str.length();
        }
        FixedSizeStringBuffer buffer = FixedSizeStringBuffer.getBuffer();
        buffer.append(resourceType.toString());
        buffer.append("/");
        buffer.append(str, 0, indexOf);
        String fixedSizeStringBuffer = buffer.toString();
        FixedSizeStringBuffer.freeBuffer(buffer);
        return fixedSizeStringBuffer;
    }

    public static List<ThemeDefinition.FallbackInfo> getFallbackList(String str) {
        return COMPATIBILITY_FALLBACKS.get(str);
    }

    public static List<ThemeDefinition.FallbackInfo> getMayFilterFallbackList(String str, ThemeDefinition.ResourceType resourceType, String str2) {
        if (ThemeResources.MIUI_PACKAGE.equals(str)) {
            return MIUI_OPTIMIZATION_FALLBACK.get(combineFallbackInfoKey(resourceType, ThemeToolUtils.getNameFromPath(str2)));
        }
        return COMPATIBILITY_FALLBACKS.get(str);
    }

    public static List<ThemeDefinition.NewDefaultValue> getNewDefaultValueList(String str) {
        return COMPATIBILITY_DEFAULTVALUE.get(str);
    }

    public static boolean isCompatibleResource(String str) {
        return (str.startsWith("/data/system/theme/") && new File(str).exists() && !new File(ThemeResources.THEME_VERSION_COMPATIBILITY_PATH).exists()) ? false : true;
    }

    public static boolean isThemeEnabled() {
        return sThemeEnabled;
    }
}
