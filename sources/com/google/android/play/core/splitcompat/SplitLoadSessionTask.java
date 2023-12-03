package com.google.android.play.core.splitcompat;

import android.content.Intent;
import com.google.android.play.core.splitinstall.SplitSessionStatusChanger;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadManager;
import com.iqiyi.android.qigsaw.core.splitload.SplitLoadManagerService;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import java.util.List;

/* loaded from: classes2.dex */
final class SplitLoadSessionTask implements Runnable, OnSplitLoadListener {
    private final SplitSessionStatusChanger changer;
    private final List<Intent> splitFileIntents;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadSessionTask(List<Intent> list, SplitSessionStatusChanger splitSessionStatusChanger) {
        this.splitFileIntents = list;
        this.changer = splitSessionStatusChanger;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener
    public void onCompleted() {
        this.changer.changeStatus(5);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener
    public void onFailed(int i) {
        this.changer.changeStatus(6, i);
    }

    @Override // java.lang.Runnable
    public void run() {
        if (this.splitFileIntents == null) {
            onFailed(-100);
            return;
        }
        SplitLoadManager splitLoadManagerService = SplitLoadManagerService.getInstance();
        if (splitLoadManagerService != null) {
            splitLoadManagerService.createSplitLoadTask(this.splitFileIntents, this).run();
        }
    }
}
