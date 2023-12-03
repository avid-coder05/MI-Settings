package com.android.settings.location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiModemLocationSwitchController extends BasePreferenceController {
    private static final String AIM_PKG = "com.lbe.security.miui";
    private static final String DEVICE = "ro.product.device";
    private static final String KEY_MODEL_LOCATION = "location_modem_manage";
    private static final String KEY_MODEM_NOW_VERSION = "ro.build.version.sdk";
    private static final String KEY_MODEM_OLD_VERSION = "ro.product.first_api_level";
    public static final boolean MODEM_LOCATION_ENABLE;
    private static final String PERM_MODEM_LOCATION = "com.miui.securitycenter.permission.modem_location";
    private String[] items;
    private int mCurrentAction;
    private boolean mEnable;
    private PackageManager mPkgManager;
    private UserHandle mUserHandle;

    static {
        MODEM_LOCATION_ENABLE = Build.IS_INTERNATIONAL_BUILD && Integer.parseInt(SystemProperties.get(KEY_MODEM_OLD_VERSION)) >= 29 && Integer.parseInt(SystemProperties.get(KEY_MODEM_NOW_VERSION)) >= 29 && !"phoenixin".equals(SystemProperties.get(DEVICE));
    }

    public MiuiModemLocationSwitchController(Context context, String str) {
        super(context, str);
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeModemPermission(int i) {
        if (i == 1) {
            this.mPkgManager.revokeRuntimePermission(AIM_PKG, PERM_MODEM_LOCATION, this.mUserHandle);
            this.mPkgManager.updatePermissionFlags(PERM_MODEM_LOCATION, AIM_PKG, 2, 0, this.mUserHandle);
            return;
        }
        if (i == 0) {
            this.mPkgManager.grantRuntimePermission(AIM_PKG, PERM_MODEM_LOCATION, this.mUserHandle);
        } else if (i == 2) {
            this.mPkgManager.revokeRuntimePermission(AIM_PKG, PERM_MODEM_LOCATION, this.mUserHandle);
        }
        this.mPkgManager.updatePermissionFlags(PERM_MODEM_LOCATION, AIM_PKG, 2, 2, this.mUserHandle);
    }

    private void handleClick(final Preference preference) {
        new AlertDialog.Builder(this.mContext).setTitle(R.string.location_modem_title).setSingleChoiceItems(this.items, this.mCurrentAction, new DialogInterface.OnClickListener() { // from class: com.android.settings.location.MiuiModemLocationSwitchController.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i != MiuiModemLocationSwitchController.this.mCurrentAction) {
                    MiuiModemLocationSwitchController.this.changeModemPermission(i);
                    dialogInterface.dismiss();
                    MiuiModemLocationSwitchController.this.updateState(preference);
                }
            }
        }).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).create().show();
    }

    private void init() {
        String[] strArr;
        if (!MODEM_LOCATION_ENABLE) {
            this.mEnable = false;
            return;
        }
        PackageManager packageManager = this.mContext.getPackageManager();
        this.mPkgManager = packageManager;
        if (packageManager == null) {
            return;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(AIM_PKG, 4096);
            if (packageInfo != null && (strArr = packageInfo.requestedPermissions) != null && packageInfo.applicationInfo != null) {
                int length = strArr.length - 1;
                while (true) {
                    if (length < 0) {
                        break;
                    } else if (PERM_MODEM_LOCATION.equals(packageInfo.requestedPermissions[length])) {
                        this.mEnable = true;
                        break;
                    } else {
                        length--;
                    }
                }
                this.mUserHandle = UserHandle.getUserHandleForUid(packageInfo.applicationInfo.uid);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        this.items = this.mContext.getResources().getStringArray(R.array.modem_perm_action_items);
        this.mCurrentAction = 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!this.mEnable || this.mUserHandle == null) ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_MODEL_LOCATION;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (KEY_MODEL_LOCATION.equals(preference.getKey())) {
            handleClick(preference);
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int checkPermission = this.mPkgManager.checkPermission(PERM_MODEM_LOCATION, AIM_PKG);
        int permissionFlags = this.mPkgManager.getPermissionFlags(PERM_MODEM_LOCATION, AIM_PKG, this.mUserHandle);
        if (checkPermission == 0) {
            this.mCurrentAction = 0;
        } else if (checkPermission == -1 && permissionFlags == 0) {
            this.mCurrentAction = 1;
        } else if (checkPermission == -1) {
            this.mCurrentAction = 2;
        }
        ((ValuePreference) preference).setValue(this.items[this.mCurrentAction]);
        super.updateState(preference);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
