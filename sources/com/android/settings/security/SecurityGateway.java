package com.android.settings.security;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import com.android.settings.FingerprintManageSetting;
import com.android.settings.GxzwNewFingerprintFragment;
import com.android.settings.MiuiCryptKeeperSettings;
import com.android.settings.MiuiSecurityBluetoothDeviceInfoFragment;
import com.android.settings.MiuiSecurityBluetoothMatchDeviceFragment;
import com.android.settings.MiuiSecurityChooseUnlock;
import com.android.settings.bluetooth.DevicePickerFragment;
import com.android.settings.bluetooth.MiuiDevicePickerFragment;
import com.android.settings.faceunlock.MiuiFaceDataManage;
import com.android.settings.faceunlock.MiuiNormalCameraMultiFaceInput;
import com.android.settings.privacypassword.PrivacyPasswordSetting;
import java.lang.reflect.Field;

/* loaded from: classes2.dex */
public class SecurityGateway {
    public static final String[] ENTRY_FRAGMENTS = {GxzwNewFingerprintFragment.class.getName(), MiuiSecurityChooseUnlock.MiuiSecurityChooseUnlockFragment.class.getName(), MiuiNormalCameraMultiFaceInput.NewMultiFaceEnrollFragment.class.getName(), PrivacyPasswordSetting.PrivacyPasswordSettingFragment.class.getName(), FingerprintManageSetting.FingerprintManageFragment.class.getName(), MiuiFaceDataManage.FaceManageFragment.class.getName(), DevicePickerFragment.class.getName(), MiuiDevicePickerFragment.class.getName(), MiuiSecurityBluetoothMatchDeviceFragment.class.getName(), MiuiSecurityBluetoothDeviceInfoFragment.class.getName(), MiuiCryptKeeperSettings.class.getName()};
    public static final String[] ENTRY_3RD_PACKAGENAME = {"com.android.soundrecorder", "com.mi.health"};

    private static boolean isCredible3rdApp(String str) {
        int i = 0;
        while (true) {
            String[] strArr = ENTRY_3RD_PACKAGENAME;
            if (i >= strArr.length) {
                return false;
            }
            if (strArr[i].equals(str)) {
                return true;
            }
            i++;
        }
    }

    public static boolean isSecurityFragment(String str) {
        int i = 0;
        while (true) {
            String[] strArr = ENTRY_FRAGMENTS;
            if (i >= strArr.length) {
                return true;
            }
            if (strArr[i].equals(str)) {
                return false;
            }
            i++;
        }
    }

    private static boolean isSystemApp(Context context, String str) {
        ApplicationInfo applicationInfo;
        if (!TextUtils.isEmpty(str) && context != null) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(str, 0);
                if (packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null) {
                    return false;
                }
                return (applicationInfo.flags & 1) != 0;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Boolean isSystemOrCredibleApp(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return Boolean.TRUE;
        }
        return Boolean.valueOf(isSystemApp(context, str) || isCredible3rdApp(str));
    }

    public static String reflectGetAppReferrer(Activity activity) {
        try {
            Field declaredField = Class.forName("android.app.Activity").getDeclaredField("mReferrer");
            declaredField.setAccessible(true);
            return (String) declaredField.get(activity);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
