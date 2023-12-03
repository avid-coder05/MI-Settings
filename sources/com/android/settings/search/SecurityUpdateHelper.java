package com.android.settings.search;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.os.UserHandle;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.FingerprintHelper;
import com.android.settings.R;
import com.android.settings.faceunlock.KeyguardSettingsFaceUnlockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
class SecurityUpdateHelper extends BaseSearchUpdateHelper {
    private static final String ADD_FACE_RECOGINITION_RESOURCE = "add_facerecoginition_text";
    private static final String ADD_FINGERPRINT_RESOURCE = "add_fingerprint_text";
    private static final String BLUETOOTH_UNLOCK_RESOURCE = "bluetooth_unlock_title";
    private static final String CREDENTIALS_RESET_RESOURCE = "credentials_reset";
    private static final String LOCK_RESOURCE = "lock_settings";
    private static final String LOCK_RESOURCE_WITH_FINGERPRINT_RESOURCE = "lock_settings_with_fingerprint";
    private static final String NEW_ENCRYPTION_RESOURCE = "security_encryption_title";
    private static final String OLD_ENCRYPTION_RESOURCE = "crypt_keeper_encrypt_title";
    private static final String PALM_ENABLED_RESOURCE = "palm_enabled";
    private static final String SENSOR_PROXIMITY_RESOURCE = "screen_on_proximity_sensor_title";
    private static final String SMARTCOVER_RESOURCE = "smartcover_lock_or_unlock_screen_tittle";
    private static final String SUSPEND_GESTURE_RESOURCE = "suspend_gesture_enabled";
    private static final String TRUSTED_CREDENTIALS_RESOURCE = "trusted_credentials";

    SecurityUpdateHelper() {
    }

    private static boolean isEllipticProximity(Context context) {
        return Build.VERSION.SDK_INT >= 28 ? SystemProperties.getBoolean("ro.vendor.audio.us.proximity", false) : SystemProperties.getBoolean("ro.audio.us.proximity", false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void update(Context context, ArrayList<ContentProviderOperation> arrayList) {
        if (new FingerprintHelper(context).isHardwareDetected()) {
            Iterator<String> it = BaseSearchUpdateHelper.getIdWithResource(context, LOCK_RESOURCE).iterator();
            while (it.hasNext()) {
                BaseSearchUpdateHelper.updateItemData(context, arrayList, it.next(), "name", context.getResources().getString(R.string.lock_settings_with_fingerprint));
            }
            BaseSearchUpdateHelper.updatePath(context, arrayList, LOCK_RESOURCE, LOCK_RESOURCE_WITH_FINGERPRINT_RESOURCE);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, ADD_FINGERPRINT_RESOURCE);
        }
        if (!LockPatternUtils.isDeviceEncryptionEnabled()) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, NEW_ENCRYPTION_RESOURCE);
        } else if ("file".equals(SystemProperties.get("ro.crypto.type"))) {
            BaseSearchUpdateHelper.disableByResource(context, arrayList, OLD_ENCRYPTION_RESOURCE, true);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, NEW_ENCRYPTION_RESOURCE);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, OLD_ENCRYPTION_RESOURCE);
        }
        if (!Build.DEVICE.equals("centaur")) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, PALM_ENABLED_RESOURCE);
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SUSPEND_GESTURE_RESOURCE);
        }
        if (FeatureParser.getBoolean("support_hall_sensor", false)) {
            List<String> idWithResource = BaseSearchUpdateHelper.getIdWithResource(context, SMARTCOVER_RESOURCE);
            String str = FeatureParser.getBoolean("support_multiple_small_win_cover", false) ? idWithResource.get(1) : idWithResource.get(0);
            BaseSearchUpdateHelper.updateItemAdditionalData(context, arrayList, str, BaseSearchUpdateHelper.getAdditionalSettingsValue(context, str) | 1);
        } else {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SMARTCOVER_RESOURCE);
        }
        if (!context.getPackageManager().hasSystemFeature("android.hardware.sensor.proximity") || isEllipticProximity(context)) {
            BaseSearchUpdateHelper.hideByResource(context, arrayList, SENSOR_PROXIMITY_RESOURCE);
        }
        String str2 = BaseSearchUpdateHelper.getIdWithResource(context, "bluetooth_unlock_title").get(0);
        BaseSearchUpdateHelper.updateItemAdditionalData(context, arrayList, str2, BaseSearchUpdateHelper.getAdditionalSettingsValue(context, str2) | 2);
        boolean z = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_credentials", UserHandle.myUserId()) != null;
        BaseSearchUpdateHelper.disableByResource(context, arrayList, TRUSTED_CREDENTIALS_RESOURCE, z);
        BaseSearchUpdateHelper.disableByResource(context, arrayList, CREDENTIALS_RESET_RESOURCE, z);
        if (KeyguardSettingsFaceUnlockUtils.isSupportFaceUnlock(context) && UserHandle.myUserId() == 0) {
            return;
        }
        BaseSearchUpdateHelper.hideByResource(context, arrayList, ADD_FACE_RECOGINITION_RESOURCE);
    }
}
