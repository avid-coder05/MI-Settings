package com.android.settings.applications;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.magicwindow.MagicWinAppController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MiuiApplicationsSettings extends DashboardFragment {
    private List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new XSpaceSettingsController(context, getActivity(), "xspace"));
        arrayList.add(new PermissionManagerController(context, getActivity(), "permission_manager"));
        arrayList.add(new ApplicationLockController(context, getActivity(), "application_lock"));
        arrayList.add(new LauncherIconManagerController(context, getActivity(), "launcher_icon_management"));
        arrayList.add(new MagicWinAppController(context, getActivity(), "magic_window"));
        arrayList.add(new UninstalledSystemAppsController(context, getActivity(), "already_delete_system_app"));
        arrayList.add(new SafeInstallModeController(context, "safe_install_mode_pref"));
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
        return "MiuiApplicationsSettings";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.applications_settings;
    }
}
