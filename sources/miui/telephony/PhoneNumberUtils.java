package miui.telephony;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import com.android.internal.telephony.PhoneConstants;
import com.miui.internal.telephony.phonenumber.ChineseTelocation;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import miui.os.Build;
import miui.system.R;
import miui.telephony.phonenumber.CountryCode;
import miui.telephony.phonenumber.CountryCodeConverter;
import miui.telephony.phonenumber.Prefix;
import miui.util.AppConstants;

/* loaded from: classes4.dex */
public class PhoneNumberUtils extends android.telephony.PhoneNumberUtils {
    private static final String CHINA_COUNTRY_CODE = "86";
    private static final int CHINA_IOT_MOBILE_NUMBER_LENGTH = 13;
    public static final String CHINA_MCC = "460";
    private static final int CHINA_MOBILE_NUMBER_LENGTH = 11;
    private static final String CHINA_MOBILE_NUMBER_PREFIX = "1";
    private static final String CHINA_REGION_CODE1 = "+86";
    private static final String CHINA_REGION_CODE2 = "0086";
    private static final String[] EMERGENCY_NUMBERS = {"110", "112", "119", "120", "122", "911", "999", "995", "100", "101", "102", "190"};
    static final String LOG_TAG = "PhoneNumberUtils";
    public static final int MASK_PHONE_NUMBER_MODE_HEAD = 0;
    public static final int MASK_PHONE_NUMBER_MODE_MIDDLE = 2;
    public static final int MASK_PHONE_NUMBER_MODE_TAIL = 1;
    private static final int MIN_QUERY_LOCATION_EFFECTIVE_IOT_NUMBER_LENGTH = 9;
    private static final int MIN_QUERY_LOCATION_EFFECTIVE_NUMBER_LENGTH = 7;
    public static final String PAYPHONE_NUMBER = "-3";
    public static final String PRIVATE_NUMBER = "-2";
    public static final String UNKNOWN_NUMBER = "-1";

    /* loaded from: classes4.dex */
    public interface OperatorQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str);
    }

    /* loaded from: classes4.dex */
    public static class PhoneNumber {
        private static final String EMPTY = "";
        private static final char HASH_STRING_INDICATOR = 1;
        private static final int MAX_NUMBER_LENGTH = 256;
        private static final char MISSING_AREA_CODE_INDICATOR = 2;
        private static final int POOL_SIZE = 10;
        private static final PhoneNumber[] sPool = new PhoneNumber[10];
        private static int sPoolIndex = -1;
        private String mAreaCode;
        private StringBuffer mBuffer = new StringBuffer(256);
        private String mCountryCode;
        private String mDefaultCountryCode;
        private String mEffectiveNumber;
        private int mEffectiveNumberStart;
        private boolean mIsChinaEnvironment;
        private String mNetIddCode;
        private CharSequence mOriginal;
        private String mPostDialString;
        private int mPostDialStringStart;
        private String mPrefix;

        private PhoneNumber() {
            clear();
        }

        public static String addCountryCode(String str) {
            if (TextUtils.isEmpty(str)) {
                return str;
            }
            PhoneNumber parse = parse(str);
            boolean z = TextUtils.isEmpty(parse.getCountryCode()) && !PhoneNumberUtils.isEmergencyNumber(str);
            if (z && parse.isChineseNumber()) {
                if (!TextUtils.isEmpty(parse.getPrefix()) || parse.isServiceNumber()) {
                    z = false;
                } else if (!parse.isNormalMobileNumber()) {
                    z = !TextUtils.isEmpty(parse.getAreaCode());
                }
            }
            if ((str.startsWith("*") || str.startsWith("#")) && str.endsWith("#")) {
                z = false;
            }
            if (z) {
                String userDefinedCountryCode = CountryCode.getUserDefinedCountryCode();
                if (TextUtils.isEmpty(userDefinedCountryCode)) {
                    userDefinedCountryCode = CountryCode.getIccCountryCode();
                }
                if (!TextUtils.isEmpty(userDefinedCountryCode)) {
                    if ("39".equals(userDefinedCountryCode) || str.charAt(0) != '0') {
                        str = CountryCode.GSM_GENERAL_IDD_CODE + userDefinedCountryCode + str;
                    } else {
                        str = CountryCode.GSM_GENERAL_IDD_CODE + userDefinedCountryCode + str.substring(1);
                    }
                }
            }
            parse.recycle();
            return str;
        }

        private static boolean areEqual(CharSequence charSequence, int i, CharSequence charSequence2, int i2, int i3) {
            if (charSequence == null || charSequence2 == null || i < 0 || i2 < 0 || i3 < 0 || charSequence.length() < i + i3 || charSequence2.length() < i2 + i3) {
                return false;
            }
            for (int i4 = 0; i4 < i3; i4++) {
                if (charSequence.charAt(i + i4) != charSequence2.charAt(i2 + i4)) {
                    return false;
                }
            }
            return true;
        }

        private void attach(CharSequence charSequence) {
            if (charSequence == null) {
                charSequence = "";
            }
            this.mOriginal = charSequence;
            int length = charSequence.length();
            boolean z = false;
            for (int i = 0; i < length; i++) {
                char charAt = charSequence.charAt(i);
                if (z && android.telephony.PhoneNumberUtils.isNonSeparator(charAt)) {
                    this.mBuffer.append(charAt);
                } else if (i == 0 && charAt == '+') {
                    this.mBuffer.append(charAt);
                } else if (charAt >= '0' && charAt <= '9') {
                    this.mBuffer.append(charAt);
                } else if (!z && android.telephony.PhoneNumberUtils.isStartsPostDial(charAt)) {
                    this.mPostDialStringStart = this.mBuffer.length();
                    this.mBuffer.append(charAt);
                    z = true;
                }
            }
            if (z) {
                return;
            }
            this.mPostDialStringStart = this.mBuffer.length();
        }

        private void clear() {
            this.mBuffer.setLength(0);
            this.mPrefix = null;
            this.mCountryCode = null;
            this.mAreaCode = null;
            this.mEffectiveNumberStart = 0;
            this.mEffectiveNumber = null;
            this.mPostDialStringStart = 0;
            this.mPostDialString = null;
            this.mIsChinaEnvironment = false;
            this.mNetIddCode = null;
        }

        public static String getDefaultCountryCode() {
            return CountryCode.getIccCountryCode();
        }

        public static String getDialableNumber(String str) {
            if (TextUtils.isEmpty(str)) {
                return "";
            }
            int indexOf = str.indexOf(2);
            return str.charAt(0) == 1 ? str.substring(indexOf < 0 ? 1 : indexOf + 1) : str;
        }

        public static String getHashString(String str) {
            String effectiveNumber;
            PhoneNumber parse = parse(str);
            if (parse.isSmsPrefix()) {
                effectiveNumber = parse.getPrefix() + parse.getEffectiveNumber();
            } else {
                effectiveNumber = parse.getEffectiveNumber();
            }
            String format = !parse.isChineseNumber() ? String.format("%c(00%s)%s%s", Character.valueOf(HASH_STRING_INDICATOR), parse.getCountryCode(), effectiveNumber, parse.getPostDialString()) : parse.isNormalMobileNumber() ? String.format("%c(00%s)%s%s", Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, effectiveNumber, parse.getPostDialString()) : !TextUtils.isEmpty(parse.getCountryCode()) ? !TextUtils.isEmpty(parse.getAreaCode()) ? String.format("%c(00%s)%s-%s%s", Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, parse.getAreaCode(), effectiveNumber, parse.getPostDialString()) : String.format("%c(00%s)%s%s", Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, effectiveNumber, parse.getPostDialString()) : !TextUtils.isEmpty(parse.getAreaCode()) ? String.format("%c(00%s)%s-%s%s", Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, parse.getAreaCode(), effectiveNumber, parse.getPostDialString()) : String.format("%c(00%s)%c%s%s", Character.valueOf(HASH_STRING_INDICATOR), PhoneNumberUtils.CHINA_COUNTRY_CODE, Character.valueOf(MISSING_AREA_CODE_INDICATOR), effectiveNumber, parse.getPostDialString());
            parse.recycle();
            return format;
        }

        public static String getLocation(Context context, CharSequence charSequence) {
            PhoneNumber parse = parse(charSequence);
            String location = parse.getLocation(context);
            parse.recycle();
            return location;
        }

        public static String getLocationAreaCode(Context context, String str) {
            PhoneNumber parse = parse(str);
            String locationAreaCode = parse.getLocationAreaCode(context);
            parse.recycle();
            return locationAreaCode;
        }

        public static String getOperator(Context context, CharSequence charSequence) {
            PhoneNumber parse = parse(charSequence);
            String operator = parse.getOperator(context);
            parse.recycle();
            return operator;
        }

        public static boolean isChineseOperator() {
            return CountryCode.isChinaEnvironment();
        }

        public static boolean isValidCountryCode(String str) {
            return CountryCodeConverter.isValidCountryCode(str);
        }

        public static PhoneNumber parse(CharSequence charSequence) {
            return parse(charSequence, CountryCode.isChinaEnvironment(), null);
        }

        public static PhoneNumber parse(CharSequence charSequence, boolean z) {
            return parse(charSequence, z, null);
        }

        public static PhoneNumber parse(CharSequence charSequence, boolean z, String str) {
            PhoneNumber phoneNumber;
            PhoneNumber[] phoneNumberArr = sPool;
            synchronized (phoneNumberArr) {
                int i = sPoolIndex;
                if (i == -1) {
                    phoneNumber = new PhoneNumber();
                } else {
                    PhoneNumber phoneNumber2 = phoneNumberArr[i];
                    sPoolIndex = i - 1;
                    phoneNumberArr[i] = null;
                    phoneNumber = phoneNumber2;
                }
            }
            phoneNumber.attach(charSequence);
            phoneNumber.mIsChinaEnvironment = z;
            phoneNumber.mNetIddCode = str;
            return phoneNumber;
        }

        public static String replaceCdmaInternationalAccessCode(String str) {
            if (str.startsWith(PhoneNumberUtils.CHINA_REGION_CODE1) && PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(CountryCode.getNetworkCountryCode())) {
                String substring = str.substring(3);
                if (PhoneNumberUtils.isChinaMobileNumber(substring) || substring.charAt(0) == '0') {
                    return substring;
                }
                return '0' + substring;
            } else if (TextUtils.isEmpty(str) || str.charAt(0) != '+') {
                return str;
            } else {
                return CountryCode.getIddCodes().get(0) + str.substring(1);
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:17:0x0041  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.String getAreaCode() {
            /*
                r6 = this;
                java.lang.String r0 = r6.mAreaCode
                if (r0 != 0) goto L6f
                java.lang.String r0 = ""
                r6.mAreaCode = r0
                boolean r0 = r6.isChineseNumber()
                if (r0 == 0) goto L6f
                java.lang.String r0 = r6.getPrefix()
                boolean r0 = miui.telephony.phonenumber.Prefix.isSmsPrefix(r0)
                if (r0 != 0) goto L6f
                java.lang.String r0 = r6.getCountryCode()
                boolean r1 = android.text.TextUtils.isEmpty(r0)
                r2 = 1
                if (r1 == 0) goto L3e
                r1 = 0
                java.lang.StringBuffer r3 = r6.mBuffer
                int r3 = r3.length()
                int r3 = r3 - r2
                int r4 = r6.mEffectiveNumberStart
                if (r3 <= r4) goto L3f
                java.lang.StringBuffer r3 = r6.mBuffer
                char r3 = r3.charAt(r4)
                r4 = 48
                if (r3 != r4) goto L3f
                int r1 = r6.mEffectiveNumberStart
                int r1 = r1 + r2
                r6.mEffectiveNumberStart = r1
            L3e:
                r1 = r2
            L3f:
                if (r1 == 0) goto L6f
                com.miui.internal.telephony.phonenumber.ChineseTelocation r1 = com.miui.internal.telephony.phonenumber.ChineseTelocation.getInstance()
                java.lang.StringBuffer r3 = r6.mBuffer
                int r4 = r6.mEffectiveNumberStart
                int r5 = r6.mPostDialStringStart
                int r5 = r5 - r4
                java.lang.String r1 = r1.parseAreaCode(r3, r4, r5)
                r6.mAreaCode = r1
                int r1 = r1.length()
                if (r1 != 0) goto L64
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 == 0) goto L64
                int r0 = r6.mEffectiveNumberStart
                int r0 = r0 - r2
                r6.mEffectiveNumberStart = r0
                goto L6f
            L64:
                int r0 = r6.mEffectiveNumberStart
                java.lang.String r1 = r6.mAreaCode
                int r1 = r1.length()
                int r0 = r0 + r1
                r6.mEffectiveNumberStart = r0
            L6f:
                java.lang.String r6 = r6.mAreaCode
                return r6
            */
            throw new UnsupportedOperationException("Method not decompiled: miui.telephony.PhoneNumberUtils.PhoneNumber.getAreaCode():java.lang.String");
        }

        public String getCountryCode() {
            if (this.mCountryCode == null) {
                getPrefix();
                Iterator<String> it = CountryCode.getIddCodes().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    String next = it.next();
                    String str = CountryCode.GSM_GENERAL_IDD_CODE;
                    if (!areEqual(this.mBuffer, this.mEffectiveNumberStart, CountryCode.GSM_GENERAL_IDD_CODE, 0, 1)) {
                        if (!TextUtils.isEmpty(this.mNetIddCode)) {
                            next = this.mNetIddCode;
                        }
                        str = !areEqual(this.mBuffer, this.mEffectiveNumberStart, next, 0, next.length()) ? null : next;
                    }
                    if (str != null) {
                        int length = this.mEffectiveNumberStart + str.length();
                        this.mEffectiveNumberStart = length;
                        String parse = CountryCodeConverter.parse(this.mBuffer, length, this.mPostDialStringStart - length);
                        this.mCountryCode = parse;
                        if (parse.length() != 0) {
                            this.mEffectiveNumberStart += this.mCountryCode.length();
                            break;
                        }
                        this.mEffectiveNumberStart -= str.length();
                    } else {
                        this.mCountryCode = "";
                    }
                }
            }
            return this.mCountryCode;
        }

        public String getEffectiveNumber() {
            if (this.mEffectiveNumber == null) {
                getAreaCode();
                int length = this.mBuffer.length();
                int i = this.mEffectiveNumberStart;
                if (length > i) {
                    this.mEffectiveNumber = this.mBuffer.substring(i, this.mPostDialStringStart);
                } else {
                    this.mEffectiveNumber = "";
                }
            }
            if (TextUtils.isEmpty(this.mEffectiveNumber)) {
                String charSequence = this.mOriginal.toString();
                this.mOriginal = charSequence;
                return charSequence.toString();
            }
            return this.mEffectiveNumber;
        }

        public String getFakeNumberToQueryLocation() {
            String effectiveNumber = getEffectiveNumber();
            if (TextUtils.isEmpty(getAreaCode()) && effectiveNumber.startsWith("1")) {
                int length = effectiveNumber.length();
                int i = 7;
                int i2 = 11;
                if (effectiveNumber.startsWith("141") || effectiveNumber.startsWith("1064")) {
                    i = 9;
                    i2 = 13;
                }
                if (length < i || length >= i2) {
                    return this.mOriginal.toString();
                }
                StringBuilder sb = new StringBuilder(this.mOriginal);
                while (length < i2) {
                    sb.append('9');
                    length++;
                }
                return sb.toString();
            }
            return this.mOriginal.toString();
        }

        public String getLocation(Context context) {
            if (Build.checkRegion("tw")) {
                return "";
            }
            Locale locale = context.getResources().getConfiguration().locale;
            if (locale.getLanguage().equals(Locale.CHINA.getLanguage()) && isChineseNumber()) {
                ChineseTelocation chineseTelocation = ChineseTelocation.getInstance();
                StringBuffer stringBuffer = this.mBuffer;
                int i = this.mEffectiveNumberStart;
                return chineseTelocation.getLocation(context, stringBuffer, i, this.mPostDialStringStart - i, isNormalMobileNumber() || getAreaCode().length() > 0);
            }
            return ChineseTelocation.getInstance().getExternalLocation(context, getCountryCode(), this.mOriginal, locale);
        }

        public String getLocationAreaCode(Context context) {
            if (isChineseNumber()) {
                if (isNormalMobileNumber()) {
                    ChineseTelocation chineseTelocation = ChineseTelocation.getInstance();
                    StringBuffer stringBuffer = this.mBuffer;
                    int i = this.mEffectiveNumberStart;
                    return chineseTelocation.getAreaCode(stringBuffer, i, this.mPostDialStringStart - i);
                }
                return getAreaCode();
            }
            return "";
        }

        public String getNormalizedNumber(boolean z, boolean z2) {
            if (!isChineseNumber()) {
                int i = this.mEffectiveNumberStart;
                if (z) {
                    i -= getCountryCode().length();
                }
                String substring = this.mBuffer.substring(i, z2 ? this.mBuffer.length() : this.mPostDialStringStart);
                if (!z || getCountryCode().length() <= 0) {
                    return substring;
                }
                return CountryCode.GSM_GENERAL_IDD_CODE + substring;
            } else if (isNormalMobileNumber()) {
                String substring2 = this.mBuffer.substring(this.mEffectiveNumberStart, z2 ? this.mBuffer.length() : this.mPostDialStringStart);
                if (z) {
                    substring2 = PhoneNumberUtils.CHINA_REGION_CODE1 + substring2;
                }
                return substring2;
            } else {
                int length = z2 ? this.mBuffer.length() : this.mPostDialStringStart;
                if (TextUtils.isEmpty(getAreaCode()) || isServiceNumber()) {
                    return this.mBuffer.substring(this.mEffectiveNumberStart, length);
                }
                String substring3 = this.mBuffer.substring(this.mEffectiveNumberStart - getAreaCode().length(), length);
                if (z) {
                    return PhoneNumberUtils.CHINA_REGION_CODE1 + substring3;
                }
                return "0" + substring3;
            }
        }

        public String getNumberWithoutPrefix(boolean z) {
            int length = !TextUtils.isEmpty(getPrefix()) ? getPrefix().length() : 0;
            return z ? this.mBuffer.substring(length) : this.mBuffer.substring(length, this.mPostDialStringStart);
        }

        public String getOperator(Context context) {
            if (!Build.checkRegion("tw") && context.getResources().getConfiguration().locale.getLanguage().equals(Locale.CHINA.getLanguage()) && isChineseNumber()) {
                ChineseTelocation chineseTelocation = ChineseTelocation.getInstance();
                String stringBuffer = this.mBuffer.toString();
                int i = this.mEffectiveNumberStart;
                return chineseTelocation.getOperator(context, stringBuffer, i, this.mPostDialStringStart - i, isNormalMobileNumber());
            }
            return "";
        }

        public String getPostDialString() {
            if (this.mPostDialString == null) {
                int length = this.mBuffer.length();
                int i = this.mPostDialStringStart;
                if (length > i) {
                    this.mPostDialString = this.mBuffer.substring(i);
                } else {
                    this.mPostDialString = "";
                }
            }
            return this.mPostDialString;
        }

        public String getPrefix() {
            if (this.mPrefix == null && this.mIsChinaEnvironment) {
                StringBuffer stringBuffer = this.mBuffer;
                int i = this.mEffectiveNumberStart;
                String parse = Prefix.parse(stringBuffer, i, this.mPostDialStringStart - i);
                this.mPrefix = parse;
                this.mEffectiveNumberStart += parse.length();
            }
            return this.mPrefix;
        }

        public boolean isChineseNumber() {
            String countryCode = getCountryCode();
            return !TextUtils.isEmpty(countryCode) ? PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(countryCode) : this.mIsChinaEnvironment || PhoneNumberUtils.CHINA_COUNTRY_CODE.equals(this.mDefaultCountryCode);
        }

        public boolean isNormalMobileNumber() {
            getAreaCode();
            if (isChineseNumber()) {
                int i = this.mPostDialStringStart;
                int i2 = this.mEffectiveNumberStart;
                int i3 = i - i2;
                if (i3 != 11) {
                    if (i3 == 13 && this.mBuffer.charAt(i2) == '1') {
                        char charAt = this.mBuffer.charAt(this.mEffectiveNumberStart + 1);
                        return charAt != '0' ? charAt == '4' && this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '1' : this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '6' && this.mBuffer.charAt(this.mEffectiveNumberStart + 3) == '4';
                    }
                    return false;
                } else if (this.mBuffer.charAt(i2) == '1') {
                    switch (this.mBuffer.charAt(this.mEffectiveNumberStart + 1)) {
                        case '3':
                            return (this.mBuffer.charAt(this.mEffectiveNumberStart + 2) == '8' && this.mBuffer.charAt(this.mEffectiveNumberStart + 3) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 4) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 5) == '1' && this.mBuffer.charAt(this.mEffectiveNumberStart + 6) == '3' && this.mBuffer.charAt(this.mEffectiveNumberStart + 7) == '8' && this.mBuffer.charAt(this.mEffectiveNumberStart + 8) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 9) == '0' && this.mBuffer.charAt(this.mEffectiveNumberStart + 10) == '0') ? false : true;
                        case '4':
                        case '5':
                        case '6':
                        case '8':
                        case '9':
                            return true;
                        case '7':
                            return this.mBuffer.charAt(this.mEffectiveNumberStart + 2) != '9';
                        default:
                            return false;
                    }
                } else {
                    return false;
                }
            }
            return false;
        }

        public boolean isServiceNumber() {
            getAreaCode();
            if (isChineseNumber()) {
                int i = this.mPostDialStringStart;
                int i2 = this.mEffectiveNumberStart;
                int i3 = i - i2;
                if (i3 > 2) {
                    char charAt = this.mBuffer.charAt(i2);
                    char charAt2 = this.mBuffer.charAt(this.mEffectiveNumberStart + 1);
                    char charAt3 = this.mBuffer.charAt(this.mEffectiveNumberStart + 2);
                    if (charAt == '1' && (charAt2 == '0' || charAt2 == '1' || charAt2 == '2')) {
                        return true;
                    }
                    if (charAt == '2' || charAt == '3' || charAt == '5' || charAt == '6' || charAt == '7') {
                        return charAt2 == '0' && charAt3 == '0' && i3 > 7;
                    } else if (i3 != 5) {
                        return i3 != 10 ? i3 == 11 && "13800138000".equals(getEffectiveNumber().replaceAll(" ", "")) : (charAt == '4' || charAt == '8') && this.mEffectiveNumberStart == 0 && charAt2 == '0' && charAt3 == '0';
                    } else if (charAt == '9') {
                        return charAt2 == '5' || charAt2 == '6';
                    } else {
                        return false;
                    }
                }
                return false;
            }
            return false;
        }

        public boolean isSmsPrefix() {
            return Prefix.isSmsPrefix(getPrefix());
        }

        public void recycle() {
            clear();
            PhoneNumber[] phoneNumberArr = sPool;
            synchronized (phoneNumberArr) {
                int i = sPoolIndex;
                if (i < phoneNumberArr.length) {
                    int i2 = i + 1;
                    sPoolIndex = i2;
                    phoneNumberArr[i2] = this;
                }
            }
        }

        public void setDefaultCountryCode(String str) {
            this.mDefaultCountryCode = str;
        }
    }

    /* loaded from: classes4.dex */
    public interface TelocationAndOperatorQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str, String str2);
    }

    /* loaded from: classes4.dex */
    private static class TelocationAsyncQueryHandler extends AsyncQueryHandler {
        private static final int EVENT_QUERY_OPERATOR = 20;
        private static final int EVENT_QUERY_TELOCATION = 10;
        private static final int EVENT_QUERY_TELOCATION_AND_OPERATOR = 30;
        private Handler mWorkerHandler;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class SingletonHolder {
            private static final TelocationAsyncQueryHandler INSTANCE = new TelocationAsyncQueryHandler();

            private SingletonHolder() {
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* loaded from: classes4.dex */
        public static final class TelocationWorkerArgs {
            public Context context;
            public Object cookie1;
            public Object cookie2;
            public Object cookie3;
            public Object cookie4;
            public Handler handler;
            public String location;
            public String operator;
            public OperatorQueryListener operatorQueryListener;
            public String phoneNumber;
            public TelocationAndOperatorQueryListener telocationAndOperatorQueryListener;
            public TelocationQueryListener telocationQueryListener;

            protected TelocationWorkerArgs() {
            }
        }

        /* loaded from: classes4.dex */
        protected class TelocationWorkerHandler extends AsyncQueryHandler.WorkerHandler {
            public TelocationWorkerHandler(Looper looper) {
                super(TelocationAsyncQueryHandler.this, looper);
            }

            @Override // android.content.AsyncQueryHandler.WorkerHandler, android.os.Handler
            public void handleMessage(Message message) {
                TelocationWorkerArgs telocationWorkerArgs = (TelocationWorkerArgs) message.obj;
                int i = message.arg1;
                if (i == 10 || i == 30) {
                    telocationWorkerArgs.location = TelocationAsyncQueryHandler.queryTelocation(telocationWorkerArgs.context, telocationWorkerArgs.phoneNumber);
                }
                int i2 = message.arg1;
                if (i2 == 20 || i2 == 30) {
                    telocationWorkerArgs.operator = TelocationAsyncQueryHandler.queryOperator(telocationWorkerArgs.context, telocationWorkerArgs.phoneNumber);
                }
                Message obtainMessage = telocationWorkerArgs.handler.obtainMessage(message.what);
                obtainMessage.arg1 = message.arg1;
                obtainMessage.obj = message.obj;
                obtainMessage.sendToTarget();
            }
        }

        private TelocationAsyncQueryHandler() {
            super(null);
        }

        public static TelocationAsyncQueryHandler getInstance() {
            return SingletonHolder.INSTANCE;
        }

        public static String queryOperator(Context context, CharSequence charSequence) {
            return PhoneNumber.getOperator(context, charSequence);
        }

        public static String queryTelocation(Context context, CharSequence charSequence) {
            return PhoneNumber.getLocation(context, charSequence);
        }

        private void sendMsg(TelocationWorkerArgs telocationWorkerArgs, int i, int i2, Object obj, Object obj2, Object obj3, Object obj4, Context context, String str) {
            telocationWorkerArgs.handler = this;
            telocationWorkerArgs.context = context;
            telocationWorkerArgs.phoneNumber = str;
            telocationWorkerArgs.cookie1 = obj;
            telocationWorkerArgs.cookie2 = obj2;
            telocationWorkerArgs.cookie3 = obj3;
            telocationWorkerArgs.cookie4 = obj4;
            telocationWorkerArgs.location = null;
            Message obtainMessage = this.mWorkerHandler.obtainMessage(i2);
            obtainMessage.arg1 = i;
            obtainMessage.obj = telocationWorkerArgs;
            obtainMessage.sendToTarget();
        }

        @Override // android.content.AsyncQueryHandler
        protected Handler createHandler(Looper looper) {
            if (this.mWorkerHandler == null) {
                this.mWorkerHandler = new TelocationWorkerHandler(looper);
            }
            return this.mWorkerHandler;
        }

        @Override // android.content.AsyncQueryHandler, android.os.Handler
        public void handleMessage(Message message) {
            TelocationAndOperatorQueryListener telocationAndOperatorQueryListener;
            OperatorQueryListener operatorQueryListener;
            TelocationQueryListener telocationQueryListener;
            TelocationWorkerArgs telocationWorkerArgs = (TelocationWorkerArgs) message.obj;
            int i = message.arg1;
            if (i == 10 && (telocationQueryListener = telocationWorkerArgs.telocationQueryListener) != null) {
                telocationQueryListener.onComplete(telocationWorkerArgs.cookie1, telocationWorkerArgs.cookie2, telocationWorkerArgs.cookie3, telocationWorkerArgs.cookie4, telocationWorkerArgs.location);
            } else if (i == 20 && (operatorQueryListener = telocationWorkerArgs.operatorQueryListener) != null) {
                operatorQueryListener.onComplete(telocationWorkerArgs.cookie1, telocationWorkerArgs.cookie2, telocationWorkerArgs.cookie3, telocationWorkerArgs.cookie4, telocationWorkerArgs.operator);
            } else if (i != 30 || (telocationAndOperatorQueryListener = telocationWorkerArgs.telocationAndOperatorQueryListener) == null) {
            } else {
                telocationAndOperatorQueryListener.onComplete(telocationWorkerArgs.cookie1, telocationWorkerArgs.cookie2, telocationWorkerArgs.cookie3, telocationWorkerArgs.cookie4, telocationWorkerArgs.location, telocationWorkerArgs.operator);
            }
        }

        public void startQueryOperatorString(int i, Object obj, Object obj2, Object obj3, Object obj4, OperatorQueryListener operatorQueryListener, Context context, String str) {
            TelocationWorkerArgs telocationWorkerArgs = new TelocationWorkerArgs();
            telocationWorkerArgs.operatorQueryListener = operatorQueryListener;
            sendMsg(telocationWorkerArgs, 20, i, obj, obj2, obj3, obj4, context, str);
        }

        public void startQueryTelocationAndOperatorString(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationAndOperatorQueryListener telocationAndOperatorQueryListener, Context context, String str) {
            TelocationWorkerArgs telocationWorkerArgs = new TelocationWorkerArgs();
            telocationWorkerArgs.telocationAndOperatorQueryListener = telocationAndOperatorQueryListener;
            sendMsg(telocationWorkerArgs, 30, i, obj, obj2, obj3, obj4, context, str);
        }

        public void startQueryTelocationString(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationQueryListener telocationQueryListener, Context context, String str) {
            TelocationWorkerArgs telocationWorkerArgs = new TelocationWorkerArgs();
            telocationWorkerArgs.telocationQueryListener = telocationQueryListener;
            sendMsg(telocationWorkerArgs, 10, i, obj, obj2, obj3, obj4, context, str);
        }
    }

    /* loaded from: classes4.dex */
    public interface TelocationQueryListener {
        void onComplete(Object obj, Object obj2, Object obj3, Object obj4, String str);
    }

    public static void cancelAsyncTelocationQuery(int i) {
        TelocationAsyncQueryHandler.getInstance().cancelOperation(i);
    }

    public static String extractNetworkPortion(String str) {
        return extractNetworkPortion(str, 0);
    }

    public static String extractNetworkPortion(String str, int i) {
        if (str == null) {
            return null;
        }
        return (i == 3 || invokeIsUriNumber(str)) ? str.substring(0, indexOfLastNetworkChar(str) + 1).trim() : android.telephony.PhoneNumberUtils.extractNetworkPortion(str);
    }

    public static String extractNetworkPortionAlt(String str) {
        return extractNetworkPortionAlt(str, 0);
    }

    public static String extractNetworkPortionAlt(String str, int i) {
        if (str == null) {
            return null;
        }
        return (i == 3 || invokeIsUriNumber(str)) ? str.substring(0, indexOfLastNetworkChar(str) + 1).trim() : invokeExtractNetworkPortionAlt(str);
    }

    public static String getDefaultIpBySim(Context context) {
        return getDefaultIpBySim(context, SubscriptionManager.getDefault().getDefaultSlotId());
    }

    public static String getDefaultIpBySim(Context context, int i) {
        TelephonyManager telephonyManager = TelephonyManager.getDefault();
        String simOperatorForSlot = telephonyManager.getSimOperatorForSlot(i);
        return telephonyManager.isSameOperator(simOperatorForSlot, TelephonyManager.OPERATOR_NUMERIC_CHINA_MOBILE) ? Prefix.PREFIX_17951 : telephonyManager.isSameOperator(simOperatorForSlot, TelephonyManager.OPERATOR_NUMERIC_CHINA_UNICOM) ? Prefix.PREFIX_17911 : telephonyManager.isSameOperator(simOperatorForSlot, TelephonyManager.OPERATOR_NUMERIC_CHINA_TELECOM) ? Prefix.PREFIX_17901 : "";
    }

    public static int getPresentation(CharSequence charSequence) {
        return (TextUtils.isEmpty(charSequence) || TextUtils.equals(charSequence, "-1")) ? PhoneConstants.PRESENTATION_UNKNOWN : TextUtils.equals(charSequence, PRIVATE_NUMBER) ? PhoneConstants.PRESENTATION_RESTRICTED : TextUtils.equals(charSequence, PAYPHONE_NUMBER) ? PhoneConstants.PRESENTATION_PAYPHONE : PhoneConstants.PRESENTATION_ALLOWED;
    }

    public static String getPresentationString(int i) {
        return i == PhoneConstants.PRESENTATION_RESTRICTED ? Resources.getSystem().getString(R.string.presentation_private) : i == PhoneConstants.PRESENTATION_PAYPHONE ? Resources.getSystem().getString(R.string.presentation_payphone) : i == PhoneConstants.PRESENTATION_UNKNOWN ? Resources.getSystem().getString(R.string.presentation_unknown) : "";
    }

    private static int indexOfLastNetworkChar(String str) {
        int length = str.length();
        int minPositive = minPositive(str.indexOf(44), str.indexOf(59));
        return minPositive < 0 ? length - 1 : minPositive - 1;
    }

    private static String invokeExtractNetworkPortionAlt(String str) {
        try {
            Method declaredMethod = android.telephony.PhoneNumberUtils.class.getDeclaredMethod("extractNetworkPortionAlt", String.class);
            declaredMethod.setAccessible(true);
            return (String) declaredMethod.invoke(null, str);
        } catch (Exception e) {
            Log.w(LOG_TAG, "invoke extractNetworkPortionAlt failed", e);
            return null;
        }
    }

    private static boolean invokeIsLocalEmergencyNumber(Context context, String str) {
        try {
            Method declaredMethod = Class.forName("miui.telephony.TelephonyManagerEx").getDeclaredMethod("isLocalEmergencyNumber", Context.class, String.class);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(str, context, str)).booleanValue();
        } catch (Exception e) {
            Log.w(LOG_TAG, "invokeIsLocalEmergencyNumber failed", e);
            return false;
        }
    }

    private static boolean invokeIsUriNumber(String str) {
        try {
            Method declaredMethod = android.telephony.PhoneNumberUtils.class.getDeclaredMethod("isUriNumber", String.class);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, str)).booleanValue();
        } catch (Exception e) {
            Log.w(LOG_TAG, "invoke isUriNumber failed", e);
            return false;
        }
    }

    private static boolean isAlnum(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public static boolean isChinaMobileNumber(String str) {
        if (!TextUtils.isEmpty(str) && str.length() >= 11) {
            String stripSeparators = android.telephony.PhoneNumberUtils.stripSeparators(str);
            if (stripSeparators.length() >= 13) {
                return stripSeparators.substring((stripSeparators.length() - 11) - 2).startsWith("861");
            }
            if (stripSeparators.length() >= 11) {
                return stripSeparators.substring(stripSeparators.length() - 11).startsWith("1");
            }
        }
        return false;
    }

    public static boolean isChineseOperator(String str) {
        return !TextUtils.isEmpty(str) && str.startsWith("460");
    }

    public static boolean isDialable(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!android.telephony.PhoneNumberUtils.isDialable(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmergencyNumber(String str) {
        return (Build.VERSION.SDK_INT < 21 || !miui.os.Build.IS_MIUI) ? isMiuiEmergencyNumber(str, true) || android.telephony.PhoneNumberUtils.isEmergencyNumber(str) : invokeIsLocalEmergencyNumber(AppConstants.getCurrentApplication(), str);
    }

    @Deprecated
    public static boolean isMiuiEmergencyNumber(String str, boolean z) {
        if (str == null) {
            return false;
        }
        for (String str2 : EMERGENCY_NUMBERS) {
            if (z) {
                if (str2.equals(str)) {
                    return true;
                }
            } else if (str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isServiceNumber(String str) {
        PhoneNumber parse = PhoneNumber.parse(str);
        return parse != null && parse.isServiceNumber();
    }

    public static String maskPhoneNumber(String str, int i) {
        int i2;
        if (str == null) {
            return "";
        }
        int i3 = 0;
        for (int i4 = 0; i4 < str.length(); i4++) {
            if (isAlnum(str.charAt(i4))) {
                i3++;
            }
        }
        if (i3 < 7) {
            return new String(str);
        }
        int i5 = i3 < 11 ? 2 : 3;
        if (i == 0) {
            i2 = 0;
        } else if (i == 1) {
            i2 = i3 - i5;
        } else if (i != 2) {
            throw new IllegalArgumentException("Invalid cut mode");
        } else {
            i2 = (i3 - i5) / 2;
        }
        StringBuilder sb = new StringBuilder();
        int i6 = i5;
        int i7 = 0;
        for (int i8 = 0; i8 < str.length(); i8++) {
            if (isAlnum(str.charAt(i8))) {
                if (i7 < i2 || i6 <= 0) {
                    sb.append(str.charAt(i8));
                } else {
                    sb.append('?');
                    i6--;
                }
                i7++;
            } else {
                sb.append(str.charAt(i8));
            }
        }
        return sb.toString();
    }

    private static int minPositive(int i, int i2) {
        if (i >= 0 && i2 >= 0) {
            return i < i2 ? i : i2;
        } else if (i >= 0) {
            return i;
        } else {
            if (i2 >= 0) {
                return i2;
            }
            return -1;
        }
    }

    public static String miuiFormatNumber(String str, String str2, String str3) {
        PhoneNumber parse;
        if (Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE) && (parse = PhoneNumber.parse(str)) != null) {
            String prefix = parse.getPrefix();
            if (!TextUtils.isEmpty(prefix) && str.startsWith(prefix)) {
                return prefix + " " + android.telephony.PhoneNumberUtils.formatNumber(str.substring(prefix.length()), str2, str3);
            }
        }
        return android.telephony.PhoneNumberUtils.formatNumber(str, str2, str3);
    }

    public static String parseNumber(String str) {
        PhoneNumber parse;
        return (TelephonyManager.getDefault().getSimState() != 5 || (parse = PhoneNumber.parse(str)) == null) ? str : parse.getEffectiveNumber();
    }

    public static String parseTelocationString(Context context, CharSequence charSequence) {
        return TelocationAsyncQueryHandler.queryTelocation(context, charSequence);
    }

    public static void queryOperatorStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, OperatorQueryListener operatorQueryListener, Context context, String str) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryOperatorString(i, obj, obj2, obj3, obj4, operatorQueryListener, context, str);
        } else {
            operatorQueryListener.onComplete(obj, obj2, obj3, obj4, null);
        }
    }

    public static void queryOperatorStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, OperatorQueryListener operatorQueryListener, Context context, String str, boolean z) {
        if (z) {
            TelocationAsyncQueryHandler.getInstance().startQueryOperatorString(i, obj, obj2, obj3, obj4, operatorQueryListener, context, str);
        } else {
            operatorQueryListener.onComplete(obj, obj2, obj3, obj4, null);
        }
    }

    public static void queryTelocationAndOperatorStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationAndOperatorQueryListener telocationAndOperatorQueryListener, Context context, String str) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationAndOperatorString(i, obj, obj2, obj3, obj4, telocationAndOperatorQueryListener, context, str);
        } else {
            telocationAndOperatorQueryListener.onComplete(obj, obj2, obj3, obj4, null, null);
        }
    }

    public static void queryTelocationAndOperatorStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationAndOperatorQueryListener telocationAndOperatorQueryListener, Context context, String str, boolean z) {
        if (z) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationAndOperatorString(i, obj, obj2, obj3, obj4, telocationAndOperatorQueryListener, context, str);
        } else {
            telocationAndOperatorQueryListener.onComplete(obj, obj2, obj3, obj4, null, null);
        }
    }

    public static void queryTelocationStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationQueryListener telocationQueryListener, Context context, String str) {
        if (ChineseTelocation.isTelocationEnable(context.getContentResolver())) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationString(i, obj, obj2, obj3, obj4, telocationQueryListener, context, str);
        } else {
            telocationQueryListener.onComplete(obj, obj2, obj3, obj4, null);
        }
    }

    public static void queryTelocationStringAsync(int i, Object obj, Object obj2, Object obj3, Object obj4, TelocationQueryListener telocationQueryListener, Context context, String str, boolean z) {
        if (z) {
            TelocationAsyncQueryHandler.getInstance().startQueryTelocationString(i, obj, obj2, obj3, obj4, telocationQueryListener, context, str);
        } else {
            telocationQueryListener.onComplete(obj, obj2, obj3, obj4, null);
        }
    }

    public static String removeDashesAndBlanks(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt != ' ' && charAt != '-') {
                sb.append(charAt);
            }
        }
        return sb.toString();
    }

    public static String[] splitNetworkAndPostDialPortion(String str) {
        if (str == null) {
            return null;
        }
        int indexOfLastNetworkChar = indexOfLastNetworkChar(str) + 1;
        String[] strArr = new String[2];
        strArr[0] = str.substring(0, indexOfLastNetworkChar);
        strArr[1] = indexOfLastNetworkChar == str.length() ? "" : str.substring(indexOfLastNetworkChar);
        return strArr;
    }

    public static String stripSeparatorsAndCountryCode(String str) {
        String stripSeparators = android.telephony.PhoneNumberUtils.stripSeparators(str);
        return stripSeparators != null ? stripSeparators.startsWith(CHINA_REGION_CODE1) ? stripSeparators.substring(3) : stripSeparators.startsWith(CHINA_REGION_CODE2) ? stripSeparators.substring(4) : stripSeparators : stripSeparators;
    }

    public static String toLogSafePhoneNumber(String str) {
        return toLogSafePhoneNumber(str, 0);
    }

    public static String toLogSafePhoneNumber(String str, int i) {
        int length = str == null ? 0 : str.length();
        if (length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(length);
        int i2 = length > i ? length - i : length;
        for (int i3 = 0; i3 < length; i3++) {
            char charAt = str.charAt(i3);
            if (i3 >= i2 || charAt == '-' || charAt == '@' || charAt == '.') {
                sb.append(charAt);
            } else {
                sb.append('x');
            }
        }
        return sb.toString();
    }
}
