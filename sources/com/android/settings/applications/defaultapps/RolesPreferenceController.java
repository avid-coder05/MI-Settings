package com.android.settings.applications.defaultapps;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.text.ListFormatter;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.AppUtils;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class RolesPreferenceController extends BasePreferenceController {
    private final Intent mIntent;
    private final PackageManager mPackageManager;
    private final RoleManager mRoleManager;

    public RolesPreferenceController(Context context, String str) {
        super(context, str);
        PackageManager packageManager = context.getPackageManager();
        this.mPackageManager = packageManager;
        this.mRoleManager = (RoleManager) context.getSystemService(RoleManager.class);
        String permissionControllerPackageName = packageManager.getPermissionControllerPackageName();
        if (permissionControllerPackageName != null) {
            this.mIntent = new Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS").setPackage(permissionControllerPackageName);
        } else {
            this.mIntent = null;
        }
    }

    private CharSequence getDefaultAppLabel(String str) {
        List roleHolders = this.mRoleManager.getRoleHolders(str);
        if (roleHolders.isEmpty()) {
            return null;
        }
        return AppUtils.getApplicationLabel(this.mPackageManager, (String) roleHolders.get(0));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mIntent != null ? 1 : 3;
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
        ArrayList arrayList = new ArrayList();
        CharSequence defaultAppLabel = getDefaultAppLabel("android.app.role.BROWSER");
        if (!TextUtils.isEmpty(defaultAppLabel)) {
            arrayList.add(defaultAppLabel);
        }
        CharSequence defaultAppLabel2 = getDefaultAppLabel("android.app.role.DIALER");
        if (!TextUtils.isEmpty(defaultAppLabel2)) {
            arrayList.add(defaultAppLabel2);
        }
        CharSequence defaultAppLabel3 = getDefaultAppLabel("android.app.role.SMS");
        if (!TextUtils.isEmpty(defaultAppLabel3)) {
            arrayList.add(defaultAppLabel3);
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return ListFormatter.getInstance().format(arrayList);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), this.mPreferenceKey)) {
            Intent intent = this.mIntent;
            if (intent != null) {
                this.mContext.startActivity(intent);
                return true;
            }
            return true;
        }
        return false;
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
