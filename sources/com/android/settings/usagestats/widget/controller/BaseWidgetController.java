package com.android.settings.usagestats.widget.controller;

import android.content.Context;
import android.view.View;
import com.android.settings.usagestats.model.DayAppUsageStats;
import com.android.settings.usagestats.model.UsageFloorData;
import java.util.Observable;
import java.util.Observer;

/* loaded from: classes2.dex */
public abstract class BaseWidgetController implements Observer {
    protected static DayAppUsageStats mTodayAppUsageStats;
    private boolean hasDealData = false;
    protected boolean isWeekData;
    protected Context mContext;
    protected UsageFloorData mFloorData;
    protected View mView;

    public BaseWidgetController(Context context, UsageFloorData usageFloorData) {
        this.mContext = context;
        this.mFloorData = usageFloorData;
    }

    private void dealStringEvent(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1286591077:
                if (str.equals("notify_device_usage_data")) {
                    c = 0;
                    break;
                }
                break;
            case 17876926:
                if (str.equals("notify_all_data")) {
                    c = 1;
                    break;
                }
                break;
            case 1033178385:
                if (str.equals("notify_release")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                updateDeviceMsg();
                return;
            case 1:
                updateAllData();
                return;
            case 2:
                release();
                return;
            default:
                return;
        }
    }

    public static void renewWeekState() {
        mTodayAppUsageStats = null;
    }

    protected void changeChannel(boolean z) {
    }

    protected abstract void dealData();

    protected void doUpdateOthers(Object obj) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void release() {
    }

    public void setWeekData(boolean z) {
        this.isWeekData = z;
    }

    public void setWidget(View view) {
        this.mView = view;
        if (this.hasDealData) {
            return;
        }
        dealData();
        this.hasDealData = true;
    }

    @Override // java.util.Observer
    public void update(Observable observable, Object obj) {
        if (obj instanceof Boolean) {
            changeChannel(((Boolean) obj).booleanValue());
        } else if (obj instanceof String) {
            dealStringEvent((String) obj);
        } else if (!(obj instanceof DayAppUsageStats)) {
            doUpdateOthers(obj);
        } else {
            DayAppUsageStats dayAppUsageStats = (DayAppUsageStats) obj;
            mTodayAppUsageStats = dayAppUsageStats;
            updateTodayAppUsageData(dayAppUsageStats);
        }
    }

    @Deprecated
    protected void updateAllData() {
    }

    protected void updateDeviceMsg() {
    }

    protected void updateTodayAppUsageData(DayAppUsageStats dayAppUsageStats) {
    }
}
