package com.android.settings.usagestats;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.BaseFragment;
import com.android.settings.R;
import com.android.settings.usagestats.controller.DeviceUsageController;
import com.android.settings.usagestats.utils.ControllerObserverUtil;
import com.android.settings.usagestats.utils.DateUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.util.HashMap;
import miuix.appcompat.app.TimePickerDialog;
import miuix.pickerwidget.widget.TimePicker;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class UsageStatsTimeSetFragment extends BaseFragment implements TimePickerDialog.OnTimeSetListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = UsageStatsTimeSetFragment.class.getSimpleName();
    private int initWeekDayTime;
    private int initWeekEndTime;
    private boolean isChecked;
    private boolean isSetWeekDay;
    private int mStartTime;
    private TimePickerDialog mTimePickerDialog;
    private Object object = new Object();
    private View vContainer1;
    private View vContainer2;
    private View vSetWeekDayTime;
    private View vSetWeekEndTime;
    private SlidingButton vSwitch;
    private View vSwitchContainer;
    private TextView vWeekDayTime;
    private TextView vWeekDayTitle;
    private TextView vWeekEndTime;
    private TextView vWeekEndTitle;

    private Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    private int getColor(int i) {
        return getActivity().getColor(i);
    }

    private String getName() {
        return UsageStatsTimeSetFragment.class.getName();
    }

    private void initData() {
        this.initWeekDayTime = DeviceUsageController.getLimitedTimeCommon(getApplicationContext(), true);
        int limitedTimeCommon = DeviceUsageController.getLimitedTimeCommon(getApplicationContext(), false);
        this.initWeekEndTime = limitedTimeCommon;
        if (this.initWeekDayTime == 0) {
            this.initWeekDayTime = 300;
        }
        if (limitedTimeCommon == 0) {
            this.initWeekEndTime = 480;
        }
        setTimeText(this.vWeekDayTime, this.initWeekDayTime);
        setTimeText(this.vWeekEndTime, this.initWeekEndTime);
        this.isChecked = DeviceUsageController.getMonitorStatus(getApplicationContext());
        setTextColor();
        this.vSwitch.setChecked(this.isChecked);
        this.vSwitch.setOnPerformCheckedChangeListener(this);
    }

    private void initListener() {
        this.vSetWeekEndTime.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.UsageStatsTimeSetFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UsageStatsTimeSetFragment.this.vSwitch.isChecked()) {
                    UsageStatsTimeSetFragment.this.isSetWeekDay = false;
                    UsageStatsTimeSetFragment.this.mTimePickerDialog.updateTime(UsageStatsTimeSetFragment.this.initWeekEndTime / 60, UsageStatsTimeSetFragment.this.initWeekEndTime % 60);
                    UsageStatsTimeSetFragment.this.mTimePickerDialog.show();
                }
            }
        });
        this.vSetWeekDayTime.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.UsageStatsTimeSetFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (UsageStatsTimeSetFragment.this.vSwitch.isChecked()) {
                    UsageStatsTimeSetFragment.this.isSetWeekDay = true;
                    UsageStatsTimeSetFragment.this.mTimePickerDialog.updateTime(UsageStatsTimeSetFragment.this.initWeekDayTime / 60, UsageStatsTimeSetFragment.this.initWeekDayTime % 60);
                    UsageStatsTimeSetFragment.this.mTimePickerDialog.show();
                }
            }
        });
        this.vSwitchContainer.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.usagestats.UsageStatsTimeSetFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                UsageStatsTimeSetFragment.this.isChecked = !r2.isChecked;
                UsageStatsTimeSetFragment.this.vSwitch.setChecked(UsageStatsTimeSetFragment.this.isChecked);
                UsageStatsTimeSetFragment usageStatsTimeSetFragment = UsageStatsTimeSetFragment.this;
                usageStatsTimeSetFragment.doCheckedStateChange(usageStatsTimeSetFragment.isChecked);
            }
        });
    }

    private void initView(View view) {
        this.vSwitch = (SlidingButton) view.findViewById(R.id.set_time);
        this.vContainer1 = view.findViewById(R.id.ll_normal_container);
        this.vContainer2 = view.findViewById(R.id.ll_week_container);
        this.vWeekEndTime = (TextView) view.findViewById(R.id.tv_week_day_time);
        this.vWeekDayTime = (TextView) view.findViewById(R.id.tv_normal_time);
        this.vSetWeekEndTime = view.findViewById(R.id.ll_week_day_time_set);
        this.vSetWeekDayTime = view.findViewById(R.id.ll_normal_day_time_set);
        this.vWeekDayTitle = (TextView) view.findViewById(R.id.tv_normal_time_title);
        this.vWeekEndTitle = (TextView) view.findViewById(R.id.tv_week_day_time_title);
        this.vSwitchContainer = view.findViewById(R.id.ll_switch);
        FragmentActivity activity = getActivity();
        int i = this.mStartTime;
        this.mTimePickerDialog = new TimePickerDialog(activity, this, i / 60, i % 60, true);
    }

    private void setTextColor() {
        this.vWeekDayTime.setEnabled(this.isChecked);
        this.vWeekEndTime.setEnabled(this.isChecked);
        this.vWeekDayTitle.setEnabled(this.isChecked);
        this.vWeekEndTitle.setEnabled(this.isChecked);
        this.vWeekEndTime.setTextColor(getColor(this.isChecked ? R.color.usage_stats_black60 : R.color.usage_stats_black30));
        this.vWeekDayTime.setTextColor(getColor(this.isChecked ? R.color.usage_stats_black60 : R.color.usage_stats_black30));
    }

    private void setTimeReport() {
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("weekday_limitTime", String.valueOf(this.initWeekDayTime));
            hashMap.put("weekend_limitTime", String.valueOf(this.initWeekEndTime));
            OneTrackInterfaceUtils.track("deviceMonitor_TimeLimit", hashMap);
        } catch (IllegalStateException unused) {
            Log.d(TAG, "setTimeReport: IllegalStateException occurs");
        }
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
            textView.setText(getResources().getQuantityString(R.plurals.usage_state_minute, i3, Integer.valueOf(i3)));
        } else if (i3 == 0) {
            textView.setText(getResources().getQuantityString(R.plurals.usage_state_hour, i2, Integer.valueOf(i2)));
        } else {
            textView.setText(getString(R.string.usage_state_hour_minute, Integer.valueOf(i2), Integer.valueOf(i3)));
        }
    }

    public void doCheckedStateChange(boolean z) {
        if (this.initWeekDayTime == 300 && DeviceUsageController.getLimitedTimeCommon(getApplicationContext(), true) == 0) {
            DeviceUsageController.setLimitedTimeCommon(getApplicationContext(), this.initWeekDayTime, true);
            DeviceUsageController.setLimitedTimeCommon(getApplicationContext(), this.initWeekEndTime, false);
        }
        DeviceUsageController.setTodayNotifyTime(getApplicationContext(), 0L);
        DeviceUsageController.setMonitorStatus(getApplicationContext(), z);
        if (z) {
            DeviceUsageController.startMonitor(getApplicationContext());
        } else {
            DeviceUsageController.stopMonitor(getApplicationContext());
        }
        setTextColor();
        ControllerObserverUtil.getInstance().notify(this.object);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.isChecked = z;
        doCheckedStateChange(z);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.usagestats_app_usage_time_set, viewGroup, false);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageEnd(getName());
        } catch (IllegalStateException unused) {
            Log.d(TAG, "onPause: IllegalStateException occurs ");
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (TextUtils.isEmpty(getName())) {
            return;
        }
        try {
            MiStatInterfaceUtils.trackPageStart(getName());
        } catch (IllegalStateException unused) {
            Log.d(TAG, "onResume: IllegalStateException occurs ");
        }
    }

    @Override // miuix.appcompat.app.TimePickerDialog.OnTimeSetListener
    public void onTimeSet(TimePicker timePicker, int i, int i2) {
        if (i == 0 && i2 == 0) {
            Toast.makeText(getApplicationContext(), R.string.usage_state_set_invalid_time_toast, 0).show();
            return;
        }
        if (this.isSetWeekDay) {
            this.initWeekDayTime = (i * 60) + i2;
            DeviceUsageController.setLimitedTimeCommon(getApplicationContext(), this.initWeekDayTime, true);
            setTimeText(this.vWeekDayTime, this.initWeekDayTime);
            if (DateUtils.isWeekdayToday()) {
                DeviceUsageController.setTodayNotifyTime(getApplicationContext(), 0L);
                DeviceUsageController.setLimitedTimeToday(getApplicationContext(), 0);
            }
        } else {
            this.initWeekEndTime = (i * 60) + i2;
            DeviceUsageController.setLimitedTimeCommon(getApplicationContext(), this.initWeekEndTime, false);
            setTimeText(this.vWeekEndTime, this.initWeekEndTime);
            if (!DateUtils.isWeekdayToday()) {
                DeviceUsageController.setTodayNotifyTime(getApplicationContext(), 0L);
                DeviceUsageController.setLimitedTimeToday(getApplicationContext(), 0);
            }
        }
        DeviceUsageController.startMonitor(getApplicationContext());
        ControllerObserverUtil.getInstance().notify(this.object);
        setTimeReport();
    }

    @Override // com.android.settings.BaseFragment, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        initView(view);
        initData();
        initListener();
    }
}
