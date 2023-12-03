package miui.upnp.typedef.datatype;

import android.util.Log;
import miui.upnp.typedef.exception.InvalidDataTypeException;

/* loaded from: classes4.dex */
public enum DataType {
    UNKNOWN,
    BIN_BASE64,
    BIN_HEX,
    BOOLEAN,
    CHAR,
    DATE,
    DATETIME,
    DATETIME_TZ,
    FIXED_14_4,
    FLOAT,
    I1,
    I2,
    I4,
    INT,
    NUMBER,
    R4,
    R8,
    STRING,
    TIME,
    TIME_TZ,
    UI1,
    UI2,
    UI4,
    URI,
    UUID;

    private static final String TAG = DataType.class.getSimpleName();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: miui.upnp.typedef.datatype.DataType$1  reason: invalid class name */
    /* loaded from: classes4.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$datatype$DataType;

        static {
            int[] iArr = new int[DataType.values().length];
            $SwitchMap$miui$upnp$typedef$datatype$DataType = iArr;
            try {
                iArr[DataType.BIN_BASE64.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.BIN_HEX.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.STRING.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.TIME.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.TIME_TZ.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.DATE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.DATETIME.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.DATETIME_TZ.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.FIXED_14_4.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.URI.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.UUID.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.CHAR.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.I1.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.I2.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.INT.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.NUMBER.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.UI1.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.UI2.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.I4.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.UI4.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.FLOAT.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.R4.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.R8.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$datatype$DataType[DataType.BOOLEAN.ordinal()] = 24;
            } catch (NoSuchFieldError unused24) {
            }
        }
    }

    public static Boolean BooleanValueOf(String str) {
        if (str == null) {
            return Boolean.FALSE;
        }
        String upperCase = str.toUpperCase();
        if (upperCase.equals("1") || upperCase.equals("YES") || upperCase.equals("TRUE")) {
            return Boolean.TRUE;
        }
        if (upperCase.equals("0") || upperCase.equals("NO") || upperCase.equals("FALSE")) {
            return Boolean.FALSE;
        }
        Log.e(TAG, "invalid value: " + str);
        return Boolean.FALSE;
    }

    public static String BooleanValueToString(boolean z) {
        return z ? "1" : "0";
    }

    public static DataType create(String str) throws InvalidDataTypeException {
        if (str.equals("bin.base64")) {
            return BIN_BASE64;
        }
        if (str.equals("bin.hex")) {
            return BIN_HEX;
        }
        if (str.equals("string")) {
            return STRING;
        }
        if (str.equals("time")) {
            return TIME;
        }
        if (str.equals("time.tz")) {
            return TIME_TZ;
        }
        if (str.equals("date")) {
            return DATE;
        }
        if (str.equals("dateTime")) {
            return DATETIME;
        }
        if (str.equals("dateTime.tz")) {
            return DATETIME_TZ;
        }
        if (str.equals("fixed.14.4")) {
            return FIXED_14_4;
        }
        if (str.equals("uri")) {
            return URI;
        }
        if (str.equals("uuid")) {
            return UUID;
        }
        if (str.equals("i1")) {
            return I1;
        }
        if (str.equals("i2")) {
            return I2;
        }
        if (str.equals("int")) {
            return INT;
        }
        if (str.equals("number")) {
            return NUMBER;
        }
        if (str.equals("ui1")) {
            return UI1;
        }
        if (str.equals("ui2")) {
            return UI2;
        }
        if (str.equals("i4")) {
            return I4;
        }
        if (str.equals("ui4")) {
            return UI4;
        }
        if (str.equals("float")) {
            return FLOAT;
        }
        if (str.equals("r4")) {
            return R4;
        }
        if (str.equals("r8")) {
            return R8;
        }
        if (str.equals("char")) {
            return CHAR;
        }
        if (str.equals("boolean")) {
            return BOOLEAN;
        }
        throw new InvalidDataTypeException(str);
    }

    public Object createObjectValue() {
        Class<?> javaDataType = getJavaDataType();
        if (javaDataType == String.class) {
            return "";
        }
        try {
            if (javaDataType == Integer.class) {
                return 0;
            }
            if (javaDataType == Long.class) {
                return 0L;
            }
            if (javaDataType == Float.class) {
                return Float.valueOf(0.0f);
            }
            if (javaDataType == Double.class) {
                return Double.valueOf(0.0d);
            }
            if (javaDataType == Boolean.class) {
                return Boolean.FALSE;
            }
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getJavaDataType() {
        switch (AnonymousClass1.$SwitchMap$miui$upnp$typedef$datatype$DataType[ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return String.class;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
                return Integer.class;
            case 20:
                return Long.class;
            case 21:
            case 22:
                return Float.class;
            case 23:
                return Double.class;
            case 24:
                return Boolean.class;
            default:
                return null;
        }
    }

    public String getStringType() {
        switch (AnonymousClass1.$SwitchMap$miui$upnp$typedef$datatype$DataType[ordinal()]) {
            case 1:
                return "bin.base64";
            case 2:
                return "bin.hex";
            case 3:
                return "string";
            case 4:
                return "time";
            case 5:
                return "time.tz";
            case 6:
                return "date";
            case 7:
                return "dateTime";
            case 8:
                return "dateTime.tz";
            case 9:
                return "fixed.14.4";
            case 10:
                return "uri";
            case 11:
                return "uuid";
            case 12:
                return "char";
            case 13:
                return "i1";
            case 14:
                return "i2";
            case 15:
                return "int";
            case 16:
                return "number";
            case 17:
                return "ui1";
            case 18:
                return "ui2";
            case 19:
                return "i4";
            case 20:
                return "ui4";
            case 21:
                return "float";
            case 22:
                return "r4";
            case 23:
                return "r8";
            case 24:
                return "boolean";
            default:
                return null;
        }
    }

    public Object toObjectValue(String str) {
        Class<?> javaDataType = getJavaDataType();
        if (javaDataType == String.class) {
            return str;
        }
        try {
            if (javaDataType == Integer.class) {
                return Integer.valueOf(str);
            }
            if (javaDataType == Long.class) {
                return Long.valueOf(str);
            }
            if (javaDataType == Float.class) {
                return Float.valueOf(str);
            }
            if (javaDataType == Double.class) {
                return Double.valueOf(str);
            }
            if (javaDataType == Boolean.class) {
                return BooleanValueOf(str);
            }
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String toStringValue(Object obj) {
        Class<?> javaDataType = getJavaDataType();
        if (javaDataType != String.class && javaDataType != Integer.class && javaDataType != Long.class && javaDataType != Float.class && javaDataType != Double.class) {
            if (javaDataType == Boolean.class) {
                return BooleanValueToString(((Boolean) obj).booleanValue());
            }
            return null;
        }
        return obj.toString();
    }

    public boolean validate(Object obj, Object obj2) {
        Class<?> javaDataType = getJavaDataType();
        if (javaDataType != String.class && javaDataType != Boolean.class && obj.getClass() == obj2.getClass() && obj.getClass() == javaDataType) {
            return javaDataType == Integer.class ? ((Integer) obj).intValue() <= ((Integer) obj2).intValue() : javaDataType == Long.class ? ((Long) obj).longValue() <= ((Long) obj2).longValue() : javaDataType == Float.class ? ((Float) obj).floatValue() <= ((Float) obj2).floatValue() : javaDataType == Double.class && ((Double) obj).doubleValue() <= ((Double) obj2).doubleValue();
        }
        return false;
    }

    public boolean validate(Object obj, Object obj2, Object obj3) {
        Class<?> javaDataType = getJavaDataType();
        if (javaDataType == String.class || javaDataType == Boolean.class) {
            return false;
        }
        if (javaDataType != obj2.getClass()) {
            Log.d(TAG, "dataType invalid");
            return false;
        } else if (javaDataType == Integer.class) {
            Integer num = (Integer) obj2;
            return ((Integer) obj).intValue() <= num.intValue() && num.intValue() <= ((Integer) obj3).intValue();
        } else if (javaDataType == Long.class) {
            Long l = (Long) obj2;
            return ((Long) obj).longValue() <= l.longValue() && l.longValue() <= ((Long) obj3).longValue();
        } else if (javaDataType == Float.class) {
            Float f = (Float) obj2;
            return Float.compare(f.floatValue(), ((Float) obj).floatValue()) >= 0 && Float.compare(((Float) obj3).floatValue(), f.floatValue()) >= 0;
        } else if (javaDataType == Double.class) {
            Double d = (Double) obj2;
            return Double.compare(d.doubleValue(), ((Double) obj).doubleValue()) >= 0 && Double.compare(((Double) obj3).doubleValue(), d.doubleValue()) >= 0;
        } else {
            return false;
        }
    }
}
