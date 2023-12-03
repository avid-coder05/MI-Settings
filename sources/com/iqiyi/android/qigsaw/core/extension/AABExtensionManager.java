package com.iqiyi.android.qigsaw.core.extension;

import android.app.Application;
import android.content.Context;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
interface AABExtensionManager {
    void activeApplication(Application application, Context context) throws AABExtensionException;

    Application createApplication(ClassLoader classLoader, String str) throws AABExtensionException;

    Map<String, List<String>> getSplitActivitiesMap();

    boolean isSplitActivity(String str);

    boolean isSplitReceiver(String str);

    boolean isSplitService(String str);
}
