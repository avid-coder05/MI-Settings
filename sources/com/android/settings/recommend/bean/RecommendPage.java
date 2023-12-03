package com.android.settings.recommend.bean;

import java.util.List;

/* loaded from: classes2.dex */
public class RecommendPage {
    private List<RecommendItem> items;
    private int sourcePageIndex;

    public List<RecommendItem> getItems() {
        return this.items;
    }

    public int getSourcePageIndex() {
        return this.sourcePageIndex;
    }

    public void setItems(List<RecommendItem> list) {
        this.items = list;
    }

    public void setSourcePageIndex(int i) {
        this.sourcePageIndex = i;
    }

    public String toString() {
        return "RecommendPage{sourcePageIndex=" + this.sourcePageIndex + ", items=" + this.items + '}';
    }
}
