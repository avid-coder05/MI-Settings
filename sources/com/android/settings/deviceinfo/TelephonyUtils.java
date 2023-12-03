package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.ServiceState;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import com.android.settings.R;
import miui.telephony.TelephonyManager;

/* loaded from: classes.dex */
public class TelephonyUtils {
    private static final String TAG = "TelephonyUtils";
    private static String[] mDataTypeNameDefault;
    private static SparseArray<String> mDataTypeNameCusRegMap = new SparseArray<>();
    private static SparseArray<String> mDataTypeNameMIUIRegion = new SparseArray<>();
    private static SparseArray<String> mDataTypeNameMcc = new SparseArray<>();
    private static SparseArray<String> mDataTypeNameMccMnc = new SparseArray<>();

    public static int getDataNetTypeFromServiceState(int i, ServiceState serviceState) {
        int i2;
        if (Build.VERSION.SDK_INT >= 25) {
            i2 = 19;
            if ((i == 13 || i == 19) && serviceState != null) {
                if (!serviceState.isUsingCarrierAggregation()) {
                    i2 = 13;
                }
                Log.d(TAG, "getDataNetTypeFromServiceState:srcDataNetType = " + i + ", destDataNetType " + i2);
                return i2;
            }
        }
        i2 = i;
        Log.d(TAG, "getDataNetTypeFromServiceState:srcDataNetType = " + i + ", destDataNetType " + i2);
        return i2;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x00df A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00e0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static java.lang.String getNetworkTypeName(android.content.Context r4, int r5, int r6, boolean r7) {
        /*
            Method dump skipped, instructions count: 263
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.deviceinfo.TelephonyUtils.getNetworkTypeName(android.content.Context, int, int, boolean):java.lang.String");
    }

    public static String getNetworkTypeName(String str) {
        return shouldShowLTE() ? "4G".equals(str) ? "LTE" : "4G+".equals(str) ? "LTE+" : str : str;
    }

    private static Resources getResourcesForOperation(Context context, String str, boolean z) {
        String str2 = TAG;
        Log.d(str2, "getResourcesForOperation operation=" + str + ",invalidMnc=" + z);
        if (context == null) {
            return null;
        }
        if (TextUtils.isEmpty(str)) {
            return context.getResources();
        }
        Configuration configuration = context.getResources().getConfiguration();
        Configuration configuration2 = new Configuration();
        configuration2.setTo(configuration);
        try {
            int intValue = Integer.valueOf(str.substring(0, 3)).intValue();
            int intValue2 = z ? Integer.valueOf(str.substring(3, str.length())).intValue() : 0;
            configuration2.mcc = intValue;
            configuration2.mnc = intValue2;
            if (intValue2 == 0) {
                configuration2.mnc = 65535;
            }
            Log.d(str2, "getResourcesForOperation mcc=" + intValue + ",mnc=" + intValue2);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            DisplayMetrics displayMetrics2 = new DisplayMetrics();
            displayMetrics2.setTo(displayMetrics);
            return new Resources(context.getResources().getAssets(), displayMetrics2, configuration2);
        } catch (Exception e) {
            Log.e(TAG, "getResourcesForOperation " + e);
            return null;
        }
    }

    private static String getSimCountryCode() {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        if (telephonyManager.getSimState() == 5) {
            return telephonyManager.getSimCountryIso();
        }
        Log.d(TAG, "getSimCountryCode sim is not ready");
        return "";
    }

    public static void initDataTypeName(Context context) {
        setDataTypeDefault(context);
        setDataTypeCusomizedRegion(context);
        updateDataTypeMiuiRegion(context, System.getProperty("ro.miui.mcc"));
    }

    private static void setDataTypeCusomizedRegion(Context context) {
        if (context == null) {
            return;
        }
        try {
            int[] intArray = context.getResources().getIntArray(R.array.data_type_name_cus_reg_key);
            String[] stringArray = context.getResources().getStringArray(R.array.data_type_name_cus_reg_value);
            if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                for (int i = 0; i < intArray.length; i++) {
                    mDataTypeNameCusRegMap.put(intArray[i], stringArray[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "setDataTypeCusomizedRegion " + e);
        }
    }

    private static void setDataTypeDefault(Context context) {
        if (context != null && mDataTypeNameDefault == null) {
            try {
                mDataTypeNameDefault = context.getResources().getStringArray(R.array.data_network_type_name_default);
            } catch (Exception e) {
                Log.e(TAG, "setDataTypeDefault " + e);
            }
        }
    }

    private static boolean shouldShowLTE() {
        String simCountryCode = getSimCountryCode();
        return "DE".equalsIgnoreCase(simCountryCode) || "CS".equalsIgnoreCase(simCountryCode) || "PL".equalsIgnoreCase(simCountryCode) || "CZ".equalsIgnoreCase(simCountryCode) || "AT".equalsIgnoreCase(simCountryCode);
    }

    public static void updateDataTypeMcc(Context context, String str) {
        String str2 = TAG;
        Log.d(str2, "updateDataTypeMcc  operation=" + str);
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            mDataTypeNameMcc.clear();
            Resources resourcesForOperation = getResourcesForOperation(context, str, false);
            if (resourcesForOperation == null) {
                return;
            }
            int[] intArray = resourcesForOperation.getIntArray(R.array.data_type_name_mcc_key);
            String[] stringArray = resourcesForOperation.getStringArray(R.array.data_type_name_mcc_value);
            Log.d(str2, "updateDataTypeMcc  cus_key=" + intArray + ",cus_val=" + stringArray);
            if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                for (int i = 0; i < intArray.length; i++) {
                    Log.d(TAG, "updateDataTypeMcc  cus_key[i]=" + intArray[i] + ",cus_val[i]=" + stringArray[i]);
                    mDataTypeNameMcc.put(intArray[i], stringArray[i]);
                }
                return;
            }
            Log.w(str2, "updateDataTypeMcc  cus_key or cus_val is null");
        } catch (Exception e) {
            Log.e(TAG, "updateDataTypeMcc " + e);
        }
    }

    public static void updateDataTypeMccMnc(Context context, String str) {
        String str2 = TAG;
        Log.d(str2, "updateDataTypeMccMnc  operation=" + str);
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            mDataTypeNameMccMnc.clear();
            Resources resourcesForOperation = getResourcesForOperation(context, str, true);
            if (resourcesForOperation == null) {
                return;
            }
            int[] intArray = resourcesForOperation.getIntArray(R.array.data_type_name_mcc_mnc_key);
            String[] stringArray = resourcesForOperation.getStringArray(R.array.data_type_name_mcc_mnc_value);
            if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                for (int i = 0; i < intArray.length; i++) {
                    mDataTypeNameMccMnc.put(intArray[i], stringArray[i]);
                }
                return;
            }
            Log.w(str2, "updateDataTypeMccMnc  key or val is null");
        } catch (Exception e) {
            Log.e(TAG, "updateDataTypeMccMnc " + e);
        }
    }

    private static void updateDataTypeMiuiRegion(Context context, String str) {
        if (context == null || TextUtils.isEmpty(str)) {
            return;
        }
        try {
            mDataTypeNameMIUIRegion.clear();
            if (!TextUtils.isEmpty(str) && str.length() >= 1) {
                str = str.substring(1, str.length());
            }
            Resources resourcesForOperation = getResourcesForOperation(context, str, false);
            if (resourcesForOperation == null) {
                return;
            }
            int[] intArray = resourcesForOperation.getIntArray(R.array.data_type_name_miui_mcc_key);
            String[] stringArray = resourcesForOperation.getStringArray(R.array.data_type_name_miui_mcc_value);
            if (intArray != null && stringArray != null && intArray.length == stringArray.length) {
                for (int i = 0; i < intArray.length; i++) {
                    mDataTypeNameMIUIRegion.put(intArray[i], stringArray[i]);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "updateDataTypeMiuiRegion " + e);
        }
    }
}
