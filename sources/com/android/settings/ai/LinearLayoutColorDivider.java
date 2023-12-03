package com.android.settings.ai;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class LinearLayoutColorDivider extends RecyclerView.ItemDecoration {
    private final Drawable mDivider;
    private final int mOrientation;
    private final int mSize;

    public LinearLayoutColorDivider(Resources resources, int i, int i2, int i3) {
        this.mDivider = new ColorDrawable(resources.getColor(i));
        this.mSize = resources.getDimensionPixelSize(i2);
        this.mOrientation = i3;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        if (this.mOrientation == 0) {
            rect.set(0, 0, this.mSize, 0);
        } else {
            rect.set(0, 0, 0, this.mSize);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
    public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
        Rect rect = new Rect();
        int i = 0;
        if (this.mOrientation == 0) {
            rect.top = recyclerView.getPaddingTop();
            rect.bottom = recyclerView.getHeight() - recyclerView.getPaddingBottom();
            int childCount = recyclerView.getChildCount();
            while (i < childCount - 1) {
                View childAt = recyclerView.getChildAt(i);
                int right = childAt.getRight() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt.getLayoutParams())).rightMargin;
                rect.left = right;
                rect.right = right + this.mSize;
                this.mDivider.setBounds(rect);
                this.mDivider.draw(canvas);
                i++;
            }
            return;
        }
        rect.left = recyclerView.getPaddingLeft();
        rect.right = recyclerView.getWidth() - recyclerView.getPaddingRight();
        int childCount2 = recyclerView.getChildCount();
        while (i < childCount2 - 1) {
            View childAt2 = recyclerView.getChildAt(i);
            int bottom = childAt2.getBottom() + ((ViewGroup.MarginLayoutParams) ((RecyclerView.LayoutParams) childAt2.getLayoutParams())).bottomMargin;
            rect.top = bottom;
            rect.bottom = bottom + this.mSize;
            this.mDivider.setBounds(rect);
            this.mDivider.draw(canvas);
            i++;
        }
    }
}
