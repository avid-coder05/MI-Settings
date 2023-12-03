package com.android.settings;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import com.android.settings.SettingsPreferenceFragment;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.Fragment;

/* loaded from: classes.dex */
public class BaseFragment extends Fragment implements DialogCreatable, OnBackPressedListener {
    private SettingsPreferenceFragment.SettingsDialogFragment mDialogFragment;
    private View mView;

    private void setupActionBar() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            int i = arguments.getInt(":android:show_fragment_title");
            ActionBar appCompatActionBar = getAppCompatActivity().getAppCompatActionBar();
            if (appCompatActionBar == null || i <= 0) {
                return;
            }
            appCompatActionBar.setTitle(i);
        }
    }

    public View doInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return super.onInflateView(layoutInflater, viewGroup, bundle);
    }

    public void finish() {
        if (getActivity() == null) {
            return;
        }
        if (!(getActivity() instanceof MiuiSettings)) {
            if (isResumed()) {
                getActivity().onBackPressed();
                return;
            } else {
                getActivity().finish();
                return;
            }
        }
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        if (supportFragmentManager == null || !isResumed()) {
            getActivity().getSupportFragmentManager().popBackStack();
        } else {
            supportFragmentManager.popBackStackImmediate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Intent getIntent() {
        return getActivity().getIntent();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Object getSystemService(String str) {
        return getActivity().getSystemService(str);
    }

    public boolean onBackPressed() {
        return false;
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_DayNight_Settings_NoTitle);
    }

    @Override // com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        return null;
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public boolean onCreateOptionsMenu(Menu menu) {
        onCreateOptionsMenu(menu, getMenuInflater());
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        SettingsPreferenceFragment.SettingsDialogFragment settingsDialogFragment;
        if (isRemoving() && (settingsDialogFragment = this.mDialogFragment) != null) {
            settingsDialogFragment.dismiss();
            this.mDialogFragment = null;
        }
        super.onDetach();
    }

    @Override // miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        View doInflateView = doInflateView(layoutInflater, viewGroup, bundle);
        this.mView = doInflateView;
        return doInflateView;
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return false;
    }

    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return false;
    }

    @Override // androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        setupActionBar();
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setResult(int i) {
        getActivity().setResult(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showDialog(int i) {
        if (this.mDialogFragment != null) {
            Log.e("BaseFragment", "Old dialog fragment not null!");
        }
        if (getActivity() == null) {
            return;
        }
        SettingsPreferenceFragment.SettingsDialogFragment newInstance = SettingsPreferenceFragment.SettingsDialogFragment.newInstance(this, i);
        this.mDialogFragment = newInstance;
        newInstance.show(getActivity().getSupportFragmentManager(), Integer.toString(i));
    }

    public boolean startFragment(Fragment fragment, String str, int i, Bundle bundle, int i2) {
        MiuiUtils.startPreferencePanel(getActivity(), str, bundle, i2, null, fragment, i);
        return true;
    }
}
