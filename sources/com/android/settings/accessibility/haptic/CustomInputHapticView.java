package com.android.settings.accessibility.haptic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.settings.R;
import com.android.settings.R$styleable;
import java.util.List;

/* loaded from: classes.dex */
public class CustomInputHapticView extends View {
    private final int mBgColor;
    private final Paint mPaint;
    private final int mRadius;
    private String mText;
    private CustomViewTouchHelper mTouchHelper;

    /* loaded from: classes.dex */
    private class CustomViewTouchHelper extends ExploreByTouchHelper {
        private final Rect mTempRect;

        public CustomViewTouchHelper(View view) {
            super(view);
            this.mTempRect = new Rect();
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return 0;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List<Integer> list) {
        }

        protected boolean onItemClicked(int i) {
            CustomInputHapticView.this.mTouchHelper.invalidateVirtualView(i);
            CustomInputHapticView.this.mTouchHelper.sendEventForVirtualView(i, 1);
            return true;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (i2 != 16) {
                return false;
            }
            return onItemClicked(i);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            super.onPopulateEventForVirtualView(i, accessibilityEvent);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setText(CustomInputHapticView.this.mText);
            accessibilityNodeInfoCompat.setContentDescription(CustomInputHapticView.this.mText);
            Rect rect = this.mTempRect;
            accessibilityNodeInfoCompat.addAction(16);
            rect.left = 0;
            rect.right = CustomInputHapticView.this.getWidth();
            rect.top = 0;
            rect.bottom = CustomInputHapticView.this.getHeight();
            accessibilityNodeInfoCompat.setBoundsInParent(rect);
        }
    }

    public CustomInputHapticView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPaint = new Paint();
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.CustomA11yHapticView);
        String string = getResources().getString(R.string.general_text);
        this.mBgColor = obtainStyledAttributes.getColor(R$styleable.CustomA11yHapticView_bg, getResources().getColor(R.color.input_view_bg_color));
        this.mRadius = obtainStyledAttributes.getDimensionPixelSize(R$styleable.CustomA11yHapticView_radius, 60);
        String string2 = obtainStyledAttributes.getString(R$styleable.CustomA11yHapticView_text);
        this.mText = TextUtils.isEmpty(string2) ? string : string2;
        obtainStyledAttributes.recycle();
        CustomViewTouchHelper customViewTouchHelper = new CustomViewTouchHelper(this);
        this.mTouchHelper = customViewTouchHelper;
        ViewCompat.setAccessibilityDelegate(this, customViewTouchHelper);
    }

    @Override // android.view.View
    public boolean dispatchHoverEvent(MotionEvent motionEvent) {
        return super.dispatchHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return CustomInputHapticView.class.getName();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mBgColor);
        float width = getWidth();
        float height = getHeight();
        int i = this.mRadius;
        canvas.drawRoundRect(0.0f, 0.0f, width, height, i, i, this.mPaint);
        this.mPaint.setColor(getResources().getColor(R.color.input_view_cursor_color));
        canvas.drawRect(getResources().getDimensionPixelSize(R.dimen.custom_input_left), getResources().getDimensionPixelSize(R.dimen.custom_input_top), getResources().getDimensionPixelSize(R.dimen.custom_input_right), getResources().getDimensionPixelSize(R.dimen.custom_input_bottom), this.mPaint);
    }
}
