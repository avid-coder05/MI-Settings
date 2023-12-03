package com.google.android.play.core.splitinstall;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import com.iqiyi.android.qigsaw.core.splitload.SplitCompatResourcesLoader;
import com.iqiyi.android.qigsaw.core.splitload.SplitLibraryLoaderHelper;
import java.io.File;

/* loaded from: classes2.dex */
public class SplitInstallHelper {
    private SplitInstallHelper() {
    }

    @SuppressLint({"UnsafeDynamicallyLoadedCode"})
    public static void loadLibrary(Context context, String str) {
        if (SplitLibraryLoaderHelper.loadSplitLibrary(context, str)) {
            return;
        }
        try {
            System.loadLibrary(str);
        } catch (UnsatisfiedLinkError e) {
            boolean z = false;
            try {
                String str2 = context.getApplicationInfo().nativeLibraryDir;
                String mapLibraryName = System.mapLibraryName(str);
                StringBuilder sb = new StringBuilder(String.valueOf(str2).length() + 1 + String.valueOf(mapLibraryName).length());
                sb.append(str2);
                sb.append("/");
                sb.append(mapLibraryName);
                String sb2 = sb.toString();
                if (new File(sb2).exists()) {
                    System.load(sb2);
                    z = true;
                }
                if (!z) {
                    throw e;
                }
            } catch (UnsatisfiedLinkError e2) {
                throw e2;
            }
        }
    }

    public static void loadResources(Activity activity, Resources resources) {
        try {
            SplitCompatResourcesLoader.loadResources(activity, resources);
        } catch (Throwable th) {
            throw new RuntimeException("Failed to load activity resources", th);
        }
    }

    public static void loadResources(Service service) {
        try {
            SplitCompatResourcesLoader.loadResources(service, service.getBaseContext().getResources());
        } catch (Throwable th) {
            throw new RuntimeException("Failed to load service resources", th);
        }
    }

    public static void loadResources(BroadcastReceiver broadcastReceiver, Context context) {
        if (context.getClass().getSimpleName().equals("ReceiverRestrictedContext")) {
            try {
                SplitCompatResourcesLoader.loadResources(((ContextWrapper) context).getBaseContext(), context.getResources());
            } catch (Throwable th) {
                throw new RuntimeException("Failed to load receiver resources", th);
            }
        }
    }

    public static void updateAppInfo(Context context) {
    }
}
