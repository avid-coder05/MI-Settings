package com.google.android.setupdesign.items;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes2.dex */
public abstract class AbstractItem extends AbstractItemHierarchy implements IItem {
    public AbstractItem() {
    }

    public AbstractItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy
    public int getCount() {
        return 1;
    }

    @Override // com.google.android.setupdesign.items.ItemHierarchy
    public IItem getItemAt(int i) {
        return this;
    }

    public void notifyItemChanged() {
        notifyItemRangeChanged(0, 1);
    }
}
