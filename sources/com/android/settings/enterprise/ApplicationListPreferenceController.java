package com.android.settings.enterprise;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.IconDrawableFactory;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.UserAppInfo;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.AppPreference;
import java.util.List;

/* loaded from: classes.dex */
public class ApplicationListPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, ApplicationFeatureProvider.ListOfAppsCallback {
    private SettingsPreferenceFragment mParent;
    private final PackageManager mPm;

    /* loaded from: classes.dex */
    public interface ApplicationListBuilder {
        void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback);
    }

    public ApplicationListPreferenceController(Context context, ApplicationListBuilder applicationListBuilder, PackageManager packageManager, SettingsPreferenceFragment settingsPreferenceFragment) {
        super(context);
        this.mPm = packageManager;
        this.mParent = settingsPreferenceFragment;
        applicationListBuilder.buildApplicationList(context, this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settings.applications.ApplicationFeatureProvider.ListOfAppsCallback
    public void onListOfAppsResult(List<UserAppInfo> list) {
        PreferenceScreen preferenceScreen = this.mParent.getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(this.mContext);
        Context context = this.mParent.getContext();
        for (int i = 0; i < list.size(); i++) {
            UserAppInfo userAppInfo = list.get(i);
            AppPreference appPreference = new AppPreference(context);
            appPreference.setTitle(userAppInfo.appInfo.loadLabel(this.mPm));
            appPreference.setIcon(newInstance.getBadgedIcon(userAppInfo.appInfo));
            appPreference.setOrder(i);
            appPreference.setSelectable(false);
            preferenceScreen.addPreference(appPreference);
        }
    }
}
