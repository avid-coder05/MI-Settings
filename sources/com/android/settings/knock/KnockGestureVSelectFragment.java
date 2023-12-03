package com.android.settings.knock;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.KeyAndGestureShortcutStatHelperFragment;
import com.android.settings.MiuiShortcut$Key;
import com.android.settings.R;
import com.android.settings.search.SearchUpdater;
import com.android.settings.stat.commonpreference.KeySettingsStatHelper;
import java.util.ArrayList;
import java.util.List;
import miui.cloud.sync.MiCloudStatusInfo;
import miui.os.Build;
import miuix.preference.RadioButtonPreference;
import miuix.preference.RadioButtonPreferenceCategory;

/* loaded from: classes.dex */
public class KnockGestureVSelectFragment extends KeyAndGestureShortcutStatHelperFragment {
    private Context mContext;
    private List<String> mFeatureList = new ArrayList();
    private RadioButtonPreferenceCategory mKeyGestureFunctionOptional;
    private Resources mResources;

    private boolean appIsIntall(String str) {
        Intent intent = new Intent();
        intent.setPackage(str);
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE);
        return queryIntentActivities != null && queryIntentActivities.size() > 0;
    }

    private void initFeatureList() {
        boolean appIsIntall = appIsIntall("com.tencent.mm");
        boolean appIsIntall2 = appIsIntall("com.eg.android.AlipayGphone");
        if (!Build.IS_GLOBAL_BUILD) {
            this.mFeatureList.add("launch_voice_assistant");
            this.mFeatureList.add("launch_ai_shortcut");
        }
        if (appIsIntall2) {
            this.mFeatureList.add("launch_alipay_payment_code");
        }
        if (appIsIntall) {
            this.mFeatureList.add("launch_wechat_payment_code");
        }
        if (appIsIntall2) {
            this.mFeatureList.add("launch_alipay_scanner");
        }
        if (appIsIntall) {
            this.mFeatureList.add("launch_wechat_scanner");
        }
        this.mFeatureList.add("turn_on_torch");
        this.mFeatureList.add("launch_camera");
        this.mFeatureList.add("launch_calculator");
        this.mFeatureList.add("dump_log");
        this.mFeatureList.add(MiCloudStatusInfo.QuotaInfo.WARN_NONE);
    }

    @Override // com.android.settings.KeyAndGestureShortcutStatHelperFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.knock_settings_quick_feature_select_fragment);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mResources = activity.getResources();
        this.mPageTitle = KeySettingsStatHelper.GESTURE_V_PAGE_KEY;
        this.mKeyGestureFunctionOptional = (RadioButtonPreferenceCategory) findPreference("knock_gesture_function_optional");
        initFeatureList();
        String keyAndGestureShortcutSetFunction = MiuiShortcut$Key.getKeyAndGestureShortcutSetFunction(this.mContext, "knock_gesture_v");
        for (String str : this.mFeatureList) {
            RadioButtonPreference radioButtonPreference = new RadioButtonPreference(getPrefContext());
            radioButtonPreference.setLayoutResource(R.layout.miuix_preference_radiobutton_two_state_background);
            radioButtonPreference.setKey(str);
            radioButtonPreference.setTitle(this.mResources.getIdentifier(str, "string", this.mContext.getPackageName()));
            radioButtonPreference.setPersistent(false);
            this.mKeyGestureFunctionOptional.addPreference(radioButtonPreference);
            if (str.equals(keyAndGestureShortcutSetFunction)) {
                this.mKeyGestureFunctionOptional.setCheckedPreference(radioButtonPreference);
            }
        }
        if (this.mKeyGestureFunctionOptional.getCheckedPosition() < 0) {
            this.mKeyGestureFunctionOptional.setCheckedPreference((RadioButtonPreference) this.mKeyGestureFunctionOptional.getPreference(r7.getPreferenceCount() - 1));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof RadioButtonPreference) {
            RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preference;
            this.mKeyGestureFunctionOptional.setCheckedPreference(radioButtonPreference);
            Settings.System.putStringForUser(getContentResolver(), "knock_gesture_v", radioButtonPreference.getKey(), -2);
            this.mShortcutMap.put("knock_gesture_v", radioButtonPreference.getKey());
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ActionBar actionBar = getActivity().getActionBar();
        Context context = this.mContext;
        if (context == null || actionBar == null) {
            return;
        }
        actionBar.setSubtitle(MiuiShortcut$Key.getResourceForKey("knock_edge_area_invalid", context));
    }
}
