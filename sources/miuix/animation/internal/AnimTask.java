package miuix.animation.internal;

import android.util.Log;
import java.util.concurrent.atomic.AtomicInteger;
import miuix.animation.listener.UpdateInfo;
import miuix.animation.utils.LinkNode;

/* loaded from: classes5.dex */
public class AnimTask extends LinkNode<AnimTask> implements Runnable {
    public static final AtomicInteger sTaskCount = new AtomicInteger();
    public final AnimStats animStats = new AnimStats();
    public volatile long deltaT;
    public volatile TransitionInfo info;
    public volatile int startPos;
    public volatile boolean toPage;
    public volatile long totalT;

    public static boolean isRunning(byte b) {
        return b == 1 || b == 2;
    }

    public int getAnimCount() {
        return this.animStats.animCount;
    }

    public int getTotalAnimCount() {
        int i = 0;
        while (this != null) {
            i += this.animStats.animCount;
            this = (AnimTask) this.next;
        }
        return i;
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            AnimRunnerTask.doAnimationFrame(this, this.totalT, this.deltaT, true, this.toPage);
        } catch (Exception e) {
            Log.d("miuix_anim", "doAnimationFrame failed", e);
        }
        if (sTaskCount.decrementAndGet() == 0) {
            AnimRunner.sRunnerHandler.sendEmptyMessage(2);
        }
    }

    public void setup(int i, int i2) {
        this.animStats.clear();
        this.animStats.animCount = i2;
        this.startPos = i;
    }

    public void start(long j, long j2, boolean z) {
        this.totalT = j;
        this.deltaT = j2;
        this.toPage = z;
        ThreadPoolUtil.post(this);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateAnimStats() {
        int i = this.startPos + this.animStats.animCount;
        for (int i2 = this.startPos; i2 < i; i2++) {
            UpdateInfo updateInfo = this.info.updateList.get(i2);
            if (updateInfo != null) {
                if (updateInfo.animInfo.op == 0 || updateInfo.animInfo.op == 1) {
                    this.animStats.startCount++;
                } else {
                    this.animStats.initCount++;
                    byte b = updateInfo.animInfo.op;
                    if (b == 3) {
                        this.animStats.endCount++;
                    } else if (b == 4) {
                        this.animStats.cancelCount++;
                    } else if (b == 5) {
                        this.animStats.failCount++;
                    }
                }
            }
        }
    }
}
