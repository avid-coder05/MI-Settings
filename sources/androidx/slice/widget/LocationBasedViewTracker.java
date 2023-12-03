package androidx.slice.widget;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes.dex */
public class LocationBasedViewTracker implements Runnable, View.OnLayoutChangeListener {
    private final Rect mFocusRect;
    private final ViewGroup mParent;
    private final SelectionLogic mSelectionLogic;
    private static final SelectionLogic INPUT_FOCUS = new SelectionLogic() { // from class: androidx.slice.widget.LocationBasedViewTracker.1
        @Override // androidx.slice.widget.LocationBasedViewTracker.SelectionLogic
        public void selectView(View view) {
            view.requestFocus();
        }
    };
    @TargetApi(21)
    private static final SelectionLogic A11Y_FOCUS = new SelectionLogic() { // from class: androidx.slice.widget.LocationBasedViewTracker.2
        @Override // androidx.slice.widget.LocationBasedViewTracker.SelectionLogic
        public void selectView(View view) {
            view.performAccessibilityAction(64, null);
        }
    };

    /* loaded from: classes.dex */
    private interface SelectionLogic {
        void selectView(View view);
    }

    private LocationBasedViewTracker(ViewGroup parent, View selected, SelectionLogic selectionLogic) {
        Rect rect = new Rect();
        this.mFocusRect = rect;
        this.mParent = parent;
        this.mSelectionLogic = selectionLogic;
        selected.getDrawingRect(rect);
        parent.offsetDescendantRectToMyCoords(selected, rect);
        parent.addOnLayoutChangeListener(this);
        parent.requestLayout();
    }

    public static void trackA11yFocus(ViewGroup parent) {
        if (Build.VERSION.SDK_INT >= 21 && ((AccessibilityManager) parent.getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            ArrayList<View> arrayList = new ArrayList<>();
            parent.addFocusables(arrayList, 2, 0);
            View view = null;
            Iterator<View> it = arrayList.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                View next = it.next();
                if (next.isAccessibilityFocused()) {
                    view = next;
                    break;
                }
            }
            if (view != null) {
                new LocationBasedViewTracker(parent, view, A11Y_FOCUS);
            }
        }
    }

    public static void trackInputFocused(ViewGroup parent) {
        View findFocus = parent.findFocus();
        if (findFocus != null) {
            new LocationBasedViewTracker(parent, findFocus, INPUT_FOCUS);
        }
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        this.mParent.removeOnLayoutChangeListener(this);
        this.mParent.post(this);
    }

    @Override // java.lang.Runnable
    public void run() {
        int abs;
        ArrayList<View> arrayList = new ArrayList<>();
        this.mParent.addFocusables(arrayList, 2, 0);
        Rect rect = new Rect();
        Iterator<View> it = arrayList.iterator();
        int i = Integer.MAX_VALUE;
        View view = null;
        while (it.hasNext()) {
            View next = it.next();
            next.getDrawingRect(rect);
            this.mParent.offsetDescendantRectToMyCoords(next, rect);
            if (this.mFocusRect.intersect(rect) && i > (abs = Math.abs(this.mFocusRect.left - rect.left) + Math.abs(this.mFocusRect.right - rect.right) + Math.abs(this.mFocusRect.top - rect.top) + Math.abs(this.mFocusRect.bottom - rect.bottom))) {
                view = next;
                i = abs;
            }
        }
        if (view != null) {
            this.mSelectionLogic.selectView(view);
        }
    }
}
