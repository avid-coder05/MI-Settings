package com.android.settings.bluetooth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import com.android.settings.R;
import com.android.settingslib.util.HapticUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: classes.dex */
public class MiuiHeadsetTransparentAdjustView extends View {
    private static final Boolean DBG = Boolean.TRUE;
    final boolean isPrimaryUser;
    private int mBigPointCenterColor;
    private int mBigPointColor;
    private float mBigPointsRadius;
    private int mCurrentPointIndex;
    private HapticUtil mHapticUtil;
    private String[] mLabels;
    private int mLastCurrentPointIndex;
    private TransparentLevelChangeListener mListener;
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
    private class LabeledSeekBarExploreByTouchHelper extends ExploreByTouchHelper {
        private boolean mIsLayoutRtl;
        final /* synthetic */ MiuiHeadsetTransparentAdjustView this$0;

        private Rect getBoundsInParentFromVirtualViewId(int i) {
            if (this.mIsLayoutRtl) {
                i = (this.this$0.mPointCount - 1) - i;
            }
            Rect rect = new Rect();
            float floatValue = ((Float) this.this$0.mVirtualPointsXList.get(i)).floatValue();
            rect.set((int) floatValue, 0, (int) (this.this$0.getHeight() + floatValue), this.this$0.getHeight());
            return rect;
        }

        private int getHalfVirtualViewWidth() {
            return Math.max(0, ((this.this$0.getWidth() - this.this$0.getPaddingStart()) - this.this$0.getPaddingEnd()) / ((this.this$0.mPointCount - 1) * 2));
        }

        private int getVirtualViewIdIndexFromX(float f) {
            int min = Math.min((Math.max(0, (((int) f) - this.this$0.getPaddingStart()) / getHalfVirtualViewWidth()) + 1) / 2, this.this$0.mPointCount - 1);
            return this.mIsLayoutRtl ? (this.this$0.mPointCount - 1) - min : min;
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected int getVirtualViewAt(float f, float f2) {
            return getVirtualViewIdIndexFromX(f);
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void getVisibleVirtualViews(List<Integer> list) {
            int i = this.this$0.mPointCount;
            for (int i2 = 0; i2 < i; i2++) {
                list.add(Integer.valueOf(i2));
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected boolean onPerformActionForVirtualView(int i, int i2, Bundle bundle) {
            if (this.mIsLayoutRtl) {
                i = (this.this$0.mPointCount - 1) - i;
            }
            if (i != -1 && i2 == 16) {
                if (i != this.this$0.mCurrentPointIndex) {
                    this.this$0.mCurrentPointIndex = i;
                    if (this.this$0.mListener != null) {
                        this.this$0.mListener.onTransparentLevelChange(this.this$0.isRtl() ? (this.this$0.mPointCount - 1) - this.this$0.mCurrentPointIndex : this.this$0.mCurrentPointIndex);
                    }
                    this.this$0.invalidate();
                    this.this$0.mHapticUtil.performHapticFeedback();
                }
                sendEventForVirtualView(i, 1);
                return true;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // androidx.customview.widget.ExploreByTouchHelper
        public void onPopulateEventForVirtualView(int i, AccessibilityEvent accessibilityEvent) {
            try {
                accessibilityEvent.setClassName(Button.class.getName());
                accessibilityEvent.setChecked(i == this.this$0.mCurrentPointIndex);
                accessibilityEvent.setContentDescription(this.this$0.mLabels[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override // androidx.customview.widget.ExploreByTouchHelper
        protected void onPopulateNodeForVirtualView(int i, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
            try {
                accessibilityNodeInfoCompat.setClassName(Button.class.getName());
                accessibilityNodeInfoCompat.setBoundsInParent(getBoundsInParentFromVirtualViewId(i));
                accessibilityNodeInfoCompat.addAction(16);
                boolean z = true;
                accessibilityNodeInfoCompat.setClickable(true);
                accessibilityNodeInfoCompat.setCheckable(true);
                if (i != this.this$0.mCurrentPointIndex) {
                    z = false;
                }
                accessibilityNodeInfoCompat.setChecked(z);
                accessibilityNodeInfoCompat.setContentDescription(this.this$0.mLabels[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* loaded from: classes.dex */
    public interface RecommendListener {
    }

    /* loaded from: classes.dex */
    public interface TransparentLevelChangeListener {
        void onTransparentLevelChange(int i);
    }

    public MiuiHeadsetTransparentAdjustView(Context context) {
        super(context);
        this.mPointCount = 2;
        this.mCurrentPointIndex = 0;
        this.mLastCurrentPointIndex = 0;
        this.mPointsXList = new ArrayList();
        this.mVirtualPointsXList = new ArrayList();
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        init(null, 0);
    }

    public MiuiHeadsetTransparentAdjustView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPointCount = 2;
        this.mCurrentPointIndex = 0;
        this.mLastCurrentPointIndex = 0;
        this.mPointsXList = new ArrayList();
        this.mVirtualPointsXList = new ArrayList();
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
        init(attributeSet, 0);
    }

    public MiuiHeadsetTransparentAdjustView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mPointCount = 2;
        this.mCurrentPointIndex = 0;
        this.mLastCurrentPointIndex = 0;
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
        return super.dispatchHoverEvent(motionEvent);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (DBG.booleanValue()) {
            Log.d("MiuiHeadsetTransparentAdjustView", "onDraw");
        }
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
        getParent().requestDisallowInterceptTouchEvent(true);
        if (isEnabled() && motionEvent.getAction() == 1) {
            float f = 2.1474836E9f;
            int i = 0;
            for (int i2 = 0; i2 < this.mPointCount; i2++) {
                float abs = Math.abs(motionEvent.getX() - this.mPointsXList.get(i2).floatValue());
                if (abs < f) {
                    i = i2;
                    f = abs;
                }
            }
            Boolean bool = DBG;
            if (bool.booleanValue()) {
                Log.d("MiuiHeadsetTransparentAdjustView", "mCurrentPointIndex: " + this.mCurrentPointIndex + " nearestIndex: " + i);
            }
            if (i != this.mCurrentPointIndex) {
                this.mCurrentPointIndex = i;
                if (this.mListener != null) {
                    if (bool.booleanValue()) {
                        Log.d("MiuiHeadsetTransparentAdjustView", "Anc level change!");
                    }
                    this.mListener.onTransparentLevelChange(isRtl() ? (this.mPointCount - 1) - this.mCurrentPointIndex : this.mCurrentPointIndex);
                }
                invalidate();
                ensurePerformHapticFeedback(motionEvent.getAction());
            }
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
        }
    }

    public void setRecommendListener(RecommendListener recommendListener) {
        this.mRecommendListener = recommendListener;
    }

    public void setTransparentLevelChangeListener(TransparentLevelChangeListener transparentLevelChangeListener) {
        this.mListener = transparentLevelChangeListener;
    }
}
