package miui.notification;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

/* loaded from: classes3.dex */
public class SwipeHelper implements Gefingerpoken {
    static final float ALPHA_FADE_END = 0.5f;
    private static final boolean CONSTRAIN_SWIPE = true;
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_INVALIDATE = false;
    private static final boolean DISMISS_IF_SWIPED_FAR_ENOUGH = true;
    private static final boolean FADE_OUT_DURING_SWIPE = true;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final int SNAP_ANIM_LEN = 150;
    static final String TAG = "com.android.systemui.SwipeHelper";
    public static final int X = 0;
    public static final int Y = 1;
    private Callback mCallback;
    private boolean mCanCurrViewBeDimissed;
    private View mCurrAnimView;
    private View mCurrView;
    private float mDensityScale;
    private boolean mDragging;
    private float mInitialTouchPos;
    private View.OnLongClickListener mLongPressListener;
    private boolean mLongPressSent;
    private float mPagingTouchSlop;
    private int mSwipeDirection;
    private Runnable mWatchLongPress;
    private static LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    public static float ALPHA_FADE_START = 0.0f;
    private float SWIPE_ESCAPE_VELOCITY = 100.0f;
    private int DEFAULT_ESCAPE_ANIMATION_DURATION = 200;
    private int MAX_ESCAPE_ANIMATION_DURATION = 400;
    private int MAX_DISMISS_VELOCITY = 2000;
    private float mMinAlpha = 0.0f;
    private Handler mHandler = new Handler();
    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private long mLongPressTimeout = ViewConfiguration.getLongPressTimeout() * 1.5f;

    /* loaded from: classes3.dex */
    public interface Callback {
        boolean canChildBeDismissed(View view);

        View getChildAtPosition(MotionEvent motionEvent);

        View getChildContentView(View view);

        void onBeginDrag(View view);

        void onChildDismissed(View view);

        void onDragCancelled(View view);
    }

    public SwipeHelper(int i, Callback callback, float f, float f2) {
        this.mCallback = callback;
        this.mSwipeDirection = i;
        this.mDensityScale = f;
        this.mPagingTouchSlop = f2;
    }

    private ObjectAnimator createTranslationAnimation(View view, float f) {
        return ObjectAnimator.ofFloat(view, this.mSwipeDirection == 0 ? "translationX" : "translationY", f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float getAlphaForOffset(View view) {
        float size = getSize(view);
        float f = ALPHA_FADE_END * size;
        float translation = getTranslation(view);
        float f2 = ALPHA_FADE_START;
        float f3 = 1.0f;
        if (translation >= size * f2) {
            f3 = 1.0f - ((translation - (size * f2)) / f);
        } else if (translation < (1.0f - f2) * size) {
            f3 = 1.0f + (((size * f2) + translation) / f);
        }
        return Math.max(this.mMinAlpha, f3);
    }

    private float getPerpendicularVelocity(VelocityTracker velocityTracker) {
        return this.mSwipeDirection == 0 ? velocityTracker.getYVelocity() : velocityTracker.getXVelocity();
    }

    private float getPos(MotionEvent motionEvent) {
        return this.mSwipeDirection == 0 ? motionEvent.getX() : motionEvent.getY();
    }

    private float getSize(View view) {
        return this.mSwipeDirection == 0 ? view.getMeasuredWidth() : view.getMeasuredHeight();
    }

    private float getTranslation(View view) {
        return this.mSwipeDirection == 0 ? view.getTranslationX() : view.getTranslationY();
    }

    private float getVelocity(VelocityTracker velocityTracker) {
        return this.mSwipeDirection == 0 ? velocityTracker.getXVelocity() : velocityTracker.getYVelocity();
    }

    public static void invalidateGlobalRegion(View view) {
        invalidateGlobalRegion(view, new RectF(view.getLeft(), view.getTop(), view.getRight(), view.getBottom()));
    }

    public static void invalidateGlobalRegion(View view, RectF rectF) {
        while (view.getParent() != null && (view.getParent() instanceof View)) {
            view = (View) view.getParent();
            view.getMatrix().mapRect(rectF);
            view.invalidate((int) Math.floor(rectF.left), (int) Math.floor(rectF.top), (int) Math.ceil(rectF.right), (int) Math.ceil(rectF.bottom));
        }
    }

    private void setTranslation(View view, float f) {
        if (this.mSwipeDirection == 0) {
            view.setTranslationX(f);
        } else {
            view.setTranslationY(f);
        }
    }

    public void dismissChild(final View view, float f) {
        final View childContentView = this.mCallback.getChildContentView(view);
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(view);
        float size = (f < 0.0f || (f == 0.0f && getTranslation(childContentView) < 0.0f) || (f == 0.0f && getTranslation(childContentView) == 0.0f && this.mSwipeDirection == 1)) ? -getSize(childContentView) : getSize(childContentView);
        int min = f != 0.0f ? Math.min(this.MAX_ESCAPE_ANIMATION_DURATION, (int) ((Math.abs(size - getTranslation(childContentView)) * 1000.0f) / Math.abs(f))) : this.DEFAULT_ESCAPE_ANIMATION_DURATION;
        childContentView.setLayerType(2, null);
        ObjectAnimator createTranslationAnimation = createTranslationAnimation(childContentView, size);
        createTranslationAnimation.setInterpolator(sLinearInterpolator);
        createTranslationAnimation.setDuration(min);
        createTranslationAnimation.addListener(new AnimatorListenerAdapter() { // from class: miui.notification.SwipeHelper.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                SwipeHelper.this.mCallback.onChildDismissed(view);
                childContentView.setLayerType(0, null);
            }
        });
        createTranslationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miui.notification.SwipeHelper.3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (canChildBeDismissed) {
                    View view2 = childContentView;
                    view2.setAlpha(SwipeHelper.this.getAlphaForOffset(view2));
                }
                SwipeHelper.invalidateGlobalRegion(childContentView);
            }
        });
        createTranslationAnimation.start();
    }

    /* JADX WARN: Code restructure failed: missing block: B:9:0x000e, code lost:
    
        if (r0 != 3) goto L27;
     */
    @Override // miui.notification.Gefingerpoken
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r5) {
        /*
            r4 = this;
            int r0 = r5.getAction()
            r1 = 0
            if (r0 == 0) goto L57
            r2 = 1
            if (r0 == r2) goto L4a
            r3 = 2
            if (r0 == r3) goto L12
            r5 = 3
            if (r0 == r5) goto L4a
            goto La1
        L12:
            android.view.View r0 = r4.mCurrView
            if (r0 == 0) goto La1
            boolean r0 = r4.mLongPressSent
            if (r0 != 0) goto La1
            android.view.VelocityTracker r0 = r4.mVelocityTracker
            r0.addMovement(r5)
            float r0 = r4.getPos(r5)
            float r1 = r4.mInitialTouchPos
            float r0 = r0 - r1
            float r0 = java.lang.Math.abs(r0)
            float r1 = r4.mPagingTouchSlop
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            if (r0 <= 0) goto La1
            miui.notification.SwipeHelper$Callback r0 = r4.mCallback
            android.view.View r1 = r4.mCurrView
            r0.onBeginDrag(r1)
            r4.mDragging = r2
            float r5 = r4.getPos(r5)
            android.view.View r0 = r4.mCurrAnimView
            float r0 = r4.getTranslation(r0)
            float r5 = r5 - r0
            r4.mInitialTouchPos = r5
            r4.removeLongPressCallback()
            goto La1
        L4a:
            r4.mDragging = r1
            r5 = 0
            r4.mCurrView = r5
            r4.mCurrAnimView = r5
            r4.mLongPressSent = r1
            r4.removeLongPressCallback()
            goto La1
        L57:
            r4.mDragging = r1
            r4.mLongPressSent = r1
            miui.notification.SwipeHelper$Callback r0 = r4.mCallback
            android.view.View r0 = r0.getChildAtPosition(r5)
            r4.mCurrView = r0
            android.view.VelocityTracker r0 = r4.mVelocityTracker
            r0.clear()
            android.view.View r0 = r4.mCurrView
            if (r0 == 0) goto La1
            miui.notification.SwipeHelper$Callback r1 = r4.mCallback
            android.view.View r0 = r1.getChildContentView(r0)
            r4.mCurrAnimView = r0
            miui.notification.SwipeHelper$Callback r0 = r4.mCallback
            android.view.View r1 = r4.mCurrView
            boolean r0 = r0.canChildBeDismissed(r1)
            r4.mCanCurrViewBeDimissed = r0
            android.view.VelocityTracker r0 = r4.mVelocityTracker
            r0.addMovement(r5)
            float r5 = r4.getPos(r5)
            r4.mInitialTouchPos = r5
            android.view.View$OnLongClickListener r5 = r4.mLongPressListener
            if (r5 == 0) goto La1
            java.lang.Runnable r5 = r4.mWatchLongPress
            if (r5 != 0) goto L98
            miui.notification.SwipeHelper$1 r5 = new miui.notification.SwipeHelper$1
            r5.<init>()
            r4.mWatchLongPress = r5
        L98:
            android.os.Handler r5 = r4.mHandler
            java.lang.Runnable r0 = r4.mWatchLongPress
            long r1 = r4.mLongPressTimeout
            r5.postDelayed(r0, r1)
        La1:
            boolean r4 = r4.mDragging
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.notification.SwipeHelper.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0022, code lost:
    
        if (r0 != 4) goto L65;
     */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00fb  */
    /* JADX WARN: Removed duplicated region for block: B:64:0x0104  */
    @Override // miui.notification.Gefingerpoken
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            Method dump skipped, instructions count: 273
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.notification.SwipeHelper.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void removeLongPressCallback() {
        Runnable runnable = this.mWatchLongPress;
        if (runnable != null) {
            this.mHandler.removeCallbacks(runnable);
            this.mWatchLongPress = null;
        }
    }

    public void setDensityScale(float f) {
        this.mDensityScale = f;
    }

    public void setLongPressListener(View.OnLongClickListener onLongClickListener) {
        this.mLongPressListener = onLongClickListener;
    }

    public void setMinAlpha(float f) {
        this.mMinAlpha = f;
    }

    public void setPagingTouchSlop(float f) {
        this.mPagingTouchSlop = f;
    }

    public void snapChild(View view, float f) {
        final View childContentView = this.mCallback.getChildContentView(view);
        final boolean canChildBeDismissed = this.mCallback.canChildBeDismissed(childContentView);
        ObjectAnimator createTranslationAnimation = createTranslationAnimation(childContentView, 0.0f);
        createTranslationAnimation.setDuration((long) SNAP_ANIM_LEN);
        createTranslationAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: miui.notification.SwipeHelper.4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (canChildBeDismissed) {
                    View view2 = childContentView;
                    view2.setAlpha(SwipeHelper.this.getAlphaForOffset(view2));
                }
                SwipeHelper.invalidateGlobalRegion(childContentView);
            }
        });
        createTranslationAnimation.start();
    }
}
