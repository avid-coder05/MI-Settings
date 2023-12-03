package com.android.settings.usagestats.holder;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import com.android.settings.R;
import com.android.settings.usagestats.TimeoverActivity;
import com.android.settings.usagestats.controller.AppUsageController;
import com.android.settings.usagestats.utils.AppInfoUtils;
import com.android.settings.usagestats.utils.AppLimitStateUtils;
import com.android.settings.usagestats.utils.AppUsageStatsFactory;
import com.android.settings.usagestats.utils.DateUtils;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class AppTimeLimitSetHolder extends BaseHolder implements TimePickerDialog.OnTimeSetListener {
    private boolean hasUpdateData;
    private boolean isSetWeekDay;
    private boolean isSysApp;
    private boolean isWeekDayToday;
    private AlertDialog mDialog;
    private int mDisableColor;
    private String mFromPager;
    private String mPkgName;
    private int mTimeEnableColor;
    private TimePickerDialog mTimePickerDialog;
    private int mTitleEnableColor;
    private int mTodayUsageTime;
    private int mWeekDayLimitTime;
    private int mWeekEndLimitTime;
    private int todayLimitTime;
    private View vLine1;
    private View vLine2;
    private View vLine3;
    private SlidingButton vSwitch;
    private View vSwitchContainer;
    private TextView vSwitchSummary;
    private TextView vSwitchTitle;
    private View vWeekDay;
    private TextView vWeekDayTime;
    private TextView vWeekDayTitle;
    private View vWeekEnd;
    private TextView vWeekEndTime;
    private TextView vWeekEndTitle;

    public AppTimeLimitSetHolder(Context context, String str) {
        super(context);
        this.mPkgName = str;
        this.mTimePickerDialog = new TimePickerDialog(this.mContext, this, 2, 0, true);
    }

    private void amendTime(boolean z) {
        if (this.isWeekDayToday != z) {
            this.isWeekDayToday = z;
            this.mWeekDayLimitTime = AppLimitStateUtils.getLimitTime(this.mContext, this.mPkgName, true);
            this.mWeekEndLimitTime = AppLimitStateUtils.getLimitTime(this.mContext, this.mPkgName, false);
            this.mTodayUsageTime = (int) (AppUsageStatsFactory.loadTodayTotalTimeForPackage(this.mContext, this.mPkgName, DateUtils.today(), System.currentTimeMillis()) / DateUtils.INTERVAL_MINUTE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ensureOpenLimit(boolean z) {
        if (!z) {
            this.vSwitch.setChecked(false);
            updateAppState(this.vSwitch.isChecked());
        } else if (this.todayLimitTime <= this.mTodayUsageTime) {
            showConfirmDialog(true);
        } else {
            this.vSwitch.setChecked(true);
            updateAppState(true);
        }
    }

    private <T extends View> T findView(int i) {
        return (T) this.mContentView.findViewById(i);
    }

    private void hideSetTimeItem() {
        this.vSwitch.setEnabled(false);
        this.vSwitchContainer.setVisibility(8);
        this.vWeekDay.setVisibility(8);
        this.vWeekEnd.setVisibility(8);
        this.vLine1.setVisibility(8);
        this.vLine2.setVisibility(8);
        this.vLine3.setVisibility(8);
        findView(R.id.tv_sysapp_summary).setVisibility(0);
    }

    private boolean isFromTimeOver() {
        return TextUtils.equals(this.mFromPager, TimeoverActivity.class.getSimpleName());
    }

    private void setTimeText() {
        setTimeText(this.vWeekDayTime, this.mWeekDayLimitTime);
        setTimeText(this.vWeekEndTime, this.mWeekEndLimitTime);
    }

    private void setTimeText(TextView textView, int i) {
        if (i == 0) {
            textView.setVisibility(4);
            return;
        }
        textView.setVisibility(0);
        int i2 = i / 60;
        int i3 = i % 60;
        if (i2 == 0) {
            textView.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_minute, i3, Integer.valueOf(i3)));
        } else if (i3 == 0) {
            textView.setText(this.mContext.getResources().getQuantityString(R.plurals.usage_state_hour, i2, Integer.valueOf(i2)));
        } else {
            textView.setText(this.mContext.getString(R.string.usage_state_hour_minute, Integer.valueOf(i2), Integer.valueOf(i3)));
        }
    }

    private void setViewState() {
        if (this.isSysApp) {
            this.vSwitchTitle.setTextColor(this.mDisableColor);
            this.vSwitchSummary.setTextColor(this.mDisableColor);
        }
        this.vWeekDayTitle.setTextColor((this.isSysApp || !this.vSwitch.isChecked()) ? this.mDisableColor : this.mTitleEnableColor);
        this.vWeekEndTitle.setTextColor((this.isSysApp || !this.vSwitch.isChecked()) ? this.mDisableColor : this.mTitleEnableColor);
        this.vWeekEndTime.setTextColor((this.isSysApp || !this.vSwitch.isChecked()) ? this.mDisableColor : this.mTimeEnableColor);
        this.vWeekDayTime.setTextColor((this.isSysApp || !this.vSwitch.isChecked()) ? this.mDisableColor : this.mTimeEnableColor);
    }

    private void showConfirmDialog(final boolean z) {
        if (this.mDialog == null) {
            this.mDialog = new AlertDialog.Builder(this.mContext).setIconAttribute(16843605).setTitle(R.string.usage_app_limit_alter_title).setMessage(R.string.usage_app_limit_alter_summary).setPositiveButton(R.string.screen_confirm, new DialogInterface.OnClickListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.7
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppTimeLimitSetHolder appTimeLimitSetHolder = AppTimeLimitSetHolder.this;
                    AppUsageController.suspendApp(appTimeLimitSetHolder.mContext, appTimeLimitSetHolder.mPkgName, true);
                    if (!z) {
                        AppTimeLimitSetHolder.this.timeSetFinish();
                        return;
                    }
                    AppTimeLimitSetHolder.this.vSwitch.setChecked(true);
                    AppTimeLimitSetHolder.this.updateAppState(true);
                }
            }).setNegativeButton(R.string.screen_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.6
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (z) {
                        AppTimeLimitSetHolder.this.vSwitch.setChecked(false);
                        return;
                    }
                    AppTimeLimitSetHolder appTimeLimitSetHolder = AppTimeLimitSetHolder.this;
                    boolean z2 = appTimeLimitSetHolder.isWeekDayToday;
                    AppTimeLimitSetHolder appTimeLimitSetHolder2 = AppTimeLimitSetHolder.this;
                    appTimeLimitSetHolder.todayLimitTime = z2 ? appTimeLimitSetHolder2.mWeekDayLimitTime : appTimeLimitSetHolder2.mWeekEndLimitTime;
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.5
                @Override // android.content.DialogInterface.OnCancelListener
                public void onCancel(DialogInterface dialogInterface) {
                    if (z) {
                        AppTimeLimitSetHolder.this.vSwitch.setChecked(false);
                        return;
                    }
                    AppTimeLimitSetHolder appTimeLimitSetHolder = AppTimeLimitSetHolder.this;
                    boolean z2 = appTimeLimitSetHolder.isWeekDayToday;
                    AppTimeLimitSetHolder appTimeLimitSetHolder2 = AppTimeLimitSetHolder.this;
                    appTimeLimitSetHolder.todayLimitTime = z2 ? appTimeLimitSetHolder2.mWeekDayLimitTime : appTimeLimitSetHolder2.mWeekEndLimitTime;
                }
            }).create();
        }
        this.mDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void timeSetFinish() {
        if (this.isSetWeekDay) {
            this.mWeekDayLimitTime = this.todayLimitTime;
        } else {
            this.mWeekEndLimitTime = this.todayLimitTime;
        }
        setTimeText();
        AppLimitStateUtils.setLimitTime(this.mContext, this.mPkgName, this.todayLimitTime, this.isSetWeekDay);
        if (this.isSetWeekDay == this.isWeekDayToday) {
            AppLimitStateUtils.register(this.mContext, this.mPkgName, this.todayLimitTime - this.mTodayUsageTime);
            this.hasUpdateData = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAppState(boolean z) {
        this.hasUpdateData = true;
        setViewState();
        if (z) {
            AppLimitStateUtils.openTimeLimit(this.mContext, this.mPkgName);
            AppLimitStateUtils.register(this.mContext, this.mPkgName, this.todayLimitTime - this.mTodayUsageTime);
            AppLimitStateUtils.registerCrossDayAlarm(this.mContext);
            return;
        }
        AppUsageController.removeUnregisterApp(this.mContext, this.mPkgName);
        AppLimitStateUtils.cancelTimeLimit(this.mContext, this.mPkgName);
        AppUsageController.unregisterAppUsageObserver(this.mContext, this.mPkgName);
        AppUsageController.suspendApp(this.mContext, this.mPkgName, false);
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    protected View inflateView() {
        return View.inflate(this.mContext, R.layout.usagestats_app_limit_item, null);
    }

    public void onDestroy() {
        onStop();
        AlertDialog alertDialog = this.mDialog;
        if (alertDialog == null || !alertDialog.isShowing()) {
            return;
        }
        this.mDialog.dismiss();
        this.mDialog = null;
    }

    public void onResume() {
        this.isWeekDayToday = DateUtils.isWeekdayToday();
        this.mWeekDayLimitTime = AppLimitStateUtils.getLimitTime(this.mContext, this.mPkgName, true);
        int limitTime = AppLimitStateUtils.getLimitTime(this.mContext, this.mPkgName, false);
        this.mWeekEndLimitTime = limitTime;
        if (this.isWeekDayToday) {
            limitTime = this.mWeekDayLimitTime;
        }
        this.todayLimitTime = limitTime;
        setViewState();
        setTimeText();
    }

    public void onStop() {
        if (this.hasUpdateData || !isFromTimeOver()) {
            return;
        }
        AppUsageController.suspendApp(this.mContext, this.mPkgName, true);
    }

    @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        if (i == 0 && i2 == 0) {
            Toast.makeText(this.mContext.getApplicationContext(), R.string.usage_state_set_invalid_time_toast, 0).show();
            return;
        }
        boolean isWeekdayToday = DateUtils.isWeekdayToday();
        int i3 = (i * 60) + i2;
        if (this.isSetWeekDay == isWeekdayToday) {
            if (i3 == (isWeekdayToday ? this.mWeekDayLimitTime : this.mWeekEndLimitTime)) {
                return;
            }
        }
        amendTime(isWeekdayToday);
        this.todayLimitTime = i3;
        if (this.isSetWeekDay != isWeekdayToday || i3 > this.mTodayUsageTime) {
            timeSetFinish();
        } else {
            showConfirmDialog(false);
        }
    }

    @Override // com.android.settings.usagestats.holder.BaseHolder
    public void renderView() {
        this.isSysApp = AppLimitStateUtils.UNABLE_LIMIT_APPS.contains(this.mPkgName);
        this.mDisableColor = AppInfoUtils.getColor(this.mContext, R.color.usage_stats_black30);
        this.mTitleEnableColor = AppInfoUtils.getColor(this.mContext, R.color.usage_state_black);
        this.mTimeEnableColor = AppInfoUtils.getColor(this.mContext, R.color.usage_stats_black60);
        this.vLine1 = findView(R.id.line1);
        this.vLine2 = findView(R.id.line2);
        this.vLine3 = findView(R.id.line3);
        this.vSwitchContainer = findView(R.id.rl_switch_container);
        this.vSwitch = (SlidingButton) findView(R.id.app_limit_switch);
        this.vWeekDay = findView(R.id.ll_weekday);
        this.vWeekEnd = findView(R.id.ll_weekend);
        this.vWeekDayTitle = (TextView) findView(R.id.tv_weekday_title);
        this.vWeekDayTime = (TextView) findView(R.id.tv_weekday_time);
        this.vWeekEndTitle = (TextView) findView(R.id.tv_weekend_title);
        this.vWeekEndTime = (TextView) findView(R.id.tv_weekend_time);
        this.vSwitchTitle = (TextView) findView(R.id.tv_limit_title);
        this.vSwitchSummary = (TextView) findView(R.id.ttv_limit_summary);
        if (this.isSysApp) {
            hideSetTimeItem();
        } else {
            setListener();
        }
    }

    public void setFromPager(String str) {
        this.mFromPager = str;
    }

    public void setListener() {
        this.vSwitch.setChecked(!this.isSysApp && AppLimitStateUtils.isOpenTimeLimit(this.mContext, this.mPkgName));
        this.vSwitchContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                AppTimeLimitSetHolder.this.ensureOpenLimit(!r0.vSwitch.isChecked());
            }
        });
        this.vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                AppTimeLimitSetHolder.this.ensureOpenLimit(z);
            }
        });
        this.vWeekDay.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AppTimeLimitSetHolder.this.vSwitch.isChecked()) {
                    AppTimeLimitSetHolder.this.isSetWeekDay = true;
                    AppTimeLimitSetHolder.this.mTimePickerDialog.updateTime(AppTimeLimitSetHolder.this.mWeekDayLimitTime / 60, AppTimeLimitSetHolder.this.mWeekDayLimitTime % 60);
                    AppTimeLimitSetHolder.this.mTimePickerDialog.show();
                }
            }
        });
        this.vWeekEnd.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.holder.AppTimeLimitSetHolder.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (AppTimeLimitSetHolder.this.vSwitch.isChecked()) {
                    AppTimeLimitSetHolder.this.isSetWeekDay = false;
                    AppTimeLimitSetHolder.this.mTimePickerDialog.updateTime(AppTimeLimitSetHolder.this.mWeekEndLimitTime / 60, AppTimeLimitSetHolder.this.mWeekEndLimitTime % 60);
                    AppTimeLimitSetHolder.this.mTimePickerDialog.show();
                }
            }
        });
    }

    public void setTodayUsageTime(int i) {
        this.mTodayUsageTime = i;
    }
}
