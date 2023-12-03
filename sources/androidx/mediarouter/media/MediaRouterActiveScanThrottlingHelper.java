package androidx.mediarouter.media;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

/* loaded from: classes.dex */
class MediaRouterActiveScanThrottlingHelper {
    private boolean mActiveScan;
    private long mCurrentTime;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private long mSuppressActiveScanTimeout;
    private final Runnable mUpdateDiscoveryRequestRunnable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MediaRouterActiveScanThrottlingHelper(Runnable updateDiscoveryRequestRunnable) {
        this.mUpdateDiscoveryRequestRunnable = updateDiscoveryRequestRunnable;
    }

    public boolean finalizeActiveScanAndScheduleSuppressActiveScanRunnable() {
        if (this.mActiveScan) {
            long j = this.mSuppressActiveScanTimeout;
            if (j > 0) {
                this.mHandler.postDelayed(this.mUpdateDiscoveryRequestRunnable, j);
            }
        }
        return this.mActiveScan;
    }

    public void requestActiveScan(boolean activeScanAsRequested, long requestTimestamp) {
        if (activeScanAsRequested) {
            long j = this.mCurrentTime;
            if (j - requestTimestamp >= 30000) {
                return;
            }
            this.mSuppressActiveScanTimeout = Math.max(this.mSuppressActiveScanTimeout, (requestTimestamp + 30000) - j);
            this.mActiveScan = true;
        }
    }

    public void reset() {
        this.mSuppressActiveScanTimeout = 0L;
        this.mActiveScan = false;
        this.mCurrentTime = SystemClock.elapsedRealtime();
        this.mHandler.removeCallbacks(this.mUpdateDiscoveryRequestRunnable);
    }
}
