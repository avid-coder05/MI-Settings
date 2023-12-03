package miui.keyguard.clock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import miui.date.DateUtils;
import miui.util.AccessibilityHapticUtils;

/* loaded from: classes3.dex */
public class MiuiLeftTopClock extends MiuiBaseClock {
    private TextView mTimeText;

    public MiuiLeftTopClock(Context context) {
        this(context, null);
    }

    public MiuiLeftTopClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.keyguard.clock.MiuiBaseClock, android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        TextView textView = (TextView) findViewById(R.id.current_time);
        this.mTimeText = textView;
        textView.setClickable(AccessibilityHapticUtils.isSupportAccessibilityHaptic(this.mContext));
        updateTime();
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z) {
        super.setTextColorDark(z);
        int color = z ? getContext().getResources().getColor(R.color.miui_common_time_dark_text_color) : -1;
        this.mTimeText.setTextColor(color);
        setInfoDarkMode(color);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        super.updateTime();
        this.mTimeText.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64));
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsLayoutParams() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = this.mHasTopMargin ? (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top)) : 0;
        layoutParams.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_left)));
        setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mCurrentDate.getLayoutParams();
        layoutParams2.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_date_info_top_margin));
        layoutParams2.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra)));
        this.mCurrentDate.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mLunarCalendarInfo.getLayoutParams();
        layoutParams3.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_clock_lunar_calendar_top_margin));
        layoutParams3.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra)));
        this.mLunarCalendarInfo.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mOwnerInfo.getLayoutParams();
        layoutParams4.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_clock_owner_info_top_margin));
        layoutParams4.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.left_top_clock_date_margin_extra)));
        this.mOwnerInfo.setLayoutParams(layoutParams4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.keyguard.clock.MiuiBaseClock
    public void updateViewsTextSize() {
        super.updateViewsTextSize();
        this.mTimeText.setTextSize(0, (int) (this.mScaleRatio * this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_left_top_clock_time_text_size)));
    }
}
