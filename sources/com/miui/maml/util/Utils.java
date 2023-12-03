package com.miui.maml.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import java.util.ArrayList;
import miui.telephony.phonenumber.CountryCode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class Utils {
    private static ArrayList<String> INTENT_BLACK_LIST = null;
    private static int sAcrossUsersFullPermission = -2;
    private static int sAcrossUsersPermission = -2;

    /* loaded from: classes2.dex */
    public static class Point {
        public double x;
        public double y;

        public Point(double d, double d2) {
            this.x = d;
            this.y = d2;
        }

        public void Offset(Point point) {
            this.x += point.x;
            this.y += point.y;
        }

        Point minus(Point point) {
            return new Point(this.x - point.x, this.y - point.y);
        }
    }

    /* loaded from: classes2.dex */
    public interface XmlTraverseListener {
        void onChild(Element element);
    }

    static {
        ArrayList<String> arrayList = new ArrayList<>();
        INTENT_BLACK_LIST = arrayList;
        arrayList.add("android.intent.action.AIRPLANE_MODE");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_LOW");
        INTENT_BLACK_LIST.add("android.intent.action.BATTERY_OKAY");
        INTENT_BLACK_LIST.add("android.intent.action.BOOT_COMPLETED");
        INTENT_BLACK_LIST.add("android.intent.action.CONFIGURATION_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.DEVICE_STORAGE_LOW");
        INTENT_BLACK_LIST.add("android.intent.action.DEVICE_STORAGE_OK");
        INTENT_BLACK_LIST.add("android.intent.action.DREAMING_STARTED");
        INTENT_BLACK_LIST.add("android.intent.action.DREAMING_STOPPED");
        INTENT_BLACK_LIST.add("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
        INTENT_BLACK_LIST.add("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
        INTENT_BLACK_LIST.add("android.intent.action.LOCALE_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.MY_PACKAGE_REPLACED");
        INTENT_BLACK_LIST.add("android.intent.action.NEW_OUTGOING_CALL");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_ADDED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_DATA_CLEARED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_FIRST_LAUNCH");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_FULLY_REMOVED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_INSTALL");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_NEEDS_VERIFICATION");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_REMOVED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_REPLACED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_RESTARTED");
        INTENT_BLACK_LIST.add("android.intent.action.PACKAGE_VERIFIED");
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_POWER_CONNECTED");
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_POWER_DISCONNECTED");
        INTENT_BLACK_LIST.add("android.intent.action.REBOOT");
        INTENT_BLACK_LIST.add("android.intent.action.SCREEN_OFF");
        INTENT_BLACK_LIST.add("android.intent.action.SCREEN_ON");
        INTENT_BLACK_LIST.add("android.intent.action.ACTION_SHUTDOWN");
        INTENT_BLACK_LIST.add("android.intent.action.TIMEZONE_CHANGED");
        INTENT_BLACK_LIST.add("android.intent.action.TIME_TICK");
        INTENT_BLACK_LIST.add("android.intent.action.UID_REMOVED");
        INTENT_BLACK_LIST.add("android.intent.action.USER_PRESENT");
    }

    public static double Dist(Point point, Point point2, boolean z) {
        double d = point.x - point2.x;
        double d2 = point.y - point2.y;
        return z ? Math.sqrt((d * d) + (d2 * d2)) : (d * d) + (d2 * d2);
    }

    public static String addFileNameSuffix(String str, String str2) {
        return addFileNameSuffix(str, "_", str2);
    }

    public static String addFileNameSuffix(String str, String str2, String str3) {
        int indexOf = str.indexOf(46);
        if (indexOf == -1) {
            return str;
        }
        return str.substring(0, indexOf) + str2 + str3 + str.substring(indexOf);
    }

    private static boolean arrContains(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (TextUtils.equals(str2, str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (equals(str2, str)) {
                return true;
            }
        }
        return false;
    }

    public static void asserts(boolean z, String str) throws Exception {
        if (!z) {
            throw new Exception(str);
        }
    }

    public static String doubleToString(double d) {
        String valueOf = String.valueOf(d);
        return valueOf.endsWith(".0") ? valueOf.substring(0, valueOf.length() - 2) : valueOf;
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || (obj != null && obj.equals(obj2));
    }

    public static float getAttrAsFloat(Element element, String str, float f) {
        String attribute = element.getAttribute(str);
        if (!TextUtils.isEmpty(attribute)) {
            try {
                return Float.parseFloat(attribute);
            } catch (NumberFormatException unused) {
            }
        }
        return f;
    }

    public static int getAttrAsInt(Element element, String str, int i) {
        String attribute = element.getAttribute(str);
        if (!TextUtils.isEmpty(attribute)) {
            try {
                return Integer.parseInt(attribute);
            } catch (NumberFormatException unused) {
            }
        }
        return i;
    }

    public static long getAttrAsLong(Element element, String str, long j) {
        String attribute = element.getAttribute(str);
        if (!TextUtils.isEmpty(attribute)) {
            try {
                return Long.parseLong(attribute);
            } catch (NumberFormatException unused) {
            }
        }
        return j;
    }

    public static long getAttrAsLongThrows(Element element, String str) throws NumberFormatException {
        return Long.parseLong(element.getAttribute(str));
    }

    public static Element getChild(Element element, String str) {
        if (element == null) {
            return null;
        }
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && item.getNodeName().equalsIgnoreCase(str)) {
                return (Element) item;
            }
        }
        return null;
    }

    public static PorterDuff.Mode getPorterDuffMode(int i) {
        return getPorterDuffMode(i, PorterDuff.Mode.SRC_OVER);
    }

    public static PorterDuff.Mode getPorterDuffMode(int i, PorterDuff.Mode mode) {
        for (PorterDuff.Mode mode2 : PorterDuff.Mode.values()) {
            if (mode2.ordinal() == i) {
                return mode2;
            }
        }
        return mode;
    }

    public static PorterDuff.Mode getPorterDuffMode(String str) {
        if (TextUtils.isEmpty(str)) {
            return PorterDuff.Mode.SRC_OVER;
        }
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_OVER;
        for (PorterDuff.Mode mode2 : PorterDuff.Mode.values()) {
            if (str.equalsIgnoreCase(mode2.name())) {
                return mode2;
            }
        }
        return mode;
    }

    public static double getVariableNumber(String str, Variables variables) {
        return new IndexedVariable(str, variables, true).getDouble();
    }

    public static boolean isProtectedIntent(String str) {
        if (str == null) {
            return false;
        }
        return INTENT_BLACK_LIST.contains(str.trim());
    }

    public static int mixAlpha(int i, int i2) {
        if (i >= 255) {
            i = i2;
        } else if (i2 < 255) {
            i = Math.round((i * i2) / 255.0f);
        }
        return Math.min(255, Math.max(0, i));
    }

    public static String numberToString(Number number) {
        String valueOf = String.valueOf(number);
        return valueOf.endsWith(".0") ? valueOf.substring(0, valueOf.length() - 2) : valueOf;
    }

    public static double parseDouble(String str) {
        if (str.startsWith(CountryCode.GSM_GENERAL_IDD_CODE) && str.length() > 1) {
            str = str.substring(1);
        }
        return Double.parseDouble(str);
    }

    public static Point pointProjectionOnSegment(Point point, Point point2, Point point3, boolean z) {
        Point minus = point2.minus(point);
        Point minus2 = point3.minus(point);
        double Dist = ((minus.x * minus2.x) + (minus.y * minus2.y)) / Dist(point, point2, false);
        if (Dist < 0.0d || Dist > 1.0d) {
            if (z) {
                return Dist < 0.0d ? point : point2;
            }
            return null;
        }
        minus.x *= Dist;
        minus.y *= Dist;
        minus.Offset(point);
        return minus;
    }

    public static void putVariableNumber(String str, Variables variables, double d) {
        variables.put(str, d);
    }

    public static void putVariableNumber(String str, Variables variables, Double d) {
        variables.put(str, d.doubleValue());
    }

    public static void putVariableString(String str, Variables variables, String str2) {
        variables.put(str, str2);
    }

    public static void sendBroadcast(Context context, Intent intent) {
        if (sAcrossUsersPermission == -2) {
            sAcrossUsersPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        if (sAcrossUsersPermission == 0) {
            context.sendBroadcastAsUser(intent, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.sendBroadcast(intent);
        }
    }

    public static void startActivity(Context context, Intent intent, Bundle bundle) {
        if (sAcrossUsersFullPermission == -2) {
            sAcrossUsersFullPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
        }
        if (sAcrossUsersFullPermission == 0) {
            HideSdkDependencyUtils.Context_startActivityAsUser(context, intent, bundle, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.startActivity(intent, bundle);
        }
    }

    public static void startService(Context context, Intent intent) {
        if (sAcrossUsersPermission == -2) {
            sAcrossUsersPermission = context.checkSelfPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        if (sAcrossUsersPermission == 0) {
            HideSdkDependencyUtils.Context_startServiceAsUser(context, intent, HideSdkDependencyUtils.UserHandle_CURRENT());
        } else {
            context.startService(intent);
        }
    }

    public static double stringToDouble(String str, double d) {
        if (str == null) {
            return d;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException unused) {
            return d;
        }
    }

    public static void traverseXmlElementChildren(Element element, String str, XmlTraverseListener xmlTraverseListener) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1 && (str == null || TextUtils.equals(item.getNodeName(), str))) {
                xmlTraverseListener.onChild((Element) item);
            }
        }
    }

    public static void traverseXmlElementChildrenTags(Element element, String[] strArr, XmlTraverseListener xmlTraverseListener) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            String nodeName = item.getNodeName();
            if (item.getNodeType() == 1 && (strArr == null || arrContains(strArr, nodeName))) {
                xmlTraverseListener.onChild((Element) item);
            }
        }
    }
}
