package com.android.settings.notification;

import android.app.ExtraNotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MiuiSettings;
import android.service.notification.ZenModeConfig;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;
import java.util.List;
import miui.vip.VipService;

/* loaded from: classes2.dex */
public class SilentModeSeekBarPreference extends Preference implements SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private H mHandler;
    private TextView mRemainTime;
    private miuix.androidbasewidget.widget.SeekBar mSeekBar;
    private TextView mSelectedText;
    private RelativeLayout mTimeLabel;
    private List<TextView> mTimeList;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public final class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            SilentModeSeekBarPreference.this.updateRemainTimeSeekbar();
        }
    }

    public SilentModeSeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SilentModeSeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SilentModeSeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    private int getProgressLevel(int i) {
        if (i <= 12) {
            return 0;
        }
        if (Math.abs(i - 25) <= 12) {
            return 1;
        }
        if (Math.abs(i - 50) <= 12) {
            return 2;
        }
        return Math.abs(i + (-75)) <= 12 ? 3 : 4;
    }

    private float getXPosition(miuix.androidbasewidget.widget.SeekBar seekBar) {
        return (seekBar.getThumb().getBounds().centerX() - (this.mRemainTime.getPaint().measureText(this.mRemainTime.getText().toString()) / 2.0f)) + ((LinearLayout.LayoutParams) seekBar.getLayoutParams()).leftMargin;
    }

    private int progressToMinute(int i) {
        if (i <= 50) {
            return (i / 25) * 30;
        }
        if (i <= 75) {
            return 120;
        }
        return i <= 100 ? 480 : 0;
    }

    private void setInitStatus() {
        ZenModeConfig.ZenRule zenRule = ExtraNotificationManager.getZenModeConfig(this.mContext).manualRule;
        long tryParseCountdownConditionId = zenRule != null ? ZenModeConfig.tryParseCountdownConditionId(zenRule.conditionId) - System.currentTimeMillis() : 0L;
        if (tryParseCountdownConditionId > 0) {
            this.mTimeLabel.setVisibility(8);
            this.mRemainTime.setVisibility(0);
            this.mSeekBar.setProgress(timeToProgress(tryParseCountdownConditionId / 1000));
            return;
        }
        this.mTimeLabel.setVisibility(0);
        this.mRemainTime.setVisibility(8);
        this.mSeekBar.setProgress(0);
    }

    private int timeToProgress(long j) {
        long j2;
        long j3;
        long j4;
        if (j <= 3600) {
            j2 = j / 72;
        } else {
            if (j <= 7200) {
                j3 = (j - 3600) / 144;
                j4 = 50;
            } else if (j <= 28800) {
                j3 = (j - 3600) / 864;
                j4 = 75;
            } else {
                j2 = 0;
            }
            j2 = j3 + j4;
        }
        return (int) j2;
    }

    private String turnMillSecondsToHour(long j) {
        StringBuilder sb = new StringBuilder();
        int i = (int) (j / 3600000);
        int i2 = (int) (j % 3600000);
        int i3 = i2 / 60000;
        int i4 = (i2 % 60000) / VipService.VIP_SERVICE_FAILURE;
        if (i > 0) {
            if (i < 10) {
                sb.append("0");
            }
            sb.append(i);
            sb.append(":");
        }
        if (i3 < 10) {
            sb.append("0");
        }
        sb.append(i3);
        sb.append(":");
        if (i4 < 10) {
            sb.append("0");
        }
        sb.append(i4);
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRemainTimeSeekbar() {
        ZenModeConfig.ZenRule zenRule = ExtraNotificationManager.getZenModeConfig(this.mContext).manualRule;
        long tryParseCountdownConditionId = zenRule != null ? ZenModeConfig.tryParseCountdownConditionId(zenRule.conditionId) - System.currentTimeMillis() : 0L;
        if (tryParseCountdownConditionId <= 0) {
            this.mTimeLabel.setVisibility(0);
            this.mRemainTime.setVisibility(8);
            this.mSeekBar.setProgress(0);
            return;
        }
        this.mTimeLabel.setVisibility(8);
        this.mRemainTime.setVisibility(0);
        this.mSeekBar.setProgress(timeToProgress(tryParseCountdownConditionId / 1000));
        this.mRemainTime.setText(turnMillSecondsToHour(tryParseCountdownConditionId));
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) this.mRemainTime.getLayoutParams();
        layoutParams.leftMargin = (int) getXPosition(this.mSeekBar);
        this.mRemainTime.setLayoutParams(layoutParams);
        this.mHandler.removeMessages(1);
        H h = this.mHandler;
        h.sendMessageDelayed(h.obtainMessage(1), 1000L);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.mContext = getContext();
        this.mSeekBar = (miuix.androidbasewidget.widget.SeekBar) view.findViewById(R.id.time_count_seekbar);
        this.mTimeLabel = (RelativeLayout) view.findViewById(R.id.time_label);
        this.mRemainTime = (TextView) view.findViewById(R.id.remain_time);
        ArrayList arrayList = new ArrayList();
        this.mTimeList = arrayList;
        arrayList.add((TextView) view.findViewById(R.id.always));
        this.mTimeList.add((TextView) view.findViewById(R.id.onehour));
        this.mTimeList.add((TextView) view.findViewById(R.id.twohours));
        this.mTimeList.add((TextView) view.findViewById(R.id.fourhours));
        this.mTimeList.add((TextView) view.findViewById(R.id.eighthours));
        TextView textView = this.mTimeList.get(0);
        this.mSelectedText = textView;
        textView.setTextColor(getContext().getResources().getColor(R.color.time_selected));
        this.mSelectedText.setTextSize(2, 12.0f);
        setInitStatus();
        this.mHandler = new H(Looper.getMainLooper());
        this.mSeekBar.setOnSeekBarChangeListener(this);
        this.mHandler.sendEmptyMessageDelayed(1, 50L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.silent_mode_count_down_bar);
        return super.onCreateView(viewGroup);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (this.mTimeLabel.getVisibility() == 0) {
            int progressLevel = getProgressLevel(i);
            if (this.mTimeList.get(progressLevel).equals(this.mSelectedText)) {
                return;
            }
            this.mSelectedText.setTextSize(2, 10.0f);
            this.mSelectedText.setTextColor(getContext().getResources().getColor(R.color.time_not_selected));
            TextView textView = this.mTimeList.get(progressLevel);
            this.mSelectedText = textView;
            textView.setTextSize(2, 12.0f);
            this.mSelectedText.setTextColor(getContext().getResources().getColor(R.color.time_selected));
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mTimeLabel.setVisibility(0);
        this.mRemainTime.setVisibility(8);
        this.mHandler.removeMessages(1);
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progressLevel = getProgressLevel(seekBar.getProgress()) * 25;
        seekBar.setProgress(progressLevel);
        int progressToMinute = progressToMinute(progressLevel);
        int zenMode = MiuiSettings.SilenceMode.getZenMode(this.mContext);
        this.mHandler.removeMessages(1);
        ExtraNotificationManager.startCountDownSilenceMode(this.mContext, zenMode, progressToMinute);
        if (seekBar.getProgress() > 0) {
            this.mHandler.sendEmptyMessageDelayed(1, 50L);
        }
    }
}
