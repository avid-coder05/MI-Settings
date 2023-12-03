package com.android.settings.analytics;

import android.text.TextUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;

/* loaded from: classes.dex */
public class SearchStatItem {
    private int mClickedItemOrder = -1;
    private String mClickedResource = null;
    private boolean mIsAlreadyStat = false;
    private String mKeyWork;
    private int mSearchResultCount;

    public void clear() {
        this.mIsAlreadyStat = false;
        this.mKeyWork = null;
        this.mSearchResultCount = 0;
        this.mClickedItemOrder = -1;
        this.mClickedResource = null;
    }

    public String getKeyWork() {
        return this.mKeyWork;
    }

    public void setClickedItemOrder(int i) {
        this.mClickedItemOrder = i;
    }

    public void setClickedResource(String str) {
        this.mClickedResource = str;
    }

    public void setIsAlreadyStat(boolean z) {
        this.mIsAlreadyStat = z;
    }

    public void setKeyWork(String str) {
        this.mKeyWork = str;
    }

    public void setSearchResultCount(int i) {
        this.mSearchResultCount = i;
    }

    public void traceSearchEvent(boolean z) {
        if (TextUtils.isEmpty(this.mKeyWork)) {
            return;
        }
        if (z || !this.mIsAlreadyStat) {
            HashMap hashMap = new HashMap();
            hashMap.put("search_keyword", this.mKeyWork);
            hashMap.put("search_result_count", Integer.valueOf(this.mSearchResultCount));
            hashMap.put("search_item_click_order", Integer.valueOf(this.mClickedItemOrder));
            OneTrackInterfaceUtils.track("search_item_click", hashMap);
        }
    }
}
