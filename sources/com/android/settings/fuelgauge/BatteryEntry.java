package com.android.settings.fuelgauge;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.BatteryConsumer;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UidBatteryConsumer;
import android.os.UserBatteryConsumer;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.DebugUtils;
import android.util.Log;
import com.android.settings.R;
import com.android.settingslib.Utils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import miui.content.res.ThemeResources;

/* loaded from: classes.dex */
public class BatteryEntry {
    private static NameAndIconLoader mRequestThread;
    static Handler sHandler;
    public Drawable icon;
    public int iconId;
    private final BatteryConsumer mBatteryConsumer;
    private double mConsumedPower;
    private final int mConsumerType;
    private final Context mContext;
    private String mDefaultPackageName;
    private final boolean mIsHidden;
    private final int mPowerComponentId;
    private long mTimeInBackgroundMs;
    private long mTimeInForegroundMs;
    private final int mUid;
    private long mUsageDurationMs;
    public String name;
    public double percent;
    static final HashMap<String, UidToDetail> sUidCache = new HashMap<>();
    static final ArrayList<BatteryEntry> sRequestQueue = new ArrayList<>();
    static Locale sCurrentLocale = null;
    public static final Comparator<BatteryEntry> COMPARATOR = new Comparator() { // from class: com.android.settings.fuelgauge.BatteryEntry$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            int lambda$static$0;
            lambda$static$0 = BatteryEntry.lambda$static$0((BatteryEntry) obj, (BatteryEntry) obj2);
            return lambda$static$0;
        }
    };

    /* loaded from: classes.dex */
    public static final class NameAndIcon {
        public final Drawable icon;
        public final int iconId;
        public final String name;
        public final String packageName;

        public NameAndIcon(String str, Drawable drawable, int i) {
            this(str, null, drawable, i);
        }

        public NameAndIcon(String str, String str2, Drawable drawable, int i) {
            this.name = str;
            this.icon = drawable;
            this.iconId = i;
            this.packageName = str2;
        }
    }

    /* loaded from: classes.dex */
    private static class NameAndIconLoader extends Thread {
        private boolean mAbort;

        public NameAndIconLoader() {
            super("BatteryUsage Icon Loader");
            this.mAbort = false;
        }

        public void abort() {
            this.mAbort = true;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            BatteryEntry remove;
            while (true) {
                ArrayList<BatteryEntry> arrayList = BatteryEntry.sRequestQueue;
                synchronized (arrayList) {
                    if (arrayList.isEmpty() || this.mAbort) {
                        break;
                    }
                    remove = arrayList.remove(0);
                }
                NameAndIcon loadNameAndIcon = BatteryEntry.loadNameAndIcon(remove.mContext, remove.getUid(), BatteryEntry.sHandler, remove, remove.mDefaultPackageName, remove.name, remove.icon);
                if (loadNameAndIcon != null) {
                    remove.icon = loadNameAndIcon.icon;
                    remove.name = loadNameAndIcon.name;
                    remove.mDefaultPackageName = loadNameAndIcon.packageName;
                }
            }
            Handler handler = BatteryEntry.sHandler;
            if (handler != null) {
                handler.sendEmptyMessage(2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class UidToDetail {
        Drawable icon;
        String name;
        String packageName;

        UidToDetail() {
        }
    }

    public BatteryEntry(Context context, int i, double d, double d2, long j) {
        this.mContext = context;
        this.mBatteryConsumer = null;
        this.mUid = -1;
        this.mIsHidden = false;
        this.mPowerComponentId = i;
        this.mConsumedPower = i != 0 ? d - d2 : d;
        this.mUsageDurationMs = j;
        this.mConsumerType = 3;
        NameAndIcon nameAndIconFromPowerComponent = getNameAndIconFromPowerComponent(context, i);
        int i2 = nameAndIconFromPowerComponent.iconId;
        this.iconId = i2;
        this.name = nameAndIconFromPowerComponent.name;
        if (i2 != 0) {
            this.icon = context.getDrawable(i2);
        }
    }

    public BatteryEntry(Context context, int i, String str, double d, double d2) {
        this.mContext = context;
        this.mBatteryConsumer = null;
        this.mUid = -1;
        this.mIsHidden = false;
        this.mPowerComponentId = i;
        int i2 = R.drawable.ic_power_system;
        this.iconId = i2;
        this.icon = context.getDrawable(i2);
        this.name = str;
        this.mConsumedPower = i != 0 ? d - d2 : d;
        this.mConsumerType = 3;
    }

    public BatteryEntry(Context context, Handler handler, UserManager userManager, BatteryConsumer batteryConsumer, boolean z, int i, String[] strArr, String str) {
        this(context, handler, userManager, batteryConsumer, z, i, strArr, str, true);
    }

    public BatteryEntry(Context context, Handler handler, UserManager userManager, BatteryConsumer batteryConsumer, boolean z, int i, String[] strArr, String str, boolean z2) {
        sHandler = handler;
        this.mContext = context;
        this.mBatteryConsumer = batteryConsumer;
        this.mIsHidden = z;
        this.mDefaultPackageName = str;
        this.mPowerComponentId = -1;
        if (!(batteryConsumer instanceof UidBatteryConsumer)) {
            if (!(batteryConsumer instanceof UserBatteryConsumer)) {
                throw new IllegalArgumentException("Unsupported battery consumer: " + batteryConsumer);
            }
            this.mUid = -1;
            this.mConsumerType = 2;
            this.mConsumedPower = batteryConsumer.getConsumedPower();
            NameAndIcon nameAndIconFromUserId = getNameAndIconFromUserId(context, ((UserBatteryConsumer) batteryConsumer).getUserId());
            this.icon = nameAndIconFromUserId.icon;
            this.name = nameAndIconFromUserId.name;
            return;
        }
        this.mUid = i;
        this.mConsumerType = 1;
        this.mConsumedPower = batteryConsumer.getConsumedPower();
        UidBatteryConsumer uidBatteryConsumer = (UidBatteryConsumer) batteryConsumer;
        if (this.mDefaultPackageName == null) {
            if (strArr == null || strArr.length != 1) {
                this.mDefaultPackageName = uidBatteryConsumer.getPackageWithHighestDrain();
            } else {
                this.mDefaultPackageName = strArr[0];
            }
        }
        if (this.mDefaultPackageName != null) {
            PackageManager packageManager = context.getPackageManager();
            try {
                this.name = packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mDefaultPackageName, 0)).toString();
            } catch (PackageManager.NameNotFoundException unused) {
                Log.d("BatteryEntry", "PackageManager failed to retrieve ApplicationInfo for: " + this.mDefaultPackageName);
                this.name = this.mDefaultPackageName;
            }
        }
        getQuickNameIconForUid(i, strArr, z2);
        this.mTimeInForegroundMs = uidBatteryConsumer.getTimeInStateMs(0);
        this.mTimeInBackgroundMs = uidBatteryConsumer.getTimeInStateMs(1);
    }

    public static void clearUidCache() {
        sUidCache.clear();
    }

    public static NameAndIcon getNameAndIconFromPowerComponent(Context context, int i) {
        String string;
        int i2;
        if (i == 0) {
            string = context.getResources().getString(R.string.power_screen);
            i2 = R.drawable.ic_settings_display;
        } else if (i == 6) {
            string = context.getResources().getString(R.string.power_flashlight);
            i2 = R.drawable.ic_settings_display;
        } else if (i == 8) {
            string = context.getResources().getString(R.string.power_cell);
            i2 = R.drawable.ic_cellular_1_bar;
        } else if (i == 11) {
            string = context.getResources().getString(R.string.power_wifi);
            i2 = R.drawable.ic_settings_wireless;
        } else if (i == 2) {
            string = context.getResources().getString(R.string.power_bluetooth);
            i2 = 17302853;
        } else if (i != 3) {
            switch (i) {
                case 13:
                case 16:
                    string = context.getResources().getString(R.string.power_idle);
                    i2 = R.drawable.ic_settings_phone_idle;
                    break;
                case 14:
                    string = context.getResources().getString(R.string.power_phone);
                    i2 = R.drawable.ic_settings_voice_calls;
                    break;
                case 15:
                    string = context.getResources().getString(R.string.ambient_display_screen_title);
                    i2 = R.drawable.ic_settings_aod;
                    break;
                default:
                    string = DebugUtils.constantToString(BatteryConsumer.class, "POWER_COMPONENT_", i);
                    i2 = R.drawable.ic_power_system;
                    break;
            }
        } else {
            string = context.getResources().getString(R.string.power_camera);
            i2 = R.drawable.ic_settings_camera;
        }
        return new NameAndIcon(string, null, i2);
    }

    public static NameAndIcon getNameAndIconFromUid(Context context, String str, int i) {
        Drawable drawable = context.getDrawable(R.drawable.ic_power_system);
        if (i == 0) {
            str = context.getResources().getString(R.string.process_kernel_label);
        } else if ("mediaserver".equals(str)) {
            str = context.getResources().getString(R.string.process_mediaserver_label);
        } else if ("dex2oat".equals(str) || "dex2oat32".equals(str) || "dex2oat64".equals(str)) {
            str = context.getResources().getString(R.string.process_dex2oat_label);
        }
        return new NameAndIcon(str, drawable, 0);
    }

    public static NameAndIcon getNameAndIconFromUserId(Context context, int i) {
        String string;
        Drawable drawable;
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        UserInfo userInfo = userManager.getUserInfo(i);
        if (userInfo != null) {
            drawable = Utils.getUserIcon(context, userManager, userInfo);
            string = Utils.getUserLabel(context, userInfo);
        } else {
            string = context.getResources().getString(R.string.running_process_item_removed_user_label);
            drawable = null;
        }
        return new NameAndIcon(string, drawable, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$static$0(BatteryEntry batteryEntry, BatteryEntry batteryEntry2) {
        return Double.compare(batteryEntry2.getConsumedPower(), batteryEntry.getConsumedPower());
    }

    public static NameAndIcon loadNameAndIcon(Context context, int i, Handler handler, BatteryEntry batteryEntry, String str, String str2, Drawable drawable) {
        String str3;
        String str4;
        Drawable drawable2;
        CharSequence text;
        Drawable drawable3;
        if (i == 0 || i == -1) {
            return null;
        }
        PackageManager packageManager = context.getPackageManager();
        String[] packagesForUid = i == 1000 ? new String[]{ThemeResources.FRAMEWORK_PACKAGE} : packageManager.getPackagesForUid(i);
        int i2 = 0;
        if (packagesForUid != null) {
            int length = packagesForUid.length;
            String[] strArr = new String[length];
            System.arraycopy(packagesForUid, 0, strArr, 0, packagesForUid.length);
            IPackageManager packageManager2 = AppGlobals.getPackageManager();
            int userId = UserHandle.getUserId(i);
            str3 = str;
            int i3 = 0;
            while (true) {
                if (i3 >= length) {
                    drawable2 = drawable;
                    break;
                }
                try {
                    ApplicationInfo applicationInfo = packageManager2.getApplicationInfo(strArr[i3], i2, userId);
                    if (applicationInfo == null) {
                        Log.d("BatteryEntry", "Retrieving null app info for package " + strArr[i3] + ", user " + userId);
                    } else {
                        CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                        if (loadLabel != null) {
                            strArr[i3] = loadLabel.toString();
                        }
                        if (applicationInfo.icon != 0) {
                            str3 = packagesForUid[i3];
                            drawable2 = applicationInfo.loadIcon(packageManager);
                            break;
                        }
                        continue;
                    }
                } catch (RemoteException e) {
                    Log.d("BatteryEntry", "Error while retrieving app info for package " + strArr[i3] + ", user " + userId, e);
                }
                i3++;
                i2 = 0;
            }
            if (length == 1) {
                str4 = strArr[0];
            } else {
                int i4 = 0;
                int length2 = packagesForUid.length;
                str4 = str2;
                int i5 = 0;
                while (i5 < length2) {
                    String str5 = packagesForUid[i5];
                    try {
                        PackageInfo packageInfo = packageManager2.getPackageInfo(str5, i4, userId);
                        if (packageInfo == null) {
                            Log.d("BatteryEntry", "Retrieving null package info for package " + str5 + ", user " + userId);
                        } else {
                            int i6 = packageInfo.sharedUserLabel;
                            if (i6 != 0 && (text = packageManager.getText(str5, i6, packageInfo.applicationInfo)) != null) {
                                String charSequence = text.toString();
                                try {
                                    ApplicationInfo applicationInfo2 = packageInfo.applicationInfo;
                                    if (applicationInfo2.icon != 0) {
                                        try {
                                            drawable3 = applicationInfo2.loadIcon(packageManager);
                                            str3 = str5;
                                        } catch (RemoteException e2) {
                                            e = e2;
                                            str4 = charSequence;
                                            str3 = str5;
                                            Log.d("BatteryEntry", "Error while retrieving package info for package " + str5 + ", user " + userId, e);
                                            i5++;
                                            i4 = 0;
                                        }
                                    } else {
                                        drawable3 = drawable2;
                                    }
                                    drawable2 = drawable3;
                                    str4 = charSequence;
                                    break;
                                } catch (RemoteException e3) {
                                    e = e3;
                                    str4 = charSequence;
                                }
                            }
                        }
                    } catch (RemoteException e4) {
                        e = e4;
                    }
                    i5++;
                    i4 = 0;
                }
            }
        } else {
            str3 = str;
            str4 = str2;
            drawable2 = drawable;
        }
        String num = Integer.toString(i);
        if (drawable2 == null) {
            drawable2 = packageManager.getDefaultActivityIcon();
        }
        UidToDetail uidToDetail = new UidToDetail();
        uidToDetail.name = str4;
        uidToDetail.icon = drawable2;
        uidToDetail.packageName = str3;
        sUidCache.put(num, uidToDetail);
        if (handler != null) {
            handler.sendMessage(handler.obtainMessage(1, batteryEntry));
        }
        return new NameAndIcon(str4, str3, drawable2, 0);
    }

    public static void startRequestQueue() {
        if (sHandler != null) {
            ArrayList<BatteryEntry> arrayList = sRequestQueue;
            synchronized (arrayList) {
                if (!arrayList.isEmpty()) {
                    NameAndIconLoader nameAndIconLoader = mRequestThread;
                    if (nameAndIconLoader != null) {
                        nameAndIconLoader.abort();
                    }
                    NameAndIconLoader nameAndIconLoader2 = new NameAndIconLoader();
                    mRequestThread = nameAndIconLoader2;
                    nameAndIconLoader2.setPriority(1);
                    mRequestThread.start();
                    arrayList.notify();
                }
            }
        }
    }

    public static void stopRequestQueue() {
        ArrayList<BatteryEntry> arrayList = sRequestQueue;
        synchronized (arrayList) {
            NameAndIconLoader nameAndIconLoader = mRequestThread;
            if (nameAndIconLoader != null) {
                nameAndIconLoader.abort();
                mRequestThread = null;
                arrayList.clear();
                sHandler = null;
            }
        }
    }

    public void add(BatteryConsumer batteryConsumer) {
        this.mConsumedPower += batteryConsumer.getConsumedPower();
        if (batteryConsumer instanceof UidBatteryConsumer) {
            UidBatteryConsumer uidBatteryConsumer = (UidBatteryConsumer) batteryConsumer;
            this.mTimeInForegroundMs += uidBatteryConsumer.getTimeInStateMs(0);
            this.mTimeInBackgroundMs += uidBatteryConsumer.getTimeInStateMs(1);
            if (this.mDefaultPackageName == null) {
                this.mDefaultPackageName = uidBatteryConsumer.getPackageWithHighestDrain();
            }
        }
    }

    public double getConsumedPower() {
        return this.mConsumedPower;
    }

    public String getDefaultPackageName() {
        return this.mDefaultPackageName;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public String getKey() {
        BatteryConsumer batteryConsumer = this.mBatteryConsumer;
        if (batteryConsumer instanceof UidBatteryConsumer) {
            return Integer.toString(this.mUid);
        }
        if (batteryConsumer instanceof UserBatteryConsumer) {
            return "U|" + this.mBatteryConsumer.getUserId();
        }
        return "S|" + this.mPowerComponentId;
    }

    public String getLabel() {
        return this.name;
    }

    void getQuickNameIconForUid(int i, String[] strArr, boolean z) {
        Locale locale = Locale.getDefault();
        if (sCurrentLocale != locale) {
            clearUidCache();
            sCurrentLocale = locale;
        }
        String num = Integer.toString(i);
        HashMap<String, UidToDetail> hashMap = sUidCache;
        if (hashMap.containsKey(num)) {
            UidToDetail uidToDetail = hashMap.get(num);
            this.mDefaultPackageName = uidToDetail.packageName;
            this.name = uidToDetail.name;
            this.icon = uidToDetail.icon;
            return;
        }
        if (strArr == null || strArr.length == 0) {
            NameAndIcon nameAndIconFromUid = getNameAndIconFromUid(this.mContext, this.name, i);
            this.icon = nameAndIconFromUid.icon;
            this.name = nameAndIconFromUid.name;
        } else {
            this.icon = this.mContext.getPackageManager().getDefaultActivityIcon();
        }
        if (sHandler == null || !z) {
            return;
        }
        ArrayList<BatteryEntry> arrayList = sRequestQueue;
        synchronized (arrayList) {
            arrayList.add(this);
        }
    }

    public long getTimeInBackgroundMs() {
        if (this.mBatteryConsumer instanceof UidBatteryConsumer) {
            return this.mTimeInBackgroundMs;
        }
        return 0L;
    }

    public long getTimeInForegroundMs() {
        return this.mBatteryConsumer instanceof UidBatteryConsumer ? this.mTimeInForegroundMs : this.mUsageDurationMs;
    }

    public int getUid() {
        return this.mUid;
    }

    public boolean isAppEntry() {
        return this.mBatteryConsumer instanceof UidBatteryConsumer;
    }

    public boolean isHidden() {
        return this.mIsHidden;
    }

    public boolean isUserEntry() {
        return this.mBatteryConsumer instanceof UserBatteryConsumer;
    }
}
