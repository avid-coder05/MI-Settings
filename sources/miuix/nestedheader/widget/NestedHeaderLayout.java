package miuix.nestedheader.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import com.miui.blur.sdk.drawable.BlurDrawable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.listener.TransitionListener;
import miuix.animation.listener.UpdateInfo;
import miuix.nestedheader.R$dimen;
import miuix.nestedheader.R$id;
import miuix.nestedheader.R$styleable;
import miuix.nestedheader.widget.NestedScrollingLayout;

/* loaded from: classes5.dex */
public class NestedHeaderLayout extends NestedScrollingLayout {
    private boolean isTouch;
    private boolean mAcceptHeaderRootViewAlpha;
    private boolean mAcceptTriggerRootViewAlpha;
    private boolean mAutoAnim;
    private int mHeaderBottomMargin;
    private int mHeaderContentBottomMargin;
    private int mHeaderContentId;
    private float mHeaderContentMinHeight;
    private View mHeaderContentView;
    private int mHeaderInitTop;
    private int mHeaderMeasuredHeight;
    private int mHeaderTopmMargin;
    private View mHeaderView;
    private int mHeaderViewId;
    private int mLastScrollingProgress;
    private NestedHeaderChangedListener mNestedHeaderChangedListener;
    private NestedScrollingLayout.OnNestedChangedListener mOnNestedChangedListener;
    private float mRangeOffset;
    private int mTriggerBottomMargin;
    private int mTriggerContentBottomMargin;
    private int mTriggerContentId;
    private float mTriggerContentMinHeight;
    private View mTriggerContentView;
    private int mTriggerMeasuredHeight;
    private int mTriggerTopmMargin;
    private View mTriggerView;
    private int mTriggerViewId;
    private String mValueTag;

    /* loaded from: classes5.dex */
    public interface NestedHeaderChangedListener {
        void onHeaderClosed(View view);

        void onHeaderOpened(View view);

        void onTriggerClosed(View view);

        void onTriggerOpened(View view);
    }

    public NestedHeaderLayout(Context context) {
        this(context, null);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NestedHeaderLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHeaderBottomMargin = 0;
        this.mHeaderTopmMargin = 0;
        this.mTriggerBottomMargin = 0;
        this.mTriggerTopmMargin = 0;
        this.mHeaderInitTop = 0;
        this.mHeaderContentBottomMargin = 0;
        this.mTriggerContentBottomMargin = 0;
        this.mHeaderMeasuredHeight = 0;
        this.mTriggerMeasuredHeight = 0;
        this.mLastScrollingProgress = 0;
        this.isTouch = false;
        this.mAutoAnim = true;
        this.mAcceptTriggerRootViewAlpha = false;
        this.mAcceptHeaderRootViewAlpha = false;
        this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
        this.mOnNestedChangedListener = new NestedScrollingLayout.OnNestedChangedListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.1
            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStartNestedScroll(int i2) {
                if (i2 == 0) {
                    NestedHeaderLayout.this.updateTouch(true);
                } else {
                    NestedHeaderLayout.this.updateTag();
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScroll(int i2) {
                if (i2 == 0) {
                    NestedHeaderLayout.this.updateTouch(false);
                }
            }

            @Override // miuix.nestedheader.widget.NestedScrollingLayout.OnNestedChangedListener
            public void onStopNestedScrollAccepted(int i2) {
                if (NestedHeaderLayout.this.mAutoAnim) {
                    NestedHeaderLayout.this.updateAdsorption();
                }
            }
        };
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.NestedHeaderLayout);
        this.mHeaderViewId = obtainStyledAttributes.getResourceId(R$styleable.NestedHeaderLayout_headerView, R$id.header_view);
        this.mTriggerViewId = obtainStyledAttributes.getResourceId(R$styleable.NestedHeaderLayout_triggerView, R$id.trigger_view);
        this.mHeaderContentId = obtainStyledAttributes.getResourceId(R$styleable.NestedHeaderLayout_headerContentId, R$id.header_content_view);
        this.mTriggerContentId = obtainStyledAttributes.getResourceId(R$styleable.NestedHeaderLayout_triggerContentId, R$id.trigger_content_view);
        int i2 = R$styleable.NestedHeaderLayout_headerContentMinHeight;
        Resources resources = context.getResources();
        int i3 = R$dimen.miuix_nested_header_layout_content_min_height;
        this.mHeaderContentMinHeight = obtainStyledAttributes.getDimension(i2, resources.getDimension(i3));
        this.mTriggerContentMinHeight = obtainStyledAttributes.getDimension(R$styleable.NestedHeaderLayout_triggerContentMinHeight, context.getResources().getDimension(i3));
        this.mRangeOffset = obtainStyledAttributes.getDimension(R$styleable.NestedHeaderLayout_rangeOffset, 0.0f);
        obtainStyledAttributes.recycle();
        addOnScrollListener(this.mOnNestedChangedListener);
    }

    private void applyContentAlpha(List<View> list, float f) {
        if (list == null) {
            return;
        }
        float max = Math.max(0.0f, Math.min(1.0f, f));
        for (View view : list) {
            if (!(view.getBackground() instanceof BlurDrawable)) {
                view.setAlpha(max);
            }
        }
    }

    private void autoAdsorption(int i) {
        final String l = Long.toString(SystemClock.elapsedRealtime());
        this.mValueTag = l;
        Folme.useValue(new Object[0]).setTo(l, Integer.valueOf(getScrollingProgress())).to(l, Integer.valueOf(i), new AnimConfig().addListeners(new TransitionListener() { // from class: miuix.nestedheader.widget.NestedHeaderLayout.2
            @Override // miuix.animation.listener.TransitionListener
            public void onUpdate(Object obj, Collection<UpdateInfo> collection) {
                UpdateInfo findByName = UpdateInfo.findByName(collection, l);
                if (findByName == null || !NestedHeaderLayout.this.isScrolling(l)) {
                    return;
                }
                NestedHeaderLayout.this.syncScrollingProgress(findByName.getIntValue());
            }
        }));
    }

    private void checkSendHeaderChangeListener(int i, int i2, boolean z) {
        if (this.mNestedHeaderChangedListener == null) {
            return;
        }
        if (z) {
            if (i2 == 0 && getHeaderViewVisible()) {
                this.mNestedHeaderChangedListener.onHeaderOpened(this.mHeaderView);
            } else if (i2 == getScrollingTo() && getTriggerViewVisible()) {
                this.mNestedHeaderChangedListener.onTriggerOpened(this.mTriggerView);
            }
            if (i >= 0 || i2 <= 0 || !getHeaderViewVisible()) {
                return;
            }
            this.mNestedHeaderChangedListener.onHeaderOpened(this.mHeaderView);
            return;
        }
        if (i2 == 0 && getTriggerViewVisible()) {
            this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
        } else if (i2 == getScrollingFrom() && getHeaderViewVisible()) {
            this.mNestedHeaderChangedListener.onHeaderClosed(this.mHeaderView);
        } else if (i2 == getScrollingFrom() && !getHeaderViewVisible()) {
            this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
        }
        int scrollingFrom = getHeaderViewVisible() ? 0 : getScrollingFrom();
        if (i <= scrollingFrom || i2 >= scrollingFrom || !getTriggerViewVisible()) {
            return;
        }
        this.mNestedHeaderChangedListener.onTriggerClosed(this.mTriggerView);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isScrolling(String str) {
        return (this.isTouch || !this.mValueTag.equals(str) || getAcceptedNestedFlingInConsumedProgress()) ? false : true;
    }

    private List<View> makeContentViewList(View view, boolean z) {
        if (view == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        if (!z) {
            arrayList.add(view);
            return arrayList;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                arrayList.add(viewGroup.getChildAt(i));
            }
        } else {
            arrayList.add(view);
        }
        return arrayList;
    }

    private List<View> makeHeaderContentViewList(View view) {
        return makeContentViewList(view, this.mHeaderContentId == R$id.header_content_view || this.mHeaderContentView != null);
    }

    private List<View> makeTriggerContentViewList(View view) {
        return makeContentViewList(view, this.mTriggerContentId == R$id.trigger_content_view || this.mTriggerContentView != null);
    }

    private void relayoutContent(View view, View view2, int i, int i2, boolean z) {
        view.layout(view.getLeft(), i, view.getRight(), Math.max(i, view.getMeasuredHeight() + i + i2));
        if (view != view2) {
            int max = Math.max(view2.getTop(), 0);
            int top = view2.getTop();
            int measuredHeight = view2.getMeasuredHeight() + max;
            if (z) {
                i2 /= 2;
            }
            view2.layout(view2.getLeft(), max, view2.getRight(), Math.max(top, measuredHeight + i2));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncScrollingProgress(int i) {
        updateScrollingProgress(i);
        onScrollingProgressUpdated(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAdsorption() {
        if (getScrollingProgress() == 0 || getScrollingProgress() >= getScrollingTo() || getScrollingProgress() <= getScrollingFrom()) {
            return;
        }
        int i = 0;
        if (getScrollingProgress() > getScrollingFrom() && getScrollingProgress() < getScrollingFrom() * 0.5f) {
            i = getScrollingFrom();
        } else if ((getScrollingProgress() < getScrollingFrom() * 0.5f || getScrollingProgress() >= 0) && ((getScrollingProgress() <= 0 || getScrollingProgress() >= getScrollingTo() * 0.5f) && getScrollingProgress() >= getScrollingTo() * 0.5f && getScrollingProgress() < getScrollingTo())) {
            i = getScrollingTo();
        }
        autoAdsorption(i);
    }

    private void updateScrollingRange(boolean z, boolean z2, boolean z3) {
        int i;
        boolean z4;
        int i2;
        int i3;
        boolean z5;
        View view = this.mHeaderView;
        if (view == null || view.getVisibility() == 8) {
            i = 0;
            z4 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.mHeaderView.getLayoutParams();
            this.mHeaderBottomMargin = marginLayoutParams.bottomMargin;
            this.mHeaderTopmMargin = marginLayoutParams.topMargin;
            this.mHeaderMeasuredHeight = this.mHeaderView.getMeasuredHeight();
            View view2 = this.mHeaderContentView;
            if (view2 != null) {
                this.mHeaderContentBottomMargin = ((ViewGroup.MarginLayoutParams) view2.getLayoutParams()).bottomMargin;
            }
            i = ((int) ((((-this.mHeaderMeasuredHeight) + this.mRangeOffset) - this.mHeaderTopmMargin) - this.mHeaderBottomMargin)) + 0;
            z4 = true;
        }
        View view3 = this.mTriggerView;
        if (view3 == null || view3.getVisibility() == 8) {
            i2 = i;
            i3 = 0;
            z5 = false;
        } else {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.mTriggerView.getLayoutParams();
            this.mTriggerBottomMargin = marginLayoutParams2.bottomMargin;
            this.mTriggerTopmMargin = marginLayoutParams2.topMargin;
            this.mTriggerMeasuredHeight = this.mTriggerView.getMeasuredHeight();
            View view4 = this.mTriggerContentView;
            if (view4 != null) {
                this.mTriggerContentBottomMargin = ((ViewGroup.MarginLayoutParams) view4.getLayoutParams()).bottomMargin;
            }
            int i4 = this.mTriggerMeasuredHeight + this.mTriggerTopmMargin + this.mTriggerBottomMargin + 0;
            if (z4) {
                i2 = i;
                z5 = true;
                i3 = i4;
            } else {
                i2 = -i4;
                z5 = true;
                i3 = 0;
            }
        }
        setScrollingRange(i2, i3, z4, z5, z, z2, z3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTag() {
        this.mValueTag = Long.toString(SystemClock.elapsedRealtime());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTouch(boolean z) {
        this.isTouch = z;
        if (z) {
            updateTag();
        }
    }

    public boolean getHeaderViewVisible() {
        View view = this.mHeaderView;
        return view != null && view.getVisibility() == 0;
    }

    public boolean getTriggerViewVisible() {
        View view = this.mTriggerView;
        return view != null && view.getVisibility() == 0;
    }

    public boolean isHeaderOpen() {
        return getHeaderViewVisible() && getScrollingProgress() >= 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.nestedheader.widget.NestedScrollingLayout, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mHeaderView = findViewById(this.mHeaderViewId);
        View findViewById = findViewById(this.mTriggerViewId);
        this.mTriggerView = findViewById;
        View view = this.mHeaderView;
        if (view == null && findViewById == null) {
            throw new IllegalArgumentException("The headerView or triggerView attribute is required and must refer to a valid child.");
        }
        if (view != null) {
            View findViewById2 = view.findViewById(this.mHeaderContentId);
            this.mHeaderContentView = findViewById2;
            if (findViewById2 == null) {
                this.mHeaderContentView = this.mHeaderView.findViewById(16908318);
            }
        }
        View view2 = this.mTriggerView;
        if (view2 != null) {
            View findViewById3 = view2.findViewById(this.mTriggerContentId);
            this.mTriggerContentView = findViewById3;
            if (findViewById3 == null) {
                this.mTriggerContentView = this.mTriggerView.findViewById(16908318);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.nestedheader.widget.NestedScrollingLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateScrollingRange(true, false, false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.nestedheader.widget.NestedScrollingLayout
    public void onScrollingProgressUpdated(int i) {
        int i2;
        int i3;
        float f;
        float f2;
        int i4;
        int i5;
        float f3;
        float f4;
        super.onScrollingProgressUpdated(i);
        View view = this.mTriggerView;
        if (view == null || view.getVisibility() == 8) {
            i2 = i;
            i3 = 0;
        } else {
            i2 = i - Math.max(0, Math.min(getScrollingTo(), i));
            int max = Math.max(getScrollingFrom(), Math.min(getScrollingTo(), i));
            int i6 = this.mTriggerTopmMargin;
            View view2 = this.mHeaderView;
            if (view2 == null || view2.getVisibility() == 8) {
                int i7 = this.mTriggerTopmMargin + this.mTriggerBottomMargin + this.mTriggerMeasuredHeight;
                i4 = max + i7;
                i5 = i6;
                i3 = i7;
            } else {
                i4 = max;
                i5 = this.mHeaderTopmMargin + this.mHeaderMeasuredHeight + this.mHeaderBottomMargin + this.mTriggerTopmMargin;
                i3 = 0;
            }
            View view3 = this.mTriggerContentView;
            if (view3 == null) {
                view3 = this.mTriggerView;
            }
            View view4 = view3;
            relayoutContent(this.mTriggerView, view4, i5, ((i4 - this.mTriggerBottomMargin) - this.mTriggerTopmMargin) - this.mTriggerMeasuredHeight, false);
            if (this.mTriggerContentView == null) {
                f3 = i4 - this.mTriggerBottomMargin;
                f4 = this.mTriggerContentMinHeight;
            } else {
                f3 = i4 - this.mTriggerContentBottomMargin;
                f4 = this.mTriggerContentMinHeight;
            }
            float f5 = f3 / f4;
            float max2 = Math.max(0.0f, Math.min(1.0f, f5));
            if (this.mAcceptTriggerRootViewAlpha) {
                this.mTriggerView.setAlpha(max2);
            } else {
                View view5 = this.mTriggerView;
                if ((view5 instanceof ViewGroup) && ((ViewGroup) view5).getChildCount() > 0) {
                    for (int i8 = 0; i8 < ((ViewGroup) this.mTriggerView).getChildCount(); i8++) {
                        View childAt = ((ViewGroup) this.mTriggerView).getChildAt(i8);
                        if (!(childAt.getBackground() instanceof BlurDrawable)) {
                            childAt.setAlpha(max2);
                        }
                    }
                }
            }
            applyContentAlpha(makeTriggerContentViewList(view4), f5 - 1.0f);
        }
        View view6 = this.mHeaderView;
        if (view6 != null && view6.getVisibility() != 8) {
            int i9 = this.mHeaderInitTop + this.mHeaderTopmMargin;
            View view7 = this.mHeaderContentView;
            if (view7 == null) {
                view7 = this.mHeaderView;
            }
            View view8 = view7;
            relayoutContent(this.mHeaderView, view8, i9, i2, false);
            if (this.mHeaderContentView == null) {
                f = i2 - this.mHeaderBottomMargin;
                f2 = this.mHeaderContentMinHeight;
            } else {
                f = i2 - this.mHeaderContentBottomMargin;
                f2 = this.mHeaderContentMinHeight;
            }
            float f6 = (f + f2) / f2;
            float max3 = Math.max(0.0f, Math.min(1.0f, f6 + 1.0f));
            if (this.mAcceptHeaderRootViewAlpha) {
                this.mHeaderView.setAlpha(max3);
            } else {
                View view9 = this.mHeaderView;
                if ((view9 instanceof ViewGroup) && ((ViewGroup) view9).getChildCount() > 0) {
                    for (int i10 = 0; i10 < ((ViewGroup) this.mHeaderView).getChildCount(); i10++) {
                        View childAt2 = ((ViewGroup) this.mHeaderView).getChildAt(i10);
                        if (!(childAt2.getBackground() instanceof BlurDrawable)) {
                            childAt2.setAlpha(max3);
                        }
                    }
                }
            }
            applyContentAlpha(makeHeaderContentViewList(view8), f6);
            i3 = this.mHeaderMeasuredHeight + this.mHeaderTopmMargin + this.mHeaderBottomMargin;
        }
        View view10 = this.mScrollableView;
        view10.offsetTopAndBottom((i + i3) - view10.getTop());
        int i11 = this.mLastScrollingProgress;
        if (i - i11 > 0) {
            checkSendHeaderChangeListener(i11, i, true);
        } else if (i - i11 < 0) {
            checkSendHeaderChangeListener(i11, i, false);
        }
        this.mLastScrollingProgress = i;
        updateHeaderOpen(isHeaderOpen());
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        View view = this.mHeaderView;
        if (view != null) {
            this.mHeaderInitTop = view.getTop();
        }
    }

    public void setAcceptTriggerRootViewAlpha(boolean z) {
        this.mAcceptTriggerRootViewAlpha = z;
    }

    public void setAutoAllClose(boolean z) {
        if (!z || getScrollingProgress() <= getScrollingFrom()) {
            syncScrollingProgress(getScrollingFrom());
        } else {
            autoAdsorption(getScrollingFrom());
        }
    }

    public void setAutoAllOpen(boolean z) {
        if (!z || getScrollingProgress() >= getScrollingTo()) {
            syncScrollingProgress(getScrollingTo());
        } else {
            autoAdsorption(getScrollingTo());
        }
    }

    public void setAutoAnim(boolean z) {
        this.mAutoAnim = z;
    }

    public void setAutoHeaderClose(boolean z) {
        if (!getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) {
            return;
        }
        if (z) {
            autoAdsorption(getScrollingFrom());
        } else if (getHeaderViewVisible()) {
            syncScrollingProgress(getScrollingFrom());
        }
    }

    public void setAutoHeaderOpen(boolean z) {
        if (!getHeaderViewVisible() || getScrollingProgress() >= 0) {
            return;
        }
        if (z) {
            autoAdsorption(0);
        } else {
            syncScrollingProgress(0);
        }
    }

    public void setAutoTriggerClose(boolean z) {
        int scrollingFrom = (getTriggerViewVisible() && getHeaderViewVisible() && getScrollingProgress() > 0) ? 0 : (!getTriggerViewVisible() || getHeaderViewVisible() || getScrollingProgress() <= getScrollingFrom()) ? -1 : getScrollingFrom();
        if (scrollingFrom != -1 && z) {
            autoAdsorption(scrollingFrom);
        } else if (scrollingFrom != -1) {
            syncScrollingProgress(scrollingFrom);
        }
    }

    public void setAutoTriggerOpen(boolean z) {
        if (!getTriggerViewVisible() || getScrollingProgress() >= getScrollingTo()) {
            return;
        }
        if (z) {
            autoAdsorption(getScrollingTo());
        } else {
            syncScrollingProgress(getScrollingTo());
        }
    }

    public void setBlurBackgroupAcceptAlpha(boolean z) {
        this.mAcceptHeaderRootViewAlpha = z;
    }

    public void setHeaderViewVisible(boolean z) {
        View view = this.mHeaderView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, false, z);
        }
    }

    public void setNestedHeaderChangedListener(NestedHeaderChangedListener nestedHeaderChangedListener) {
        this.mNestedHeaderChangedListener = nestedHeaderChangedListener;
    }

    public void setTriggerViewVisible(boolean z) {
        View view = this.mTriggerView;
        if (view != null) {
            view.setVisibility(z ? 0 : 8);
            updateScrollingRange(false, z, false);
        }
    }
}
