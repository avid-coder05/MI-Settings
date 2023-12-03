package miui.util;

import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import miui.content.res.ThemeZipFile;
import miui.os.SystemProperties;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/* loaded from: classes4.dex */
public class FeatureParser {
    private static final String ASSET_DIR = "device_features/";
    private static final String SYSTEM_DIR = "/system/etc/device_features";
    private static final String TAG = "FeatureParser";
    private static final String TAG_BOOL = "bool";
    private static final String TAG_FLOAT = "float";
    private static final String TAG_INTEGER = "integer";
    private static final String TAG_INTEGER_ARRAY = "integer-array";
    private static final String TAG_ITEM = "item";
    private static final String TAG_STRING = "string";
    private static final String TAG_STRING_ARRAY = "string-array";
    public static final int TYPE_BOOL = 1;
    public static final int TYPE_FLOAT = 6;
    public static final int TYPE_INTEGER = 2;
    public static final int TYPE_INTEGER_ARRAY = 5;
    public static final int TYPE_STRING = 3;
    public static final int TYPE_STRING_ARRAY = 4;
    private static final String VENDOR_DIR = "/vendor/etc/device_features";
    private static final String FLAG_QSSI = SystemProperties.get("ro.product.system.device");
    private static HashMap<String, Integer> sIntMap = new HashMap<>();
    private static HashMap<String, Boolean> sBooleanMap = new HashMap<>();
    private static HashMap<String, String> sStrMap = new HashMap<>();
    private static HashMap<String, ArrayList<Integer>> sIntArrMap = new HashMap<>();
    private static HashMap<String, ArrayList<String>> sStrArrMap = new HashMap<>();
    private static HashMap<String, Float> sFloatMap = new HashMap<>();

    static {
        read();
    }

    public static boolean getBoolean(String str, boolean z) {
        Boolean bool = sBooleanMap.get(str);
        return bool != null ? bool.booleanValue() : z;
    }

    public static String getDeviceFeaturesDir() {
        return isSsiEnabled() ? VENDOR_DIR : SYSTEM_DIR;
    }

    public static Float getFloat(String str, float f) {
        Float f2 = sFloatMap.get(str);
        if (f2 != null) {
            f = f2.floatValue();
        }
        return Float.valueOf(f);
    }

    public static int[] getIntArray(String str) {
        ArrayList<Integer> arrayList = sIntArrMap.get(str);
        if (arrayList != null) {
            int size = arrayList.size();
            int[] iArr = new int[size];
            for (int i = 0; i < size; i++) {
                iArr[i] = arrayList.get(i).intValue();
            }
            return iArr;
        }
        return null;
    }

    public static int getInteger(String str, int i) {
        Integer num = sIntMap.get(str);
        return num != null ? num.intValue() : i;
    }

    public static String getString(String str) {
        return sStrMap.get(str);
    }

    public static String[] getStringArray(String str) {
        ArrayList<String> arrayList = sStrArrMap.get(str);
        if (arrayList != null) {
            return (String[]) arrayList.toArray(new String[0]);
        }
        return null;
    }

    public static boolean hasFeature(String str, int i) {
        switch (i) {
            case 1:
                return sBooleanMap.containsKey(str);
            case 2:
                return sIntMap.containsKey(str);
            case 3:
                return sStrMap.containsKey(str);
            case 4:
                return sStrArrMap.containsKey(str);
            case 5:
                return sIntArrMap.containsKey(str);
            case 6:
                return sFloatMap.containsKey(str);
            default:
                return false;
        }
    }

    private static boolean isSsiEnabled() {
        String str = FLAG_QSSI;
        return TextUtils.equals(str, "missi") || TextUtils.equals(str, "qssi");
    }

    private static void read() {
        String str;
        FileInputStream fileInputStream;
        InputStream inputStream = null;
        try {
            try {
                try {
                    String str2 = Build.DEVICE;
                    if ("cancro".equals(str2)) {
                        String str3 = Build.MODEL;
                        str = str3.startsWith("MI 3") ? "cancro_MI3.xml" : str3.startsWith("MI 4") ? "cancro_MI4.xml" : null;
                    } else {
                        str = str2 + ThemeZipFile.THEME_VALUE_FILE_SUFFIX;
                    }
                    try {
                        fileInputStream = Resources.getSystem().getAssets().open(ASSET_DIR + str);
                    } catch (IOException unused) {
                        Log.i(TAG, "can't find " + str + " in assets/" + ASSET_DIR + ",it may be in " + getDeviceFeaturesDir());
                        fileInputStream = null;
                    }
                    if (fileInputStream == null) {
                        try {
                            File file = new File(getDeviceFeaturesDir(), str);
                            if (!file.exists()) {
                                Log.e(TAG, "both assets/device_features/ and " + getDeviceFeaturesDir() + " don't exist " + str);
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                        return;
                                    } catch (IOException unused2) {
                                        return;
                                    }
                                }
                                return;
                            }
                            fileInputStream = new FileInputStream(file);
                        } catch (IOException unused3) {
                            inputStream = fileInputStream;
                            if (inputStream == null) {
                                return;
                            }
                            inputStream.close();
                        } catch (XmlPullParserException unused4) {
                            inputStream = fileInputStream;
                            if (inputStream == null) {
                                return;
                            }
                            inputStream.close();
                        } catch (Throwable th) {
                            inputStream = fileInputStream;
                            th = th;
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                } catch (IOException unused5) {
                                }
                            }
                            throw th;
                        }
                    }
                    XmlPullParser newPullParser = XmlPullParserFactory.newInstance().newPullParser();
                    newPullParser.setInput(fileInputStream, "UTF-8");
                    String str4 = null;
                    ArrayList<Integer> arrayList = null;
                    ArrayList<String> arrayList2 = null;
                    for (int eventType = newPullParser.getEventType(); 1 != eventType; eventType = newPullParser.next()) {
                        if (eventType == 2) {
                            String name = newPullParser.getName();
                            if (newPullParser.getAttributeCount() > 0) {
                                str4 = newPullParser.getAttributeValue(0);
                            }
                            if (TAG_INTEGER_ARRAY.equals(name)) {
                                arrayList = new ArrayList<>();
                            } else if (TAG_STRING_ARRAY.equals(name)) {
                                arrayList2 = new ArrayList<>();
                            } else if (TAG_BOOL.equals(name)) {
                                sBooleanMap.put(str4, Boolean.valueOf(newPullParser.nextText()));
                            } else if (TAG_INTEGER.equals(name)) {
                                sIntMap.put(str4, Integer.valueOf(newPullParser.nextText()));
                            } else if ("string".equals(name)) {
                                sStrMap.put(str4, newPullParser.nextText());
                            } else if (TAG_FLOAT.equals(name)) {
                                sFloatMap.put(str4, Float.valueOf(Float.parseFloat(newPullParser.nextText())));
                            } else if ("item".equals(name)) {
                                if (arrayList != null) {
                                    arrayList.add(Integer.valueOf(newPullParser.nextText()));
                                } else if (arrayList2 != null) {
                                    arrayList2.add(newPullParser.nextText());
                                }
                            }
                        } else if (eventType == 3) {
                            String name2 = newPullParser.getName();
                            if (TAG_INTEGER_ARRAY.equals(name2)) {
                                sIntArrMap.put(str4, arrayList);
                                arrayList = null;
                            } else if (TAG_STRING_ARRAY.equals(name2)) {
                                sStrArrMap.put(str4, arrayList2);
                                arrayList2 = null;
                            }
                        }
                    }
                    fileInputStream.close();
                } catch (IOException unused6) {
                }
            } catch (IOException unused7) {
            }
        } catch (XmlPullParserException unused8) {
        } catch (Throwable th2) {
            th = th2;
        }
    }
}
