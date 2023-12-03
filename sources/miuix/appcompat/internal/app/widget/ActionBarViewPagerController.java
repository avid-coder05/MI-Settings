package miuix.appcompat.internal.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.OriginalViewPager;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.R$id;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.internal.app.widget.ActionBarImpl;
import miuix.internal.util.DeviceHelper;
import miuix.springback.view.SpringBackLayout;
import miuix.viewpager.widget.ViewPager;

/* loaded from: classes5.dex */
public class ActionBarViewPagerController {
    private ActionBarImpl mActionBar;
    private ArrayList<ActionBar.FragmentViewPagerChangeListener> mListeners;
    private DynamicFragmentPagerAdapter mPagerAdapter;
    private ActionBar.TabListener mTabListener = new ActionBar.TabListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarViewPagerController.1
        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
            int count = ActionBarViewPagerController.this.mPagerAdapter.getCount();
            for (int i = 0; i < count; i++) {
                if (ActionBarViewPagerController.this.mPagerAdapter.getTabAt(i) == tab) {
                    ActionBarViewPagerController.this.mViewPager.setCurrentItem(i, tab instanceof ActionBarImpl.TabImpl ? ((ActionBarImpl.TabImpl) tab).mWithAnim : true);
                    return;
                }
            }
        }

        @Override // androidx.appcompat.app.ActionBar.TabListener
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        }
    };
    private ViewPager mViewPager;

    /* loaded from: classes5.dex */
    private static class ScrollStatus {
        int mFromPos;
        private float mOffsetAtScroll;
        private int mPosAtScroll;
        boolean mScrollBegin;
        boolean mScrollEnd;
        int mToPos;

        private ScrollStatus() {
            this.mPosAtScroll = -1;
        }

        private void onScrollBegin(int i, float f) {
            this.mScrollBegin = false;
            boolean z = f > this.mOffsetAtScroll;
            this.mFromPos = z ? i : i + 1;
            if (z) {
                i++;
            }
            this.mToPos = i;
        }

        private void onScrollEnd() {
            this.mFromPos = this.mToPos;
            this.mPosAtScroll = -1;
            this.mOffsetAtScroll = 0.0f;
            this.mScrollEnd = true;
        }

        private void onScrollPositionChange(int i, float f) {
            this.mPosAtScroll = i;
            this.mOffsetAtScroll = f;
            this.mScrollBegin = true;
            this.mScrollEnd = false;
        }

        void update(int i, float f) {
            if (f < 1.0E-4f) {
                onScrollEnd();
            } else if (this.mPosAtScroll != i) {
                onScrollPositionChange(i, f);
            } else if (this.mScrollBegin) {
                onScrollBegin(i, f);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActionBarViewPagerController(ActionBarImpl actionBarImpl, FragmentManager fragmentManager, Lifecycle lifecycle, boolean z) {
        this.mActionBar = actionBarImpl;
        ActionBarOverlayLayout actionBarOverlayLayout = actionBarImpl.getActionBarOverlayLayout();
        Context context = actionBarOverlayLayout.getContext();
        int i = R$id.view_pager;
        View findViewById = actionBarOverlayLayout.findViewById(i);
        if (findViewById instanceof ViewPager) {
            this.mViewPager = (ViewPager) findViewById;
        } else {
            ViewPager viewPager = new ViewPager(context);
            this.mViewPager = viewPager;
            viewPager.setId(i);
            SpringBackLayout springBackLayout = new SpringBackLayout(context);
            springBackLayout.setScrollOrientation(5);
            springBackLayout.addView(this.mViewPager, new OriginalViewPager.LayoutParams());
            springBackLayout.setTarget(this.mViewPager);
            ((ViewGroup) actionBarOverlayLayout.findViewById(16908290)).addView(springBackLayout, new ViewGroup.LayoutParams(-1, -1));
        }
        DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter = new DynamicFragmentPagerAdapter(context, fragmentManager);
        this.mPagerAdapter = dynamicFragmentPagerAdapter;
        this.mViewPager.setAdapter(dynamicFragmentPagerAdapter);
        this.mViewPager.addOnPageChangeListener(new OriginalViewPager.OnPageChangeListener() { // from class: miuix.appcompat.internal.app.widget.ActionBarViewPagerController.2
            ScrollStatus mStatus = new ScrollStatus();

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i2) {
                if (ActionBarViewPagerController.this.mListeners != null) {
                    Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                    while (it.hasNext()) {
                        ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageScrollStateChanged(i2);
                    }
                }
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageScrolled(int i2, float f, int i3) {
                this.mStatus.update(i2, f);
                if (this.mStatus.mScrollBegin || ActionBarViewPagerController.this.mListeners == null) {
                    return;
                }
                boolean hasActionMenu = ActionBarViewPagerController.this.mPagerAdapter.hasActionMenu(this.mStatus.mFromPos);
                boolean hasActionMenu2 = ActionBarViewPagerController.this.mPagerAdapter.hasActionMenu(this.mStatus.mToPos);
                if (ActionBarViewPagerController.this.mPagerAdapter.isRTL()) {
                    i2 = ActionBarViewPagerController.this.mPagerAdapter.toIndexForRTL(i2);
                    if (!this.mStatus.mScrollEnd) {
                        i2--;
                        f = 1.0f - f;
                    }
                }
                Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                while (it.hasNext()) {
                    ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageScrolled(i2, f, hasActionMenu, hasActionMenu2);
                }
            }

            @Override // androidx.viewpager.widget.OriginalViewPager.OnPageChangeListener
            public void onPageSelected(int i2) {
                int indexForRTL = ActionBarViewPagerController.this.mPagerAdapter.toIndexForRTL(i2);
                ActionBarViewPagerController.this.mActionBar.setSelectedNavigationItem(indexForRTL);
                ActionBarViewPagerController.this.mPagerAdapter.setPrimaryItem((ViewGroup) ActionBarViewPagerController.this.mViewPager, i2, (Object) ActionBarViewPagerController.this.mPagerAdapter.getFragment(i2, false, false));
                if (ActionBarViewPagerController.this.mListeners != null) {
                    Iterator it = ActionBarViewPagerController.this.mListeners.iterator();
                    while (it.hasNext()) {
                        ((ActionBar.FragmentViewPagerChangeListener) it.next()).onPageSelected(indexForRTL);
                    }
                }
            }
        });
        if (z && DeviceHelper.isFeatureWholeAnim()) {
            addOnFragmentViewPagerChangeListener(new ViewPagerScrollEffect(this.mViewPager, this.mPagerAdapter));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addFragmentTab(String str, ActionBar.Tab tab, int i, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        ((ActionBarImpl.TabImpl) tab).setInternalTabListener(this.mTabListener);
        this.mActionBar.internalAddTab(tab, i);
        int addFragment = this.mPagerAdapter.addFragment(str, i, cls, bundle, tab, z);
        if (this.mPagerAdapter.isRTL()) {
            this.mViewPager.setCurrentItem(this.mPagerAdapter.getCount() - 1);
        }
        return addFragment;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int addFragmentTab(String str, ActionBar.Tab tab, Class<? extends Fragment> cls, Bundle bundle, boolean z) {
        ((ActionBarImpl.TabImpl) tab).setInternalTabListener(this.mTabListener);
        this.mActionBar.internalAddTab(tab);
        int addFragment = this.mPagerAdapter.addFragment(str, cls, bundle, tab, z);
        if (this.mPagerAdapter.isRTL()) {
            this.mViewPager.setCurrentItem(this.mPagerAdapter.getCount() - 1);
        }
        return addFragment;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addOnFragmentViewPagerChangeListener(ActionBar.FragmentViewPagerChangeListener fragmentViewPagerChangeListener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(fragmentViewPagerChangeListener);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getFragmentTabCount() {
        return this.mPagerAdapter.getCount();
    }
}
