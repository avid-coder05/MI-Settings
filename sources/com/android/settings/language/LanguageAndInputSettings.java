package com.android.settings.language;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.inputmethod.PhysicalKeyboardPreferenceController;
import com.android.settings.inputmethod.SpellCheckerPreferenceController;
import com.android.settings.inputmethod.VirtualKeyboardPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* loaded from: classes.dex */
public class LanguageAndInputSettings extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R.xml.language_and_input) { // from class: com.android.settings.language.LanguageAndInputSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
            return LanguageAndInputSettings.buildPreferenceControllers(context, null);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Lifecycle lifecycle) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new PhoneLanguagePreferenceController(context));
        VirtualKeyboardPreferenceController virtualKeyboardPreferenceController = new VirtualKeyboardPreferenceController(context);
        PhysicalKeyboardPreferenceController physicalKeyboardPreferenceController = new PhysicalKeyboardPreferenceController(context, lifecycle);
        arrayList.add(virtualKeyboardPreferenceController);
        arrayList.add(physicalKeyboardPreferenceController);
        arrayList.add(new PreferenceCategoryController(context, "keyboards_category").setChildren(Arrays.asList(virtualKeyboardPreferenceController, physicalKeyboardPreferenceController)));
        TtsPreferenceController ttsPreferenceController = new TtsPreferenceController(context, "tts_settings_summary");
        arrayList.add(ttsPreferenceController);
        PointerSpeedController pointerSpeedController = new PointerSpeedController(context);
        arrayList.add(pointerSpeedController);
        arrayList.add(new PreferenceCategoryController(context, "pointer_and_tts_category").setChildren(Arrays.asList(pointerSpeedController, ttsPreferenceController)));
        arrayList.add(new SpellCheckerPreferenceController(context));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "LanguageAndInputSettings";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 750;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.language_and_input;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        activity.setTitle(R.string.language_settings);
    }
}
