package com.iqiyi.android.qigsaw.core.extension;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
final class AABExtensionManagerImpl implements AABExtensionManager {
    private SplitComponentInfoProvider infoProvider;
    private List<String> splitActivities;
    private Map<String, List<String>> splitActivitiesMap;
    private List<String> splitReceivers;
    private List<String> splitServices;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AABExtensionManagerImpl(SplitComponentInfoProvider splitComponentInfoProvider) {
        this.infoProvider = splitComponentInfoProvider;
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    @SuppressLint({"DiscouragedPrivateApi"})
    public void activeApplication(Application application, Context context) throws AABExtensionException {
        if (application != null) {
            Throwable e = null;
            try {
                Method declaredMethod = Application.class.getDeclaredMethod("attach", Context.class);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(application, context);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
                e = e2;
            }
            if (e != null) {
                throw new AABExtensionException(e);
            }
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    @SuppressLint({"PrivateApi"})
    public Application createApplication(ClassLoader classLoader, String str) throws AABExtensionException {
        String splitApplicationName = this.infoProvider.getSplitApplicationName(str);
        if (TextUtils.isEmpty(splitApplicationName)) {
            e = null;
        } else {
            try {
                return (Application) classLoader.loadClass(splitApplicationName).newInstance();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e = e;
            }
        }
        if (e == null) {
            return null;
        }
        throw new AABExtensionException(e);
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    public Map<String, List<String>> getSplitActivitiesMap() {
        if (this.splitActivitiesMap == null) {
            this.splitActivitiesMap = this.infoProvider.getSplitActivitiesMap();
        }
        return this.splitActivitiesMap;
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    public boolean isSplitActivity(String str) {
        if (this.splitActivities == null) {
            Collection<List<String>> values = getSplitActivitiesMap().values();
            ArrayList arrayList = new ArrayList(0);
            if (!values.isEmpty()) {
                Iterator<List<String>> it = values.iterator();
                while (it.hasNext()) {
                    arrayList.addAll(it.next());
                }
            }
            this.splitActivities = arrayList;
        }
        return this.splitActivities.contains(str);
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    public boolean isSplitReceiver(String str) {
        if (this.splitReceivers == null) {
            this.splitReceivers = this.infoProvider.getSplitReceivers();
        }
        return this.splitReceivers.contains(str);
    }

    @Override // com.iqiyi.android.qigsaw.core.extension.AABExtensionManager
    public boolean isSplitService(String str) {
        if (this.splitServices == null) {
            this.splitServices = this.infoProvider.getSplitServices();
        }
        return this.splitServices.contains(str);
    }
}
