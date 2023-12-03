package com.android.settingslib.net;

import com.android.settingslib.net.NetworkCycleData;

/* loaded from: classes2.dex */
public class NetworkCycleDataForUid extends NetworkCycleData {
    private long mBackgroudUsage;
    private long mForegroudUsage;

    /* loaded from: classes2.dex */
    public static class Builder extends NetworkCycleData.Builder {
        private NetworkCycleDataForUid mObject = new NetworkCycleDataForUid();

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleDataForUid build() {
            return getObject();
        }

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleDataForUid getObject() {
            return this.mObject;
        }

        public Builder setBackgroundUsage(long j) {
            getObject().mBackgroudUsage = j;
            return this;
        }

        public Builder setForegroundUsage(long j) {
            getObject().mForegroudUsage = j;
            return this;
        }
    }

    private NetworkCycleDataForUid() {
    }

    public long getBackgroudUsage() {
        return this.mBackgroudUsage;
    }

    public long getForegroudUsage() {
        return this.mForegroudUsage;
    }
}
