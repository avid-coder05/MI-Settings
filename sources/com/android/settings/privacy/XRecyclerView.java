package com.android.settings.privacy;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes2.dex */
public class XRecyclerView extends RecyclerView {
    private HeaderViewRecyclerAdapter mAdapter;

    public XRecyclerView(Context context) {
        super(context);
        wrapHeaderAdapter();
    }

    public XRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        wrapHeaderAdapter();
    }

    public XRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        wrapHeaderAdapter();
    }

    private void wrapHeaderAdapter() {
        HeaderViewRecyclerAdapter headerViewRecyclerAdapter = new HeaderViewRecyclerAdapter(super.getAdapter());
        this.mAdapter = headerViewRecyclerAdapter;
        super.setAdapter(headerViewRecyclerAdapter);
    }

    public void addFooterView(View view) {
        this.mAdapter.addFooterView(view);
    }

    public void addHeaderView(View view) {
        this.mAdapter.addHeaderView(view);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public RecyclerView.Adapter getAdapter() {
        return this.mAdapter.getAdapter();
    }

    public int getFootersCount() {
        return this.mAdapter.getFootersCount();
    }

    public int getHeadersCount() {
        return this.mAdapter.getHeadersCount();
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        this.mAdapter.setAdapter(adapter);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        super.setLayoutManager(layoutManager);
    }
}
