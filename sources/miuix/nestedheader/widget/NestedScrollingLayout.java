package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import miuix.nestedheader.R$styleable;

/* loaded from: classes5.dex */
public class NestedScrollingLayout extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3 {
    private boolean isFirstsetScrollingRange;
    private boolean isHeaderOpen;
    private long mHeaderOpenTime;
    private boolean mHeaderViewVisible;
    private boolean mNestedFlingInConsumedProgress;
    private long mNestedFlingStartInConsumedTime;
    private boolean mNestedScrollAcceptedFling;
    private boolean mNestedScrollInConsumedProgress;
    private final NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;
    private final int[] mNestedScrollingV2ConsumedCompat;
    private List<OnNestedChangedListener> mOnNestedChangedListeners;
    private final int[] mParentOffsetInWindow;
    private final int[] mParentScrollConsumed;
    private int mScrollType;
    protected View mScrollableView;
    private int mScrollableViewId;
    private int mScrollingFrom;
    private int mScrollingProgress;
    private int mScrollingTo;
    private boolean mTriggerViewVisible;

    /* loaded from: classes5.dex */
    public interface OnNestedChangedListener {
        void onStartNestedScroll(int i);

        void onStopNestedScroll(int i);

        void onStopNestedScrollAccepted(int i);
    }

    public NestedScrollingLayout(Context context) {
        this(context, null);
    }

    public NestedScrollingLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedScrollingLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mNestedScrollingV2ConsumedCompat = new int[2];
        this.mParentOffsetInWindow = new int[2];
        this.mParentScrollConsumed = new int[2];
        this.isFirstsetScrollingRange = true;
        this.mHeaderOpenTime = 0L;
        this.mNestedFlingStartInConsumedTime = 0L;
        this.isHeaderOpen = false;
        this.mHeaderViewVisible = false;
        this.mTriggerViewVisible = false;
        this.mScrollType = 1;
        this.mOnNestedChangedListeners = new ArrayList();
        this.mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        this.mNestedScrollingChildHelper = miuix.core.view.NestedScrollingChildHelper.obtain(this);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.NestedScrollingLayout);
        this.mScrollableViewId = obtainStyledAttributes.getResourceId(R$styleable.NestedScrollingLayout_scrollableView, 16908298);
        obtainStyledAttributes.recycle();
        setNestedScrollingEnabled(true);
    }

    private void dispatchScrollingProgressUpdated() {
        onScrollingProgressUpdated(this.mScrollingProgress);
    }

    private void sendStartNestedScroll(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStartNestedScroll(i);
        }
    }

    private void sendStopNestedScroll(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStopNestedScroll(i);
        }
    }

    private void sendStopNestedScrollAccepted(int i) {
        Iterator<OnNestedChangedListener> it = this.mOnNestedChangedListeners.iterator();
        while (it.hasNext()) {
            it.next().onStopNestedScrollAccepted(i);
        }
    }

    public void addOnScrollListener(OnNestedChangedListener onNestedChangedListener) {
        this.mOnNestedChangedListeners.add(onNestedChangedListener);
    }

    public boolean dispatchNestedPreScroll(int i, int i2, int[] iArr, int[] iArr2, int i3) {
        return this.mNestedScrollingChildHelper.dispatchNestedPreScroll(i, i2, iArr, iArr2, i3);
    }

    public void dispatchNestedScroll(int i, int i2, int i3, int i4, int[] iArr, int i5, int[] iArr2) {
        this.mNestedScrollingChildHelper.dispatchNestedScroll(i, i2, i3, i4, iArr, i5, iArr2);
    }

    public boolean getAcceptedNestedFlingInConsumedProgress() {
        return this.mNestedFlingInConsumedProgress;
    }

    public int getScrollType() {
        return this.mScrollType;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getScrollingFrom() {
        return this.mScrollingFrom;
    }

    public int getScrollingProgress() {
        return this.mScrollingProgress;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getScrollingTo() {
        return this.mScrollingTo;
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public boolean isNestedScrollingEnabled() {
        return this.mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        View findViewById = findViewById(this.mScrollableViewId);
        this.mScrollableView = findViewById;
        if (findViewById == null) {
            throw new IllegalArgumentException("The scrollableView attribute is required and must refer to a valid child.");
        }
        findViewById.setNestedScrollingEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        dispatchScrollingProgressUpdated();
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
        onNestedPreScroll(view, i, i2, iArr, 0);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedPreScroll(View view, int i, int i2, int[] iArr, int i3) {
        if (i3 != 0) {
            if (!this.mNestedFlingInConsumedProgress) {
                this.mNestedFlingStartInConsumedTime = SystemClock.elapsedRealtime();
            }
            this.mNestedFlingInConsumedProgress = true;
        } else {
            this.mNestedScrollInConsumedProgress = true;
        }
        int[] iArr2 = this.mParentScrollConsumed;
        if (i2 > 0) {
            int max = Math.max(this.mScrollingFrom, Math.min(this.mScrollingTo, this.mScrollingProgress - i2));
            int i4 = this.mScrollingProgress - max;
            this.mScrollingProgress = max;
            dispatchScrollingProgressUpdated();
            iArr[0] = iArr[0] + 0;
            iArr[1] = iArr[1] + i4;
        }
        if (dispatchNestedPreScroll(i - iArr[0], i2 - iArr[1], iArr2, null, i3)) {
            iArr[0] = iArr[0] + iArr2[0];
            iArr[1] = iArr[1] + iArr2[1];
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
        onNestedScroll(view, i, i2, i3, i4, 0);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5) {
        onNestedScroll(view, i, i2, i3, i4, 0, this.mNestedScrollingV2ConsumedCompat);
    }

    @Override // androidx.core.view.NestedScrollingParent3
    public void onNestedScroll(View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
        boolean z;
        dispatchNestedScroll(i, i2, i3, i4, this.mParentOffsetInWindow, i5, iArr);
        int i6 = i4 - iArr[1];
        if (i4 >= 0 || i6 == 0) {
            return;
        }
        int i7 = this.mScrollingProgress;
        int i8 = i7 - i6;
        boolean z2 = i5 == 0;
        int i9 = this.mScrollingFrom;
        boolean z3 = i8 > i9;
        boolean z4 = this.mTriggerViewVisible;
        int max = Math.max(i9, Math.min(z2 || !z4 || (z4 && !this.mHeaderViewVisible && i5 == 1 && !z3) || (z4 && i5 == 1 && this.mHeaderViewVisible && ((!(z = this.isHeaderOpen) && i8 < 0) || (z && (this.mHeaderOpenTime > this.mNestedFlingStartInConsumedTime ? 1 : (this.mHeaderOpenTime == this.mNestedFlingStartInConsumedTime ? 0 : -1)) <= 0))) ? this.mScrollingTo : z4 && !this.mHeaderViewVisible && i5 == 1 && z3 && i7 == i9 ? i9 : 0, i8));
        int i10 = this.mScrollingProgress - max;
        this.mScrollingProgress = max;
        dispatchScrollingProgressUpdated();
        iArr[0] = iArr[0] + 0;
        iArr[1] = iArr[1] + i10;
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public void onNestedScrollAccepted(View view, View view2, int i) {
        this.mNestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
        startNestedScroll(i & 2);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onNestedScrollAccepted(View view, View view2, int i, int i2) {
        onNestedScrollAccepted(view, view2, i);
        if (i2 != 0) {
            this.mNestedScrollAcceptedFling = true;
        } else {
            this.mNestedScrollAcceptedFling = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onScrollingProgressUpdated(int i) {
    }

    @Override // android.view.ViewGroup, android.view.ViewParent, androidx.core.view.NestedScrollingParent
    public boolean onStartNestedScroll(View view, View view2, int i) {
        boolean z = (i & 2) != 0;
        if (this.mNestedScrollingChildHelper.startNestedScroll(i)) {
            return true;
        }
        return isEnabled() && z;
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public boolean onStartNestedScroll(View view, View view2, int i, int i2) {
        sendStartNestedScroll(i2);
        return this.mNestedScrollingChildHelper.startNestedScroll(i, i2) || onStartNestedScroll(view, view, i);
    }

    @Override // androidx.core.view.NestedScrollingParent2
    public void onStopNestedScroll(View view, int i) {
        this.mNestedScrollingParentHelper.onStopNestedScroll(view, i);
        sendStopNestedScroll(i);
        stopNestedScroll(i);
        if (this.mNestedScrollInConsumedProgress) {
            this.mNestedScrollInConsumedProgress = false;
            if (this.mNestedFlingInConsumedProgress || this.mNestedScrollAcceptedFling) {
                return;
            }
            sendStopNestedScrollAccepted(i);
        } else if (!this.mNestedFlingInConsumedProgress) {
            sendStopNestedScrollAccepted(i);
        } else {
            this.mNestedFlingInConsumedProgress = false;
            sendStopNestedScrollAccepted(i);
        }
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void setNestedScrollingEnabled(boolean z) {
        this.mNestedScrollingChildHelper.setNestedScrollingEnabled(z);
    }

    public void setScrollType(int i) {
        this.mScrollType = i;
    }

    public void setScrollingRange(int i, int i2, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (i > i2) {
            Log.w("NestedScrollingLayout", "wrong scrolling range: [%d, %d], making from=to");
            i = i2;
        }
        this.mScrollingFrom = i;
        this.mScrollingTo = i2;
        this.mHeaderViewVisible = z;
        this.mTriggerViewVisible = z2;
        if (this.mScrollingProgress < i) {
            this.mScrollingProgress = i;
        }
        if (this.mScrollingProgress > i2) {
            this.mScrollingProgress = i2;
        }
        if (((z3 && this.isFirstsetScrollingRange) || z4 || z5) && z) {
            this.mScrollingProgress = 0;
            this.isFirstsetScrollingRange = false;
        } else if ((z3 && this.isFirstsetScrollingRange) || z4) {
            this.mScrollingProgress = i;
            this.isFirstsetScrollingRange = false;
        }
        dispatchScrollingProgressUpdated();
    }

    @Override // android.view.View
    public boolean startNestedScroll(int i) {
        return this.mNestedScrollingChildHelper.startNestedScroll(i);
    }

    @Override // android.view.View, androidx.core.view.NestedScrollingChild
    public void stopNestedScroll() {
        this.mNestedScrollingChildHelper.stopNestedScroll();
    }

    public void stopNestedScroll(int i) {
        this.mNestedScrollingChildHelper.stopNestedScroll(i);
    }

    public void updateHeaderOpen(boolean z) {
        if (!this.isHeaderOpen && z) {
            this.mHeaderOpenTime = SystemClock.elapsedRealtime();
        }
        this.isHeaderOpen = z;
    }

    public void updateScrollingProgress(int i) {
        this.mScrollingProgress = i;
    }
}
