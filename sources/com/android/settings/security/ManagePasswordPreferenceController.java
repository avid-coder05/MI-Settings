package com.android.settings.security;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.search.tree.MiuiSecurityAndPrivacySettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class ManagePasswordPreferenceController extends AbstractPreferenceController {
    private Activity mActivity;

    public ManagePasswordPreferenceController(Context context, Activity activity) {
        super(context);
        this.mActivity = activity;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return MiuiSecurityAndPrivacySettingsTree.MANAGE_PASSWORD;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey()) || this.mActivity == null || (intent = preference.getIntent()) == null) {
            return super.handlePreferenceTreeClick(preference);
        }
        this.mActivity.startActivity(intent);
        Pair<Integer, Integer> systemDefaultEnterAnim = MiuiUtils.getSystemDefaultEnterAnim(this.mActivity);
        this.mActivity.overridePendingTransition(((Integer) systemDefaultEnterAnim.first).intValue(), ((Integer) systemDefaultEnterAnim.second).intValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return SettingsFeatures.isManagePasswordNeeded(this.mContext);
    }
}
