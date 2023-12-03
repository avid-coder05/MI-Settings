package com.android.settings.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.settings.R;
import com.android.settingslib.util.HapticUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class FontSizeAdjustView extends View {
    final boolean isPrimaryUser;
    private ExploreByTouchHelper mAccessHelper;
    private int mBigPointCenterColor;
    private int mBigPointColor;
    private float mBigPointsRadius;
    private int mCurrentPointIndex;
    private HapticUtil mHapticUtil;
    private String[] mLabels;
    private int mLastCurrentPointIndex;
    private FontSizeChangeListener mListener;
    private int mPointCount;
    private Paint mPointPaint;
    private float mPointsRadius;
    private List<Float> mPointsXList;
    private float mPointsY;
    private RecommendListener mRecommendListener;
    private int mSmallPointColor;
    private List<Float> mVirtualPointsXList;
    final int myUserId;

    /* loaded from: classes.dex */
    public interface FontSizeChangeListener {
        void onSizeChange(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class LabeledSeekBarExploreByTouchHelper extends ExploreByTouchHelper {
        private boolean mIsLayoutRtl;

        public LabeledSeekBarExploreByTouchHelper(View view) {
            super(view);
            this.mIsLayoutRtl = view.getResources().getConfiguration().getLayoutDirection() == 1;
        }

        private Rect getBoundsInParentFromVirtualViewId(int i) {
            if (this.mIsLayoutRtl) {
                i = (FontSizeAdjustView.this.mPointCount - 1) - i;
            }
            Rect rect = new Rect();
            float floatValue = ((Float) FontSizeAdjustView.this.mVirtualPointsXList.get(i)).floatValue();
            rect.set((int) floatValue, 0, (int) (FontSizeAdjustView.this.getHeight() + floatValue), FontSizeAdjustView.this.getHeight());
            return rect;
        }

        private int getHalfVirtualViewWidth() {
            return Math.max(0, ((FontSizeAdjustView.this.getWidth() - FontSizeAdjustView.this.getPaddingStart()) - FontSizeAdjustView.this.getPaddingEnd()) / ((FontSizeAdjustView.this.mPointCount - 1) * 2));
        }

        private int getVirtualViewIdIndexFromX(float f) {
            int min = Math.min((Math.max(0, (((int) f) - FontSizeAdjustView.this.getPaddingStart()) / getHalfVirtualViewWidth()) + 1) / 2, FontSizeAdjustView.this.mPointCount - 1);
            return this.mIsLayoutRtl ? (FontSizeAdjustView.this.mPointCount - 1) - min : min;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return getVirtualViewIdIndexFromX(f);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List<Integer> list) {
            int i = FontSizeAdjustView.this.mPointCount;
            for (int i2 = 0; i2 < i; i2++) {
                list.add(Integer.valueOf(i2));
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (this.mIsLayoutRtl) {
                i = (FontSizeAdjustView.this.mPointCount - 1) - i;
            }
            if (i != -1 && i2 == 16) {
                if (i != FontSizeAdjustView.this.mCurrentPointIndex) {
                    FontSizeAdjustView.this.mCurrentPointIndex = i;
                    if (FontSizeAdjustView.this.mListener != null) {
                        FontSizeAdjustView.this.mListener.onSizeChange(FontSizeAdjustView.this.isRtl() ? (FontSizeAdjustView.this.mPointCount - 1) - FontSizeAdjustView.this.mCurrentPointIndex : FontSizeAdjustView.this.mCurrentPointIndex);
                    }
                    FontSizeAdjustView.this.invalidate();
                    FontSizeAdjustView.this.mHapticUtil.performHapticFeedback();
                }
                sendEventForVirtualView(i, 1);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.setClassName(Button.class.getName());
            accessibilityEvent.setContentDescription(FontSizeAdjustView.this.mLabels[i]);
            accessibilityEvent.setChecked(i == FontSizeAdjustView.this.mCurrentPointIndex);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            accessibilityNodeInfoCompat.setClassName(Button.class.getName());
            accessibilityNodeInfoCompat.setBoundsInParent(getBoundsInParentFromVirtualViewId(i));
            accessibilityNodeInfoCompat.addAction(16);
            accessibilityNodeInfoCompat.setContentDescription(FontSizeAdjustView.this.mLabels[i]);
            accessibilityNodeInfoCompat.setClickable(true);
            accessibilityNodeInfoCompat.setCheckable(true);
            accessibilityNodeInfoCompat.setChecked(i == FontSizeAdjustView.this.mCurrentPointIndex);
        }
    }

    /* loaded from: classes.dex */
    public interface RecommendListener {
        void scrollViewToHideRecommend();

        void showRecommendLayout();
    }

    public FontSizeAdjustView(Context context) {
        super(context);
        this.mPointCount = 7;
        this.mCurrentPointIndex = 1;
        this.mLastCurrentPointIndex = 1;
        this.mPointsXList = new ArrayList();
        this.mVirtualPointsXList = new ArrayList();
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        init(null, 0);
    }

    public FontSizeAdjustView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPointCount = 7;
        this.mCurrentPointIndex = 1;
        this.mLastCurrentPointIndex = 1;
        this.mPointsXList = new ArrayList();
        this.mVirtualPointsXList = new ArrayList();
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        init(attributeSet, 0);
    }

    public FontSizeAdjustView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPointCount = 7;
        this.mCurrentPointIndex = 1;
        this.mLastCurrentPointIndex = 1;
        this.mPointsXList = new ArrayList();
        this.mVirtualPointsXList = new ArrayList();
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        init(attributeSet, i);
    }

    private void ensurePerformHapticFeedback(int i) {
        this.mHapticUtil.performHapticFeedback();
    }

    private void init(AttributeSet attributeSet, int i) {
        this.mBigPointColor = getResources().getColor(R.color.font_size_seekbar_big_pointer_blue, null);
        this.mSmallPointColor = getResources().getColor(R.color.font_size_view_small_color, null);
        this.mBigPointCenterColor = getResources().getColor(R.color.font_size_view_big_center_color, null);
        this.mPointsRadius = getResources().getDimension(R.dimen.font_size_view_small_radius);
        this.mBigPointsRadius = getResources().getDimension(R.dimen.font_size_view_big_radius);
        Paint paint = new Paint();
        this.mPointPaint = paint;
        paint.setAntiAlias(true);
        this.mPointPaint.setStyle(Paint.Style.FILL);
        this.mPointPaint.setStrokeWidth(0.0f);
        this.mHapticUtil = HapticUtil.getInstance(getContext().getApplicationContext());
        LabeledSeekBarExploreByTouchHelper labeledSeekBarExploreByTouchHelper = new LabeledSeekBarExploreByTouchHelper(this);
        this.mAccessHelper = labeledSeekBarExploreByTouchHelper;
        ViewCompat.setAccessibilityDelegate(this, labeledSeekBarExploreByTouchHelper);
        this.mLabels = ScreenZoomUtils.getEntriesFontSize(getContext());
        if (this.isPrimaryUser) {
            return;
        }
        setEnabled(false);
        setAlpha(0.3f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRtl() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        return this.mAccessHelper.dispatchHoverEvent(motionEvent) || super.dispatchHoverEvent(motionEvent);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < this.mPointCount; i++) {
            if (i == this.mCurrentPointIndex) {
                this.mPointPaint.setColor(this.mBigPointColor);
                canvas.drawCircle(this.mPointsXList.get(i).floatValue(), this.mPointsY, this.mBigPointsRadius, this.mPointPaint);
                this.mPointPaint.setColor(this.mBigPointCenterColor);
                canvas.drawCircle(this.mPointsXList.get(i).floatValue(), this.mPointsY, this.mPointsRadius, this.mPointPaint);
            } else {
                this.mPointPaint.setColor(this.mSmallPointColor);
                canvas.drawCircle(this.mPointsXList.get(i).floatValue(), this.mPointsY, this.mPointsRadius, this.mPointPaint);
            }
        }
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mPointsY = getHeight() / 2;
        float width = (getWidth() - getHeight()) / (this.mPointCount - 1);
        this.mPointsXList.clear();
        for (int i5 = 0; i5 < this.mPointCount; i5++) {
            float f = i5 * width;
            this.mPointsXList.add(Float.valueOf((getHeight() / 2) + f));
            this.mVirtualPointsXList.add(Float.valueOf(f));
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int i;
        getParent().requestDisallowInterceptTouchEvent(true);
        if (isEnabled()) {
            int action = motionEvent.getAction();
            if (action == 0 || action == 1 || action == 2) {
                float f = 2.1474836E9f;
                int i2 = 0;
                for (int i3 = 0; i3 < this.mPointCount; i3++) {
                    float abs = Math.abs(motionEvent.getX() - this.mPointsXList.get(i3).floatValue());
                    if (abs < f) {
                        i2 = i3;
                        f = abs;
                    }
                }
                if (i2 != this.mCurrentPointIndex) {
                    this.mCurrentPointIndex = i2;
                    FontSizeChangeListener fontSizeChangeListener = this.mListener;
                    if (fontSizeChangeListener != null) {
                        fontSizeChangeListener.onSizeChange(isRtl() ? (this.mPointCount - 1) - this.mCurrentPointIndex : this.mCurrentPointIndex);
                    }
                    invalidate();
                    ensurePerformHapticFeedback(motionEvent.getAction());
                }
                if (1 == action && this.mRecommendListener != null) {
                    int i4 = isRtl() ? (this.mPointCount - 1) - 5 : 5;
                    int i5 = this.mCurrentPointIndex;
                    if (i5 == i4) {
                        this.mLastCurrentPointIndex = i5;
                        this.mRecommendListener.showRecommendLayout();
                    }
                    if (this.mLastCurrentPointIndex == i4 && (i = this.mCurrentPointIndex) != i4) {
                        this.mLastCurrentPointIndex = i;
                        this.mRecommendListener.scrollViewToHideRecommend();
                    }
                }
            }
            return true;
        }
        return true;
    }

    public void setCurrentPointIndex(int i) {
        this.mCurrentPointIndex = i;
        if (isRtl()) {
            this.mCurrentPointIndex = (this.mPointCount - 1) - i;
        }
        invalidate();
    }

    public void setFontSizeChangeListener(FontSizeChangeListener fontSizeChangeListener) {
        this.mListener = fontSizeChangeListener;
    }

    public void setLastCurrentPointIndex(int i) {
        this.mLastCurrentPointIndex = i;
        if (isRtl()) {
            this.mLastCurrentPointIndex = (this.mPointCount - 1) - i;
        }
        invalidate();
    }

    public void setPointCount(int i) {
        if (i > 0) {
            this.mPointCount = i;
            this.mLabels = ScreenZoomUtils.getEntries(getContext());
        }
    }

    public void setRecommendListener(RecommendListener recommendListener) {
        this.mRecommendListener = recommendListener;
    }
}
