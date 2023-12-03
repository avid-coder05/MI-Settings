package com.iqiyi.android.qigsaw.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import com.google.android.play.core.splitinstall.SplitInstallHelper;
import com.iqiyi.android.qigsaw.core.common.CompatBundle;
import com.iqiyi.android.qigsaw.core.common.ICompatBundle;

/* loaded from: classes2.dex */
class InjectActivityResource implements Application.ActivityLifecycleCallbacks {
    private static InjectActivityResource cb;

    InjectActivityResource() {
    }

    public static void inject(Application application) {
        ICompatBundle iCompatBundle = CompatBundle.instance;
        if (iCompatBundle != null && iCompatBundle.injectActivityResource() && cb == null) {
            InjectActivityResource injectActivityResource = new InjectActivityResource();
            cb = injectActivityResource;
            application.registerActivityLifecycleCallbacks(injectActivityResource);
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPreCreated(Activity activity, Bundle bundle) {
        SplitInstallHelper.loadResources(activity, activity.getResources());
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
    }
}
