package com.android.settings.applications.defaultapps;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.ResolveInfo;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.applications.DefaultHomeSettings;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;

/* loaded from: classes.dex */
public class DefaultHomePreferenceController extends DefaultAppPreferenceController {
    static final IntentFilter HOME_FILTER;
    private final String mPackageName;

    static {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        HOME_FILTER = intentFilter;
        intentFilter.addCategory("android.intent.category.HOME");
        intentFilter.addCategory("android.intent.category.DEFAULT");
    }

    public DefaultHomePreferenceController(Context context) {
        super(context);
        this.mPackageName = this.mContext.getPackageName();
    }

    private ComponentName getCurrentDefaultHome() {
        ResolveInfo currentDefaultHome = DefaultHomeSettings.getCurrentDefaultHome(this.mPackageManager);
        if (currentDefaultHome == null) {
            return null;
        }
        return currentDefaultHome.getComponentInfo().getComponentName();
    }

    private ActivityInfo getOnlyAppInfo(List<ResolveInfo> list) {
        ArrayList arrayList = new ArrayList();
        this.mPackageManager.getHomeActivities(list);
        Iterator<ResolveInfo> it = list.iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            if (!activityInfo.packageName.equals(this.mPackageName)) {
                arrayList.add(activityInfo);
            }
        }
        if (arrayList.size() == 1) {
            return (ActivityInfo) arrayList.get(0);
        }
        return null;
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        ArrayList arrayList = new ArrayList();
        ComponentName currentDefaultHome = getCurrentDefaultHome();
        if (currentDefaultHome != null) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, currentDefaultHome);
        }
        ActivityInfo onlyAppInfo = getOnlyAppInfo(arrayList);
        if (onlyAppInfo != null) {
            return new DefaultAppInfo(this.mContext, this.mPackageManager, this.mUserId, onlyAppInfo.getComponentName());
        }
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_home";
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected Intent getSettingIntent(DefaultAppInfo defaultAppInfo) {
        String str;
        if (defaultAppInfo == null) {
            return null;
        }
        ComponentName componentName = defaultAppInfo.componentName;
        if (componentName != null) {
            str = componentName.getPackageName();
        } else {
            PackageItemInfo packageItemInfo = defaultAppInfo.packageItemInfo;
            if (packageItemInfo == null) {
                return null;
            }
            str = packageItemInfo.packageName;
        }
        Intent addFlags = new Intent("android.intent.action.APPLICATION_PREFERENCES").setPackage(str).addFlags(268468224);
        if (addFlags.resolveActivity(this.mPackageManager) != null) {
            return addFlags;
        }
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (Build.IS_TABLET || !"com.android.settings.applications.defaultapps.DefaultHomePicker".equals(preference.getFragment())) {
            return false;
        }
        preference.getContext().startActivity(new Intent("com.miui.settings.HOME_SETTINGS_MIUI"));
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_default_home);
    }
}
