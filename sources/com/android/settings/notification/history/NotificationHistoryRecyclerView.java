package com.android.settings.notification.history;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes2.dex */
public class NotificationHistoryRecyclerView extends RecyclerView {
    private float dXLast;
    private OnItemSwipeDeleteListener listener;

    /* loaded from: classes2.dex */
    private class DismissTouchHelper extends ItemTouchHelper.SimpleCallback {
        public DismissTouchHelper(int i, int i2) {
            super(i, i2);
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
            if (!z) {
                NotificationHistoryRecyclerView.this.dXLast = 0.0f;
                return;
            }
            View view = viewHolder.itemView;
            float swipeThreshold = getSwipeThreshold(viewHolder) * view.getWidth();
            float f3 = -swipeThreshold;
            if ((f < f3 || f > swipeThreshold) != (NotificationHistoryRecyclerView.this.dXLast < f3 || NotificationHistoryRecyclerView.this.dXLast > swipeThreshold)) {
                view.performHapticFeedback(4);
            }
            NotificationHistoryRecyclerView.this.dXLast = f;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            return false;
        }

        @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
            if (NotificationHistoryRecyclerView.this.listener != null) {
                NotificationHistoryRecyclerView.this.listener.onItemSwipeDeleted(viewHolder.getAdapterPosition());
            }
        }
    }

    /* loaded from: classes2.dex */
    public interface OnItemSwipeDeleteListener {
        void onItemSwipeDeleted(int i);
    }

    public NotificationHistoryRecyclerView(Context context) {
        this(context, null);
    }

    public NotificationHistoryRecyclerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NotificationHistoryRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutManager(new LinearLayoutManager(getContext()));
        addItemDecoration(new DividerItemDecoration(getContext(), 1));
        new ItemTouchHelper(new DismissTouchHelper(0, 48)).attachToRecyclerView(this);
        setNestedScrollingEnabled(false);
    }

    public void setOnItemSwipeDeleteListener(OnItemSwipeDeleteListener onItemSwipeDeleteListener) {
        this.listener = onItemSwipeDeleteListener;
    }
}
