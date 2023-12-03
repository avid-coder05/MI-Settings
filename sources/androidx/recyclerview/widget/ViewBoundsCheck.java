package androidx.recyclerview.widget;

import android.view.View;

/* loaded from: classes.dex */
class ViewBoundsCheck {
    BoundFlags mBoundFlags = new BoundFlags();
    final Callback mCallback;

    /* loaded from: classes.dex */
    static class BoundFlags {
        int mBoundFlags = 0;
        int mChildEnd;
        int mChildStart;
        int mRvEnd;
        int mRvStart;

        BoundFlags() {
        }

        void addFlags(int flags) {
            this.mBoundFlags = flags | this.mBoundFlags;
        }

        boolean boundsMatch() {
            int i = this.mBoundFlags;
            if ((i & 7) == 0 || (i & (compare(this.mChildStart, this.mRvStart) << 0)) != 0) {
                int i2 = this.mBoundFlags;
                if ((i2 & 112) == 0 || (i2 & (compare(this.mChildStart, this.mRvEnd) << 4)) != 0) {
                    int i3 = this.mBoundFlags;
                    if ((i3 & 1792) == 0 || (i3 & (compare(this.mChildEnd, this.mRvStart) << 8)) != 0) {
                        int i4 = this.mBoundFlags;
                        return (i4 & 28672) == 0 || ((compare(this.mChildEnd, this.mRvEnd) << 12) & i4) != 0;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }

        int compare(int x, int y) {
            if (x > y) {
                return 1;
            }
            return x == y ? 2 : 4;
        }

        void resetFlags() {
            this.mBoundFlags = 0;
        }

        void setBounds(int rvStart, int rvEnd, int childStart, int childEnd) {
            this.mRvStart = rvStart;
            this.mRvEnd = rvEnd;
            this.mChildStart = childStart;
            this.mChildEnd = childEnd;
        }
    }

    /* loaded from: classes.dex */
    interface Callback {
        View getChildAt(int index);

        int getChildEnd(View view);

        int getChildStart(View view);

        int getParentEnd();

        int getParentStart();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ViewBoundsCheck(Callback callback) {
        this.mCallback = callback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public View findOneViewWithinBoundFlags(int fromIndex, int toIndex, int preferredBoundFlags, int acceptableBoundFlags) {
        int parentStart = this.mCallback.getParentStart();
        int parentEnd = this.mCallback.getParentEnd();
        int i = toIndex > fromIndex ? 1 : -1;
        View view = null;
        while (fromIndex != toIndex) {
            View childAt = this.mCallback.getChildAt(fromIndex);
            this.mBoundFlags.setBounds(parentStart, parentEnd, this.mCallback.getChildStart(childAt), this.mCallback.getChildEnd(childAt));
            if (preferredBoundFlags != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(preferredBoundFlags);
                if (this.mBoundFlags.boundsMatch()) {
                    return childAt;
                }
            }
            if (acceptableBoundFlags != 0) {
                this.mBoundFlags.resetFlags();
                this.mBoundFlags.addFlags(acceptableBoundFlags);
                if (this.mBoundFlags.boundsMatch()) {
                    view = childAt;
                }
            }
            fromIndex += i;
        }
        return view;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isViewWithinBoundFlags(View child, int boundsFlags) {
        this.mBoundFlags.setBounds(this.mCallback.getParentStart(), this.mCallback.getParentEnd(), this.mCallback.getChildStart(child), this.mCallback.getChildEnd(child));
        if (boundsFlags != 0) {
            this.mBoundFlags.resetFlags();
            this.mBoundFlags.addFlags(boundsFlags);
            return this.mBoundFlags.boundsMatch();
        }
        return false;
    }
}
