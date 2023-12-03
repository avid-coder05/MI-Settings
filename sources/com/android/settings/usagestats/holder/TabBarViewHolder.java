package com.android.settings.usagestats.holder;

import android.content.Context;
import android.view.View;
import com.android.settings.R;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.widget.controller.BaseWidgetController;
import com.android.settings.usagestats.widget.controller.TabViewController;

/* loaded from: classes2.dex */
public class TabBarViewHolder extends BaseHolder {
    private BaseWidgetController mController;

    public TabBarViewHolder(Context context) {
        super(context);
        this.mController = new TabViewController(context, null);
        ControllerObserverUtil.getInstance().addObserver(this.mController);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.widget_tab_layout, null);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.mController.setWidget(this.mContentView);
    }
}
