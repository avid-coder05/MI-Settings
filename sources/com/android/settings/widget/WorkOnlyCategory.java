package com.android.settings.widget;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.AttributeSet;
import androidx.preference.PreferenceCategory;
import com.android.settings.SelfAvailablePreference;
import com.android.settings.SubSettings;
import com.android.settings.Utils;

/* loaded from: classes2.dex */
public class WorkOnlyCategory extends PreferenceCategory implements SelfAvailablePreference {
    public WorkOnlyCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private static boolean resolveSubSettings(Context context, UserHandle userHandle) {
        return !context.getPackageManager().queryIntentActivitiesAsUser(new Intent(context, SubSettings.class), 1, userHandle.getIdentifier()).isEmpty();
    }

    public static boolean virtualKeyboardsForWorkAvailable(Context context) {
        UserHandle managedProfile = Utils.getManagedProfile(UserManager.get(context));
        return managedProfile != null && resolveSubSettings(context, managedProfile);
    }

    @Override // com.android.settings.SelfAvailablePreference
    public boolean isAvailable(Context context) {
        UserHandle managedProfile = Utils.getManagedProfile(UserManager.get(context));
        return managedProfile != null && resolveSubSettings(context, managedProfile);
    }
}
