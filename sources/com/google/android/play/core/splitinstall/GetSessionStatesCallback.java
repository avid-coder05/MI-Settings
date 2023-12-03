package com.google.android.play.core.splitinstall;

import android.os.Bundle;
import com.google.android.play.core.tasks.TaskWrapper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
final class GetSessionStatesCallback extends SplitInstallServiceCallbackImpl<List<SplitInstallSessionState>> {
    /* JADX INFO: Access modifiers changed from: package-private */
    public GetSessionStatesCallback(SplitInstallService splitInstallService, TaskWrapper<List<SplitInstallSessionState>> taskWrapper) {
        super(splitInstallService, taskWrapper);
    }

    @Override // com.google.android.play.core.splitinstall.SplitInstallServiceCallbackImpl, com.google.android.play.core.splitinstall.protocol.ISplitInstallServiceCallbackProxy
    public void onGetSessionStates(List<Bundle> list) {
        super.onGetSessionStates(list);
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<Bundle> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(SplitInstallSessionState.createFrom(it.next()));
        }
        this.mTask.setResult(arrayList);
    }
}
