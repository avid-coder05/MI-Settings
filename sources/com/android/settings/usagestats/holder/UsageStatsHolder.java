package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;
import com.android.settings.usagestats.widget.controller.NotificationCountController;
import com.android.settings.usagestats.widget.controller.UnlockCountController;

/* loaded from: classes2.dex */
public class UsageStatsHolder extends BaseHolder {
    private UsageFloorData floorData;
    private boolean isWeek;
    private int mBarType;
    private BaseWidgetController mController;

    public UsageStatsHolder(Context context, int i, UsageFloorData usageFloorData) {
        super(context);
        initData(i, usageFloorData);
    }

    public UsageStatsHolder(Context context, int i, UsageFloorData usageFloorData, boolean z) {
        super(context);
        this.isWeek = z;
        initData(i, usageFloorData);
    }

    private void initController() {
        int i = this.mBarType;
        if (i == 3) {
            this.mController = new UnlockCountController(this.mContext, this.floorData);
        } else if (i == 2) {
            this.mController = new NotificationCountController(this.mContext, this.floorData);
        }
        this.mController.setWeekData(this.isWeek);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_device_usage, null);
    }

    public void initData(int i, UsageFloorData usageFloorData) {
        this.mBarType = i;
        this.floorData = usageFloorData;
        initController();
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
