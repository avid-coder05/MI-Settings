package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.AppUsageStateViewController;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;

/* loaded from: classes2.dex */
public class AppUsageHolder extends BaseHolder {
    private BaseWidgetController mController;

    public AppUsageHolder(Context context, UsageFloorData usageFloorData) {
        super(context);
        this.mController = new AppUsageStateViewController(this.mContext, usageFloorData);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_app_usage, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
