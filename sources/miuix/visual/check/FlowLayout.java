package miuix.visual.check;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.view.ViewCompat;
import miuix.visualcheck.R$styleable;

/* loaded from: classes5.dex */
public class FlowLayout extends ViewGroup {
    private int itemSpacing;
    private int lineSpacing;
    private int mLineGravity;
    private boolean singleLine;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.singleLine = false;
        loadFromAttributes(context, attributeSet);
    }

    @TargetApi(21)
    public FlowLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.singleLine = false;
        loadFromAttributes(context, attributeSet);
    }

    private static int getMeasuredDimension(int i, int i2, int i3) {
        return i2 != Integer.MIN_VALUE ? i2 != 1073741824 ? i3 : i : Math.min(i3, i);
    }

    private void loadFromAttributes(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R$styleable.FlowLayout, 0, 0);
        this.lineSpacing = obtainStyledAttributes.getDimensionPixelSize(R$styleable.FlowLayout_lineSpacing, 0);
        this.itemSpacing = obtainStyledAttributes.getDimensionPixelSize(R$styleable.FlowLayout_itemSpacing, 0);
        this.mLineGravity = obtainStyledAttributes.getInt(R$styleable.FlowLayout_lineGravity, 4);
        obtainStyledAttributes.recycle();
    }

    protected int getItemSpacing() {
        return this.itemSpacing;
    }

    protected int getLineSpacing() {
        return this.lineSpacing;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        if (getChildCount() == 0) {
            return;
        }
        boolean z2 = ViewCompat.getLayoutDirection(this) == 1;
        int paddingRight = z2 ? getPaddingRight() : getPaddingLeft();
        int paddingLeft = z2 ? getPaddingLeft() : getPaddingRight();
        int paddingTop = getPaddingTop();
        int i8 = (i3 - i) - paddingLeft;
        int i9 = paddingRight;
        int i10 = paddingTop;
        int i11 = i8;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        while (i12 < getChildCount()) {
            View childAt = getChildAt(i12);
            if (childAt.getVisibility() == 8) {
                i7 = paddingRight;
            } else {
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    i6 = MarginLayoutParamsCompat.getMarginStart(marginLayoutParams);
                    i5 = MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams);
                } else {
                    i5 = 0;
                    i6 = 0;
                }
                int measuredWidth = i9 + i6 + childAt.getMeasuredWidth();
                if (!this.singleLine && measuredWidth > i8) {
                    i10 = paddingTop + this.lineSpacing;
                    i9 = paddingRight;
                    i13 = i12;
                }
                int i15 = i9 + i6;
                int measuredWidth2 = childAt.getMeasuredWidth() + i15;
                i7 = paddingRight;
                int measuredHeight = i10 + childAt.getMeasuredHeight();
                if (z2) {
                    childAt.layout(i8 - measuredWidth2, i10, (i8 - i9) - i6, measuredHeight);
                } else {
                    childAt.layout(i15, i10, measuredWidth2, measuredHeight);
                }
                if (this.mLineGravity != 1 && i13 == i12 && i13 != 0) {
                    while (i14 < i13) {
                        int i16 = this.mLineGravity == 4 ? i11 / 2 : i11;
                        View childAt2 = getChildAt(i14);
                        if (z2) {
                            i16 = -i16;
                        }
                        childAt2.offsetLeftAndRight(i16);
                        i14++;
                    }
                }
                i9 += i6 + i5 + childAt.getMeasuredWidth() + this.itemSpacing;
                i11 = i8 - i9;
                if (this.mLineGravity != 1 && i12 == getChildCount() - 1) {
                    for (int i17 = i13; i17 <= i12; i17++) {
                        int i18 = this.mLineGravity == 4 ? i11 / 2 : i11;
                        View childAt3 = getChildAt(i17);
                        if (z2) {
                            i18 = -i18;
                        }
                        childAt3.offsetLeftAndRight(i18);
                    }
                }
                if (paddingTop < measuredHeight) {
                    paddingTop = measuredHeight;
                }
                i14 = i13;
            }
            i12++;
            paddingRight = i7;
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i2);
        int i7 = (mode == Integer.MIN_VALUE || mode == 1073741824) ? size : Integer.MAX_VALUE;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = i7 - getPaddingRight();
        int i8 = paddingTop;
        int i9 = 0;
        int i10 = 0;
        while (i9 < getChildCount()) {
            View childAt = getChildAt(i9);
            if (childAt.getVisibility() == 8) {
                i6 = paddingRight;
            } else {
                measureChild(childAt, i, i2);
                ViewGroup.LayoutParams layoutParams = childAt.getLayoutParams();
                if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
                    i3 = marginLayoutParams.leftMargin + 0;
                    i4 = marginLayoutParams.rightMargin + 0;
                } else {
                    i3 = 0;
                    i4 = 0;
                }
                int i11 = paddingLeft;
                if (paddingLeft + i3 + childAt.getMeasuredWidth() <= paddingRight || this.singleLine) {
                    i5 = i11;
                } else {
                    i5 = getPaddingLeft();
                    i8 = this.lineSpacing + paddingTop;
                }
                i6 = paddingRight;
                int measuredWidth = i5 + i3 + childAt.getMeasuredWidth();
                int measuredHeight = i8 + childAt.getMeasuredHeight();
                if (measuredWidth > i10) {
                    i10 = measuredWidth;
                }
                paddingLeft = i5 + i3 + i4 + childAt.getMeasuredWidth() + this.itemSpacing;
                if (i9 == getChildCount() - 1) {
                    i10 += i4;
                }
                if (paddingTop < measuredHeight) {
                    paddingTop = measuredHeight;
                }
            }
            i9++;
            paddingRight = i6;
        }
        setMeasuredDimension(getMeasuredDimension(size, mode, i10 + getPaddingRight()), getMeasuredDimension(size2, mode2, paddingTop + getPaddingBottom()));
    }

    protected void setItemSpacing(int i) {
        this.itemSpacing = i;
    }

    protected void setLineSpacing(int i) {
        this.lineSpacing = i;
    }
}
