package com.android.settings.search.cloud;

import java.util.HashMap;

/* loaded from: classes2.dex */
public class CloudSortData {
    Double cloudWeight = Double.valueOf(0.5d);
    String extra;
    HashMap<String, Double> sortResources;
    String version;

    public Double getCloudWeight() {
        return this.cloudWeight;
    }

    public String getExtra() {
        return this.extra;
    }

    public HashMap<String, Double> getSortResources() {
        return this.sortResources;
    }

    public String getVersion() {
        return this.version;
    }

    public void setCloudWeight(Double d) {
        this.cloudWeight = d;
    }

    public void setExtra(String str) {
        this.extra = str;
    }

    public void setSortResources(HashMap<String, Double> hashMap) {
        this.sortResources = hashMap;
    }

    public void setVersion(String str) {
        this.version = str;
    }
}
