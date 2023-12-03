package com.iqiyi.android.qigsaw.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Looper;
import android.os.MessageQueue;
import androidx.annotation.Keep;
import com.google.android.play.core.splitcompat.SplitCompat;
import com.iqiyi.android.qigsaw.core.common.ProcessUtil;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.extension.AABExtension;
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitApkInstaller;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallReporterManager;
import com.iqiyi.android.qigsaw.core.splitinstall.SplitUninstallReporterManager;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerService;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadReporterManager;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitInstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitLoadReporter;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUninstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.DefaultSplitUpdateReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitInstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitLoadReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUninstallReporter;
import com.iqiyi.android.qigsaw.core.splitreport.SplitUpdateReporter;
import com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitUpdateReporterManager;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

@Keep
/* loaded from: classes2.dex */
public class Qigsaw {
    private static final AtomicReference<Qigsaw> sReference = new AtomicReference<>();
    private final Context context;
    private final String currentProcessName;
    private final Downloader downloader;
    private final boolean isMainProcess;
    private boolean onApplicationCreated = false;
    private final SplitConfiguration splitConfiguration;

    private Qigsaw(Context context, Downloader downloader, SplitConfiguration splitConfiguration) {
        this.context = context;
        this.downloader = downloader;
        this.splitConfiguration = splitConfiguration;
        String processName = ProcessUtil.getProcessName(context);
        this.currentProcessName = processName;
        this.isMainProcess = context.getPackageName().equals(processName);
        InjectActivityResource.inject((Application) context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void cleanStaleSplits(Context context) {
        try {
            Intent intent = new Intent();
            intent.setClassName(context, "com.iqiyi.android.qigsaw.core.splitinstall.SplitCleanService");
            context.startService(intent);
        } catch (Exception unused) {
        }
    }

    public static void install(Context context, Downloader downloader) {
        install(context, downloader, SplitConfiguration.newBuilder().build());
    }

    public static void install(Context context, Downloader downloader, SplitConfiguration splitConfiguration) {
        AtomicReference<Qigsaw> atomicReference = sReference;
        if (atomicReference.get() == null) {
            atomicReference.set(new Qigsaw(context, downloader, splitConfiguration));
        }
        instance().onBaseContextAttached();
    }

    private static Qigsaw instance() {
        AtomicReference<Qigsaw> atomicReference = sReference;
        if (atomicReference.get() != null) {
            return atomicReference.get();
        }
        throw new RuntimeException("Have you invoke Qigsaw#install(...)?");
    }

    public static void onApplicationCreated() {
        instance().onCreated();
    }

    public static void onApplicationGetResources(Resources resources) {
        if (!SplitLoadManagerService.hasInstance() || resources == null) {
            return;
        }
        SplitLoadManagerService.getInstance().getResources(resources);
    }

    private void onBaseContextAttached() {
        SplitBaseInfoProvider.setPackageName(this.context.getPackageName());
        boolean isQigsawMode = SplitBaseInfoProvider.isQigsawMode();
        if (this.isMainProcess) {
            SplitUpdateReporter splitUpdateReporter = this.splitConfiguration.updateReporter;
            if (splitUpdateReporter == null) {
                splitUpdateReporter = new DefaultSplitUpdateReporter(this.context);
            }
            SplitUpdateReporterManager.install(splitUpdateReporter);
        }
        Context context = this.context;
        SplitConfiguration splitConfiguration = this.splitConfiguration;
        SplitLoadManagerService.install(context, splitConfiguration.splitLoadMode, isQigsawMode, this.isMainProcess, this.currentProcessName, splitConfiguration.workProcesses, splitConfiguration.forbiddenWorkProcesses);
        SplitLoadManagerService.getInstance().clear();
        SplitLoadManagerService.getInstance().injectPathClassloader();
        AABExtension.getInstance().clear();
        AABExtension.getInstance().createAndActiveSplitApplication(this.context, isQigsawMode);
        SplitCompat.install(this.context);
    }

    private void onCreated() {
        AABExtension.getInstance().onApplicationCreate();
        SplitLoadReporter splitLoadReporter = this.splitConfiguration.loadReporter;
        if (splitLoadReporter == null) {
            splitLoadReporter = new DefaultSplitLoadReporter(this.context);
        }
        SplitLoadReporterManager.install(splitLoadReporter);
        if (this.isMainProcess) {
            SplitInstallReporter splitInstallReporter = this.splitConfiguration.installReporter;
            if (splitInstallReporter == null) {
                splitInstallReporter = new DefaultSplitInstallReporter(this.context);
            }
            SplitInstallReporterManager.install(splitInstallReporter);
            SplitUninstallReporter splitUninstallReporter = this.splitConfiguration.uninstallReporter;
            if (splitUninstallReporter == null) {
                splitUninstallReporter = new DefaultSplitUninstallReporter(this.context);
            }
            SplitUninstallReporterManager.install(splitUninstallReporter);
            Context context = this.context;
            Downloader downloader = this.downloader;
            SplitConfiguration splitConfiguration = this.splitConfiguration;
            SplitApkInstaller.install(context, downloader, splitConfiguration.obtainUserConfirmationDialogClass, splitConfiguration.verifySignature);
            SplitApkInstaller.startUninstallSplits(this.context);
            if (Looper.myLooper() != null) {
                Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() { // from class: com.iqiyi.android.qigsaw.core.Qigsaw.1
                    @Override // android.os.MessageQueue.IdleHandler
                    public boolean queueIdle() {
                        Qigsaw.cleanStaleSplits(Qigsaw.this.context);
                        return false;
                    }
                });
            } else {
                cleanStaleSplits(this.context);
            }
        }
        this.onApplicationCreated = true;
    }

    public static void preloadInstalledSplits(Collection<String> collection) {
        if (!instance().onApplicationCreated) {
            throw new RuntimeException("This method must be invoked after Qigsaw#onApplicationCreated()!");
        }
        SplitLoadManagerService.getInstance().preloadInstalledSplits(collection);
    }

    public static void registerSplitActivityLifecycleCallbacks(SplitActivityLifecycleCallbacks splitActivityLifecycleCallbacks) {
        Context context = instance().context;
        if (!(context instanceof Application)) {
            throw new RuntimeException("If you want to monitor lifecycle of split activity, Application context must be required for Qigsaw#install(...)!");
        }
        ((Application) context).registerActivityLifecycleCallbacks(splitActivityLifecycleCallbacks);
    }

    public static void unregisterSplitActivityLifecycleCallbacks(SplitActivityLifecycleCallbacks splitActivityLifecycleCallbacks) {
        Context context = instance().context;
        if (!(context instanceof Application)) {
            throw new RuntimeException("If you want to monitor lifecycle of split activity, Application context must be required for Qigsaw#install(...)!");
        }
        ((Application) context).unregisterActivityLifecycleCallbacks(splitActivityLifecycleCallbacks);
    }

    public static boolean updateSplits(Context context, String str, String str2) {
        try {
            Intent intent = new Intent();
            intent.setClassName(context, "com.iqiyi.android.qigsaw.core.splitrequest.splitinfo.SplitUpdateService");
            intent.putExtra(SplitConstants.NEW_SPLIT_INFO_VERSION, str);
            intent.putExtra(SplitConstants.NEW_SPLIT_INFO_PATH, str2);
            context.startService(intent);
            return true;
        } catch (Exception unused) {
            return false;
        }
    }
}
