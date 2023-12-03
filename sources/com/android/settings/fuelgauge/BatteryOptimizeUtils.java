package com.android.settings.fuelgauge;

import android.app.AppOpsManager;
import android.content.Context;
import android.util.Log;
import com.android.settingslib.fuelgauge.PowerAllowlistBackend;

/* loaded from: classes.dex */
public class BatteryOptimizeUtils {
    private boolean mAllowListed;
    AppOpsManager mAppOpsManager;
    BatteryUtils mBatteryUtils;
    private int mMode;
    private final String mPackageName;
    PowerAllowlistBackend mPowerAllowListBackend;
    private final int mUid;

    /* renamed from: com.android.settings.fuelgauge.BatteryOptimizeUtils$1  reason: invalid class name */
    /* loaded from: classes.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$settings$fuelgauge$BatteryOptimizeUtils$AppUsageState;

        static {
            int[] iArr = new int[AppUsageState.values().length];
            $SwitchMap$com$android$settings$fuelgauge$BatteryOptimizeUtils$AppUsageState = iArr;
            try {
                iArr[AppUsageState.RESTRICTED.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$settings$fuelgauge$BatteryOptimizeUtils$AppUsageState[AppUsageState.UNRESTRICTED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$settings$fuelgauge$BatteryOptimizeUtils$AppUsageState[AppUsageState.OPTIMIZED.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* loaded from: classes.dex */
    public enum AppUsageState {
        UNKNOWN,
        RESTRICTED,
        UNRESTRICTED,
        OPTIMIZED
    }

    public BatteryOptimizeUtils(Context context, int i, String str) {
        this.mUid = i;
        this.mPackageName = str;
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mPowerAllowListBackend = PowerAllowlistBackend.getInstance(context);
        this.mMode = this.mAppOpsManager.checkOpNoThrow(70, i, str);
        this.mAllowListed = this.mPowerAllowListBackend.isAllowlisted(str);
    }

    private void refreshState() {
        this.mPowerAllowListBackend.refreshList();
        this.mAllowListed = this.mPowerAllowListBackend.isAllowlisted(this.mPackageName);
        this.mMode = this.mAppOpsManager.checkOpNoThrow(70, this.mUid, this.mPackageName);
        Log.d("BatteryOptimizeUtils", String.format("refresh %s state, allowlisted = %s, mode = %d", this.mPackageName, Boolean.valueOf(this.mAllowListed), Integer.valueOf(this.mMode)));
    }

    public AppUsageState getAppUsageState() {
        refreshState();
        boolean z = this.mAllowListed;
        if (z || this.mMode != 1) {
            if (z && this.mMode == 0) {
                return AppUsageState.UNRESTRICTED;
            }
            if (z || this.mMode != 0) {
                Log.d("BatteryOptimizeUtils", "get unknown app usage state.");
                return AppUsageState.UNKNOWN;
            }
            return AppUsageState.OPTIMIZED;
        }
        return AppUsageState.RESTRICTED;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getPackageName() {
        String str = this.mPackageName;
        return str == null ? "unknown" : str;
    }

    public boolean isSystemOrDefaultApp() {
        this.mPowerAllowListBackend.refreshList();
        return this.mPowerAllowListBackend.isSysAllowlisted(this.mPackageName) || this.mPowerAllowListBackend.isDefaultActiveApp(this.mPackageName);
    }

    public boolean isValidPackageName() {
        return this.mBatteryUtils.getPackageUid(this.mPackageName) != -1;
    }

    public void setAppUsageState(AppUsageState appUsageState) {
        int i = AnonymousClass1.$SwitchMap$com$android$settings$fuelgauge$BatteryOptimizeUtils$AppUsageState[appUsageState.ordinal()];
        if (i == 1) {
            this.mBatteryUtils.setForceAppStandby(this.mUid, this.mPackageName, 1);
            this.mPowerAllowListBackend.removeApp(this.mPackageName);
        } else if (i == 2) {
            this.mBatteryUtils.setForceAppStandby(this.mUid, this.mPackageName, 0);
            this.mPowerAllowListBackend.addApp(this.mPackageName);
        } else if (i != 3) {
            Log.d("BatteryOptimizeUtils", "set unknown app usage state.");
        } else {
            this.mBatteryUtils.setForceAppStandby(this.mUid, this.mPackageName, 0);
            this.mPowerAllowListBackend.removeApp(this.mPackageName);
        }
    }
}
