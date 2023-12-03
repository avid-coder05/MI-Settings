package com.android.settings.wifi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;

/* loaded from: classes2.dex */
public class NetworkCheckController extends AbstractPreferenceController {
    public NetworkCheckController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "network_check";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "network_check")) {
            Intent intent = new Intent();
            intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity");
            this.mContext.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        Intent intent = new Intent();
        intent.setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.networkassistant.ui.activity.NetworkDiagnosticsActivity");
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(intent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }
}
