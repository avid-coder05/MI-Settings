package com.android.settings.device;

import android.app.Activity;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.provider.Settings;
import android.provider.SystemSettings$System;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import androidx.preference.PreferenceManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.credentials.MiuiCredentialsUpdater;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.util.FeatureParser;
import miui.util.HardwareInfo;

/* loaded from: classes.dex */
public class MiuiAboutPhoneUtils {
    private static final String DEVICE_NAME = Build.DEVICE;
    private static MiuiAboutPhoneUtils sInstance;
    private Context mContext;
    private ArrayList<String> mPocoDevices;
    private StorageManager mStorageManager;
    private String mTotalRamStr;
    private HashMap<PhoneConfigurationType, List<String>> mConfigurationHashMap = new HashMap<>();
    private long mTotalMemoryByte = -1;
    private String mChangedLang = "";

    /* loaded from: classes.dex */
    public enum PhoneConfigurationType {
        LOW_CONFIGURATION_VERSION,
        STANDARD_CONFIGURATION_VERSION,
        HIGH_CONFIGURATION_VERSION,
        ENJOY_VERSIION
    }

    private MiuiAboutPhoneUtils(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mStorageManager = (StorageManager) applicationContext.getSystemService("storage");
        init();
    }

    private boolean checkRamNotZero() {
        if (TextUtils.isEmpty(this.mTotalRamStr)) {
            return false;
        }
        try {
            return Float.parseFloat(this.mTotalRamStr.split("\\s")[0]) > 0.0f;
        } catch (Exception e) {
            Log.e("MiuiAboutPhoneUtils", "checkRamNotZero mTotalRamStr: " + this.mTotalRamStr, e);
            return true;
        }
    }

    public static boolean enableShowCredentials() {
        return isOwnerUser() && MiuiCredentialsUpdater.supportVerification();
    }

    public static String formatSize(Context context, long j) {
        return Build.VERSION.SDK_INT <= 25 ? Formatter.formatFileSize(context, j) : MiuiUtils.formatSizeWith1024(context, j);
    }

    public static boolean getBooleanPreference(Context context, String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (defaultSharedPreferences != null) {
            return defaultSharedPreferences.getBoolean(str, false);
        }
        return false;
    }

    public static String getCTANumble() {
        return !TextUtils.isEmpty(SystemProperties.get(SystemSettings$System.RO_MARKET_NAME, (String) null)) ? Build.MODEL : "";
    }

    public static int getDefaultNameRes() {
        if (FeatureParser.getBoolean("is_redmi", false)) {
            return 286196147;
        }
        if (FeatureParser.getBoolean("is_poco", false)) {
            return 286196144;
        }
        if (FeatureParser.getBoolean("is_hongmi", false)) {
            return 286196141;
        }
        if (!FeatureParser.getBoolean("is_xiaomi", false)) {
            return FeatureParser.getBoolean("is_pad", false) ? 286196143 : 286196285;
        } else if ("beryllium".equals(SystemProperties.get("ro.product.device"))) {
            return SystemProperties.get("ro.boot.hwc", "").contains("INDIA") ? 286196146 : 286196145;
        } else {
            return 286196153;
        }
    }

    public static String getDeviceMarketName() {
        String str = SystemProperties.get(SystemSettings$System.RO_MARKET_NAME, (String) null);
        return (str == null || str.length() == 0) ? getModelNumber() : str;
    }

    public static String getFormattedKernelVersion() {
        try {
            String readLine = readLine("/proc/version");
            boolean equals = "ID".equals(miui.os.Build.getRegion());
            Matcher matcher = (equals ? Pattern.compile("Linux version (\\S+) \\((\\S+?)\\) (?:\\(gcc.+? \\)) (#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)") : Pattern.compile("\\w+\\s+\\w+\\s+([^\\s]+)\\s+\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+([^\\s]+)\\s+(?:PREEMPT\\s+)?(.+)")).matcher(readLine);
            if (equals && !matcher.matches()) {
                Log.d("MiuiAboutPhoneUtils", "Regex try match PROC_VERSION_REGEX_ID_NEW ");
                matcher = Pattern.compile("Linux version (\\S+) \\((\\S+?)\\) \\(.+\\) (#\\d+) (?:.*?)?((Sun|Mon|Tue|Wed|Thu|Fri|Sat).+)").matcher(readLine);
                if (!matcher.matches()) {
                    Log.d("MiuiAboutPhoneUtils", "Regex try match PROC_VERSION_REGEX ");
                    matcher = Pattern.compile("\\w+\\s+\\w+\\s+([^\\s]+)\\s+\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+([^\\s]+)\\s+(?:PREEMPT\\s+)?(.+)").matcher(readLine);
                }
            }
            if (!matcher.matches()) {
                Log.e("MiuiAboutPhoneUtils", "Regex did not match on /proc/version: " + readLine);
                return "Unavailable";
            } else if (matcher.groupCount() < 4) {
                Log.e("MiuiAboutPhoneUtils", "Regex match on /proc/version only returned " + matcher.groupCount() + " groups");
                return "Unavailable";
            } else if (equals) {
                return matcher.group(1) + "\n" + matcher.group(2) + " " + matcher.group(3) + "\n" + matcher.group(4);
            } else {
                return new StringBuilder(matcher.group(1)).toString();
            }
        } catch (IOException e) {
            Log.e("MiuiAboutPhoneUtils", "IO Exception when getting kernel version for Device Info screen", e);
            return "Unavailable";
        }
    }

    public static MiuiAboutPhoneUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MiuiAboutPhoneUtils(context);
        }
        return sInstance;
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0093 A[Catch: IOException -> 0x006d, TRY_ENTER, TRY_LEAVE, TryCatch #10 {IOException -> 0x006d, blocks: (B:22:0x0069, B:45:0x0093), top: B:71:0x0039 }] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00a5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x009b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0089 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:78:? A[SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:25:0x006e -> B:70:0x0096). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float getMaxFre(int r8, java.io.BufferedReader r9) {
        /*
            r7 = this;
            java.lang.String r7 = "is_hongmi"
            r0 = 0
            boolean r7 = miui.util.FeatureParser.getBoolean(r7, r0)
            if (r7 != 0) goto L1d
            java.lang.String r7 = "is_xiaomi"
            boolean r7 = miui.util.FeatureParser.getBoolean(r7, r0)
            if (r7 != 0) goto L1d
            java.lang.String r7 = "is_pad"
            boolean r7 = miui.util.FeatureParser.getBoolean(r7, r0)
            if (r7 == 0) goto L1a
            goto L1d
        L1a:
            java.lang.String r7 = "/cpufreq/scaling_max_freq"
            goto L1f
        L1d:
            java.lang.String r7 = "/cpufreq/cpuinfo_max_freq"
        L1f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "/sys/devices/system/cpu/cpu"
            r0.append(r1)
            java.lang.String r8 = java.lang.String.valueOf(r8)
            r0.append(r8)
            r0.append(r7)
            java.lang.String r7 = r0.toString()
            r8 = 0
            r0 = 0
            java.io.FileReader r1 = new java.io.FileReader     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L80
            r1.<init>(r7)     // Catch: java.lang.Throwable -> L7e java.lang.Exception -> L80
            java.io.BufferedReader r7 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L78 java.lang.Exception -> L7b
            r0 = 8192(0x2000, float:1.148E-41)
            r7.<init>(r1, r0)     // Catch: java.lang.Throwable -> L78 java.lang.Exception -> L7b
            java.lang.String r9 = r7.readLine()     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            if (r9 == 0) goto L61
            java.lang.Long r9 = java.lang.Long.valueOf(r9)     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            long r2 = r9.longValue()     // Catch: java.lang.Throwable -> L72 java.lang.Exception -> L75
            r4 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 / r4
            r4 = 12
            long r2 = r2 + r4
            r4 = 25
            long r2 = r2 / r4
            long r2 = r2 * r4
            float r8 = (float) r2
            r9 = 1148846080(0x447a0000, float:1000.0)
            float r8 = r8 / r9
        L61:
            r7.close()     // Catch: java.io.IOException -> L65
            goto L69
        L65:
            r7 = move-exception
            r7.printStackTrace()
        L69:
            r1.close()     // Catch: java.io.IOException -> L6d
            goto L96
        L6d:
            r7 = move-exception
            r7.printStackTrace()
            goto L96
        L72:
            r8 = move-exception
            r9 = r7
            goto L79
        L75:
            r9 = move-exception
            r0 = r1
            goto L84
        L78:
            r8 = move-exception
        L79:
            r0 = r1
            goto L99
        L7b:
            r7 = move-exception
            r0 = r1
            goto L81
        L7e:
            r8 = move-exception
            goto L99
        L80:
            r7 = move-exception
        L81:
            r6 = r9
            r9 = r7
            r7 = r6
        L84:
            r9.printStackTrace()     // Catch: java.lang.Throwable -> L97
            if (r7 == 0) goto L91
            r7.close()     // Catch: java.io.IOException -> L8d
            goto L91
        L8d:
            r7 = move-exception
            r7.printStackTrace()
        L91:
            if (r0 == 0) goto L96
            r0.close()     // Catch: java.io.IOException -> L6d
        L96:
            return r8
        L97:
            r8 = move-exception
            r9 = r7
        L99:
            if (r9 == 0) goto La3
            r9.close()     // Catch: java.io.IOException -> L9f
            goto La3
        L9f:
            r7 = move-exception
            r7.printStackTrace()
        La3:
            if (r0 == 0) goto Lad
            r0.close()     // Catch: java.io.IOException -> La9
            goto Lad
        La9:
            r7 = move-exception
            r7.printStackTrace()
        Lad:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.device.MiuiAboutPhoneUtils.getMaxFre(int, java.io.BufferedReader):float");
    }

    /* JADX WARN: Removed duplicated region for block: B:55:0x007a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0070 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:70:? A[SYNTHETIC] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:20:0x0041 -> B:52:0x006c). Please submit an issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private float getMaxFreFromProc() {
        /*
            r5 = this;
            java.lang.String r5 = "/proc/ppm/dump_cluster_2_dvfs_table"
            r0 = 0
            r1 = -1082130432(0xffffffffbf800000, float:-1.0)
            java.io.FileReader r2 = new java.io.FileReader     // Catch: java.lang.Throwable -> L51 java.lang.Exception -> L56
            r2.<init>(r5)     // Catch: java.lang.Throwable -> L51 java.lang.Exception -> L56
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4c
            r3 = 8192(0x2000, float:1.148E-41)
            r5.<init>(r2, r3)     // Catch: java.lang.Throwable -> L47 java.lang.Exception -> L4c
            java.lang.String r0 = r5.readLine()     // Catch: java.lang.Exception -> L45 java.lang.Throwable -> L6d
            if (r0 == 0) goto L34
            java.lang.String r3 = " "
            java.lang.String[] r0 = r0.split(r3)     // Catch: java.lang.Exception -> L45 java.lang.Throwable -> L6d
            r3 = 0
            r0 = r0[r3]     // Catch: java.lang.Exception -> L45 java.lang.Throwable -> L6d
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch: java.lang.Exception -> L45 java.lang.Throwable -> L6d
            long r0 = r0.longValue()     // Catch: java.lang.Exception -> L45 java.lang.Throwable -> L6d
            float r0 = (float) r0
        L29:
            r1 = 1148846080(0x447a0000, float:1000.0)
            int r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r1 <= 0) goto L33
            r1 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r1
            goto L29
        L33:
            r1 = r0
        L34:
            r5.close()     // Catch: java.lang.Exception -> L38
            goto L3c
        L38:
            r5 = move-exception
            r5.printStackTrace()
        L3c:
            r2.close()     // Catch: java.lang.Exception -> L40
            goto L6c
        L40:
            r5 = move-exception
            r5.printStackTrace()
            goto L6c
        L45:
            r0 = move-exception
            goto L5a
        L47:
            r5 = move-exception
            r4 = r0
            r0 = r5
            r5 = r4
            goto L6e
        L4c:
            r5 = move-exception
            r4 = r0
            r0 = r5
            r5 = r4
            goto L5a
        L51:
            r5 = move-exception
            r2 = r0
            r0 = r5
            r5 = r2
            goto L6e
        L56:
            r5 = move-exception
            r2 = r0
            r0 = r5
            r5 = r2
        L5a:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L6d
            if (r5 == 0) goto L67
            r5.close()     // Catch: java.lang.Exception -> L63
            goto L67
        L63:
            r5 = move-exception
            r5.printStackTrace()
        L67:
            if (r2 == 0) goto L6c
            r2.close()     // Catch: java.lang.Exception -> L40
        L6c:
            return r1
        L6d:
            r0 = move-exception
        L6e:
            if (r5 == 0) goto L78
            r5.close()     // Catch: java.lang.Exception -> L74
            goto L78
        L74:
            r5 = move-exception
            r5.printStackTrace()
        L78:
            if (r2 == 0) goto L82
            r2.close()     // Catch: java.lang.Exception -> L7e
            goto L82
        L7e:
            r5 = move-exception
            r5.printStackTrace()
        L82:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.device.MiuiAboutPhoneUtils.getMaxFreFromProc():float");
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13, types: [java.lang.Exception] */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v16 */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v24 */
    /* JADX WARN: Type inference failed for: r4v3 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v7, types: [java.io.BufferedReader] */
    private String getMaxFreqFromProcPriority() {
        FileReader fileReader;
        Throwable th;
        Exception e;
        BufferedReader bufferedReader;
        Exception e2 = "proc/cpumaxfreq";
        try {
            try {
                fileReader = new FileReader("proc/cpumaxfreq");
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e3) {
            fileReader = null;
            e = e3;
            bufferedReader = null;
        } catch (Throwable th3) {
            fileReader = null;
            th = th3;
            e2 = 0;
        }
        try {
            bufferedReader = new BufferedReader(fileReader, 8192);
            try {
                String readLine = bufferedReader.readLine();
                try {
                    bufferedReader.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
                try {
                    fileReader.close();
                    return readLine;
                } catch (Exception e5) {
                    e5.printStackTrace();
                    return readLine;
                }
            } catch (Exception e6) {
                e = e6;
                e.printStackTrace();
                e2 = bufferedReader;
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                        e2 = bufferedReader;
                    } catch (Exception e7) {
                        e7.printStackTrace();
                        e2 = e7;
                    }
                }
                if (fileReader != null) {
                    try {
                        fileReader.close();
                    } catch (Exception e8) {
                        e2 = e8;
                        e2.printStackTrace();
                    }
                }
                return "";
            }
        } catch (Exception e9) {
            e = e9;
            bufferedReader = null;
        } catch (Throwable th4) {
            th = th4;
            e2 = 0;
            if (e2 != 0) {
                try {
                    e2.close();
                } catch (Exception e10) {
                    e10.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                    throw th;
                } catch (Exception e11) {
                    e11.printStackTrace();
                    throw th;
                }
            }
            throw th;
        }
    }

    public static String getMiuiVersion(Context context) {
        return getMiuiVersion(context, true, true);
    }

    private static String getMiuiVersion(Context context, boolean z, boolean z2) {
        String str;
        String str2 = SystemProperties.get("ro.miui.cust_incremental", "");
        if (TextUtils.isEmpty(str2)) {
            String str3 = "Chozen " + getMiuiVersionCode() + " ";
            boolean z3 = miui.os.Build.IS_STABLE_VERSION;
            String str4 = (z3 ? "Chozen " : str3) + " OS " + SystemProperties.get("") + " ";
            if (z3) {
                return getMiuiVersionStable(context, str4, z, z2, false);
            }
            String str5 = str4 + Build.VERSION.INCREMENTAL;
            if (z2) {
                if (z) {
                    str = str5 + "\n";
                } else {
                    str = str5 + " | ";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(context.getString(miui.os.Build.IS_ALPHA_BUILD ? R.string.alpha_build : R.string.developer_build));
                return sb.toString();
            }
            return str5;
        }
        return str2;
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0075  */
    /* JADX WARN: Removed duplicated region for block: B:34:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static java.lang.String getMiuiVersionCode() {
        /*
            java.lang.String r0 = ""
            java.lang.String r1 = "ro.miui.ui.version.code"
            r2 = 0
            int r3 = android.os.SystemProperties.getInt(r1, r2)
            java.lang.String r4 = "ro.miui.ui.version.name"
            r5 = 0
            java.lang.String r4 = android.os.SystemProperties.get(r4, r5)
            boolean r6 = android.text.TextUtils.isEmpty(r4)
            r7 = 2
            if (r6 != 0) goto L6f
            java.lang.String r6 = "V"
            boolean r6 = r4.startsWith(r6)
            if (r6 == 0) goto L6f
            r6 = 86
            int r6 = r4.indexOf(r6)     // Catch: java.lang.Exception -> L68
            int r6 = r6 + 1
            int r8 = r4.length()     // Catch: java.lang.Exception -> L68
            if (r8 < r6) goto L6f
            java.lang.String r4 = r4.substring(r6)     // Catch: java.lang.Exception -> L68
            boolean r6 = android.text.TextUtils.isEmpty(r4)     // Catch: java.lang.Exception -> L68
            if (r6 != 0) goto L6f
            int r6 = r4.length()     // Catch: java.lang.Exception -> L68
            r8 = 3
            if (r6 != r8) goto L6f
            java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch: java.lang.Exception -> L68
            int r3 = r3.length()     // Catch: java.lang.Exception -> L68
            if (r3 != r7) goto L6f
            int r3 = java.lang.Integer.parseInt(r4)     // Catch: java.lang.Exception -> L68
            float r3 = (float) r3     // Catch: java.lang.Exception -> L68
            r4 = 1065353216(0x3f800000, float:1.0)
            float r3 = r3 * r4
            r4 = 1092616192(0x41200000, float:10.0)
            float r3 = r3 / r4
            java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch: java.lang.Exception -> L68
            java.lang.String r4 = "0+?$"
            java.lang.String r3 = r3.replaceAll(r4, r0)     // Catch: java.lang.Exception -> L67
            java.lang.String r4 = "[.]$"
            java.lang.String r0 = r3.replaceAll(r4, r0)     // Catch: java.lang.Exception -> L67
            r5 = r0
            goto L6f
        L67:
            r5 = r3
        L68:
            java.lang.String r0 = "MiuiAboutPhoneUtils"
            java.lang.String r3 = "getMiuiVersinCode error"
            android.util.Log.i(r0, r3)
        L6f:
            boolean r0 = android.text.TextUtils.isEmpty(r5)
            if (r0 == 0) goto L8d
            int r0 = android.os.SystemProperties.getInt(r1, r2)
            r1 = 11
            if (r0 >= r1) goto L83
            int r0 = r0 + r7
            java.lang.String r5 = java.lang.String.valueOf(r0)
            goto L8d
        L83:
            if (r0 != r1) goto L88
            java.lang.String r5 = "12.5"
            goto L8d
        L88:
            int r0 = r0 + r7
            java.lang.String r5 = java.lang.String.valueOf(r0)
        L8d:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.device.MiuiAboutPhoneUtils.getMiuiVersionCode():java.lang.String");
    }

    public static String getMiuiVersionInCard(Context context, boolean z, boolean z2) {
        String str;
        String str2 = SystemProperties.get("ro.miui.cust_incremental", "");
        if (TextUtils.isEmpty(str2)) {
            String str3 = "Chozen ";
            String str4 = "Chozen " + getMiuiVersionCode() + " ";
            if (SettingsFeatures.isFoldDevice()) {
                str3 = "MIUI FOLD ";
                str4 = str3;
            }
            boolean z3 = miui.os.Build.IS_STABLE_VERSION;
            if (!z3) {
                str3 = str4;
            }
            String str5 = str3 + "Gaming Edition by Akish" + SystemProperties.get("") + "\n";
            if (z3) {
                return getMiuiVersionStable(context, str5, z, z2, true);
            }
            String str6 = str5 + Build.VERSION.INCREMENTAL;
            if (z2) {
                if (str4 != null) {
                    str = str6 + " ";
                } else {
                    str = str6 + "\n";
                }
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append(context.getString(miui.os.Build.IS_ALPHA_BUILD ? R.string.alpha_build : R.string.developer_build));
                return sb.toString();
            }
            return str6;
        }
        return str2;
    }

    private static String getMiuiVersionStable(Context context, String str, boolean z, boolean z2, boolean z3) {
        StringBuilder sb = new StringBuilder();
        String str2 = Build.VERSION.INCREMENTAL;
        int i = 0;
        if (!TextUtils.isEmpty(str2)) {
            if (str2.startsWith("V")) {
                str2 = str2.substring(1, str2.length());
            }
            int lastIndexOf = str2.lastIndexOf(".");
            if (lastIndexOf > 0) {
                str2 = str2.substring(0, lastIndexOf) + "(" + str2.substring(lastIndexOf + 1) + ")";
            }
        }
        sb.append(str);
        int i2 = "P".equals(Settings.Global.getString(context.getContentResolver(), "miui_current_version_branch")) ? R.string.closed_beta : R.string.stable_build;
        if (!TextUtils.isEmpty(str2)) {
            StringTokenizer stringTokenizer = new StringTokenizer(str2, "\\.", false);
            while (stringTokenizer.hasMoreElements() && i < 3) {
                sb.append(stringTokenizer.nextToken());
                sb.append(i < 2 ? "." : "");
                i++;
            }
            if (!z3) {
                if (z2) {
                    sb.append("\n");
                    sb.append(context.getString(i2));
                }
                if (z) {
                    sb.append("\n");
                    sb.append(str2);
                }
            } else if (!z) {
                sb.append(context.getString(i2));
                sb.append("\n");
                sb.append(str2);
            } else if (z2) {
                sb.append("\n");
                sb.append(context.getString(i2));
            }
        }
        return sb.toString();
    }

    public static String getMiuiVersionWithoutBuildType(Context context, boolean z) {
        return getMiuiVersion(context, z, false);
    }

    public static String getModelNumber() {
        String str = SystemProperties.get("ro.miui.cust_model", "");
        if (!TextUtils.isEmpty(str)) {
            return str + getMsvSuffix();
        }
        String str2 = Build.DEVICE;
        if ("leo".equals(str2)) {
            return "MI NOTE PRO" + getMsvSuffix();
        } else if ("ferrari".equals(str2)) {
            return "Mi 4i" + getMsvSuffix();
        } else if ("kenzo".equals(str2)) {
            return "Redmi Note 3" + getMsvSuffix();
        } else if ("mido".equals(str2) && miui.os.Build.IS_GLOBAL_BUILD && (miui.os.Build.checkRegion("HK") || miui.os.Build.checkRegion("TW"))) {
            return "Redmi Note 4X" + getMsvSuffix();
        } else {
            return Build.MODEL + getMsvSuffix();
        }
    }

    private static String getMsvSuffix() {
        try {
            return Long.parseLong(readLine("/sys/board_properties/soc/msv"), 16) == 0 ? " (ENGINEERING)" : "";
        } catch (IOException | NumberFormatException unused) {
            return "";
        }
    }

    public static String getOpconfigVersion() {
        try {
            Method declaredMethod = Class.forName("miui.telephony.TelephonyManagerEx").getDeclaredMethod("getDefault", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(null, new Object[0]);
            Method declaredMethod2 = invoke.getClass().getDeclaredMethod("getCotaOpconfigVersionName", new Class[0]);
            declaredMethod2.setAccessible(true);
            return (String) declaredMethod2.invoke(invoke, new Object[0]);
        } catch (Exception e) {
            Log.e("MiuiAboutPhoneUtils", "getOpconfigVersion error", e);
            return "";
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:32:0x0083 -> B:75:0x00a6). Please submit an issue!!! */
    private String getRamFromProcMv() {
        FileReader fileReader;
        String[] split;
        String str = "";
        BufferedReader bufferedReader = null;
        try {
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e = e2;
            fileReader = null;
        } catch (Throwable th) {
            th = th;
            fileReader = null;
        }
        if (!new File("proc/mv").exists()) {
            Log.i("MiuiAboutPhoneUtils", "proc/mv not exist");
            return null;
        }
        fileReader = new FileReader("proc/mv");
        try {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(fileReader, 8192);
                while (true) {
                    try {
                        String readLine = bufferedReader2.readLine();
                        if (readLine == null) {
                            try {
                                break;
                            } catch (Exception e3) {
                                e3.printStackTrace();
                            }
                        } else if (!TextUtils.isEmpty(readLine) && readLine.startsWith("D:") && (split = readLine.split(" ")) != null && split.length >= 3) {
                            str = split[2];
                            if (TextUtils.isDigitsOnly(str)) {
                                str = this.mContext.getResources().getString(R.string.size_suffix, String.format("%.2f", Float.valueOf(str)), "GB");
                            }
                        }
                    } catch (Exception e4) {
                        e = e4;
                        bufferedReader = bufferedReader2;
                        e.printStackTrace();
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e5) {
                                e5.printStackTrace();
                            }
                        }
                        if (fileReader != null) {
                            fileReader.close();
                        }
                        return str;
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedReader = bufferedReader2;
                        if (bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch (Exception e6) {
                                e6.printStackTrace();
                            }
                        }
                        if (fileReader != null) {
                            try {
                                fileReader.close();
                                throw th;
                            } catch (Exception e7) {
                                e7.printStackTrace();
                                throw th;
                            }
                        }
                        throw th;
                    }
                }
                bufferedReader2.close();
                fileReader.close();
            } catch (Exception e8) {
                e = e8;
            }
            return str;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    private static String getResourceName(String str) {
        if ("cancro".equals(Build.DEVICE) && Build.MODEL.startsWith("MI 3")) {
            return str + "_mi3";
        }
        return str;
    }

    private String getString(int i) {
        return this.mContext.getResources().getString(i);
    }

    public static String getStringPreference(Context context, String str) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (defaultSharedPreferences != null) {
            return defaultSharedPreferences.getString(str, null);
        }
        return null;
    }

    public static String getUpdateInfo(Context context) {
        String string = Settings.Global.getString(context.getContentResolver(), "miui_new_version");
        if (TextUtils.isEmpty(string) || "null".equalsIgnoreCase(string.trim()) || string.equals(Build.VERSION.INCREMENTAL)) {
            return null;
        }
        return context.getString(R.string.update_new_version);
    }

    public static String getWrapModelNumber() {
        int indexOf;
        String deviceMarketName = getDeviceMarketName();
        String str = SystemProperties.get("ro.product.model.separator");
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(deviceMarketName)) {
            return deviceMarketName;
        }
        String trim = str.toLowerCase().trim();
        if (!deviceMarketName.toLowerCase().contains(trim) || (indexOf = deviceMarketName.toLowerCase().indexOf(trim)) <= 0) {
            return deviceMarketName;
        }
        return deviceMarketName.substring(0, indexOf).trim() + "\n" + deviceMarketName.substring(indexOf);
    }

    private void init() {
        String[] queryStringArray = queryStringArray(this.mContext, "low_configuration_version");
        String[] queryStringArray2 = queryStringArray(this.mContext, "standard_configuration_version");
        String[] queryStringArray3 = queryStringArray(this.mContext, "high_configuration_version");
        String[] queryStringArray4 = queryStringArray(this.mContext, "enjoy_version");
        if (queryStringArray != null && queryStringArray.length == 3) {
            this.mConfigurationHashMap.put(PhoneConfigurationType.LOW_CONFIGURATION_VERSION, Arrays.asList(queryStringArray));
        }
        if (queryStringArray2 != null && queryStringArray2.length == 3) {
            this.mConfigurationHashMap.put(PhoneConfigurationType.STANDARD_CONFIGURATION_VERSION, Arrays.asList(queryStringArray2));
        }
        if (queryStringArray3 != null && queryStringArray3.length == 3) {
            this.mConfigurationHashMap.put(PhoneConfigurationType.HIGH_CONFIGURATION_VERSION, Arrays.asList(queryStringArray3));
        }
        if (queryStringArray4 != null && queryStringArray4.length == 3) {
            this.mConfigurationHashMap.put(PhoneConfigurationType.ENJOY_VERSIION, Arrays.asList(queryStringArray4));
        }
        this.mPocoDevices = new ArrayList<>();
        String[] queryStringArray5 = queryStringArray(this.mContext, "poco_device_list");
        if (queryStringArray5 != null) {
            for (String str : queryStringArray5) {
                this.mPocoDevices.add(str);
            }
        }
    }

    public static boolean isLocalCnAndChinese() {
        return "CN".equals(Locale.getDefault().getCountry()) && "zh".equals(Locale.getDefault().getLanguage());
    }

    public static boolean isOwnerUser() {
        return UserHandle.myUserId() == 0;
    }

    public static boolean isUpdaterEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "miui_updater_enable", 0) == 1;
    }

    public static boolean isUseGota() {
        return miui.os.Build.IS_INTERNATIONAL_BUILD && SystemProperties.getInt("ro.miui.gota", 0) == 1 && !SystemProperties.get("ro.product.name", "unknown").endsWith("eea");
    }

    public static String miuiFormatSize(Context context, long j) {
        return MiuiUtils.formatSize(context, j);
    }

    public static String[] queryStringArray(Context context, String str) {
        int identifier;
        Resources resources = context.getResources();
        if (resources == null || (identifier = resources.getIdentifier(getResourceName(str), "array", context.getPackageName())) == 0) {
            return null;
        }
        return resources.getStringArray(identifier);
    }

    private static String readLine(String str) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(str), 256);
        try {
            return bufferedReader.readLine();
        } finally {
            bufferedReader.close();
        }
    }

    private long roundStorageSize(long j) {
        long j2 = 1;
        long j3 = 1;
        while (true) {
            long j4 = j2 * j3;
            if (j4 >= j) {
                return j4;
            }
            j2 <<= 1;
            if (j2 > 512) {
                j3 *= 1000;
                j2 = 1;
            }
        }
    }

    public static boolean setBooleanPreference(Context context, String str, boolean z) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (defaultSharedPreferences == null || TextUtils.isEmpty(str)) {
            return false;
        }
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putBoolean(str, z);
        return edit.commit();
    }

    public static boolean setStringPreference(Context context, String str, String str2) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        if (defaultSharedPreferences == null || TextUtils.isEmpty(str)) {
            return false;
        }
        SharedPreferences.Editor edit = defaultSharedPreferences.edit();
        edit.putString(str, str2);
        return edit.commit();
    }

    public static void startUpdater(Activity activity) {
        if (activity != null) {
            Intent intent = new Intent();
            if (!isUseGota() || isUpdaterEnable(activity)) {
                intent.setClassName("com.android.updater", "com.android.updater.MainActivity");
            } else {
                intent.setClassName("com.google.android.gms", "com.google.android.gms.update.SystemUpdateActivity");
            }
            intent.setFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            intent.putExtra("caller", "Settings");
            if (activity.getPackageManager().resolveActivity(intent, SearchUpdater.GOOGLE) != null) {
                activity.startActivity(intent);
                TypedArray obtainStyledAttributes = activity.obtainStyledAttributes(null, new int[]{16842936, 16842937}, 16842926, 0);
                int resourceId = obtainStyledAttributes.getResourceId(0, -1);
                int resourceId2 = obtainStyledAttributes.getResourceId(1, -1);
                obtainStyledAttributes.recycle();
                if (resourceId == -1 || resourceId2 == -1) {
                    return;
                }
                activity.overridePendingTransition(resourceId, resourceId2);
                MiStatInterfaceUtils.trackEvent("provision_about_page_goto_updater");
                OneTrackInterfaceUtils.track("provision_about_page_goto_updater", null);
            }
        }
    }

    public static boolean supportCit() {
        return FeatureParser.getBoolean("support_cit", true);
    }

    public static boolean supportDisplayPreInstalledApplication() {
        return FeatureParser.getBoolean("support_pre_installed_application", false) && !miui.os.Build.IS_GLOBAL_BUILD && isLocalCnAndChinese() && miui.os.Build.IS_STABLE_VERSION;
    }

    public String fillOverview() {
        return this.mContext.getString(R.string.available_size, getAvailableMemory()) + "\n" + this.mContext.getString(R.string.total_size, getTotalMemory());
    }

    public String getAvailableMemory() {
        return miuiFormatSize(this.mContext, getAvailableMemorySize());
    }

    public long getAvailableMemorySize() {
        long j = 0;
        if (!FeatureParser.getBoolean("support_emulated_storage", false) && !TextUtils.equals("mixed", SystemProperties.get("ro.boot.sdcard.type"))) {
            StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
            j = 0 + (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        }
        List<VolumeInfo> volumes = this.mStorageManager.getVolumes();
        Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
        for (VolumeInfo volumeInfo : volumes) {
            if (volumeInfo.getType() == 1) {
                try {
                    j = ((StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class)).getFreeBytes(volumeInfo.getFsUuid());
                } catch (Exception e) {
                    File path = volumeInfo.getPath();
                    if (path != null) {
                        j = path.getUsableSpace();
                    }
                    e.printStackTrace();
                }
            }
        }
        Log.d("MiuiAboutPhoneUtils", "getAvailableMemorySize(), current free memory : " + j);
        return j;
    }

    public String getCpuInfo() {
        try {
            String[] list = new File("/sys/devices/system/cpu").list(new FilenameFilter() { // from class: com.android.settings.device.MiuiAboutPhoneUtils.1
                @Override // java.io.FilenameFilter
                public boolean accept(File file, String str) {
                    return str.matches("cpu[0-9]{1}");
                }
            });
            String string = list.length == 2 ? getString(R.string.max_cpu2_info) : list.length == 4 ? getString(R.string.max_cpu4_info) : list.length == 6 ? getString(R.string.max_cpu6_info) : list.length == 8 ? getString(R.string.max_cpu8_info) : list.length == 10 ? getString(R.string.max_cpu10_info) : "";
            String maxFreqFromProcPriority = getMaxFreqFromProcPriority();
            if (!TextUtils.isEmpty(maxFreqFromProcPriority)) {
                return string + maxFreqFromProcPriority + getString(R.string.giga_hertz);
            }
            float f = 0.0f;
            for (int i = 0; i < list.length; i++) {
                float maxFre = getMaxFre(i, null);
                if (f < maxFre) {
                    f = maxFre;
                }
            }
            float maxFreFromProc = getMaxFreFromProc();
            if (maxFreFromProc < 0.0f) {
                maxFreFromProc = FeatureParser.getInteger("cpu_max_freq", -1);
            }
            if (maxFreFromProc >= 0.0f) {
                f = maxFreFromProc / 100.0f;
            }
            if ("A2-LTE-ALL".equals(SystemProperties.get("persist.radio.modem"))) {
                f = 1.8f;
            }
            return ((int) ((100.0f * f) + 0.5d)) % 10 == 0 ? string + String.format("%.1f", Float.valueOf(f)) + getString(R.string.giga_hertz) : string + String.format("%.2f", Float.valueOf(f)) + getString(R.string.giga_hertz);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public PhoneConfigurationType getPhoneConfigurationType() throws RuntimeException {
        String totaolRam = getTotaolRam();
        String totalMemory = getTotalMemory();
        String cpuInfo = getCpuInfo();
        if (!TextUtils.isEmpty(totalMemory) && totalMemory.contains(".")) {
            int indexOf = totalMemory.indexOf(".");
            totalMemory = totalMemory.substring(0, indexOf) + totalMemory.substring(indexOf + 1).replaceAll("\\d+", "");
        }
        for (PhoneConfigurationType phoneConfigurationType : PhoneConfigurationType.values()) {
            List<String> list = this.mConfigurationHashMap.get(phoneConfigurationType);
            if (list != null && list.size() == 3 && !TextUtils.isEmpty(totaolRam) && list.get(0).replace(" ", "").equals(totaolRam.replace(" ", "")) && !TextUtils.isEmpty(totalMemory) && list.get(1).replace(" ", "").equals(totalMemory.replace(" ", "")) && !TextUtils.isEmpty(cpuInfo) && list.get(2).replace(" ", "").equals(cpuInfo.replace(" ", ""))) {
                return phoneConfigurationType;
            }
        }
        throw new RuntimeException("Can not recognize phone configuration type, please make sure mConfigurationHashMap is correct ");
    }

    public String getTotalMemory() {
        return miuiFormatSize(this.mContext, getTotalMemoryBytes());
    }

    public long getTotalMemoryBytes() {
        if (this.mTotalMemoryByte == -1) {
            List<VolumeInfo> volumes = this.mStorageManager.getVolumes();
            Collections.sort(volumes, VolumeInfo.getDescriptionComparator());
            long j = 0;
            for (VolumeInfo volumeInfo : volumes) {
                if (volumeInfo.getType() == 1) {
                    try {
                        j = ((StorageStatsManager) this.mContext.getSystemService(StorageStatsManager.class)).getTotalBytes(volumeInfo.getFsUuid());
                    } catch (Exception e) {
                        File path = volumeInfo.getPath();
                        if (path != null) {
                            j = path.getTotalSpace();
                        }
                        long pow = (j >= 8000000000L ? (long) Math.pow(2.0d, Math.ceil(Math.log(j / 1000000000) / Math.log(2.0d))) : (j / 1000000000) + 1) * 1000000000;
                        if (pow < j) {
                            pow = roundStorageSize(j);
                        }
                        e.printStackTrace();
                        j = pow;
                    }
                }
            }
            this.mTotalMemoryByte = j;
        }
        return this.mTotalMemoryByte;
    }

    public String getTotaolRam() {
        String language = this.mContext.getResources().getConfiguration().locale.getLanguage();
        if (checkRamNotZero() && TextUtils.equals(language, this.mChangedLang)) {
            return this.mTotalRamStr;
        }
        this.mTotalRamStr = getRamFromProcMv();
        Log.i("MiuiAboutPhoneUtils", "getRamFromProcMv mTotalRamStr: " + this.mTotalRamStr);
        if (!checkRamNotZero()) {
            this.mTotalRamStr = formatSize(this.mContext, HardwareInfo.getTotalPhysicalMemory());
            Log.i("MiuiAboutPhoneUtils", "getTotalPhysicalMemory mTotalRamStr: " + this.mTotalRamStr);
        }
        this.mChangedLang = language;
        return this.mTotalRamStr;
    }

    public boolean isMIUILite() {
        return SettingsFeatures.isMiuiLiteVersion();
    }

    public boolean isPocoDevice() {
        ArrayList<String> arrayList = this.mPocoDevices;
        return (arrayList != null && arrayList.contains(Build.DEVICE)) || (!TextUtils.isEmpty(getDeviceMarketName()) && getDeviceMarketName().toLowerCase().contains("poco"));
    }
}
