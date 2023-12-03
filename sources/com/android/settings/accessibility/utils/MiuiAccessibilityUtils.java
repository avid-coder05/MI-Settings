package com.android.settings.accessibility.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserManager;
import android.provider.Settings;
import android.speech.tts.TtsEngines;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import com.android.settings.MiuiUtils;
import com.android.settings.accessibility.MiuiAccessibilityAsrController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.accessibility.AccessibilityUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes.dex */
public final class MiuiAccessibilityUtils {
    private static final IWindowManager mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    private static final int mUserId = ActivityManager.getCurrentUser();
    private static final IAccessibilityManager mAccessibilityManager = IAccessibilityManager.Stub.asInterface(ServiceManager.getService("accessibility"));

    public static void enableAccessibility(Context context) {
        boolean z;
        List<AccessibilityServiceInfo> installedSpeakingAccessibilityServices = getInstalledSpeakingAccessibilityServices(context);
        UserManager userManager = (UserManager) context.getSystemService("user");
        ContentResolver contentResolver = context.getContentResolver();
        if (installedSpeakingAccessibilityServices.isEmpty()) {
            return;
        }
        try {
            z = mWindowManager.isKeyguardLocked();
        } catch (RemoteException e) {
            Log.w("MiuiAccessibilityUtils", "get isKeyguardLocked", e);
            z = false;
        }
        boolean z2 = userManager.getUsers().size() > 1;
        AccessibilityServiceInfo accessibilityServiceInfo = installedSpeakingAccessibilityServices.get(0);
        boolean z3 = (accessibilityServiceInfo.flags & 4) != 0;
        if (!z3) {
            int size = installedSpeakingAccessibilityServices.size();
            int i = 1;
            while (true) {
                if (i >= size) {
                    break;
                }
                AccessibilityServiceInfo accessibilityServiceInfo2 = installedSpeakingAccessibilityServices.get(i);
                if ((accessibilityServiceInfo2.flags & 4) != 0) {
                    z3 = true;
                    accessibilityServiceInfo = accessibilityServiceInfo2;
                    break;
                }
                i++;
            }
        }
        ServiceInfo serviceInfo = accessibilityServiceInfo.getResolveInfo().serviceInfo;
        ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
        if (z && z2) {
            if (z) {
                try {
                    mAccessibilityManager.temporaryEnableAccessibilityStateUntilKeyguardRemoved(componentName, z3);
                    return;
                } catch (RemoteException e2) {
                    Log.w("MiuiAccessibilityUtils", "when temporaryEnableAccessibilityStateUntilKeyguardRemoved", e2);
                    return;
                }
            }
            return;
        }
        String flattenToString = componentName.flattenToString();
        int i2 = mUserId;
        Settings.Secure.putStringForUser(contentResolver, "enabled_accessibility_services", flattenToString, i2);
        Settings.Secure.putStringForUser(contentResolver, "touch_exploration_granted_accessibility_services", flattenToString, i2);
        if (z3) {
            Settings.Secure.putIntForUser(contentResolver, "touch_exploration_enabled", 1, i2);
        }
        Settings.Secure.putIntForUser(contentResolver, "accessibility_script_injection", 1, i2);
        Settings.Secure.putString(contentResolver, "tts_default_synth", new TtsEngines(context).getDefaultEngine());
        Settings.Secure.putIntForUser(contentResolver, "accessibility_enabled", 1, i2);
    }

    private static List<AccessibilityServiceInfo> getInstalledSpeakingAccessibilityServices(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(AccessibilityManager.getInstance(context).getInstalledAccessibilityServiceList());
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            if ((((AccessibilityServiceInfo) it.next()).feedbackType & 1) == 0) {
                it.remove();
            }
        }
        return arrayList;
    }

    public static boolean hideAllMiuiAccessibilityService(Context context) {
        return (Build.IS_INTERNATIONAL_BUILD && !MiuiUtils.isPackagesSupportMetaDataFeature(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, context, "com.miui.accessibility.adapt_international")) || (SettingsFeatures.isSplitTabletDevice() && !MiuiUtils.isPackagesSupportMetaDataFeature(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, context, "com.miui.accessibility.adapt_pad"));
    }

    public static boolean isAccessibilityServiceOn(Context context, ComponentName componentName) {
        return AccessibilityUtils.getEnabledServicesFromSettings(context).contains(componentName);
    }

    public static boolean isHapticOn(Context context) {
        return AccessibilityUtils.getEnabledServicesFromSettings(context).contains(ComponentName.unflattenFromString("com.miui.accessibility/com.miui.accessibility.haptic.HapticAccessibilityService"));
    }

    public static boolean isRemoveScreenReaderVibrator(Context context) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "is_remove_screen_reader_vibrator", 1, -2) == 1;
    }

    public static boolean isServiceInstalled(Context context, String str) {
        AccessibilityManager accessibilityManager;
        AccessibilityServiceInfo installedServiceInfoWithComponentName;
        if (TextUtils.isEmpty(str) || (installedServiceInfoWithComponentName = (accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility")).getInstalledServiceInfoWithComponentName(ComponentName.unflattenFromString(str))) == null) {
            return false;
        }
        return accessibilityManager.getInstalledAccessibilityServiceList().contains(installedServiceInfoWithComponentName);
    }

    public static boolean isTallBackActive(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        return accessibilityManager.isEnabled() && accessibilityManager.isTouchExplorationEnabled();
    }
}
