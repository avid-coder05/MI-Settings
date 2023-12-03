package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import java.util.LinkedHashMap;

/* loaded from: classes2.dex */
final class SplitInfoListing {
    private final LinkedHashMap<String, SplitInfo> splitInfoMap;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInfoListing(LinkedHashMap<String, SplitInfo> linkedHashMap) {
        this.splitInfoMap = linkedHashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LinkedHashMap<String, SplitInfo> getSplitInfoMap() {
        return this.splitInfoMap;
    }
}
