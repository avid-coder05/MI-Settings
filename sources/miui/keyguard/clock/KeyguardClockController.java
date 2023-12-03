package miui.keyguard.clock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miui.system.internal.R;
import java.util.TimeZone;

/* loaded from: classes3.dex */
public class KeyguardClockController {
    private static final String AUTO_DUAL_CLOCK = "auto_dual_clock";
    public static final int HIDE_LUNAR_CALENDAR = 0;
    public static final int KEYGUARD_CLOCK_CENTER_HORIZONTAL = 1;
    public static final int KEYGUARD_CLOCK_CENTER_VERTICAL = 3;
    public static final int KEYGUARD_CLOCK_DEFAULT = 0;
    public static final int KEYGUARD_CLOCK_DUAL = 101;
    public static final int KEYGUARD_CLOCK_LEFT_TOP = 2;
    public static final int KEYGUARD_CLOCK_LEFT_TOP_LARGE = 4;
    private static final String KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR = "show_lunar_calendar";
    private static final String RESIDENT_TIMEZONE = "resident_timezone";
    public static final String SELECTED_KEYGUARD_CLOCK_POSITION = "selected_keyguard_clock_position";
    public static final int SHOW_LUNAR_CALENDAR = 1;
    public static final int UNDEFINED = -1;
    private IClockView mClockView;
    private ViewGroup mContainer;
    private Context mContext;
    private boolean mDualClockOpen;
    private String mResidentTimezone;
    private int mSelectedClockPosition;
    private boolean mShowDualClock;
    private String mCurrentTimezone = TimeZone.getDefault().getID();
    private boolean mAutoDualClock = true;
    private boolean mHasTopMargin = true;
    private String mOwnerString = null;
    private int mClockStyle = 0;
    private int mLastClockPosition = 0;
    private int mShowLunarCalendar = -1;
    private float mScaleRatio = 1.0f;
    private boolean mTextDark = false;
    private final Handler mHandler = new Handler();
    private boolean mAutoUpdateTime = true;
    private Runnable mUpdateTimeRunnable = new Runnable() { // from class: miui.keyguard.clock.KeyguardClockController.1
        @Override // java.lang.Runnable
        public void run() {
            if (KeyguardClockController.this.mClockView != null) {
                KeyguardClockController.this.mClockView.updateTime();
            }
        }
    };
    private final BroadcastReceiver mTimezoneChangeReceiver = new BroadcastReceiver() { // from class: miui.keyguard.clock.KeyguardClockController.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            KeyguardClockController.this.mHandler.post(new Runnable() { // from class: miui.keyguard.clock.KeyguardClockController.2.1
                @Override // java.lang.Runnable
                public void run() {
                    KeyguardClockController.this.mCurrentTimezone = TimeZone.getDefault().getID();
                    KeyguardClockController.this.updateDualClock();
                }
            });
        }
    };
    private final BroadcastReceiver mUpdateTimeReceiver = new BroadcastReceiver() { // from class: miui.keyguard.clock.KeyguardClockController.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            KeyguardClockController.this.mHandler.post(KeyguardClockController.this.mUpdateTimeRunnable);
        }
    };
    ContentObserver mDualClockOpenObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.KeyguardClockController.4
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            KeyguardClockController keyguardClockController = KeyguardClockController.this;
            keyguardClockController.mDualClockOpen = Settings.System.getInt(keyguardClockController.mContext.getContentResolver(), "auto_dual_clock", 0) != 0;
            KeyguardClockController.this.updateDualClock();
        }
    };
    ContentObserver mResidentTimezoneObserver = new ContentObserver(new Handler()) { // from class: miui.keyguard.clock.KeyguardClockController.5
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            KeyguardClockController keyguardClockController = KeyguardClockController.this;
            keyguardClockController.mResidentTimezone = Settings.System.getString(keyguardClockController.mContext.getContentResolver(), "resident_timezone");
            KeyguardClockController.this.updateDualClock();
        }
    };

    /* loaded from: classes3.dex */
    public interface IClockView {
        int getClockHeight();

        float getClockVisibleHeight();

        float getTopMargin();

        void setClockAlpha(float f);

        void setOwnerInfo(String str);

        void setScaleRatio(float f);

        void setShowLunarCalendar(boolean z);

        void setTextColorDark(boolean z);

        void updateResidentTimeZone(String str);

        default void updateSecondClockVisibility(int i) {
        }

        void updateTime();

        void updateTimeZone(String str);

        void updateViewTopMargin(boolean z);
    }

    public KeyguardClockController(Context context, ViewGroup viewGroup) {
        this.mDualClockOpen = false;
        this.mShowDualClock = false;
        this.mSelectedClockPosition = 0;
        this.mContext = context;
        this.mContainer = viewGroup;
        this.mSelectedClockPosition = Settings.System.getInt(context.getContentResolver(), SELECTED_KEYGUARD_CLOCK_POSITION, 0);
        this.mDualClockOpen = Settings.System.getInt(context.getContentResolver(), "auto_dual_clock", 0) != 0;
        String string = Settings.System.getString(context.getContentResolver(), "resident_timezone");
        this.mResidentTimezone = string;
        this.mShowDualClock = (!this.mDualClockOpen || string == null || string.equals(this.mCurrentTimezone)) ? false : true;
        addClockView();
        updateKeyguardClock();
    }

    private void addClockView() {
        View inflateClockView = inflateClockView();
        this.mContainer.addView(inflateClockView);
        IClockView iClockView = (IClockView) inflateClockView;
        this.mClockView = iClockView;
        if (iClockView != null) {
            iClockView.updateResidentTimeZone(this.mResidentTimezone);
            this.mClockView.updateTimeZone(this.mCurrentTimezone);
            this.mClockView.setShowLunarCalendar(getShowLunarCalendar());
            this.mClockView.setScaleRatio(this.mScaleRatio);
            this.mClockView.setTextColorDark(this.mTextDark);
            this.mClockView.updateViewTopMargin(this.mHasTopMargin);
            this.mClockView.setOwnerInfo(this.mOwnerString);
        }
    }

    private boolean getShowLunarCalendar() {
        int i = this.mShowLunarCalendar;
        return i != -1 ? i == 1 : Settings.System.getInt(this.mContext.getContentResolver(), KEY_SETTINGS_SYSTEM_SHOW_LUNAR_CALENDAR, 0) == 1;
    }

    private void registerDualClockObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("auto_dual_clock"), false, this.mDualClockOpenObserver);
        this.mDualClockOpenObserver.onChange(false);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("resident_timezone"), false, this.mResidentTimezoneObserver);
        this.mResidentTimezoneObserver.onChange(false);
    }

    private void unregisterDualClockObserver() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mDualClockOpenObserver);
        this.mContext.getContentResolver().unregisterContentObserver(this.mResidentTimezoneObserver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDualClock() {
        String str;
        this.mShowDualClock = this.mAutoDualClock && this.mDualClockOpen && (str = this.mResidentTimezone) != null && !str.equals(this.mCurrentTimezone);
        updateKeyguardClock();
    }

    public int getClockHeight() {
        return this.mClockView.getClockHeight();
    }

    public int getClockPosition() {
        if (this.mShowDualClock) {
            return 101;
        }
        int i = this.mClockStyle;
        if (i != 0) {
            return i;
        }
        int i2 = this.mSelectedClockPosition;
        return i2 != 0 ? i2 : getDefaultClockPosition();
    }

    public float getClockVisibleHeight() {
        return this.mClockView.getClockVisibleHeight();
    }

    public int getDefaultClockPosition() {
        String str = Build.DEVICE;
        return ("davinci".equals(str) || "davinciin".equals(str) || "raphael".equals(str) || "raphaelin".equals(str) || "chiron".equals(str) || "polaris".equals(str)) ? 3 : 0;
    }

    public float getTopMargin() {
        return this.mClockView.getTopMargin();
    }

    public View inflateClockView() {
        int clockPosition = getClockPosition();
        return LayoutInflater.from(this.mContext).inflate(clockPosition != 1 ? clockPosition != 2 ? clockPosition != 3 ? clockPosition != 4 ? clockPosition != 101 ? Build.VERSION.SDK_INT < 30 ? R.layout.miui_center_horizontal_clock : R.layout.miui_left_top_large_clock : R.layout.miui_dual_clock : R.layout.miui_left_top_large_clock : R.layout.miui_vertical_clock : R.layout.miui_left_top_clock : R.layout.miui_center_horizontal_clock, this.mContainer, false);
    }

    public boolean isDualClock() {
        return this.mShowDualClock;
    }

    public void onAddToWindow() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        this.mContext.registerReceiver(this.mTimezoneChangeReceiver, intentFilter);
        if (this.mAutoUpdateTime) {
            IntentFilter intentFilter2 = new IntentFilter();
            intentFilter2.addAction("android.intent.action.TIME_TICK");
            intentFilter2.addAction("android.intent.action.TIME_SET");
            this.mContext.registerReceiver(this.mUpdateTimeReceiver, intentFilter2);
        }
        registerDualClockObserver();
    }

    public void onRemoveFromWindow() {
        this.mContext.unregisterReceiver(this.mTimezoneChangeReceiver);
        if (this.mAutoUpdateTime) {
            this.mContext.unregisterReceiver(this.mUpdateTimeReceiver);
        }
        unregisterDualClockObserver();
    }

    public void setAutoDualClock(boolean z) {
        this.mAutoDualClock = z;
        updateDualClock();
    }

    public void setAutoUpdateTime(boolean z) {
        this.mAutoUpdateTime = z;
    }

    public void setClockAlpha(float f) {
        this.mClockView.setClockAlpha(f);
    }

    public void setClockStyle(int i) {
        if (this.mClockStyle != i) {
            this.mClockStyle = i;
            updateKeyguardClock();
        }
    }

    public void setHasTopMargin(boolean z) {
        if (this.mHasTopMargin != z) {
            this.mHasTopMargin = z;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.updateViewTopMargin(z);
            }
        }
    }

    public void setOwnerInfo(String str) {
        if (this.mOwnerString != str) {
            this.mOwnerString = str;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setOwnerInfo(str);
            }
        }
    }

    public void setScaleRatio(float f) {
        if (this.mScaleRatio != f) {
            this.mScaleRatio = f;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setScaleRatio(f);
            }
        }
    }

    public void setShowLunarCalendar(int i) {
        if (this.mShowLunarCalendar != i) {
            this.mShowLunarCalendar = i;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setShowLunarCalendar(getShowLunarCalendar());
            }
        }
    }

    public void setTextColorDark(boolean z) {
        if (this.mTextDark != z) {
            this.mTextDark = z;
            IClockView iClockView = this.mClockView;
            if (iClockView != null) {
                iClockView.setTextColorDark(z);
            }
        }
    }

    public void updateKeyguardClock() {
        if (getClockPosition() != this.mLastClockPosition) {
            this.mLastClockPosition = getClockPosition();
            this.mContainer.removeAllViews();
            addClockView();
        }
    }

    public void updateSecondClockVisibility(int i) {
        IClockView iClockView = this.mClockView;
        if (iClockView != null) {
            iClockView.updateSecondClockVisibility(i);
        }
    }

    public void updateTime() {
        IClockView iClockView = this.mClockView;
        if (iClockView != null) {
            iClockView.updateTime();
        }
    }
}
