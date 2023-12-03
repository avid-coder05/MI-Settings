package com.android.settings.usagestats.widget.render;

import com.android.settings.usagestats.model.DayDeviceUsageStats;
import java.util.List;

/* loaded from: classes2.dex */
public interface ISetDeviceDataInterface {
    void setDeviceDataList(List<DayDeviceUsageStats> list);

    void setOneDayList(List<Integer> list);
}
