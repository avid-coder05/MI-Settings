package androidx.recyclerview.widget;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
class ScrollbarHelper {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollExtent(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (smoothScrollbarEnabled) {
            return Math.min(orientation.getTotalSpace(), orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild));
        }
        return Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollOffset(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled, boolean reverseLayout) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        int max = reverseLayout ? Math.max(0, (state.getItemCount() - Math.max(lm.getPosition(startChild), lm.getPosition(endChild))) - 1) : Math.max(0, Math.min(lm.getPosition(startChild), lm.getPosition(endChild)));
        if (smoothScrollbarEnabled) {
            return Math.round((max * (Math.abs(orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild)) / (Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1))) + (orientation.getStartAfterPadding() - orientation.getDecoratedStart(startChild)));
        }
        return max;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int computeScrollRange(RecyclerView.State state, OrientationHelper orientation, View startChild, View endChild, RecyclerView.LayoutManager lm, boolean smoothScrollbarEnabled) {
        if (lm.getChildCount() == 0 || state.getItemCount() == 0 || startChild == null || endChild == null) {
            return 0;
        }
        if (smoothScrollbarEnabled) {
            return (int) (((orientation.getDecoratedEnd(endChild) - orientation.getDecoratedStart(startChild)) / (Math.abs(lm.getPosition(startChild) - lm.getPosition(endChild)) + 1)) * state.getItemCount());
        }
        return state.getItemCount();
    }
}
