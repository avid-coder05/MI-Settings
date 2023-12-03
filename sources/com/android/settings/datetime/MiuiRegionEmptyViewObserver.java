package com.android.settings.datetime;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public class MiuiRegionEmptyViewObserver extends RecyclerView.AdapterDataObserver {
    private View mEmptyView;
    private RecyclerView mRecyclerView;

    public MiuiRegionEmptyViewObserver(View view, RecyclerView recyclerView) {
        this.mEmptyView = view;
        this.mRecyclerView = recyclerView;
        checkItemIsEmpty();
    }

    public void checkItemIsEmpty() {
        if (this.mEmptyView == null || this.mRecyclerView.getAdapter() == null) {
            return;
        }
        this.mEmptyView.setVisibility(this.mRecyclerView.getAdapter().getItemCount() == 0 ? 0 : 8);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
    public void onChanged() {
        super.onChanged();
        checkItemIsEmpty();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
    public void onItemRangeInserted(int i, int i2) {
        super.onItemRangeInserted(i, i2);
        checkItemIsEmpty();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
    public void onItemRangeRemoved(int i, int i2) {
        super.onItemRangeRemoved(i, i2);
        checkItemIsEmpty();
    }
}
