package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.lifecycle.LifecycleObserver;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.google.common.primitives.Ints;
import java.util.HashMap;
import java.util.Map;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class AccessibilityTimeoutController extends AbstractPreferenceController implements LifecycleObserver, PreferenceControllerMixin, Preference.OnPreferenceClickListener {
    private final Map<String, Integer> mAccessibilityTimeoutKeyToValueMap;
    private int mAccessibilityUiTimeoutValue;
    private final ContentResolver mContentResolver;
    private OnChangeListener mOnChangeListener;
    private RadioButtonPreference mPreference;
    private final String mPreferenceKey;
    private final Resources mResources;

    /* loaded from: classes.dex */
    public interface OnChangeListener {
        void onCheckedChanged(Preference preference);
    }

    public AccessibilityTimeoutController(Context context, Lifecycle lifecycle, String str) {
        super(context);
        this.mAccessibilityTimeoutKeyToValueMap = new HashMap();
        this.mContentResolver = context.getContentResolver();
        this.mResources = context.getResources();
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
        this.mPreferenceKey = str;
    }

    private int getAccessibilityTimeoutValue() {
        return getSecureAccessibilityTimeoutValue(this.mContentResolver, "accessibility_interactive_ui_timeout_ms");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static int getSecureAccessibilityTimeoutValue(ContentResolver contentResolver, String str) {
        Integer tryParse;
        String string = Settings.Secure.getString(contentResolver, str);
        if (string == null || (tryParse = Ints.tryParse(string)) == null) {
            return 0;
        }
        return tryParse.intValue();
    }

    private Map<String, Integer> getTimeoutValueToKeyMap() {
        if (this.mAccessibilityTimeoutKeyToValueMap.size() == 0) {
            String[] stringArray = this.mResources.getStringArray(R.array.accessibility_timeout_control_selector_keys);
            int[] intArray = this.mResources.getIntArray(R.array.accessibility_timeout_selector_values);
            int length = intArray.length;
            for (int i = 0; i < length; i++) {
                this.mAccessibilityTimeoutKeyToValueMap.put(stringArray[i], Integer.valueOf(intArray[i]));
            }
        }
        return this.mAccessibilityTimeoutKeyToValueMap;
    }

    private void handlePreferenceChange(String str) {
        putSecureString("accessibility_non_interactive_ui_timeout_ms", str);
        putSecureString("accessibility_interactive_ui_timeout_ms", str);
    }

    private void putSecureString(String str, String str2) {
        Settings.Secure.putString(this.mContentResolver, str, str2);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        radioButtonPreference.setOnPreferenceClickListener(this);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        handlePreferenceChange(String.valueOf(getTimeoutValueToKeyMap().get(this.mPreferenceKey).intValue()));
        OnChangeListener onChangeListener = this.mOnChangeListener;
        if (onChangeListener != null) {
            onChangeListener.onCheckedChanged(this.mPreference);
            return true;
        }
        return true;
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    protected void updatePreferenceCheckedState(int i) {
        if (this.mAccessibilityUiTimeoutValue == i) {
            this.mPreference.setChecked(true);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mAccessibilityUiTimeoutValue = getAccessibilityTimeoutValue();
        RadioButtonPreference radioButtonPreference = this.mPreference;
        if (radioButtonPreference == null) {
            return;
        }
        radioButtonPreference.setChecked(false);
        updatePreferenceCheckedState(getTimeoutValueToKeyMap().get(this.mPreference.getKey()).intValue());
    }
}
