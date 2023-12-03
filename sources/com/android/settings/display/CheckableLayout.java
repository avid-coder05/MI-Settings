package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.LinearLayout;

/* loaded from: classes.dex */
public class CheckableLayout extends LinearLayout implements Checkable {
    private boolean mChecked;

    public CheckableLayout(Context context) {
        super(context);
        this.mChecked = false;
    }

    public CheckableLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, 0);
        this.mChecked = false;
    }

    public CheckableLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChecked = false;
    }

    @Override // android.widget.Checkable
    public boolean isChecked() {
        return this.mChecked;
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean z) {
        if (this.mChecked != z) {
            this.mChecked = z;
            refreshDrawableState();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                if (childAt instanceof Checkable) {
                    ((Checkable) childAt).setChecked(z);
                }
            }
        }
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }
}
