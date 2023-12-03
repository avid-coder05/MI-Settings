package com.android.settings.connection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.xiaomi.mirror.MirrorManager;
import java.util.List;

/* loaded from: classes.dex */
public class MiMirrorController extends BasePreferenceController {
    private static final String TAG = "MiMirrorController";
    private String mAppName;

    public MiMirrorController(Context context, String str) {
        super(context, str);
    }

    private Intent getIntent() {
        Intent intent = new Intent("miui.intent.action.MIRROR_SETTING");
        intent.setPackage("com.xiaomi.mirror");
        return intent;
    }

    private String getWorkingMasterName() {
        try {
            MirrorManager mirrorManager = MirrorManager.get(this.mContext.getApplicationContext());
            if (mirrorManager == null) {
                Log.d(TAG, "has no MirrorManager");
                return "";
            }
            boolean isWorking = mirrorManager.isWorking();
            String workingMasterName = mirrorManager.getWorkingMasterName();
            Log.d(TAG, "MirrorManager is working: " + isWorking + ",WorkingMasterName: " + workingMasterName);
            return isWorking ? workingMasterName : "";
        } catch (Exception | NoSuchMethodError e) {
            Log.e(TAG, "getWorkingMasterName: " + e.getMessage());
            return "";
        }
    }

    private boolean isMirrorSupported() {
        try {
            MirrorManager mirrorManager = MirrorManager.get(this.mContext);
            if (mirrorManager == null) {
                Log.d(TAG, "has no MirrorManager");
                return false;
            }
            return mirrorManager.isModelSupport();
        } catch (Exception | NoSuchMethodError e) {
            Log.e(TAG, "isMirrorSupported: " + e.getMessage());
            return false;
        }
    }

    private boolean isShowWorkingMasterName() {
        PackageInfo packageInfo;
        try {
            packageInfo = this.mContext.getPackageManager().getPackageInfo("com.xiaomi.mirror", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        return packageInfo != null && packageInfo.getLongVersionCode() < 30700;
    }

    private boolean resolveMiMirrorSettings() {
        ResolveInfo resolveInfo;
        Intent intent = getIntent();
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(intent, 1);
        if (queryIntentActivities.isEmpty() || (resolveInfo = queryIntentActivities.get(0)) == null) {
            return false;
        }
        CharSequence loadLabel = resolveInfo.loadLabel(packageManager);
        this.mAppName = loadLabel != null ? loadLabel.toString() : "";
        Log.d(TAG, "mirror manager app name: " + this.mAppName);
        return true;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("mimirror_settings");
        if (findPreference instanceof ValuePreference) {
            ValuePreference valuePreference = (ValuePreference) findPreference;
            valuePreference.setShowRightArrow(true);
            valuePreference.setTitle(this.mAppName);
            if (isShowWorkingMasterName()) {
                valuePreference.setValue(getWorkingMasterName());
            }
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (isMirrorSupported() && resolveMiMirrorSettings() && !TextUtils.isEmpty(this.mAppName)) ? 0 : 2;
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
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "mimirror_settings")) {
            this.mContext.startActivity(getIntent());
            return false;
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
