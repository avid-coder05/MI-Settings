package com.android.settings.privacy;

import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.AdapterDataObserver mObserver;
    private final List<FixedViewInfo> mHeaderViewInfos = new ArrayList();
    private final List<FixedViewInfo> mFooterViewInfos = new ArrayList();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class FixedViewInfo {
        int itemViewType;
        View view;

        private FixedViewInfo() {
        }
    }

    /* loaded from: classes2.dex */
    private static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View view) {
            super(view);
        }
    }

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter) {
        RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() { // from class: com.android.settings.privacy.HeaderViewRecyclerAdapter.1
            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onChanged() {
                HeaderViewRecyclerAdapter.this.notifyDataSetChanged();
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int i, int i2) {
                super.onItemRangeChanged(i, i2);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeChanged(int i, int i2, Object obj) {
                HeaderViewRecyclerAdapter headerViewRecyclerAdapter = HeaderViewRecyclerAdapter.this;
                headerViewRecyclerAdapter.notifyItemRangeChanged(headerViewRecyclerAdapter.getHeadersCount() + i, i2, obj);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int i, int i2) {
                HeaderViewRecyclerAdapter headerViewRecyclerAdapter = HeaderViewRecyclerAdapter.this;
                headerViewRecyclerAdapter.notifyItemRangeInserted(headerViewRecyclerAdapter.getHeadersCount() + i, i2);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeMoved(int i, int i2, int i3) {
                HeaderViewRecyclerAdapter headerViewRecyclerAdapter = HeaderViewRecyclerAdapter.this;
                headerViewRecyclerAdapter.notifyItemMoved(headerViewRecyclerAdapter.getHeadersCount() + i, HeaderViewRecyclerAdapter.this.getHeadersCount() + i2);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeRemoved(int i, int i2) {
                HeaderViewRecyclerAdapter headerViewRecyclerAdapter = HeaderViewRecyclerAdapter.this;
                headerViewRecyclerAdapter.notifyItemRangeRemoved(headerViewRecyclerAdapter.getHeadersCount() + i, i2);
            }
        };
        this.mObserver = adapterDataObserver;
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(adapterDataObserver);
        }
    }

    private void addFooterView(View view, int i) {
        FixedViewInfo fixedViewInfo = new FixedViewInfo();
        fixedViewInfo.view = view;
        fixedViewInfo.itemViewType = i;
        this.mFooterViewInfos.add(fixedViewInfo);
        notifyDataSetChanged();
    }

    private void addHeaderView(View view, int i) {
        FixedViewInfo fixedViewInfo = new FixedViewInfo();
        fixedViewInfo.view = view;
        fixedViewInfo.itemViewType = i;
        this.mHeaderViewInfos.add(fixedViewInfo);
        notifyDataSetChanged();
    }

    private View findViewForInfos(int i) {
        for (FixedViewInfo fixedViewInfo : this.mHeaderViewInfos) {
            if (fixedViewInfo.itemViewType == i) {
                return fixedViewInfo.view;
            }
        }
        for (FixedViewInfo fixedViewInfo2 : this.mFooterViewInfos) {
            if (fixedViewInfo2.itemViewType == i) {
                return fixedViewInfo2.view;
            }
        }
        return null;
    }

    private int generateUniqueViewType() {
        boolean z;
        int random;
        int itemCount = getItemCount();
        do {
            z = true;
            random = ((int) (Math.random() * 2.147483647E9d)) + 1;
            int i = 0;
            while (true) {
                if (i >= itemCount) {
                    z = false;
                    break;
                } else if (random == getItemViewType(i)) {
                    break;
                } else {
                    i++;
                }
            }
        } while (z);
        return random;
    }

    private void handleLayoutIfStaggeredGridLayout(RecyclerView.ViewHolder viewHolder, int i) {
        if (isHeader(i) || isFooter(i)) {
            ((StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams()).setFullSpan(true);
        }
    }

    private boolean isStaggeredGridLayout(RecyclerView.ViewHolder viewHolder) {
        ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
        return layoutParams != null && (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams);
    }

    public void addFooterView(View view) {
        addFooterView(view, generateUniqueViewType());
    }

    public void addHeaderView(View view) {
        addHeaderView(view, generateUniqueViewType());
    }

    public RecyclerView.Adapter getAdapter() {
        return this.mAdapter;
    }

    public int getFootersCount() {
        return this.mFooterViewInfos.size();
    }

    public int getHeadersCount() {
        return this.mHeaderViewInfos.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        int size = this.mHeaderViewInfos.size() + this.mFooterViewInfos.size();
        RecyclerView.Adapter adapter = this.mAdapter;
        return size + (adapter == null ? 0 : adapter.getItemCount());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        if (isHeader(i)) {
            return this.mHeaderViewInfos.get(i).itemViewType;
        }
        if (isFooter(i)) {
            return this.mFooterViewInfos.get((i - this.mHeaderViewInfos.size()) - this.mAdapter.getItemCount()).itemViewType;
        }
        return this.mAdapter.getItemViewType(i - getHeadersCount());
    }

    public boolean isFooter(int i) {
        return getItemCount() - i <= getFootersCount();
    }

    public boolean isHeader(int i) {
        return i < getHeadersCount();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = this.mAdapter;
        if (adapter != null) {
            adapter.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (isHeader(i) || isFooter(i)) {
            return;
        }
        this.mAdapter.onBindViewHolder(viewHolder, i - getHeadersCount());
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View findViewForInfos = findViewForInfos(i);
        return findViewForInfos != null ? new ViewHolder(findViewForInfos) : this.mAdapter.onCreateViewHolder(viewGroup, i);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = this.mAdapter;
        if (adapter != null) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof ViewHolder ? super.onFailedToRecycleView(viewHolder) : this.mAdapter.onFailedToRecycleView(viewHolder);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            super.onViewAttachedToWindow(viewHolder);
        } else {
            this.mAdapter.onViewAttachedToWindow(viewHolder);
        }
        if (isStaggeredGridLayout(viewHolder)) {
            handleLayoutIfStaggeredGridLayout(viewHolder, viewHolder.getLayoutPosition());
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            super.onViewDetachedFromWindow(viewHolder);
        } else {
            this.mAdapter.onViewDetachedFromWindow(viewHolder);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ViewHolder) {
            super.onViewRecycled(viewHolder);
        } else {
            this.mAdapter.onViewRecycled(viewHolder);
        }
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof HeaderViewRecyclerAdapter) {
            throw new IllegalArgumentException("Cannot wrap a HeaderViewRecyclerAdapter");
        }
        this.mAdapter = adapter;
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mObserver);
        }
        notifyDataSetChanged();
    }
}
