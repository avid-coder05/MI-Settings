package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.miui.AppOpsUtils;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.FixedSizeRadioButtonPreference;
import java.util.List;
import java.util.Objects;
import miui.content.res.ThemeResources;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class PreferredSettings extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class PreferredSettingsFragment extends SettingsPreferenceFragment {
        private String mBrowserEntry;
        private int mCurrentPos = -1;
        private String mGalleryEntry;
        private Intent mIntent;
        private IntentFilter mIntentFilter;
        private List<ResolveInfo> mList;
        private String mPackageName;
        private PackageManager mPackgeManager;
        private ResolveInfo mPreferedApp;
        private String mPreferredLabel;

        private void addPreference(List<ResolveInfo> list, ResolveInfo resolveInfo) {
            for (int i = 0; i < list.size(); i++) {
                ResolveInfo resolveInfo2 = list.get(i);
                if (resolveInfo2 != null) {
                    FixedSizeRadioButtonPreference fixedSizeRadioButtonPreference = new FixedSizeRadioButtonPreference(getPrefContext());
                    boolean isSame = isSame(resolveInfo2, resolveInfo);
                    this.mCurrentPos = isSame ? i : this.mCurrentPos;
                    fixedSizeRadioButtonPreference.setKey(String.valueOf(i));
                    fixedSizeRadioButtonPreference.setTitle(resolveInfo2.activityInfo.applicationInfo.loadLabel(this.mPackgeManager));
                    fixedSizeRadioButtonPreference.setIcon(resolveInfo2.activityInfo.loadIcon(this.mPackgeManager));
                    fixedSizeRadioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                    getPreferenceScreen().addPreference(fixedSizeRadioButtonPreference);
                    fixedSizeRadioButtonPreference.setChecked(isSame);
                }
            }
        }

        private boolean isSame(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
            return resolveInfo != null && resolveInfo2 != null && Objects.equals(resolveInfo.activityInfo.name, resolveInfo2.activityInfo.name) && Objects.equals(resolveInfo.activityInfo.processName, resolveInfo2.activityInfo.processName);
        }

        private List<ResolveInfo> queryResolveInfoList() {
            List<ResolveInfo> queryIntentActivities = getPrefContext().getPackageManager().queryIntentActivities(this.mIntent, 131072);
            int size = queryIntentActivities.size();
            if (size > 0) {
                ResolveInfo resolveInfo = queryIntentActivities.get(0);
                if (resolveInfo.system) {
                    this.mPreferedApp = resolveInfo;
                }
                for (int i = 1; i < size; i++) {
                    ResolveInfo resolveInfo2 = queryIntentActivities.get(i);
                    if (resolveInfo.priority != resolveInfo2.priority || resolveInfo.isDefault != resolveInfo2.isDefault) {
                        while (i < size) {
                            queryIntentActivities.remove(i);
                            size--;
                        }
                    }
                    setPreferedApp(resolveInfo2);
                }
            }
            return queryIntentActivities;
        }

        private void setDefaultBrowser(ResolveInfo resolveInfo) {
            this.mPackgeManager.setDefaultBrowserPackageNameAsUser(resolveInfo.activityInfo.packageName, getPrefContext().getUserId());
            this.mPackageName = resolveInfo.activityInfo.packageName;
        }

        private void setPreferedApp(ResolveInfo resolveInfo) {
            if (resolveInfo.system) {
                if (this.mPreferedApp == null) {
                    this.mPreferedApp = resolveInfo;
                } else if (this.mPackgeManager.checkSignatures(resolveInfo.activityInfo.packageName, ThemeResources.FRAMEWORK_PACKAGE) == 0) {
                    this.mPreferedApp = resolveInfo;
                }
            }
        }

        private void setPreferredApplication(ResolveInfo resolveInfo) {
            if (!AppOpsUtils.isXOptMode() || !this.mPreferredLabel.equalsIgnoreCase(this.mGalleryEntry)) {
                setPreferredApplication(resolveInfo, this.mIntentFilter);
                return;
            }
            IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
            intentFilter.addCategory("android.intent.category.APP_GALLERY");
            setPreferredApplication(resolveInfo, intentFilter);
        }

        private void setPreferredApplication(ResolveInfo resolveInfo, IntentFilter intentFilter) {
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> queryIntentActivities = packageManager.queryIntentActivities(this.mIntent, 131072);
            int size = queryIntentActivities.size();
            ComponentName[] componentNameArr = new ComponentName[size];
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                ResolveInfo resolveInfo2 = queryIntentActivities.get(i2);
                ActivityInfo activityInfo = resolveInfo2.activityInfo;
                componentNameArr[i2] = new ComponentName(activityInfo.packageName, activityInfo.name);
                int i3 = resolveInfo2.match;
                if (i3 > i) {
                    i = i3;
                }
            }
            this.mIntentFilter.addCategory("android.intent.category.DEFAULT");
            this.mIntentFilter.addCategory("android.intent.category.BROWSABLE");
            if (intentFilter.countDataAuthorities() == 0 && intentFilter.countDataPaths() == 0 && intentFilter.countDataSchemes() <= 1 && intentFilter.countDataTypes() == 0) {
                packageManager.replacePreferredActivity(intentFilter, i, componentNameArr, new ComponentName("com.no.such.packagename", "com.no.such.packagename.no.such.class"));
                packageManager.clearPackagePreferredActivities("com.no.such.packagename");
            } else if (!TextUtils.isEmpty(this.mPackageName)) {
                packageManager.clearPackagePreferredActivities(this.mPackageName);
            }
            if (resolveInfo != null) {
                IntentFilter intentFilter2 = new IntentFilter(intentFilter);
                ActivityInfo activityInfo2 = resolveInfo.activityInfo;
                packageManager.addPreferredActivity(intentFilter2, i, componentNameArr, new ComponentName(activityInfo2.packageName, activityInfo2.name));
            } else {
                resolveInfo = packageManager.resolveActivity(this.mIntent, 0);
            }
            this.mPackageName = resolveInfo.activityInfo.packageName;
        }

        private void setSelect(int i) {
            List<ResolveInfo> list = this.mList;
            int size = list != null ? list.size() : 0;
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            if (preferenceScreen.getPreferenceCount() != size) {
                return;
            }
            int i2 = 0;
            while (i2 < size) {
                RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.getPreference(i2);
                boolean z = i2 == i;
                radioButtonPreference.setChecked(z);
                if (z) {
                    ResolveInfo resolveInfo = this.mList.get(i2);
                    setPreferredApplication(resolveInfo);
                    if (this.mPreferredLabel.equalsIgnoreCase(this.mBrowserEntry)) {
                        setDefaultBrowser(resolveInfo);
                    } else {
                        setPreferredApplication(resolveInfo);
                    }
                }
                i2++;
            }
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
        public void onCreatePreferences(Bundle bundle, String str) {
            super.onCreatePreferences(bundle, str);
            addPreferencesFromResource(R.xml.preferred_app_settings);
            this.mPackgeManager = getPrefContext().getPackageManager();
            Bundle arguments = getArguments();
            if (arguments != null) {
                this.mIntent = (Intent) arguments.getParcelable("preferred_app_intent");
                this.mIntentFilter = (IntentFilter) arguments.getParcelable("preferred_app_intent_filter");
                this.mPackageName = arguments.getString("preferred_app_package_name");
                this.mPreferredLabel = arguments.getString("preferred_label");
                this.mBrowserEntry = arguments.getString("preferred_browser_entry");
                this.mGalleryEntry = arguments.getString("preferred_gallery_entry");
                ResolveInfo resolveActivity = this.mPackgeManager.resolveActivity(this.mIntent, 0);
                List<ResolveInfo> queryResolveInfoList = queryResolveInfoList();
                this.mList = queryResolveInfoList;
                addPreference(queryResolveInfoList, resolveActivity);
            }
        }

        @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onDestroy() {
            super.onDestroy();
            getPreferenceScreen().removeAll();
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
            int intValue = Integer.valueOf(radioButtonPreference.getKey()).intValue();
            if (this.mCurrentPos == intValue) {
                radioButtonPreference.setChecked(true);
                return false;
            }
            setSelect(intValue);
            this.mCurrentPos = intValue;
            return super.onPreferenceTreeClick(preference);
        }
    }

    private void addFragment(Intent intent, IntentFilter intentFilter, String str, String str2, String str3, String str4) {
        PreferredSettingsFragment preferredSettingsFragment = new PreferredSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("preferred_app_intent", intent);
        bundle.putParcelable("preferred_app_intent_filter", intentFilter);
        bundle.putString("preferred_app_package_name", str);
        bundle.putString("preferred_label", str2);
        bundle.putString("preferred_browser_entry", str3);
        bundle.putString("preferred_gallery_entry", str4);
        preferredSettingsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.preference_container, preferredSettingsFragment).commit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        IntentFilter intentFilter;
        Intent intent;
        String str;
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setDisplayHomeAsUpEnabled(true);
        }
        IntentFilter intentFilter2 = (IntentFilter) getIntent().getParcelableExtra("preferred_app_intent_filter");
        String stringExtra = getIntent().getStringExtra("preferred_app_package_name");
        Intent intent2 = (Intent) getIntent().getParcelableExtra("preferred_app_intent");
        String stringExtra2 = getIntent().getStringExtra("preferred_label");
        String[] stringArray = getResources().getStringArray(R.array.preferred_app_entries);
        String str2 = stringArray[3];
        String str3 = stringArray[5];
        if (intentFilter2 == null) {
            IntentFilter intentFilter3 = new IntentFilter();
            intentFilter3.addAction("android.intent.action.MAIN");
            intentFilter3.addCategory("android.intent.category.HOME");
            intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intentFilter = intentFilter3;
            str = stringArray[0];
        } else {
            intentFilter = intentFilter2;
            intent = intent2;
            str = stringExtra2;
        }
        appCompatActionBar.setTitle(str);
        if (bundle == null) {
            addFragment(intent, intentFilter, stringExtra, str, str2, str3);
        }
    }
}
