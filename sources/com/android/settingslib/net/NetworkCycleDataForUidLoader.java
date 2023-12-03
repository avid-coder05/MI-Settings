package com.android.settingslib.net;

import android.content.Context;
import android.util.Log;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class NetworkCycleDataForUidLoader extends NetworkCycleDataLoader<List<NetworkCycleDataForUid>> {
    private final List<NetworkCycleDataForUid> mData;
    private final boolean mRetrieveDetail;
    private final List<Integer> mUids;

    /* loaded from: classes2.dex */
    public static abstract class Builder<T extends NetworkCycleDataForUidLoader> extends NetworkCycleDataLoader.Builder<T> {
        private boolean mRetrieveDetail;
        private final List<Integer> mUids;

        public Builder(Context context) {
            super(context);
            this.mUids = new ArrayList();
            this.mRetrieveDetail = true;
        }

        public Builder<T> addUid(int i) {
            this.mUids.add(Integer.valueOf(i));
            return this;
        }

        public Builder<T> setRetrieveDetail(boolean z) {
            this.mRetrieveDetail = z;
            return this;
        }
    }

    private NetworkCycleDataForUidLoader(Builder builder) {
        super(builder);
        this.mUids = builder.mUids;
        this.mRetrieveDetail = builder.mRetrieveDetail;
        this.mData = new ArrayList();
    }

    public static Builder<?> builder(Context context) {
        return new Builder<NetworkCycleDataForUidLoader>(context) { // from class: com.android.settingslib.net.NetworkCycleDataForUidLoader.1
            @Override // com.android.settingslib.net.NetworkCycleDataLoader.Builder
            public NetworkCycleDataForUidLoader build() {
                return new NetworkCycleDataForUidLoader(this);
            }
        };
    }

    private long getForegroundUsage(long j, long j2, int i) {
        return getTotalUsage(this.mNetworkStatsManager.queryDetailsForUidTagState(this.mNetworkTemplate, j, j2, i, 0, 2));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    public List<NetworkCycleDataForUid> getCycleUsage() {
        return this.mData;
    }

    public List<Integer> getUids() {
        return this.mUids;
    }

    @Override // com.android.settingslib.net.NetworkCycleDataLoader
    void recordUsage(long j, long j2) {
        try {
            Iterator<Integer> it = this.mUids.iterator();
            long j3 = 0;
            long j4 = 0;
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                long totalUsage = getTotalUsage(this.mNetworkStatsManager.queryDetailsForUid(this.mNetworkTemplate, j, j2, intValue));
                if (totalUsage > 0) {
                    long j5 = j3 + totalUsage;
                    if (this.mRetrieveDetail) {
                        j4 += getForegroundUsage(j, j2, intValue);
                    }
                    j3 = j5;
                }
            }
            if (j3 > 0) {
                NetworkCycleDataForUid.Builder builder = new NetworkCycleDataForUid.Builder();
                builder.setStartTime(j).setEndTime(j2).setTotalUsage(j3);
                if (this.mRetrieveDetail) {
                    builder.setBackgroundUsage(j3 - j4).setForegroundUsage(j4);
                }
                this.mData.add(builder.build());
            }
        } catch (Exception e) {
            Log.e("NetworkDataForUidLoader", "Exception querying network detail.", e);
        }
    }
}
