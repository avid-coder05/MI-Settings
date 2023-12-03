package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.Intent;
import com.android.settingslib.applications.DefaultAppInfo;

/* loaded from: classes.dex */
public class DefaultEmergencyPreferenceController extends DefaultAppPreferenceController {
    public static final Intent QUERY_INTENT = new Intent("android.telephony.action.EMERGENCY_ASSISTANCE");

    public DefaultEmergencyPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        return null;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_emergency_app";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }
}
