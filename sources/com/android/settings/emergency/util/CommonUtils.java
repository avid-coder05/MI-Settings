package com.android.settings.emergency.util;

import android.app.MiuiStatusBarManager;
import android.app.MiuiStatusBarState;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.core.content.FileProvider;
import com.android.settings.R;
import com.android.settings.cloud.util.ReflectUtil;
import com.android.settings.emergency.ui.SosExitAlertActivity;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import miui.app.constants.ThemeManagerConstants;
import miui.os.Build;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;

/* loaded from: classes.dex */
public class CommonUtils {
    public static boolean ensureDirs(String str) {
        File parentFile = new File(str).getParentFile();
        return parentFile.exists() || parentFile.mkdirs();
    }

    public static SubscriptionInfo getCurrentEnableSubInfo() {
        List<SubscriptionInfo> subscriptionInfoList = SubscriptionManager.getDefault().getSubscriptionInfoList();
        if (subscriptionInfoList != null && !subscriptionInfoList.isEmpty()) {
            SubscriptionInfo subscriptionInfoForSlot = SubscriptionManager.getDefault().getSubscriptionInfoForSlot(SubscriptionManager.getDefault().getDefaultVoiceSlotId());
            if (subscriptionInfoForSlot != null && subscriptionInfoForSlot.isActivated()) {
                return subscriptionInfoForSlot;
            }
            for (SubscriptionInfo subscriptionInfo : subscriptionInfoList) {
                if (subscriptionInfo.isActivated()) {
                    return subscriptionInfo;
                }
            }
        }
        return null;
    }

    public static String getFormatTime(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(j));
    }

    public static Uri getOutputMediaFileUri(Context context, String str, String str2) {
        File file = new File(str2);
        if (!file.exists()) {
            Log.i("SOS-CommonUtils", "getOutputMediaFileUri: file is not exist !");
            return null;
        }
        try {
            Uri uriForFile = FileProvider.getUriForFile(context, "com.android.settings.files", file);
            context.grantUriPermission(str, uriForFile, 3);
            return uriForFile;
        } catch (Exception e) {
            Log.e("SOS-CommonUtils", "getOutputMediaFileUri: e = " + e.toString());
            return null;
        }
    }

    public static boolean isMobileDataEnable(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        if (telephonyManager == null) {
            Log.d("SOS-CommonUtils", "getMobileDataEnabled()- remote exception retVal=false");
            return false;
        }
        int defaultDataSubscriptionId = SubscriptionManager.getDefault().getDefaultDataSubscriptionId();
        Log.d("SOS-CommonUtils", "getMobileDataEnabled()+ subId=" + defaultDataSubscriptionId);
        boolean isDataEnabled = telephonyManager.createForSubscriptionId(defaultDataSubscriptionId).isDataEnabled();
        Log.d("SOS-CommonUtils", "getMobileDataEnabled()- subId=" + defaultDataSubscriptionId + " retVal=" + isDataEnabled);
        return isDataEnabled;
    }

    public static boolean isPreLoadGoogleCsp() {
        return SystemProperties.getBoolean("ro.miui.google.csp", false);
    }

    public static boolean isSosNewFeatureSupport(Context context) {
        if ("com.android.mms".equals(Telephony.Sms.getDefaultSmsPackage(context))) {
            if (Build.IS_INTERNATIONAL_BUILD) {
                return "taoyao".equals(android.os.Build.DEVICE);
            }
            return true;
        }
        return false;
    }

    private static void sendGoogleTextMessage(String str, String str2, PendingIntent pendingIntent, int i) {
        try {
            Class<?> cls = Class.forName("android.telephony.SmsManager");
            Method method = cls.getMethod("getSmsManagerForSubscriptionId", Integer.TYPE);
            method.setAccessible(true);
            Object invoke = method.invoke(null, Integer.valueOf(SubscriptionManager.getDefault().getSubscriptionIdForSlot(i)));
            Method method2 = cls.getMethod("divideMessage", String.class);
            method2.setAccessible(true);
            ArrayList arrayList = (ArrayList) method2.invoke(invoke, str2);
            Method method3 = cls.getMethod("sendMultipartTextMessage", String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class);
            method3.setAccessible(true);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(pendingIntent);
            method3.invoke(invoke, str, null, arrayList, arrayList2, null);
        } catch (Exception e) {
            Log.e("SOS-CommonUtils", "exception when sendGoogleTextMessage : ", e);
        }
    }

    public static void sendTextMessage(String str, String str2, PendingIntent pendingIntent, int i) {
        if (isPreLoadGoogleCsp()) {
            Log.e("SOS-CommonUtils", "use google csp");
            sendGoogleTextMessage(str, str2, pendingIntent, i);
            return;
        }
        try {
            Class<?> cls = Class.forName("miui.telephony.SmsManager");
            Method method = cls.getMethod("getDefault", Integer.TYPE);
            method.setAccessible(true);
            Object invoke = method.invoke(null, Integer.valueOf(i));
            Method method2 = cls.getMethod("divideMessage", String.class);
            method2.setAccessible(true);
            ArrayList arrayList = (ArrayList) method2.invoke(invoke, str2);
            Method method3 = cls.getMethod("sendMultipartTextMessage", String.class, String.class, ArrayList.class, ArrayList.class, ArrayList.class);
            method3.setAccessible(true);
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(pendingIntent);
            method3.invoke(invoke, str, null, arrayList, arrayList2, null);
        } catch (Exception e) {
            Log.e("SOS-CommonUtils", "exception when sendTextMessage : ", e);
        }
    }

    private static void setSosStatusBar(Context context, boolean z) {
        ((StatusBarManager) context.getSystemService(ThemeManagerConstants.COMPONENT_CODE_STATUSBAR)).setStatus(z ? 1 : 0, "com.miui.app.ExtraStatusBarManager.action_status_sos", null);
    }

    private static void setSosStatusBarAdaptR(Context context, boolean z) {
        if (!z) {
            MiuiStatusBarManager.clearState(context, "action_status_sos");
            return;
        }
        Intent intent = new Intent();
        intent.setClass(context, SosExitAlertActivity.class);
        PendingIntent activity = PendingIntent.getActivity(context, 0, intent, 335544320);
        MiuiStatusBarState.MiniStateViewBuilder miniStateViewBuilder = new MiuiStatusBarState.MiniStateViewBuilder(context);
        MiuiStatusBarManager.applyState(context, new MiuiStatusBarState("action_status_sos", miniStateViewBuilder.setPendingIntent(activity).setBackgroundColor(context.getResources().getColor(R.color.sos_status_bar_bg_adapt_r)).setTitle(context.getResources().getString(R.string.miui_sos_launching_title)).build(), miniStateViewBuilder.build(), 3));
    }

    public static void setSosStatusBarVisibility(Context context, boolean z) {
        try {
            int i = MiuiStatusBarState.PROMPT_VERSION;
            if (((Integer) ReflectUtil.getStaticObjectField(MiuiStatusBarState.class, "PROMPT_VERSION")).intValue() == 2) {
                setSosStatusBarAdaptR(context, z);
            } else {
                setSosStatusBar(context, z);
            }
        } catch (Exception e) {
            Log.e("SOS-CommonUtils", "exception when setSosStatusBarVisibility : ", e);
            setSosStatusBar(context, z);
        }
    }
}
