package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultBrowserPreferenceController extends DefaultAppPreferenceController {
    static final Intent BROWSE_PROBE = new Intent().setAction("android.intent.action.VIEW").addCategory("android.intent.category.BROWSABLE").setData(Uri.parse("http:")).addFlags(512);

    public DefaultBrowserPreferenceController(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<ResolveInfo> getCandidates(PackageManager packageManager, int i) {
        ActivityInfo activityInfo;
        ArrayList arrayList = new ArrayList();
        List<ResolveInfo> queryIntentActivitiesAsUser = packageManager.queryIntentActivitiesAsUser(BROWSE_PROBE, 131072, i);
        if (queryIntentActivitiesAsUser != null) {
            ArraySet arraySet = new ArraySet();
            for (ResolveInfo resolveInfo : queryIntentActivitiesAsUser) {
                if (resolveInfo.handleAllWebDataURI && (activityInfo = resolveInfo.activityInfo) != null && activityInfo.enabled && activityInfo.applicationInfo.enabled) {
                    String str = activityInfo.packageName;
                    if (!arraySet.contains(str)) {
                        arrayList.add(resolveInfo);
                        arraySet.add(str);
                    }
                }
            }
        }
        return arrayList;
    }

    private String getOnlyAppLabel() {
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        if (candidates == null || candidates.size() != 1) {
            return null;
        }
        ResolveInfo resolveInfo = candidates.get(0);
        String charSequence = resolveInfo.loadLabel(this.mPackageManager).toString();
        ComponentInfo componentInfo = resolveInfo.getComponentInfo();
        Log.d("BrowserPrefCtrl", "Getting label for the only browser app: " + (componentInfo != null ? componentInfo.packageName : null) + charSequence);
        return charSequence;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        try {
            String defaultBrowserPackageNameAsUser = this.mPackageManager.getDefaultBrowserPackageNameAsUser(this.mUserId);
            Log.d("BrowserPrefCtrl", "Get default browser package: " + defaultBrowserPackageNameAsUser);
            Context context = this.mContext;
            PackageManager packageManager = this.mPackageManager;
            int i = this.mUserId;
            return new DefaultAppInfo(context, packageManager, i, packageManager.getApplicationInfoAsUser(defaultBrowserPackageNameAsUser, 0, i));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    public CharSequence getDefaultAppLabel() {
        if (isAvailable()) {
            DefaultAppInfo defaultAppInfo = getDefaultAppInfo();
            CharSequence loadLabel = defaultAppInfo != null ? defaultAppInfo.loadLabel() : null;
            return !TextUtils.isEmpty(loadLabel) ? loadLabel : getOnlyAppLabel();
        }
        return null;
    }

    Drawable getOnlyAppIcon() {
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        if (candidates != null && candidates.size() == 1) {
            ComponentInfo componentInfo = candidates.get(0).getComponentInfo();
            String str = componentInfo == null ? null : componentInfo.packageName;
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(str, 0, this.mUserId);
                Log.d("BrowserPrefCtrl", "Getting icon for the only browser app: " + str);
                return IconDrawableFactory.newInstance(this.mContext).getBadgedIcon(componentInfo, applicationInfoAsUser, this.mUserId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("BrowserPrefCtrl", "Error getting app info for " + str);
            }
        }
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_browser";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<ResolveInfo> candidates = getCandidates(this.mPackageManager, this.mUserId);
        return (candidates == null || candidates.isEmpty()) ? false : true;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        CharSequence defaultAppLabel = getDefaultAppLabel();
        if (TextUtils.isEmpty(defaultAppLabel)) {
            return;
        }
        preference.setSummary(defaultAppLabel);
    }
}
