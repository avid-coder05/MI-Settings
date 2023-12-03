package com.android.settingslib.miuisettings.preference;

import android.app.ActionBar;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.RecyclerView;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class PreferenceFragment extends miuix.preference.PreferenceFragment {
    private boolean mOnUnbindCalled = false;
    private boolean mHavePrefs = false;

    private static void dispatchOnBindPreferences(PreferenceGroup preferenceGroup, RecyclerView recyclerView) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        if (preferenceCount > 0) {
            for (int i = 0; i < preferenceCount; i++) {
                androidx.preference.Preference preference = preferenceGroup.getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    dispatchOnBindPreferences((PreferenceGroup) preference, recyclerView);
                }
            }
        }
    }

    private static void dispatchOnDetach(PreferenceGroup preferenceGroup) {
        int preferenceCount = preferenceGroup.getPreferenceCount();
        if (preferenceCount > 0) {
            for (int i = 0; i < preferenceCount; i++) {
                androidx.preference.Preference preference = preferenceGroup.getPreference(i);
                if (preference instanceof PreferenceGroup) {
                    dispatchOnDetach((PreferenceGroup) preference);
                } else if (preference instanceof PreferenceApiDiff) {
                    ((PreferenceApiDiff) preference).onDetached();
                }
            }
        }
    }

    public ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public miuix.appcompat.app.ActionBar getAppCompatActionBar() {
        if (getActivity() instanceof AppCompatActivity) {
            return ((AppCompatActivity) getActivity()).getAppCompatActionBar();
        }
        return null;
    }

    public MenuInflater getMenuInflater() {
        return getActivity().getMenuInflater();
    }

    public Context getThemedContext() {
        return getPreferenceManager().getContext();
    }

    public void invalidateOptionsMenu() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
        if (getPreferenceScreen() != null) {
            dispatchOnBindPreferences(getPreferenceScreen(), getListView());
        }
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        if (this.mHavePrefs) {
            if (getPreferenceScreen() != null) {
                dispatchOnDetach(getPreferenceScreen());
            }
            onUnbindPreferences();
        }
    }

    @Override // miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(androidx.preference.Preference preference) {
        return onPreferenceTreeClick(getPreferenceScreen(), preference) || super.onPreferenceTreeClick(preference);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, androidx.preference.Preference preference) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.PreferenceFragmentCompat
    public void onUnbindPreferences() {
        super.onUnbindPreferences();
        if (this.mHavePrefs) {
            return;
        }
        this.mOnUnbindCalled = true;
    }

    @Override // androidx.preference.PreferenceFragmentCompat
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        super.setPreferenceScreen(preferenceScreen);
        if (this.mOnUnbindCalled) {
            this.mOnUnbindCalled = false;
            this.mHavePrefs = true;
        }
    }

    public void setThemeRes(int i) {
    }
}
