package com.android.settings.development;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Parcel;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.IWindowManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.security.AdbUtils;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.os.Build;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiOptimizationController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, OnActivityResultListener {
    private Activity mActivity;
    private boolean mLastStatus;
    private int mPhysicalDensity;
    private int mPhysicalHeight;
    private int mPhysicalWidth;
    private IWindowManager mWindowManager;

    public MiuiOptimizationController(Context context) {
        super(context);
        this.mLastStatus = SystemProperties.getBoolean("persist.sys.miui_optimization", !Build.IS_CTS_BUILD);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
            getPhysicalSize();
        }
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    }

    private void enableDefaultFPS(boolean z) {
        if (z) {
            int topFps = (int) getTopFps();
            Log.i("MiuiOptimization", "Set " + topFps);
            Settings.System.putInt(this.mContext.getContentResolver(), "peak_refresh_rate", topFps);
        }
    }

    private void getPhysicalSize() {
        for (Display.Mode mode : this.mActivity.getWindowManager().getDefaultDisplay().getSupportedModes()) {
            if (mode.getPhysicalHeight() > this.mPhysicalHeight) {
                this.mPhysicalWidth = mode.getPhysicalWidth();
                this.mPhysicalHeight = mode.getPhysicalHeight();
            }
        }
        this.mPhysicalDensity = SystemProperties.getInt("ro.sf.lcd_density", 560);
    }

    private float getTopFps() {
        float f = 60.0f;
        for (float f2 : ((DisplayManager) this.mContext.getSystemService("display")).getDisplay(0).getSupportedRefreshRates()) {
            if (f2 > f) {
                f = f2;
            }
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void positiveClick(Boolean bool) {
        SystemProperties.set("persist.sys.miui_optimization", bool.toString());
        whenChangeOptimizaion(bool.booleanValue());
    }

    private void resetDefaultBrowserForCts(boolean z) {
        Context context;
        if (!z || (context = this.mContext) == null) {
            return;
        }
        PackageManager packageManager = context.getPackageManager();
        try {
            int userId = this.mContext.getUserId();
            String defaultBrowserPackageNameAsUser = packageManager.getDefaultBrowserPackageNameAsUser(userId);
            if ("com.android.browser".equals(defaultBrowserPackageNameAsUser) || "com.mi.globalbrowser".equals(defaultBrowserPackageNameAsUser)) {
                packageManager.setDefaultBrowserPackageNameAsUser(null, userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void revokeHuanjiRuntimePermission() {
        Log.i("MiuiOptimization", "revokeHuanjiRuntimePermission");
        try {
            PackageManager packageManager = this.mContext.getPackageManager();
            packageManager.revokeRuntimePermission("com.miui.huanji", "android.permission.ACCESS_FINE_LOCATION", UserHandle.OWNER);
            packageManager.revokeRuntimePermission("com.miui.backup", "android.permission.ACCESS_FINE_LOCATION", UserHandle.OWNER);
            packageManager.revokeRuntimePermission("com.miui.huanji", "android.permission.CAMERA", UserHandle.OWNER);
            packageManager.revokeRuntimePermission("com.miui.backup", "android.permission.CAMERA", UserHandle.OWNER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchResolution(int i, int i2, int i3, int i4) {
        Parcel obtain = Parcel.obtain();
        Parcel obtain2 = Parcel.obtain();
        try {
            try {
                obtain.writeInterfaceToken("android.view.IWindowManager");
                obtain.writeInt(i);
                obtain.writeInt(i2);
                obtain.writeInt(i3);
                obtain.writeInt(i4);
                Log.d("MiuiOptimization", "CTS switchResolution [ displayId:" + i + ", width:" + i2 + ", height:" + i3 + ", density:" + i4 + "]");
                this.mWindowManager.asBinder().transact(255, obtain, obtain2, 0);
                obtain2.readException();
            } catch (RemoteException e) {
                Log.e("MiuiOptimization", e.toString());
            }
        } finally {
            obtain2.recycle();
            obtain.recycle();
        }
    }

    private void switchResolutionForCtsIfNeed(boolean z) {
        int i;
        int i2;
        int i3;
        Point point = new Point();
        Point point2 = new Point();
        try {
            this.mWindowManager.getBaseDisplaySize(0, point);
            this.mWindowManager.getInitialDisplaySize(0, point2);
            if (!z) {
                int[] ctsResolution = getCtsResolution();
                if (ctsResolution == null) {
                    return;
                }
                i = ctsResolution[0];
                i2 = ctsResolution[1];
                i3 = ctsResolution[2];
                SystemProperties.set("persist.sys.cts_resolution", "");
            } else if (point.x == this.mPhysicalWidth) {
                return;
            } else {
                int baseDisplayDensity = this.mWindowManager.getBaseDisplayDensity(0);
                i3 = this.mPhysicalDensity;
                i = this.mPhysicalWidth;
                i2 = this.mPhysicalHeight;
                SystemProperties.set("persist.sys.cts_resolution", point.x + "," + point.y + "," + baseDisplayDensity);
            }
            switchResolution(0, i, i2, i3);
        } catch (RemoteException e) {
            Log.e("MiuiOptimization", e.toString());
        }
    }

    private void whenChangeOptimizaion(boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), MiuiSettings.Secure.MIUI_OPTIMIZATION, z ? 1 : 0);
        resetDefaultBrowserForCts(!z ? 1 : 0);
        setChecked(z);
        enableDefaultFPS(!z ? 1 : 0);
        switchResolutionForCtsIfNeed(!z ? 1 : 0);
        revokeHuanjiRuntimePermission();
    }

    private void writeMiuiOptimizationOptions(Object obj) {
        if (obj instanceof Boolean) {
            final Boolean bool = (Boolean) obj;
            Intent interceptIntent = AdbUtils.getInterceptIntent("", "miui_close_optimization", "");
            if (!AdbUtils.isIntentEnable(this.mContext, interceptIntent) || this.mActivity == null || bool.booleanValue()) {
                new AlertDialog.Builder(this.mContext).setMessage(bool.booleanValue() ? R.string.open_optimization_message : R.string.close_optimization_message).setTitle(bool.booleanValue() ? R.string.open_miui_optimization_title : R.string.close_miui_optimization_title).setPositiveButton(bool.booleanValue() ? R.string.open_optimization_option : R.string.close_optimization_option, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.MiuiOptimizationController.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MiuiOptimizationController.this.positiveClick(bool);
                        MiuiOptimizationController.this.mLastStatus = true;
                    }
                }).setNegativeButton(R.string.optimization_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.development.MiuiOptimizationController.1
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MiuiOptimizationController.this.setChecked(!bool.booleanValue());
                    }
                }).show();
            } else {
                this.mActivity.startActivityForResult(interceptIntent, 25);
            }
            setChecked(!bool.booleanValue());
        }
    }

    public int[] getCtsResolution() {
        int[] iArr = new int[3];
        String str = SystemProperties.get("persist.sys.cts_resolution", (String) null);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            iArr[0] = Integer.parseInt(str.split(",")[0]);
            iArr[1] = Integer.parseInt(str.split(",")[1]);
            iArr[2] = Integer.parseInt(str.split(",")[2]);
            return iArr;
        } catch (NumberFormatException e) {
            Log.e("MiuiOptimization", "getResolutionFromProperty exception:" + e.toString());
            return null;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "miui_experience_optimization";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (UserHandle.myUserId() == 0) {
            return !SystemProperties.getBoolean("persist.sys.miui_optimization", !Build.IS_CTS_BUILD);
        }
        return false;
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i == 25) {
            if (i2 == -1) {
                positiveClick(Boolean.FALSE);
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!TextUtils.equals(preference.getKey(), "miui_experience_optimization") || Utils.isMonkeyRunning()) {
            return true;
        }
        writeMiuiOptimizationOptions(obj);
        return true;
    }

    public void setChecked(boolean z) {
        ((CheckBoxPreference) this.mPreference).setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        boolean z = SystemProperties.getBoolean("persist.sys.miui_optimization", !Build.IS_CTS_BUILD);
        setChecked(z);
        if (this.mLastStatus != z) {
            whenChangeOptimizaion(z);
            this.mLastStatus = z;
        }
    }
}
