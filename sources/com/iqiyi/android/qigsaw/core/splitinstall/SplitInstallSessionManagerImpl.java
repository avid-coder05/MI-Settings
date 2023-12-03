package com.iqiyi.android.qigsaw.core.splitinstall;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class SplitInstallSessionManagerImpl implements SplitInstallSessionManager {
    private final Context mContext;
    private final String mPackageName;
    private final SparseArray<SplitInstallInternalSessionState> mActiveSessionStates = new SparseArray<>();
    private final Object mLock = new Object();

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInstallSessionManagerImpl(Context context) {
        this.mContext = context;
        this.mPackageName = context.getPackageName();
    }

    private static <C> List<C> asList(SparseArray<C> sparseArray) {
        ArrayList arrayList = new ArrayList(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public void changeSessionState(int i, int i2) {
        synchronized (this.mLock) {
            SplitInstallInternalSessionState splitInstallInternalSessionState = this.mActiveSessionStates.get(i);
            if (splitInstallInternalSessionState != null) {
                splitInstallInternalSessionState.setStatus(i2);
                if (i2 == 7 || i2 == 6 || i2 == 10) {
                    removeSessionState(i);
                }
            }
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public void emitSessionState(SplitInstallInternalSessionState splitInstallInternalSessionState) {
        Bundle transform2Bundle = SplitInstallInternalSessionState.transform2Bundle(splitInstallInternalSessionState);
        Intent intent = new Intent();
        intent.putExtra("session_state", transform2Bundle);
        intent.setPackage(this.mPackageName);
        intent.setAction("com.iqiyi.android.play.core.splitinstall.receiver.SplitInstallUpdateIntentService");
        this.mContext.sendBroadcast(intent);
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public SplitInstallInternalSessionState getSessionState(int i) {
        SplitInstallInternalSessionState splitInstallInternalSessionState;
        synchronized (this.mLock) {
            splitInstallInternalSessionState = this.mActiveSessionStates.get(i);
        }
        return splitInstallInternalSessionState;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public List<SplitInstallInternalSessionState> getSessionStates() {
        List<SplitInstallInternalSessionState> asList;
        synchronized (this.mLock) {
            asList = asList(this.mActiveSessionStates);
        }
        return asList;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public boolean isActiveSessionsLimitExceeded() {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mActiveSessionStates.size(); i++) {
                if (this.mActiveSessionStates.valueAt(i).status() == 2) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public boolean isIncompatibleWithExistingSession(List<String> list) {
        boolean z;
        synchronized (this.mLock) {
            List<SplitInstallInternalSessionState> sessionStates = getSessionStates();
            z = false;
            for (int i = 0; i < sessionStates.size(); i++) {
                SplitInstallInternalSessionState splitInstallInternalSessionState = sessionStates.get(i);
                Iterator<String> it = list.iterator();
                while (true) {
                    if (it.hasNext()) {
                        if (splitInstallInternalSessionState.moduleNames().contains(it.next())) {
                            z = true;
                            break;
                        } else if (z) {
                            break;
                        }
                    }
                }
            }
        }
        return z;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public void removeSessionState(int i) {
        synchronized (this.mLock) {
            if (i != 0) {
                this.mActiveSessionStates.remove(i);
            }
        }
    }

    @Override // com.iqiyi.android.qigsaw.core.splitinstall.SplitInstallSessionManager
    public void setSessionState(int i, SplitInstallInternalSessionState splitInstallInternalSessionState) {
        synchronized (this.mLock) {
            if (i != 0) {
                if (this.mActiveSessionStates.get(i) == null) {
                    this.mActiveSessionStates.put(i, splitInstallInternalSessionState);
                }
            }
        }
    }
}
