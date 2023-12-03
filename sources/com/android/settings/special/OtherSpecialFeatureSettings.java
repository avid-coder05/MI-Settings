package com.android.settings.special;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;

/* loaded from: classes2.dex */
public class OtherSpecialFeatureSettings extends DashboardFragment {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "OtherSpecialFeatureSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.other_special_function_settings;
    }
}
