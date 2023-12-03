package com.miui.maml.data;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import com.miui.maml.NotifierManager;
import miui.date.Calendar;

/* loaded from: classes2.dex */
public class DateTimeVariableUpdater extends NotifierVariableUpdater {
    private static final int[] fields = {22, 21, 20, 18, 9};
    private static Calendar sCalendar;
    private IndexedVariable mAmPm;
    protected Calendar mCalendar;
    private long mCurrentTime;
    private IndexedVariable mDate;
    private IndexedVariable mDateLunar;
    private IndexedVariable mDayOfWeek;
    private volatile boolean mFinished;
    private IndexedVariable mHour12;
    private IndexedVariable mHour24;
    private long mLastUpdatedTime;
    private final Object mLock;
    private IndexedVariable mMinute;
    private IndexedVariable mMonth;
    private IndexedVariable mMonth1;
    private IndexedVariable mMonthLunar;
    private IndexedVariable mMonthLunarLeap;
    private IndexedVariable mNextAlarm;
    private long mNextUpdateTime;
    private IndexedVariable mSecond;
    private IndexedVariable mTime;
    private long mTimeAccuracy;
    private int mTimeAccuracyField;
    private int mTimeFormat;
    private IndexedVariable mTimeFormatVar;
    private IndexedVariable mTimeSys;
    private Runnable mTimeUpdater;
    private IndexedVariable mYear;
    private IndexedVariable mYearLunar;
    private IndexedVariable mYearLunar1864;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.DateTimeVariableUpdater$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy;

        static {
            int[] iArr = new int[Accuracy.values().length];
            $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy = iArr;
            try {
                iArr[Accuracy.Day.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy[Accuracy.Hour.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy[Accuracy.Minute.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy[Accuracy.Second.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* loaded from: classes2.dex */
    public enum Accuracy {
        Day,
        Hour,
        Minute,
        Second
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        this(variableUpdaterManager, Accuracy.Minute);
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager, Accuracy accuracy) {
        super(variableUpdaterManager, NotifierManager.TYPE_TIME_CHANGED);
        this.mCalendar = new Calendar();
        this.mTimeFormat = -1;
        this.mLock = new Object();
        initInner(accuracy);
    }

    public DateTimeVariableUpdater(VariableUpdaterManager variableUpdaterManager, String str) {
        super(variableUpdaterManager, NotifierManager.TYPE_TIME_CHANGED);
        this.mCalendar = new Calendar();
        this.mTimeFormat = -1;
        this.mLock = new Object();
        this.mTimeUpdater = new TimeUpdater(this);
        Accuracy accuracy = null;
        if (!TextUtils.isEmpty(str)) {
            for (Accuracy accuracy2 : Accuracy.values()) {
                if (accuracy2.name().equals(str)) {
                    accuracy = accuracy2;
                }
            }
        }
        if (accuracy == null) {
            accuracy = Accuracy.Minute;
            Log.w("DateTimeVariableUpdater", "invalid accuracy tag:" + str);
        }
        initInner(accuracy);
    }

    public static String formatDate(CharSequence charSequence, long j) {
        if (sCalendar == null) {
            sCalendar = new Calendar();
        }
        sCalendar.setTimeInMillis(j);
        return sCalendar.format(charSequence);
    }

    private void initInner(Accuracy accuracy) {
        Log.i("DateTimeVariableUpdater", "init with accuracy:" + accuracy.name());
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$DateTimeVariableUpdater$Accuracy[accuracy.ordinal()];
        if (i == 1) {
            this.mTimeAccuracy = 86400000L;
            this.mTimeAccuracyField = 9;
        } else if (i == 2) {
            this.mTimeAccuracy = 3600000L;
            this.mTimeAccuracyField = 18;
        } else if (i == 3) {
            this.mTimeAccuracy = 60000L;
            this.mTimeAccuracyField = 20;
        } else if (i != 4) {
            this.mTimeAccuracy = 60000L;
            this.mTimeAccuracyField = 20;
        } else {
            this.mTimeAccuracy = 1000L;
            this.mTimeAccuracyField = 21;
        }
        Variables variables = getContext().mVariables;
        this.mYear = new IndexedVariable("year", variables, true);
        this.mMonth = new IndexedVariable("month", variables, true);
        this.mMonth1 = new IndexedVariable("month1", variables, true);
        this.mDate = new IndexedVariable("date", variables, true);
        this.mYearLunar = new IndexedVariable("year_lunar", variables, true);
        this.mYearLunar1864 = new IndexedVariable("year_lunar1864", variables, true);
        this.mMonthLunar = new IndexedVariable("month_lunar", variables, true);
        this.mMonthLunarLeap = new IndexedVariable("month_lunar_leap", variables, true);
        this.mDateLunar = new IndexedVariable("date_lunar", variables, true);
        this.mDayOfWeek = new IndexedVariable("day_of_week", variables, true);
        this.mAmPm = new IndexedVariable("ampm", variables, true);
        this.mHour12 = new IndexedVariable("hour12", variables, true);
        this.mHour24 = new IndexedVariable("hour24", variables, true);
        this.mMinute = new IndexedVariable("minute", variables, true);
        this.mSecond = new IndexedVariable("second", variables, true);
        this.mTime = new IndexedVariable("time", variables, true);
        IndexedVariable indexedVariable = new IndexedVariable("time_sys", variables, true);
        this.mTimeSys = indexedVariable;
        indexedVariable.set(System.currentTimeMillis());
        this.mNextAlarm = new IndexedVariable("next_alarm_time", variables, false);
        this.mTimeFormatVar = new IndexedVariable("time_format", variables, true);
    }

    private void refreshAlarm() {
        this.mNextAlarm.set(Settings.System.getString(getContext().mContext.getContentResolver(), "next_alarm_formatted"));
    }

    private void updateTime() {
        long currentTimeMillis = System.currentTimeMillis();
        this.mTimeSys.set(currentTimeMillis);
        long j = currentTimeMillis / 1000;
        if (j != this.mLastUpdatedTime) {
            this.mCalendar.setTimeInMillis(currentTimeMillis);
            int i = this.mCalendar.get(1);
            int i2 = this.mCalendar.get(5);
            int i3 = this.mCalendar.get(9);
            this.mAmPm.set(this.mCalendar.get(17));
            this.mHour24.set(this.mCalendar.get(18));
            int i4 = this.mCalendar.get(18) % 12;
            this.mHour12.set(i4 == 0 ? 12.0d : i4);
            this.mMinute.set(this.mCalendar.get(20));
            this.mYear.set(i);
            this.mMonth.set(i2);
            this.mMonth1.set(i2 + 1);
            this.mDate.set(i3);
            this.mDayOfWeek.set(this.mCalendar.get(14));
            this.mSecond.set(this.mCalendar.get(21));
            this.mYearLunar.set(this.mCalendar.get(2));
            this.mMonthLunar.set(this.mCalendar.get(6));
            this.mDateLunar.set(this.mCalendar.get(10));
            this.mYearLunar1864.set(this.mCalendar.get(4));
            this.mMonthLunarLeap.set(this.mCalendar.get(8));
            this.mLastUpdatedTime = j;
        }
    }

    public void checkUpdateTime() {
        if (this.mFinished) {
            return;
        }
        synchronized (this.mLock) {
            if (this.mFinished) {
                return;
            }
            getContext().getHandler().removeCallbacks(this.mTimeUpdater);
            long currentTimeMillis = System.currentTimeMillis();
            this.mCalendar.setTimeInMillis(currentTimeMillis);
            for (int i : fields) {
                if (i == this.mTimeAccuracyField) {
                    break;
                }
                this.mCalendar.set(i, 0);
            }
            int i2 = DateFormat.is24HourFormat(getContext().mContext) ? 1 : 0;
            long timeInMillis = this.mCalendar.getTimeInMillis();
            if (this.mCurrentTime != timeInMillis || this.mTimeFormat != i2) {
                this.mCurrentTime = timeInMillis;
                this.mNextUpdateTime = timeInMillis + this.mTimeAccuracy;
                this.mTimeFormat = i2;
                this.mTimeFormatVar.set(i2);
                getRoot().requestUpdate();
            }
            getContext().getHandler().postDelayed(this.mTimeUpdater, this.mNextUpdateTime - currentTimeMillis);
        }
    }

    @Override // com.miui.maml.data.NotifierVariableUpdater, com.miui.maml.data.VariableUpdater
    public void finish() {
        synchronized (this.mLock) {
            super.finish();
            this.mFinished = true;
            this.mLastUpdatedTime = 0L;
            sCalendar = null;
            getContext().getHandler().removeCallbacks(this.mTimeUpdater);
        }
    }

    @Override // com.miui.maml.data.NotifierVariableUpdater, com.miui.maml.data.VariableUpdater
    public void init() {
        super.init();
        refreshAlarm();
        updateTime();
        checkUpdateTime();
    }

    @Override // com.miui.maml.NotifierManager.OnNotifyListener
    public void onNotify(Context context, Intent intent, Object obj) {
        resetCalendar();
        checkUpdateTime();
    }

    @Override // com.miui.maml.data.NotifierVariableUpdater, com.miui.maml.data.VariableUpdater
    public void pause() {
        super.pause();
        getContext().getHandler().removeCallbacks(this.mTimeUpdater);
    }

    protected void resetCalendar() {
        this.mCalendar = new Calendar();
        if (sCalendar != null) {
            sCalendar = new Calendar();
        }
    }

    @Override // com.miui.maml.data.NotifierVariableUpdater, com.miui.maml.data.VariableUpdater
    public void resume() {
        super.resume();
        refreshAlarm();
        resetCalendar();
        checkUpdateTime();
    }

    @Override // com.miui.maml.data.VariableUpdater
    public void tick(long j) {
        super.tick(j);
        this.mTime.set(j);
        updateTime();
    }
}
