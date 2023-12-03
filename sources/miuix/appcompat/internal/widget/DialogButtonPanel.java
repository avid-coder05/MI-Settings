package miuix.appcompat.internal.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import miuix.appcompat.R$dimen;
import miuix.internal.util.ViewUtils;

/* loaded from: classes5.dex */
public class DialogButtonPanel extends LinearLayout {
    private final int mButtonMarginHorizontal;
    private final int mButtonMarginVertical;
    private boolean mForceVertical;
    private final int mPanelPaddingHorizontal;

    public DialogButtonPanel(Context context) {
        this(context, null);
    }

    public DialogButtonPanel(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DialogButtonPanel(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPanelPaddingHorizontal = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_dialog_button_panel_horizontal_margin);
        this.mButtonMarginHorizontal = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_dialog_btn_margin_horizontal);
        this.mButtonMarginVertical = getResources().getDimensionPixelOffset(R$dimen.miuix_appcompat_dialog_btn_margin_vertical);
    }

    private void handleButtonLayout(int i) {
        boolean isVerticalNeeded = isVerticalNeeded(i);
        int childCount = getChildCount();
        if (isVerticalNeeded) {
            setOrientation(1);
            setPadding(this.mPanelPaddingHorizontal, getPaddingTop(), this.mPanelPaddingHorizontal, getPaddingBottom());
            int i2 = 0;
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt = getChildAt(i3);
                boolean z = childAt.getVisibility() == 0;
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) childAt.getLayoutParams();
                layoutParams.width = -1;
                layoutParams.height = -2;
                layoutParams.weight = 0.0f;
                layoutParams.topMargin = z ? i2 : 0;
                layoutParams.rightMargin = 0;
                layoutParams.leftMargin = 0;
                if (z) {
                    i2 = this.mButtonMarginVertical;
                }
            }
            return;
        }
        setOrientation(0);
        setPadding(this.mPanelPaddingHorizontal, getPaddingTop(), this.mPanelPaddingHorizontal, getPaddingBottom());
        boolean isLayoutRtl = ViewUtils.isLayoutRtl(this);
        int i4 = 0;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt2 = getChildAt(i5);
            boolean z2 = childAt2.getVisibility() == 0;
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) childAt2.getLayoutParams();
            layoutParams2.width = 0;
            layoutParams2.height = -1;
            layoutParams2.weight = 1.0f;
            layoutParams2.topMargin = 0;
            if (!z2) {
                layoutParams2.rightMargin = 0;
                layoutParams2.leftMargin = 0;
            } else if (isLayoutRtl) {
                layoutParams2.rightMargin = i4;
            } else {
                layoutParams2.leftMargin = i4;
            }
            if (z2) {
                i4 = this.mButtonMarginHorizontal;
            }
        }
    }

    private boolean isEllipsized(TextView textView, int i) {
        return ((int) textView.getPaint().measureText(textView.getText().toString())) > (i - textView.getPaddingStart()) - textView.getPaddingEnd();
    }

    private boolean isVerticalNeeded(int i) {
        if (this.mForceVertical) {
            return true;
        }
        int childCount = getChildCount();
        int i2 = childCount;
        for (int i3 = childCount - 1; i3 >= 0; i3--) {
            if (getChildAt(i3).getVisibility() == 8) {
                i2--;
            }
        }
        if (i2 < 2) {
            return false;
        }
        if (i2 >= 3) {
            return true;
        }
        int i4 = (i - this.mButtonMarginHorizontal) / 2;
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if ((childAt instanceof TextView) && childAt.getVisibility() == 0 && isEllipsized((TextView) childAt, i4)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        handleButtonLayout(View.MeasureSpec.getSize(i));
        super.onMeasure(i, i2);
    }

    public void setForceVertical(boolean z) {
        this.mForceVertical = z;
    }
}
