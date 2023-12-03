package com.android.settings.display;

import android.content.Context;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManagerGlobal;
import com.android.settings.R;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.display.DisplayDensityUtils;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ScreenZoomUtils {
    static Point mInitalPoint = new Point();

    private static int getCurrentDisplayDensity(int i) {
        try {
            WindowManagerGlobal.getWindowManagerService().getInitialDisplaySize(0, mInitalPoint);
            if (TextUtils.isEmpty(SystemProperties.get("persist.sys.miui_resolution", (String) null))) {
                return getDefaultDisplayDensity(i);
            }
            return Math.round(((r0.getInitialDisplayDensity(i) * Integer.valueOf(r1.split(",")[0]).intValue()) * 1.0f) / mInitalPoint.x);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    private static int getDefaultDisplayDensity(int i) {
        try {
            return WindowManagerGlobal.getWindowManagerService().getInitialDisplayDensity(i);
        } catch (RemoteException unused) {
            return -1;
        }
    }

    public static String[] getEntries(Context context) {
        return context != null ? context.getResources().getStringArray(R.array.screen_zoom_level) : new String[0];
    }

    public static String[] getEntriesFontSize(Context context) {
        ArrayList arrayList = new ArrayList();
        if (context != null) {
            Iterator<Integer> it = PageLayoutFragment.PAGE_LAYOUT_TITLE.values().iterator();
            while (it.hasNext()) {
                arrayList.add(context.getResources().getString(it.next().intValue()));
            }
            return (String[]) arrayList.toArray(new String[0]);
        }
        return new String[0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getLastZoomLevel(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "key_screen_zoom_level", 1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isBiggerMode(Context context, int i) {
        return i > getLastZoomLevel(context);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isSmallerMode(Context context, int i) {
        return i < getLastZoomLevel(context);
    }

    private static void saveZoomLevel(Context context, int i) {
        Settings.System.putInt(context.getContentResolver(), "key_screen_zoom_level", i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void setZoomLevel(Context context, int i) {
        int i2;
        int i3 = 0;
        int currentDisplayDensity = getCurrentDisplayDensity(0);
        Display[] displays = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplays();
        if (i == 0) {
            int i4 = (int) (currentDisplayDensity * 0.8f);
            if (i4 > 0) {
                if (!SettingsFeatures.isFoldDevice() || displays == null || displays.length == 0) {
                    DisplayDensityUtils.setForcedDisplayDensity(0, i4);
                } else {
                    int length = displays.length;
                    while (i3 < length) {
                        DisplayDensityUtils.setForcedDisplayDensity(displays[i3].getDisplayId(), i4);
                        i3++;
                    }
                }
            }
        } else if (i != 1) {
            if (i == 2 && (i2 = (int) (currentDisplayDensity * 1.05f)) > 0) {
                if (!SettingsFeatures.isFoldDevice() || displays == null || displays.length == 0) {
                    DisplayDensityUtils.setForcedDisplayDensity(0, i2);
                } else {
                    int length2 = displays.length;
                    while (i3 < length2) {
                        DisplayDensityUtils.setForcedDisplayDensity(displays[i3].getDisplayId(), i2);
                        i3++;
                    }
                }
            }
        } else if (!SettingsFeatures.isFoldDevice() || displays == null || displays.length == 0) {
            DisplayDensityUtils.clearForcedDisplayDensity(0);
        } else {
            int length3 = displays.length;
            while (i3 < length3) {
                DisplayDensityUtils.clearForcedDisplayDensity(displays[i3].getDisplayId());
                i3++;
            }
        }
        saveZoomLevel(context, i);
    }
}
