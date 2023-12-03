package com.android.settings.special;

import android.content.Context;
import android.os.Bundle;
import androidx.preference.CheckBoxPreference;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public class ExternalRamFragment extends DashboardFragment {
    private Context mContext;
    private Map<String, BasePreferenceController> mControlMap = new HashMap();
    private CheckBoxPreference mExternalRamPref;

    private void initPreferenceList() {
        this.mExternalRamPref = (CheckBoxPreference) findPreference("external_ram");
        this.mExternalRamPref.setOnPreferenceChangeListener((ExternalRamController) this.mControlMap.get("external_ram"));
        this.mExternalRamPref.setChecked(ExternalRamController.isChecked(this.mContext));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        this.mControlMap.put("external_ram", new ExternalRamController(context));
        return super.createPreferenceControllers(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.external_ram_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getContext();
        initPreferenceList();
    }
}
