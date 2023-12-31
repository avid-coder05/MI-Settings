package com.android.settingslib.net;

import com.android.settingslib.net.NetworkCycleData;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* loaded from: classes2.dex */
public class NetworkCycleChartData extends NetworkCycleData {
    public static final long BUCKET_DURATION_MS = TimeUnit.DAYS.toMillis(1);
    private List<NetworkCycleData> mUsageBuckets;

    /* loaded from: classes2.dex */
    public static class Builder extends NetworkCycleData.Builder {
        private NetworkCycleChartData mObject = new NetworkCycleChartData();

        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleChartData build() {
            return getObject();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settingslib.net.NetworkCycleData.Builder
        public NetworkCycleChartData getObject() {
            return this.mObject;
        }

        public Builder setUsageBuckets(List<NetworkCycleData> list) {
            getObject().mUsageBuckets = list;
            return this;
        }
    }

    private NetworkCycleChartData() {
    }

    public List<NetworkCycleData> getUsageBuckets() {
        return this.mUsageBuckets;
    }
}
