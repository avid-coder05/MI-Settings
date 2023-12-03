package com.android.settingslib.dream;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.service.dreams.IDreamManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes2.dex */
public class DreamBackend {
    private static DreamBackend sInstance;
    private final DreamInfoComparator mComparator;
    private final Context mContext;
    private final IDreamManager mDreamManager;
    private final boolean mDreamsActivatedOnDockByDefault;
    private final boolean mDreamsActivatedOnSleepByDefault;
    private final boolean mDreamsEnabledByDefault;

    /* loaded from: classes2.dex */
    public static class DreamInfo {
        public CharSequence caption;
        public ComponentName componentName;
        public Drawable icon;
        public boolean isActive;
        public ComponentName settingsComponentName;

        public String toString() {
            StringBuilder sb = new StringBuilder(DreamInfo.class.getSimpleName());
            sb.append('[');
            sb.append(this.caption);
            if (this.isActive) {
                sb.append(",active");
            }
            sb.append(',');
            sb.append(this.componentName);
            if (this.settingsComponentName != null) {
                sb.append("settings=");
                sb.append(this.settingsComponentName);
            }
            sb.append(']');
            return sb.toString();
        }
    }

    /* loaded from: classes2.dex */
    private static class DreamInfoComparator implements Comparator<DreamInfo> {
        private final ComponentName mDefaultDream;

        public DreamInfoComparator(ComponentName componentName) {
            this.mDefaultDream = componentName;
        }

        private String sortKey(DreamInfo dreamInfo) {
            StringBuilder sb = new StringBuilder();
            sb.append(dreamInfo.componentName.equals(this.mDefaultDream) ? '0' : '1');
            sb.append(dreamInfo.caption);
            return sb.toString();
        }

        @Override // java.util.Comparator
        public int compare(DreamInfo dreamInfo, DreamInfo dreamInfo2) {
            return sortKey(dreamInfo).compareTo(sortKey(dreamInfo2));
        }
    }

    public DreamBackend(Context context) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService("dreams"));
        this.mComparator = new DreamInfoComparator(getDefaultDream());
        this.mDreamsEnabledByDefault = applicationContext.getResources().getBoolean(17891520);
        this.mDreamsActivatedOnSleepByDefault = applicationContext.getResources().getBoolean(17891519);
        this.mDreamsActivatedOnDockByDefault = applicationContext.getResources().getBoolean(17891518);
    }

    private boolean getBoolean(String str, boolean z) {
        return Settings.Secure.getInt(this.mContext.getContentResolver(), str, z ? 1 : 0) == 1;
    }

    private static ComponentName getDreamComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        return new ComponentName(serviceInfo.packageName, serviceInfo.name);
    }

    public static DreamBackend getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DreamBackend(context);
        }
        return sInstance;
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x007a  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0093  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.content.ComponentName getSettingsComponentName(android.content.pm.PackageManager r6, android.content.pm.ResolveInfo r7) {
        /*
            java.lang.String r0 = "DreamBackend"
            r1 = 0
            if (r7 == 0) goto Lbc
            android.content.pm.ServiceInfo r2 = r7.serviceInfo
            if (r2 == 0) goto Lbc
            android.os.Bundle r3 = r2.metaData
            if (r3 != 0) goto Lf
            goto Lbc
        Lf:
            java.lang.String r3 = "android.service.dream"
            android.content.res.XmlResourceParser r2 = r2.loadXmlMetaData(r6, r3)     // Catch: java.lang.Throwable -> L69 java.lang.Throwable -> L70
            if (r2 != 0) goto L22
            java.lang.String r6 = "No android.service.dream meta-data"
            android.util.Log.w(r0, r6)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            if (r2 == 0) goto L21
            r2.close()
        L21:
            return r1
        L22:
            android.content.pm.ServiceInfo r3 = r7.serviceInfo     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            android.content.res.Resources r6 = r6.getResourcesForApplication(r3)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            android.util.AttributeSet r3 = android.util.Xml.asAttributeSet(r2)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
        L2e:
            int r4 = r2.next()     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            r5 = 1
            if (r4 == r5) goto L39
            r5 = 2
            if (r4 == r5) goto L39
            goto L2e
        L39:
            java.lang.String r4 = r2.getName()     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            java.lang.String r5 = "dream"
            boolean r4 = r5.equals(r4)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            if (r4 != 0) goto L4e
            java.lang.String r6 = "Meta-data does not start with dream tag"
            android.util.Log.w(r0, r6)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            r2.close()
            return r1
        L4e:
            int[] r4 = com.android.internal.R.styleable.Dream     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            android.content.res.TypedArray r6 = r6.obtainAttributes(r3, r4)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            r3 = 0
            java.lang.String r3 = r6.getString(r3)     // Catch: java.lang.Throwable -> L63 java.lang.Throwable -> L66
            r6.recycle()     // Catch: java.lang.Throwable -> L61 java.lang.Throwable -> L63
            r2.close()
            r6 = r1
            goto L78
        L61:
            r6 = move-exception
            goto L73
        L63:
            r6 = move-exception
            r1 = r2
            goto L6a
        L66:
            r6 = move-exception
            r3 = r1
            goto L73
        L69:
            r6 = move-exception
        L6a:
            if (r1 == 0) goto L6f
            r1.close()
        L6f:
            throw r6
        L70:
            r6 = move-exception
            r2 = r1
            r3 = r2
        L73:
            if (r2 == 0) goto L78
            r2.close()
        L78:
            if (r6 == 0) goto L93
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error parsing : "
            r2.append(r3)
            android.content.pm.ServiceInfo r7 = r7.serviceInfo
            java.lang.String r7 = r7.packageName
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            android.util.Log.w(r0, r7, r6)
            return r1
        L93:
            if (r3 == 0) goto Lb5
            r6 = 47
            int r6 = r3.indexOf(r6)
            if (r6 >= 0) goto Lb5
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            android.content.pm.ServiceInfo r7 = r7.serviceInfo
            java.lang.String r7 = r7.packageName
            r6.append(r7)
            java.lang.String r7 = "/"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r3 = r6.toString()
        Lb5:
            if (r3 != 0) goto Lb8
            goto Lbc
        Lb8:
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r3)
        Lbc:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.dream.DreamBackend.getSettingsComponentName(android.content.pm.PackageManager, android.content.pm.ResolveInfo):android.content.ComponentName");
    }

    private static void logd(String str, Object... objArr) {
    }

    private void setBoolean(String str, boolean z) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), str, z ? 1 : 0);
    }

    public ComponentName getActiveDream() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return null;
        }
        try {
            ComponentName[] dreamComponents = iDreamManager.getDreamComponents();
            if (dreamComponents == null || dreamComponents.length <= 0) {
                return null;
            }
            return dreamComponents[0];
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to get active dream", e);
            return null;
        }
    }

    public CharSequence getActiveDreamName() {
        ComponentName activeDream = getActiveDream();
        if (activeDream != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                ServiceInfo serviceInfo = packageManager.getServiceInfo(activeDream, 0);
                if (serviceInfo != null) {
                    return serviceInfo.loadLabel(packageManager);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return null;
    }

    public Drawable getActiveIcon() {
        ComponentName activeDream = getActiveDream();
        if (activeDream != null) {
            PackageManager packageManager = this.mContext.getPackageManager();
            try {
                ServiceInfo serviceInfo = packageManager.getServiceInfo(activeDream, 0);
                if (serviceInfo != null) {
                    return serviceInfo.loadIcon(packageManager);
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
        return null;
    }

    public ComponentName getDefaultDream() {
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return null;
        }
        try {
            return iDreamManager.getDefaultDreamComponentForUser(this.mContext.getUserId());
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to get default dream", e);
            return null;
        }
    }

    public List<DreamInfo> getDreamInfos() {
        logd("getDreamInfos()", new Object[0]);
        ComponentName activeDream = getActiveDream();
        PackageManager packageManager = this.mContext.getPackageManager();
        List<ResolveInfo> queryIntentServices = packageManager.queryIntentServices(new Intent("android.service.dreams.DreamService"), 128);
        ArrayList arrayList = new ArrayList(queryIntentServices.size());
        for (ResolveInfo resolveInfo : queryIntentServices) {
            if (resolveInfo.serviceInfo != null) {
                DreamInfo dreamInfo = new DreamInfo();
                dreamInfo.caption = resolveInfo.loadLabel(packageManager);
                dreamInfo.icon = resolveInfo.loadIcon(packageManager);
                ComponentName dreamComponentName = getDreamComponentName(resolveInfo);
                dreamInfo.componentName = dreamComponentName;
                dreamInfo.isActive = dreamComponentName.equals(activeDream);
                dreamInfo.settingsComponentName = getSettingsComponentName(packageManager, resolveInfo);
                arrayList.add(dreamInfo);
            }
        }
        Collections.sort(arrayList, this.mComparator);
        return arrayList;
    }

    public int getWhenToDreamSetting() {
        if (isEnabled()) {
            if (isActivatedOnDock() && isActivatedOnSleep()) {
                return 2;
            }
            if (isActivatedOnDock()) {
                return 1;
            }
            return isActivatedOnSleep() ? 0 : 3;
        }
        return 3;
    }

    public boolean isActivatedOnDock() {
        return getBoolean("screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefault);
    }

    public boolean isActivatedOnSleep() {
        return getBoolean("screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefault);
    }

    public boolean isEnabled() {
        return getBoolean("screensaver_enabled", this.mDreamsEnabledByDefault);
    }

    public void launchSettings(Context context, DreamInfo dreamInfo) {
        logd("launchSettings(%s)", dreamInfo);
        if (dreamInfo == null || dreamInfo.settingsComponentName == null) {
            return;
        }
        context.startActivity(new Intent().setComponent(dreamInfo.settingsComponentName));
    }

    public void setActivatedOnDock(boolean z) {
        logd("setActivatedOnDock(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_activate_on_dock", z);
    }

    public void setActivatedOnSleep(boolean z) {
        logd("setActivatedOnSleep(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_activate_on_sleep", z);
    }

    public void setActiveDream(ComponentName componentName) {
        logd("setActiveDream(%s)", componentName);
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return;
        }
        try {
            ComponentName[] componentNameArr = {componentName};
            if (componentName == null) {
                componentNameArr = null;
            }
            iDreamManager.setDreamComponents(componentNameArr);
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to set active dream to " + componentName, e);
        }
    }

    public void setEnabled(boolean z) {
        logd("setEnabled(%s)", Boolean.valueOf(z));
        setBoolean("screensaver_enabled", z);
    }

    public void setWhenToDream(int i) {
        setEnabled(i != 3);
        if (i == 0) {
            setActivatedOnDock(false);
            setActivatedOnSleep(true);
        } else if (i == 1) {
            setActivatedOnDock(true);
            setActivatedOnSleep(false);
        } else if (i != 2) {
        } else {
            setActivatedOnDock(true);
            setActivatedOnSleep(true);
        }
    }

    public void startDreaming() {
        logd("startDreaming()", new Object[0]);
        IDreamManager iDreamManager = this.mDreamManager;
        if (iDreamManager == null) {
            return;
        }
        try {
            iDreamManager.dream();
        } catch (RemoteException e) {
            Log.w("DreamBackend", "Failed to dream", e);
        }
    }
}
