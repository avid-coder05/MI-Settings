package miui.telephony;

import android.content.Context;
import android.os.Process;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import com.miui.internal.telephony.TelephonyManagerAndroidImpl;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.reflect.Method;

/* loaded from: classes4.dex */
public abstract class TelephonyManager {
    public static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    public static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE";
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_OFFHOOK = 2;
    public static final int CALL_STATE_RINGING = 1;
    public static final int CF_ACTION_DISABLE = 0;
    public static final int CF_ACTION_ENABLE = 1;
    public static final int CF_ACTION_ERASURE = 4;
    public static final int CF_ACTION_REGISTRATION = 3;
    public static final int CF_REASON_ALL = 4;
    public static final int CF_REASON_ALL_CONDITIONAL = 5;
    public static final int CF_REASON_BUSY = 1;
    public static final int CF_REASON_NOT_REACHABLE = 3;
    public static final int CF_REASON_NO_REPLY = 2;
    public static final int CF_REASON_UNCONDITIONAL = 0;
    public static final int CT_VOLTE_MODE_HVOLTE = 2;
    public static final int CT_VOLTE_MODE_NOT_SUPPORT = 0;
    public static final int CT_VOLTE_MODE_VOLTE_ONLY = 1;
    public static final String CUSTOMIZED_REGION;
    public static final int DATA_ACTIVITY_DORMANT = 4;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_CONNECTED = 2;
    public static final int DATA_CONNECTING = 1;
    public static final int DATA_DISCONNECTED = 0;
    public static final String DATA_DOMESTIC_ROAMING = "data_domestic_roaming";
    public static final int DATA_SUSPENDED = 3;
    public static final String EXTRA_INCOMING_NUMBER = "incoming_number";
    public static final String EXTRA_STATE = "state";
    private static final boolean IS_CARRIER_APEX_ENABLED;
    private static final boolean IS_CUST_SINGLE_SIM;
    private static final boolean IS_GOOGLE_CSP;
    public static final String MCC_CHINA = "460";
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_DC_HSPAP = 20;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_LTE_CA = 19;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final String OPERATOR_NUMERIC_CHINA_MOBILE = "46000";
    public static final String OPERATOR_NUMERIC_CHINA_TELECOM = "46003";
    public static final String OPERATOR_NUMERIC_CHINA_UNICOM = "46001";
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_SIP = 3;
    public static final String REGION;
    public static final int SET_CALL_FOWARD_FAILURE = -1;
    public static final int SET_CALL_FOWARD_NOT_SUPPORT = -2;
    public static final int SET_CALL_FOWARD_SUCCESS = 0;
    public static final int SET_CALL_FOWARD_UT_DATA_DISABLED = -3;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_UNKNOWN = 0;
    protected static final String TAG = "TeleMgr";
    private static final String[] VDF_NUMERICS;
    public static final boolean VICE_IMS_AUTO_REJECT_SUPPORT;
    private String BUILD_OPERATOR_TYPE = null;
    public static final String EXTRA_STATE_IDLE = android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
    public static final String EXTRA_STATE_RINGING = android.telephony.TelephonyManager.EXTRA_STATE_RINGING;
    public static final String EXTRA_STATE_OFFHOOK = android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class Holder {
        static final TelephonyManager INSTANCE;

        static {
            INSTANCE = Build.IS_MIUI ? getMiuiTelephonyManager() : TelephonyManagerAndroidImpl.getDefault();
        }

        private Holder() {
        }

        private static TelephonyManager getMiuiTelephonyManager() {
            try {
                Class<?> cls = Class.forName("miui.telephony.TelephonyManagerEx");
                return (TelephonyManager) Method.of(cls, "getDefault", cls, new Class[0]).invokeObject(cls, (Object) null, new Object[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    static {
        IS_CUST_SINGLE_SIM = SystemProperties.getInt("ro.miui.singlesim", 0) == 1;
        IS_GOOGLE_CSP = SystemProperties.getBoolean("ro.miui.google.csp", false);
        CUSTOMIZED_REGION = SystemProperties.get("ro.miui.customized.region", "");
        REGION = SystemProperties.get("ro.miui.build.region", "");
        IS_CARRIER_APEX_ENABLED = SystemProperties.getBoolean("ro.miui.carrier.apex", false);
        VICE_IMS_AUTO_REJECT_SUPPORT = SystemProperties.getBoolean("ro.miui.vicegwsd", false);
        VDF_NUMERICS = new String[]{"23003", "26202", "21401", "20205", "21670", "27201", "22210", "20404", "26801", "22601", "23415"};
    }

    public static boolean checkCallingOrSelfPermissionGranted(int i) {
        if (getAppIdUserHandle(i) != 1000) {
            return Process.myUid() >= 0 && isSameAppUserHandle(i, Process.myUid());
        }
        return true;
    }

    private static int getAppIdUserHandle(int i) {
        Class cls = Integer.TYPE;
        return Method.of(UserHandle.class, "getAppId", cls, new Class[]{cls}).invokeInt(UserHandle.class, (Object) null, new Object[]{Integer.valueOf(i)});
    }

    public static TelephonyManager getDefault() {
        return Holder.INSTANCE;
    }

    public static boolean isBuildRegionForTaiWan() {
        return "tw".equals(REGION);
    }

    public static boolean isCarrierApexEnabled() {
        return IS_CARRIER_APEX_ENABLED;
    }

    public static boolean isCustForClEntel() {
        String str = CUSTOMIZED_REGION;
        return "cl_entel".equals(str) || "cl_en".equals(str);
    }

    public static boolean isCustForEsTelefonica() {
        return "es_telefonica".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForEsVodafone() {
        return "es_vodafone".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrOrange() {
        return "fr_orange".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrRussia() {
        return "ru".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrSfr() {
        return "fr_sfr".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForHkH3g() {
        return "hk_h3g".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForJpKd() {
        return "jp_kd".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForJpSb() {
        return "jp_sb".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrKt() {
        return "kr_kt".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrLgu() {
        return "kr_lgu".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrOps() {
        return isCustForKrSkt() || isCustForKrKt() || isCustForKrLgu();
    }

    public static boolean isCustForKrSkt() {
        return "kr_skt".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForLmClaro() {
        return "lm_cr".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForMxAt() {
        return "mx_at".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForMxTelcel() {
        return "mx_telcel".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForTurkCell() {
        return "tr_turkcell".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustSingleSimDevice() {
        return IS_CUST_SINGLE_SIM;
    }

    public static boolean isDomesticRoamingEnable(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), DATA_DOMESTIC_ROAMING, 1) != 0;
    }

    public static boolean isFiveSignalBarsSupported() {
        return !isCustForJpSb();
    }

    public static boolean isForEEA() {
        return "eea".equals(REGION);
    }

    public static boolean isForEEAHasSubnetVDF() {
        if (isForEEA()) {
            String str = SystemProperties.get("gsm.sim.operator.numeric", "");
            for (String str2 : VDF_NUMERICS) {
                if (str.contains(str2)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean isGoogleCsp() {
        return IS_GOOGLE_CSP;
    }

    private static boolean isSameAppUserHandle(int i, int i2) {
        Class cls = Boolean.TYPE;
        Class cls2 = Integer.TYPE;
        return Method.of(UserHandle.class, "isSameApp", cls, new Class[]{cls2, cls2}).invokeBoolean(UserHandle.class, (Object) null, new Object[]{Integer.valueOf(i), Integer.valueOf(i2)});
    }

    public static boolean isSupportDomesticRoaming() {
        return Build.checkRegion("PL");
    }

    public static boolean isViceImsAutoRejectSupport() {
        return !Build.IS_INTERNATIONAL_BUILD && VICE_IMS_AUTO_REJECT_SUPPORT;
    }

    public static boolean setDomesticRoamingEnable(Context context, boolean z) {
        return Settings.Global.putInt(context.getContentResolver(), DATA_DOMESTIC_ROAMING, z ? 1 : 0);
    }

    public abstract int getCallState();

    public abstract int getCallStateForSlot(int i);

    public abstract int getCallStateForSubscription(int i);

    public abstract CellLocation getCellLocationForSlot(int i);

    public int getCtVolteSupportedMode() {
        return 0;
    }

    public abstract int getDataActivity();

    public abstract int getDataActivityForSlot(int i);

    public abstract int getDataActivityForSubscription(int i);

    public abstract int getDataState();

    public abstract int getDataStateForSlot(int i);

    public abstract int getDataStateForSubscription(int i);

    public abstract String getDeviceId();

    public abstract String getDeviceIdForSlot(int i);

    public abstract String getDeviceIdForSubscription(int i);

    public List<String> getDeviceIdList() {
        Rlog.i("TelephonyManager", "unexpected getDeviceIdList method call");
        return null;
    }

    public abstract String getDeviceSoftwareVersion();

    public abstract String getDeviceSoftwareVersionForSlot(int i);

    public abstract String getDeviceSoftwareVersionForSubscription(int i);

    public int getIccCardCount() {
        int phoneCount = getPhoneCount();
        int i = 0;
        for (int i2 = 0; i2 < phoneCount; i2++) {
            if (hasIccCard(i2)) {
                i++;
            }
        }
        return i;
    }

    public abstract String getImei();

    public abstract String getImeiForSlot(int i);

    public abstract String getImeiForSubscription(int i);

    public List<String> getImeiList() {
        return new ArrayList(0);
    }

    public abstract String getLine1Number();

    public abstract String getLine1NumberForSlot(int i);

    public abstract String getLine1NumberForSubscription(int i);

    public abstract String getMeid();

    public abstract String getMeidForSlot(int i);

    public abstract String getMeidForSubscription(int i);

    public List<String> getMeidList() {
        return new ArrayList(0);
    }

    public abstract String getMiuiDeviceId();

    public abstract int getMiuiLevel(SignalStrength signalStrength);

    public String getMobileNetworkCapability(int i) {
        return null;
    }

    public abstract String getNetworkCountryIso();

    public abstract String getNetworkCountryIsoForSlot(int i);

    public abstract String getNetworkCountryIsoForSubscription(int i);

    public abstract String getNetworkOperator();

    public abstract String getNetworkOperatorForSlot(int i);

    public abstract String getNetworkOperatorForSubscription(int i);

    public abstract String getNetworkOperatorName();

    public abstract String getNetworkOperatorNameForSlot(int i);

    public abstract String getNetworkOperatorNameForSubscription(int i);

    public abstract int getNetworkType();

    public abstract int getNetworkTypeForSlot(int i);

    public abstract int getNetworkTypeForSubscription(int i);

    public int getNrConfigType() {
        return -1;
    }

    public int getNrConfigType(int i) {
        return -1;
    }

    public abstract int getPhoneCount();

    public abstract int getPhoneType();

    public abstract int getPhoneTypeForSlot(int i);

    public abstract int getPhoneTypeForSubscription(int i);

    public abstract String getSimCountryIso();

    public abstract String getSimCountryIsoForSlot(int i);

    public abstract String getSimCountryIsoForSubscription(int i);

    public abstract String getSimOperator();

    public abstract String getSimOperatorForSlot(int i);

    public abstract String getSimOperatorForSubscription(int i);

    public abstract String getSimOperatorName();

    public abstract String getSimOperatorNameForSlot(int i);

    public abstract String getSimOperatorNameForSubscription(int i);

    public abstract String getSimSerialNumber();

    public abstract String getSimSerialNumberForSlot(int i);

    public abstract String getSimSerialNumberForSubscription(int i);

    public abstract int getSimState();

    public abstract int getSimStateForSlot(int i);

    public abstract int getSimStateForSubscription(int i);

    public abstract String getSmallDeviceId();

    public String getSpn(String str, int i, String str2, boolean z) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public abstract String getSubscriberId();

    public abstract String getSubscriberIdForSlot(int i);

    public abstract String getSubscriberIdForSubscription(int i);

    public abstract String getVoiceMailAlphaTag();

    public abstract String getVoiceMailAlphaTagForSlot(int i);

    public abstract String getVoiceMailAlphaTagForSubscription(int i);

    public abstract String getVoiceMailNumber();

    public abstract String getVoiceMailNumberForSlot(int i);

    public abstract String getVoiceMailNumberForSubscription(int i);

    public int getXMNetworkType() {
        return 0;
    }

    public int getXMNetworkType(int i) {
        return 0;
    }

    public abstract boolean hasIccCard();

    public abstract boolean hasIccCard(int i);

    public boolean isChinaTelecomTest(String str) {
        if (!Build.IS_CT_CUSTOMIZATION_TEST || TextUtils.isEmpty(str)) {
            return false;
        }
        return str.equals(OPERATOR_NUMERIC_CHINA_TELECOM) || str.equals("46011") || str.equals("45502") || str.equals("45507") || str.equals("00101") || str.equals("20404");
    }

    public boolean isCmccCooperationDevice() {
        return false;
    }

    public boolean isDisableLte(boolean z) {
        if (Build.IS_GLOBAL_BUILD) {
            String str = android.os.Build.DEVICE;
            boolean z2 = "ido".equals(str) || "kenzo".equals(str);
            if (z2 && z) {
                int phoneCount = getPhoneCount();
                for (int i = 0; i < phoneCount; i++) {
                    String simOperatorForSlot = getSimOperatorForSlot(i);
                    if (simOperatorForSlot != null && simOperatorForSlot.startsWith("510")) {
                        return true;
                    }
                }
                return false;
            }
            return z2;
        }
        return false;
    }

    public boolean isDualNrEnabled() {
        return false;
    }

    public boolean isDualNrSupported() {
        return false;
    }

    public boolean isDualSaSupported() {
        return false;
    }

    public boolean isDualVolteSupported() {
        return false;
    }

    public boolean isFiveGCapable() {
        return false;
    }

    public boolean isGameFiveGOptimizeSupported() {
        return false;
    }

    public boolean isGwsdSupport() {
        return false;
    }

    public boolean isImsRegistered(int i) {
        return false;
    }

    public abstract boolean isMultiSimEnabled();

    public boolean isSameOperator(String str, String str2) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public boolean isUserFiveGEnabled() {
        return false;
    }

    public boolean isUserFiveGSaEnabled() {
        return false;
    }

    public boolean isUserFiveGSaEnabled(int i) {
        return false;
    }

    public boolean isVideoTelephonyAvailable(int i) {
        return false;
    }

    public boolean isVoNRSupported() {
        return false;
    }

    public abstract boolean isVoiceCapable();

    public boolean isVolteEnabledByPlatform() {
        return false;
    }

    public boolean isVolteEnabledByPlatform(int i) {
        return false;
    }

    public boolean isVolteEnabledByUser() {
        return false;
    }

    public boolean isVolteEnabledByUser(int i) {
        return false;
    }

    public boolean isVtEnabledByPlatform() {
        return false;
    }

    public boolean isVtEnabledByPlatform(int i) {
        return false;
    }

    public boolean isWifiCallingAvailable(int i) {
        return false;
    }

    public abstract void listen(PhoneStateListener phoneStateListener, int i);

    public abstract void listenForSlot(int i, PhoneStateListener phoneStateListener, int i2);

    public abstract void listenForSubscription(int i, PhoneStateListener phoneStateListener, int i2);

    public void setCallForwardingOption(int i, int i2, int i3, String str, ResultReceiver resultReceiver) {
        throw new UnsupportedOperationException("setCallForwardingOption not supported");
    }

    public void setIccCardActivate(int i, boolean z) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public void setMobileNetworkCapability(String str) {
    }

    public void setUserFiveGEnabled(boolean z) {
    }

    public void setUserFiveGSaEnabled(boolean z) {
    }

    public void setUserFiveGSaEnabled(boolean z, int i) {
    }
}
