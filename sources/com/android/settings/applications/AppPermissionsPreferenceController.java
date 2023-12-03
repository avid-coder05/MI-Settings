package com.android.settings.applications;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.text.ListFormatter;
import android.miui.AppOpsUtils;
import android.util.ArraySet;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.applications.PermissionsSummaryHelper;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* loaded from: classes.dex */
public class AppPermissionsPreferenceController extends BasePreferenceController {
    private static final int NUM_PACKAGE_TO_CHECK = 4;
    static int NUM_PERMISSIONS_TO_SHOW = 3;
    private static final String TAG = "AppPermissionPrefCtrl";
    int mNumPackageChecked;
    private final PackageManager mPackageManager;
    private final Set<CharSequence> mPermissionGroups;
    private final PermissionsSummaryHelper.PermissionsResultCallback mPermissionsCallback;
    private Preference mPreference;

    public AppPermissionsPreferenceController(Context context, String str) {
        super(context, str);
        this.mPermissionsCallback = new PermissionsSummaryHelper.PermissionsResultCallback() { // from class: com.android.settings.applications.AppPermissionsPreferenceController.1
            @Override // com.android.settingslib.applications.PermissionsSummaryHelper.PermissionsResultCallback
            public void onPermissionSummaryResult(int i, int i2, int i3, List<CharSequence> list) {
                AppPermissionsPreferenceController.this.updateSummary(list);
            }
        };
        this.mPackageManager = context.getPackageManager();
        this.mPermissionGroups = new ArraySet();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$queryPermissionSummary$0(PackageInfo packageInfo) {
        return packageInfo.permissions != null;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return !AppOpsUtils.isXOptMode() ? 3 : 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    void queryPermissionSummary() {
        Iterator it = ((List) this.mPackageManager.getInstalledPackages(4096).stream().filter(new Predicate() { // from class: com.android.settings.applications.AppPermissionsPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$queryPermissionSummary$0;
                lambda$queryPermissionSummary$0 = AppPermissionsPreferenceController.lambda$queryPermissionSummary$0((PackageInfo) obj);
                return lambda$queryPermissionSummary$0;
            }
        }).limit(4L).collect(Collectors.toList())).iterator();
        while (it.hasNext()) {
            PermissionsSummaryHelper.getPermissionSummary(this.mContext, ((PackageInfo) it.next()).packageName, this.mPermissionsCallback);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mPreference = preference;
        this.mNumPackageChecked = 0;
        queryPermissionSummary();
    }

    void updateSummary(List<CharSequence> list) {
        this.mPermissionGroups.addAll(list);
        int i = this.mNumPackageChecked + 1;
        this.mNumPackageChecked = i;
        if (i < 4) {
            return;
        }
        List list2 = (List) this.mPermissionGroups.stream().limit(NUM_PERMISSIONS_TO_SHOW).collect(Collectors.toList());
        this.mPreference.setSummary(!list2.isEmpty() ? this.mPermissionGroups.size() > NUM_PERMISSIONS_TO_SHOW ? this.mContext.getString(R.string.app_permissions_summary_more, ListFormatter.getInstance().format(list2).toLowerCase()) : this.mContext.getString(R.string.app_permissions_summary, ListFormatter.getInstance().format(list2).toLowerCase()) : this.mContext.getString(R.string.runtime_permissions_summary_no_permissions_granted));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
