package androidx.slice.widget;

/* loaded from: classes.dex */
public class SliceViewPolicy {
    private PolicyChangeListener mListener;
    private int mMaxHeight = 0;
    private int mMaxSmallHeight = 0;
    private boolean mScrollable = true;
    private int mMode = 2;

    /* loaded from: classes.dex */
    public interface PolicyChangeListener {
        void onMaxHeightChanged(int newNewHeight);

        void onMaxSmallChanged(int newMaxSmallHeight);

        void onModeChanged(int newMode);

        void onScrollingChanged(boolean newScrolling);
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    public int getMaxSmallHeight() {
        return this.mMaxSmallHeight;
    }

    public int getMode() {
        return this.mMode;
    }

    public boolean isScrollable() {
        return this.mScrollable;
    }

    public void setListener(PolicyChangeListener listener) {
        this.mListener = listener;
    }

    public void setMaxHeight(int max) {
        if (max != this.mMaxHeight) {
            this.mMaxHeight = max;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onMaxHeightChanged(max);
            }
        }
    }

    public void setMaxSmallHeight(int maxSmallHeight) {
        if (this.mMaxSmallHeight != maxSmallHeight) {
            this.mMaxSmallHeight = maxSmallHeight;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onMaxSmallChanged(maxSmallHeight);
            }
        }
    }

    public void setMode(int mode) {
        if (this.mMode != mode) {
            this.mMode = mode;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onModeChanged(mode);
            }
        }
    }

    public void setScrollable(boolean scrollable) {
        if (scrollable != this.mScrollable) {
            this.mScrollable = scrollable;
            PolicyChangeListener policyChangeListener = this.mListener;
            if (policyChangeListener != null) {
                policyChangeListener.onScrollingChanged(scrollable);
            }
        }
    }
}
