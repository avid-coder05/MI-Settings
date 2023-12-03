package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import android.widget.Switch;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.SettingsMainSwitchPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;
import com.google.common.primitives.Floats;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class CaptionPropertiesFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener, OnMainSwitchChangeListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.captioning_settings);
    private CaptioningManager mCaptioningManager;
    private float[] mFontSizeValuesArray;
    private Preference mMoreOptions;
    private final List<Preference> mPreferenceList = new ArrayList();
    private SettingsMainSwitchPreference mSwitch;
    private Preference mTextAppearance;

    private CharSequence geTextAppearanceSummary(Context context) {
        String[] stringArray = context.getResources().getStringArray(R.array.captioning_font_size_selector_summaries);
        int indexOf = Floats.indexOf(this.mFontSizeValuesArray, this.mCaptioningManager.getFontScale());
        if (indexOf == -1) {
            indexOf = 0;
        }
        return stringArray[indexOf];
    }

    private void initFontSizeValuesArray() {
        String[] stringArray = getPrefContext().getResources().getStringArray(R.array.captioning_font_size_selector_values);
        int length = stringArray.length;
        this.mFontSizeValuesArray = new float[length];
        for (int i = 0; i < length; i++) {
            this.mFontSizeValuesArray[i] = Float.parseFloat(stringArray[i]);
        }
    }

    private void initializeAllPreferences() {
        if (MiuiAboutPhoneUtils.getInstance(getActivity()).isMIUILite()) {
            getPreferenceScreen().removePreference((LayoutPreference) findPreference("captions_preview"));
        }
        this.mSwitch = (SettingsMainSwitchPreference) findPreference("captioning_preference_switch");
        this.mTextAppearance = findPreference("captioning_caption_appearance");
        this.mMoreOptions = findPreference("captioning_more_options");
        this.mPreferenceList.add(this.mTextAppearance);
        this.mPreferenceList.add(this.mMoreOptions);
    }

    private void installUpdateListeners() {
        this.mSwitch.setOnPreferenceChangeListener(this);
        this.mSwitch.addOnSwitchChangeListener(this);
    }

    private void updateAllPreferences() {
        this.mSwitch.setChecked(this.mCaptioningManager.isEnabled());
        this.mTextAppearance.setSummary(geTextAppearanceSummary(getPrefContext()));
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R.string.help_url_caption;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "CaptionPropertiesFragment";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.captioning_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        initializeAllPreferences();
        installUpdateListeners();
        initFontSizeValuesArray();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mSwitch == preference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_enabled", ((Boolean) obj).booleanValue() ? 1 : 0);
            return true;
        }
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updateAllPreferences();
    }

    @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
    public void onSwitchChanged(Switch r1, boolean z) {
        Settings.Secure.putInt(getActivity().getContentResolver(), "accessibility_captioning_enabled", z ? 1 : 0);
    }
}
