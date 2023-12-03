package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.UserHandle;
import com.android.settings.FingerprintHelper;
import com.android.settings.KeyguardAdvancedSettings;
import com.android.settings.MiuiChangeScreenLockPreferenceController;
import com.android.settings.MiuiKeyguardSettingsUtils;
import com.android.settings.MiuiLockUnificationPreferenceController;
import com.android.settings.MiuiSecuritySettings;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.MiuiGxzwUtils;
import com.android.settingslib.search.SettingsTree;
import miui.securityspace.CrossUserUtils;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class LockScreenSettingsTree extends SettingsTree {
    private Context mContext;

    protected LockScreenSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mContext = context;
    }

    public Intent getIntent() {
        return "lockscreen_magazine".equals(getColumnValue("resource")) ? MiuiSecuritySettings.getWallpaperIntent(this.mContext) : super.getIntent();
    }

    protected int getStatus() {
        boolean z;
        String columnValue = getColumnValue("resource");
        if ("lock_settings".equals(columnValue)) {
            if (new FingerprintHelper(this.mContext).isHardwareDetected()) {
                setColumnValue("resource", "lock_settings_with_fingerprint");
            }
        } else if ("lock_settings_with_fingerprint".equals(columnValue)) {
            if (!new FingerprintHelper(this.mContext).isHardwareDetected()) {
                setColumnValue("resource", "lock_settings");
            }
        } else if ("add_fingerprint_text".equals(columnValue)) {
            if (!new FingerprintHelper(this.mContext).isHardwareDetected()) {
                return 0;
            }
        } else if ("add_facerecoginition_text".equals(columnValue) || "manage_facerecoginition_text".equals(columnValue)) {
            if (KeyguardSettingsFaceUnlockUtils.hasEnrolledFaces(this.mContext)) {
                setColumnValue("resource", "manage_facerecoginition_text");
            } else {
                setColumnValue("resource", "add_facerecoginition_text");
            }
        } else if ("secure_keyguard_business_title".equals(columnValue)) {
            if (!CrossUserUtils.isAirSpace(this.mContext, UserHandle.myUserId())) {
                return 0;
            }
        } else if ("lockscreen_magazine".equals(columnValue)) {
            if (!MiuiSecuritySettings.isLockScreenMagazineAvailable(this.mContext)) {
                return 0;
            }
        } else if ("screen_on_proximity_sensor_title".equals(columnValue)) {
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") || KeyguardAdvancedSettings.isEllipticProximity(this.mContext)) {
                return 0;
            }
        } else if (MiuiSettingsTree.AOD_NOTIFICATION_STYLE.equals(columnValue)) {
            if (!MiuiKeyguardSettingsUtils.isSupportAodAnimateDevice(this.mContext)) {
                return 0;
            }
        } else if (MiuiSecurityAndPrivacySettingsTree.LOCK_SETTINGS_PROFILE_UNIFICATION_TITLE.equals(columnValue)) {
            if (!new MiuiLockUnificationPreferenceController(this.mContext).isAvailable()) {
                return 0;
            }
        } else if (MiuiSecurityAndPrivacySettingsTree.UNLOCK_SET_UNLOCK_LAUNCH_PICKER_TITLE_PROFILE.equals(columnValue)) {
            if (!new MiuiChangeScreenLockPreferenceController(this.mContext).isAvailable()) {
                return 0;
            }
        } else if ("smartcover_lock_or_unlock_screen_tittle".equals(columnValue)) {
            this.mContext.getResources();
            int identifier = Resources.getSystem().getIdentifier("config_smartCoverEnabled", "bool", "android.miui");
            if (identifier > 0) {
                this.mContext.getResources();
                z = Resources.getSystem().getBoolean(identifier);
            } else {
                z = false;
            }
            if (!z) {
                return 0;
            }
        }
        return super.getStatus();
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if ("suspend_gesture_enabled".equals(columnValue) || "palm_enabled".equals(columnValue)) {
            if (!Build.DEVICE.equals("centaur")) {
                return true;
            }
        } else if ("keyguard_advance_setting_title".equals(columnValue)) {
            setColumnValue(FunctionColumns.FRAGMENT, KeyguardAdvancedSettings.class.getName());
        } else if ("add_facerecoginition_text".equals(columnValue)) {
            if (!KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(this.mContext)) {
                return true;
            }
        } else if ("gxzw_lowlight_open_title".equals(columnValue) && !MiuiGxzwUtils.isSupportLowlight()) {
            return true;
        }
        return super.initialize();
    }
}
