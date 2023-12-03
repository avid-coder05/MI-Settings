package com.iqiyi.android.qigsaw.core.splitload;

import android.app.Application;
import android.content.Context;
import com.iqiyi.android.qigsaw.core.extension.AABExtension;
import com.iqiyi.android.qigsaw.core.extension.AABExtensionException;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes2.dex */
final class SplitActivator {
    private static final Map<String, Application> sSplitApplicationMap = new HashMap();
    private final AABExtension aabExtension = AABExtension.getInstance();
    private final Context appContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitActivator(Context context) {
        this.appContext = context;
    }

    private boolean debuggable() {
        try {
            return (this.appContext.getApplicationInfo().flags & 2) != 0;
        } catch (Throwable unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void attachSplitApplication(Application application) throws SplitLoadException {
        try {
            this.aabExtension.activeApplication(application, this.appContext);
        } catch (AABExtensionException e) {
            throw new SplitLoadException(-25, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void createAndActivateSplitContentProviders(ClassLoader classLoader, String str) throws SplitLoadException {
        try {
            this.aabExtension.createAndActivateSplitProviders(classLoader, str);
        } catch (AABExtensionException e) {
            throw new SplitLoadException(-26, e);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Application createSplitApplication(ClassLoader classLoader, String str) throws SplitLoadException {
        try {
            Application createApplication = this.aabExtension.createApplication(classLoader, str);
            if (createApplication != null) {
                sSplitApplicationMap.put(str, createApplication);
            }
            return createApplication;
        } catch (Throwable th) {
            if (!debuggable() || (th instanceof AABExtensionException)) {
                throw new SplitLoadException(-24, th);
            }
            throw new RuntimeException(th);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invokeOnCreateForSplitApplication(Application application) throws SplitLoadException {
        if (application != null) {
            try {
                HiddenApiReflection.findMethod((Class<?>) Application.class, "onCreate", (Class<?>[]) new Class[0]).invoke(application, new Object[0]);
            } catch (Throwable th) {
                if (!debuggable()) {
                    throw new SplitLoadException(-25, th);
                }
                throw new RuntimeException(th);
            }
        }
    }
}
