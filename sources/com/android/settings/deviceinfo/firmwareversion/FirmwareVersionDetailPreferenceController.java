package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.app.PlatLogoActivity;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.slices.Sliceable;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class FirmwareVersionDetailPreferenceController extends BasePreferenceController {
    private static final int ACTIVITY_TRIGGER_COUNT = 3;
    private static final int DELAY_TIMER_MILLIS = 500;
    private static final String TAG = "firmwareDialogCtrl";
    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private final long[] mHits;
    private final UserManager mUserManager;

    public FirmwareVersionDetailPreferenceController(Context context, String str) {
        super(context, str);
        this.mHits = new long[3];
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        initializeAdminPermissions();
    }

    void arrayCopy() {
        long[] jArr = this.mHits;
        System.arraycopy(jArr, 1, jArr, 0, jArr.length - 1);
    }

    @Override // com.android.settings.slices.Sliceable
    public void copy() {
        Sliceable.setCopyContent(this.mContext, getSummary(), this.mContext.getText(R.string.firmware_version));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        return Build.VERSION.RELEASE_OR_CODENAME;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey()) && !Utils.isMonkeyRunning()) {
            arrayCopy();
            long[] jArr = this.mHits;
            jArr[jArr.length - 1] = SystemClock.uptimeMillis();
            if (this.mHits[0] >= SystemClock.uptimeMillis() - 500) {
                if (this.mUserManager.hasUserRestriction("no_fun")) {
                    RestrictedLockUtils.EnforcedAdmin enforcedAdmin = this.mFunDisallowedAdmin;
                    if (enforcedAdmin != null && !this.mFunDisallowedBySystem) {
                        RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, enforcedAdmin);
                    }
                    Log.d(TAG, "Sorry, no fun for you!");
                    return true;
                }
                Intent className = new Intent("android.intent.action.MAIN").setClassName(ThemeResources.FRAMEWORK_PACKAGE, PlatLogoActivity.class.getName());
                try {
                    this.mContext.startActivity(className);
                } catch (Exception unused) {
                    Log.e(TAG, "Unable to start activity " + className.toString());
                }
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    void initializeAdminPermissions() {
        this.mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_fun", UserHandle.myUserId());
        this.mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_fun", UserHandle.myUserId());
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public boolean useDynamicSliceSummary() {
        return true;
    }
}
