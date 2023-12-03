package com.android.settingslib.miuisettings.preference;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.WrapperListAdapter;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceManager;
import java.lang.reflect.Field;
import java.util.List;

/* loaded from: classes2.dex */
public class PreferenceDelegate {
    private static Field PreferenceAdapter_mPreferenceList = null;
    private static Field Preference_mIconResId = null;
    private static String TAG = "Miui_Preference";
    private PreferenceGroupAdapter mAdapter;
    private PreferenceApiDiff mApiDiff;
    private int mItemIndex;
    private androidx.preference.Preference mPreference;
    private boolean mShowIcon;
    private boolean mVisible;

    public PreferenceDelegate(androidx.preference.Preference preference, PreferenceApiDiff preferenceApiDiff) {
        this(preference, preferenceApiDiff, false);
    }

    public PreferenceDelegate(androidx.preference.Preference preference, PreferenceApiDiff preferenceApiDiff, boolean z) {
        this.mVisible = true;
        this.mShowIcon = false;
        this.mPreference = preference;
        this.mApiDiff = preferenceApiDiff;
        this.mShowIcon = z;
        if (((preference instanceof PreferenceFeature) && ((PreferenceFeature) preference).hasIcon()) || this.mShowIcon) {
            return;
        }
        hideIcon();
    }

    private static List<androidx.preference.Preference> getAdapterPreferenceList(PreferenceGroupAdapter preferenceGroupAdapter) {
        if (PreferenceAdapter_mPreferenceList == null) {
            try {
                Field declaredField = PreferenceGroupAdapter.class.getDeclaredField("mVisiblePreferences");
                PreferenceAdapter_mPreferenceList = declaredField;
                declaredField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.e(TAG, "", e);
            }
        }
        try {
            Object obj = PreferenceAdapter_mPreferenceList.get(preferenceGroupAdapter);
            if (obj instanceof List) {
                return (List) obj;
            }
            return null;
        } catch (IllegalAccessException e2) {
            Log.e(TAG, "", e2);
            return null;
        }
    }

    private void hideIcon() {
        if (this.mPreference.isIconSpaceReserved()) {
            this.mPreference.setIconSpaceReserved(false);
        }
        if (this.mPreference.getIcon() == null) {
            return;
        }
        this.mPreference.setIcon((Drawable) null);
        if (Preference_mIconResId == null) {
            try {
                Field declaredField = androidx.preference.Preference.class.getDeclaredField("mIconResId");
                Preference_mIconResId = declaredField;
                declaredField.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Preference_mIconResId.set(this.mPreference, 0);
        } catch (Exception e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mApiDiff.onAttached();
    }

    public void onBindViewEnd(View view) {
        this.mApiDiff.onBindView(view);
    }

    public void onBindViewStart(View view) {
    }

    public void onFragmentBindPreference(ListView listView) {
        if (listView != null) {
            ListAdapter adapter = listView.getAdapter();
            if (adapter instanceof WrapperListAdapter) {
                this.mAdapter = (PreferenceGroupAdapter) ((WrapperListAdapter) adapter).getWrappedAdapter();
            } else {
                this.mAdapter = (PreferenceGroupAdapter) adapter;
            }
            List<androidx.preference.Preference> adapterPreferenceList = getAdapterPreferenceList(this.mAdapter);
            if (adapterPreferenceList == null || adapterPreferenceList.size() <= 0) {
                return;
            }
            for (int i = 0; i < adapterPreferenceList.size(); i++) {
                if (this.mPreference == adapterPreferenceList.get(i)) {
                    this.mItemIndex = i;
                }
            }
        }
    }
}
