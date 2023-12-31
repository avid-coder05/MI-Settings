package com.android.settings.homepage.contextualcards.slices;

import android.graphics.Canvas;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.homepage.contextualcards.slices.SliceFullCardRendererHelper;
import com.android.settings.homepage.contextualcards.slices.SliceHalfCardRendererHelper;

/* loaded from: classes.dex */
public class SwipeDismissalDelegate extends ItemTouchHelper.Callback {
    private final Listener mListener;

    /* loaded from: classes.dex */
    public interface Listener {
        void onSwiped(int i);
    }

    public SwipeDismissalDelegate(Listener listener) {
        this.mListener = listener;
    }

    private View getSwipeableView(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH ? ((SliceHalfCardRendererHelper.HalfCardViewHolder) viewHolder).content : ((SliceFullCardRendererHelper.SliceViewHolder) viewHolder).sliceView;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(getSwipeableView(viewHolder));
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if ((viewHolder.getItemViewType() == SliceContextualCardRenderer.VIEW_TYPE_FULL_WIDTH || viewHolder.getItemViewType() == SliceContextualCardRenderer.VIEW_TYPE_HALF_WIDTH) && viewHolder.itemView.findViewById(R.id.dismissal_view).getVisibility() != 0) {
            return ItemTouchHelper.Callback.makeMovementFlags(0, 12);
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
        View swipeableView = getSwipeableView(viewHolder);
        View findViewById = viewHolder.itemView.findViewById(R.id.dismissal_icon_start);
        View findViewById2 = viewHolder.itemView.findViewById(R.id.dismissal_icon_end);
        if (f > 0.0f) {
            findViewById.setVisibility(0);
            findViewById2.setVisibility(8);
        } else if (f < 0.0f) {
            findViewById.setVisibility(8);
            findViewById2.setVisibility(0);
        }
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(canvas, recyclerView, swipeableView, f, f2, i, z);
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
        return false;
    }

    @Override // androidx.recyclerview.widget.ItemTouchHelper.Callback
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        this.mListener.onSwiped(viewHolder.getAdapterPosition());
    }
}
