package com.android.settings.accessibility;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.androidx.FragmentPagerAdapter;
import miuix.appcompat.internal.app.widget.ScrollingTabTextView;

/* loaded from: classes.dex */
public class MiuiAccessibilityFragment extends SettingsPreferenceFragment {
    private static final Class<? extends Fragment>[] a11ySettingsClass = {GeneralAccessibilitySettings.class, VisualAccessibilitySettings.class, HearingAccessibilitySettings.class, PhysicalAccessibilitySettings.class};
    protected PreviewPagerAdapter mFragmentAdapter;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() { // from class: com.android.settings.accessibility.MiuiAccessibilityFragment.1
        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrollStateChanged(int i) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageScrolled(int i, float f, int i2) {
        }

        @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
        public void onPageSelected(int i) {
            MiuiAccessibilityFragment.this.setTabSelected(i);
        }
    };
    private View mRootLayout;
    private LinearLayout mTabBar;
    private String[] mTitles;
    private ViewPager mViewPager;

    /* loaded from: classes.dex */
    private class PreviewPagerAdapter extends FragmentPagerAdapter {
        PreviewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return MiuiAccessibilityFragment.a11ySettingsClass.length;
        }

        @Override // com.android.settingslib.androidx.FragmentPagerAdapter
        public Fragment getItem(int i) {
            Class cls = MiuiAccessibilityFragment.a11ySettingsClass[i];
            if (cls != null) {
                return Fragment.instantiate(MiuiAccessibilityFragment.this.getActivity(), cls.getName());
            }
            return null;
        }
    }

    private void initTitles() {
        this.mTitles = new String[]{getString(R.string.accessibility_settings_tabs_general), getString(R.string.accessibility_settings_tabs_visual), getString(R.string.accessibility_settings_tabs_hearing), getString(R.string.accessibility_settings_tabs_physical)};
    }

    protected View createTabView(String str) {
        ScrollingTabTextView scrollingTabTextView = new ScrollingTabTextView(getActivity(), null, R.attr.actionBarTabTextExpandStyle);
        scrollingTabTextView.setEllipsize(TextUtils.TruncateAt.END);
        scrollingTabTextView.setTextColor(getResources().getColorStateList(R.color.actionbar_text_selector_tablet));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        scrollingTabTextView.setLayoutParams(layoutParams);
        scrollingTabTextView.setText(str);
        return scrollingTabTextView;
    }

    protected int getDefaultVisibleTabIndex() {
        return 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiAccessibilityFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initTitles();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = this.mRootLayout;
        if (view != null) {
            ViewGroup viewGroup2 = (ViewGroup) view.getParent();
            if (viewGroup2 != null) {
                viewGroup2.removeView(this.mRootLayout);
            }
            return this.mRootLayout;
        }
        View inflate = layoutInflater.inflate(R.layout.miui_accessibility_layout, viewGroup, false);
        this.mRootLayout = inflate;
        ViewPager viewPager = (ViewPager) inflate.findViewById(R.id.viewPager);
        this.mViewPager = viewPager;
        viewPager.setOnPageChangeListener(this.mPageChangeListener);
        PreviewPagerAdapter previewPagerAdapter = new PreviewPagerAdapter(getChildFragmentManager());
        this.mFragmentAdapter = previewPagerAdapter;
        this.mViewPager.setAdapter(previewPagerAdapter);
        this.mTabBar = (LinearLayout) this.mRootLayout.findViewById(R.id.tablayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, -2, 1.0f);
        for (final int i = 0; i < a11ySettingsClass.length; i++) {
            View createTabView = createTabView(this.mTitles[i]);
            createTabView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.MiuiAccessibilityFragment.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    MiuiAccessibilityFragment.this.setTabSelected(i);
                    MiuiAccessibilityFragment.this.mViewPager.setCurrentItem(i);
                }
            });
            this.mTabBar.addView(createTabView, layoutParams);
        }
        int defaultVisibleTabIndex = getDefaultVisibleTabIndex();
        if (defaultVisibleTabIndex == this.mViewPager.getCurrentItem()) {
            this.mTabBar.getChildAt(defaultVisibleTabIndex).setSelected(true);
        }
        this.mViewPager.setCurrentItem(defaultVisibleTabIndex);
        super.onCreateView(layoutInflater, viewGroup, bundle);
        return this.mRootLayout;
    }

    public void setTabSelected(int i) {
        int childCount = this.mTabBar.getChildCount();
        int i2 = 0;
        while (i2 < childCount) {
            this.mTabBar.getChildAt(i2).setSelected(i2 == i);
            i2++;
        }
    }
}
