package miui.telephony;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import miui.os.Build;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.phonenumber.CountryCode;

/* loaded from: classes4.dex */
public class MiuiHeDuoHaoUtil {
    public static final String DIAL = "dial";
    public static final String HEDUOHAO_PREFIX_SIM1 = "125831";
    public static final String HEDUOHAO_PREFIX_SIM2 = "125832";
    public static final String HEDUOHAO_PREFIX_SIM3 = "125833";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String ORDER_ID = "order_id";
    public static final String PASS_ID = "pass_id";
    public static final String SLOT_ID = "slot_id";
    public static final String STATUS = "status";
    public static final String SUB_ID = "sub_id";
    private static final String TAG = "MiuiHeDuoHaoUtil";
    public static final String TOGGLE = "toggle";
    public static final Uri URI = Uri.parse("content://com.android.providers.telephony.heduohaoprovider/heduohao");

    /* loaded from: classes4.dex */
    public static class HeDuoHao {
        public int mDial;
        public String mName;
        public String mNumber;
        public int mOrderId;
        public String mPassId;
        public int mSlotId;
        public int mStatus;
        public int mSubId;
        public int mToggle;
    }

    private MiuiHeDuoHaoUtil() {
    }

    public static String addHeDuoHaoPrefix(String str, int i) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        String replaceAll = str.substring(getIndexExcludeCC(str)).replaceAll(" ", "");
        if (i == 1) {
            return "125831" + replaceAll;
        } else if (i == 2) {
            return "125832" + replaceAll;
        } else if (i != 3) {
            return str;
        } else {
            return "125833" + replaceAll;
        }
    }

    public static int getIndexExcludeCC(String str) {
        String countryCode = PhoneNumberUtils.PhoneNumber.parse(str, "86".equals(CountryCode.getNetworkCountryCode())).getCountryCode();
        if (TextUtils.isEmpty(countryCode)) {
            return 0;
        }
        return str.indexOf(countryCode) + countryCode.length();
    }

    public static boolean isHeDuoHao(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String replaceAll = str.substring(getIndexExcludeCC(str)).replaceAll(" ", "");
        return replaceAll.startsWith("125831") || replaceAll.startsWith("125832") || replaceAll.startsWith("125833");
    }

    public static boolean isHeDuoHaoEnable() {
        return Build.IS_CM_CUSTOMIZATION || Build.IS_CM_CUSTOMIZATION_TEST;
    }

    public static String removeHeDuoHaoPrefix(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        if (isHeDuoHao(str)) {
            int indexExcludeCC = getIndexExcludeCC(str);
            String replaceAll = str.substring(indexExcludeCC).replaceAll(" ", "");
            if (replaceAll.startsWith("125831")) {
                Log.i(TAG, "removeHeDuoHaoPrefix: 1");
                return str.substring(0, indexExcludeCC) + replaceAll.replaceFirst("125831", "");
            } else if (replaceAll.startsWith("125832")) {
                Log.i(TAG, "removeHeDuoHaoPrefix: 2");
                return str.substring(0, indexExcludeCC) + replaceAll.replaceFirst("125832", "");
            } else if (replaceAll.startsWith("125833")) {
                Log.i(TAG, "removeHeDuoHaoPrefix: 3");
                return str.substring(0, indexExcludeCC) + replaceAll.replaceFirst("125833", "");
            } else {
                return replaceAll;
            }
        }
        return str;
    }
}
