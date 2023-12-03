package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import miui.keyguard.clock.KeyguardClockController;

/* loaded from: classes3.dex */
public class MiuiBaseClock extends LinearLayout implements KeyguardClockController.IClockView {
    protected boolean m24HourFormat;
    protected Calendar mCalendar;
    private int mCalendarDayOfWeek;
    protected Context mContext;
    protected TextView mCurrentDate;
    protected int mDensityDpi;
    protected float mFontScale;
    protected boolean mFontScaleChanged;
    protected boolean mHasTopMargin;
    protected String mLanguage;
    protected TextView mLunarCalendarInfo;
    protected TextView mOwnerInfo;
    protected Resources mResources;
    protected float mScaleRatio;
    private boolean mShowLunarCalendar;
    protected boolean mTextDark;
    protected int mUserId;

    public MiuiBaseClock(Context context) {
        this(context, null);
    }

    public MiuiBaseClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = null;
        this.mResources = null;
        this.mTextDark = false;
        this.mHasTopMargin = true;
        this.mScaleRatio = 1.0f;
        this.mContext = context;
        this.mResources = context.getResources();
        updateHourFormat();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public int getClockHeight() {
        if (getHeight() > 0) {
            return getHeight();
        }
        return 0;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getClockVisibleHeight() {
        if (getHeight() > 0) {
            return getHeight();
        }
        return 0.0f;
    }

    public View getLunarCalendarView() {
        return this.mLunarCalendarInfo;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return 0.0f;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateViewsLayoutParams();
        updateViewsTextSize();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        float f = configuration.fontScale;
        if (this.mFontScale != f) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            this.mFontScale = f;
        }
        int i = configuration.densityDpi;
        if (this.mDensityDpi != i) {
            this.mFontScaleChanged = true;
            updateViewsTextSize();
            updateViewsLayoutParams();
            this.mDensityDpi = i;
        }
        String language = configuration.locale.getLanguage();
        if (TextUtils.isEmpty(language) || language.equals(this.mLanguage)) {
            return;
        }
        this.mLanguage = language;
        onLanguageChanged(language);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mCurrentDate = (TextView) findViewById(R.id.current_date);
        this.mLunarCalendarInfo = (TextView) findViewById(R.id.unlock_screen_lunar_calendar_info);
        this.mOwnerInfo = (TextView) findViewById(R.id.unlock_screen_owner_info);
        this.mCalendar = new Calendar();
        updateLunarCalendarInfo();
    }

    protected void onLanguageChanged(String str) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setClockAlpha(float f) {
        setAlpha(f);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setInfoDarkMode(int i) {
        this.mCurrentDate.setTextColor(i);
        this.mLunarCalendarInfo.setTextColor(i);
    }

    public void setIs24HourFormat(boolean z) {
        this.m24HourFormat = z;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setOwnerInfo(String str) {
        if (TextUtils.isEmpty(str)) {
            this.mOwnerInfo.setVisibility(8);
            return;
        }
        this.mOwnerInfo.setVisibility(0);
        this.mOwnerInfo.setText(str);
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setScaleRatio(float f) {
        this.mScaleRatio = f;
        updateViewsTextSize();
        try {
            updateViewsLayoutParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setShowLunarCalendar(boolean z) {
        this.mShowLunarCalendar = z;
        updateLunarCalendarInfo();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z) {
        this.mOwnerInfo.setTextColor(z ? getContext().getResources().getColor(R.color.miui_owner_info_dark_text_color) : getContext().getResources().getColor(R.color.miui_owner_info_light_text_color));
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    public void updateLunarCalendarInfo() {
        if (!Locale.CHINESE.getLanguage().equals(Locale.getDefault().getLanguage()) || !this.mShowLunarCalendar) {
            this.mLunarCalendarInfo.setVisibility(8);
            return;
        }
        Calendar calendar = new Calendar();
        this.mLunarCalendarInfo.setVisibility(0);
        this.mLunarCalendarInfo.setText(calendar.format("YY年 N月e"));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateResidentTimeZone(String str) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        this.mCurrentDate.setText(this.mCalendar.format(this.mContext.getString(this.m24HourFormat ? R.string.miui_lock_screen_date : R.string.miui_lock_screen_date_12)));
        int i = this.mCalendar.get(14);
        if (i != this.mCalendarDayOfWeek) {
            updateLunarCalendarInfo();
            this.mCalendarDayOfWeek = i;
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mCalendar = new Calendar(TimeZone.getTimeZone(str));
        updateTime();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateViewTopMargin(boolean z) {
        this.mHasTopMargin = z;
        try {
            updateViewsLayoutParams();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updateViewsLayoutParams() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        float dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_clock_date_text_size));
        this.mCurrentDate.setTextSize(0, dimensionPixelSize);
        this.mLunarCalendarInfo.setTextSize(0, dimensionPixelSize);
        this.mOwnerInfo.setTextSize(0, (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_clock_date_text_size)));
    }
}
