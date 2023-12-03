package miui.keyguard.clock;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miui.system.internal.R;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import miui.date.Calendar;
import miui.date.DateUtils;
import miui.keyguard.clock.KeyguardClockController;
import miui.util.AccessibilityHapticUtils;

/* loaded from: classes3.dex */
public class MiuiDualClock extends RelativeLayout implements KeyguardClockController.IClockView {
    private static final String TAG = "MiuiDualClock";
    private static final String WEATHER_URI = "content://weather/realtimeLocalWeatherData/4/1";
    private boolean m24HourFormat;
    private boolean mAttached;
    private boolean mAutoTimeZone;
    ContentObserver mAutoTimeZoneObserver;
    private Calendar mCalendar;
    private Context mContext;
    private String mCountry;
    private boolean mDateTooLong;
    private String mLanguage;
    private TextView mLocalCity;
    private OnLocalCityChangeListener mLocalCityChangeListener;
    private TextView mLocalDate;
    private TextView mLocalTime;
    private String mLocalTimeZone;
    private Calendar mResidentCalendar;
    private TextView mResidentCity;
    private TextView mResidentDate;
    private LinearLayout mResidentLayout;
    private TextView mResidentTime;
    private String mResidentTimeZone;
    protected float mScaleRatio;

    /* loaded from: classes3.dex */
    public interface OnLocalCityChangeListener {
        void onLocalCityChanged(String str);
    }

    public MiuiDualClock(Context context) {
        this(context, null);
    }

    public MiuiDualClock(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mLanguage = "";
        this.mCountry = Locale.getDefault().getCountry();
        this.mAutoTimeZone = true;
        this.mDateTooLong = false;
        this.mScaleRatio = 1.0f;
        this.mAutoTimeZoneObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.MiuiDualClock.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                super.onChange(z);
                MiuiDualClock miuiDualClock = MiuiDualClock.this;
                miuiDualClock.mAutoTimeZone = Settings.Global.getInt(miuiDualClock.mContext.getContentResolver(), "auto_time_zone", 0) > 0;
                MiuiDualClock.this.updateLocalCity();
            }
        };
        this.mContext = context;
        this.mAutoTimeZone = Settings.Global.getInt(context.getContentResolver(), "auto_time_zone", 0) > 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private String getNamebyZone(String str) {
        try {
            Class<?> cls = Class.forName("android.icu.text.TimeZoneNames");
            Method declaredMethod = cls.getDeclaredMethod("getInstance", Locale.class);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(cls, Locale.getDefault());
            if (str == null || !str.equals("Asia/Shanghai")) {
                Method declaredMethod2 = cls.getDeclaredMethod("getExemplarLocationName", String.class);
                declaredMethod2.setAccessible(true);
                return (String) declaredMethod2.invoke(invoke, str);
            }
            Class<?> cls2 = Class.forName("android.icu.text.TimeZoneNames$NameType");
            Object obj = new Object();
            for (Object[] objArr : cls2.getEnumConstants()) {
                if (objArr.toString().equalsIgnoreCase("LONG_STANDARD")) {
                    obj = objArr;
                }
            }
            Method declaredMethod3 = cls.getDeclaredMethod("getDisplayName", String.class, cls2, Long.TYPE);
            declaredMethod3.setAccessible(true);
            return (String) declaredMethod3.invoke(invoke, "Asia/Shanghai", obj, Long.valueOf(new Date().getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return this.mContext.getString(R.string.miui_clock_city_name_second);
        }
    }

    private void updateDateLines() {
        boolean z = ((float) (((int) this.mLocalDate.getPaint().measureText(this.mLocalDate.getText().toString())) + ((int) this.mResidentDate.getPaint().measureText(this.mResidentDate.getText().toString())))) > getResources().getDimension(R.dimen.miui_dual_clock_max_width) * 2.0f;
        if (z != this.mDateTooLong) {
            this.mDateTooLong = z;
            updateTime();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateLocalCity() {
        if (this.mAutoTimeZone) {
            new AsyncTask<Void, Void, String>() { // from class: miui.keyguard.clock.MiuiDualClock.2
                /* JADX INFO: Access modifiers changed from: protected */
                /* JADX WARN: Code restructure failed: missing block: B:15:0x0053, code lost:
                
                    if (r1 == null) goto L17;
                 */
                /* JADX WARN: Code restructure failed: missing block: B:17:0x0056, code lost:
                
                    return r0;
                 */
                @Override // android.os.AsyncTask
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct add '--show-bad-code' argument
                */
                public java.lang.String doInBackground(java.lang.Void... r9) {
                    /*
                        r8 = this;
                        java.lang.String r9 = "MiuiDualClock"
                        java.lang.String r0 = ""
                        r1 = 0
                        miui.keyguard.clock.MiuiDualClock r8 = miui.keyguard.clock.MiuiDualClock.this     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        android.content.Context r8 = miui.keyguard.clock.MiuiDualClock.access$100(r8)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        android.content.ContentResolver r2 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        java.lang.String r8 = "content://weather/realtimeLocalWeatherData/4/1"
                        android.net.Uri r3 = android.net.Uri.parse(r8)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        r4 = 0
                        r5 = 0
                        r6 = 0
                        r7 = 0
                        android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        if (r1 == 0) goto L30
                    L1f:
                        boolean r8 = r1.moveToNext()     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        if (r8 == 0) goto L30
                        java.lang.String r8 = "city_name"
                        int r8 = r1.getColumnIndex(r8)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        java.lang.String r0 = r1.getString(r8)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        goto L1f
                    L30:
                        java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        r8.<init>()     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        java.lang.String r2 = "update local city name, city="
                        r8.append(r2)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        r8.append(r0)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        java.lang.String r8 = r8.toString()     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        android.util.Log.i(r9, r8)     // Catch: java.lang.Throwable -> L4b java.lang.Exception -> L4d
                        if (r1 == 0) goto L56
                    L47:
                        r1.close()
                        goto L56
                    L4b:
                        r8 = move-exception
                        goto L57
                    L4d:
                        r8 = move-exception
                        java.lang.String r2 = "get city exception"
                        android.util.Log.e(r9, r2, r8)     // Catch: java.lang.Throwable -> L4b
                        if (r1 == 0) goto L56
                        goto L47
                    L56:
                        return r0
                    L57:
                        if (r1 == 0) goto L5c
                        r1.close()
                    L5c:
                        throw r8
                    */
                    throw new UnsupportedOperationException("Method not decompiled: miui.keyguard.clock.MiuiDualClock.AnonymousClass2.doInBackground(java.lang.Void[]):java.lang.String");
                }

                /* JADX INFO: Access modifiers changed from: protected */
                @Override // android.os.AsyncTask
                public void onPostExecute(String str) {
                    if (TextUtils.isEmpty(str)) {
                        str = MiuiDualClock.this.mContext.getString(R.string.miui_clock_city_name_local);
                    }
                    MiuiDualClock.this.mLocalCity.setText(str);
                    if (MiuiDualClock.this.mLocalCityChangeListener != null) {
                        MiuiDualClock.this.mLocalCityChangeListener.onLocalCityChanged(str);
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
            return;
        }
        String namebyZone = getNamebyZone(this.mLocalTimeZone);
        this.mLocalCity.setText(namebyZone);
        OnLocalCityChangeListener onLocalCityChangeListener = this.mLocalCityChangeListener;
        if (onLocalCityChangeListener != null) {
            onLocalCityChangeListener.onLocalCityChanged(namebyZone);
        }
    }

    private void updateResidentCityName() {
        this.mResidentCity.setText(getNamebyZone(this.mResidentTimeZone));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public int getClockHeight() {
        return getHeight();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getClockVisibleHeight() {
        return getHeight();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public float getTopMargin() {
        return this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mAttached) {
            return;
        }
        this.mAttached = true;
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("auto_time_zone"), false, this.mAutoTimeZoneObserver);
        this.mAutoTimeZoneObserver.onChange(false);
        updateViewsLayoutParams();
        updateViewsTextSize();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        String language = configuration.locale.getLanguage();
        String country = configuration.locale.getCountry();
        if ((language == null || language.equals(this.mLanguage)) && (country == null || country.equals(this.mCountry))) {
            return;
        }
        updateResidentCityName();
        updateLocalCity();
        this.mDateTooLong = false;
        updateTime();
        updateDateLines();
        this.mLanguage = language;
        this.mCountry = country;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mAttached) {
            this.mAttached = false;
            this.mContext.getContentResolver().unregisterContentObserver(this.mAutoTimeZoneObserver);
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mLocalCity = (TextView) findViewById(R.id.local_city_name);
        TextView textView = (TextView) findViewById(R.id.local_time);
        this.mLocalTime = textView;
        textView.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mLocalDate = (TextView) findViewById(R.id.local_date);
        this.mResidentCity = (TextView) findViewById(R.id.resident_city_name);
        TextView textView2 = (TextView) findViewById(R.id.resident_time);
        this.mResidentTime = textView2;
        textView2.setAccessibilityDelegate(new MiuiClockAccessibilityDelegate(this.mContext));
        this.mResidentDate = (TextView) findViewById(R.id.resident_date);
        this.mResidentLayout = (LinearLayout) findViewById(R.id.resident_time_layout);
        boolean isSupportAccessibilityHaptic = AccessibilityHapticUtils.isSupportAccessibilityHaptic(this.mContext);
        this.mLocalTime.setClickable(isSupportAccessibilityHaptic);
        this.mResidentTime.setClickable(isSupportAccessibilityHaptic);
        this.mLanguage = this.mContext.getResources().getConfiguration().locale.getLanguage();
        TimeZone timeZone = TimeZone.getDefault();
        this.mLocalTimeZone = timeZone.getID();
        updateLocalCity();
        this.mCalendar = new Calendar();
        if (TextUtils.isEmpty(this.mResidentTimeZone)) {
            this.mResidentTimeZone = timeZone.getID();
        }
        updateResidentCityName();
        this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
        updateHourFormat();
        updateTime();
        updateDateLines();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setClockAlpha(float f) {
        setAlpha(f);
    }

    public void setIs24HourFormat(boolean z) {
        this.m24HourFormat = z;
    }

    public void setOnLocalCityChangeListener(OnLocalCityChangeListener onLocalCityChangeListener) {
        this.mLocalCityChangeListener = onLocalCityChangeListener;
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setOwnerInfo(String str) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setScaleRatio(float f) {
        this.mScaleRatio = f;
        updateViewsTextSize();
        updateViewsLayoutParams();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setShowLunarCalendar(boolean z) {
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void setTextColorDark(boolean z) {
        int color = z ? getContext().getResources().getColor(R.color.miui_common_time_dark_text_color) : -1;
        this.mLocalCity.setTextColor(color);
        this.mLocalTime.setTextColor(color);
        this.mLocalDate.setTextColor(color);
        this.mResidentCity.setTextColor(color);
        this.mResidentTime.setTextColor(color);
        this.mResidentDate.setTextColor(color);
    }

    public void updateHourFormat() {
        this.m24HourFormat = DateFormat.is24HourFormat(this.mContext);
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateResidentTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mResidentTimeZone = str;
        Log.i(TAG, "update resident timeZone:" + this.mResidentTimeZone);
        this.mResidentCalendar = new Calendar(TimeZone.getTimeZone(this.mResidentTimeZone));
        updateTime();
        updateResidentCityName();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateSecondClockVisibility(int i) {
        LinearLayout linearLayout = this.mResidentLayout;
        if (linearLayout != null) {
            linearLayout.setVisibility(i);
        }
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTime() {
        updateTime(this.mCalendar, this.mLocalTime, this.mLocalDate);
        updateTime(this.mResidentCalendar, this.mResidentTime, this.mResidentDate);
    }

    public void updateTime(Calendar calendar, TextView textView, TextView textView2) {
        calendar.setTimeInMillis(System.currentTimeMillis());
        textView.setText(DateUtils.formatDateTime(System.currentTimeMillis(), (this.m24HourFormat ? 32 : 16) | 12 | 64, calendar.getTimeZone()));
        textView2.setText(calendar.format(this.mContext.getString(this.mDateTooLong ? this.m24HourFormat ? R.string.miui_lock_screen_date_two_lines : R.string.miui_lock_screen_date_two_lines_12 : this.m24HourFormat ? R.string.miui_lock_screen_date : R.string.miui_lock_screen_date_12)));
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateTimeZone(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.mLocalTimeZone = str;
        Log.i(TAG, "update local timeZone:" + this.mLocalTimeZone);
        this.mCalendar = new Calendar(TimeZone.getTimeZone(this.mLocalTimeZone));
        updateTime();
        updateLocalCity();
    }

    @Override // miui.keyguard.clock.KeyguardClockController.IClockView
    public void updateViewTopMargin(boolean z) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        if (z) {
            layoutParams.topMargin = (int) (this.mScaleRatio * this.mContext.getResources().getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top));
        } else {
            layoutParams.topMargin = 0;
        }
        setLayoutParams(layoutParams);
    }

    protected void updateViewsLayoutParams() {
        Resources resources = this.mContext.getResources();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
        layoutParams.topMargin = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_dual_clock_margin_top));
        setLayoutParams(layoutParams);
        int dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_dual_clock_time_margin_top));
        int dimensionPixelSize2 = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_dual_clock_date_margin_top));
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.mLocalTime.getLayoutParams();
        layoutParams2.topMargin = dimensionPixelSize;
        this.mLocalTime.setLayoutParams(layoutParams2);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.mLocalDate.getLayoutParams();
        layoutParams3.topMargin = dimensionPixelSize2;
        this.mLocalDate.setLayoutParams(layoutParams3);
        LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.mResidentTime.getLayoutParams();
        layoutParams4.topMargin = dimensionPixelSize;
        this.mResidentTime.setLayoutParams(layoutParams4);
        LinearLayout.LayoutParams layoutParams5 = (LinearLayout.LayoutParams) this.mResidentDate.getLayoutParams();
        layoutParams5.topMargin = dimensionPixelSize2;
        this.mResidentDate.setLayoutParams(layoutParams5);
        RelativeLayout.LayoutParams layoutParams6 = (RelativeLayout.LayoutParams) this.mResidentLayout.getLayoutParams();
        layoutParams6.setMarginStart((int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_resident_time_margin_start)));
        this.mResidentLayout.setLayoutParams(layoutParams6);
    }

    protected void updateViewsTextSize() {
        Resources resources = this.mContext.getResources();
        float dimensionPixelSize = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_dual_clock_city_text_size));
        this.mLocalCity.setTextSize(0, dimensionPixelSize);
        this.mResidentCity.setTextSize(0, dimensionPixelSize);
        this.mLocalDate.setTextSize(0, dimensionPixelSize);
        this.mResidentDate.setTextSize(0, dimensionPixelSize);
        float dimensionPixelSize2 = (int) (this.mScaleRatio * resources.getDimensionPixelSize(R.dimen.miui_dual_clock_time_text_size));
        this.mLocalTime.setTextSize(0, dimensionPixelSize2);
        this.mResidentTime.setTextSize(0, dimensionPixelSize2);
    }
}
