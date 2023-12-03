package miui.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import miui.os.SystemProperties;
import miui.provider.MiCloudSmsCmd;

/* loaded from: classes4.dex */
public class MiuiFeatureUtils {
    private static final String DEFAULT_CONFIG_FILE_PATH = "/system/etc/miui_feature/default.conf";
    public static final String FEATURE_COMPLETE_ANIMATION = "feature_complete_animation";
    public static final String FEATURE_RUNTIME_BLUR = "feature_runtime_blur";
    public static final String FEATURE_THUMBNAIL = "feature_thumbnail";
    private static final String LITE_CONFIG_FILE_PATH = "/system/etc/miui_feature/lite.conf";
    private static final String MIUISDK_FEATURE_PREFIX = "ro.sys.";
    private static final String MIUISDK_KEY = "miuisdk";
    private static final String PRPPERTY = "persist.sys.miui_feature_config";
    private static final String SYSTEM_KEY = "system";
    private static final String TAG = "MiuiFeatureUtils";
    private static String sConfigFilePath;
    private static HashMap<String, HashMap<String, Boolean>> sConfigResult;
    private static boolean sIsLiteMode;
    private static boolean sIsLiteModeSupported;
    private static HashMap<String, Boolean> sMiuisdkConfigResult;
    private static HashMap<String, Boolean> sSystemConfigResult;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class ConfigReader {
        private HashMap<String, HashMap<String, Boolean>> mConfigResult = null;
        private HashMap<String, Boolean> mCurrentConfig = null;
        private String mCurrentGroupName;
        private String mPath;

        public ConfigReader(String str) {
            this.mPath = null;
            this.mPath = str;
        }

        private int findEqualSignPos(String str) {
            if (TextUtils.isEmpty(str)) {
                return -1;
            }
            return str.indexOf("=");
        }

        private boolean matchGroup(String str) {
            return !TextUtils.isEmpty(str) && str.startsWith("[") && str.endsWith("]");
        }

        /* JADX WARN: Not initialized variable reg: 1, insn: 0x0050: MOVE (r0 I:??[OBJECT, ARRAY]) = (r1 I:??[OBJECT, ARRAY]), block:B:23:0x0050 */
        /* JADX WARN: Removed duplicated region for block: B:30:0x0053 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean parseInternal() {
            /*
                r6 = this;
                r0 = 0
                java.io.BufferedReader r1 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L1e java.io.IOException -> L20
                java.io.FileReader r2 = new java.io.FileReader     // Catch: java.lang.Throwable -> L1e java.io.IOException -> L20
                java.lang.String r3 = r6.mPath     // Catch: java.lang.Throwable -> L1e java.io.IOException -> L20
                r2.<init>(r3)     // Catch: java.lang.Throwable -> L1e java.io.IOException -> L20
                r1.<init>(r2)     // Catch: java.lang.Throwable -> L1e java.io.IOException -> L20
            Ld:
                java.lang.String r0 = r1.readLine()     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L4f
                if (r0 == 0) goto L17
                r6.parseLine(r0)     // Catch: java.io.IOException -> L1c java.lang.Throwable -> L4f
                goto Ld
            L17:
                r6 = 1
                r1.close()     // Catch: java.io.IOException -> L4e
                goto L4e
            L1c:
                r0 = move-exception
                goto L24
            L1e:
                r6 = move-exception
                goto L51
            L20:
                r1 = move-exception
                r5 = r1
                r1 = r0
                r0 = r5
            L24:
                java.lang.String r2 = "MiuiFeatureUtils"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4f
                r3.<init>()     // Catch: java.lang.Throwable -> L4f
                java.lang.String r4 = "Failed to parse feature file "
                r3.append(r4)     // Catch: java.lang.Throwable -> L4f
                java.lang.String r6 = r6.mPath     // Catch: java.lang.Throwable -> L4f
                r3.append(r6)     // Catch: java.lang.Throwable -> L4f
                java.lang.String r6 = ", error : "
                r3.append(r6)     // Catch: java.lang.Throwable -> L4f
                java.lang.String r6 = r0.toString()     // Catch: java.lang.Throwable -> L4f
                r3.append(r6)     // Catch: java.lang.Throwable -> L4f
                java.lang.String r6 = r3.toString()     // Catch: java.lang.Throwable -> L4f
                android.util.Log.e(r2, r6)     // Catch: java.lang.Throwable -> L4f
                if (r1 == 0) goto L4d
                r1.close()     // Catch: java.io.IOException -> L4d
            L4d:
                r6 = 0
            L4e:
                return r6
            L4f:
                r6 = move-exception
                r0 = r1
            L51:
                if (r0 == 0) goto L56
                r0.close()     // Catch: java.io.IOException -> L56
            L56:
                throw r6
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.util.MiuiFeatureUtils.ConfigReader.parseInternal():boolean");
        }

        private void parseLine(String str) {
            int findEqualSignPos;
            String removeComment = removeComment(str.trim());
            if (TextUtils.isEmpty(removeComment)) {
                return;
            }
            Boolean bool = null;
            if (matchGroup(removeComment)) {
                String trim = removeComment.substring(1, removeComment.length() - 1).toLowerCase().trim();
                this.mCurrentGroupName = trim;
                if (TextUtils.isEmpty(trim)) {
                    this.mCurrentConfig = null;
                    return;
                }
                if (this.mConfigResult == null) {
                    this.mConfigResult = new HashMap<>();
                }
                HashMap<String, Boolean> hashMap = this.mConfigResult.get(this.mCurrentGroupName);
                this.mCurrentConfig = hashMap;
                if (hashMap == null) {
                    HashMap<String, Boolean> hashMap2 = new HashMap<>();
                    this.mCurrentConfig = hashMap2;
                    this.mConfigResult.put(this.mCurrentGroupName, hashMap2);
                }
            } else if (this.mCurrentConfig == null || (findEqualSignPos = findEqualSignPos(removeComment)) < 1 || findEqualSignPos == removeComment.length() - 1) {
            } else {
                String trim2 = removeComment.substring(0, findEqualSignPos).toLowerCase().trim();
                String trim3 = removeComment.substring(findEqualSignPos + 1, removeComment.length()).toLowerCase().trim();
                if (trim3.equals("yes") || trim3.equals("y") || trim3.equals("true") || trim3.equals("t")) {
                    bool = Boolean.TRUE;
                } else if (trim3.equals("no") || trim3.equals(MiCloudSmsCmd.TYPE_NOISE) || trim3.equals("false") || trim3.equals("f")) {
                    bool = Boolean.FALSE;
                }
                if (bool != null) {
                    this.mCurrentConfig.put(trim2, bool);
                }
            }
        }

        private String removeComment(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            int indexOf = str.indexOf("#");
            return indexOf < 0 ? str : str.substring(0, indexOf);
        }

        public HashMap<String, HashMap<String, Boolean>> getConfigResult() {
            return this.mConfigResult;
        }

        public boolean parse() {
            if (TextUtils.isEmpty(this.mPath)) {
                return false;
            }
            return parseInternal();
        }
    }

    /* loaded from: classes4.dex */
    public static class Features {
        HashMap<String, Boolean> mFeatures;

        Features(HashMap<String, Boolean> hashMap) {
            this.mFeatures = hashMap;
        }

        public boolean isFeatureSupported(String str, boolean z) {
            Boolean bool;
            if (TextUtils.isEmpty(str)) {
                return z;
            }
            if (this.mFeatures == null || TextUtils.isEmpty(str) || (bool = this.mFeatures.get(str.toLowerCase())) == null) {
                Log.w(MiuiFeatureUtils.TAG, "Failed to get feature " + str + " for current package ");
                return z;
            }
            return bool.booleanValue();
        }
    }

    static {
        try {
            init();
        } catch (Exception unused) {
            sIsLiteModeSupported = false;
            Log.e(TAG, "Failed to initialize MiuiFeatureUtils!");
        }
    }

    private MiuiFeatureUtils() {
    }

    public static Features getLocalFeature(Context context) {
        String packageName = context != null ? context.getPackageName() : null;
        if (sConfigResult == null || TextUtils.isEmpty(packageName)) {
            Log.w(TAG, "Failed to get feature set for package " + packageName);
            return null;
        }
        return new Features(sConfigResult.get(packageName));
    }

    private static void init() {
        if (new File(DEFAULT_CONFIG_FILE_PATH).exists() && new File(LITE_CONFIG_FILE_PATH).exists()) {
            sIsLiteModeSupported = true;
        }
        String str = SystemProperties.get(PRPPERTY);
        if (!TextUtils.isEmpty(str) && new File(str).exists()) {
            sConfigFilePath = str;
        }
        if (TextUtils.isEmpty(sConfigFilePath) && new File(DEFAULT_CONFIG_FILE_PATH).exists()) {
            sConfigFilePath = DEFAULT_CONFIG_FILE_PATH;
        }
        if (TextUtils.isEmpty(sConfigFilePath)) {
            return;
        }
        ConfigReader configReader = new ConfigReader(sConfigFilePath);
        if (configReader.parse()) {
            HashMap<String, HashMap<String, Boolean>> configResult = configReader.getConfigResult();
            sConfigResult = configResult;
            if (configResult != null) {
                sSystemConfigResult = configResult.get(SYSTEM_KEY);
                sMiuisdkConfigResult = sConfigResult.get(MIUISDK_KEY);
            }
            if (LITE_CONFIG_FILE_PATH.equals(sConfigFilePath)) {
                sIsLiteMode = true;
            }
            Log.v(TAG, "Loaded and parsed feature configure file successfully");
        }
    }

    public static boolean isLiteMode() {
        return sIsLiteMode;
    }

    public static boolean isLiteModeSupported() {
        return sIsLiteModeSupported;
    }

    public static boolean isLocalFeatureSupported(Context context, String str, boolean z) {
        HashMap<String, Boolean> hashMap;
        Boolean bool;
        if (TextUtils.isEmpty(str)) {
            return z;
        }
        String packageName = context != null ? context.getPackageName() : null;
        if (sConfigResult == null || TextUtils.isEmpty(packageName) || (hashMap = sConfigResult.get(packageName)) == null || (bool = hashMap.get(str.toLowerCase())) == null) {
            Log.w(TAG, "Failed to get feature " + str + " for package " + packageName);
            return z;
        }
        return bool.booleanValue();
    }

    public static boolean isSystemFeatureSupported(String str, boolean z) {
        Boolean bool;
        if (TextUtils.isEmpty(str)) {
            return z;
        }
        HashMap<String, Boolean> hashMap = sSystemConfigResult;
        if (hashMap == null || (bool = hashMap.get(str.toLowerCase())) == null) {
            Log.w(TAG, "Failed to get system feature " + str);
            return z;
        }
        return bool.booleanValue();
    }

    public static void setMiuisdkProperties() {
        HashMap<String, Boolean> hashMap = sMiuisdkConfigResult;
        if (hashMap == null) {
            return;
        }
        try {
            for (Map.Entry<String, Boolean> entry : hashMap.entrySet()) {
                String key = entry.getKey();
                Boolean value = entry.getValue();
                if (!TextUtils.isEmpty(key) && value != null) {
                    SystemProperties.set(MIUISDK_FEATURE_PREFIX + key, value.toString());
                }
            }
        } catch (Exception unused) {
            Log.v(TAG, "Failed to set miui sdk features.");
        }
    }
}
