package com.android.settings.development;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.securityspace.ConfigUtils;

/* loaded from: classes.dex */
public class SecondSpaceDeleteController extends DeveloperOptionsPreferenceController implements LifecycleObserver, OnResume {
    private Activity mActivity;
    private PreferenceCategory mSecondSpaceCategory;

    public SecondSpaceDeleteController(Activity activity, Lifecycle lifecycle) {
        super(activity);
        this.mActivity = activity;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private int getSecondSpaceId() {
        return Settings.Secure.getIntForUser(this.mActivity.getContentResolver(), "second_user_id", -10000, 0);
    }

    private void updateState() {
        setVisible(this.mSecondSpaceCategory, "secondspace_category", isAvailable());
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSecondSpaceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "secondspace_category";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals("delete_second_space", preference.getKey())) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycore", "com.miui.securityspace.ui.activity.RemoveUserActivity"));
            intent.putExtra("remove_user_bussiness_type", 0);
            this.mActivity.startActivity(intent);
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return ConfigUtils.isSupportSecuritySpace() && getSecondSpaceId() != -10000 && UserHandle.myUserId() == 0;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mSecondSpaceCategory == null) {
            return;
        }
        updateState();
    }
}
