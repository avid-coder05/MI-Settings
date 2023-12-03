package com.android.settings.usagestats.widget.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.usagestats.UsageStatsTimeSetActivity;
import com.android.settings.usagestats.controller.DeviceUsageController;
import com.android.settings.usagestats.model.UsageFloorData;

/* loaded from: classes2.dex */
public class UsageTimeRemindController extends BaseWidgetController {
    private int initWeekDayTime;
    private int initWeekEndTime;
    private boolean isOpenRemind;
    private TextView vWeekDayText;
    private TextView vWeekEndText;

    public UsageTimeRemindController(Context context, UsageFloorData usageFloorData) {
        super(context, usageFloorData);
        this.initWeekDayTime = 0;
        this.initWeekEndTime = 0;
    }

    private void initValue() {
        if (this.initWeekDayTime == 0) {
            this.initWeekDayTime = 300;
        }
        if (this.initWeekEndTime == 0) {
            this.initWeekEndTime = 480;
        }
    }

    private void setWeekText() {
        if (this.initWeekEndTime == 0) {
            this.vWeekEndText.setVisibility(4);
            return;
        }
        this.vWeekEndText.setVisibility(0);
        int i = this.initWeekEndTime;
        int i2 = i / 60;
        int i3 = i % 60;
        if (i2 == 0) {
            this.vWeekEndText.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_week_day_minute, i3, Integer.valueOf(i3)));
        } else if (i3 == 0) {
            this.vWeekEndText.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_week_day_hour, i2, Integer.valueOf(i2)));
        } else {
            this.vWeekEndText.setText(this.mContext.getString(R.string.usage_state_week_day_hour_minute, Integer.valueOf(i2), Integer.valueOf(i3)));
        }
    }

    private void setWorkText() {
        if (this.initWeekDayTime == 0) {
            this.vWeekDayText.setVisibility(4);
            return;
        }
        this.vWeekDayText.setVisibility(0);
        int i = this.initWeekDayTime;
        int i2 = i / 60;
        int i3 = i % 60;
        if (i2 == 0) {
            this.vWeekDayText.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_work_day_minute, i3, Integer.valueOf(i3)));
        } else if (i3 == 0) {
            this.vWeekDayText.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_work_day_hour, i2, Integer.valueOf(i2)));
        } else {
            this.vWeekDayText.setText(this.mContext.getString(R.string.usage_state_work_day_hour_minute, Integer.valueOf(i2), Integer.valueOf(i3)));
        }
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void dealData() {
        this.mView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.widget.controller.UsageTimeRemindController.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UsageTimeRemindController.this.mContext, UsageStatsTimeSetActivity.class);
                if (!(UsageTimeRemindController.this.mContext instanceof Activity)) {
                    intent.addFlags(268435456);
                }
                UsageTimeRemindController.this.mContext.startActivity(intent);
            }
        });
        this.vWeekEndText = (TextView) this.mView.findViewById(R.id.tv_week_day);
        this.vWeekDayText = (TextView) this.mView.findViewById(R.id.tv_work_day);
        if (!this.isOpenRemind) {
            this.isOpenRemind = DeviceUsageController.getMonitorStatus(this.mContext.getApplicationContext());
        }
        if (!this.isOpenRemind) {
            this.vWeekEndText.setVisibility(4);
            this.vWeekDayText.setVisibility(4);
            return;
        }
        int i = this.initWeekEndTime;
        if (i == 0) {
            i = DeviceUsageController.getLimitedTimeCommon(this.mContext.getApplicationContext(), false);
        }
        this.initWeekEndTime = i;
        int i2 = this.initWeekDayTime;
        if (i2 == 0) {
            i2 = DeviceUsageController.getLimitedTimeCommon(this.mContext.getApplicationContext(), true);
        }
        this.initWeekDayTime = i2;
        initValue();
        setWorkText();
        setWeekText();
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void doUpdateOthers(Object obj) {
        boolean monitorStatus = DeviceUsageController.getMonitorStatus(this.mContext.getApplicationContext());
        this.isOpenRemind = monitorStatus;
        if (!monitorStatus) {
            this.vWeekEndText.setVisibility(4);
            this.vWeekDayText.setVisibility(4);
            return;
        }
        this.initWeekDayTime = DeviceUsageController.getLimitedTimeCommon(this.mContext.getApplicationContext(), true);
        this.initWeekEndTime = DeviceUsageController.getLimitedTimeCommon(this.mContext.getApplicationContext(), false);
        initValue();
        setWorkText();
        setWeekText();
    }

    @Override // com.android.settings.usagestats.widget.controller.BaseWidgetController
    protected void updateAllData() {
    }
}
