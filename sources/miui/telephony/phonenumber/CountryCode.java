package miui.telephony.phonenumber;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.util.Arrays;
import java.util.List;
import miui.os.SystemProperties;
import miui.util.AppConstants;

/* loaded from: classes4.dex */
public class CountryCode {
    private static final String CONTACT_COUNTRYCODE = "persist.radio.countrycode";
    private static final String EMPTY = "";
    public static final String GSM_GENERAL_IDD_CODE = "+";
    private static String ICC_COUNTRY_CODE = "";
    private static String ICC_OPERATOR = "";
    private static String NETWORK_COUNTRY_CODE = "";
    private static String NETWORK_OPERATOR = "";
    private static final String DEFAULT_IDD_CODE = "00";
    private static List<String> NETWORK_IDD_CODE = Arrays.asList(DEFAULT_IDD_CODE);

    public static String getIccCountryCode() {
        updateIcc();
        return ICC_COUNTRY_CODE;
    }

    public static String getIddCode() {
        updateNetwork();
        return NETWORK_IDD_CODE.get(0);
    }

    public static List<String> getIddCodes() {
        updateNetwork();
        return NETWORK_IDD_CODE;
    }

    public static String getNetworkCountryCode() {
        updateNetwork();
        return NETWORK_COUNTRY_CODE;
    }

    public static String getUserDefinedCountryCode() {
        return SystemProperties.get(CONTACT_COUNTRYCODE, "");
    }

    public static boolean isChinaEnvironment() {
        String userDefinedCountryCode = getUserDefinedCountryCode();
        if (TextUtils.isEmpty(userDefinedCountryCode)) {
            userDefinedCountryCode = getIccCountryCode();
        }
        return CountryCodeConverter.isChinaEnvironment(userDefinedCountryCode, getNetworkCountryCode());
    }

    private static void updateIcc() {
        String simOperator = ((TelephonyManager) AppConstants.getCurrentApplication().getSystemService("phone")).getSimOperator();
        if (TextUtils.isEmpty(simOperator) || simOperator.length() < 3 || simOperator.equals(ICC_OPERATOR)) {
            return;
        }
        ICC_OPERATOR = simOperator;
        ICC_COUNTRY_CODE = CountryCodeConverter.getCountryCode(simOperator.substring(0, 3));
    }

    private static void updateNetwork() {
        String networkOperator = ((TelephonyManager) AppConstants.getCurrentApplication().getSystemService("phone")).getNetworkOperator();
        if (TextUtils.isEmpty(networkOperator) || networkOperator.length() < 3 || networkOperator.equals(NETWORK_OPERATOR)) {
            return;
        }
        NETWORK_OPERATOR = networkOperator;
        String substring = networkOperator.substring(0, 3);
        NETWORK_COUNTRY_CODE = CountryCodeConverter.getCountryCode(substring);
        List<String> iddCodes = CountryCodeConverter.getIddCodes(substring);
        NETWORK_IDD_CODE = iddCodes;
        if (iddCodes == null || NETWORK_COUNTRY_CODE.isEmpty()) {
            NETWORK_IDD_CODE = Arrays.asList(DEFAULT_IDD_CODE);
        }
    }
}
