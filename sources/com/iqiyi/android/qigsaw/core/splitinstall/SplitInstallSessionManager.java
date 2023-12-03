package com.iqiyi.android.qigsaw.core.splitinstall;

import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public interface SplitInstallSessionManager {
    void changeSessionState(int i, int i2);

    void emitSessionState(SplitInstallInternalSessionState splitInstallInternalSessionState);

    SplitInstallInternalSessionState getSessionState(int i);

    List<SplitInstallInternalSessionState> getSessionStates();

    boolean isActiveSessionsLimitExceeded();

    boolean isIncompatibleWithExistingSession(List<String> list);

    void removeSessionState(int i);

    void setSessionState(int i, SplitInstallInternalSessionState splitInstallInternalSessionState);
}
