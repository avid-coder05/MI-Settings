package com.android.settings.magicwindow;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.util.Slog;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.utils.SettingsFeatures;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class MagicWinAppController extends BasePreferenceController implements IAppController {
    private static final String TAG = "MagicWinAppController";
    private Activity mActivity;
    private String mPreferenceKey;

    public MagicWinAppController(Context context, Activity activity, String str) {
        super(context, str);
        this.mActivity = activity;
        this.mPreferenceKey = str;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.magicwindow.IAppController
    public List getAppControlInfoList() {
        long uptimeMillis = SystemClock.uptimeMillis();
        PackageManager packageManager = this.mActivity.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(8192);
        ArrayList arrayList = new ArrayList(0);
        ArrayList arrayList2 = new ArrayList(0);
        Map map = null;
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            if (classLoader != null) {
                Class<?> loadClass = classLoader.loadClass("android.magicwin.MiuiMagicWindowManager");
                map = (Map) loadClass.getMethod("getMiuiMagicWinEnabledApps", new Class[0]).invoke(loadClass, new Object[0]);
            }
        } catch (Exception unused) {
            Slog.d(TAG, "getMiuiMagicWinEnabledApps Error: class is null .");
        }
        if (map != null) {
            for (ApplicationInfo applicationInfo : installedApplications) {
                if (map.containsKey(applicationInfo.packageName)) {
                    arrayList.add(applicationInfo);
                }
            }
            if (arrayList.size() > 0) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    ApplicationInfo applicationInfo2 = (ApplicationInfo) it.next();
                    arrayList2.add(new ChildAppItemInfo(applicationInfo2.packageName, (String) applicationInfo2.loadLabel(packageManager), applicationInfo2.loadIcon(packageManager), ((Boolean) map.get(applicationInfo2.packageName)).booleanValue()));
                }
            }
        } else {
            Slog.d(TAG, "getMiuiMagicWinEnabledApps failed");
        }
        Slog.d(TAG, "getAppList take" + (SystemClock.uptimeMillis() - uptimeMillis) + "ms");
        return arrayList2;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return SettingsFeatures.isSupportMagicWindow(this.mContext) ? 0 : 2;
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
        Activity activity;
        if (!this.mPreferenceKey.equals(preference.getKey()) || preference.getIntent() == null || (activity = this.mActivity) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        if (activity.isInMultiWindowMode()) {
            preference.getIntent().setFlags(268435456);
        }
        this.mActivity.startActivity(preference.getIntent());
        return true;
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
