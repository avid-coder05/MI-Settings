package com.android.settings.development.graphicsdriver;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.R;
import com.android.settings.SettingsActivity;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.MainSwitchBarController;
import com.android.settings.widget.SettingsMainSwitchBar;
import com.android.settingslib.development.DevelopmentSettingsEnabler;

/* loaded from: classes.dex */
public class GraphicsDriverDashboard extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.graphics_driver_settings) { // from class: com.android.settings.development.graphicsdriver.GraphicsDriverDashboard.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(context);
        }
    };
    private SettingsMainSwitchBar switchBar;

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "GraphicsDriverDashboard";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1613;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.graphics_driver_settings;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        if (isMiuiSettingsActivity()) {
            this.switchBar = new SettingsMainSwitchBar(getActivity());
        } else {
            this.switchBar = ((SettingsActivity) getActivity()).getSwitchBar();
        }
        getSettingsLifecycle().addObserver(new GraphicsDriverGlobalSwitchBarController(getActivity(), new MainSwitchBarController(this.switchBar)));
        this.switchBar.setTitle(getContext().getString(R.string.graphics_driver_main_switch_title));
        this.switchBar.show();
    }
}
