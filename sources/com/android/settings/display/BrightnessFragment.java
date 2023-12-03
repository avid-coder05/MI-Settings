package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.hardware.display.BrightnessInfo;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.Log;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.hardware.display.DisplayFeatureManager;
import miui.util.FeatureParser;

/* loaded from: classes.dex */
public class BrightnessFragment extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mAutoAdjustModeEnable;
    private boolean mAutomaticAvailable;
    private volatile boolean mAutomaticBrightnessEnabled;
    private BackgroundHandler mBackgroundHandler;
    private IBinder mBinder;
    private PreferenceGroup mBrightnessModeGroup;
    private BrightnessObserver mBrightnessObserver;
    private BrightnessSeekBarPreference mBrightnessSeekBarPreference;
    private Context mContext;
    private final DisplayManager.DisplayListener mDisplayListener;
    private DisplayManager mDisplayManager;
    private final Handler mHandler;
    private Preference mHighBrightnessHintSummary;
    private boolean mIsOdinInternal;
    private volatile boolean mIsVrModeEnabled;
    private float mMaximumBrightness;
    private float mMaximumBrightnessForVr;
    private float mMinimumBrightness;
    private float mMinimumBrightnessForVr;
    private boolean mSmoothLightModeAvailable = FeatureParser.getBoolean("support_backlight_bit_switch", false);
    private CheckBoxPreference mSmoothLightModeEnable;
    private final Runnable mStartListeningRunnable;
    private final Runnable mStopListeningRunnable;
    private boolean mSunlightModeAvailable;
    private CheckBoxPreference mSunlightModeEnable;
    private final Runnable mUpdateBrightnessSeekBarRunnable;
    private final Runnable mUpdateModeRunnable;
    private IVrManager mVrManager;
    private final IVrStateCallbacks mVrStateCallbacks;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class BackgroundHandler extends Handler {
        private BackgroundHandler(Looper looper) {
            super(looper);
        }
    }

    /* loaded from: classes.dex */
    private class BrightnessObserver extends ContentObserver {
        private final Uri BRIGHTNESS_FLOAT_URI;
        private final Uri BRIGHTNESS_FOR_VR_FLOAT_URI;
        private final Uri BRIGHTNESS_MODE_URI;
        private final Uri BRIGHTNESS_URI;

        public BrightnessObserver(Handler handler) {
            super(handler);
            this.BRIGHTNESS_MODE_URI = Settings.System.getUriFor("screen_brightness_mode");
            this.BRIGHTNESS_URI = Settings.System.getUriFor("screen_brightness");
            this.BRIGHTNESS_FLOAT_URI = Settings.System.getUriFor("screen_brightness_float");
            this.BRIGHTNESS_FOR_VR_FLOAT_URI = Settings.System.getUriFor("screen_brightness_for_vr_float");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            onChange(z, null);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (z) {
                return;
            }
            if (this.BRIGHTNESS_MODE_URI.equals(uri)) {
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateModeRunnable);
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateBrightnessSeekBarRunnable);
            } else if (this.BRIGHTNESS_FOR_VR_FLOAT_URI.equals(uri)) {
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateBrightnessSeekBarRunnable);
            } else {
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateModeRunnable);
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateBrightnessSeekBarRunnable);
            }
        }

        public void startObserving() {
            ContentResolver contentResolver = BrightnessFragment.this.mContext.getContentResolver();
            contentResolver.registerContentObserver(this.BRIGHTNESS_MODE_URI, false, this);
            contentResolver.registerContentObserver(this.BRIGHTNESS_URI, false, this);
            contentResolver.registerContentObserver(this.BRIGHTNESS_FLOAT_URI, false, this);
            contentResolver.registerContentObserver(this.BRIGHTNESS_FOR_VR_FLOAT_URI, false, this);
            BrightnessFragment.this.mDisplayManager.registerDisplayListener(BrightnessFragment.this.mDisplayListener, BrightnessFragment.this.mHandler, 8L);
        }

        public void stopObserving() {
            BrightnessFragment.this.mContext.getContentResolver().unregisterContentObserver(this);
            BrightnessFragment.this.mDisplayManager.unregisterDisplayListener(BrightnessFragment.this.mDisplayListener);
        }
    }

    public BrightnessFragment() {
        boolean z = false;
        if (Build.DEVICE.equalsIgnoreCase("odin") && !miui.os.Build.IS_INTERNATIONAL_BUILD) {
            z = true;
        }
        this.mIsOdinInternal = z;
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: com.android.settings.display.BrightnessFragment.1
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int i) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int i) {
                BrightnessFragment.this.mBackgroundHandler.post(BrightnessFragment.this.mUpdateBrightnessSeekBarRunnable);
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int i) {
            }
        };
        this.mUpdateModeRunnable = new Runnable() { // from class: com.android.settings.display.BrightnessFragment.2
            @Override // java.lang.Runnable
            public void run() {
                if (BrightnessFragment.this.mAutomaticAvailable) {
                    BrightnessFragment brightnessFragment = BrightnessFragment.this;
                    brightnessFragment.mAutomaticBrightnessEnabled = brightnessFragment.isAutoBrightnessEnabled();
                    BrightnessFragment.this.mHandler.obtainMessage(0, BrightnessFragment.this.mAutomaticBrightnessEnabled ? 1 : 0, 0).sendToTarget();
                }
            }
        };
        this.mUpdateBrightnessSeekBarRunnable = new Runnable() { // from class: com.android.settings.display.BrightnessFragment.3
            @Override // java.lang.Runnable
            public void run() {
                boolean z2 = BrightnessFragment.this.mIsVrModeEnabled;
                BrightnessInfo brightnessInfo = BrightnessFragment.this.mContext.getDisplay().getBrightnessInfo();
                if (brightnessInfo == null) {
                    return;
                }
                BrightnessFragment.this.mMaximumBrightness = brightnessInfo.brightnessMaximum;
                BrightnessFragment.this.mMinimumBrightness = brightnessInfo.brightnessMinimum;
                BrightnessFragment.this.mHandler.obtainMessage(1, Float.floatToIntBits(brightnessInfo.brightness), z2 ? 1 : 0).sendToTarget();
            }
        };
        this.mVrStateCallbacks = new IVrStateCallbacks.Stub() { // from class: com.android.settings.display.BrightnessFragment.4
            public void onVrStateChanged(boolean z2) {
                BrightnessFragment.this.mHandler.obtainMessage(2, z2 ? 1 : 0, 0).sendToTarget();
            }
        };
        this.mStartListeningRunnable = new Runnable() { // from class: com.android.settings.display.BrightnessFragment.5
            @Override // java.lang.Runnable
            public void run() {
                if (BrightnessFragment.this.mVrManager != null) {
                    try {
                        BrightnessFragment.this.mVrManager.registerListener(BrightnessFragment.this.mVrStateCallbacks);
                        BrightnessFragment brightnessFragment = BrightnessFragment.this;
                        brightnessFragment.mIsVrModeEnabled = brightnessFragment.mVrManager.getVrModeState();
                    } catch (RemoteException e) {
                        Log.e("BrightnessFragment", "Failed to register VR mode state listener: ", e);
                    }
                }
                BrightnessFragment.this.mBrightnessObserver.startObserving();
                BrightnessFragment.this.mUpdateModeRunnable.run();
                BrightnessFragment.this.mUpdateBrightnessSeekBarRunnable.run();
            }
        };
        this.mStopListeningRunnable = new Runnable() { // from class: com.android.settings.display.BrightnessFragment.6
            @Override // java.lang.Runnable
            public void run() {
                if (BrightnessFragment.this.mVrManager != null) {
                    try {
                        BrightnessFragment.this.mVrManager.unregisterListener(BrightnessFragment.this.mVrStateCallbacks);
                    } catch (RemoteException e) {
                        Log.e("BrightnessFragment", "Failed to unregister VR mode state listener: ", e);
                    }
                }
                BrightnessFragment.this.mBrightnessObserver.stopObserving();
            }
        };
        this.mHandler = new Handler() { // from class: com.android.settings.display.BrightnessFragment.7
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                int i = message.what;
                if (i == 0) {
                    BrightnessFragment.this.updateAutomaticBrightnessMode(message.arg1 != 0);
                } else if (i == 1) {
                    BrightnessFragment.this.mBrightnessSeekBarPreference.updateBrightnessSeekBar(Float.intBitsToFloat(message.arg1), message.arg2 != 0, BrightnessFragment.this.mMinimumBrightness, BrightnessFragment.this.mMaximumBrightness);
                } else if (i != 2) {
                } else {
                    BrightnessFragment.this.updateVrMode(message.arg1 != 0);
                    BrightnessFragment.this.mBrightnessSeekBarPreference.updateVrMode(message.arg1 != 0);
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAutoBrightnessEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "screen_brightness_mode", 0) != 0;
    }

    private boolean isSunlightModeEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), "sunlight_mode", 0) != 0;
    }

    private void resetAutoBrightnessShortModel() {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            try {
                obtain.writeInterfaceToken("android.view.android.hardware.display.IDisplayManager");
                this.mBinder.transact(16777214, obtain, obtain2, 0);
            } catch (RemoteException unused) {
                Log.d("BrightnessFragment", "RemoteException!");
            }
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    private void setAutomaticBrightnessMode(int i) {
        if (i != 1) {
            resetAutoBrightnessShortModel();
        }
        Settings.System.putInt(this.mContext.getContentResolver(), "screen_brightness_mode", i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAutomaticBrightnessMode(boolean z) {
        this.mAutoAdjustModeEnable.setChecked(z);
        if (this.mSunlightModeAvailable) {
            if (z) {
                this.mBrightnessModeGroup.removePreference(this.mSunlightModeEnable);
                updateHighBrightnessHintSummary(true);
            } else {
                this.mBrightnessModeGroup.addPreference(this.mSunlightModeEnable);
                updateSunlightMode(isSunlightModeEnabled());
            }
        }
        setAutomaticBrightnessMode(z ? 1 : 0);
    }

    private void updateHighBrightnessHintSummary(boolean z) {
        if (this.mIsOdinInternal) {
            if (z) {
                getPreferenceScreen().addPreference(this.mHighBrightnessHintSummary);
            } else {
                getPreferenceScreen().removePreference(this.mHighBrightnessHintSummary);
            }
        }
    }

    private void updateSmoothLightMode(boolean z) {
        boolean z2 = SystemProperties.getBoolean("persist.vendor.light.bit.switch", false);
        if (z2 == z) {
            return;
        }
        this.mSmoothLightModeEnable.setChecked(z);
        Log.d("BrightnessFragment", "updateSmoothLightMode: isChecked: " + z + ", current status: " + z2);
        DisplayFeatureManager.getInstance().setScreenEffect(42, z ? 1 : 0);
    }

    private void updateSunlightMode(boolean z) {
        this.mSunlightModeEnable.setChecked(z);
        updateHighBrightnessHintSummary(z);
        Settings.System.putInt(this.mContext.getContentResolver(), "sunlight_mode", z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateVrMode(boolean z) {
        if (this.mIsVrModeEnabled != z) {
            this.mIsVrModeEnabled = z;
            this.mBackgroundHandler.post(this.mUpdateBrightnessSeekBarRunnable);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mContext = getActivity();
        addPreferencesFromResource(R.xml.brightness_settings);
        HandlerThread handlerThread = new HandlerThread("BrightnessFragment", 10);
        handlerThread.start();
        this.mBackgroundHandler = new BackgroundHandler(handlerThread.getLooper());
        BrightnessSeekBarPreference brightnessSeekBarPreference = (BrightnessSeekBarPreference) findPreference("brightness");
        this.mBrightnessSeekBarPreference = brightnessSeekBarPreference;
        brightnessSeekBarPreference.setIcon(R.drawable.sun_brightness_icon);
        this.mBrightnessModeGroup = (PreferenceGroup) findPreference("brightness_mode_group");
        this.mAutoAdjustModeEnable = (CheckBoxPreference) findPreference("brightness_auto_mode_enable");
        this.mSunlightModeEnable = (CheckBoxPreference) findPreference("brightness_sunlight_mode_enable");
        this.mSmoothLightModeEnable = (CheckBoxPreference) findPreference("brightness_smooth_mode_enable");
        this.mHighBrightnessHintSummary = findPreference("high_brightness_hint_summary");
        this.mSunlightModeAvailable = FeatureParser.getBoolean("config_sunlight_mode_available", true);
        this.mAutoAdjustModeEnable.setOnPreferenceChangeListener(this);
        this.mSunlightModeEnable.setOnPreferenceChangeListener(this);
        if (this.mSmoothLightModeAvailable) {
            this.mSmoothLightModeEnable.setChecked(SystemProperties.getBoolean("persist.vendor.light.bit.switch", false));
            this.mSmoothLightModeEnable.setOnPreferenceChangeListener(this);
        } else {
            this.mBrightnessModeGroup.removePreference(this.mSmoothLightModeEnable);
        }
        if (!this.mSunlightModeAvailable) {
            this.mBrightnessModeGroup.removePreference(this.mSunlightModeEnable);
        }
        getPreferenceScreen().removePreference(this.mHighBrightnessHintSummary);
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        this.mMinimumBrightness = 0.0f;
        this.mMaximumBrightness = 1.0f;
        this.mMinimumBrightnessForVr = powerManager.getBrightnessConstraint(5);
        this.mMaximumBrightnessForVr = powerManager.getBrightnessConstraint(6);
        this.mVrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        this.mAutomaticAvailable = getResources().getBoolean(285540353);
        this.mBrightnessObserver = new BrightnessObserver(this.mHandler);
        this.mDisplayManager = (DisplayManager) this.mContext.getSystemService(DisplayManager.class);
        this.mBinder = ServiceManager.getService("display");
        updateAutomaticBrightnessMode(isAutoBrightnessEnabled());
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        this.mAutoAdjustModeEnable.setOnPreferenceChangeListener(null);
        this.mSunlightModeEnable.setOnPreferenceChangeListener(null);
        this.mBackgroundHandler.getLooper().quit();
        super.onDestroy();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        this.mBackgroundHandler.removeCallbacks(this.mStopListeningRunnable);
        this.mBackgroundHandler.post(this.mStopListeningRunnable);
        super.onPause();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (getListView().isComputingLayout()) {
            return false;
        }
        String key = preference.getKey();
        if ("brightness_auto_mode_enable".equals(key)) {
            updateAutomaticBrightnessMode(((Boolean) obj).booleanValue());
            return true;
        } else if ("brightness_sunlight_mode_enable".equals(key)) {
            updateSunlightMode(((Boolean) obj).booleanValue());
            return true;
        } else if ("brightness_smooth_mode_enable".equals(key)) {
            updateSmoothLightMode(((Boolean) obj).booleanValue());
            return true;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        this.mBackgroundHandler.removeCallbacks(this.mStartListeningRunnable);
        this.mBackgroundHandler.post(this.mStartListeningRunnable);
        super.onResume();
    }
}
