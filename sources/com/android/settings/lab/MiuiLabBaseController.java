package com.android.settings.lab;

import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.TwoStatePreference;

/* loaded from: classes.dex */
public abstract class MiuiLabBaseController<T extends Preference> implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "MiuiLabBaseController";
    protected T mPreference;

    public MiuiLabBaseController(PreferenceGroup preferenceGroup) {
        T t = (T) preferenceGroup.findPreference(getPreferenceKey());
        this.mPreference = t;
        if (t instanceof TwoStatePreference) {
            t.setOnPreferenceChangeListener(this);
        }
        if (t == null) {
            Log.w(TAG, "preference not found: " + getPreferenceKey());
        }
    }

    public final void dipatchClick(String str) {
        if (this.mPreference != null) {
            if (TextUtils.equals(str, getPreferenceKey())) {
                onClick();
                return;
            }
            return;
        }
        Log.i(TAG, "dipatchClick, preference is null: " + getPreferenceKey());
    }

    protected abstract String getPreferenceKey();

    /* JADX INFO: Access modifiers changed from: protected */
    public void onClick() {
    }

    protected void onPause() {
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ((TwoStatePreference) preference).setChecked(((Boolean) obj).booleanValue());
        onClick();
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onResume() {
    }

    public final void pause() {
        if (this.mPreference != null) {
            onPause();
            return;
        }
        Log.i(TAG, "pause, preference is null: " + getPreferenceKey());
    }

    public final void resume() {
        if (this.mPreference != null) {
            onResume();
            return;
        }
        Log.i(TAG, "resume, preference is null: " + getPreferenceKey());
    }
}
