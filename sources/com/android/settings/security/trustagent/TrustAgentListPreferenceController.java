package com.android.settings.security.trustagent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.SearchIndexableData;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.password.ChooseLockSettingsHelper;
import com.android.settings.security.SecurityFeatureProvider;
import com.android.settings.security.trustagent.TrustAgentManager;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnSaveInstanceState;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class TrustAgentListPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnSaveInstanceState, OnCreate, OnResume {
    private static final int MY_USER_ID = UserHandle.myUserId();
    static final String PREF_KEY_SECURITY_CATEGORY = "security_category";
    static final String PREF_KEY_TRUST_AGENT = "trust_agent";
    private final SettingsPreferenceFragment mHost;
    private final LockPatternUtils mLockPatternUtils;
    private PreferenceCategory mSecurityCategory;
    private Intent mTrustAgentClickIntent;
    private final TrustAgentManager mTrustAgentManager;
    final List<String> mTrustAgentsKeyList;

    public TrustAgentListPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context);
        SecurityFeatureProvider securityFeatureProvider = FeatureFactory.getFactory(context).getSecurityFeatureProvider();
        this.mHost = settingsPreferenceFragment;
        this.mLockPatternUtils = securityFeatureProvider.getLockPatternUtils(context);
        this.mTrustAgentManager = securityFeatureProvider.getTrustAgentManager();
        this.mTrustAgentsKeyList = new ArrayList();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    private List<TrustAgentManager.TrustAgentComponentInfo> getActiveTrustAgents(Context context) {
        return this.mTrustAgentManager.getActiveTrustAgents(context, this.mLockPatternUtils);
    }

    private void updateTrustAgents() {
        List<TrustAgentManager.TrustAgentComponentInfo> activeTrustAgents;
        if (this.mSecurityCategory == null || !isAvailable() || (activeTrustAgents = getActiveTrustAgents(this.mContext)) == null) {
            return;
        }
        int size = activeTrustAgents.size();
        for (int i = 0; i < size; i++) {
            Preference findPreference = this.mSecurityCategory.findPreference(PREF_KEY_TRUST_AGENT + i);
            if (findPreference == null) {
                break;
            }
            this.mSecurityCategory.removePreference(findPreference);
        }
        this.mTrustAgentsKeyList.clear();
        boolean isSecure = this.mLockPatternUtils.isSecure(MY_USER_ID);
        int size2 = activeTrustAgents.size();
        for (int i2 = 0; i2 < size2; i2++) {
            RestrictedPreference restrictedPreference = new RestrictedPreference(this.mSecurityCategory.getContext());
            TrustAgentManager.TrustAgentComponentInfo trustAgentComponentInfo = activeTrustAgents.get(i2);
            this.mTrustAgentsKeyList.add(PREF_KEY_TRUST_AGENT + i2);
            restrictedPreference.setKey(PREF_KEY_TRUST_AGENT + i2);
            restrictedPreference.setTitle(trustAgentComponentInfo.title);
            restrictedPreference.setSummary(trustAgentComponentInfo.summary);
            restrictedPreference.setIntent(new Intent("android.intent.action.MAIN").setComponent(trustAgentComponentInfo.componentName));
            restrictedPreference.setDisabledByAdmin(trustAgentComponentInfo.admin);
            if (!restrictedPreference.isDisabledByAdmin() && !isSecure) {
                restrictedPreference.setEnabled(false);
                restrictedPreference.setSummary(R.string.disabled_because_no_backup_security);
            }
            this.mSecurityCategory.addPreference(restrictedPreference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSecurityCategory = (PreferenceCategory) preferenceScreen.findPreference(PREF_KEY_SECURITY_CATEGORY);
        updateTrustAgents();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return PREF_KEY_TRUST_AGENT;
    }

    public boolean handleActivityResult(int i, int i2) {
        if (i == 126 && i2 == -1) {
            Intent intent = this.mTrustAgentClickIntent;
            if (intent != null) {
                this.mHost.startActivity(intent);
                this.mTrustAgentClickIntent = null;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (this.mTrustAgentsKeyList.contains(preference.getKey())) {
            boolean show = new ChooseLockSettingsHelper.Builder(this.mHost.getActivity(), this.mHost).setRequestCode(126).setTitle(preference.getTitle()).show();
            Intent intent2 = preference.getIntent();
            this.mTrustAgentClickIntent = intent2;
            intent2.putExtra("page_transition_type", 1);
            if (!show && (intent = this.mTrustAgentClickIntent) != null) {
                this.mHost.startActivity(intent);
                this.mTrustAgentClickIntent = null;
            }
            return true;
        }
        return super.handlePreferenceTreeClick(preference);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_trust_agent_click_intent);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        if (bundle == null || !bundle.containsKey("trust_agent_click_intent")) {
            return;
        }
        this.mTrustAgentClickIntent = (Intent) bundle.getParcelable("trust_agent_click_intent");
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        updateTrustAgents();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnSaveInstanceState
    public void onSaveInstanceState(Bundle bundle) {
        Intent intent = this.mTrustAgentClickIntent;
        if (intent != null) {
            bundle.putParcelable("trust_agent_click_intent", intent);
        }
    }

    @Override // com.android.settings.core.PreferenceControllerMixin
    public void updateDynamicRawDataToIndex(List<SearchIndexableRaw> list) {
        List<TrustAgentManager.TrustAgentComponentInfo> activeTrustAgents;
        if (isAvailable() && (activeTrustAgents = getActiveTrustAgents(this.mContext)) != null) {
            int size = activeTrustAgents.size();
            for (int i = 0; i < size; i++) {
                SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
                TrustAgentManager.TrustAgentComponentInfo trustAgentComponentInfo = activeTrustAgents.get(i);
                ((SearchIndexableData) searchIndexableRaw).key = PREF_KEY_TRUST_AGENT + i;
                searchIndexableRaw.title = trustAgentComponentInfo.title;
                list.add(searchIndexableRaw);
            }
        }
    }
}
