package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.AppUsageListController;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;

/* loaded from: classes2.dex */
public class AppUsageListHolder extends BaseHolder {
    private BaseWidgetController mController;
    private UsageFloorData mFloorData;

    public AppUsageListHolder(Context context, UsageFloorData usageFloorData) {
        super(context);
        initData(usageFloorData);
    }

    public AppUsageListHolder(Context context, UsageFloorData usageFloorData, boolean z) {
        super(context);
        initData(usageFloorData);
        this.mController.setWeekData(z);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.widget_app_usage_list, null);
    }

    public void initData(UsageFloorData usageFloorData) {
        this.mFloorData = usageFloorData;
        this.mController = new AppUsageListController(this.mContext, this.mFloorData);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
