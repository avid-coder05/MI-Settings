package com.android.settings.accessibility;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.preference.PreferenceScreen;
import com.android.internal.content.PackageMonitor;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.accessibility.voiceaccess.VoiceAccessController;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class InstalledAccessibilityService extends SettingsPreferenceFragment {
    private static final ComponentName ENVSOUNDRECOGNITION = ComponentName.unflattenFromString(EnvironmentSoundRecognitionController.ENVIRONMENT_SOUND_RECOGNITION_SERVICE);
    public static List<String> HIDE_SERVICES_LIST;
    public static List<Integer> mNoNeedDisplayKeyList;
    private DevicePolicyManager mDpm;
    private final Handler mHandler = new Handler();
    private final Runnable mUpdateRunnable = new Runnable() { // from class: com.android.settings.accessibility.InstalledAccessibilityService.1
        @Override // java.lang.Runnable
        public void run() {
            if (InstalledAccessibilityService.this.getActivity() != null) {
                InstalledAccessibilityService.this.updatePreferences();
            }
        }
    };
    private final PackageMonitor mSettingsPackageMonitor = new PackageMonitor() { // from class: com.android.settings.accessibility.InstalledAccessibilityService.2
        private void sendUpdate() {
            InstalledAccessibilityService.this.mHandler.postDelayed(InstalledAccessibilityService.this.mUpdateRunnable, 1000L);
        }

        public void onPackageAdded(String str, int i) {
            sendUpdate();
        }

        public void onPackageAppeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageDisappeared(String str, int i) {
            sendUpdate();
        }

        public void onPackageRemoved(String str, int i) {
            sendUpdate();
        }
    };

    static {
        ArrayList arrayList = new ArrayList();
        mNoNeedDisplayKeyList = arrayList;
        arrayList.add(Integer.valueOf(R.array.config_preinstalled_screen_reader_services));
        mNoNeedDisplayKeyList.add(Integer.valueOf(R.array.config_preinstalled_display_services));
        mNoNeedDisplayKeyList.add(Integer.valueOf(R.array.config_preinstalled_interaction_control_services));
        mNoNeedDisplayKeyList.add(Integer.valueOf(R.array.config_downloaded_services));
        ArrayList arrayList2 = new ArrayList();
        HIDE_SERVICES_LIST = arrayList2;
        arrayList2.add("com.miui.accessibility/com.miui.accessibility.haptic.HapticAccessibilityService");
        HIDE_SERVICES_LIST.add(VoiceAccessController.VOICEACCESS_A11y_SERVICE);
        HIDE_SERVICES_LIST.add(EnvironmentSoundRecognitionController.ENVIRONMENT_SOUND_RECOGNITION_SERVICE);
    }

    public static List<ComponentName> buildNoNeedDisplayServices(Context context) {
        ArrayList arrayList = new ArrayList();
        Iterator<Integer> it = mNoNeedDisplayKeyList.iterator();
        while (it.hasNext()) {
            for (String str : context.getResources().getStringArray(it.next().intValue())) {
                arrayList.add(ComponentName.unflattenFromString(str));
            }
        }
        return arrayList;
    }

    private static void doShieldShortcut(final Context context, final ComponentName componentName) {
        if (context == null || componentName == null) {
            return;
        }
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.accessibility.InstalledAccessibilityService.3
            @Override // java.lang.Runnable
            public void run() {
                Context context2 = context;
                if (context2 == null || PreferredShortcuts.retrieveUserShortcutType(context2.getApplicationContext(), componentName.flattenToString(), 1) == 0) {
                    return;
                }
                AccessibilityUtil.optInAllValuesToSettings(context.getApplicationContext(), 0, componentName);
                AccessibilityUtil.optOutAllValuesFromSettings(context.getApplicationContext(), -1, componentName);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen == null) {
            return;
        }
        preferenceScreen.removeAll();
        List<RestrictedPreference> installedAccessibilityList = AccessibilitySettings.getInstalledAccessibilityList(getPrefContext(), true);
        int size = installedAccessibilityList.size();
        for (int i = 0; i < size; i++) {
            RestrictedPreference restrictedPreference = installedAccessibilityList.get(i);
            if (restrictedPreference != null && !HIDE_SERVICES_LIST.contains(restrictedPreference.getKey())) {
                preferenceScreen.addPreference(restrictedPreference);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return InstalledAccessibilityService.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.installed_accessible_services;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        this.mSettingsPackageMonitor.register(getActivity(), Looper.getMainLooper(), false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        updatePreferences();
        doShieldShortcut(getPrefContext().getApplicationContext(), ENVSOUNDRECOGNITION);
    }
}
