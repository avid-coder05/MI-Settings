package com.android.settings.dashboard.profileselector;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.dashboard.profileselector.ProfileSelectFragment;
import java.util.Locale;
import miuix.miuixbasewidget.widget.FilterSortView;

/* loaded from: classes.dex */
public abstract class ProfileSelectFragment extends DashboardFragment {
    private static final int[] LABEL = {R.string.category_personal, R.string.category_work};
    private ViewGroup mContentView;
    private TabHelper tabHelper;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class TabHelper {
        private FilterSortView.TabView[] tabViews;
        private FilterSortView tabs;
        private ViewPager viewPager;

        public TabHelper(int i, FilterSortView filterSortView, ViewPager viewPager) {
            this.tabViews = new FilterSortView.TabView[i];
            this.tabs = filterSortView;
            this.viewPager = viewPager;
        }

        private int findIndex(FilterSortView.TabView tabView) {
            if (this.tabViews != null && tabView != null) {
                int i = 0;
                while (true) {
                    FilterSortView.TabView[] tabViewArr = this.tabViews;
                    if (i >= tabViewArr.length) {
                        break;
                    } else if (tabViewArr[i] == tabView) {
                        return i;
                    } else {
                        i++;
                    }
                }
            }
            return -1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addTabs$0(FilterSortView.TabView tabView, View view) {
            int findIndex = findIndex(tabView);
            if (findIndex < 0 || findIndex >= this.viewPager.getChildCount()) {
                return;
            }
            this.viewPager.setCurrentItem(findIndex);
        }

        public void addTabs() {
            if (this.tabViews != null) {
                int min = Math.min(ProfileSelectFragment.LABEL.length, this.tabViews.length);
                for (int i = 0; i < min; i++) {
                    FilterSortView filterSortView = this.tabs;
                    final FilterSortView.TabView addTab = filterSortView.addTab(filterSortView.getContext().getString(ProfileSelectFragment.LABEL[i]));
                    this.tabViews[i] = addTab;
                    addTab.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment$TabHelper$$ExternalSyntheticLambda0
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            ProfileSelectFragment.TabHelper.this.lambda$addTabs$0(addTab, view);
                        }
                    });
                }
                this.tabs.setFilteredTab(this.tabViews[0]);
            }
        }

        public void pageSelected(int i) {
            FilterSortView.TabView[] tabViewArr;
            FilterSortView filterSortView = this.tabs;
            if (filterSortView == null || (tabViewArr = this.tabViews) == null || i > tabViewArr.length - 1) {
                return;
            }
            filterSortView.setFilteredTab(tabViewArr[i]);
        }

        public void setUpViewPager() {
            this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.android.settings.dashboard.profileselector.ProfileSelectFragment.TabHelper.1
                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrollStateChanged(int i) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageScrolled(int i, float f, int i2) {
                }

                @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
                public void onPageSelected(int i) {
                    TabHelper.this.pageSelected(i);
                }
            });
        }
    }

    /* loaded from: classes.dex */
    static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final Fragment[] mChildFragments;
        private final Context mContext;

        ViewPagerAdapter(ProfileSelectFragment profileSelectFragment) {
            super(profileSelectFragment.getChildFragmentManager());
            this.mContext = profileSelectFragment.getContext();
            this.mChildFragments = profileSelectFragment.getFragments();
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return this.mChildFragments.length;
        }

        @Override // androidx.fragment.app.FragmentStatePagerAdapter
        public Fragment getItem(int i) {
            return this.mChildFragments[ProfileSelectFragment.convertPosition(i)];
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public CharSequence getPageTitle(int i) {
            return this.mContext.getString(ProfileSelectFragment.LABEL[ProfileSelectFragment.convertPosition(i)]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int convertPosition(int i) {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? (LABEL.length - 1) - i : i;
    }

    public abstract Fragment[] getFragments();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "ProfileSelectFragment";
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.placeholder_preference_screen;
    }

    int getTabId(Activity activity, Bundle bundle) {
        if (bundle != null) {
            int i = bundle.getInt(":settings:show_fragment_tab", -1);
            if (i != -1) {
                return i;
            }
            if (UserManager.get(activity).isManagedProfile(bundle.getInt("android.intent.extra.USER_ID", UserHandle.SYSTEM.getIdentifier()))) {
                return 1;
            }
        }
        return UserManager.get(activity).isManagedProfile(activity.getIntent().getContentUserHint()) ? 1 : 0;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.preference_list_profile_select_fragment, viewGroup, false);
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        ViewGroup viewGroup3 = (ViewGroup) super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mContentView = viewGroup3;
        viewGroup2.addView(viewGroup3);
        FragmentActivity activity = getActivity();
        int convertPosition = convertPosition(getTabId(activity, getArguments()));
        View findViewById = inflate.findViewById(R.id.tab_container);
        ViewPager viewPager = (ViewPager) findViewById.findViewById(R.id.view_pager);
        viewPager.setAdapter(new ViewPagerAdapter(this));
        TabHelper tabHelper = new TabHelper(getFragments().length, (FilterSortView) findViewById.findViewById(R.id.tabs), viewPager);
        this.tabHelper = tabHelper;
        tabHelper.addTabs();
        this.tabHelper.setUpViewPager();
        findViewById.setVisibility(0);
        this.tabHelper.pageSelected(convertPosition);
        ((FrameLayout) inflate.findViewById(R.id.list_container)).setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        RecyclerView listView = getListView();
        listView.setOverScrollMode(2);
        Utils.setActionBarShadowAnimation(activity, getSettingsLifecycle(), listView);
        return inflate;
    }
}
