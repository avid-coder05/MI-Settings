package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;

/* loaded from: classes2.dex */
public class FloatingAppBarScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior {
    private boolean initialized;

    public FloatingAppBarScrollingViewBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.material.appbar.AppBarLayout.ScrollingViewBehavior, androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onDependentViewChanged(CoordinatorLayout coordinatorLayout, View view, View view2) {
        boolean onDependentViewChanged = super.onDependentViewChanged(coordinatorLayout, view, view2);
        if (!this.initialized && (view2 instanceof AppBarLayout)) {
            this.initialized = true;
            setAppBarLayoutTransparent((AppBarLayout) view2);
        }
        return onDependentViewChanged;
    }

    void setAppBarLayoutTransparent(AppBarLayout appBarLayout) {
        appBarLayout.setBackgroundColor(0);
        appBarLayout.setTargetElevation(0.0f);
    }

    @Override // com.google.android.material.appbar.HeaderScrollingViewBehavior
    protected boolean shouldHeaderOverlapScrollingChild() {
        return true;
    }
}
