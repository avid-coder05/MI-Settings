package com.android.settings.accounts;

import android.accounts.Account;
import android.accounts.AuthenticatorDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.location.LocationSettings;
import com.android.settings.search.SearchUpdater;
import com.android.settings.utils.LocalClassLoaderContextThemeWrapper;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.core.instrumentation.Instrumentable;
import miuix.preference.PreferenceFragment;

/* loaded from: classes.dex */
public class AccountTypePreferenceLoader {
    private AuthenticatorHelper mAuthenticatorHelper;
    private PreferenceFragment mFragment;
    private UserHandle mUserHandle;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class FragmentStarter implements Preference.OnPreferenceClickListener {
        private final String mClass;
        private final int mTitleRes;

        public FragmentStarter(String str, int i) {
            this.mClass = str;
            this.mTitleRes = i;
        }

        @Override // androidx.preference.Preference.OnPreferenceClickListener
        public boolean onPreferenceClick(Preference preference) {
            new SubSettingLauncher(preference.getContext()).setTitleRes(this.mTitleRes).setDestination(this.mClass).setSourceMetricsCategory(AccountTypePreferenceLoader.this.mFragment instanceof Instrumentable ? ((Instrumentable) AccountTypePreferenceLoader.this.mFragment).getMetricsCategory() : 0).launch();
            if (this.mClass.equals(LocationSettings.class.getName())) {
                AccountTypePreferenceLoader.this.mFragment.getActivity().sendBroadcast(new Intent("com.android.settings.accounts.LAUNCHING_LOCATION_SETTINGS"), "android.permission.WRITE_SECURE_SETTINGS");
                return true;
            }
            return true;
        }
    }

    public AccountTypePreferenceLoader(PreferenceFragment preferenceFragment, AuthenticatorHelper authenticatorHelper, UserHandle userHandle) {
        this.mFragment = preferenceFragment;
        this.mAuthenticatorHelper = authenticatorHelper;
        this.mUserHandle = userHandle;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSafeIntent(PackageManager packageManager, Intent intent, String str) {
        AuthenticatorDescription accountTypeDescription = this.mAuthenticatorHelper.getAccountTypeDescription(str);
        ResolveInfo resolveActivityAsUser = packageManager.resolveActivityAsUser(intent, 0, this.mUserHandle.getIdentifier());
        if (resolveActivityAsUser == null) {
            return false;
        }
        try {
            return resolveActivityAsUser.activityInfo.applicationInfo.uid == packageManager.getApplicationInfo(accountTypeDescription.packageName, 0).uid;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("AccountTypePrefLoader", "Intent considered unsafe due to exception.", e);
            return false;
        }
    }

    public PreferenceScreen addPreferencesForType(String str, PreferenceScreen preferenceScreen) {
        PreferenceScreen preferenceScreen2;
        AuthenticatorDescription accountTypeDescription;
        AuthenticatorDescription authenticatorDescription = null;
        if (this.mAuthenticatorHelper.containsAccountType(str)) {
            try {
                accountTypeDescription = this.mAuthenticatorHelper.getAccountTypeDescription(str);
            } catch (PackageManager.NameNotFoundException unused) {
                preferenceScreen2 = null;
            } catch (Resources.NotFoundException unused2) {
                preferenceScreen2 = null;
            }
            if (accountTypeDescription != null) {
                try {
                } catch (PackageManager.NameNotFoundException unused3) {
                    preferenceScreen2 = null;
                } catch (Resources.NotFoundException unused4) {
                    preferenceScreen2 = null;
                }
                if (accountTypeDescription.accountPreferencesId != 0) {
                    Context createPackageContextAsUser = this.mFragment.getActivity().createPackageContextAsUser(accountTypeDescription.packageName, 0, this.mUserHandle);
                    Resources.Theme newTheme = this.mFragment.getResources().newTheme();
                    newTheme.applyStyle(R.style.Theme_SettingsBase, true);
                    LocalClassLoaderContextThemeWrapper localClassLoaderContextThemeWrapper = new LocalClassLoaderContextThemeWrapper(getClass(), createPackageContextAsUser, 0);
                    localClassLoaderContextThemeWrapper.getTheme().setTo(newTheme);
                    preferenceScreen2 = this.mFragment.getPreferenceManager().inflateFromResource(localClassLoaderContextThemeWrapper, accountTypeDescription.accountPreferencesId, preferenceScreen);
                    for (int i = 0; i < preferenceScreen2.getPreferenceCount(); i++) {
                        try {
                            Preference preference = preferenceScreen2.getPreference(i);
                            if (preference != null && TextUtils.equals("account_settings", preference.getKey())) {
                                preference.setLayoutResource(R.layout.miuix_preference_layout);
                            }
                        } catch (PackageManager.NameNotFoundException unused5) {
                            authenticatorDescription = accountTypeDescription;
                            Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
                            return preferenceScreen2;
                        } catch (Resources.NotFoundException unused6) {
                            authenticatorDescription = accountTypeDescription;
                            Log.w("AccountTypePrefLoader", "Couldn't load preferences.xml file from " + authenticatorDescription.packageName);
                            return preferenceScreen2;
                        }
                    }
                    return preferenceScreen2;
                }
                return null;
            }
            return null;
        }
        return null;
    }

    public void updatePreferenceIntents(PreferenceGroup preferenceGroup, final String str, Account account) {
        final PackageManager packageManager = this.mFragment.getActivity().getPackageManager();
        int i = 0;
        while (i < preferenceGroup.getPreferenceCount()) {
            Preference preference = preferenceGroup.getPreference(i);
            if (preference instanceof PreferenceGroup) {
                updatePreferenceIntents((PreferenceGroup) preference, str, account);
            }
            Intent intent = preference.getIntent();
            if (intent != null) {
                if (TextUtils.equals(intent.getAction(), "android.settings.LOCATION_SOURCE_SETTINGS")) {
                    preference.setOnPreferenceClickListener(new FragmentStarter(LocationSettings.class.getName(), R.string.location_settings_title));
                } else if (packageManager.resolveActivityAsUser(intent, SearchUpdater.GOOGLE, this.mUserHandle.getIdentifier()) == null) {
                    preferenceGroup.removePreference(preference);
                } else {
                    intent.putExtra("account", account);
                    intent.setFlags(intent.getFlags() | 268435456);
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accounts.AccountTypePreferenceLoader.1
                        @Override // androidx.preference.Preference.OnPreferenceClickListener
                        public boolean onPreferenceClick(Preference preference2) {
                            Intent intent2 = preference2.getIntent();
                            if (AccountTypePreferenceLoader.this.isSafeIntent(packageManager, intent2, str)) {
                                AccountTypePreferenceLoader.this.mFragment.getActivity().startActivityAsUser(intent2, AccountTypePreferenceLoader.this.mUserHandle);
                                return true;
                            }
                            Log.e("AccountTypePrefLoader", "Refusing to launch authenticator intent becauseit exploits Settings permissions: " + intent2);
                            return true;
                        }
                    });
                }
            }
            i++;
        }
    }
}
