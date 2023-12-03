package com.android.settingslib.core;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;

/* loaded from: classes2.dex */
public abstract class AbstractPreferenceController {
    private static final String TAG = "AbstractPrefController";
    protected final Context mContext;

    public AbstractPreferenceController(Context context) {
        this.mContext = context;
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        String preferenceKey = getPreferenceKey();
        if (TextUtils.isEmpty(preferenceKey)) {
            Log.w(TAG, "Skipping displayPreference because key is empty:" + getClass().getName());
        } else if (!isAvailable()) {
            setVisible(preferenceScreen, preferenceKey, false);
        } else {
            setVisible(preferenceScreen, preferenceKey, true);
            if (this instanceof Preference.OnPreferenceChangeListener) {
                Preference findPreference = preferenceScreen.findPreference(preferenceKey);
                if (findPreference != null) {
                    findPreference.setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) this);
                } else {
                    setVisible(preferenceScreen, preferenceKey, false);
                }
            }
        }
    }

    public abstract String getPreferenceKey();

    public CharSequence getSummary() {
        return null;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    public abstract boolean isAvailable();

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshSummary(Preference preference) {
        CharSequence summary;
        if (preference == null || (summary = getSummary()) == null) {
            return;
        }
        preference.setSummary(summary);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setVisible(Preference preference, boolean z) {
        preference.setVisible(z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setVisible(PreferenceGroup preferenceGroup, String str, boolean z) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference != null) {
            findPreference.setVisible(z);
        }
    }

    public void updateState(Preference preference) {
        refreshSummary(preference);
    }
}
