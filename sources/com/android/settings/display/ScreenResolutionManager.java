package com.android.settings.display;

import android.graphics.Point;
import android.hardware.display.DisplayManagerGlobal;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.util.FeatureParser;
import miuix.visual.check.VisualCheckGroup;

/* loaded from: classes.dex */
public class ScreenResolutionManager extends SettingsPreferenceFragment implements VisualCheckGroup.OnCheckedChangeListener, Preference.OnPreferenceChangeListener {
    private Display mDisplay;
    private int mInitalDensity;
    private Point mInitalPoint;
    PreferenceCategory mSaveBatteryCategory;
    CheckBoxPreference mSaveBatteryMode;
    private boolean mScreenResolutionSwitching;
    ScreenResolutionPreference mSelectResolution;
    private IWindowManager mWindowManager;
    private int mFHDWidth = 1080;
    private int mFHDHeight = 2400;
    private int mQHDWidth = 1440;
    private int mQHDHeight = 3200;

    private int calculateHeightFromWidth(int i) {
        Display.Mode mode = this.mDisplay.getMode();
        return (int) (((mode.getPhysicalHeight() * 1.0f) / mode.getPhysicalWidth()) * i);
    }

    private void initSupportSolution() {
        int[] intArray = FeatureParser.getIntArray("screen_resolution_supported");
        if (intArray == null || intArray.length <= 1) {
            return;
        }
        int i = intArray[0];
        this.mQHDWidth = i;
        this.mQHDHeight = calculateHeightFromWidth(i);
        int i2 = intArray[1];
        this.mFHDWidth = i2;
        this.mFHDHeight = calculateHeightFromWidth(i2);
    }

    private boolean isCompatMode() {
        return Settings.System.getInt(getActivity().getContentResolver(), "miui_screen_compat", 1) == 1;
    }

    private boolean isQhdMode() {
        String str = SystemProperties.get("persist.sys.miui_resolution", (String) null);
        return str == null || "".equals(str) || Integer.parseInt(str.split(",")[0]) != this.mFHDWidth;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onResume$0() {
        finishFragment();
    }

    private void switchResolution(int i) {
        switchResolution(0, i, calculateHeightFromWidth(i), Math.round(((this.mInitalDensity * i) * 1.0f) / this.mInitalPoint.x));
    }

    private void switchResolution(int i, int i2, int i3, int i4) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            obtain.writeInterfaceToken("android.view.IWindowManager");
            obtain.writeInt(i);
            obtain.writeInt(i2);
            obtain.writeInt(i3);
            obtain.writeInt(i4);
            Log.d("ScreenResolutionManager", "switchResolution [ displayId:" + i + ", width:" + i2 + ", height:" + i3 + ", density:" + i4 + "]");
            this.mWindowManager.asBinder().transact(255, obtain, obtain2, 0);
            obtain2.readException();
        } catch (RemoteException unused) {
        } catch (Throwable th) {
            obtain2.recycle();
            obtain.recycle();
            throw th;
        }
        obtain2.recycle();
        obtain.recycle();
    }

    @Override // miuix.visual.check.VisualCheckGroup.OnCheckedChangeListener
    public void onCheckedChanged(VisualCheckGroup visualCheckGroup, int i) {
        if (this.mScreenResolutionSwitching) {
            return;
        }
        this.mScreenResolutionSwitching = true;
        this.mSelectResolution.setSwitchEnabled(false);
        if (i == R.id.resolution_fhd) {
            switchResolution(this.mFHDWidth);
        } else if (i == R.id.resolution_qhd) {
            switchResolution(this.mQHDWidth);
        } else {
            Log.e("ScreenResolutionManager", "Switch resolution error.");
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Log.e("ScreenResolutionManager", "Settings onCreate");
        addPreferencesFromResource(R.xml.full_screen_resolution_settings);
        if (bundle != null) {
            this.mScreenResolutionSwitching = bundle.getBoolean("screen_resolution_switching");
        }
        this.mDisplay = DisplayManagerGlobal.getInstance().getRealDisplay(0);
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mInitalPoint = new Point();
        try {
            this.mInitalDensity = this.mWindowManager.getInitialDisplayDensity(0);
            this.mWindowManager.getInitialDisplaySize(0, this.mInitalPoint);
        } catch (Exception e) {
            Log.e("ScreenResolutionManager", "Exception: ", e);
        }
        initSupportSolution();
        this.mSelectResolution = (ScreenResolutionPreference) findPreference("full_screen_resolution_selection");
        boolean isQhdMode = isQhdMode();
        this.mSelectResolution.setQHDSolution(this.mQHDWidth, this.mQHDHeight);
        this.mSelectResolution.setFHDSolution(this.mFHDWidth, this.mFHDHeight);
        this.mSelectResolution.setQhdChecked(isQhdMode);
        this.mSelectResolution.setQhdImage(R.drawable.qhd_image);
        this.mSelectResolution.setFhdImage(R.drawable.fhd_image);
        this.mSelectResolution.setQhdText(R.string.resolution_qhd);
        this.mSelectResolution.setFhdText(R.string.resolution_fhd);
        this.mSelectResolution.setQhdTextSummary(R.string.resolution_qhd_summary);
        this.mSelectResolution.setFhdTextSummary(R.string.resolution_fhd_summary);
        this.mSelectResolution.setOnCheckedChangeListener(this);
        this.mSelectResolution.setSwitchEnabled(!this.mScreenResolutionSwitching);
        this.mSelectResolution.setEnabled(false);
        this.mSaveBatteryCategory = (PreferenceCategory) findPreference("save_battery_mode_category");
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference("save_battery_mode");
        this.mSaveBatteryMode = checkBoxPreference;
        checkBoxPreference.setChecked(isCompatMode());
        this.mSaveBatteryMode.setOnPreferenceChangeListener(this);
        if (isQhdMode) {
            return;
        }
        getPreferenceScreen().removePreference(this.mSaveBatteryCategory);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            Settings.System.putInt(getActivity().getContentResolver(), "miui_screen_compat", 1);
        } else {
            Settings.System.putInt(getActivity().getContentResolver(), "miui_screen_compat", 0);
        }
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (getAppCompatActionBar() != null) {
            getAppCompatActionBar().setTitle(getActivity().getResources().getString(R.string.screen_resolution_title));
            getAppCompatActionBar().setSubtitle(getActivity().getResources().getString(R.string.resolution_sub_title));
        }
        if (getActivity().isInMultiWindowMode()) {
            finishFragment();
        }
        if (this.mScreenResolutionSwitching) {
            this.mScreenResolutionSwitching = false;
            new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.settings.display.ScreenResolutionManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ScreenResolutionManager.this.lambda$onResume$0();
                }
            });
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mScreenResolutionSwitching) {
            bundle.putBoolean("screen_resolution_switching", true);
        }
    }
}
