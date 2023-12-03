package com.android.settings.usagestats.widget.render;

import com.android.settings.usagestats.model.AppUsageStats;
import com.android.settings.usagestats.model.AppValueData;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes2.dex */
public interface IRenderOneAppInterface {
    void setOneDayList(List<AppUsageStats> list);

    void setWeekList(ArrayList<AppValueData> arrayList);
}
