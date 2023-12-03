package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.os.UserHandle;
import com.android.settings.Utils;

/* loaded from: classes.dex */
public class DefaultWorkBrowserPreferenceController extends DefaultBrowserPreferenceController {
    private final UserHandle mUserHandle;

    public DefaultWorkBrowserPreferenceController(Context context) {
        super(context);
        UserHandle managedProfile = Utils.getManagedProfile(this.mUserManager);
        this.mUserHandle = managedProfile;
        if (managedProfile != null) {
            this.mUserId = managedProfile.getIdentifier();
        }
    }

    @Override // com.android.settings.applications.defaultapps.DefaultBrowserPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "work_default_browser";
    }

    @Override // com.android.settings.applications.defaultapps.DefaultBrowserPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mUserHandle == null) {
            return false;
        }
        return super.isAvailable();
    }
}
