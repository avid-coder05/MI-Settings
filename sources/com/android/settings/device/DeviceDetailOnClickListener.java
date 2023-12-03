package com.android.settings.device;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import com.android.internal.app.PlatLogoActivity;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.util.ToastUtil;
import miui.content.res.ThemeResources;
import miui.os.Build;

/* loaded from: classes.dex */
public class DeviceDetailOnClickListener implements View.OnClickListener {
    private Context mContext;
    private RestrictedLockUtils.EnforcedAdmin mDebuggingFeaturesDisallowedAdmin;
    private boolean mDebuggingFeaturesDisallowedBySystem;
    private int mDevHitCountdown;
    private RestrictedLockUtils.EnforcedAdmin mFunDisallowedAdmin;
    private boolean mFunDisallowedBySystem;
    private String mLastHitKey;
    private UserManager mUm;
    private int mPrefHitCountdown = 4;
    private long mLastPrefHitTime = 0;
    private long[] mHits = new long[3];
    private int mKernelHitCountdown = 4;
    private long mLastKernelHitTime = 0;
    private int mMemoryHitCountdown = 4;
    private long mLastMemoryHitTime = 0;

    public DeviceDetailOnClickListener(Context context) {
        this.mContext = context;
        this.mUm = (UserManager) context.getApplicationContext().getSystemService("user");
        this.mDebuggingFeaturesDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_debugging_features", UserHandle.myUserId());
        this.mDebuggingFeaturesDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_debugging_features", UserHandle.myUserId());
        this.mDevHitCountdown = DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(this.mContext) ? -1 : 7;
        this.mFunDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_fun", UserHandle.myUserId());
        this.mFunDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_fun", UserHandle.myUserId());
    }

    private void dealCpuClick(String str) {
        String str2;
        if (!"cpu_item".equals(str) || (str2 = this.mLastHitKey) == null || !str2.equals(str)) {
            this.mPrefHitCountdown = 4;
        } else if (SystemClock.elapsedRealtime() - this.mLastPrefHitTime > 3000) {
            int i = this.mPrefHitCountdown - 1;
            this.mPrefHitCountdown = i;
            if (i > 0) {
                Context context = this.mContext;
                Resources resources = context.getResources();
                int i2 = R.plurals.show_rep_countdown;
                int i3 = this.mPrefHitCountdown;
                ToastUtil.show(context, resources.getQuantityString(i2, i3, Integer.valueOf(i3)), 0);
            }
            if (this.mPrefHitCountdown <= 0) {
                Intent intent = new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://284"));
                intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
                this.mContext.sendBroadcast(intent);
                this.mPrefHitCountdown = 4;
                this.mLastPrefHitTime = SystemClock.elapsedRealtime();
            }
        }
    }

    private void dealFirmwareVersion(String str) {
        if ("firmware_version".equals(str)) {
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
                    Log.d("DeviceDetailOnClickListener", "Sorry, no fun for you!");
                    return;
                }
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setClassName(ThemeResources.FRAMEWORK_PACKAGE, PlatLogoActivity.class.getName());
                try {
                    this.mContext.startActivity(intent);
                } catch (Exception unused) {
                    Log.e("DeviceDetailOnClickListener", "Unable to start activity " + intent.toString());
                }
            }
        }
    }

    private void dealKernelVersion(String str) {
        String str2;
        if ("kernel_version".equals(str)) {
            if (!MiuiAboutPhoneUtils.supportCit() || (str2 = this.mLastHitKey) == null || !str2.equals(str)) {
                this.mKernelHitCountdown = 4;
            } else if (SystemClock.elapsedRealtime() - this.mLastKernelHitTime <= 3000) {
                Context context = this.mContext;
                ToastUtil.show(context, context.getResources().getString(R.string.launching_cit), 0);
            } else {
                int i = this.mKernelHitCountdown - 1;
                this.mKernelHitCountdown = i;
                if (i > 0) {
                    int i2 = R.plurals.show_cit_countdown;
                    Context context2 = this.mContext;
                    Resources resources = context2.getResources();
                    int i3 = this.mKernelHitCountdown;
                    ToastUtil.show(context2, resources.getQuantityString(i2, i3, Integer.valueOf(i3)), 0);
                }
                if (this.mKernelHitCountdown <= 0) {
                    Intent intent = new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://6484"));
                    intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
                    this.mContext.sendBroadcast(intent);
                    this.mKernelHitCountdown = 4;
                    this.mLastKernelHitTime = SystemClock.elapsedRealtime();
                }
            }
        }
    }

    private void dealMemoryClick(String str) {
        String str2;
        if ("device_internal_memory".equals(str)) {
            if (!MiuiAboutPhoneUtils.supportCit() || (str2 = this.mLastHitKey) == null || !str2.equals(str)) {
                this.mMemoryHitCountdown = 4;
            } else if (SystemClock.elapsedRealtime() - this.mLastMemoryHitTime <= 3000) {
                Context context = this.mContext;
                ToastUtil.show(context, context.getResources().getString(R.string.launching_pho), 0);
            } else {
                int i = this.mMemoryHitCountdown - 1;
                this.mMemoryHitCountdown = i;
                if (i > 0) {
                    Context context2 = this.mContext;
                    Resources resources = context2.getResources();
                    int i2 = R.plurals.show_pho_countdown;
                    int i3 = this.mMemoryHitCountdown;
                    ToastUtil.show(context2, resources.getQuantityString(i2, i3, Integer.valueOf(i3)), 0);
                }
                if (this.mMemoryHitCountdown <= 0) {
                    Intent intent = new Intent("android.telephony.action.SECRET_CODE", Uri.parse("android_secret_code://4636"));
                    intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
                    this.mContext.sendBroadcast(intent);
                    this.mMemoryHitCountdown = 4;
                    this.mLastMemoryHitTime = SystemClock.elapsedRealtime();
                }
            }
        }
    }

    private void dealMiuiVersionClick(String str) {
        ComponentName deviceOwnerComponent;
        if (!"miui_version".equals(str) || Utils.isMonkeyRunning() || MiuiUtils.isSecondSpace(this.mContext)) {
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

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view instanceof BaseDeviceCardItem) {
            String key = ((BaseDeviceCardItem) view).getKey();
            dealCpuClick(key);
            dealMiuiVersionClick(key);
            dealFirmwareVersion(key);
            dealKernelVersion(key);
            dealMemoryClick(key);
            this.mLastHitKey = key;
        }
    }
}
