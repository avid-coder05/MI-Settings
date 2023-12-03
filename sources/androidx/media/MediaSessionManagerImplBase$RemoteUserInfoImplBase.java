package androidx.media;

import android.text.TextUtils;
import androidx.core.util.ObjectsCompat;

/* loaded from: classes.dex */
class MediaSessionManagerImplBase$RemoteUserInfoImplBase implements MediaSessionManager$RemoteUserInfoImpl {
    private String mPackageName;
    private int mPid;
    private int mUid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaSessionManagerImplBase$RemoteUserInfoImplBase(String packageName, int pid, int uid) {
        this.mPackageName = packageName;
        this.mPid = pid;
        this.mUid = uid;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof MediaSessionManagerImplBase$RemoteUserInfoImplBase) {
            MediaSessionManagerImplBase$RemoteUserInfoImplBase mediaSessionManagerImplBase$RemoteUserInfoImplBase = (MediaSessionManagerImplBase$RemoteUserInfoImplBase) obj;
            return (this.mPid < 0 || mediaSessionManagerImplBase$RemoteUserInfoImplBase.mPid < 0) ? TextUtils.equals(this.mPackageName, mediaSessionManagerImplBase$RemoteUserInfoImplBase.mPackageName) && this.mUid == mediaSessionManagerImplBase$RemoteUserInfoImplBase.mUid : TextUtils.equals(this.mPackageName, mediaSessionManagerImplBase$RemoteUserInfoImplBase.mPackageName) && this.mPid == mediaSessionManagerImplBase$RemoteUserInfoImplBase.mPid && this.mUid == mediaSessionManagerImplBase$RemoteUserInfoImplBase.mUid;
        }
        return false;
    }

    public int hashCode() {
        return ObjectsCompat.hash(this.mPackageName, Integer.valueOf(this.mUid));
    }
}
