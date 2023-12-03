package com.iqiyi.android.qigsaw.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.LruCache;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.extension.AABExtension;
import com.iqiyi.android.qigsaw.core.splitreport.SplitBriefInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfo;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManager;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitInfoManagerService;

/* loaded from: classes2.dex */
public abstract class SplitActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    private static final String SPLIT_NAME_BASE = "base";
    private static final String TAG = "SplitActivityLifecycleCallbacks";
    private final LruCache<String, String> splitActivityNameCache = new LruCache<>(20);
    private final LruCache<String, SplitBriefInfo> splitBriefInfoCache = new LruCache<>(10);

    private SplitBriefInfo getSplitBriefInfoForActivity(Activity activity) {
        SplitInfoManager splitInfoManagerService;
        SplitInfo splitInfo;
        String splitNameForActivityName = getSplitNameForActivityName(activity);
        if (SPLIT_NAME_BASE.equals(splitNameForActivityName)) {
            return null;
        }
        SplitBriefInfo splitBriefInfo = this.splitBriefInfoCache.get(splitNameForActivityName);
        if (splitBriefInfo != null || (splitInfoManagerService = SplitInfoManagerService.getInstance()) == null || (splitInfo = splitInfoManagerService.getSplitInfo(activity, splitNameForActivityName)) == null) {
            return splitBriefInfo;
        }
        SplitBriefInfo splitBriefInfo2 = new SplitBriefInfo(splitInfo.getSplitName(), splitInfo.getSplitVersion(), splitInfo.isBuiltIn());
        this.splitBriefInfoCache.put(splitNameForActivityName, splitBriefInfo2);
        return splitBriefInfo2;
    }

    private String getSplitNameForActivityName(Activity activity) {
        String name = activity.getClass().getName();
        String str = this.splitActivityNameCache.get(name);
        if (str == null) {
            str = AABExtension.getInstance().getSplitNameForActivityName(name);
            if (str == null) {
                str = SPLIT_NAME_BASE;
            }
            this.splitActivityNameCache.put(name, str);
        }
        return str;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public final void onActivityCreated(Activity activity, Bundle bundle) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityCreated(splitBriefInfoForActivity, activity, bundle);
            SplitLog.i(TAG, "Activity %s of split %s is created.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityDestroyed(splitBriefInfoForActivity, activity);
            SplitLog.i(TAG, "Activity %s of split %s is destroyed.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityPaused(splitBriefInfoForActivity, activity);
            SplitLog.i(TAG, "Activity %s of split %s is paused.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityResumed(splitBriefInfoForActivity, activity);
            SplitLog.i(TAG, "Activity %s of split %s is resumed.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivitySaveInstanceState(splitBriefInfoForActivity, activity, bundle);
            SplitLog.i(TAG, "Activity %s of split %s is saving state.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityStarted(splitBriefInfoForActivity, activity);
            SplitLog.i(TAG, "Activity %s of split %s is started.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        SplitBriefInfo splitBriefInfoForActivity = getSplitBriefInfoForActivity(activity);
        if (splitBriefInfoForActivity != null) {
            onSplitActivityStopped(splitBriefInfoForActivity, activity);
            SplitLog.i(TAG, "Activity %s of split %s is stopped.", activity.getClass().getName(), splitBriefInfoForActivity.toString());
        }
    }

    public abstract void onSplitActivityCreated(SplitBriefInfo splitBriefInfo, Activity activity, Bundle bundle);

    public abstract void onSplitActivityDestroyed(SplitBriefInfo splitBriefInfo, Activity activity);

    public abstract void onSplitActivityPaused(SplitBriefInfo splitBriefInfo, Activity activity);

    public abstract void onSplitActivityResumed(SplitBriefInfo splitBriefInfo, Activity activity);

    public abstract void onSplitActivitySaveInstanceState(SplitBriefInfo splitBriefInfo, Activity activity, Bundle bundle);

    public abstract void onSplitActivityStarted(SplitBriefInfo splitBriefInfo, Activity activity);

    public abstract void onSplitActivityStopped(SplitBriefInfo splitBriefInfo, Activity activity);
}
