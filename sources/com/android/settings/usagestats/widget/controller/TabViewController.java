package com.android.settings.usagestats.widget.controller;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.usagestats.model.UsageFloorData;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.ControllerObserverUtil;

/* loaded from: classes2.dex */
public class TabViewController extends BaseWidgetController {
    private TextView btnOnDay;
    private TextView btnWeek;

    public TabViewController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
    }

    private void reset() {
        if (this.isWeekData) {
            this.btnWeek.setTextColor(AppInfoUtils.getColor(this.mContext, R.color.usage_stats_tab_select));
            this.btnOnDay.setTextColor(AppInfoUtils.getColor(this.mContext, R.color.usage_stats_tab_unselect));
            return;
        }
        this.btnWeek.setTextColor(AppInfoUtils.getColor(this.mContext, R.color.usage_stats_tab_unselect));
        this.btnOnDay.setTextColor(AppInfoUtils.getColor(this.mContext, R.color.usage_stats_tab_select));
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void changeChannel(boolean z) {
        this.isWeekData = z;
        reset();
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.mView.setOnTouchListener(new View.OnTouchListener() { // from class: com.android.settings.usagestats.widget.controller.TabViewController.1
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        this.btnOnDay = (TextView) this.mView.findViewById(R.id.btn_today);
        this.btnWeek = (TextView) this.mView.findViewById(R.id.btn_week);
        reset();
        this.btnOnDay.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.TabViewController.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ControllerObserverUtil.getInstance().notify(Boolean.FALSE);
            }
        });
        this.btnWeek.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.TabViewController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ControllerObserverUtil.getInstance().notify(Boolean.TRUE);
            }
        });
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateAllData() {
    }
}
