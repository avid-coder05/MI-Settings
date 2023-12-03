package miuix.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.ViewUtils;
import miuix.appcompat.R$attr;
import miuix.appcompat.R$styleable;

/* loaded from: classes5.dex */
public class GroupButton extends AppCompatButton {
    private Drawable mButtonSelectorBackground;
    private boolean mPrimary;
    private static final int[] STATE_FIRST_V = {R$attr.state_first_v};
    private static final int[] STATE_MIDDLE_V = {R$attr.state_middle_v};
    private static final int[] STATE_LAST_V = {R$attr.state_last_v};
    private static final int[] STATE_FIRST_H = {R$attr.state_first_h};
    private static final int[] STATE_MIDDLE_H = {R$attr.state_middle_h};
    private static final int[] STATE_LAST_H = {R$attr.state_last_h};
    private static final int[] STATE_SINGLE_H = {R$attr.state_single_h};

    public GroupButton(Context context) {
        super(context);
    }

    public GroupButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GroupButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initAttr(context, attributeSet, i);
    }

    private void initAttr(Context context, AttributeSet attributeSet, int i) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.GroupButton, i, 0);
        try {
            int i2 = R$styleable.GroupButton_miuixSelectGroupButtonBackground;
            if (obtainStyledAttributes.hasValue(i2)) {
                this.mButtonSelectorBackground = obtainStyledAttributes.getDrawable(i2);
            }
            int i3 = R$styleable.GroupButton_primaryButton;
            if (obtainStyledAttributes.hasValue(i3)) {
                this.mPrimary = obtainStyledAttributes.getBoolean(i3, false);
            }
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public Drawable getButtonSelectorBackground() {
        return this.mButtonSelectorBackground;
    }

    public boolean isPrimary() {
        return this.mPrimary;
    }

    @Override // android.widget.TextView, android.view.View
    protected int[] onCreateDrawableState(int i) {
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (viewGroup != null && (viewGroup instanceof LinearLayout)) {
            int orientation = ((LinearLayout) viewGroup).getOrientation();
            int indexOfChild = viewGroup.indexOfChild(this);
            int i2 = 0;
            boolean z = true;
            boolean z2 = true;
            for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                if (viewGroup.getChildAt(i3).getVisibility() == 0) {
                    i2++;
                    if (i3 < indexOfChild) {
                        z = false;
                    }
                    if (i3 > indexOfChild) {
                        z2 = false;
                    }
                }
            }
            boolean z3 = i2 == 1;
            if (orientation == 1) {
                int[] onCreateDrawableState = super.onCreateDrawableState(i + 2);
                Button.mergeDrawableStates(onCreateDrawableState, STATE_SINGLE_H);
                if (!z3) {
                    if (z) {
                        Button.mergeDrawableStates(onCreateDrawableState, STATE_FIRST_V);
                    } else if (z2) {
                        Button.mergeDrawableStates(onCreateDrawableState, STATE_LAST_V);
                    } else {
                        Button.mergeDrawableStates(onCreateDrawableState, STATE_MIDDLE_V);
                    }
                }
                return onCreateDrawableState;
            }
            boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
            int[] onCreateDrawableState2 = super.onCreateDrawableState(i + 1);
            if (z3) {
                Button.mergeDrawableStates(onCreateDrawableState2, STATE_SINGLE_H);
            } else if (z) {
                Button.mergeDrawableStates(onCreateDrawableState2, isLayoutRtl ? STATE_LAST_H : STATE_FIRST_H);
            } else if (z2) {
                Button.mergeDrawableStates(onCreateDrawableState2, isLayoutRtl ? STATE_FIRST_H : STATE_LAST_H);
            } else {
                Button.mergeDrawableStates(onCreateDrawableState2, STATE_MIDDLE_H);
            }
            return onCreateDrawableState2;
        }
        return super.onCreateDrawableState(i);
    }
}
