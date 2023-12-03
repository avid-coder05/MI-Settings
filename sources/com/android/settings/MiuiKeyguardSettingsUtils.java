package com.android.settings;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.faceunlock.MiuiFaceDataInput;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import miui.content.res.ThemeResources;
import miui.util.FeatureParser;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiKeyguardSettingsUtils {
    public static final boolean IS_FOLD;

    static {
        IS_FOLD = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2;
    }

    public static boolean getBooolExtra(Bundle bundle, Intent intent, String str) {
        return (bundle != null ? bundle.getBoolean(str, false) : false) || (intent != null ? intent.getBooleanExtra(str, false) : false);
    }

    public static int getEffectiveUserId(Context context, Bundle bundle) {
        return UserManager.get(context).getCredentialOwnerProfile(Utils.getUserIdFromBundle(context, bundle));
    }

    public static CharSequence getHeader(Intent intent) {
        if (intent != null) {
            CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.android.settings.ConfirmCredentials.header");
            return TextUtils.isEmpty(charSequenceExtra) ? intent.getCharSequenceExtra("com.android.settings.ConfirmLockPattern.header") : charSequenceExtra;
        }
        return null;
    }

    public static int getIntExtra(Bundle bundle, Intent intent, String str, int i) {
        int i2 = bundle != null ? bundle.getInt(str, i) : i;
        return i2 != i ? i2 : intent != null ? intent.getIntExtra(str, i) : i;
    }

    public static int getKeyguardNotificationStatus(Context context, ContentResolver contentResolver) {
        int i = 1;
        boolean z = Settings.Global.getInt(contentResolver, "new_device_after_support_notification_animation", 0) != 0;
        if ("perseus".equals(Build.DEVICE) || (z && isSupportAodAnimateDevice(context))) {
            i = 2;
        }
        return Settings.System.getInt(contentResolver, "wakeup_for_keyguard_notification", i);
    }

    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", ThemeResources.FRAMEWORK_PACKAGE);
        if (identifier > 0) {
            return context.getResources().getDimensionPixelSize(identifier);
        }
        return 0;
    }

    public static int getUserId(Context context, Bundle bundle) {
        return Utils.getUserIdFromBundle(context, bundle);
    }

    public static int getUserId(Bundle bundle, LockPatternUtils lockPatternUtils, Activity activity) {
        int identifier = Utils.getSecureTargetUser(activity.getActivityToken(), UserManager.get(activity), null, activity.getIntent().getExtras()).getIdentifier();
        if ("android.app.action.SET_NEW_PARENT_PROFILE_PASSWORD".equals(activity.getIntent().getAction()) || !lockPatternUtils.isSeparateProfileChallengeAllowed(identifier)) {
            if (bundle == null) {
                bundle = activity.getIntent().getExtras();
            }
            return Utils.getUserIdFromBundle(activity, bundle);
        }
        return identifier;
    }

    public static boolean instanceofSettingsPreFragment(Fragment fragment) {
        return (fragment instanceof MiuiSettingsPreferenceFragment) || (fragment instanceof FragmentResultCallBack);
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean isInFullWindowGestureMode(Context context) {
        return MiuiSettings.Global.getBoolean(context.getContentResolver(), "force_fsg_nav_bar");
    }

    public static boolean isManagedProfile(UserManager userManager, int i) {
        return userManager != null && userManager.isManagedProfile(i);
    }

    public static boolean isNightMode(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService("uimode");
        return uiModeManager != null && uiModeManager.getNightMode() == 2;
    }

    public static boolean isPad() {
        return FeatureParser.getBoolean("is_pad", false);
    }

    public static boolean isShowDialogToAddFace(Activity activity) {
        boolean isHardwareDetected = new FingerprintHelper(activity).isHardwareDetected();
        boolean hasEnrolledFaces = KeyguardSettingsFaceUnlockUtils.hasEnrolledFaces(activity);
        boolean isSupportFaceUnlock = KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(activity);
        boolean z = ("odin".equals(Build.DEVICE) || FeatureParser.getBoolean("support_tee_face_unlock", false)) ? false : true;
        return isHardwareDetected ? !hasEnrolledFaces && z && UserHandle.myUserId() == 0 && isSupportFaceUnlock : !hasEnrolledFaces && z && UserHandle.myUserId() == 0 && activity.getIntent().getBooleanExtra("add_keyguard_password_then_add_fingerprint", false) && isSupportFaceUnlock;
    }

    public static boolean isSupportAodAnimateDevice(Context context) {
        return FeatureParser.getBoolean("support_aod", false);
    }

    public static boolean isWakeupForNotification(Context context, ContentResolver contentResolver) {
        return getKeyguardNotificationStatus(context, contentResolver) == 1;
    }

    public static void onFragmentResult(Fragment fragment, int i, Bundle bundle) {
        if (fragment instanceof MiuiSettingsPreferenceFragment) {
            ((MiuiSettingsPreferenceFragment) fragment).onFragmentResult(i, bundle);
        } else if (fragment instanceof FragmentResultCallBack) {
            ((FragmentResultCallBack) fragment).onFragmentResult(i, bundle);
        }
    }

    public static void putExtraUserId(Intent intent, int i) {
        intent.putExtra("android.intent.extra.USER_ID", i);
    }

    public static void putKeyguardNotificationStatus(Context context, ContentResolver contentResolver, int i) {
        if (i == 2 && !isSupportAodAnimateDevice(context)) {
            i = 1;
        }
        Settings.System.putInt(contentResolver, "wakeup_for_keyguard_notification", i);
    }

    public static void saveUpdatepatternTime(Context context) {
        SharedPreferences.Editor edit = context.getSharedPreferences("pref_password_time_out", 0).edit();
        edit.putLong("pref_password_time_out_value", System.currentTimeMillis());
        edit.apply();
    }

    public static void setSettingsSplit(Intent intent) {
        if (isPad() || IS_FOLD) {
            intent.addMiuiFlags(4);
        }
    }

    public static void showDialogToAddFace(final Activity activity, final byte[] bArr, int i, final boolean z) {
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiKeyguardSettingsUtils.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                if (i2 != -1) {
                    if (z) {
                        dialogInterface.dismiss();
                        return;
                    }
                    activity.setResult(-1);
                    activity.finish();
                    return;
                }
                Intent intent = new Intent(activity, MiuiFaceDataInput.class);
                intent.putExtra("input_facedata_need_skip_password", true);
                byte[] bArr2 = bArr;
                if (bArr2 != null) {
                    intent.putExtra("hw_auth_token", bArr2);
                }
                MiuiKeyguardSettingsUtils.setSettingsSplit(intent);
                activity.startActivity(intent);
                activity.setResult(-1);
                activity.finish();
            }
        };
        new AlertDialog.Builder(activity, i).setCancelable(true).setTitle(R.string.new_fingerprint_to_new_face_dialog_tittle).setMessage(R.string.new_fingerprint_to_new_face_dialog_msg).setPositiveButton(R.string.new_password_to_new_fingerprint_dialog_positive_msg, onClickListener).setNegativeButton(R.string.new_password_to_new_fingerprint_dialog_negative_msg, onClickListener).setCancelable(false).create().show();
    }

    public static boolean showWaitTurnOffPassword(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "is_security_encryption_enabled", 0) > 0 && System.currentTimeMillis() - context.getSharedPreferences("pref_password_time_out", 0).getLong("pref_password_time_out_value", 0L) < 5000;
    }

    public static boolean startFragment(Fragment fragment, String str, int i, Bundle bundle, int i2) {
        FragmentActivity activity = fragment.getActivity();
        if (activity == null) {
            Log.w("MiuiKeyguardSettingsUtils", "startFragment error, activity is null");
            return false;
        } else if (activity instanceof SettingsActivity) {
            ((SettingsActivity) activity).startPreferencePanel(fragment, str, bundle, i2, null, fragment, i);
            return true;
        } else if (activity instanceof MiuiSettings) {
            ((MiuiSettings) activity).startPreferencePanel(str, bundle, i2, null, fragment, i);
            return true;
        } else if (activity instanceof PreferenceActivity) {
            ((PreferenceActivity) activity).startPreferencePanel(str, bundle, i2, null, fragment, i);
            return true;
        } else {
            Log.w("MiuiKeyguardSettingsUtils", "Parent isn't SettingsActivity nor PreferenceActivity, thus there's no way to launch the given Fragment (name: " + str + ", requestCode: " + i + ")");
            return false;
        }
    }
}
