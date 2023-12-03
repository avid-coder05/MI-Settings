package com.android.settings.accessibility;

import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class AccessibilityButtonController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static final String ACCESSIBILITY_BUTTON_SP = "ACCESSIBILITY_BUTTON_SP";
    private static final String ACCESSIBILITY_MENU_SERVICE = "com.android.settings/com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService";
    private static final String ACCESSIBILITY_SELECTTOSPEAK_SERVICE = "com.google.android.marvin.talkback/com.google.android.accessibility.selecttospeak.SelectToSpeakService";
    public static final String IS_ACCESSIBILITY_BUTTON_OPEN = "is_accessibility_button_open";
    private static ArrayList<ComponentName> sAccessibilityServices;
    private CheckBoxPreference mPreference;
    private SharedPreferences mSharedPrefs;

    static {
        ArrayList<ComponentName> arrayList = new ArrayList<>();
        sAccessibilityServices = arrayList;
        arrayList.add(ComponentName.unflattenFromString("com.android.settings/com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService"));
        sAccessibilityServices.add(ComponentName.unflattenFromString("com.google.android.marvin.talkback/com.google.android.accessibility.selecttospeak.SelectToSpeakService"));
    }

    public AccessibilityButtonController(Context context, String str) {
        super(context, str);
        this.mSharedPrefs = context.getSharedPreferences(ACCESSIBILITY_BUTTON_SP, 0);
    }

    public static boolean hasAccessibilityButtonTargets(Context context) {
        return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), AccessibilityUtil.convertKeyFromSettings(1)));
    }

    public static boolean isAccessibilityButtonCheckboxOpen(Context context) {
        return context.getSharedPreferences(ACCESSIBILITY_BUTTON_SP, 0).getInt(IS_ACCESSIBILITY_BUTTON_OPEN, 1) == 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPreferenceChange$0() {
        if (this.mContext == null) {
            return;
        }
        Iterator<ComponentName> it = sAccessibilityServices.iterator();
        while (it.hasNext()) {
            AccessibilityUtils.setAccessibilityServiceState(this.mContext, it.next(), false);
        }
    }

    private void updateStatus() {
        this.mPreference.setChecked(isAccessibilityButtonCheckboxOpen(this.mContext) || hasAccessibilityButtonTargets(this.mContext));
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (CheckBoxPreference) preferenceScreen.findPreference(this.mPreferenceKey);
            updateStatus();
        }
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mPreferenceKey.equals(preference.getKey())) {
            boolean booleanValue = ((Boolean) obj).booleanValue();
            this.mSharedPrefs.edit().putInt(IS_ACCESSIBILITY_BUTTON_OPEN, booleanValue ? 1 : 0).apply();
            if (booleanValue) {
                return true;
            }
            Settings.Secure.putString(this.mContext.getContentResolver(), "accessibility_button_targets", "");
            ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.accessibility.AccessibilityButtonController$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AccessibilityButtonController.this.lambda$onPreferenceChange$0();
                }
            });
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (this.mPreference != null) {
            updateStatus();
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
