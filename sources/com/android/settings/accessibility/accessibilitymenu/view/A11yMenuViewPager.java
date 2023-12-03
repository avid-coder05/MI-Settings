package com.android.settings.accessibility.accessibilitymenu.view;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R;
import com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService;
import com.android.settings.accessibility.accessibilitymenu.model.A11yMenuShortcut;
import com.android.settings.accessibility.accessibilitymenu.view.A11yMenuViewPager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public final class A11yMenuViewPager {
    private ViewGroup a11yMenuLayout;
    private List<GridView> gridPageList = new ArrayList();
    public final AccessibilityMenuService service;
    public ViewPager viewPager;
    private ViewPagerAdapter<GridView> viewPagerAdapter;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.accessibility.accessibilitymenu.view.A11yMenuViewPager$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements ViewTreeObserver.OnGlobalLayoutListener {
        private boolean isFirstTime = true;

        AnonymousClass1() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onGlobalLayout$0(GridView gridView) {
            A11yMenuViewPager.this.adjustTextViewHeight(gridView);
        }

        @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
        public final void onGlobalLayout() {
            final GridView gridView;
            if (!this.isFirstTime || A11yMenuViewPager.this.gridPageList.isEmpty() || (gridView = (GridView) A11yMenuViewPager.this.gridPageList.get(0)) == null || gridView.getChildAt(0) == null) {
                return;
            }
            this.isFirstTime = false;
            gridView.postDelayed(new Runnable() { // from class: com.android.settings.accessibility.accessibilitymenu.view.A11yMenuViewPager$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    A11yMenuViewPager.AnonymousClass1.this.lambda$onGlobalLayout$0(gridView);
                }
            }, 100L);
            int measuredHeight = gridView.getChildAt(0).getMeasuredHeight();
            int dimension = (int) A11yMenuViewPager.this.service.getResources().getDimension(R.dimen.a11ymenu_layout_margin);
            int dimension2 = (int) A11yMenuViewPager.this.service.getResources().getDimension(R.dimen.table_margin_top);
            int i = A11yMenuViewPager.this.service.getResources().getConfiguration().orientation;
            int measuredHeight2 = A11yMenuViewPager.this.viewPager.getMeasuredHeight();
            if (i == 1) {
                measuredHeight2 = (measuredHeight * 3) + dimension + dimension2;
            } else if (i == 2) {
                DisplayMetrics displayMetrics = A11yMenuViewPager.this.service.getResources().getDisplayMetrics();
                measuredHeight2 = displayMetrics.heightPixels - ((LinearLayout) A11yMenuViewPager.this.a11yMenuLayout.findViewById(R.id.footerlayout)).getMeasuredHeight();
                int i2 = (((measuredHeight2 - dimension2) - dimension) - (measuredHeight * 3)) / 4;
                Iterator it = A11yMenuViewPager.this.gridPageList.iterator();
                while (it.hasNext()) {
                    ((GridView) it.next()).setVerticalSpacing(i2);
                }
                A11yMenuViewPager.this.viewPager.setPadding(dimension, i2 + dimension2, dimension, dimension);
                A11yMenuViewPager.this.viewPager.getLayoutParams().width = displayMetrics.heightPixels;
            }
            A11yMenuViewPager.this.viewPager.getLayoutParams().height = measuredHeight2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public A11yMenuViewPager(AccessibilityMenuService accessibilityMenuService) {
        this.service = accessibilityMenuService;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void adjustTextViewHeight(GridView gridView) {
        int[] iArr = {0, 0, 0};
        int childCount = gridView.getChildCount();
        View[] viewArr = new View[childCount];
        for (int i = 0; i < childCount; i++) {
            viewArr[i] = gridView.getChildAt(i).findViewById(R.id.shortcutLabel);
            int measuredHeight = viewArr[i].getMeasuredHeight();
            int i2 = i / 3;
            if (measuredHeight > iArr[i2]) {
                iArr[i2] = measuredHeight;
            }
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            viewArr[i3].setMinimumHeight(iArr[i3 / 3]);
        }
    }

    private void updateFooterState() {
    }

    public final void configureViewPagerAndFooter(ViewGroup viewGroup, List<A11yMenuShortcut> list) {
        this.a11yMenuLayout = viewGroup;
        this.viewPager = (ViewPager) viewGroup.findViewById(R.id.view_pager);
        ViewPagerAdapter<GridView> viewPagerAdapter = new ViewPagerAdapter<>();
        this.viewPagerAdapter = viewPagerAdapter;
        this.viewPager.setAdapter(viewPagerAdapter);
        if (list != null && !list.isEmpty()) {
            if (!this.gridPageList.isEmpty()) {
                this.gridPageList.clear();
            }
            int size = list.size();
            int i = 0;
            while (i < size) {
                int min = Math.min(i + 9, size);
                List<A11yMenuShortcut> subList = list.subList(i, min);
                GridView gridView = (GridView) LayoutInflater.from(this.service).inflate(R.layout.grid_view, (ViewGroup) null).findViewById(R.id.gridview);
                A11yMenuAdapter a11yMenuAdapter = new A11yMenuAdapter(this.service, subList);
                gridView.setNumColumns(3);
                gridView.setAdapter((ListAdapter) a11yMenuAdapter);
                this.gridPageList.add(gridView);
                i = min;
            }
            ViewPagerAdapter<GridView> viewPagerAdapter2 = this.viewPagerAdapter;
            viewPagerAdapter2.widgetList = this.gridPageList;
            viewPagerAdapter2.notifyDataSetChanged();
        }
        updateFooterState();
        this.a11yMenuLayout.getViewTreeObserver().addOnGlobalLayoutListener(new AnonymousClass1());
    }

    public void disableMenu(int i, boolean z) {
        View childAt;
        if (this.gridPageList.isEmpty() || (childAt = this.gridPageList.get(0).getChildAt(i)) == null) {
            return;
        }
        childAt.setAlpha(z ? 1.0f : 0.3f);
        View findViewById = childAt.findViewById(R.id.shortcutIconBtn);
        if (findViewById != null) {
            findViewById.setEnabled(z);
        }
    }
}
