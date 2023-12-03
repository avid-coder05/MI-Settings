package com.android.settings.device.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.app.PlatLogoActivity;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.content.res.ThemeResources;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiFirmwareVersionController extends BaseDeviceInfoController {
    private static final String LOG_TAG = "MiuiFirmwareVersionController";
    private final String KEY_FIRMWARE_VERSION;
    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private long[] mHits;
    private UserManager mUm;

    public MiuiFirmwareVersionController(Context context) {
        super(context);
        this.KEY_FIRMWARE_VERSION = "firmware_version";
        this.mHits = new long[3];
        this.mUm = (UserManager) context.getApplicationContext().getSystemService("user");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
        if (valuePreference != null) {
            if (Build.IS_CU_CUSTOMIZATION_TEST || !Build.IS_INTERNATIONAL_BUILD) {
                setValueSummary(valuePreference, Build.VERSION.RELEASE);
                return;
            }
            setValueSummary(valuePreference, Build.VERSION.RELEASE + " " + android.os.Build.ID);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "firmware_version";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(getPreferenceKey(), preference.getKey())) {
            long[] jArr = this.mHits;
            System.arraycopy(jArr, 1, jArr, 0, jArr.length - 1);
            long[] jArr2 = this.mHits;
            jArr2[jArr2.length - 1] = SystemClock.uptimeMillis();
            if (this.mHits[0] >= SystemClock.uptimeMillis() - 500) {
                if (this.mUm.hasUserRestriction("no_fun")) {
                    RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mFunDisallowedAdmin;
                    if (enforcedAdmin != null && !this.mFunDisallowedBySystem) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, enforcedAdmin);
                    }
                    Log.d(LOG_TAG, "Sorry, no fun for you!");
                    return false;
                }
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName(ThemeResources.FRAMEWORK_PACKAGE, PlatLogoActivity.class.getName());
                try {
                    this.mContext.startActivity(intent);
                } catch (Exception unused) {
                    Log.e(LOG_TAG, "Unable to start activity " + intent.toString());
                }
            }
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_fun", UserHandle.myUserId());
        this.mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_fun", UserHandle.myUserId());
    }
}
