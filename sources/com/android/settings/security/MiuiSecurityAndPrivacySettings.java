package com.android.settings.security;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiChangeProfileScreenLockPreferenceController;
import com.android.settings.MiuiLockUnificationPreferenceController;
import com.android.settings.PrivacyPasswordUnlockStateController;
import com.android.settings.R;
import com.android.settings.core.OnActivityResultListener;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.EmergencyBroadcastPreferenceController;
import com.android.settings.search.tree.MiuiSecurityAndPrivacySettingsTree;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes2.dex */
public class MiuiSecurityAndPrivacySettings extends DashboardFragment implements OnActivityResultListener {
    private UnlockModeCardPreferenceController mCardPreferenceController;
    private MiuiChangeProfileScreenLockPreferenceController mChangeProfileScreenLockPreferenceController;
    private MiuiLockUnificationPreferenceController mLockUnificationPreferenceController;
    private PrivacyPasswordUnlockStateController mPrivacyPasswordUnlockStateController;
    private View mRootView;
    private PreferenceCategory mWorkProfileCategory;

    private List<AbstractPreferenceController> buildPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        UnlockModeCardPreferenceController unlockModeCardPreferenceController = new UnlockModeCardPreferenceController(getActivity(), "unlock_mode_card_preference", this);
        this.mCardPreferenceController = unlockModeCardPreferenceController;
        arrayList.add(unlockModeCardPreferenceController);
        arrayList.add(new ManagePasswordPreferenceController(context, getActivity()));
        PrivacyPasswordUnlockStateController privacyPasswordUnlockStateController = new PrivacyPasswordUnlockStateController(context, "privacy_password");
        this.mPrivacyPasswordUnlockStateController = privacyPasswordUnlockStateController;
        arrayList.add(privacyPasswordUnlockStateController);
        arrayList.add(new EmergencyBroadcastPreferenceController(context, MiuiSecurityAndPrivacySettingsTree.CELL_BROADCAST_SETTINGS));
        arrayList.add(new PrivacyPolicyController(context, "keyguard_privacy_policy"));
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
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public int getPageIndex() {
        return 3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.security_privacy_settings;
    }

    public void launchConfirmDeviceLockForUnification() {
        this.mLockUnificationPreferenceController.launchConfirmDeviceLockForUnification();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        this.mCardPreferenceController.handleActivityResult(i, i2, intent);
        this.mLockUnificationPreferenceController.handleActivityResult(i, i2, intent);
        this.mPrivacyPasswordUnlockStateController.handleActivityResult(i, i2);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mChangeProfileScreenLockPreferenceController = new MiuiChangeProfileScreenLockPreferenceController(getActivity());
        this.mLockUnificationPreferenceController = new MiuiLockUnificationPreferenceController(getActivity(), this, this);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mRootView == null) {
            View inflate = layoutInflater.inflate(R.layout.security_privacy_settings, viewGroup, false);
            this.mRootView = inflate;
            ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
            View view = (View) getListView().getParent();
            if (view instanceof SpringBackLayout) {
                view.setEnabled(false);
            }
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public void onFragmentResult(int i, Bundle bundle) {
        int i2;
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
            i2 = bundle.getInt("miui_security_fragment_result");
        } else {
            i2 = -1;
        }
        this.mCardPreferenceController.handleActivityResult(i, i2, intent);
        this.mLockUnificationPreferenceController.handleActivityResult(i, i2, intent);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("unlock_set_or_change_profile".equals(preference.getKey())) {
            this.mChangeProfileScreenLockPreferenceController.handlePreferenceTreeClick(this);
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (!this.mChangeProfileScreenLockPreferenceController.isAvailable() && !this.mLockUnificationPreferenceController.isAvailable()) {
            PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("security_category_profile");
            this.mWorkProfileCategory = preferenceCategory;
            if (preferenceCategory != null) {
                getPreferenceScreen().removePreference(this.mWorkProfileCategory);
                return;
            }
            return;
        }
        if (this.mChangeProfileScreenLockPreferenceController.isAvailable()) {
            this.mChangeProfileScreenLockPreferenceController.displayPreference(getPreferenceScreen());
            this.mChangeProfileScreenLockPreferenceController.updateState();
        }
        if (this.mLockUnificationPreferenceController.isAvailable()) {
            this.mLockUnificationPreferenceController.displayPreference(getPreferenceScreen());
            this.mLockUnificationPreferenceController.updateState();
        }
    }

    public void unifyUncompliantLocks() {
        this.mLockUnificationPreferenceController.unifyUncompliantLocks();
    }

    public void updateUnificationPreference() {
        this.mLockUnificationPreferenceController.updateState();
    }
}
