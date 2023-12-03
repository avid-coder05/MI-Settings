package com.android.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.app.timezonedetector.TimeZoneDetector;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.hardware.SensorPrivacyManager;
import android.hardware.input.InputDeviceIdentifier;
import android.hardware.usb.UsbManager;
import android.icu.util.TimeZone;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.MiuiWifiManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.storage.DiskInfo;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.preference.PreferenceFrameLayout;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.provider.SystemSettings$System;
import android.security.MiuiLockPatternUtils;
import android.support.v4.media.session.PlaybackStateCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceScreen;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.settings.accessibility.MiuiAccessibilityAsrController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datetime.DualClockHealper;
import com.android.settings.search.SearchUpdater;
import com.android.settings.security.SecuritySettingsController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import com.android.settings.wifi.AutoConnectUtils;
import com.android.settingslib.inputmethod.InputMethodAndSubtypeUtilCompat;
import com.android.settingslib.wifi.AccessPoint;
import com.miui.enterprise.RestrictionsHelper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import miui.accounts.ExtraAccountManager;
import miui.content.ExtraIntent;
import miui.content.res.ThemeResources;
import miui.util.FeatureParser;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.widget.Spinner;
import miuix.core.util.Utf8TextUtils;
import miuix.internal.util.ActionBarUtils;
import miuix.internal.util.AttributeResolver;
import miuix.pinyin.utilities.ChinesePinyinConverter;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes.dex */
public class MiuiUtils {
    public static final List<String> DOMESTIC_WHITE_LIST;
    public static final List<String> INTERNATIONAL_WHITE_LIST;
    private static List<String> LOW_MEMORY_MACHINE;
    private static MiuiUtils sInstance;
    private static Long totalMemory;

    static {
        ArrayList arrayList = new ArrayList();
        LOW_MEMORY_MACHINE = arrayList;
        arrayList.add("dandelion");
        LOW_MEMORY_MACHINE.add("angelica");
        LOW_MEMORY_MACHINE.add("angelicain");
        LOW_MEMORY_MACHINE.add("angelican");
        LOW_MEMORY_MACHINE.add("cattail");
        DOMESTIC_WHITE_LIST = new ArrayList<String>() { // from class: com.android.settings.MiuiUtils.1
            {
                add("com.android.settings");
                add("com.miui.notification");
                add(ThemeResources.SYSTEMUI_NAME);
                add("com.xiaomi.bluetooth");
                add("com.android.thememanager");
                add("com.android.quicksearchbox");
                add("com.miui.voiceassist");
                add(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME);
                add("com.miui.home");
            }
        };
        INTERNATIONAL_WHITE_LIST = new ArrayList<String>() { // from class: com.android.settings.MiuiUtils.2
            {
                add("com.android.settings");
                add("com.miui.notification");
                add(ThemeResources.SYSTEMUI_NAME);
                add("com.xiaomi.bluetooth");
                add("com.miui.home");
            }
        };
    }

    public static Dialog buildGlobalChangeWarningDialog(Context context, int i, final Runnable runnable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(i);
        builder.setMessage(R.string.global_change_warning);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiUtils.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                runnable.run();
            }
        });
        builder.setNegativeButton(17039360, (DialogInterface.OnClickListener) null);
        return builder.create();
    }

    public static Intent buildLauncherSettingsIntent() {
        if (SystemProperties.get("ro.miui.product.home", "com.miui.home").equals("com.miui.home")) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName("com.miui.home", "com.miui.home.settings.MiuiHomeSettingActivity");
            return intent;
        }
        Intent intent2 = new Intent("com.mi.android.globallauncher.Setting");
        intent2.setPackage("com.mi.android.globallauncher");
        return intent2;
    }

    public static Intent buildUcarSettingsIntent() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.UCAR_APP_SETTINGS");
        intent.setPackage("com.miui.carlink");
        for (ResolveInfo resolveInfo : AppGlobals.getInitialApplication().getPackageManager().queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals("com.miui.carlink")) {
                intent.setComponent(resolveInfo.getComponentInfo().getComponentName());
                return intent;
            }
        }
        return null;
    }

    public static Intent buildXiaoAiSettingsIntent() {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.APP_SETTINGS");
        if (SettingsFeatures.isSplitTabletDevice() || SettingsFeatures.isFoldDevice()) {
            intent.addMiuiFlags(16);
        }
        for (ResolveInfo resolveInfo : AppGlobals.getInitialApplication().getPackageManager().queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.packageName.equals("com.miui.voiceassist")) {
                intent.setComponent(resolveInfo.getComponentInfo().getComponentName());
                return intent;
            }
        }
        return null;
    }

    public static boolean canFindActivityStatic(Context context, Intent intent) {
        return intent.resolveActivityInfo(context.getPackageManager(), SearchUpdater.GOOGLE) != null;
    }

    public static void cancelSplit(Context context, Intent intent) {
    }

    public static void closeIo(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeSensorOff(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sensoroff", 0);
        boolean z = sharedPreferences.getBoolean("sensor_off", false);
        SensorPrivacyManager sensorPrivacyManager = (SensorPrivacyManager) context.getSystemService("sensor_privacy");
        boolean isAllSensorPrivacyEnabled = sensorPrivacyManager.isAllSensorPrivacyEnabled();
        Log.i("MiuiUtils", "Sensor off enable status : " + isAllSensorPrivacyEnabled);
        if (z || !isAllSensorPrivacyEnabled) {
            return;
        }
        sensorPrivacyManager.setAllSensorPrivacy(false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("sensor_off", true);
        edit.commit();
    }

    public static View decorateItemAsPreference(View view) {
        Drawable resolveDrawable = AttributeResolver.resolveDrawable(view.getContext(), R.attr.preferenceItemBackground);
        Rect rect = new Rect();
        if (resolveDrawable != null) {
            resolveDrawable.getPadding(rect);
            view.setBackground(resolveDrawable);
        }
        view.setPaddingRelative(view.getContext().getResources().getDimensionPixelSize(R.dimen.miuix_preference_item_padding_start), view.getPaddingTop(), view.getContext().getResources().getDimensionPixelSize(R.dimen.miuix_preference_item_padding_end), view.getPaddingBottom());
        return view;
    }

    public static int dp2px(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static Bitmap drawableToBitmap(Context context, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), i, options);
        options.inSampleSize = 2;
        options.inJustDecodeBounds = false;
        options.inDensity = 160;
        Bitmap decodeResource = BitmapFactory.decodeResource(context.getResources(), i, options);
        return decodeResource == null ? drawableToBitmap(context, context.getDrawable(i)) : decodeResource;
    }

    public static Bitmap drawableToBitmap(Context context, Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(canvas);
        return createBitmap;
    }

    public static void enableSpringBackLayout(View view, boolean z) {
        if (view == null) {
            return;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof SpringBackLayout) {
            parent = parent.getParent();
        }
        while (parent != null) {
            if (z && (parent instanceof View)) {
                ((View) parent).requestFocus();
            }
            if (parent instanceof SpringBackLayout) {
                SpringBackLayout springBackLayout = (SpringBackLayout) parent;
                springBackLayout.setEnabled(z);
                springBackLayout.requestDisallowInterceptTouchEvent(!z);
            }
            parent = parent.getParent();
        }
    }

    public static void enableVolumKeyWakeUp(boolean z) {
        String str = z ? "1" : "0";
        if ("leadcore".equals(FeatureParser.getString("vendor"))) {
            writeToFile("/sys/devices/platform/comip-gpio-keys/keys_wakeup", str);
        } else if ("mediatek".equals(FeatureParser.getString("vendor"))) {
            writeToFile("/sys/bus/platform/drivers/mtk-kpd/kpd_as_wake", str);
            writeToFile("/sys/devices/platform/mtk-kpd/driver/kpd_as_wake", str);
        }
    }

    public static boolean enabledInputMethod(Context context, String str) {
        List<InputMethodInfo> inputMethodList;
        boolean z;
        if (context == null || TextUtils.isEmpty(str) || (inputMethodList = ((InputMethodManager) context.getSystemService("input_method")).getInputMethodList()) == null) {
            return false;
        }
        Iterator<InputMethodInfo> it = inputMethodList.iterator();
        while (true) {
            if (!it.hasNext()) {
                z = false;
                break;
            }
            InputMethodInfo next = it.next();
            if (next != null && TextUtils.equals(str, next.getId())) {
                z = true;
                break;
            }
        }
        if (!z) {
            Log.d("MiuiUtils", "input method not contains with InputMethodList: " + str);
            return false;
        }
        Log.d("MiuiUtils", "enabledInputMethod: " + str);
        ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) {
            return false;
        }
        HashMap<String, HashSet<String>> parseInputMethodsAndSubtypesString = InputMethodAndSubtypeUtilCompat.parseInputMethodsAndSubtypesString(Settings.Secure.getString(contentResolver, "enabled_input_methods"));
        if (!parseInputMethodsAndSubtypesString.containsKey(str)) {
            parseInputMethodsAndSubtypesString.put(str, new HashSet<>());
        }
        String buildInputMethodsAndSubtypesString = InputMethodAndSubtypeUtilCompat.buildInputMethodsAndSubtypesString(parseInputMethodsAndSubtypesString);
        if (TextUtils.isEmpty(buildInputMethodsAndSubtypesString)) {
            return false;
        }
        Settings.Secure.putString(contentResolver, "enabled_input_methods", buildInputMethodsAndSubtypesString);
        return true;
    }

    public static boolean excludeXiaoAi(Context context) {
        if (context == null) {
            return false;
        }
        return !isXiaoAiExist(context) || (isLowMemoryMachine() && isLower3GB());
    }

    public static boolean existsJeejen(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 131072);
        if (queryIntentActivities != null) {
            for (ResolveInfo resolveInfo : queryIntentActivities) {
                if (resolveInfo.activityInfo.packageName.equals("com.jeejen.family") || resolveInfo.activityInfo.packageName.equals("com.jeejen.family.miui")) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static String formatShortSize(Context context, long j, String str, Locale locale) {
        if (context == null) {
            return "";
        }
        float f = (float) j;
        int i = R.string.size_byte;
        if (f > 900.0d) {
            i = R.string.size_kilo_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_mega_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_giga_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_tera_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_peta_byte;
            f /= 1000.0f;
        }
        return context.getResources().getString(R.string.size_suffix, String.format(locale, str, Float.valueOf(f)), context.getString(i));
    }

    public static String formatSize(Context context, long j) {
        if (context == null) {
            return "";
        }
        float f = (float) j;
        int i = R.string.size_byte;
        if (f > 900.0d) {
            i = R.string.size_kilo_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_mega_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_giga_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_tera_byte;
            f /= 1000.0f;
        }
        if (f > 900.0d) {
            i = R.string.size_peta_byte;
            f /= 1000.0f;
        }
        return context.getResources().getString(R.string.size_suffix, String.format("%.2f", Float.valueOf(f)), context.getString(i));
    }

    public static String formatSizeWith1024(Context context, long j) {
        if (context == null) {
            return "";
        }
        float f = (float) j;
        int i = R.string.size_byte;
        if (f > 921.6d) {
            i = R.string.size_kilo_byte;
            f /= 1024.0f;
        }
        if (f > 921.6d) {
            i = R.string.size_mega_byte;
            f /= 1024.0f;
        }
        if (f > 921.6d) {
            i = R.string.size_giga_byte;
            f /= 1024.0f;
        }
        if (f > 921.6d) {
            i = R.string.size_tera_byte;
            f /= 1024.0f;
        }
        if (f > 921.6d) {
            i = R.string.size_peta_byte;
            f /= 1024.0f;
        }
        return context.getResources().getString(R.string.size_suffix, String.format("%.2f", Float.valueOf((float) Math.ceil(f))), context.getString(i));
    }

    public static long getAppLongVersionCode(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return -1L;
        }
        try {
            return context.getPackageManager().getPackageInfo(str, 0).getLongVersionCode();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MiuiUtils", "Cannot find package: " + str, e);
            return -1L;
        }
    }

    public static int getChargeSpeed(int i, int i2) {
        if (i == 11 || i == 10) {
            if (isStrongSuperRapidCharge(i2)) {
                return 3;
            }
            if (isSuperRapidCharge(i2)) {
                return 2;
            }
            return isRapidCharge(i2) ? 1 : 0;
        }
        return -1;
    }

    public static int getDimenValue(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(i, typedValue, true);
        return (int) TypedValue.complexToFloat(typedValue.data);
    }

    public static Drawable getIconDrawableById(Context context, int i, String str) {
        Drawable drawable = null;
        if (i > 0) {
            try {
                drawable = context.createPackageContext(str, 0).getResources().getDrawable(i);
            } catch (Exception e) {
                Log.w("MiuiUtils", "Could not get getIconDrawable for " + str + ": " + e);
            }
            if (drawable == null) {
                try {
                    return context.getResources().getDrawable(i);
                } catch (Exception e2) {
                    Log.w("MiuiUtils", "Could not get getIconDrawable for com.android.settings : " + e2);
                    return drawable;
                }
            }
            return drawable;
        }
        return null;
    }

    public static MiuiUtils getInstance() {
        if (sInstance == null) {
            try {
                int i = DeviceUtils.$r8$clinit;
                sInstance = (MiuiUtils) DeviceUtils.class.newInstance();
            } catch (Exception unused) {
                sInstance = new MiuiUtils();
            }
        }
        return sInstance;
    }

    public static String getMashupSoundSummary(Context context, String str) {
        String[] stringArray = context.getResources().getStringArray(R.array.coolsound_area_mash_up);
        int i = 0;
        while (true) {
            if (i >= stringArray.length) {
                i = -1;
                break;
            } else if (str != null && str.equals(stringArray[i])) {
                break;
            } else {
                i++;
            }
        }
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? context.getResources().getString(R.string.mashup_sound) : context.getResources().getString(R.string.mashup_sound_arctic) : context.getResources().getString(R.string.mashup_sound_australia) : context.getResources().getString(R.string.mashup_sound_africa) : context.getResources().getString(R.string.mashup_sound_amazon);
    }

    public static boolean getMobileDataEnabled(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            Log.d("ConnectivityManager", "getMobileDataEnabled()- remote exception retVal=false");
            return false;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefaultDataSubscriptionId();
        Log.d("ConnectivityManager", "getMobileDataEnabled()+ subId=" + defaultDataSubscriptionId);
        boolean isDataEnabled = telephonyManager.createForSubscriptionId(defaultDataSubscriptionId).isDataEnabled();
        Log.d("ConnectivityManager", "getMobileDataEnabled()- subId=" + defaultDataSubscriptionId + " retVal=" + isDataEnabled);
        return isDataEnabled;
    }

    public static String getP2pDeviceName(Context context) {
        String str = SystemProperties.get(SystemSettings$System.RO_MARKET_NAME, (String) null);
        if (TextUtils.isEmpty(str)) {
            str = Build.MODEL;
        }
        String str2 = SystemProperties.get("persist.sys.p2p_local_name", str);
        if (str2.getBytes().length > 30) {
            int length = str2.length();
            for (int i = 0; i < length; i++) {
                if (str2.substring(0, i).getBytes().length > 30) {
                    return str2.substring(0, i - 1);
                }
            }
            return str2;
        }
        return str2;
    }

    public static int getPaperModeType(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_mode_type", 0);
    }

    public static String getQuantityStringWithUnit(Locale locale, int i, long j) {
        float f = (float) j;
        String str = "M";
        float f2 = 0.0f;
        if (f <= 1.07374184E8f) {
            if (f > 104857.6f) {
                f2 = f / 1048576.0f;
            } else if (f > 0.0f) {
                f2 = 0.1f;
            }
            return String.format(locale, String.format(Locale.US, "%%1$.%df%sB", Integer.valueOf(i), str), Float.valueOf(f2));
        }
        f2 = f / 1.0737418E9f;
        str = "G";
        return String.format(locale, String.format(Locale.US, "%%1$.%df%sB", Integer.valueOf(i), str), Float.valueOf(f2));
    }

    public static String getResourceName(Context context, int i) {
        try {
            return getResourceName(context, context.getResources().getResourceName(i));
        } catch (Exception unused) {
            Log.w("MiuiUtils", "getResourceName resId not founc");
            return null;
        }
    }

    public static String getResourceName(Context context, CharSequence charSequence) {
        String str;
        try {
            str = String.valueOf(charSequence);
            try {
                return (TextUtils.isEmpty(str) || !str.contains("/")) ? str : str.substring(str.indexOf(47) + 1);
            } catch (Resources.NotFoundException unused) {
                Log.d("MiuiUtils", "resource not found");
                return str;
            }
        } catch (Resources.NotFoundException unused2) {
            str = null;
        }
    }

    private static String getResourceName(String str) {
        if ("cancro".equals(Build.DEVICE) && Build.MODEL.startsWith("MI 3")) {
            return str + "_mi3";
        }
        return str;
    }

    public static String getStringByResName(Context context, String str, String str2) {
        return getStringByResName(context, str, "string", str2);
    }

    public static String getStringByResName(Context context, String str, String str2, String str3) {
        int identifier;
        if (context != null && !TextUtils.isEmpty(str3)) {
            try {
                Resources resources = context.createPackageContext(str, 0).getResources();
                return (resources == null || (identifier = resources.getIdentifier(str3, str2, str)) == 0) ? "" : resources.getString(identifier);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getStringFromSpecificPackage(Context context, String str, String str2) {
        int identifier;
        try {
            Resources resources = context.createPackageContext(str, 0).getResources();
            return (resources == null || (identifier = resources.getIdentifier(str2, "string", str)) == 0) ? "" : resources.getString(identifier);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static Pair<Integer, Integer> getSystemDefaultEnterAnim(Activity activity) {
        TypedArray obtainStyledAttributes = activity.obtainStyledAttributes(null, new int[]{16842936, 16842937}, 16842926, 0);
        int resourceId = obtainStyledAttributes.getResourceId(0, -1);
        int resourceId2 = obtainStyledAttributes.getResourceId(1, -1);
        obtainStyledAttributes.recycle();
        return new Pair<>(Integer.valueOf(resourceId), Integer.valueOf(resourceId2));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static Long getTotalMem() {
        BufferedReader bufferedReader;
        Throwable th;
        FileReader fileReader;
        if (totalMemory == null) {
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    fileReader = new FileReader("/proc/meminfo");
                    try {
                        bufferedReader = new BufferedReader(fileReader);
                    } catch (Exception unused) {
                    }
                } catch (Throwable th2) {
                    bufferedReader = bufferedReader2;
                    th = th2;
                }
                try {
                    Long valueOf = Long.valueOf(bufferedReader.readLine().split("\\s+")[1]);
                    totalMemory = valueOf;
                    closeIo(bufferedReader);
                    bufferedReader2 = valueOf;
                } catch (Exception unused2) {
                    bufferedReader2 = bufferedReader;
                    totalMemory = 0L;
                    closeIo(bufferedReader2);
                    bufferedReader2 = bufferedReader2;
                    closeIo(fileReader);
                    return totalMemory;
                } catch (Throwable th3) {
                    th = th3;
                    closeIo(bufferedReader);
                    closeIo(fileReader);
                    throw th;
                }
            } catch (Exception unused3) {
                fileReader = null;
            } catch (Throwable th4) {
                bufferedReader = null;
                th = th4;
                fileReader = null;
            }
            closeIo(fileReader);
        }
        return totalMemory;
    }

    public static void handleForgetPasswordRequestForSSpace(final Activity activity, String str, Intent intent, MiuiLockPatternUtils miuiLockPatternUtils, final int i) {
        final AccountManagerCallback<Bundle> accountManagerCallback = new AccountManagerCallback<Bundle>() { // from class: com.android.settings.MiuiUtils.5
            @Override // android.accounts.AccountManagerCallback
            public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                try {
                    Bundle result = accountManagerFuture.getResult();
                    if (result != null && result.getBoolean("booleanResult")) {
                        Toast.makeText(activity, R.string.resetting_second_space_passwd, 1).show();
                        activity.finish();
                        Intent intent2 = new Intent();
                        intent2.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.service.RecoveryService"));
                        intent2.putExtra("userId", i);
                        intent2.putExtra("action", "recovery");
                        activity.startService(intent2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialog_Theme_DayNight);
        builder.setTitle(R.string.reset_second_space_passwd);
        builder.setMessage(R.string.reset_second_space_passwd_detail);
        builder.setNegativeButton(R.string.reset_second_space_passwd_cancel, (DialogInterface.OnClickListener) null);
        builder.setPositiveButton(R.string.reset_second_space_passwd_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiUtils.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                Account xiaomiAccount = ExtraAccountManager.getXiaomiAccount(activity);
                AccountManager accountManager = AccountManager.get(activity);
                Bundle bundle = new Bundle();
                bundle.putBoolean("verify_only", true);
                accountManager.confirmCredentials(xiaomiAccount, bundle, activity, accountManagerCallback, null);
            }
        });
        builder.show();
    }

    public static boolean hasLauncherIcon(Context context, String str) {
        Intent intent = new Intent("android.intent.action.MAIN", (Uri) null);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    public static boolean hasSDCard(Context context) {
        DiskInfo findDiskById;
        if (context != null) {
            StorageManager storageManager = (StorageManager) context.getSystemService("storage");
            for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
                if (volumeInfo.getType() == 0 && (findDiskById = storageManager.findDiskById(volumeInfo.getDiskId())) != null && findDiskById.isSd() && volumeInfo.isMountedReadable()) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public static boolean includeXiaoAi(Context context) {
        return !excludeXiaoAi(context);
    }

    public static boolean isActivityAvalible(Context context, Intent intent) {
        List<ResolveInfo> queryIntentActivities;
        return (intent == null || context == null || (queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 1)) == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    public static boolean isAiKeyExist(Context context) {
        return context.getPackageManager().queryIntentActivities(new Intent("com.miui.voiceassist.ACTION_AI_BUTTON_SETTINGS"), 0).size() > 0;
    }

    public static boolean isAppEnabled(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isAppInstalledAndEnabled(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            return context.getPackageManager().getApplicationInfo(str, 0).enabled;
        } catch (Exception e) {
            Log.e("MiuiUtils", "isAppInstalledAndEnabled: application not found", e);
            return false;
        }
    }

    public static boolean isApplicationInstalled(Context context, String str) {
        try {
            context.getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MiuiUtils", "appliction not found", e);
            return false;
        }
    }

    public static boolean isAutoBrightnessModeEnabled(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "screen_brightness_mode", 0) != 0;
    }

    public static boolean isBackupDisabled(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "local_backup_disable_service", false);
    }

    public static boolean isDeviceFinanceOwner(Context context) {
        if (RegionUtils.IS_MX_AT || RegionUtils.IS_IN_FK || RegionUtils.IS_TH_AS) {
            try {
                DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
                return ((Integer) devicePolicyManager.getClass().getMethod("getDeviceOwnerType", ComponentName.class).invoke(devicePolicyManager, (ComponentName) devicePolicyManager.getClass().getMethod("getDeviceOwnerComponentOnCallingUser", new Class[0]).invoke(devicePolicyManager, new Object[0]))).intValue() == 1;
            } catch (Exception e) {
                Log.e("MiuiUtils", "isDeviceFinanceOwner error:" + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public static boolean isDeviceManaged(Context context) {
        return ((DevicePolicyManager) context.getSystemService("device_policy")).isDeviceManaged();
    }

    public static boolean isDeviceProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) == 1;
    }

    public static boolean isEasyMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "elderly_mode", 0) == 1;
    }

    public static boolean isFilePathValid(String str) {
        return !TextUtils.isEmpty(str) && isFileValid(new File(str));
    }

    private static boolean isFileValid(File file) {
        return file.exists() && file.length() != 0;
    }

    public static boolean isHuanjiInProgress(Context context) {
        return isHuanjiRunning(context) && isHuanjiServiceExisted(context);
    }

    private static boolean isHuanjiRunning(Context context) {
        Iterator<ActivityManager.RunningAppProcessInfo> it = ((ActivityManager) context.getSystemService("activity")).getRunningAppProcesses().iterator();
        while (it.hasNext()) {
            if ("com.miui.huanji".equals(it.next().processName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isHuanjiServiceExisted(Context context) {
        Iterator<ActivityManager.RunningServiceInfo> it = ((ActivityManager) context.getSystemService("activity")).getRunningServices(Integer.MAX_VALUE).iterator();
        while (it.hasNext()) {
            if ("com.miui.huanji.backup.BackupService".equals(it.next().service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInFullWindowGestureMode(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
    }

    public static boolean isInputMethodSupported(Context context, String str) {
        InputMethodManager inputMethodManager;
        List<InputMethodInfo> enabledInputMethodList;
        if (context == null || TextUtils.isEmpty(str) || (inputMethodManager = (InputMethodManager) context.getSystemService("input_method")) == null || (enabledInputMethodList = inputMethodManager.getEnabledInputMethodList()) == null) {
            return false;
        }
        for (InputMethodInfo inputMethodInfo : enabledInputMethodList) {
            if (inputMethodInfo != null && TextUtils.equals(str, inputMethodInfo.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInsertUsb(Context context) {
        if (context == null) {
            return false;
        }
        StorageManager storageManager = (StorageManager) context.getSystemService(StorageManager.class);
        for (VolumeInfo volumeInfo : storageManager.getVolumes()) {
            DiskInfo findDiskById = volumeInfo.getType() == 0 ? storageManager.findDiskById(volumeInfo.getDiskId()) : null;
            if (findDiskById != null && findDiskById.isUsb() && volumeInfo.getState() == 2) {
                return true;
            }
        }
        return false;
    }

    public static boolean isIntentActivityExistAsUser(Context context, Intent intent, int i) {
        if (context != null && intent != null) {
            try {
                List queryIntentActivitiesAsUser = context.getPackageManager().queryIntentActivitiesAsUser(intent, 0, i);
                if (queryIntentActivitiesAsUser != null) {
                    return queryIntentActivitiesAsUser.size() > 0;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean isLandScape(Context context) {
        try {
            return context.getResources().getConfiguration().orientation == 2;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean isLowMemoryMachine() {
        return LOW_MEMORY_MACHINE.contains(SystemProperties.get("ro.build.product"));
    }

    public static boolean isLower3GB() {
        return miui.os.Build.TOTAL_RAM < 3;
    }

    public static boolean isLower4GB() {
        return (getTotalMem().longValue() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID < 3;
    }

    public static boolean isMaintenanceMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "maintenance_mode_user_id", -1) == 110;
    }

    public static boolean isMiuiSdkSupportFolme() {
        return true;
    }

    public static boolean isNetworkConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean isPackagesSupportMetaDataFeature(String str, Context context, String str2) {
        try {
            Bundle bundle = context.getPackageManager().getApplicationInfo(str, 128).metaData;
            if (bundle != null) {
                return bundle.getBoolean(str2, false);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPaperModeEnable(Context context) {
        return MiuiSettings.System.getBoolean(context.getContentResolver(), "screen_paper_mode_enabled", false);
    }

    public static boolean isRapidCharge(int i) {
        return i == 1;
    }

    public static boolean isSecondSpace(Context context) {
        return UserHandle.myUserId() == Settings.Secure.getIntForUser(context.getContentResolver(), "second_user_id", -10000, 0);
    }

    public static boolean isStrongSuperRapidCharge(int i) {
        return i == 4;
    }

    public static boolean isSuperRapidCharge(int i) {
        return i == 2 || i == 3;
    }

    public static boolean isSupportHDRMode() {
        int identifier = Resources.getSystem().getIdentifier("support_video_hdr_mode", "bool", "android.miui");
        if (identifier > 0) {
            return Resources.getSystem().getBoolean(identifier);
        }
        return false;
    }

    public static boolean isSupportNightMode(Context context) {
        return queryBoolean(context, "support_night_mode");
    }

    public static boolean isSupportSafetyEmergencySettings(Context context) {
        return miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean isSupportScreenFps() {
        int[] intArray;
        return UserHandle.myUserId() == 0 && (intArray = FeatureParser.getIntArray("fpsList")) != null && intArray.length > 0;
    }

    public static boolean isSupportSecuritySettings(Context context) {
        return SecuritySettingsController.hasSecurityCenterSecureEntry();
    }

    public static boolean isSupportSmartDark() {
        return true;
    }

    public static boolean isSupportSubScreen() {
        return "star".equals(Build.DEVICE);
    }

    public static boolean isSupportUcarSettings(Context context) {
        Intent intent = new Intent();
        intent.setAction("miui.intent.action.UCAR_APP_SETTINGS");
        intent.setPackage("com.miui.carlink");
        List<ResolveInfo> queryIntentActivities = context.getPackageManager().queryIntentActivities(intent, 0);
        return queryIntentActivities != null && queryIntentActivities.size() > 0;
    }

    public static boolean isSystemApp(Context context, String str) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (Exception e) {
            Log.w("MiuiUtils", e.toString());
            packageInfo = null;
        }
        if (packageInfo != null) {
            int i = packageInfo.applicationInfo.flags;
            return ((i & 1) == 1) || ((i & 128) == 1);
        }
        return false;
    }

    public static boolean isUWBSupport(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.miui.smarthomeplus", "com.miui.smarthomeplus.settings.uwb.UwbSettingsActivity");
        return isActivityAvalible(context, intent) && !isSecondSpace(context);
    }

    public static boolean isUsbBackupEnable(Context context) {
        return !RestrictionsHelper.hasRestriction(context, "disallow_backup");
    }

    public static boolean isXiaoAiExist(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.miui.voiceassist", 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("MiuiUtils", "isXiaoAiExist: noExist");
            return false;
        }
    }

    public static boolean needOverlayTwLocale() {
        return SystemProperties.get("ro.miui.build.region", "").toLowerCase().equals("tw");
    }

    public static boolean needRemoveMigrateHistory(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), "data_trans_history");
        return miui.os.Build.IS_INTERNATIONAL_BUILD || string == null || TextUtils.isEmpty(string);
    }

    public static boolean needRemovePersonalize(Context context) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("theme://zhuti.xiaomi.com/personalize?miback=true&miref=settings"));
        return context.getPackageManager().queryIntentActivities(intent, 0).size() <= 0;
    }

    public static boolean needRemoveSystemAppsUpdater(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.xiaomi.discover", "com.xiaomi.market.ui.UpdateAppsActivity"));
        return context.getPackageManager().queryIntentActivities(intent, 0).size() <= 0;
    }

    public static boolean needRemoveWalletEntrance(Context context) {
        Intent intent = new Intent("com.mipay.action.MIPAYINFO");
        intent.setPackage("com.mipay.wallet");
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        if (queryIntentActivities.size() <= 0) {
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("com.mipay.wallet.MIPAYINFO");
            intent.setData(Uri.parse("mipay://walletapp?id=mipay.info"));
            queryIntentActivities = packageManager.queryIntentActivities(intent, 0);
        }
        return miui.os.Build.IS_INTERNATIONAL_BUILD || queryIntentActivities.size() <= 0;
    }

    public static void notifyNightModeShowStateChange(Context context) {
        MiuiSettings.System.putBooleanForUser(context.getContentResolver(), "is_darkmode_switch_show", isSupportNightMode(context), UserHandle.myUserId());
    }

    public static void onFinishEdit(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity != null) {
            ((InputMethodManager) activity.getSystemService("input_method")).hideSoftInputFromWindow(fragment.getView().getWindowToken(), 0);
            if (activity instanceof MiuiSettings) {
                ((MiuiSettings) activity).onFinishEdit();
            }
        }
    }

    public static void onStartEdit(Fragment fragment) {
        FragmentActivity activity = fragment.getActivity();
        if (activity instanceof MiuiSettings) {
            ((MiuiSettings) activity).onStartEdit();
        }
    }

    public static String overlayLocaleLanguageLabel(Context context, String str, String str2) {
        String[] stringArray = context.getResources().getStringArray(R.array.special_locale_codes_global);
        String[] stringArray2 = context.getResources().getStringArray(R.array.special_locale_names_global);
        for (int i = 0; i < stringArray.length; i++) {
            if (stringArray[i].equals(str)) {
                str2 = stringArray2[i];
            }
        }
        return str2;
    }

    public static boolean queryBoolean(Context context, String str) {
        int identifier;
        Resources resources = context.getResources();
        if (resources == null || (identifier = resources.getIdentifier(getResourceName(str), "bool", context.getPackageName())) == 0) {
            return false;
        }
        return resources.getBoolean(identifier);
    }

    public static String[] queryStringArray(Context context, String str) {
        int identifier;
        Resources resources = context.getResources();
        if (resources == null || (identifier = resources.getIdentifier(getResourceName(str), "array", context.getPackageName())) == 0) {
            return null;
        }
        return resources.getStringArray(identifier);
    }

    public static String reflectGetReferrer(Activity activity) {
        try {
            Field declaredField = Class.forName("android.app.Activity").getDeclaredField("mReferrer");
            declaredField.setAccessible(true);
            return (String) declaredField.get(activity);
        } catch (Exception e) {
            Log.e("MiuiUtils", "reflectGetReferrer Exception", e);
            return null;
        }
    }

    public static void resetDualClockIfNeed(Context context) {
        if ("Asia/Urumqi".equals(DualClockHealper.getDualTimeZoneID(context))) {
            Settings.System.putInt(context.getContentResolver(), AodStylePreferenceController.AUTO_DUAL_CLOCK, 0);
            DualClockHealper.saveTimeZone(context, "CN", "Asia/Shanghai");
        }
    }

    public static void resetSFRDualClock(Context context) {
        DualClockHealper.saveTimeZone(context, "PT", "Europe/Lisbon");
    }

    public static void resetTimeZoneIfNeed(Context context) {
        boolean z = Settings.Global.getInt(context.getContentResolver(), "auto_time_zone", 0) == 0;
        TimeZone timeZone = TimeZone.getDefault();
        if (z && "Asia/Urumqi".equals(timeZone.getID())) {
            ((TimeZoneDetector) context.getSystemService(TimeZoneDetector.class)).suggestManualTimeZone(TimeZoneDetector.createManualTimeZoneSuggestion("Etc/GMT-6", "Settings: Set time zone"));
        }
    }

    public static void sendBroadcastToHuanji(Context context) {
        if (Settings.Secure.getInt(context.getContentResolver(), "xspace_enabled", 0) != 0) {
            Log.d("MiuiUtils", "send broadcast to huanji");
            try {
                Intent intent = new Intent();
                intent.setAction("com.miui.huanji.action.pre_boot");
                intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
                intent.addFlags(32);
                intent.setPackage("com.miui.huanji");
                context.sendBroadcastAsUser(intent, UserHandle.of(999));
            } catch (Exception unused) {
            }
        }
    }

    public static void sendBroadcastToTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("updatemiuix", 0);
        boolean z = sharedPreferences.getBoolean("miuix_updated", false);
        Log.d("MiuiUtils", "send broadcast to theme,sended status: " + z);
        if (z) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction("com.miui.action.APP_REPLACED_ACTION_FOR_THEME");
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        intent.setPackage("com.android.thememanager");
        intent.putExtra("android.intent.extra.PACKAGE_NAME", "com.android.settings");
        context.sendBroadcast(intent);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("miuix_updated", true);
        edit.commit();
    }

    public static void setDeviceName(Context context, String str) {
        SystemProperties.set(SystemSettings$System.PERSIST_SYS_DEVICE_NAME, str);
        setNetHostName(context);
    }

    public static void setNavigationBackground(Activity activity, boolean z) {
        if (activity == null) {
            return;
        }
        if (z) {
            activity.getWindow().addFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        } else {
            activity.getWindow().clearFlags(MiuiWindowManager$LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
        }
        activity.getWindow().addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
    }

    public static void setNetHostName(Context context) {
        String truncateByte;
        String str = SystemProperties.get("net.hostname");
        StringBuilder sb = new StringBuilder();
        sb.append(Build.MODEL);
        sb.append("-");
        Iterator<ChinesePinyinConverter.Token> it = ChinesePinyinConverter.getInstance(context).get(MiuiSettings.System.getDeviceName(context)).iterator();
        while (it.hasNext()) {
            sb.append(it.next().target);
        }
        String replace = sb.toString().replace(" ", "");
        if (replace.equals(str) || (truncateByte = Utf8TextUtils.truncateByte(replace, 20)) == null) {
            return;
        }
        SystemProperties.set("net.hostname", truncateByte);
    }

    public static void setP2pDeviceName(String str) {
        SystemProperties.set("persist.sys.p2p_local_name", str);
    }

    public static void setUsbCurrentFunction(Context context, String str, boolean z) {
        UsbManager usbManager = (UsbManager) context.getSystemService("usb");
        try {
            try {
                Method method = UsbManager.class.getMethod("setCurrentFunction", String.class);
                method.setAccessible(true);
                method.invoke(usbManager, str);
                Method method2 = UsbManager.class.getMethod("setUsbDataUnlocked", Boolean.TYPE);
                method2.setAccessible(true);
                method2.invoke(usbManager, Boolean.valueOf(z));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception unused) {
            Method method3 = UsbManager.class.getMethod("setCurrentFunction", String.class, Boolean.TYPE);
            method3.setAccessible(true);
            method3.invoke(usbManager, str, Boolean.valueOf(z));
        }
    }

    public static boolean shouldDisableAppTimer(Context context) {
        return false;
    }

    public static boolean shouldShowAiButton() {
        return miui.os.Build.IS_INTERNATIONAL_BUILD && FeatureParser.getBoolean("support_ai_task", false);
    }

    public static void startPreferencePanel(Activity activity, String str, Bundle bundle, int i, CharSequence charSequence, Fragment fragment, int i2) {
        if (activity instanceof MiuiSettings) {
            ((MiuiSettings) activity).startPreferencePanel(str, bundle, i, charSequence, fragment, i2);
        } else {
            new SubSettingLauncher(activity).setDestination(str).setTitleRes(i).setArguments(bundle).setResultListener(fragment, i2).launch();
        }
    }

    public static boolean supportAnimateCheck() {
        return FeatureParser.getBoolean("support_close_unlock_animator", false);
    }

    public static boolean supportPaperEyeCare() {
        return FeatureParser.getBoolean("support_paper_eyecare", false);
    }

    public static boolean supportSmartEyeCare() {
        return FeatureParser.getBoolean("support_smart_eyecare", false);
    }

    public static Bitmap toRoundCorner(Bitmap bitmap, int i, int i2, int i3, int i4, boolean z) {
        try {
            Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(createBitmap);
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, i, i2);
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(i4);
            float f = i3;
            canvas.drawRoundRect(rectF, f, f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            paint.setFilterBitmap(true);
            canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), rect, paint);
            if (z) {
                paint.setStrokeWidth(1.0f);
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(-16777216);
                paint.setAlpha(76);
                canvas.drawCircle(i / 2, i2 / 2, bitmap.getWidth() / 2, paint);
            }
            bitmap.recycle();
            return createBitmap;
        } catch (OutOfMemoryError unused) {
            return bitmap;
        }
    }

    public static void updateFragmentView(Activity activity, View view) {
        PreferenceFrameLayout.LayoutParams layoutParams = ((ViewGroup) view.getParent()).getLayoutParams();
        if (layoutParams instanceof PreferenceFrameLayout.LayoutParams) {
            layoutParams.removeBorders = true;
        }
        ViewGroup actionBarOverlayLayout = ActionBarUtils.getActionBarOverlayLayout(view);
        if ((TabletUtils.IS_TABLET && (activity instanceof MiuiSettings)) || actionBarOverlayLayout == null) {
            return;
        }
        actionBarOverlayLayout.setBackgroundResource(17170445);
    }

    private static void writeToFile(String str, String str2) {
        BufferedWriter bufferedWriter;
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                try {
                    bufferedWriter = new BufferedWriter(new FileWriter(str));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } catch (FileNotFoundException e2) {
                e = e2;
            } catch (IOException e3) {
                e = e3;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            bufferedWriter.write(str2);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e4) {
            e = e4;
            bufferedWriter2 = bufferedWriter;
            e.printStackTrace();
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
        } catch (IOException e5) {
            e = e5;
            bufferedWriter2 = bufferedWriter;
            e.printStackTrace();
            if (bufferedWriter2 != null) {
                bufferedWriter2.close();
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedWriter2 = bufferedWriter;
            if (bufferedWriter2 != null) {
                try {
                    bufferedWriter2.close();
                } catch (IOException e6) {
                    e6.printStackTrace();
                }
            }
            throw th;
        }
    }

    public void addSimLockPreference(PreferenceScreen preferenceScreen, String str) {
    }

    public boolean canFindActivity(Context context, Intent intent) {
        return intent.resolveActivityInfo(context.getPackageManager(), SearchUpdater.GOOGLE) != null;
    }

    public void connectToOtherWifi(Context context, int i) {
        int i2;
        int i3;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        wifiManager.disconnect();
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        List<ScanResult> scanResults = wifiManager.getScanResults();
        HashSet disableWifiAutoConnectSsid = MiuiSettings.System.getDisableWifiAutoConnectSsid(context);
        int i4 = -1;
        if (configuredNetworks != null && scanResults != null) {
            int i5 = -1;
            for (WifiConfiguration wifiConfiguration : configuredNetworks) {
                Iterator<ScanResult> it = scanResults.iterator();
                while (it.hasNext()) {
                    if (isTheSameWifi(context, wifiConfiguration, it.next()) && !disableWifiAutoConnectSsid.contains(wifiConfiguration.SSID) && (i2 = wifiConfiguration.priority) > i5 && (i3 = wifiConfiguration.networkId) != i) {
                        i5 = i2;
                        i4 = i3;
                    }
                }
            }
        }
        if (i4 >= 0) {
            wifiManager.enableNetwork(i4, true);
        }
    }

    public void enableTouchSensitive(Context context, boolean z) {
    }

    public int getAgpsRoaming(LocationManager locationManager) {
        return 0;
    }

    public Set<String> getHotSpotMacBlackSet(Context context) {
        return MiuiSettings.System.getHotSpotMacBlackSet(context);
    }

    public Method getMethod(Class cls, String str) {
        try {
            return cls.getMethod(str, InputDeviceIdentifier.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Phone getPhone(int i) {
        return PhoneFactory.getPhone(i);
    }

    public ArrayList<Integer> getSimSlotList(Context context) {
        return null;
    }

    public Comparator getSubscriptionInfoComparable() {
        return null;
    }

    public String getTetherDeviceChangedAction() {
        return "";
    }

    public String getViewLicenseAction() {
        return canFindActivity(AppGlobals.getInitialApplication(), new Intent(ExtraIntent.ACTION_VIEW_LICENSE)) ? ExtraIntent.ACTION_VIEW_LICENSE : ExtraIntent.ACTION_VIEW_MIUI_LICENSE;
    }

    public boolean getWifiStaSapConcurrency(Context context) {
        return true;
    }

    public boolean hasSimCard(Context context) {
        return isMultiSimSupported() ? miui.telephony.TelephonyManager.getDefault().getIccCardCount() > 0 : miui.telephony.TelephonyManager.getDefault().hasIccCard();
    }

    public boolean isMultiSimSupported() {
        return miui.telephony.TelephonyManager.getDefault().isMultiSimEnabled();
    }

    public boolean isSapBlacklistOffloadSupport(Context context) {
        try {
            return context.getResources().getBoolean(context.getResources().getIdentifier("config_hotspot_blacklist_offload", "bool", "android.miui"));
        } catch (Exception unused) {
            Log.e("MiuiUtils", "Get hotspot blacklist offload config exception");
            return false;
        }
    }

    public boolean isTheSameWifi(Context context, WifiConfiguration wifiConfiguration, ScanResult scanResult) {
        String str;
        String str2 = wifiConfiguration.SSID;
        return str2 != null && (str = scanResult.SSID) != null && str2.equals(AccessPoint.convertToQuotedString(str)) && AccessPoint.getSecurity(wifiConfiguration) == AccessPoint.getSecurity(context, scanResult);
    }

    public boolean isTouchSensitive(Context context) {
        return false;
    }

    public boolean isWpa3SoftApSupport(Context context) {
        MiuiWifiManager miuiWifiManager = (MiuiWifiManager) context.getSystemService("MiuiWifiService");
        if (miuiWifiManager != null) {
            return miuiWifiManager.isWpa3SaeSupported();
        }
        return false;
    }

    public void resetAutoConnectAp(Context context, WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration == null) {
            return;
        }
        AutoConnectUtils autoConnectUtils = AutoConnectUtils.getInstance(context);
        if (!autoConnectUtils.isAutoConnect(wifiConfiguration.SSID)) {
            autoConnectUtils.enableAutoConnect(context, wifiConfiguration.SSID, true);
        }
        autoConnectUtils.removeNoSecretWifi(context, wifiConfiguration.SSID);
    }

    public void setAgpsRoaming(LocationManager locationManager, int i) {
    }

    public void setHotSpotMacBlackSet(Context context, Set<String> set) {
        MiuiSettings.System.setHotSpotMacBlackSet(context, set);
    }

    public void setSpinnerAdapter(Context context, String[] strArr, Spinner spinner) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.miuix_appcompat_simple_spinner_layout_integrated, 16908308, strArr);
        arrayAdapter.setDropDownViewResource(R.layout.miuix_appcompat_simple_spinner_dropdown_item);
        spinner.setAdapter((SpinnerAdapter) arrayAdapter);
    }

    public void setSpinnerDisplayLocation(final Spinner spinner, final int i) {
        try {
            final ViewGroup viewGroup = (ViewGroup) spinner.getParent();
            if (viewGroup != null) {
                spinner.setClickable(false);
                spinner.setLongClickable(false);
                spinner.setContextClickable(false);
                spinner.setOnSpinnerDismissListener(new Spinner.OnSpinnerDismissListener() { // from class: com.android.settings.MiuiUtils.3
                    @Override // miuix.appcompat.widget.Spinner.OnSpinnerDismissListener
                    public void onSpinnerDismiss() {
                        Folme.useAt(viewGroup).touch().touchUp(new AnimConfig[0]);
                        viewGroup.setBackgroundColor(0);
                    }
                });
                viewGroup.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.MiuiUtils.4
                    @Override // android.view.View.OnTouchListener
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (spinner.isEnabled()) {
                            int action = motionEvent.getAction();
                            if (action == 0) {
                                Folme.useAt(view).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).touchDown(new AnimConfig[0]);
                                viewGroup.setBackgroundColor(i);
                            } else if (action == 1) {
                                spinner.performClick(motionEvent.getX(), motionEvent.getY());
                            } else if (action == 3) {
                                Folme.useAt(view).touch().touchUp(new AnimConfig[0]);
                                viewGroup.setBackgroundColor(0);
                            }
                            return true;
                        }
                        return false;
                    }
                });
            }
        } catch (Exception unused) {
        }
    }
}
