package com.android.settings.accessibility;

import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import com.android.settings.R;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class LaunchAccessibilityActivityPreferenceFragment extends ToggleFeaturePreferenceFragment {
    private AccessibilityShortcutInfo getAccessibilityShortcutInfo() {
        List installedAccessibilityShortcutListAsUser = AccessibilityManager.getInstance(getPrefContext()).getInstalledAccessibilityShortcutListAsUser(getPrefContext(), UserHandle.myUserId());
        int size = installedAccessibilityShortcutListAsUser.size();
        for (int i = 0; i < size; i++) {
            AccessibilityShortcutInfo accessibilityShortcutInfo = (AccessibilityShortcutInfo) installedAccessibilityShortcutListAsUser.get(i);
            ActivityInfo activityInfo = accessibilityShortcutInfo.getActivityInfo();
            if (this.mComponentName.getPackageName().equals(activityInfo.packageName) && this.mComponentName.getClassName().equals(activityInfo.name)) {
                return accessibilityShortcutInfo;
            }
        }
        return null;
    }

    private Intent getSettingsIntent(Bundle bundle) {
        String string = bundle.getString("settings_component_name");
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        Intent component = new Intent("android.intent.action.MAIN").setComponent(ComponentName.unflattenFromString(string));
        if (getPackageManager().queryIntentActivities(component, 0).isEmpty()) {
            return null;
        }
        return component;
    }

    private void initLaunchPreference() {
        Preference preference = new Preference(getPrefContext());
        preference.setKey("launch_preference");
        AccessibilityShortcutInfo accessibilityShortcutInfo = getAccessibilityShortcutInfo();
        preference.setTitle(accessibilityShortcutInfo == null ? "" : getString(R.string.accessibility_service_primary_open_title, accessibilityShortcutInfo.getActivityInfo().loadLabel(getPackageManager())));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.accessibility.LaunchAccessibilityActivityPreferenceFragment$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$initLaunchPreference$0;
                lambda$initLaunchPreference$0 = LaunchAccessibilityActivityPreferenceFragment.this.lambda$initLaunchPreference$0(preference2);
                return lambda$initLaunchPreference$0;
            }
        });
        getPreferenceScreen().addPreference(preference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initLaunchPreference$0(Preference preference) {
        AccessibilityStatsLogUtils.logAccessibilityServiceEnabled(this.mComponentName, true);
        launchShortcutTargetActivity(getPrefContext().getDisplayId(), this.mComponentName);
        return true;
    }

    private void launchShortcutTargetActivity(int i, ComponentName componentName) {
        Intent intent = new Intent();
        Bundle bundle = ActivityOptions.makeBasic().setLaunchDisplayId(i).toBundle();
        intent.setComponent(componentName);
        intent.addFlags(268435456);
        try {
            getPrefContext().startActivityAsUser(intent, bundle, UserHandle.of(UserHandle.myUserId()));
        } catch (ActivityNotFoundException unused) {
            Log.w("LaunchA11yActivity", "Target activity not found.");
        }
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected List<String> getPreferenceOrderList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("animated_image");
        arrayList.add("launch_preference");
        arrayList.add("general_categories");
        arrayList.add("html_description");
        return arrayList;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    int getUserShortcutTypes() {
        return AccessibilityUtil.getUserShortcutTypesFromSettings(getPrefContext(), this.mComponentName);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        initLaunchPreference();
        removePreference("use_service");
        return onCreateView;
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onPreferenceToggled(String str, boolean z) {
    }

    @Override // com.android.settings.accessibility.ToggleFeaturePreferenceFragment
    protected void onProcessArguments(Bundle bundle) {
        super.onProcessArguments(bundle);
        this.mComponentName = (ComponentName) bundle.getParcelable("component_name");
        this.mPackageName = getAccessibilityShortcutInfo().getActivityInfo().loadLabel(getPackageManager()).toString();
        int i = bundle.getInt("animated_image_res");
        if (i > 0) {
            this.mImageUri = new Uri.Builder().scheme("android.resource").authority(this.mComponentName.getPackageName()).appendPath(String.valueOf(i)).build();
        }
        this.mHtmlDescription = bundle.getCharSequence("html_description");
        String string = bundle.getString("settings_title");
        Intent settingsIntent = TextUtils.isEmpty(string) ? null : getSettingsIntent(bundle);
        this.mSettingsIntent = settingsIntent;
        if (settingsIntent == null) {
            string = null;
        }
        this.mSettingsTitle = string;
    }
}
