package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import miui.date.DateUtils;
import miui.util.AccessibilityHapticUtils;

/* loaded from: classes3.dex */
public class MiuiLeftTopLargeClock extends MiuiBaseClock {
    private TextView mCurrentDateLarge;
    private FrameLayout mDateContainer;
    private float mDefaultSpaceExtra;
    private float mDefaultSpaceMultiplier;
    private TextView mTimeText;

    public MiuiLeftTopLargeClock(Context context) {
        this(context, null);
    }

    public MiuiLeftTopLargeClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private void callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        try {
            obj.getClass().getDeclaredMethod(str, clsArr).invoke(obj, objArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.mDateContainer = (FrameLayout) findViewById(R.id.left_top_date_container);
        TextView textView2 = (TextView) findViewById(R.id.current_date_large);
        this.mCurrentDateLarge = textView2;
        this.mDefaultSpaceExtra = textView2.getLineSpacingExtra();
        this.mDefaultSpaceMultiplier = this.mCurrentDateLarge.getLineSpacingMultiplier();
        updateTime();
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void onLanguageChanged(String str) {
        Typeface create;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.mDateContainer.getLayoutParams();
        if ((Build.VERSION.SDK_INT < 30 || !"bo".equals(str)) && !"ug".equals(str)) {
            this.mCurrentDateLarge.setTypeface(Typeface.create("miclock-thin", 0));
            layoutParams.bottomMargin = 0;
            layoutParams.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_top_margin));
            this.mCurrentDateLarge.setLineSpacing(0.0f, 0.7f);
            this.mCurrentDateLarge.setAlpha(0.65f);
            this.mCurrentDateLarge.setIncludeFontPadding(true);
            callObjectMethod(this.mCurrentDateLarge, "setLetterSpacing", new Class[]{Float.TYPE}, Float.valueOf(-0.02f));
        } else {
            if ("bo".equals(str)) {
                layoutParams.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_top_margin_bo));
                layoutParams.bottomMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_bottom_margin));
                this.mCurrentDateLarge.setAlpha(0.65f);
                create = Typeface.create("miclock-thin-tibetan", 0);
            } else {
                layoutParams.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_top_margin_ug));
                layoutParams.bottomMargin = 0;
                this.mCurrentDateLarge.setAlpha(1.0f);
                create = Typeface.create("miclock-thin-ug", 0);
            }
            this.mCurrentDateLarge.setLineSpacing(this.mDefaultSpaceExtra, this.mDefaultSpaceMultiplier);
            this.mCurrentDateLarge.setTypeface(create);
            this.mCurrentDateLarge.setIncludeFontPadding(false);
            callObjectMethod(this.mCurrentDateLarge, "setLetterSpacing", new Class[]{Float.TYPE}, Float.valueOf(0.0f));
        }
        this.mDateContainer.setLayoutParams(layoutParams);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z) {
        int i = z ? -16777216 : -1;
        this.mTimeText.setTextColor(i);
        this.mCurrentDateLarge.setTextColor(i);
        setInfoDarkMode(i);
        this.mOwnerInfo.setTextColor(z ? getContext().getResources().getColor(R.color.miui_owner_info_dark_text_color) : -1);
    }

    @Override // miui.keyguard.clock.MiuiBaseClock, miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        super.updateTime();
        this.mTimeText.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64));
        this.mCurrentDateLarge.setText(this.mCalendar.format(this.mContext.getString(this.m24HourFormat ? R.string.miui_lock_screen_large_date : R.string.miui_lock_screen_large_date_12)).toUpperCase());
    }

    @Override // miui.keyguard.clock.MiuiBaseClock
    protected void updateViewsLayoutParams() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = this.mHasTopMargin ? (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_top)) : 0;
        layoutParams.setMarginStart((int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_clock_margin_left)));
        setLayoutParams(layoutParams);
        String language = this.mContext.getResources().getConfiguration().locale.getLanguage();
        if (language.equals(this.mLanguage)) {
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mDateContainer.getLayoutParams();
            layoutParams2.topMargin = (int) (this.mScaleRatio * this.mResources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_info_top_margin));
            this.mDateContainer.setLayoutParams(layoutParams2);
        } else {
            onLanguageChanged(language);
        }
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
        Resources resources = this.mContext.getResources();
        this.mTimeText.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_left_top_clock_time_text_size)));
        this.mCurrentDateLarge.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_left_top_large_clock_date_text_size)));
    }
}
