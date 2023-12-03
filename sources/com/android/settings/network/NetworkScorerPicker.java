package com.android.settings.network;

import android.content.Context;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.List;

/* loaded from: classes.dex */
public class NetworkScorerPicker extends InstrumentedPreferenceFragment implements RadioButtonPreference.OnClickListener {
    private NetworkScoreManager mNetworkScoreManager;

    private String getActiveScorerPackage() {
        return this.mNetworkScoreManager.getActiveScorerPackage();
    }

    private boolean setActiveScorer(String str) {
        if (TextUtils.equals(str, getActiveScorerPackage())) {
            return false;
        }
        return this.mNetworkScoreManager.setActiveScorer(str);
    }

    private void updateCheckedState(String str) {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int preferenceCount = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = preferenceScreen.getPreference(i);
            if (preference instanceof RadioButtonPreference) {
                ((RadioButtonPreference) preference).setChecked(TextUtils.equals(preference.getKey(), str));
            }
        }
    }

    NetworkScoreManager createNetworkScorerManager(Context context) {
        return (NetworkScoreManager) context.getSystemService("network_score");
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 861;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.network_scorer_picker_prefs;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mNetworkScoreManager = createNetworkScorerManager(context);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        updateCandidates();
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        setHasOptionsMenu(true);
        return onCreateView;
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        String key = radioButtonPreference.getKey();
        if (setActiveScorer(key)) {
            updateCheckedState(key);
        }
    }

    public void updateCandidates() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removeAll();
        List allValidScorers = this.mNetworkScoreManager.getAllValidScorers();
        String activeScorerPackage = getActiveScorerPackage();
        RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
        radioButtonPreference.setTitle(R.string.network_scorer_picker_none_preference);
        if (allValidScorers.isEmpty()) {
            radioButtonPreference.setChecked(true);
        } else {
            radioButtonPreference.setKey(null);
            radioButtonPreference.setChecked(TextUtils.isEmpty(activeScorerPackage));
            radioButtonPreference.setOnClickListener(this);
        }
        preferenceScreen.addPreference(radioButtonPreference);
        int size = allValidScorers.size();
        for (int i = 0; i < size; i++) {
            RadioButtonPreference radioButtonPreference2 = new RadioButtonPreference(getPrefContext());
            NetworkScorerAppData networkScorerAppData = (NetworkScorerAppData) allValidScorers.get(i);
            String recommendationServicePackageName = networkScorerAppData.getRecommendationServicePackageName();
            radioButtonPreference2.setTitle(networkScorerAppData.getRecommendationServiceLabel());
            radioButtonPreference2.setKey(recommendationServicePackageName);
            radioButtonPreference2.setChecked(TextUtils.equals(activeScorerPackage, recommendationServicePackageName));
            radioButtonPreference2.setOnClickListener(this);
            preferenceScreen.addPreference(radioButtonPreference2);
        }
    }
}
