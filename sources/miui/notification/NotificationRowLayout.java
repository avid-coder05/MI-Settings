package miui.notification;

import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import com.miui.system.internal.R;
import java.util.HashMap;
import miui.notification.SwipeHelper;

/* loaded from: classes3.dex */
public class NotificationRowLayout extends LinearLayout implements SwipeHelper.Callback {
    private static final int APPEAR_ANIM_LEN = 250;
    private static final boolean DEBUG = false;
    private static final int DISAPPEAR_ANIM_LEN = 250;
    private static final boolean SLOW_ANIMATIONS = false;
    private static final String TAG = "NotificationRowLayout";
    boolean mAnimateBounds;
    HashMap<View, ValueAnimator> mAppearingViews;
    private Context mContext;
    HashMap<View, ValueAnimator> mDisappearingViews;
    private LayoutTransition mRealLayoutTransition;
    boolean mRemoveViews;
    private SwipeHelper mSwipeHelper;
    Rect mTmpRect;

    public NotificationRowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationRowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAnimateBounds = true;
        this.mTmpRect = new Rect();
        this.mAppearingViews = new HashMap<>();
        this.mDisappearingViews = new HashMap<>();
        this.mRemoveViews = true;
        this.mContext = context;
        LayoutTransition layoutTransition = new LayoutTransition();
        this.mRealLayoutTransition = layoutTransition;
        layoutTransition.setAnimateParentHierarchy(true);
        setLayoutTransitionsEnabled(true);
        setOrientation(1);
        this.mSwipeHelper = new SwipeHelper(0, this, getResources().getDisplayMetrics().density, ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    private void logLayoutTransition() {
        StringBuilder sb = new StringBuilder();
        sb.append("layout ");
        sb.append(this.mRealLayoutTransition.isChangingLayout() ? "is " : "is not ");
        sb.append("in transition and animations ");
        sb.append(this.mRealLayoutTransition.isRunning() ? "are " : "are not ");
        sb.append("running.");
        Log.v(TAG, sb.toString());
    }

    @Override // miui.notification.SwipeHelper.Callback
    public boolean canChildBeDismissed(View view) {
        View findViewById = view.findViewById(R.id.veto);
        return (findViewById == null || findViewById.getVisibility() == 8) ? false : true;
    }

    public void dismissRowAnimated(View view) {
        dismissRowAnimated(view, 0);
    }

    public void dismissRowAnimated(View view, int i) {
        this.mSwipeHelper.dismissChild(view, i);
    }

    public View getChildAtPosition(float f, float f2) {
        int childCount = getChildCount();
        int i = 0;
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt.getVisibility() != 8) {
                i += childAt.getMeasuredHeight();
                if (f2 > 0.0f && f2 < i) {
                    return childAt;
                }
            }
        }
        return null;
    }

    @Override // miui.notification.SwipeHelper.Callback
    public View getChildAtPosition(MotionEvent motionEvent) {
        return getChildAtPosition(motionEvent.getX(), motionEvent.getY());
    }

    public View getChildAtRawPosition(float f, float f2) {
        getLocationOnScreen(new int[2]);
        return getChildAtPosition(f - r0[0], f2 - r0[1]);
    }

    @Override // miui.notification.SwipeHelper.Callback
    public View getChildContentView(View view) {
        return view;
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onBeginDrag(View view) {
        requestDisallowInterceptTouchEvent(true);
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onChildDismissed(View view) {
        View findViewById = view.findViewById(R.id.veto);
        if (findViewById == null || findViewById.getVisibility() == 8 || !this.mRemoveViews) {
            return;
        }
        findViewById.performClick();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mSwipeHelper.setDensityScale(getResources().getDisplayMetrics().density);
        this.mSwipeHelper.setPagingTouchSlop(ViewConfiguration.get(this.mContext).getScaledPagingTouchSlop());
    }

    @Override // miui.notification.SwipeHelper.Callback
    public void onDragCancelled(View view) {
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onInterceptTouchEvent(motionEvent) || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mSwipeHelper.onTouchEvent(motionEvent) || super.onTouchEvent(motionEvent);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            return;
        }
        this.mSwipeHelper.removeLongPressCallback();
    }

    public void setAnimateBounds(boolean z) {
        this.mAnimateBounds = z;
    }

    public void setLayoutTransitionsEnabled(boolean z) {
        if (z) {
            setLayoutTransition(this.mRealLayoutTransition);
            return;
        }
        if (this.mRealLayoutTransition.isRunning()) {
            this.mRealLayoutTransition.endChangingAnimations();
            this.mRealLayoutTransition.cancel();
        }
        setLayoutTransition(null);
    }

    public void setLongPressListener(View.OnLongClickListener onLongClickListener) {
        this.mSwipeHelper.setLongPressListener(onLongClickListener);
    }

    public void setViewRemoval(boolean z) {
        this.mRemoveViews = z;
    }
}
