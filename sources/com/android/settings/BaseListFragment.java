package com.android.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.ListFragment;

/* loaded from: classes.dex */
public class BaseListFragment extends ListFragment {
    protected boolean mIsSavedState;

    private void setupActionBar() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            int i = arguments.getInt(":android:show_fragment_title");
            ActionBar actionBar = getActionBar();
            if (actionBar == null || i <= 0) {
                return;
            }
            actionBar.setTitle(i);
        }
    }

    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onInflateView(layoutInflater, viewGroup, bundle);
    }

    public void finish() {
        if (getActivity() == null || this.mIsSavedState) {
            return;
        }
        if (!(getActivity() instanceof MiuiSettings)) {
            getActivity().onBackPressed();
            return;
        }
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override // miuix.appcompat.app.ListFragment
    public ActionBar getActionBar() {
        return super.getActionBar();
    }

    @Override // miuix.appcompat.app.ListFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_DayNight_Settings_NoTitle);
    }

    @Override // miuix.appcompat.app.ListFragment, miuix.appcompat.app.IFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }

    @Override // miuix.appcompat.app.ListFragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        return doInflateView(layoutInflater, viewGroup, bundle);
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // miuix.appcompat.app.ListFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mIsSavedState = false;
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mIsSavedState = true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupActionBar();
    }

    public boolean startFragment(ListFragment listFragment, String str, int i, Bundle bundle, int i2) {
        if (getActivity() instanceof MiuiSettings) {
            ((MiuiSettings) getActivity()).startPreferencePanel(str, bundle, i2, null, listFragment, i);
            return true;
        }
        Log.w("BaseListFragment", "Parent isn't PreferenceActivity, thus there's no way to launch the given Fragment (name: " + str + ", requestCode: " + i + ")");
        return false;
    }
}
