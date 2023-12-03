package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;
import com.android.settings.usagestats.widget.controller.PagerViewController;

/* loaded from: classes2.dex */
public class PagerHolder extends BaseHolder {
    private BaseWidgetController mController;

    public PagerHolder(Context context) {
        super(context);
        this.mController = new PagerViewController(this.mContext, null);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_pager, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
