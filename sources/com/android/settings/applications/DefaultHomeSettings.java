package com.android.settings.applications;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.SearchUpdater;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.widget.FixedSizeRadioButtonPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import miui.os.Build;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AppCompatActivity;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class DefaultHomeSettings extends AppCompatActivity {

    /* loaded from: classes.dex */
    public static class DefaultHomeSettingsFragment extends SettingsPreferenceFragment {
        private int mCurrentPos = -1;
        private ResolveInfo mCurrentResolveInfo;
        private Intent mIntent;
        private IntentFilter mIntentFilter;
        private PackageManager mPackgeManager;
        private View mPreferenceRootView;
        private List<ResolveInfo> mResolveInfos;
        private View mRootView;

        private void addHomesInfo() {
            IntentFilter intentFilter = new IntentFilter();
            this.mIntentFilter = intentFilter;
            intentFilter.addAction("android.intent.action.MAIN");
            this.mIntentFilter.addCategory("android.intent.category.HOME");
            Intent intent = new Intent("android.intent.action.MAIN");
            this.mIntent = intent;
            intent.addCategory("android.intent.category.HOME");
            List<ResolveInfo> queryIntentActivities = this.mPackgeManager.queryIntentActivities(this.mIntent, 131072);
            this.mResolveInfos = new ArrayList();
            int size = queryIntentActivities.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    ResolveInfo resolveInfo = queryIntentActivities.get(i);
                    if (!"com.android.settings".equals(resolveInfo.activityInfo.packageName)) {
                        this.mResolveInfos.add(resolveInfo);
                    }
                }
            }
        }

        private void addOlderModeLink(LayoutInflater layoutInflater) {
            if (isAddOlderModeLink()) {
                this.mRootView.findViewById(R.id.recommend_view).setVisibility(0);
                LinearLayout linearLayout = (LinearLayout) this.mRootView.findViewById(R.id.line_layout);
                RelativeLayout relativeLayout = (RelativeLayout) layoutInflater.inflate(R.layout.recommend_item, (ViewGroup) null);
                TextView textView = (TextView) relativeLayout.findViewById(R.id.item_view);
                textView.setText(R.string.oldman_mode_settings);
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.DefaultHomeSettings.DefaultHomeSettingsFragment.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        DefaultHomeSettingsFragment.this.startActivity(new Intent("com.xiaomi.action.ENTER_ELDERLY_MODE"));
                    }
                });
                linearLayout.addView(relativeLayout);
            }
        }

        private void addPreferences() {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            ResolveInfo currentDefaultHome = DefaultHomeSettings.getCurrentDefaultHome(this.mPackgeManager);
            ResolveInfo resolveInfo = this.mCurrentResolveInfo;
            if (resolveInfo == null || !isSame(currentDefaultHome, resolveInfo)) {
                this.mCurrentResolveInfo = currentDefaultHome;
                preferenceScreen.removeAll();
                for (int i = 0; i < this.mResolveInfos.size(); i++) {
                    ResolveInfo resolveInfo2 = this.mResolveInfos.get(i);
                    if (resolveInfo2 != null) {
                        FixedSizeRadioButtonPreference fixedSizeRadioButtonPreference = new FixedSizeRadioButtonPreference(getPrefContext());
                        boolean isSame = isSame(resolveInfo2, this.mCurrentResolveInfo);
                        this.mCurrentPos = isSame ? i : this.mCurrentPos;
                        fixedSizeRadioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
                        fixedSizeRadioButtonPreference.setKey(String.valueOf(i));
                        fixedSizeRadioButtonPreference.setTitle(resolveInfo2.activityInfo.applicationInfo.loadLabel(this.mPackgeManager));
                        fixedSizeRadioButtonPreference.setIcon(resolveInfo2.activityInfo.loadIcon(this.mPackgeManager));
                        preferenceScreen.addPreference(fixedSizeRadioButtonPreference);
                        fixedSizeRadioButtonPreference.setChecked(isSame);
                    }
                }
            }
        }

        private boolean isAddOlderModeLink() {
            return UserHandle.myUserId() == 0 && !SettingsFeatures.isNeedRemoveEasyMode(getContext());
        }

        private boolean isSame(ResolveInfo resolveInfo, ResolveInfo resolveInfo2) {
            return resolveInfo != null && resolveInfo2 != null && Objects.equals(resolveInfo.activityInfo.name, resolveInfo2.activityInfo.name) && Objects.equals(resolveInfo.activityInfo.processName, resolveInfo2.activityInfo.processName);
        }

        private boolean setPreferredApplication(int i) {
            ResolveInfo resolveInfo = this.mResolveInfos.get(i);
            if (!Build.IS_INTERNATIONAL_BUILD && this.mIntentFilter.hasCategory("android.intent.category.HOME") && shouldBlockThirdDesktop(resolveInfo.activityInfo.packageName)) {
                ((RadioButtonPreference) getPreferenceScreen().getPreference(i)).setChecked(false);
                startActivity(new Intent().setClassName(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, "com.miui.securitycenter.activity.ThirdDesktopAlertActivity"));
                return false;
            }
            setSelect(i);
            List<ResolveInfo> queryIntentActivities = this.mPackgeManager.queryIntentActivities(this.mIntent, 131072);
            int size = queryIntentActivities.size();
            ComponentName[] componentNameArr = new ComponentName[size];
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                ResolveInfo resolveInfo2 = queryIntentActivities.get(i3);
                ActivityInfo activityInfo = resolveInfo2.activityInfo;
                componentNameArr[i3] = new ComponentName(activityInfo.packageName, activityInfo.name);
                int i4 = resolveInfo2.match;
                if (i4 > i2) {
                    i2 = i4;
                }
            }
            IntentFilter intentFilter = new IntentFilter(this.mIntentFilter);
            intentFilter.addCategory("android.intent.category.DEFAULT");
            ComponentName homeActivities = this.mPackgeManager.getHomeActivities(new ArrayList());
            if (homeActivities != null && !TextUtils.isEmpty(homeActivities.getPackageName())) {
                this.mPackgeManager.clearPackagePreferredActivities(homeActivities.getPackageName());
            }
            PackageManager packageManager = this.mPackgeManager;
            ActivityInfo activityInfo2 = resolveInfo.activityInfo;
            packageManager.addPreferredActivity(intentFilter, i2, componentNameArr, new ComponentName(activityInfo2.packageName, activityInfo2.name));
            return true;
        }

        private void setSelect(int i) {
            PreferenceScreen preferenceScreen = getPreferenceScreen();
            int preferenceCount = preferenceScreen.getPreferenceCount();
            int i2 = 0;
            while (i2 < preferenceCount) {
                ((RadioButtonPreference) preferenceScreen.getPreference(i2)).setChecked(i2 == i);
                i2++;
            }
            this.mCurrentPos = i;
        }

        /* JADX WARN: Removed duplicated region for block: B:10:0x002d  */
        /* JADX WARN: Removed duplicated region for block: B:17:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private boolean shouldBlockThirdDesktop(java.lang.String r5) {
            /*
                r4 = this;
                r0 = 0
                android.content.ContentResolver r4 = r4.getContentResolver()     // Catch: java.lang.Exception -> L2f
                java.lang.String r1 = "content://com.miui.sec.THIRD_DESKTOP"
                android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch: java.lang.Exception -> L2f
                java.lang.String r2 = "getModeAndList"
                r3 = 0
                android.os.Bundle r4 = r4.call(r1, r2, r3, r3)     // Catch: java.lang.Exception -> L2f
                java.lang.String r1 = "mode"
                int r1 = r4.getInt(r1, r0)     // Catch: java.lang.Exception -> L2f
                java.lang.String r2 = "list"
                java.util.ArrayList r4 = r4.getStringArrayList(r2)     // Catch: java.lang.Exception -> L2f
                r2 = 1
                if (r4 == 0) goto L2a
                boolean r4 = r4.contains(r5)     // Catch: java.lang.Exception -> L2f
                if (r4 == 0) goto L2a
                r4 = r2
                goto L2b
            L2a:
                r4 = r0
            L2b:
                if (r1 != r4) goto L2e
                r0 = r2
            L2e:
                return r0
            L2f:
                r4 = move-exception
                java.lang.String r5 = "DefaultHomeSettings"
                java.lang.String r1 = "get third desktop provider exception!"
                android.util.Log.e(r5, r1, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.DefaultHomeSettings.DefaultHomeSettingsFragment.shouldBlockThirdDesktop(java.lang.String):boolean");
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.core.InstrumentedPreferenceFragment
        public int getPreferenceScreenResId() {
            return R.xml.preferred_app_settings;
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
        public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
            if (this.mRootView == null) {
                this.mRootView = layoutInflater.inflate(R.layout.preferred_default_home_settings, viewGroup, false);
                this.mPackgeManager = getPrefContext().getPackageManager();
                if (!RegionUtils.IS_JP_SB) {
                    addOlderModeLink(layoutInflater);
                }
                ViewGroup viewGroup2 = (ViewGroup) this.mRootView.findViewById(R.id.prefs_container);
                View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
                this.mPreferenceRootView = onCreateView;
                viewGroup2.addView(onCreateView);
                addHomesInfo();
            }
            return this.mRootView;
        }

        @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
        public boolean onPreferenceTreeClick(Preference preference) {
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
            int intValue = Integer.valueOf(radioButtonPreference.getKey()).intValue();
            if (this.mCurrentPos == intValue) {
                radioButtonPreference.setChecked(true);
                return false;
            } else if (setPreferredApplication(intValue)) {
                return super.onPreferenceTreeClick(preference);
            } else {
                return false;
            }
        }

        @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
        public void onResume() {
            super.onResume();
            addPreferences();
        }
    }

    private void addFragment() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        int i = R.id.preference_container;
        if (supportFragmentManager.findFragmentById(i) == null) {
            getSupportFragmentManager().beginTransaction().add(i, new DefaultHomeSettingsFragment()).commit();
        }
    }

    public static ResolveInfo getCurrentDefaultHome(PackageManager packageManager) {
        if (packageManager == null) {
            return null;
        }
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        return packageManager.resolveActivity(intent, SearchUpdater.GOOGLE);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.preference_activity);
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.setDisplayHomeAsUpEnabled(true);
            appCompatActionBar.setTitle(R.string.defalut_launcher_title);
        }
        addFragment();
    }
}
