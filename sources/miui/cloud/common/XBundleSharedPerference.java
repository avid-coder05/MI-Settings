package miui.cloud.common;

import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class XBundleSharedPerference {
    private static Bundle JSONToBundle(JSONObject jSONObject) {
        Bundle bundle = new Bundle();
        Iterator<String> keys = jSONObject.keys();
        while (keys.hasNext()) {
            String next = keys.next();
            Object opt = jSONObject.opt(next);
            if (opt != null) {
                if (opt instanceof Integer) {
                    bundle.putInt(next, ((Integer) opt).intValue());
                } else if (opt instanceof Long) {
                    bundle.putLong(next, ((Long) opt).longValue());
                } else if (opt instanceof Boolean) {
                    bundle.putBoolean(next, ((Boolean) opt).booleanValue());
                } else if (opt instanceof CharSequence) {
                    bundle.putString(next, ((CharSequence) opt).toString());
                } else if (opt instanceof JSONObject) {
                    bundle.putBundle(next, JSONToBundle((JSONObject) opt));
                }
            }
        }
        return bundle;
    }

    private static Map bundleToMap(Bundle bundle) {
        HashMap hashMap = new HashMap();
        for (String str : bundle.keySet()) {
            Object obj = bundle.get(str);
            if (obj != null) {
                if ((obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Boolean)) {
                    hashMap.put(str, obj);
                } else if (obj instanceof CharSequence) {
                    hashMap.put(str, ((CharSequence) obj).toString());
                } else if (obj instanceof Bundle) {
                    hashMap.put(str, bundleToMap((Bundle) obj));
                }
            }
        }
        return hashMap;
    }

    public static Bundle loadPreferencesBundle(SharedPreferences sharedPreferences, String str) {
        if (sharedPreferences.contains(str)) {
            try {
                return JSONToBundle(new JSONObject(sharedPreferences.getString(str, null)));
            } catch (JSONException unused) {
                XLogger.log("Bad JSON stored in shared preference. ");
                return null;
            }
        }
        return null;
    }

    public static void savePreferencesBundle(SharedPreferences.Editor editor, String str, Bundle bundle) {
        if (bundle == null) {
            return;
        }
        editor.putString(str, JSONObject.wrap(bundleToMap(bundle)).toString());
    }
}
