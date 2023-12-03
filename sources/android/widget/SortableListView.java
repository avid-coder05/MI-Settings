package android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import com.miui.system.internal.R;

/* loaded from: classes.dex */
public class SortableListView extends MiuiListView {
    private int mDraggingFrom;
    private int mDraggingItemHeight;
    private int mDraggingItemWidth;
    private int mDraggingTo;
    private int mDraggingY;
    private boolean mInterceptTouchForSorting;
    private int mItemUpperBound;
    private int mOffsetYInDraggingItem;
    private OnOrderChangedListener mOnOrderChangedListener;
    private View.OnTouchListener mOnTouchListener;
    private int mScrollBound;
    private int mScrollLowerBound;
    private int mScrollUpperBound;
    private BitmapDrawable mSnapshot;
    private Drawable mSnapshotBackgroundForOverUpperBound;
    private Drawable mSnapshotShadow;
    private int mSnapshotShadowPaddingBottom;
    private int mSnapshotShadowPaddingTop;
    private int[] mTmpLocation;

    /* loaded from: classes.dex */
    public interface OnOrderChangedListener {
        void OnOrderChanged(int i, int i2);
    }

    public SortableListView(Context context) {
        this(context, null);
    }

    public SortableListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842868);
    }

    public SortableListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDraggingFrom = -1;
        this.mDraggingTo = -1;
        this.mItemUpperBound = -1;
        this.mTmpLocation = new int[2];
        Drawable drawable = context.getResources().getDrawable(R.drawable.sortable_list_dragging_item_shadow);
        this.mSnapshotShadow = drawable;
        drawable.setAlpha(153);
        Rect rect = new Rect();
        this.mSnapshotShadow.getPadding(rect);
        this.mSnapshotShadowPaddingTop = rect.top;
        this.mSnapshotShadowPaddingBottom = rect.bottom;
        this.mOnTouchListener = new View.OnTouchListener() { // from class: android.widget.SortableListView.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int hittenItemPosition;
                if (SortableListView.this.mOnOrderChangedListener != null && (motionEvent.getAction() & 255) == 0 && (hittenItemPosition = SortableListView.this.getHittenItemPosition(motionEvent)) >= 0) {
                    SortableListView.this.mDraggingFrom = hittenItemPosition;
                    SortableListView.this.mDraggingTo = hittenItemPosition;
                    SortableListView.this.mInterceptTouchForSorting = true;
                    SortableListView sortableListView = SortableListView.this;
                    View childAt = sortableListView.getChildAt(hittenItemPosition - sortableListView.getFirstVisiblePosition());
                    SortableListView.this.mDraggingItemWidth = childAt.getWidth();
                    SortableListView.this.mDraggingItemHeight = childAt.getHeight();
                    SortableListView sortableListView2 = SortableListView.this;
                    sortableListView2.getLocationOnScreen(sortableListView2.mTmpLocation);
                    SortableListView.this.mDraggingY = ((int) motionEvent.getRawY()) - SortableListView.this.mTmpLocation[1];
                    SortableListView sortableListView3 = SortableListView.this;
                    sortableListView3.mOffsetYInDraggingItem = sortableListView3.mDraggingY - childAt.getTop();
                    Bitmap createBitmap = Bitmap.createBitmap(SortableListView.this.mDraggingItemWidth, SortableListView.this.mDraggingItemHeight, Bitmap.Config.ARGB_8888);
                    childAt.draw(new Canvas(createBitmap));
                    SortableListView.this.mSnapshot = new BitmapDrawable(SortableListView.this.getResources(), createBitmap);
                    SortableListView.this.mSnapshot.setAlpha(153);
                    SortableListView.this.mSnapshot.setBounds(childAt.getLeft(), 0, childAt.getRight(), SortableListView.this.mDraggingItemHeight);
                    if (SortableListView.this.mSnapshotBackgroundForOverUpperBound != null) {
                        SortableListView.this.mSnapshotBackgroundForOverUpperBound.setAlpha(153);
                        SortableListView.this.mSnapshotBackgroundForOverUpperBound.setBounds(childAt.getLeft(), 0, childAt.getRight(), SortableListView.this.mDraggingItemHeight);
                    }
                    SortableListView.this.mSnapshotShadow.setBounds(childAt.getLeft(), -SortableListView.this.mSnapshotShadowPaddingTop, childAt.getRight(), SortableListView.this.mDraggingItemHeight + SortableListView.this.mSnapshotShadowPaddingBottom);
                    SortableListView sortableListView4 = SortableListView.this;
                    childAt.startAnimation(sortableListView4.createAnimation(sortableListView4.mDraggingItemWidth, SortableListView.this.mDraggingItemWidth, 0, 0));
                }
                return SortableListView.this.mInterceptTouchForSorting;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Animation createAnimation(int i, int i2, int i3, int i4) {
        TranslateAnimation translateAnimation = new TranslateAnimation(i, i2, i3, i4);
        translateAnimation.setDuration(200L);
        translateAnimation.setFillAfter(true);
        return translateAnimation;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getHittenItemPosition(MotionEvent motionEvent) {
        float rawX = motionEvent.getRawX();
        float rawY = motionEvent.getRawY();
        int firstVisiblePosition = getFirstVisiblePosition();
        for (int lastVisiblePosition = getLastVisiblePosition(); lastVisiblePosition >= firstVisiblePosition; lastVisiblePosition--) {
            View childAt = getChildAt(lastVisiblePosition - firstVisiblePosition);
            if (childAt != null) {
                childAt.getLocationOnScreen(this.mTmpLocation);
                int[] iArr = this.mTmpLocation;
                if (iArr[0] <= rawX && iArr[0] + childAt.getWidth() >= rawX) {
                    int[] iArr2 = this.mTmpLocation;
                    if (iArr2[1] <= rawY && iArr2[1] + childAt.getHeight() >= rawY) {
                        return lastVisiblePosition;
                    }
                }
            }
        }
        return -1;
    }

    private void setViewAnimation(View view, Animation animation) {
        if (view == null) {
            return;
        }
        if (animation != null) {
            view.startAnimation(animation);
        } else {
            view.clearAnimation();
        }
    }

    private void setViewAnimationByPisition(int i, Animation animation) {
        setViewAnimation(getChildAt(i - getFirstVisiblePosition()), animation);
    }

    private void updateDraggingToPisition(int i) {
        if (i == this.mDraggingTo || i < 0) {
            return;
        }
        Log.d("SortableListView", "sort item from " + this.mDraggingFrom + " To " + i);
        if (this.mDraggingFrom < Math.max(this.mDraggingTo, i)) {
            while (true) {
                int i2 = this.mDraggingTo;
                if (i2 <= i || i2 <= this.mDraggingFrom) {
                    break;
                }
                Log.d("SortableListView", "item " + this.mDraggingTo + " set move down reverse animation");
                int i3 = this.mDraggingTo;
                this.mDraggingTo = i3 + (-1);
                setViewAnimationByPisition(i3, createAnimation(0, 0, -this.mDraggingItemHeight, 0));
            }
        }
        if (this.mDraggingFrom > Math.min(this.mDraggingTo, i)) {
            while (true) {
                int i4 = this.mDraggingTo;
                if (i4 >= i || i4 >= this.mDraggingFrom) {
                    break;
                }
                Log.d("SortableListView", "item " + this.mDraggingTo + " set move up reverse animation");
                int i5 = this.mDraggingTo;
                this.mDraggingTo = i5 + 1;
                setViewAnimationByPisition(i5, createAnimation(0, 0, this.mDraggingItemHeight, 0));
            }
        }
        if (this.mDraggingFrom < Math.max(this.mDraggingTo, i)) {
            while (true) {
                int i6 = this.mDraggingTo;
                if (i6 >= i) {
                    break;
                }
                int i7 = i6 + 1;
                this.mDraggingTo = i7;
                setViewAnimationByPisition(i7, createAnimation(0, 0, 0, -this.mDraggingItemHeight));
                Log.d("SortableListView", "item " + this.mDraggingTo + " set move up animation");
            }
        }
        if (this.mDraggingFrom <= Math.min(this.mDraggingTo, i)) {
            return;
        }
        while (true) {
            int i8 = this.mDraggingTo;
            if (i8 <= i) {
                return;
            }
            int i9 = i8 - 1;
            this.mDraggingTo = i9;
            setViewAnimationByPisition(i9, createAnimation(0, 0, 0, this.mDraggingItemHeight));
            Log.d("SortableListView", "item " + this.mDraggingTo + " set move down animation");
        }
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.mDraggingFrom >= 0) {
            int i = this.mDraggingY - this.mOffsetYInDraggingItem;
            int headerViewsCount = getHeaderViewsCount();
            if (headerViewsCount < getFirstVisiblePosition() || headerViewsCount > getLastVisiblePosition()) {
                headerViewsCount = getFirstVisiblePosition();
            }
            int max = Math.max(i, getChildAt(headerViewsCount - getFirstVisiblePosition()).getTop());
            int count = (getCount() - 1) - getFooterViewsCount();
            if (count < getFirstVisiblePosition() || count > getLastVisiblePosition()) {
                count = getLastVisiblePosition();
            }
            canvas.translate(0.0f, Math.min(max, getChildAt(count - getFirstVisiblePosition()).getBottom() - this.mDraggingItemHeight));
            this.mSnapshotShadow.draw(canvas);
            this.mSnapshot.draw(canvas);
            Drawable drawable = this.mSnapshotBackgroundForOverUpperBound;
            if (drawable != null && this.mDraggingTo < this.mItemUpperBound) {
                drawable.draw(canvas);
            }
            canvas.translate(0.0f, -r0);
        }
    }

    public View.OnTouchListener getListenerForStartingSort() {
        return this.mOnTouchListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.MiuiListView
    public View obtainView(int i, boolean[] zArr) {
        Animation animation;
        View obtainView = super.obtainView(i, zArr);
        int i2 = this.mDraggingFrom;
        if (i2 == i) {
            int i3 = this.mDraggingItemWidth;
            animation = createAnimation(i3, i3, 0, 0);
            Log.d("SortableListView", "item " + i + " set move out animation");
        } else if (i2 < i && i <= this.mDraggingTo) {
            animation = createAnimation(0, 0, 0, -this.mDraggingItemHeight);
            Log.d("SortableListView", "item " + i + " set move up animation");
        } else if (i2 <= i || i < this.mDraggingTo) {
            animation = null;
        } else {
            animation = createAnimation(0, 0, 0, this.mDraggingItemHeight);
            Log.d("SortableListView", "item " + i + " set move down animation");
        }
        setViewAnimation(obtainView, animation);
        return obtainView;
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.mInterceptTouchForSorting) {
            requestDisallowInterceptTouchEvent(true);
            onTouchEvent(motionEvent);
            return true;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        int max = Math.max(1, (int) (i2 * 0.25f));
        this.mScrollBound = max;
        this.mScrollUpperBound = max;
        this.mScrollLowerBound = i2 - max;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x001a, code lost:
    
        if (r0 != 5) goto L45;
     */
    @Override // android.widget.AbsListView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            boolean r0 = r5.mInterceptTouchForSorting
            if (r0 != 0) goto L9
            boolean r5 = super.onTouchEvent(r6)
            return r5
        L9:
            int r0 = r6.getAction()
            r0 = r0 & 255(0xff, float:3.57E-43)
            r1 = 0
            r2 = 1
            if (r0 == r2) goto L7a
            r3 = 2
            if (r0 == r3) goto L1e
            r6 = 3
            if (r0 == r6) goto L7a
            r6 = 5
            if (r0 == r6) goto L7a
            goto La6
        L1e:
            float r0 = r6.getY()
            int r0 = (int) r0
            boolean r3 = r5.mInterceptTouchForSorting
            if (r3 != 0) goto L2d
            int r3 = r5.mDraggingY
            if (r0 != r3) goto L2d
            goto La6
        L2d:
            int r6 = r5.getHittenItemPosition(r6)
            int r3 = r5.getHeaderViewsCount()
            if (r6 < r3) goto L42
            int r3 = r5.getCount()
            int r4 = r5.getFooterViewsCount()
            int r3 = r3 - r4
            if (r6 <= r3) goto L44
        L42:
            int r6 = r5.mDraggingTo
        L44:
            r5.updateDraggingToPisition(r6)
            r5.mDraggingY = r0
            r5.invalidate()
            int r3 = r5.mScrollLowerBound
            if (r0 <= r3) goto L58
            int r3 = r3 - r0
            int r3 = r3 * 16
            int r0 = r5.mScrollBound
            int r1 = r3 / r0
            goto L63
        L58:
            int r3 = r5.mScrollUpperBound
            if (r0 >= r3) goto L63
            int r3 = r3 - r0
            int r3 = r3 * 16
            int r0 = r5.mScrollBound
            int r1 = r3 / r0
        L63:
            if (r1 == 0) goto La6
            int r0 = r5.getFirstVisiblePosition()
            int r0 = r6 - r0
            android.view.View r0 = r5.getChildAt(r0)
            if (r0 == 0) goto La6
            int r0 = r0.getTop()
            int r0 = r0 + r1
            r5.setSelectionFromTop(r6, r0)
            goto La6
        L7a:
            int r6 = r5.mDraggingFrom
            if (r6 < 0) goto L9c
            android.widget.SortableListView$OnOrderChangedListener r0 = r5.mOnOrderChangedListener
            if (r0 == 0) goto L98
            int r3 = r5.mDraggingTo
            if (r6 == r3) goto L98
            if (r3 < 0) goto L98
            int r3 = r5.getHeaderViewsCount()
            int r6 = r6 - r3
            int r3 = r5.mDraggingTo
            int r4 = r5.getHeaderViewsCount()
            int r3 = r3 - r4
            r0.OnOrderChanged(r6, r3)
            goto L9c
        L98:
            r0 = 0
            r5.setViewAnimationByPisition(r6, r0)
        L9c:
            r5.mInterceptTouchForSorting = r1
            r6 = -1
            r5.mDraggingFrom = r6
            r5.mDraggingTo = r6
            r5.invalidate()
        La6:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.SortableListView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setOnOrderChangedListener(OnOrderChangedListener onOrderChangedListener) {
        this.mOnOrderChangedListener = onOrderChangedListener;
    }
}
