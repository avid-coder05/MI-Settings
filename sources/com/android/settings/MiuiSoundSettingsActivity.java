package com.android.settings;

import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.utils.SettingsFeatures;
import java.lang.reflect.InvocationTargetException;
import miuix.appcompat.app.ActionBar;

/* loaded from: classes.dex */
public class MiuiSoundSettingsActivity extends SettingsActivity {
    private ActionBar mActionBar;
    private ImageView mBackView;
    private int mCurrentPosition;
    private Object mHapticFragment;
    private String[] mTitles;

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return obj.getClass().getDeclaredMethod(str, clsArr).invoke(obj, objArr);
    }

    private void initActionBar() {
        int intExtra;
        this.mActionBar = getAppCompatActionBar();
        this.mTitles = new String[]{getString(R.string.sound_settings_tab_sound), getString(R.string.sound_settings_tab_haptic)};
        initActionBarBackView();
        this.mActionBar.setFragmentViewPagerMode(this, false);
        ActionBar actionBar = this.mActionBar;
        actionBar.addFragmentTab(this.mTitles[0], actionBar.newTab().setText(this.mTitles[0]), MiuiSoundSettings.class, null, false);
        initHapticFragment();
        this.mActionBar.addOnFragmentViewPagerChangeListener(new ActionBar.FragmentViewPagerChangeListener() { // from class: com.android.settings.MiuiSoundSettingsActivity.1
            @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
            public void onPageScrollStateChanged(int i) {
            }

            @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
            public void onPageScrolled(int i, float f, boolean z, boolean z2) {
            }

            @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
            public void onPageSelected(int i) {
                MiuiSoundSettingsActivity.this.mCurrentPosition = i;
                MiuiSoundSettingsActivity miuiSoundSettingsActivity = MiuiSoundSettingsActivity.this;
                boolean z = true;
                miuiSoundSettingsActivity.mHapticFragment = miuiSoundSettingsActivity.getSupportFragmentManager().findFragmentByTag(MiuiSoundSettingsActivity.this.mTitles[1]);
                if (MiuiSoundSettingsActivity.this.mHapticFragment == null) {
                    return;
                }
                try {
                    Object obj = MiuiSoundSettingsActivity.this.mHapticFragment;
                    Class[] clsArr = {Boolean.TYPE};
                    Object[] objArr = new Object[1];
                    if (MiuiSoundSettingsActivity.this.mCurrentPosition != 1) {
                        z = false;
                    }
                    objArr[0] = Boolean.valueOf(z);
                    MiuiSoundSettingsActivity.callObjectMethod(obj, "onVisible", clsArr, objArr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (getIntent() == null || this.mActionBar == null || (intExtra = getIntent().getIntExtra("extra_tab_position", 0)) >= this.mActionBar.getFragmentTabCount()) {
            return;
        }
        this.mActionBar.setSelectedNavigationItem(intExtra);
    }

    private void initActionBarBackView() {
        ImageView imageView = new ImageView(this);
        this.mBackView = imageView;
        imageView.setContentDescription(getString(R.string.back_button));
        this.mBackView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSoundSettingsActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiuiSoundSettingsActivity.this.onBackPressed();
            }
        });
        if (DarkModeTimeModeUtil.isDarkModeEnable(this)) {
            this.mBackView.setImageResource(R.drawable.miuix_appcompat_action_bar_back_dark);
        } else {
            this.mBackView.setImageResource(R.drawable.miuix_appcompat_action_bar_back_light);
        }
        this.mActionBar.setStartView(this.mBackView);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void initHapticFragment() {
        Class<?> cls;
        try {
            cls = Class.forName("com.android.settings.haptic.HapticFragment");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            cls = null;
        }
        Class<?> cls2 = cls;
        if (cls2 != null) {
            ActionBar actionBar = this.mActionBar;
            actionBar.addFragmentTab(this.mTitles[1], actionBar.newTab().setText(this.mTitles[1]), cls2, null, false);
        }
    }

    public int getCurrentPage() {
        return this.mCurrentPosition;
    }

    @Override // com.android.settings.SettingsActivity
    protected boolean needToLaunchSettingsFragment() {
        return !SettingsFeatures.isSupportSettingsHaptic(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settings.core.SettingsBaseActivity, com.android.settingslib.core.lifecycle.ObservableActivity, miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if (!SettingsFeatures.isSupportSettingsHaptic(this)) {
            setTitle(getString(((Vibrator) getSystemService("vibrator")).hasVibrator() ? R.string.sound_vibrate_settings : R.string.sound_settings));
            super.onCreate(bundle);
            return;
        }
        setTitle(getString(R.string.sound_haptic_settings));
        super.onCreate(bundle);
        initActionBar();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.SettingsActivity, com.android.settingslib.core.lifecycle.ObservableActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (SettingsFeatures.isSupportSettingsHaptic(this)) {
            Fragment findFragmentByTag = getSupportFragmentManager().findFragmentByTag(this.mTitles[1]);
            this.mHapticFragment = findFragmentByTag;
            if (findFragmentByTag != null && this.mCurrentPosition == 1) {
                try {
                    callObjectMethod(findFragmentByTag, "onVisible", new Class[]{Boolean.TYPE}, Boolean.TRUE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override // android.app.Activity, android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int i) {
        if (SettingsFeatures.isSupportSettingsHaptic(this)) {
            i = R.style.MiuiAccessibility;
        }
        super.setTheme(i);
    }
}
