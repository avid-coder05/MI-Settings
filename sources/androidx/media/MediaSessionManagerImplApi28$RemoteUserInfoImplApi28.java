package androidx.media;

import android.media.session.MediaSessionManager;

/* loaded from: classes.dex */
final class MediaSessionManagerImplApi28$RemoteUserInfoImplApi28 extends MediaSessionManagerImplBase$RemoteUserInfoImplBase {
    final MediaSessionManager.RemoteUserInfo mObject;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaSessionManagerImplApi28$RemoteUserInfoImplApi28(MediaSessionManager.RemoteUserInfo remoteUserInfo) {
        super(remoteUserInfo.getPackageName(), remoteUserInfo.getPid(), remoteUserInfo.getUid());
        this.mObject = remoteUserInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaSessionManagerImplApi28$RemoteUserInfoImplApi28(String packageName, int pid, int uid) {
        super(packageName, pid, uid);
        this.mObject = new MediaSessionManager.RemoteUserInfo(packageName, pid, uid);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getPackageName(MediaSessionManager.RemoteUserInfo remoteUserInfo) {
        return remoteUserInfo.getPackageName();
    }
}
