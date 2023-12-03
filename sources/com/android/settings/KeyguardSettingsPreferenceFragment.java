package com.android.settings;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public abstract class KeyguardSettingsPreferenceFragment extends MiuiSettingsPreferenceFragment {
    protected boolean mSetItemSpace = false;

    /* loaded from: classes.dex */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int i) {
            this.space = i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (recyclerView.getChildPosition(view) != 0) {
                rect.top = this.space;
            }
        }
    }

    protected void disableSpringBack() {
    }

    protected View inflateCustomizeView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflateCustomizeView = inflateCustomizeView(layoutInflater, viewGroup, bundle);
        if (inflateCustomizeView != null) {
            ViewGroup viewGroup2 = (ViewGroup) inflateCustomizeView.findViewById(R.id.prefs_container);
            if (viewGroup2 != null) {
                viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup, bundle));
            }
            setItemSpace();
            disableSpringBack();
            return inflateCustomizeView;
        }
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }

    protected void setItemSpace() {
    }
}
