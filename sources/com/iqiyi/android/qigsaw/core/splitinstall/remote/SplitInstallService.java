package com.iqiyi.android.qigsaw.core.splitinstall.remote;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService;
import com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallServiceCallback;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes2.dex */
public final class SplitInstallService extends Service {
    private static final Map<String, Handler> sHandlerMap = Collections.synchronizedMap(new HashMap());
    ISplitInstallService.Stub mBinder = new ISplitInstallService.Stub() { // from class: com.iqiyi.android.qigsaw.core.splitinstall.remote.SplitInstallService.1
        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void cancelInstall(String str, int i, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnCancelInstallTask(iSplitInstallServiceCallback, i));
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void deferredInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnDeferredInstallTask(iSplitInstallServiceCallback, list));
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void deferredUninstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnDeferredUninstallTask(iSplitInstallServiceCallback, list));
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void getSessionState(String str, int i, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnGetSessionStateTask(iSplitInstallServiceCallback, i));
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void getSessionStates(String str, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnGetSessionStatesTask(iSplitInstallServiceCallback));
        }

        @Override // com.iqiyi.android.qigsaw.core.splitinstall.protocol.ISplitInstallService
        public void startInstall(String str, List<Bundle> list, Bundle bundle, ISplitInstallServiceCallback iSplitInstallServiceCallback) {
            SplitInstallService.getHandler(str).post(new OnStartInstallTask(iSplitInstallServiceCallback, list));
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Handler getHandler(String str) {
        Handler handler;
        Map<String, Handler> map = sHandlerMap;
        synchronized (map) {
            if (!map.containsKey(str)) {
                HandlerThread handlerThread = new HandlerThread("split_remote_" + str, 10);
                handlerThread.start();
                map.put(str, new Handler(handlerThread.getLooper()));
            }
            handler = map.get(str);
        }
        return handler;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }
}
