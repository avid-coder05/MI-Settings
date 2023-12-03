package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.UserManager;
import android.telecom.DefaultDialerManager;
import android.telephony.TelephonyManager;
import com.android.settingslib.applications.DefaultAppInfo;
import java.util.List;

/* loaded from: classes.dex */
public class DefaultPhonePreferenceController extends DefaultAppPreferenceController {
    public DefaultPhonePreferenceController(Context context) {
        super(context);
    }

    private List<String> getCandidates() {
        return DefaultDialerManager.getInstalledDialerApplications(this.mContext, this.mUserId);
    }

    @Override // com.android.settings.applications.defaultapps.DefaultAppPreferenceController
    protected DefaultAppInfo getDefaultAppInfo() {
        try {
            Context context = this.mContext;
            PackageManager packageManager = this.mPackageManager;
            int i = this.mUserId;
            return new DefaultAppInfo(context, packageManager, i, packageManager.getApplicationInfo(DefaultDialerManager.getDefaultDialerApplication(context, i), 0));
        } catch (PackageManager.NameNotFoundException unused) {
            return null;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "default_phone_app";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        List<String> candidates;
        return (!((TelephonyManager) this.mContext.getSystemService("phone")).isVoiceCapable() || ((UserManager) this.mContext.getSystemService("user")).hasUserRestriction("no_outgoing_calls") || (candidates = getCandidates()) == null || candidates.isEmpty()) ? false : true;
    }
}
