package miuix.animation.utils;

import android.os.SystemClock;
import java.util.Arrays;
import java.util.LinkedList;

/* loaded from: classes5.dex */
public class VelocityMonitor {
    private float[] mVelocity;
    private Long mMinDeltaTime = 30L;
    private Long mMaxDeltaTime = 100L;
    private LinkedList<MoveRecord> mHistory = new LinkedList<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class MoveRecord {
        long timeStamp;
        double[] values;

        private MoveRecord() {
        }
    }

    private void addAndUpdate(MoveRecord moveRecord) {
        this.mHistory.add(moveRecord);
        if (this.mHistory.size() > 10) {
            this.mHistory.remove(0);
        }
        updateVelocity();
    }

    private float calVelocity(int i, MoveRecord moveRecord, MoveRecord moveRecord2) {
        float f;
        double d = moveRecord.values[i];
        long j = moveRecord.timeStamp;
        double velocity = getVelocity(d, moveRecord2.values[i], j - moveRecord2.timeStamp);
        int size = this.mHistory.size() - 2;
        MoveRecord moveRecord3 = null;
        while (true) {
            if (size < 0) {
                f = Float.MAX_VALUE;
                break;
            }
            MoveRecord moveRecord4 = this.mHistory.get(size);
            long j2 = j - moveRecord4.timeStamp;
            if (j2 <= this.mMinDeltaTime.longValue() || j2 >= this.mMaxDeltaTime.longValue()) {
                size--;
                moveRecord3 = moveRecord4;
            } else {
                f = getVelocity(d, moveRecord4.values[i], j2);
                double d2 = f;
                if (velocity * d2 > 0.0d) {
                    f = (float) (f > 0.0f ? Math.max(velocity, d2) : Math.min(velocity, d2));
                }
                moveRecord3 = moveRecord4;
            }
        }
        if (f == Float.MAX_VALUE && moveRecord3 != null) {
            long j3 = j - moveRecord3.timeStamp;
            if (j3 > this.mMinDeltaTime.longValue() && j3 < this.mMaxDeltaTime.longValue()) {
                f = getVelocity(d, moveRecord3.values[i], j3);
            }
        }
        if (f == Float.MAX_VALUE) {
            return 0.0f;
        }
        return f;
    }

    private void clearVelocity() {
        float[] fArr = this.mVelocity;
        if (fArr != null) {
            Arrays.fill(fArr, 0.0f);
        }
    }

    private MoveRecord getMoveRecord() {
        MoveRecord moveRecord = new MoveRecord();
        moveRecord.timeStamp = SystemClock.uptimeMillis();
        return moveRecord;
    }

    private float getVelocity(double d, double d2, long j) {
        return (float) (j == 0 ? 0.0d : (d - d2) / (((float) j) / 1000.0f));
    }

    private void updateVelocity() {
        int size = this.mHistory.size();
        if (size < 2) {
            clearVelocity();
            return;
        }
        MoveRecord last = this.mHistory.getLast();
        MoveRecord moveRecord = this.mHistory.get(size - 2);
        float[] fArr = this.mVelocity;
        if (fArr == null || fArr.length < last.values.length) {
            this.mVelocity = new float[last.values.length];
        }
        for (int i = 0; i < last.values.length; i++) {
            this.mVelocity[i] = calVelocity(i, last, moveRecord);
        }
    }

    public void clear() {
        this.mHistory.clear();
        clearVelocity();
    }

    public float getVelocity(int i) {
        float[] fArr;
        long uptimeMillis = SystemClock.uptimeMillis();
        if ((this.mHistory.size() <= 0 || Math.abs(uptimeMillis - this.mHistory.getLast().timeStamp) <= 50) && (fArr = this.mVelocity) != null && fArr.length > i) {
            return fArr[i];
        }
        return 0.0f;
    }

    public void update(double... dArr) {
        if (dArr == null || dArr.length == 0) {
            return;
        }
        MoveRecord moveRecord = getMoveRecord();
        moveRecord.values = dArr;
        addAndUpdate(moveRecord);
    }
}
