package com.android.settings.projection;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class ScreenProjectionFragment extends DashboardFragment {
    private List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        ScreenProjectionSwitchController screenProjectionSwitchController = new ScreenProjectionSwitchController(context, "pref_key_enable_screen_projection");
        getLifecycle().addObserver(screenProjectionSwitchController);
        arrayList.add(screenProjectionSwitchController);
        arrayList.add(new ScreenProjectionHelperController(context, "pref_key_screen_projection_help"));
        arrayList.add(new ScreenProjectionPolicyController(context, "pref_key_screen_projection_privacy_policy"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ScreenProjectionFragment";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.screen_projection_settings;
    }
}
