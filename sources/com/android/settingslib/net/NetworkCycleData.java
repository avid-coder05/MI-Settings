package com.android.settingslib.net;

/* loaded from: classes2.dex */
public class NetworkCycleData {
    private long mEndTime;
    private long mStartTime;
    private long mTotalUsage;

    /* loaded from: classes2.dex */
    public static class Builder {
        private NetworkCycleData mObject = new NetworkCycleData();

        public NetworkCycleData build() {
            return getObject();
        }

        protected NetworkCycleData getObject() {
            return this.mObject;
        }

        public Builder setEndTime(long j) {
            getObject().mEndTime = j;
            return this;
        }

        public Builder setStartTime(long j) {
            getObject().mStartTime = j;
            return this;
        }

        public Builder setTotalUsage(long j) {
            getObject().mTotalUsage = j;
            return this;
        }
    }

    public long getEndTime() {
        return this.mEndTime;
    }

    public long getStartTime() {
        return this.mStartTime;
    }

    public long getTotalUsage() {
        return this.mTotalUsage;
    }
}
