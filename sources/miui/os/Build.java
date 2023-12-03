package miui.os;

import android.content.Context;
import android.os.Build;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import com.miui.internal.cust.PrivateConfig;
import com.miui.internal.cust.PrivateWaterMarkerConfig;
import miui.util.FeatureParser;

/* loaded from: classes3.dex */
public class Build extends android.os.Build {
    public static final int DEVICE_HIGH_END = 3;
    public static final int DEVICE_LOW_END = 1;
    public static final int DEVICE_MIDDLE_END = 2;
    public static final boolean HAS_CUST_PARTITION;
    public static final boolean IS_ALPHA_BUILD;
    public static final boolean IS_CDMA;
    public static final boolean IS_CM_COOPERATION;
    public static final boolean IS_CM_CUSTOMIZATION;
    public static final boolean IS_CM_CUSTOMIZATION_TEST;
    public static final boolean IS_CTA_BUILD = false;
    public static final boolean IS_CTS_BUILD;
    public static final boolean IS_CT_CUSTOMIZATION;
    public static final boolean IS_CT_CUSTOMIZATION_TEST;
    public static final boolean IS_CU_CUSTOMIZATION;
    public static final boolean IS_CU_CUSTOMIZATION_TEST;
    public static final boolean IS_DEBUGGABLE;
    public static final boolean IS_DEMO_BUILD;
    public static final boolean IS_DEVELOPMENT_VERSION;
    public static final boolean IS_FUNCTION_LIMITED;
    public static final boolean IS_GLOBAL_BUILD;
    public static final boolean IS_HONGMI;
    public static final boolean IS_HONGMI2_TDSCDMA;
    public static final boolean IS_HONGMI_THREE;
    public static final boolean IS_HONGMI_THREEX;
    public static final boolean IS_HONGMI_THREEX_CM;
    public static final boolean IS_HONGMI_THREEX_CT;
    public static final boolean IS_HONGMI_THREEX_CU;
    public static final boolean IS_HONGMI_THREE_LTE;
    public static final boolean IS_HONGMI_THREE_LTE_CM;
    public static final boolean IS_HONGMI_THREE_LTE_CU;
    public static final boolean IS_HONGMI_TWO;
    public static final boolean IS_HONGMI_TWOS_LTE_MTK;
    public static final boolean IS_HONGMI_TWOX;
    public static final boolean IS_HONGMI_TWOX_BR;
    public static final boolean IS_HONGMI_TWOX_CM;
    public static final boolean IS_HONGMI_TWOX_CT;
    public static final boolean IS_HONGMI_TWOX_CU;
    public static final boolean IS_HONGMI_TWOX_IN;
    public static final boolean IS_HONGMI_TWOX_LC;
    public static final boolean IS_HONGMI_TWOX_SA;
    public static final boolean IS_HONGMI_TWO_A;
    public static final boolean IS_HONGMI_TWO_S;
    public static final boolean IS_INTERNATIONAL_BUILD;
    public static final boolean IS_MI1S;
    public static final boolean IS_MI2A;
    public static final boolean IS_MIFIVE;
    public static final boolean IS_MIFOUR;
    public static final boolean IS_MIFOUR_CDMA;
    public static final boolean IS_MIFOUR_LTE_CM;
    public static final boolean IS_MIFOUR_LTE_CT;
    public static final boolean IS_MIFOUR_LTE_CU;
    public static final boolean IS_MIFOUR_LTE_INDIA;
    public static final boolean IS_MIFOUR_LTE_SEASA;
    public static final boolean IS_MIONE;
    public static final boolean IS_MIONE_CDMA;
    public static final boolean IS_MIPAD;
    public static final boolean IS_MITHREE;
    public static final boolean IS_MITHREE_CDMA;
    public static final boolean IS_MITHREE_TDSCDMA;
    public static final boolean IS_MITWO;
    public static final boolean IS_MITWO_CDMA;
    public static final boolean IS_MITWO_TDSCDMA;
    public static final boolean IS_MIUI;
    public static final boolean IS_MIUI_LITE_VERSION;
    public static final boolean IS_N7;
    public static final boolean IS_OFFICIAL_VERSION;
    public static final boolean IS_PRIVATE_BUILD;
    public static final boolean IS_PRIVATE_WATER_MARKER;
    public static final boolean IS_PRO_DEVICE;
    public static final boolean IS_STABLE_VERSION;
    public static final boolean IS_TABLET;
    public static final boolean IS_TDS_CDMA;
    public static final boolean IS_XIAOMI;
    private static final String PROP_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String REGULAR_EXPRESSION_FOR_DEVELOPMENT = "\\d+(.\\d+){2,}(-internal)?";
    private static final String REGULAR_EXPRESSION_FOR_STABLE = "^V(\\d+.)+([A-Z]+\\d{0,}.?)+(\\d+.?){0,}$";
    private static final String TAG = "lowmemvalue";
    public static final int TOTAL_RAM;
    public static final String USERDATA_IMAGE_VERSION_CODE;
    public static final String USER_MODE = "persist.sys.user_mode";
    public static final int USER_MODE_ELDER = 1;
    public static final int USER_MODE_NORMAL = 0;

    static {
        String str = android.os.Build.DEVICE;
        boolean z = "mione".equals(str) || "mione_plus".equals(str);
        IS_MIONE = z;
        String str2 = android.os.Build.MODEL;
        IS_MI1S = "MI 1S".equals(str2) || "MI 1SC".equals(str2);
        boolean z2 = "aries".equals(str) || "taurus".equals(str) || "taurus_td".equals(str);
        IS_MITWO = z2;
        IS_MI2A = "MI 2A".equals(str2) || "MI 2A TD".equals(str2);
        boolean z3 = "pisces".equals(str) || ("cancro".equals(str) && str2.startsWith("MI 3"));
        IS_MITHREE = z3;
        boolean z4 = "cancro".equals(str) && str2.startsWith("MI 4");
        IS_MIFOUR = z4;
        boolean equals = "virgo".equals(str);
        IS_MIFIVE = equals;
        IS_XIAOMI = z || z2 || z3 || z4 || equals;
        IS_MIPAD = "mocha".equals(str);
        IS_N7 = "flo".equals(str);
        boolean equals2 = "armani".equals(str);
        IS_HONGMI_TWO_A = equals2;
        boolean z5 = "HM2014011".equals(str) || "HM2014012".equals(str);
        IS_HONGMI_TWO_S = z5;
        boolean equals3 = "HM2014501".equals(str);
        IS_HONGMI_TWOS_LTE_MTK = equals3;
        boolean z6 = "HM2013022".equals(str) || "HM2013023".equals(str) || equals2 || z5;
        IS_HONGMI_TWO = z6;
        boolean z7 = "lcsh92_wet_jb9".equals(str) || "lcsh92_wet_tdd".equals(str);
        IS_HONGMI_THREE = z7;
        boolean equals4 = "dior".equals(str);
        IS_HONGMI_THREE_LTE = equals4;
        IS_HONGMI_THREE_LTE_CM = equals4 && "LTETD".equals(SystemProperties.get("ro.boot.modem"));
        IS_HONGMI_THREE_LTE_CU = equals4 && "LTEW".equals(SystemProperties.get("ro.boot.modem"));
        boolean equals5 = "HM2014811".equals(str);
        IS_HONGMI_TWOX_CU = equals5;
        boolean z8 = "HM2014812".equals(str) || "HM2014821".equals(str);
        IS_HONGMI_TWOX_CT = z8;
        boolean z9 = "HM2014813".equals(str) || "HM2014112".equals(str);
        IS_HONGMI_TWOX_CM = z9;
        boolean equals6 = "HM2014818".equals(str);
        IS_HONGMI_TWOX_IN = equals6;
        boolean equals7 = "HM2014817".equals(str);
        IS_HONGMI_TWOX_SA = equals7;
        boolean equals8 = "HM2014819".equals(str);
        IS_HONGMI_TWOX_BR = equals8;
        boolean z10 = equals5 || z8 || z9 || equals6 || equals7 || equals8;
        IS_HONGMI_TWOX = z10;
        boolean equals9 = "lte26007".equals(str);
        IS_HONGMI_TWOX_LC = equals9;
        boolean equals10 = "gucci".equals(str);
        IS_HONGMI_THREEX = equals10;
        IS_HONGMI_THREEX_CM = equals10 && "cm".equals(SystemProperties.get("persist.sys.modem"));
        IS_HONGMI_THREEX_CU = equals10 && "cu".equals(SystemProperties.get("persist.sys.modem"));
        IS_HONGMI_THREEX_CT = equals10 && "ct".equals(SystemProperties.get("persist.sys.modem"));
        IS_HONGMI = z6 || z7 || z10 || equals4 || equals9 || equals3 || equals10;
        boolean z11 = z && hasMsm8660Property();
        IS_MIONE_CDMA = z11;
        boolean z12 = z2 && "CDMA".equals(SystemProperties.get("persist.radio.modem"));
        IS_MITWO_CDMA = z12;
        boolean z13 = z3 && "MI 3C".equals(str2);
        IS_MITHREE_CDMA = z13;
        boolean z14 = z4 && "CDMA".equals(SystemProperties.get("persist.radio.modem"));
        IS_MIFOUR_CDMA = z14;
        boolean z15 = z2 && "TD".equals(SystemProperties.get("persist.radio.modem"));
        IS_MITWO_TDSCDMA = z15;
        boolean z16 = z3 && "TD".equals(SystemProperties.get("persist.radio.modem"));
        IS_MITHREE_TDSCDMA = z16;
        IS_MIFOUR_LTE_CM = z4 && "LTE-CMCC".equals(SystemProperties.get("persist.radio.modem"));
        IS_MIFOUR_LTE_CU = z4 && "LTE-CU".equals(SystemProperties.get("persist.radio.modem"));
        boolean z17 = z4 && "LTE-CT".equals(SystemProperties.get("persist.radio.modem"));
        IS_MIFOUR_LTE_CT = z17;
        IS_MIFOUR_LTE_INDIA = z4 && "LTE-India".equals(SystemProperties.get("persist.radio.modem"));
        IS_MIFOUR_LTE_SEASA = z4 && "LTE-SEAsa".equals(SystemProperties.get("persist.radio.modem"));
        boolean equals11 = "HM2013022".equals(str);
        IS_HONGMI2_TDSCDMA = equals11;
        IS_CDMA = z11 || z12 || z13 || z14 || z17;
        IS_TDS_CDMA = z16 || equals11 || z15;
        IS_CU_CUSTOMIZATION = "cu".equals(SystemProperties.get("ro.carrier.name"));
        IS_CM_CUSTOMIZATION = "cm".equals(SystemProperties.get("ro.carrier.name")) && ("cn_chinamobile".equals(SystemProperties.get("ro.miui.cust_variant")) || "cn_cta".equals(SystemProperties.get("ro.miui.cust_variant")));
        IS_CM_COOPERATION = "cm".equals(SystemProperties.get("ro.carrier.name")) && "cn_cmcooperation".equals(SystemProperties.get("ro.miui.cust_variant"));
        IS_CT_CUSTOMIZATION = "ct".equals(SystemProperties.get("ro.carrier.name"));
        boolean z18 = !TextUtils.isEmpty(Build.VERSION.INCREMENTAL) && Build.VERSION.INCREMENTAL.matches(REGULAR_EXPRESSION_FOR_DEVELOPMENT);
        IS_DEVELOPMENT_VERSION = z18;
        boolean z19 = "user".equals(android.os.Build.TYPE) && !TextUtils.isEmpty(Build.VERSION.INCREMENTAL) && Build.VERSION.INCREMENTAL.matches(REGULAR_EXPRESSION_FOR_STABLE);
        IS_STABLE_VERSION = z19;
        IS_OFFICIAL_VERSION = z18 || z19;
        IS_ALPHA_BUILD = SystemProperties.get("ro.product.mod_device", "").endsWith("_alpha");
        IS_DEMO_BUILD = SystemProperties.get("ro.product.mod_device", "").contains("_demo");
        IS_CTS_BUILD = !SystemProperties.getBoolean("persist.sys.miui_optimization", !"1".equals(SystemProperties.get("ro.miui.cts")));
        IS_PRIVATE_BUILD = PrivateConfig.IS_PRIVATE_BUILD;
        IS_PRIVATE_WATER_MARKER = PrivateWaterMarkerConfig.IS_PRIVATE_WATER_MARKER;
        IS_CM_CUSTOMIZATION_TEST = "cm".equals(SystemProperties.get("ro.cust.test"));
        IS_CU_CUSTOMIZATION_TEST = "cu".equals(SystemProperties.get("ro.cust.test"));
        IS_CT_CUSTOMIZATION_TEST = "ct".equals(SystemProperties.get("ro.cust.test"));
        IS_FUNCTION_LIMITED = "1".equals(SystemProperties.get("persist.sys.func_limit_switch"));
        TOTAL_RAM = getTotalPhysicalRam();
        IS_MIUI_LITE_VERSION = isMiuiLiteVersion();
        IS_INTERNATIONAL_BUILD = SystemProperties.get("ro.product.mod_device", "").contains("_global");
        IS_GLOBAL_BUILD = SystemProperties.get("ro.product.mod_device", "").endsWith("_global");
        IS_TABLET = isTablet();
        USERDATA_IMAGE_VERSION_CODE = getUserdataImageVersionCode();
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
        HAS_CUST_PARTITION = SystemProperties.getBoolean("ro.miui.has_cust_partition", false);
        IS_PRO_DEVICE = SystemProperties.get("ro.miui.cust_device", "").endsWith("_pro");
        IS_MIUI = !SystemProperties.get(PROP_MIUI_VERSION_CODE, "").isEmpty();
    }

    protected Build() throws InstantiationException {
        throw new InstantiationException("Cannot instantiate utility class");
    }

    public static boolean checkRegion(String str) {
        return getRegion().equalsIgnoreCase(str);
    }

    public static String getCustVariant() {
        return !IS_INTERNATIONAL_BUILD ? SystemProperties.get("ro.miui.cust_variant", "cn") : SystemProperties.get("ro.miui.cust_variant", "hk");
    }

    public static final int getDeviceLevelForAnimation() {
        int i = TOTAL_RAM;
        if (i <= 4) {
            return 1;
        }
        return i <= 8 ? 2 : 3;
    }

    public static final String getMiUiVersionCode() {
        return SystemProperties.get(PROP_MIUI_VERSION_CODE, "");
    }

    public static String getRegion() {
        return SystemProperties.get("ro.miui.region", "CN");
    }

    private static final int getTotalPhysicalRam() {
        try {
            return (int) (((((Long) Class.forName("miui.util.HardwareInfo").getMethod("getTotalPhysicalMemory", null).invoke(null, null)).longValue() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return -1;
        }
    }

    public static int getUserMode() {
        return SystemProperties.getInt(USER_MODE, 0);
    }

    private static String getUserdataImageVersionCode() {
        String str = SystemProperties.get("ro.miui.userdata_version", "");
        if ("".equals(str)) {
            return "Unavailable";
        }
        String str2 = IS_INTERNATIONAL_BUILD ? "global" : "cn";
        String str3 = SystemProperties.get("ro.carrier.name", "");
        if (!"".equals(str3)) {
            str3 = "_" + str3;
        }
        return String.format("%s(%s%s)", str, str2, str3);
    }

    public static boolean hasCameraFlash(Context context) {
        return FeatureParser.getBoolean("support_torch", true);
    }

    private static boolean hasMsm8660Property() {
        String str = SystemProperties.get("ro.soc.name");
        return "msm8660".equals(str) || "unkown".equals(str);
    }

    private static final boolean isMiuiLiteVersion() {
        int i = SystemProperties.getInt("ro.config.low_ram.threshold_gb", 0);
        int i2 = TOTAL_RAM;
        return i2 > 0 && i2 <= i;
    }

    private static boolean isTablet() {
        return SystemProperties.get("ro.build.characteristics").contains("tablet");
    }

    private static void reboot(boolean z, String str, boolean z2) {
        try {
            IPowerManager asInterface = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
            if (asInterface != null) {
                asInterface.reboot(z, str, z2);
            }
        } catch (RemoteException unused) {
        }
    }

    public static void setUserMode(Context context, int i) {
        SystemProperties.set(USER_MODE, Integer.toString(i));
        reboot(false, null, false);
    }
}
