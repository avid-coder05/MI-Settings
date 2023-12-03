package com.android.settings.accessibility;

import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.Log;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.SubSettings;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.report.InternationalCompat;
import miui.os.Build;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class MiuiAccessibilitySettingsActivity extends SubSettings {
    private static final Class<? extends Fragment>[] a11ySettingsClass = {GeneralAccessibilitySettings.class, VisualAccessibilitySettings.class, HearingAccessibilitySettings.class, PhysicalAccessibilitySettings.class};
    private ActionBar mActionBar;
    private ImageView mBackView;
    private int mCurrentPosition;
    private String[] mTitles;

    private void initActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        this.mActionBar = appCompatActionBar;
        int i = 0;
        appCompatActionBar.setFragmentViewPagerMode(this, false);
        initActionBarBackView();
        this.mTitles = new String[]{getString(R.string.accessibility_settings_tabs_general), getString(R.string.accessibility_settings_tabs_visual), getString(R.string.accessibility_settings_tabs_hearing), getString(R.string.accessibility_settings_tabs_physical)};
        while (true) {
            Class<? extends Fragment>[] clsArr = a11ySettingsClass;
            if (i >= clsArr.length) {
                this.mActionBar.addOnFragmentViewPagerChangeListener(new ActionBar.FragmentViewPagerChangeListener() { // from class: com.android.settings.accessibility.MiuiAccessibilitySettingsActivity.1
                    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
                    public void onPageScrollStateChanged(int i2) {
                    }

                    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
                    public void onPageScrolled(int i2, float f, boolean z, boolean z2) {
                    }

                    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
                    public void onPageSelected(int i2) {
                        MiuiAccessibilitySettingsActivity.this.mCurrentPosition = i2;
                    }
                });
                return;
            }
            ActionBar actionBar = this.mActionBar;
            actionBar.addFragmentTab(this.mTitles[i], actionBar.newTab().setText(this.mTitles[i]), clsArr[i], null, false);
            i++;
        }
    }

    private void initActionBarBackView() {
        ImageView imageView = new ImageView(this);
        this.mBackView = imageView;
        imageView.setContentDescription(getString(R.string.back_button));
        this.mBackView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.MiuiAccessibilitySettingsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiAccessibilitySettingsActivity.this.onBackPressed();
            }
        });
        if (DarkModeTimeModeUtil.isDarkModeEnable(this)) {
            this.mBackView.setImageResource(R.drawable.miuix_appcompat_action_bar_back_dark);
        } else {
            this.mBackView.setImageResource(R.drawable.miuix_appcompat_action_bar_back_light);
        }
        this.mActionBar.setStartView(this.mBackView);
    }

    @Override // com.android.settings.SettingsActivity
    protected boolean needToLaunchSettingsFragment() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (!Build.IS_TABLET) {
            setRequestedOrientation(1);
        }
        initActionBar();
        if (bundle != null) {
            int i = bundle.getInt("current_position");
            this.mCurrentPosition = i;
            this.mActionBar.setSelectedNavigationItem(i);
        } else if (getIntent() != null && this.mActionBar != null) {
            this.mActionBar.setSelectedNavigationItem(getIntent().getIntExtra("extra_tab_position", 0));
        }
        InternationalCompat.trackReportEvent("setting_Additional_settings_talkback");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("current_position", this.mCurrentPosition);
        Log.d("MiuiA11ySettingsActivity", "mCurrentPosition:" + this.mCurrentPosition);
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        super.setTheme(R.style.MiuiAccessibility);
    }
}
