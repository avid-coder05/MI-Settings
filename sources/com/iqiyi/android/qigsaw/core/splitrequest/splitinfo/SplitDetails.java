package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import java.util.List;

/* loaded from: classes2.dex */
final class SplitDetails {
    private final String appVersionName;
    private final String qigsawId;
    private final List<String> splitEntryFragments;
    private final SplitInfoListing splitInfoListing;
    private final List<String> updateSplits;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitDetails(String str, String str2, List<String> list, List<String> list2, SplitInfoListing splitInfoListing) {
        this.qigsawId = str;
        this.appVersionName = str2;
        this.updateSplits = list;
        this.splitEntryFragments = list2;
        this.splitInfoListing = splitInfoListing;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getAppVersionName() {
        return this.appVersionName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getQigsawId() {
        return "1.0";
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<String> getSplitEntryFragments() {
        return this.splitEntryFragments;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInfoListing getSplitInfoListing() {
        return this.splitInfoListing;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<String> getUpdateSplits() {
        return this.updateSplits;
    }
}
