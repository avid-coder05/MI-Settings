package androidx.slice.widget;

import android.content.Context;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.widget.SliceView;
import androidx.slice.widget.SliceViewPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/* loaded from: classes.dex */
public class TemplateView extends SliceChildView implements SliceViewPolicy.PolicyChangeListener {
    private SliceAdapter mAdapter;
    private List<SliceContent> mDisplayedItems;
    private int mDisplayedItemsHeight;
    private final View mForeground;
    private int mHiddenItemCount;
    private ListContent mListContent;
    private int[] mLoc;
    private SliceView mParent;
    private final RecyclerView mRecyclerView;

    public TemplateView(Context context) {
        super(context);
        this.mDisplayedItems = new ArrayList();
        this.mDisplayedItemsHeight = 0;
        this.mLoc = new int[2];
        RecyclerView recyclerView = new RecyclerView(getContext());
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setAdapter(new SliceAdapter(context));
        SliceAdapter sliceAdapter = new SliceAdapter(context);
        this.mAdapter = sliceAdapter;
        recyclerView.setAdapter(sliceAdapter);
        addView(recyclerView);
        View view = new View(getContext());
        this.mForeground = view;
        view.setBackground(SliceViewUtil.getDrawable(getContext(), 16843534));
        addView(view);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        view.setLayoutParams(layoutParams);
    }

    private void applyRowStyle(RowStyle rowStyle) {
        if (rowStyle.getDisableRecyclerViewItemAnimator()) {
            this.mRecyclerView.setItemAnimator(null);
        }
    }

    private void updateDisplayedItems(int height) {
        ListContent listContent = this.mListContent;
        if (listContent == null || !listContent.isValid()) {
            resetView();
            return;
        }
        DisplayedListItems rowItems = this.mListContent.getRowItems(height, this.mSliceStyle, this.mViewPolicy);
        this.mDisplayedItems = rowItems.getDisplayedItems();
        this.mHiddenItemCount = rowItems.getHiddenItemCount();
        this.mDisplayedItemsHeight = ListContent.getListHeight(this.mDisplayedItems, this.mSliceStyle, this.mViewPolicy);
        this.mAdapter.setSliceItems(this.mDisplayedItems, this.mTintColor, this.mViewPolicy.getMode());
        updateOverscroll();
    }

    private void updateOverscroll() {
        this.mRecyclerView.setOverScrollMode((this.mViewPolicy.isScrollable() && (this.mDisplayedItemsHeight > getMeasuredHeight())) ? 1 : 2);
    }

    @Override // androidx.slice.widget.SliceChildView
    public int getHiddenItemCount() {
        return this.mHiddenItemCount;
    }

    @Override // androidx.slice.widget.SliceChildView
    public Set<SliceItem> getLoadingActions() {
        return this.mAdapter.getLoadingActions();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SliceView sliceView = (SliceView) getParent();
        this.mParent = sliceView;
        this.mAdapter.setParents(sliceView, this);
    }

    public void onForegroundActivated(MotionEvent event) {
        SliceView sliceView = this.mParent;
        if (sliceView != null && !sliceView.isSliceViewClickable()) {
            this.mForeground.setPressed(false);
            return;
        }
        if (Build.VERSION.SDK_INT >= 21) {
            this.mForeground.getLocationOnScreen(this.mLoc);
            this.mForeground.getBackground().setHotspot((int) (event.getRawX() - this.mLoc[0]), (int) (event.getRawY() - this.mLoc[1]));
        }
        int actionMasked = event.getActionMasked();
        if (actionMasked == 0) {
            this.mForeground.setPressed(true);
        } else if (actionMasked == 3 || actionMasked == 1 || actionMasked == 2) {
            this.mForeground.setPressed(false);
        }
    }

    @Override // androidx.slice.widget.SliceViewPolicy.PolicyChangeListener
    public void onMaxHeightChanged(int newNewHeight) {
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            updateDisplayedItems(listContent.getHeight(this.mSliceStyle, this.mViewPolicy));
        }
    }

    @Override // androidx.slice.widget.SliceViewPolicy.PolicyChangeListener
    public void onMaxSmallChanged(int newMaxSmallHeight) {
        SliceAdapter sliceAdapter = this.mAdapter;
        if (sliceAdapter != null) {
            sliceAdapter.notifyHeaderChanged();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = View.MeasureSpec.getSize(heightMeasureSpec);
        if (!this.mViewPolicy.isScrollable() && this.mDisplayedItems.size() > 0 && this.mDisplayedItemsHeight != size) {
            updateDisplayedItems(size);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // androidx.slice.widget.SliceViewPolicy.PolicyChangeListener
    public void onModeChanged(int newMode) {
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            updateDisplayedItems(listContent.getHeight(this.mSliceStyle, this.mViewPolicy));
        }
    }

    @Override // androidx.slice.widget.SliceViewPolicy.PolicyChangeListener
    public void onScrollingChanged(boolean newScrolling) {
        this.mRecyclerView.setNestedScrollingEnabled(newScrolling);
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            updateDisplayedItems(listContent.getHeight(this.mSliceStyle, this.mViewPolicy));
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void resetView() {
        this.mDisplayedItemsHeight = 0;
        this.mDisplayedItems.clear();
        this.mAdapter.setSliceItems(null, -1, getMode());
        this.mListContent = null;
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setActionLoading(SliceItem item) {
        this.mAdapter.onSliceActionLoading(item, 0);
    }

    public void setAdapter(SliceAdapter adapter) {
        this.mAdapter = adapter;
        this.mRecyclerView.setAdapter(adapter);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setAllowTwoLines(boolean allowTwoLines) {
        this.mAdapter.setAllowTwoLines(allowTwoLines);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setInsets(int l, int t, int r, int b) {
        super.setInsets(l, t, r, b);
        this.mAdapter.setInsets(l, t, r, b);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLastUpdated(long lastUpdated) {
        super.setLastUpdated(lastUpdated);
        this.mAdapter.setLastUpdated(lastUpdated);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setLoadingActions(Set<SliceItem> loadingActions) {
        this.mAdapter.setLoadingActions(loadingActions);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setPolicy(SliceViewPolicy policy) {
        super.setPolicy(policy);
        this.mAdapter.setPolicy(policy);
        policy.setListener(this);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setShowLastUpdated(boolean showLastUpdated) {
        super.setShowLastUpdated(showLastUpdated);
        this.mAdapter.setShowLastUpdated(showLastUpdated);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceActionListener(SliceView.OnSliceActionListener observer) {
        this.mObserver = observer;
        SliceAdapter sliceAdapter = this.mAdapter;
        if (sliceAdapter != null) {
            sliceAdapter.setSliceObserver(observer);
        }
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceActions(List<SliceAction> actions) {
        this.mAdapter.setSliceActions(actions);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setSliceContent(ListContent sliceContent) {
        this.mListContent = sliceContent;
        updateDisplayedItems(sliceContent.getHeight(this.mSliceStyle, this.mViewPolicy));
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setStyle(SliceStyle style, RowStyle rowStyle) {
        super.setStyle(style, rowStyle);
        this.mAdapter.setStyle(style);
        applyRowStyle(rowStyle);
    }

    @Override // androidx.slice.widget.SliceChildView
    public void setTint(int tint) {
        super.setTint(tint);
        updateDisplayedItems(getMeasuredHeight());
    }
}
