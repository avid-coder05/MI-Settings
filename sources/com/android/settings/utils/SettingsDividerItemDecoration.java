package com.android.settings.utils;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.setupdesign.DividerItemDecoration;

/* loaded from: classes2.dex */
public class SettingsDividerItemDecoration extends DividerItemDecoration {
    public SettingsDividerItemDecoration(Context context) {
        super(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.DividerItemDecoration
    public boolean isDividerAllowedAbove(RecyclerView.ViewHolder viewHolder) {
        return super.isDividerAllowedAbove(viewHolder);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.google.android.setupdesign.DividerItemDecoration
    public boolean isDividerAllowedBelow(RecyclerView.ViewHolder viewHolder) {
        return super.isDividerAllowedBelow(viewHolder);
    }
}
