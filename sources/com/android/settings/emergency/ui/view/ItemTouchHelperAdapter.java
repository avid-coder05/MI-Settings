package com.android.settings.emergency.ui.view;

import androidx.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public interface ItemTouchHelperAdapter {
    void onItemClear(RecyclerView.ViewHolder viewHolder);

    void onItemMove(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2);

    void onItemSelect(RecyclerView.ViewHolder viewHolder);
}
