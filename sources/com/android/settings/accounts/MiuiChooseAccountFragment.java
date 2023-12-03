package com.android.settings.accounts;

import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SyncAdapterType;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.CharSequences;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.enterprise.EnterprisePrivacyFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.widget.FooterPreference;
import com.google.android.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import miui.content.res.IconCustomizer;

/* loaded from: classes.dex */
public class MiuiChooseAccountFragment extends SettingsPreferenceFragment {
    public HashSet<String> mAccountTypesFilter;
    private Activity mActivity;
    private PreferenceGroup mAddAccountGroup;
    private AuthenticatorDescription[] mAuthDescs;
    private String[] mAuthorities;
    private EnterprisePrivacyFeatureProvider mFeatureProvider;
    private final ArrayList<ProviderEntry> mProviderList = new ArrayList<>();
    private HashMap<String, ArrayList<String>> mAccountTypeToAuthorities = null;
    private Map<String, AuthenticatorDescription> mTypeToAuthDescription = new HashMap();
    private FooterPreference mEnterpriseDisclosurePreference = null;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ProviderEntry implements Comparable<ProviderEntry> {
        private final CharSequence name;
        private final String type;

        ProviderEntry(CharSequence charSequence, String str) {
            this.name = charSequence;
            this.type = str;
        }

        @Override // java.lang.Comparable
        public int compareTo(ProviderEntry providerEntry) {
            CharSequence charSequence = this.name;
            if (charSequence == null) {
                return -1;
            }
            CharSequence charSequence2 = providerEntry.name;
            if (charSequence2 == null) {
                return 1;
            }
            return CharSequences.compareToIgnoreCase(charSequence, charSequence2);
        }
    }

    private void addEnterpriseDisclosure() {
        CharSequence deviceOwnerDisclosure = this.mFeatureProvider.getDeviceOwnerDisclosure();
        if (deviceOwnerDisclosure == null) {
            return;
        }
        if (this.mEnterpriseDisclosurePreference == null) {
            FooterPreference footerPreference = new FooterPreference(getPrefContext());
            this.mEnterpriseDisclosurePreference = footerPreference;
            footerPreference.setSelectable(false);
        }
        this.mEnterpriseDisclosurePreference.setTitle(deviceOwnerDisclosure);
        this.mEnterpriseDisclosurePreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.accounts.MiuiChooseAccountFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MiuiChooseAccountFragment.this.lambda$addEnterpriseDisclosure$0(view);
            }
        });
        this.mEnterpriseDisclosurePreference.setLearnMoreContentDescription(getPrefContext().getString(R.string.footer_learn_more_content_description, getLabelName()));
        this.mAddAccountGroup.addPreference(this.mEnterpriseDisclosurePreference);
    }

    private void finishWithAccountType(int i, String str) {
        if (str == null) {
            getActivity().setResult(0, null);
            finish();
            return;
        }
        Intent intent = new Intent("android.settings.ADD_ACCOUNT_SETTINGS");
        intent.putExtra("selected_account", str);
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWithAccountType(String str) {
        finishWithAccountType(-1, str);
    }

    private String getLabelName() {
        return getPrefContext().getString(R.string.header_add_an_account);
    }

    private boolean isSetupWizard() {
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            return intent.getBooleanExtra("account_setup_wizard", false);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addEnterpriseDisclosure$0(View view) {
        getPrefContext().startActivity(new Intent("android.settings.ENTERPRISE_PRIVACY_SETTINGS"));
    }

    /* JADX WARN: Code restructure failed: missing block: B:41:0x0077, code lost:
    
        if (com.android.settings.accounts.MiuiManageAccountsSettings.isUserVisible(r2) == false) goto L27;
     */
    /* JADX WARN: Removed duplicated region for block: B:44:0x007c  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x0087  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void onAuthDescriptionsUpdated() {
        /*
            Method dump skipped, instructions count: 295
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.accounts.MiuiChooseAccountFragment.onAuthDescriptionsUpdated():void");
    }

    private void updateAuthDescriptions() {
        this.mAuthDescs = AccountManager.get(getActivity()).getAuthenticatorTypes();
        int i = 0;
        while (true) {
            AuthenticatorDescription[] authenticatorDescriptionArr = this.mAuthDescs;
            if (i >= authenticatorDescriptionArr.length) {
                onAuthDescriptionsUpdated();
                return;
            } else {
                this.mTypeToAuthDescription.put(authenticatorDescriptionArr[i].type, authenticatorDescriptionArr[i]);
                i++;
            }
        }
    }

    public ArrayList<String> getAuthoritiesForAccountType(String str) {
        if (this.mAccountTypeToAuthorities == null) {
            this.mAccountTypeToAuthorities = Maps.newHashMap();
            for (SyncAdapterType syncAdapterType : ContentResolver.getSyncAdapterTypes()) {
                ArrayList<String> arrayList = this.mAccountTypeToAuthorities.get(syncAdapterType.accountType);
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                    this.mAccountTypeToAuthorities.put(syncAdapterType.accountType, arrayList);
                }
                if (Log.isLoggable("ChooseAccountFragment", 2)) {
                    Log.d("ChooseAccountFragment", "added authority " + syncAdapterType.authority + " to accountType " + syncAdapterType.accountType);
                }
                arrayList.add(syncAdapterType.authority);
            }
        }
        return this.mAccountTypeToAuthorities.get(str);
    }

    protected Drawable getDrawableForType(String str) {
        Drawable drawable = null;
        if (!this.mTypeToAuthDescription.containsKey(str)) {
            return null;
        }
        try {
            AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
            Drawable drawable2 = getActivity().createPackageContext(authenticatorDescription.packageName, 0).getResources().getDrawable(authenticatorDescription.iconId);
            if (drawable2 != null) {
                try {
                    drawable2 = IconCustomizer.generateIconStyleDrawable(drawable2);
                } catch (PackageManager.NameNotFoundException unused) {
                    drawable = drawable2;
                    Log.w("ChooseAccountFragment", "No icon name for account type " + str);
                    return drawable;
                } catch (Resources.NotFoundException unused2) {
                    drawable = drawable2;
                    Log.w("ChooseAccountFragment", "No icon resource for account type " + str);
                    return drawable;
                }
            }
            return drawable2;
        } catch (PackageManager.NameNotFoundException unused3) {
        } catch (Resources.NotFoundException unused4) {
        }
    }

    protected CharSequence getLabelForType(String str) {
        if (this.mTypeToAuthDescription.containsKey(str)) {
            try {
                AuthenticatorDescription authenticatorDescription = this.mTypeToAuthDescription.get(str);
                return getActivity().createPackageContext(authenticatorDescription.packageName, 0).getResources().getText(authenticatorDescription.labelId);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.w("ChooseAccountFragment", "No label name for account type " + str);
            } catch (Resources.NotFoundException unused2) {
                Log.w("ChooseAccountFragment", "No label resource for account type " + str);
            }
        }
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiChooseAccountFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        addPreferencesFromResource(R.xml.add_account_settings);
        this.mAuthorities = getActivity().getIntent().getStringArrayExtra("authorities");
        String[] stringArrayExtra = getActivity().getIntent().getStringArrayExtra("account_types");
        if (stringArrayExtra != null) {
            this.mAccountTypesFilter = new HashSet<>();
            for (String str : stringArrayExtra) {
                this.mAccountTypesFilter.add(str);
            }
        }
        this.mAddAccountGroup = getPreferenceScreen();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mActivity = activity;
        this.mFeatureProvider = FeatureFactory.getFactory(activity).getEnterprisePrivacyFeatureProvider(this.mActivity);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.choose_account_settings, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        if (isSetupWizard()) {
            Button button = (Button) inflate.findViewById(R.id.next);
            button.setVisibility(0);
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accounts.MiuiChooseAccountFragment.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    MiuiChooseAccountFragment.this.finishWithAccountType(null);
                }
            });
        }
        return inflate;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof MiuiProviderPreference) {
            MiuiProviderPreference miuiProviderPreference = (MiuiProviderPreference) preference;
            if (Log.isLoggable("ChooseAccountFragment", 2)) {
                Log.v("ChooseAccountFragment", "Attempting to add account of type " + miuiProviderPreference.getAccountType());
            }
            finishWithAccountType(miuiProviderPreference.getAccountType());
            return true;
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mProviderList.clear();
        updateAuthDescriptions();
    }
}
