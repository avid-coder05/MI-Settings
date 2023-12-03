package miuix.appcompat.internal.app.widget;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Iterator;
import miuix.appcompat.app.ActionBar;
import miuix.internal.util.ViewUtils;
import miuix.viewpager.widget.ViewPager;

/* compiled from: ActionBarViewPagerController.java */
/* loaded from: classes5.dex */
class ViewPagerScrollEffect implements ActionBar.FragmentViewPagerChangeListener {
    DynamicFragmentPagerAdapter mPagerAdapter;
    ViewPager mViewPager;
    Rect sRect = new Rect();
    ArrayList<View> sList = new ArrayList<>();
    int mBaseItem = -1;
    boolean mBaseItemUpdated = true;
    int mScrollBasePosition = -1;
    int mIncomingPosition = -1;
    ViewGroup mListView = null;

    public ViewPagerScrollEffect(ViewPager viewPager, DynamicFragmentPagerAdapter dynamicFragmentPagerAdapter) {
        this.mViewPager = viewPager;
        this.mPagerAdapter = dynamicFragmentPagerAdapter;
    }

    void clearTranslation(ViewGroup viewGroup) {
        fillList(viewGroup, this.sList);
        if (this.sList.isEmpty()) {
            return;
        }
        Iterator<View> it = this.sList.iterator();
        while (it.hasNext()) {
            it.next().setTranslationX(0.0f);
        }
    }

    void clearTranslation(ArrayList<View> arrayList, ViewGroup viewGroup) {
        Iterator<View> it = arrayList.iterator();
        while (it.hasNext()) {
            View next = it.next();
            if (viewGroup.indexOfChild(next) == -1 && next.getTranslationX() != 0.0f) {
                next.setTranslationX(0.0f);
            }
        }
    }

    int computOffset(int i, int i2, int i3, float f) {
        float f2 = (i < i3 ? (i * i2) / i3 : i2) + ((0.1f - ((f * f) / 0.9f)) * i2);
        if (f2 > 0.0f) {
            return (int) f2;
        }
        return 0;
    }

    void fillList(ViewGroup viewGroup, ArrayList<View> arrayList) {
        clearTranslation(arrayList, viewGroup);
        arrayList.clear();
        ViewUtils.getContentRect(viewGroup, this.sRect);
        if (this.sRect.isEmpty()) {
            return;
        }
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() != 8 || childAt.getHeight() > 0) {
                arrayList.add(childAt);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrollStateChanged(int i) {
        if (i == 0) {
            this.mBaseItem = this.mViewPager.getCurrentItem();
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup = this.mListView;
            if (viewGroup != null) {
                clearTranslation(viewGroup);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageScrolled(int i, float f, boolean z, boolean z2) {
        if (f == 0.0f) {
            this.mBaseItem = i;
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup = this.mListView;
            if (viewGroup != null) {
                clearTranslation(viewGroup);
            }
        }
        if (this.mScrollBasePosition != i) {
            int i2 = this.mBaseItem;
            if (i2 < i) {
                this.mBaseItem = i;
            } else {
                int i3 = i + 1;
                if (i2 > i3) {
                    this.mBaseItem = i3;
                }
            }
            this.mScrollBasePosition = i;
            this.mBaseItemUpdated = true;
            ViewGroup viewGroup2 = this.mListView;
            if (viewGroup2 != null) {
                clearTranslation(viewGroup2);
            }
        }
        if (f > 0.0f) {
            if (this.mBaseItemUpdated) {
                this.mBaseItemUpdated = false;
                if (this.mBaseItem != i || i >= this.mPagerAdapter.getCount() - 1) {
                    this.mIncomingPosition = i;
                } else {
                    this.mIncomingPosition = i + 1;
                }
                Fragment fragment = this.mPagerAdapter.getFragment(this.mIncomingPosition, false);
                this.mListView = null;
                if (fragment != null && fragment.getView() != null) {
                    View findViewById = fragment.getView().findViewById(16908298);
                    if (findViewById instanceof ViewGroup) {
                        this.mListView = (ViewGroup) findViewById;
                    }
                }
            }
            if (this.mIncomingPosition == i) {
                f = 1.0f - f;
            }
            float f2 = f;
            ViewGroup viewGroup3 = this.mListView;
            if (viewGroup3 != null) {
                translateView(viewGroup3, viewGroup3.getWidth(), this.mListView.getHeight(), f2, this.mIncomingPosition != i);
            }
        }
    }

    @Override // miuix.appcompat.app.ActionBar.FragmentViewPagerChangeListener
    public void onPageSelected(int i) {
    }

    void translateView(ViewGroup viewGroup, int i, int i2, float f, boolean z) {
        fillList(viewGroup, this.sList);
        if (this.sList.isEmpty()) {
            return;
        }
        int i3 = 0;
        int top = this.sList.get(0).getTop();
        int i4 = Integer.MAX_VALUE;
        Iterator<View> it = this.sList.iterator();
        while (it.hasNext()) {
            View next = it.next();
            if (i4 != next.getTop()) {
                int top2 = next.getTop();
                int computOffset = computOffset(top2 - top, i, i2, f);
                if (!z) {
                    computOffset = -computOffset;
                }
                int i5 = computOffset;
                i4 = top2;
                i3 = i5;
            }
            next.setTranslationX(i3);
        }
    }
}
