package androidx.recyclerview.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.Interpolator;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import miuix.animation.utils.VelocityMonitor;
import miuix.overscroller.widget.OverScroller;
import miuix.recyclerview.R$attr;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public abstract class RemixRecyclerView extends RecyclerView {
    private int mScrollPointerId;
    private boolean mSpringEnabled;
    private VelocityMonitor mVelocityMonitor;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ViewFlinger extends RecyclerView.ViewFlinger {
        int mCurrentFlingVelocityX;
        int mCurrentFlingVelocityY;
        private boolean mEatRunOnAnimationRequest;
        private boolean mHasReachEdgeBeforeFling;
        boolean mInterimTarget;
        Interpolator mInterpolator;
        private int mLastFlingX;
        private int mLastFlingY;
        OverScroller mOverScroller;
        private boolean mReSchedulePostAnimationCallback;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ViewFlinger() {
            super();
            Interpolator interpolator = RecyclerView.sQuinticInterpolator;
            this.mInterpolator = interpolator;
            this.mEatRunOnAnimationRequest = false;
            this.mReSchedulePostAnimationCallback = false;
            this.mCurrentFlingVelocityX = 0;
            this.mCurrentFlingVelocityY = 0;
            this.mInterimTarget = false;
            this.mOverScroller = new OverScroller(RemixRecyclerView.this.getContext(), interpolator);
        }

        private int computeScrollDuration(int i, int i2, int i3, int i4) {
            int i5;
            int abs = Math.abs(i);
            int abs2 = Math.abs(i2);
            boolean z = abs > abs2;
            int sqrt = (int) Math.sqrt((i3 * i3) + (i4 * i4));
            int sqrt2 = (int) Math.sqrt((i * i) + (i2 * i2));
            RemixRecyclerView remixRecyclerView = RemixRecyclerView.this;
            int width = z ? remixRecyclerView.getWidth() : remixRecyclerView.getHeight();
            int i6 = width / 2;
            float f = width;
            float f2 = i6;
            float distanceInfluenceForSnapDuration = f2 + (distanceInfluenceForSnapDuration(Math.min(1.0f, (sqrt2 * 1.0f) / f)) * f2);
            if (sqrt > 0) {
                i5 = Math.round(Math.abs(distanceInfluenceForSnapDuration / sqrt) * 1000.0f) * 4;
            } else {
                if (!z) {
                    abs = abs2;
                }
                i5 = (int) (((abs / f) + 1.0f) * 300.0f);
            }
            return Math.min(i5, 2000);
        }

        private float distanceInfluenceForSnapDuration(float f) {
            return (float) Math.sin((f - 0.5f) * 0.47123894f);
        }

        private void internalPostOnAnimation() {
            RemixRecyclerView.this.removeCallbacks(this);
            ViewCompat.postOnAnimation(RemixRecyclerView.this, this);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void fling(int i, int i2) {
            RemixRecyclerView.this.setScrollState(2);
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
            Interpolator interpolator = this.mInterpolator;
            Interpolator interpolator2 = RecyclerView.sQuinticInterpolator;
            if (interpolator != interpolator2) {
                this.mInterpolator = interpolator2;
                this.mOverScroller = new OverScroller(RemixRecyclerView.this.getContext(), interpolator2);
            }
            if (i != 0) {
                i = -((int) RemixRecyclerView.this.mVelocityMonitor.getVelocity(0));
            }
            int i3 = i;
            if (i2 != 0) {
                i2 = -((int) RemixRecyclerView.this.mVelocityMonitor.getVelocity(1));
            }
            int i4 = i2;
            boolean canScrollHorizontally = RemixRecyclerView.this.mLayout.canScrollHorizontally();
            if (RemixRecyclerView.this.mLayout.canScrollVertically()) {
                canScrollHorizontally |= true;
            }
            if (canScrollHorizontally) {
                this.mHasReachEdgeBeforeFling = true ^ RemixRecyclerView.this.canScrollVertically(i4 > 0 ? 1 : -1);
            } else if (canScrollHorizontally) {
                this.mHasReachEdgeBeforeFling = true ^ RemixRecyclerView.this.canScrollHorizontally(i3 > 0 ? 1 : -1);
            }
            this.mOverScroller.fling(0, 0, i3, i4, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void postOnAnimation() {
            if (this.mEatRunOnAnimationRequest) {
                this.mReSchedulePostAnimationCallback = true;
            } else {
                internalPostOnAnimation();
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void resetFlingPosition() {
            this.mLastFlingY = 0;
            this.mLastFlingX = 0;
        }

        /* JADX WARN: Removed duplicated region for block: B:120:0x0212  */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger, java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void run() {
            /*
                Method dump skipped, instructions count: 592
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RemixRecyclerView.ViewFlinger.run():void");
        }

        /* JADX WARN: Removed duplicated region for block: B:25:0x0061  */
        /* JADX WARN: Removed duplicated region for block: B:28:0x008c  */
        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void smoothScrollBy(int r9, int r10, int r11, android.view.animation.Interpolator r12) {
            /*
                r8 = this;
                androidx.recyclerview.widget.RemixRecyclerView r0 = androidx.recyclerview.widget.RemixRecyclerView.this
                int r0 = r0.getScrollState()
                if (r0 == 0) goto L9
                return
            L9:
                r0 = -2147483648(0xffffffff80000000, float:-0.0)
                r1 = 0
                if (r11 != r0) goto L11
                r8.computeScrollDuration(r9, r10, r1, r1)
            L11:
                if (r12 != 0) goto L15
                android.view.animation.Interpolator r12 = androidx.recyclerview.widget.RecyclerView.sQuinticInterpolator
            L15:
                miuix.overscroller.widget.OverScroller r11 = r8.mOverScroller
                int r11 = r11.getMode()
                r0 = 2
                if (r11 != r0) goto L34
                boolean r11 = r8.mInterimTarget
                if (r11 != 0) goto L34
                miuix.overscroller.widget.OverScroller r11 = r8.mOverScroller
                float r11 = r11.getCurrVelocityY()
                int r11 = (int) r11
                r8.mCurrentFlingVelocityY = r11
                miuix.overscroller.widget.OverScroller r11 = r8.mOverScroller
                float r11 = r11.getCurrVelocityX()
                int r11 = (int) r11
                r8.mCurrentFlingVelocityX = r11
            L34:
                androidx.recyclerview.widget.RemixRecyclerView r11 = androidx.recyclerview.widget.RemixRecyclerView.this
                androidx.recyclerview.widget.RecyclerView$LayoutManager r11 = r11.mLayout
                androidx.recyclerview.widget.RecyclerView$SmoothScroller r11 = r11.mSmoothScroller
                boolean r2 = r11 instanceof androidx.recyclerview.widget.LinearSmoothScroller
                if (r2 == 0) goto L5a
                r2 = 1067030938(0x3f99999a, float:1.2)
                r3 = r11
                androidx.recyclerview.widget.LinearSmoothScroller r3 = (androidx.recyclerview.widget.LinearSmoothScroller) r3
                int r3 = r3.mInterimTargetDx
                float r3 = (float) r3
                float r3 = r3 * r2
                androidx.recyclerview.widget.LinearSmoothScroller r11 = (androidx.recyclerview.widget.LinearSmoothScroller) r11
                int r11 = r11.mInterimTargetDy
                float r11 = (float) r11
                float r11 = r11 * r2
                float r2 = (float) r9
                int r2 = (r3 > r2 ? 1 : (r3 == r2 ? 0 : -1))
                if (r2 != 0) goto L5a
                float r2 = (float) r10
                int r11 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
                if (r11 != 0) goto L5a
                r11 = 1
                goto L5b
            L5a:
                r11 = r1
            L5b:
                r8.mInterimTarget = r11
                android.view.animation.Interpolator r11 = r8.mInterpolator
                if (r11 == r12) goto L70
                r8.mInterpolator = r12
                miuix.overscroller.widget.OverScroller r11 = new miuix.overscroller.widget.OverScroller
                androidx.recyclerview.widget.RemixRecyclerView r2 = androidx.recyclerview.widget.RemixRecyclerView.this
                android.content.Context r2 = r2.getContext()
                r11.<init>(r2, r12)
                r8.mOverScroller = r11
            L70:
                r8.mLastFlingY = r1
                r8.mLastFlingX = r1
                androidx.recyclerview.widget.RemixRecyclerView r11 = androidx.recyclerview.widget.RemixRecyclerView.this
                r11.setScrollState(r0)
                miuix.overscroller.widget.OverScroller r1 = r8.mOverScroller
                r2 = 0
                r3 = 0
                int r6 = r8.mCurrentFlingVelocityX
                int r7 = r8.mCurrentFlingVelocityY
                r4 = r9
                r5 = r10
                r1.startScrollByFling(r2, r3, r4, r5, r6, r7)
                int r9 = android.os.Build.VERSION.SDK_INT
                r10 = 23
                if (r9 >= r10) goto L91
                miuix.overscroller.widget.OverScroller r9 = r8.mOverScroller
                r9.computeScrollOffset()
            L91:
                r8.postOnAnimation()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.RemixRecyclerView.ViewFlinger.smoothScrollBy(int, int, int, android.view.animation.Interpolator):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ViewFlinger
        public void stop() {
            RemixRecyclerView.this.removeCallbacks(this);
            this.mOverScroller.abortAnimation();
        }
    }

    public RemixRecyclerView(Context context) {
        this(context, null);
    }

    public RemixRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.recyclerViewStyle);
    }

    public RemixRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScrollPointerId = -1;
        this.mSpringEnabled = true;
    }

    private void trackVelocity(MotionEvent motionEvent) {
        if (this.mVelocityMonitor == null) {
            this.mVelocityMonitor = new VelocityMonitor();
        }
        int actionMasked = motionEvent.getActionMasked();
        int actionIndex = motionEvent.getActionIndex();
        if (actionMasked == 0) {
            this.mVelocityMonitor.clear();
        } else if (actionMasked == 2) {
            int findPointerIndex = motionEvent.findPointerIndex(this.mScrollPointerId);
            if (findPointerIndex >= 0) {
                updateVelocity(motionEvent, findPointerIndex);
                return;
            }
            Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
            return;
        } else if (actionMasked != 5) {
            if (actionMasked == 6 && motionEvent.getPointerId(actionIndex) == this.mScrollPointerId) {
                this.mScrollPointerId = motionEvent.getPointerId(actionIndex == 0 ? 1 : 0);
                updateVelocity(motionEvent, actionIndex);
                return;
            }
            return;
        }
        this.mScrollPointerId = motionEvent.getPointerId(actionIndex);
        updateVelocity(motionEvent, actionIndex);
    }

    private void updateVelocity(MotionEvent motionEvent, int i) {
        if (Build.VERSION.SDK_INT >= 29) {
            this.mVelocityMonitor.update(motionEvent.getRawX(i), motionEvent.getRawY(i));
        } else {
            this.mVelocityMonitor.update(motionEvent.getRawX(), motionEvent.getRawY());
        }
    }

    public boolean getSpringEnabled() {
        return this.mSpringEnabled;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        trackVelocity(motionEvent);
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        trackVelocity(motionEvent);
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void setOverScrollMode(int i) {
        super.setOverScrollMode(i);
        if (i == 2) {
            this.mSpringEnabled = false;
        }
    }

    public void setSpringEnabled(boolean z) {
        this.mSpringEnabled = z;
    }
}
