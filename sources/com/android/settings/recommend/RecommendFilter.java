package com.android.settings.recommend;

import android.content.Context;
import android.util.Log;
import com.android.settings.recommend.bean.RecommendItem;
import java.util.List;

/* loaded from: classes2.dex */
public class RecommendFilter {
    public static final String TAG = "RecommendFilter";

    public List<RecommendItem> getListByPageIndex(Context context, int i) {
        List<RecommendItem> recommendItemList = RecommendManager.getInstance(context).getRecommendItemList(i);
        if (recommendItemList != null && recommendItemList.size() > 0) {
            Log.i(TAG, recommendItemList.toString());
            return recommendItemList;
        }
        Log.e(TAG, "getListByPageIndex not found:" + i);
        return null;
    }
}
