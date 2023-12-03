package com.android.settings.device.controller;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.util.ToastUtil;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiVersionController extends BaseDeviceInfoController {
    private RestrictedLockUtils.EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
    private boolean mDebuggingFeaturesDisallowedBySystem;
    private int mDevHitCountdown;
    private UserManager mUm;

    public MiuiVersionController(Context context) {
        super(context);
        this.mUm = (UserManager) context.getApplicationContext().getSystemService("user");
    }

    private void onMiuiVersionClicked() {
        ComponentName deviceOwnerComponent;
        if (Utils.isMonkeyRunning() || MiuiUtils.isSecondSpace(this.mContext)) {
            return;
        }
        if (this.mUm.isAdminUser() || this.mUm.isDemoUser()) {
            if (this.mUm.hasUserRestriction("no_debugging_features")) {
                if (!Build.IS_INTERNATIONAL_BUILD && this.mUm.isDemoUser() && (deviceOwnerComponent = Utils.getDeviceOwnerComponent(this.mContext)) != null) {
                    Intent action = new Intent().setPackage(deviceOwnerComponent.getPackageName()).setAction("com.android.settings.action.REQUEST_DEBUG_FEATURES");
                    if (this.mContext.getPackageManager().resolveActivity(action, 0) != null) {
                        this.mContext.startActivity(action);
                        return;
                    }
                }
                RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mDebuggingFeaturesDisallowedAdmin;
                if (enforcedAdmin == null || this.mDebuggingFeaturesDisallowedBySystem) {
                    return;
                }
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, enforcedAdmin);
                return;
            }
            int i = this.mDevHitCountdown;
            if (i <= 0) {
                if (i < 0) {
                    ToastUtil.show(this.mContext, R.string.show_dev_already, 1);
                    return;
                }
                return;
            }
            int i2 = i - 1;
            this.mDevHitCountdown = i2;
            if (i2 == 0) {
                DevelopmentSettingsEnabler.setDevelopmentSettingsEnabled(this.mContext, true);
                ToastUtil.show(this.mContext, R.string.show_dev_on, 1);
                Intent intent = new Intent();
                intent.setAction("com.android.settings.action.DEV_OPEN");
                intent.putExtra("show", true);
                this.mContext.sendBroadcast(intent);
            } else if (i2 <= 0 || i2 >= 5) {
            } else {
                Context context = this.mContext;
                Resources resources = context.getResources();
                int i3 = R.plurals.show_dev_countdown;
                int i4 = this.mDevHitCountdown;
                ToastUtil.show(context, resources.getQuantityString(i3, i4, Integer.valueOf(i4)), 0);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
        if (valuePreference != null) {
            setValueSummary(valuePreference, MiuiAboutPhoneUtils.getMiuiVersion(this.mContext));
            if (Build.IS_INTERNATIONAL_BUILD) {
                setPreferenceTitle(valuePreference, MiuiAboutPhoneUtils.getInstance(this.mContext).isPocoDevice() ? this.mContext.getResources().getString(R.string.device_miui_version_for_POCO) : this.mContext.getResources().getString(R.string.device_miui_version));
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_miui_version";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            onMiuiVersionClicked();
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return super.isAvailable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_debugging_features", UserHandle.myUserId());
        this.mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_debugging_features", UserHandle.myUserId());
        this.mDevHitCountdown = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext) ? -1 : 7;
    }
}
