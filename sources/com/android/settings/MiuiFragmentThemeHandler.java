package com.android.settings;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import com.android.settings.utils.TabletUtils;
import miuix.preference.PreferenceFragment;

/* loaded from: classes.dex */
public class MiuiFragmentThemeHandler {
    private Fragment mFragment;

    public MiuiFragmentThemeHandler(Fragment fragment) {
        this.mFragment = fragment;
    }

    private void updateThemeRes(Bundle bundle) {
        int i = R.style.Theme_DayNight_Settings_NoTitle;
        if (bundle != null) {
            i = bundle.getInt("theme_res_id");
        } else if (TabletUtils.IS_TABLET && (this.mFragment.getActivity() instanceof MiuiSettings)) {
            i = this.mFragment.getFragmentManager().getBackStackEntryCount() < 1 ? R.style.ShowTitleTheme : R.style.Theme_DayNight_Settings;
        }
        if (i != 0) {
            Fragment fragment = this.mFragment;
            if (fragment instanceof miuix.appcompat.app.Fragment) {
                ((miuix.appcompat.app.Fragment) fragment).setThemeRes(i);
            } else {
                boolean z = fragment instanceof PreferenceFragment;
            }
        }
    }

    public void onCreate(Bundle bundle) {
        updateThemeRes(bundle);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return false;
        }
        Fragment fragment = this.mFragment;
        if (fragment instanceof SettingsPreferenceFragment) {
            ((SettingsPreferenceFragment) fragment).finish();
        } else {
            FragmentActivity activity = fragment.getActivity();
            if (activity == null) {
                return true;
            }
            if (activity instanceof MiuiSettings) {
                FragmentManager fragmentManager = this.mFragment.getFragmentManager();
                if (fragmentManager == null || !this.mFragment.isResumed()) {
                    activity.getFragmentManager().popBackStack();
                } else {
                    fragmentManager.popBackStackImmediate();
                }
            } else if (this.mFragment.isResumed()) {
                activity.onBackPressed();
            } else {
                activity.finish();
            }
        }
        return true;
    }

    public void onSaveInstanceState(Bundle bundle) {
        Fragment fragment = this.mFragment;
        if (fragment instanceof miuix.appcompat.app.Fragment) {
            bundle.putInt("theme_res_id", ((miuix.appcompat.app.Fragment) fragment).getThemedContext().getThemeResId());
        } else {
            boolean z = fragment instanceof PreferenceFragment;
        }
    }
}
