package com.android.settings.location;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.IconDrawableFactory;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.RestrictedAppPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.location.InjectedSetting;
import com.android.settingslib.location.SettingsInjector;
import com.android.settingslib.widget.AppPreference;

/* loaded from: classes.dex */
public class AppSettingsInjector extends SettingsInjector {
    private final int mMetricsCategory;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    public AppSettingsInjector(Context context, int i) {
        super(context);
        this.mMetricsCategory = i;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    private static Drawable getAppIcon(Context context, InjectedSetting injectedSetting) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageItemInfo packageItemInfo = new PackageItemInfo();
            packageItemInfo.icon = injectedSetting.iconId;
            packageItemInfo.packageName = injectedSetting.packageName;
            return IconDrawableFactory.newInstance(context).getBadgedIcon(packageItemInfo, packageManager.getApplicationInfo(injectedSetting.packageName, 128), injectedSetting.mUserHandle.getIdentifier());
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("SettingsInjector", "Can't get ApplicationInfo for " + injectedSetting.packageName, e);
            return null;
        }
    }

    @Override // com.android.settingslib.location.SettingsInjector
    protected Preference createPreference(Context context, InjectedSetting injectedSetting) {
        Drawable appIcon = getAppIcon(context, injectedSetting);
        Preference appPreference = TextUtils.isEmpty(injectedSetting.userRestriction) ? new AppPreference(context) : new RestrictedAppPreference(context, injectedSetting.userRestriction);
        appPreference.setIcon(appIcon);
        return appPreference;
    }

    @Override // com.android.settingslib.location.SettingsInjector
    protected void logPreferenceClick(Intent intent) {
        this.mMetricsFeatureProvider.logStartedIntent(intent, this.mMetricsCategory);
    }
}
