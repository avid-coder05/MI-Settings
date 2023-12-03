package com.android.settingslib.applications;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.util.IconDrawableFactory;
import com.android.settingslib.widget.CandidateInfo;

/* loaded from: classes2.dex */
public class DefaultAppInfo extends CandidateInfo {
    public final ComponentName componentName;
    private final Context mContext;
    protected final PackageManager mPm;
    public final PackageItemInfo packageItemInfo;
    public final String summary;
    public final int userId;

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, ComponentName componentName) {
        this(context, packageManager, i, componentName, (String) null, true);
    }

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, ComponentName componentName, String str, boolean z) {
        super(z);
        this.mContext = context;
        this.mPm = packageManager;
        this.packageItemInfo = null;
        this.userId = i;
        this.componentName = componentName;
        this.summary = str;
    }

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo) {
        this(context, packageManager, i, packageItemInfo, (String) null, true);
    }

    public DefaultAppInfo(Context context, PackageManager packageManager, int i, PackageItemInfo packageItemInfo, String str, boolean z) {
        super(z);
        this.mContext = context;
        this.mPm = packageManager;
        this.userId = i;
        this.packageItemInfo = packageItemInfo;
        this.componentName = null;
        this.summary = str;
    }

    private ComponentInfo getComponentInfo() {
        try {
            ActivityInfo activityInfo = AppGlobals.getPackageManager().getActivityInfo(this.componentName, 0, this.userId);
            return activityInfo == null ? AppGlobals.getPackageManager().getServiceInfo(this.componentName, 0, this.userId) : activityInfo;
        } catch (RemoteException unused) {
            return null;
        }
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public String getKey() {
        ComponentName componentName = this.componentName;
        if (componentName != null) {
            return componentName.flattenToString();
        }
        PackageItemInfo packageItemInfo = this.packageItemInfo;
        if (packageItemInfo != null) {
            return packageItemInfo.packageName;
        }
        return null;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public Drawable loadIcon() {
        IconDrawableFactory newInstance = IconDrawableFactory.newInstance(this.mContext);
        if (this.componentName != null) {
            try {
                ComponentInfo componentInfo = getComponentInfo();
                ApplicationInfo applicationInfoAsUser = this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId);
                return componentInfo != null ? newInstance.getBadgedIcon(componentInfo, applicationInfoAsUser, this.userId) : newInstance.getBadgedIcon(applicationInfoAsUser);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        PackageItemInfo packageItemInfo = this.packageItemInfo;
        if (packageItemInfo != null) {
            try {
                return newInstance.getBadgedIcon(this.packageItemInfo, this.mPm.getApplicationInfoAsUser(packageItemInfo.packageName, 0, this.userId), this.userId);
            } catch (PackageManager.NameNotFoundException unused2) {
            }
        }
        return null;
    }

    @Override // com.android.settingslib.widget.CandidateInfo
    public CharSequence loadLabel() {
        if (this.componentName != null) {
            try {
                ComponentInfo componentInfo = getComponentInfo();
                return componentInfo != null ? componentInfo.loadLabel(this.mPm) : this.mPm.getApplicationInfoAsUser(this.componentName.getPackageName(), 0, this.userId).loadLabel(this.mPm);
            } catch (PackageManager.NameNotFoundException unused) {
                return null;
            }
        }
        PackageItemInfo packageItemInfo = this.packageItemInfo;
        if (packageItemInfo != null) {
            return packageItemInfo.loadLabel(this.mPm);
        }
        return null;
    }
}
