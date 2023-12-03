package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;
import com.android.settings.usagestats.widget.controller.UsageTimeRemindController;

/* loaded from: classes2.dex */
public class UsageTimeRemindHolder extends BaseHolder {
    private BaseWidgetController mController;

    public UsageTimeRemindHolder(Context context) {
        super(context);
        this.mController = new UsageTimeRemindController(this.mContext, null);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_app_remind_item, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
