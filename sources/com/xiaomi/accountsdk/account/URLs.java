package com.xiaomi.accountsdk.account;

import java.util.HashMap;
import java.util.Map;
import miui.cloud.CloudRequestUtils;

/* loaded from: classes2.dex */
public class URLs {
    public static final String ACCOUNT_DOMAIN;
    public static final String CA_ACCOUNT_DOMAIN;
    static final String HOST_URL_ACCOUNT_BASE;
    static final String OPEN_URL_GET_ACCESS_TOKEN;
    static final String OPEN_URL_REFRESH_ACCESS_TOKEN;
    public static final String URL_ACCOUNT_API_V2_BASE;
    public static final String URL_ACCOUNT_API_V3_BASE;
    public static final String URL_ACCOUNT_BASE;
    public static final String URL_ACCOUNT_HELP_CENTER;
    public static final String URL_ACCOUNT_OAUTH_BASE;
    public static final String URL_ACCOUNT_SAFE_API_BASE;
    public static final String URL_ACCOUNT_SERVICE_CONFIG;
    static final String URL_ACCOUNT_USER_PROFILE;
    @Deprecated
    public static final String URL_ACOUNT_API_BASE;
    public static final String URL_ACOUNT_API_BASE_SECURE;
    public static final String URL_ACOUNT_API_BASE_V2_SECURE;
    public static final String URL_ACTIVATOR_BASE;
    static final String URL_ADD_BIND_EMAIL;
    static final String URL_ADD_BIND_PHONE;
    static final String URL_AUTH2_AUTHORIZE;
    public static final String URL_CHANGE_PASSWORD;
    static final String URL_CHECK_PHONE_ACTIVATE_STATUS;
    static final String URL_CHECK_SAFE_EMAIL_AVAILABILITY;
    static final String URL_COMMIT_UPDATE_ICON;
    static final String URL_DELETE_BIND_PHONE;
    public static final String URL_DEVICES_SETTING;
    public static final String URL_DEV_BASE;
    public static final String URL_DEV_SETTING;
    static final String URL_EMAIL_REGISTER;
    static String URL_EXCHANGE_PHONETOKE_HTTPS;
    static final String URL_GENERATE_RANDOM_PASSWORD;
    public static final String URL_GET_BIND_EMAIL_CAPTCODE;
    public static final String URL_GET_COUNTRY_CODE;
    public static final String URL_GET_DEVICE_MODEL_INFOS;
    public static final String URL_GET_USER_CORE_INFO;
    static final String URL_IDENTITY_AUTH_FOR_ADDING_EMAIL;
    static final String URL_IDENTITY_AUTH_FOR_ADDING_PHONE;
    static final String URL_IDENTITY_AUTH_FOR_CHANGE_PWD;
    static final String URL_IDENTITY_AUTH_FOR_DELETING_PHONE;
    static final String URL_IDENTITY_AUTH_FOR_MODIFY_SAFE_PHONE;
    static final String URL_IDENTITY_AUTH_FOR_REPLACING_EMAIL;
    static final String URL_IDENTITY_AUTH_FOR_REPLACING_PHONE;
    static final String URL_IDENTITY_AUTH_FOR_SEND_EMAIL_ACTIVATE_MESSAGE;
    static final String URL_IDENTITY_AUTH_FOR_SET_SECURITY_QUESTIONS;
    @Deprecated
    public static final String URL_LOGIN;
    public static final String URL_LOGIN_AUTH2;
    static String URL_LOGIN_AUTH2_HTTPS;
    static final String URL_LOGIN_AUTH2_PASSPORT_CA;
    public static final String URL_LOGIN_AUTH_STEP2;
    static String URL_LOGIN_HTTPS;
    static final String URL_LOGIN_PASSPORT_CA;
    public static final String URL_MI_ACCOUNT_VIP_INFO;
    public static final String URL_ONE_STEP_TRANSFER_TOKEN;
    public static final String URL_OPEN_ACCOUNT_THIRD_BASE;
    static final String URL_PASSPORT_CA_ACCOUNT_BASE;
    public static final String URL_QUERY_VERIFY_PHONE_INFO;
    static final String URL_REG;
    static final String URL_REG_CHECK_VERIFY_CODE;
    public static final String URL_REG_GET_CAPTCHA_CODE;
    static final String URL_REG_GET_VERIFY_CODE;
    static final String URL_REG_PHONE;
    static final String URL_REG_SEND_PHONE_TICKET;
    static final String URL_REG_TOKEN;
    static final String URL_REG_VERIFY_PHONE;
    static final String URL_REPLACE_BIND_EMAIL;
    static final String URL_REPLACE_BIND_PHONE;
    public static final String URL_REPORT_VERIFY_PHONE_NOTIFY_STATUS;
    static final String URL_REQUEST_UPDATE_ICON;
    public static final String URL_RESEND_EMAIL;
    static final String URL_RESET_PASSWORD;
    static final String URL_SEND_BIND_EMAIL_VERIFY_CODE;
    static final String URL_SEND_BIND_PHONE_VERIFY_CODE;
    static final String URL_SEND_EMAIL_ACTIVATE_MESSAGE;
    static final String URL_SET_SECURITY_QUESTIONS;
    public static final String URL_SET_USER_EDUCATION;
    public static final String URL_SET_USER_INCOME;
    public static final String URL_SET_USER_LOCATION;
    public static final String URL_SET_USER_REGION;
    public static final String URL_USER_EXISTS;
    static final boolean USE_PREVIEW;
    static final Map<String, String> caUrlMap;

    static {
        boolean isStaging = XMPassportSettings.isStaging();
        USE_PREVIEW = isStaging;
        String str = isStaging ? "http://account.preview.n.xiaomi.net" : "https://account.xiaomi.com";
        ACCOUNT_DOMAIN = str;
        CA_ACCOUNT_DOMAIN = isStaging ? "http://account.preview.n.xiaomi.net" : "https://c.id.mi.com";
        HOST_URL_ACCOUNT_BASE = isStaging ? "account.preview.n.xiaomi.net" : "account.xiaomi.com";
        String str2 = isStaging ? "http://account.preview.n.xiaomi.net/pass" : "https://account.xiaomi.com/pass";
        URL_ACCOUNT_BASE = str2;
        String str3 = isStaging ? "http://api.account.xiaomi.com/pass" : "https://api.account.xiaomi.com/pass";
        URL_ACTIVATOR_BASE = str3;
        String str4 = isStaging ? "http://account.preview.n.xiaomi.net/pass" : "http://c.id.mi.com/pass";
        URL_PASSPORT_CA_ACCOUNT_BASE = str4;
        URL_ACOUNT_API_BASE = isStaging ? "http://api.account.preview.n.xiaomi.net/pass" : "http://api.account.xiaomi.com/pass";
        String str5 = isStaging ? "http://api.account.preview.n.xiaomi.net/pass" : "https://api.account.xiaomi.com/pass";
        URL_ACOUNT_API_BASE_SECURE = str5;
        String str6 = isStaging ? "http://api.account.preview.n.xiaomi.net/pass/v2" : "https://api.account.xiaomi.com/pass/v2";
        URL_ACOUNT_API_BASE_V2_SECURE = str6;
        String str7 = isStaging ? "http://api.account.preview.n.xiaomi.net/pass/v2/safe" : "https://api.account.xiaomi.com/pass/v2/safe";
        URL_ACCOUNT_SAFE_API_BASE = str7;
        URL_ACCOUNT_API_V2_BASE = isStaging ? "http://api.account.preview.n.xiaomi.net/pass/v2" : "https://api.account.xiaomi.com/pass/v2";
        String str8 = isStaging ? "http://api.account.preview.n.xiaomi.net/pass/v3" : "https://api.account.xiaomi.com/pass/v3";
        URL_ACCOUNT_API_V3_BASE = str8;
        String str9 = isStaging ? "http://account.preview.n.xiaomi.net/oauth2/" : "https://account.xiaomi.com/oauth2/";
        URL_ACCOUNT_OAUTH_BASE = str9;
        String str10 = isStaging ? "http://api.device.preview.n.xiaomi.net" : "https://api.device.xiaomi.net";
        URL_DEV_BASE = str10;
        URL_MI_ACCOUNT_VIP_INFO = str7 + "/user/vipLevelInfo";
        URL_ACCOUNT_SERVICE_CONFIG = isStaging ? "http://api.micloud.preview.n.xiaomi.net/micAnonymous/mic/account/config" : "https://api.g.micloud.xiaomi.net/micAnonymous/mic/account/config";
        URL_GET_DEVICE_MODEL_INFOS = str10 + "/modelinfos";
        URL_DEV_SETTING = str10 + CloudRequestUtils.URL_DEV_SETTING;
        URL_DEVICES_SETTING = str10 + "/api/user/devices/setting";
        URL_LOGIN_AUTH2 = str2 + "/serviceLoginAuth2";
        URL_LOGIN_AUTH2_HTTPS = str2 + "/serviceLoginAuth2";
        URL_EXCHANGE_PHONETOKE_HTTPS = str3 + "/phoneToken/exchangePhoneToken";
        String str11 = str4 + "/serviceLoginAuth2CA";
        URL_LOGIN_AUTH2_PASSPORT_CA = str11;
        URL_LOGIN_AUTH_STEP2 = str2 + "/loginStep2";
        URL_USER_EXISTS = str8 + "/user@id";
        URL_GET_USER_CORE_INFO = str7 + "/user/coreInfo";
        String str12 = isStaging ? "http://open.account.preview.n.xiaomi.net/third/" : "https://open.account.xiaomi.com/third/";
        URL_OPEN_ACCOUNT_THIRD_BASE = str12;
        URL_REQUEST_UPDATE_ICON = str7 + "/user/updateIconRequest";
        URL_COMMIT_UPDATE_ICON = str7 + "/user/updateIconCommit";
        URL_REG = str6 + "/user/full";
        URL_REG_PHONE = str5 + "/user/full/@phone";
        URL_RESEND_EMAIL = str5 + "/sendActivateMessage";
        URL_REG_GET_VERIFY_CODE = str2 + "/sendPhoneTicket";
        URL_REG_GET_CAPTCHA_CODE = str2 + "/getCode?icodeType=register";
        URL_REG_CHECK_VERIFY_CODE = str2 + "/verifyPhoneRegTicket";
        URL_REG_SEND_PHONE_TICKET = str2 + "/sendPhoneRegTicket";
        URL_REG_VERIFY_PHONE = str2 + "/verifyRegPhone";
        URL_REG_TOKEN = str2 + "/tokenRegister";
        URL_RESET_PASSWORD = str2 + "/auth/resetPassword";
        URL_AUTH2_AUTHORIZE = str9 + "authorize";
        URL_LOGIN = str2 + "/serviceLogin";
        URL_LOGIN_HTTPS = str2 + "/serviceLogin";
        String str13 = str4 + "/serviceLoginCA";
        URL_LOGIN_PASSPORT_CA = str13;
        OPEN_URL_GET_ACCESS_TOKEN = str12 + "getToken";
        OPEN_URL_REFRESH_ACCESS_TOKEN = str12 + "refreshToken";
        URL_ACCOUNT_USER_PROFILE = str7 + "/user/profile";
        URL_CHECK_SAFE_EMAIL_AVAILABILITY = str7 + "/user/checkSafeEmailBindParams";
        URL_SEND_BIND_EMAIL_VERIFY_CODE = str7 + "/user/sendBindSafeEmailVerifyMessage";
        URL_SEND_BIND_PHONE_VERIFY_CODE = str7 + "/user/sendBindAuthPhoneVerifyMessage";
        URL_ADD_BIND_PHONE = str7 + "/user/addPhone";
        URL_REPLACE_BIND_PHONE = str7 + "/user/updatePhone";
        URL_DELETE_BIND_PHONE = str7 + "/user/deletePhone";
        URL_REPLACE_BIND_EMAIL = str7 + "/user/replaceSafeEmailAddress";
        URL_ADD_BIND_EMAIL = str7 + "/user/addSafeEmailAddress";
        URL_SEND_EMAIL_ACTIVATE_MESSAGE = str7 + "/user/sendEmailActivateMessage";
        URL_SET_SECURITY_QUESTIONS = str7 + "/user/setSafeQuestions";
        URL_IDENTITY_AUTH_FOR_ADDING_PHONE = str7 + "/user/addPhoneAuth";
        URL_IDENTITY_AUTH_FOR_REPLACING_PHONE = str7 + "/user/updatePhoneAuth";
        URL_IDENTITY_AUTH_FOR_DELETING_PHONE = str7 + "/user/deletePhoneAuth";
        URL_IDENTITY_AUTH_FOR_REPLACING_EMAIL = str7 + "/user/replaceSafeEmailAddressAuth";
        URL_IDENTITY_AUTH_FOR_ADDING_EMAIL = str7 + "/user/addSafeEmailAddressAuth";
        URL_IDENTITY_AUTH_FOR_SEND_EMAIL_ACTIVATE_MESSAGE = str7 + "/user/sendEmailActivateMessageAuth";
        URL_IDENTITY_AUTH_FOR_SET_SECURITY_QUESTIONS = str7 + "/user/setSafeQuestionsAuth";
        URL_IDENTITY_AUTH_FOR_MODIFY_SAFE_PHONE = str7 + "/user/modifySafePhoneAuth";
        URL_IDENTITY_AUTH_FOR_CHANGE_PWD = str7 + "/user/native/changePasswordAuth";
        URL_CHECK_PHONE_ACTIVATE_STATUS = str7 + "/user/checkPhoneActivateStatus";
        URL_GET_BIND_EMAIL_CAPTCODE = str2 + "/getCode?icodeType=antispam";
        URL_CHANGE_PASSWORD = str7 + "/user/changePassword";
        URL_SET_USER_REGION = str7 + "/user/region";
        URL_SET_USER_LOCATION = str7 + "/user/setLocation";
        URL_SET_USER_EDUCATION = str7 + "/user/setEducation";
        URL_SET_USER_INCOME = str7 + "/user/setIncome";
        URL_GENERATE_RANDOM_PASSWORD = str + "/appConf/randomPwd";
        URL_EMAIL_REGISTER = str2 + "/register";
        URL_ACCOUNT_HELP_CENTER = str + "/helpcenter";
        URL_GET_COUNTRY_CODE = str5 + "/configuration/cc";
        URL_REPORT_VERIFY_PHONE_NOTIFY_STATUS = str3 + "/confirmPhone/redPointClick";
        URL_QUERY_VERIFY_PHONE_INFO = str3 + "/confirmPhone/recyleStatus";
        URL_ONE_STEP_TRANSFER_TOKEN = str + "/onesteptransfer/passtoken/refresh";
        HashMap hashMap = new HashMap();
        caUrlMap = hashMap;
        hashMap.put(URL_LOGIN_HTTPS, str13);
        hashMap.put(URL_LOGIN_AUTH2_HTTPS, str11);
    }
}
