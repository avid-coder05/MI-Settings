package com.iqiyi.android.qigsaw.core.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/* loaded from: classes2.dex */
final class SplitComponentInfoProvider {
    private final Set<String> splitNames;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitComponentInfoProvider(Set<String> set) {
        this.splitNames = set;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Map<String, List<String>> getSplitActivitiesMap() {
        HashMap hashMap = new HashMap(0);
        for (String str : this.splitNames) {
            String[] splitActivities = ComponentInfoManager.getSplitActivities(str);
            if (splitActivities != null && splitActivities.length > 0) {
                ArrayList arrayList = new ArrayList();
                Collections.addAll(arrayList, splitActivities);
                hashMap.put(str, arrayList);
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getSplitApplicationName(String str) {
        return ComponentInfoManager.getSplitApplication(str);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<String> getSplitReceivers() {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = this.splitNames.iterator();
        while (it.hasNext()) {
            String[] splitReceivers = ComponentInfoManager.getSplitReceivers(it.next());
            if (splitReceivers != null && splitReceivers.length > 0) {
                Collections.addAll(arrayList, splitReceivers);
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public List<String> getSplitServices() {
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = this.splitNames.iterator();
        while (it.hasNext()) {
            String[] splitServices = ComponentInfoManager.getSplitServices(it.next());
            if (splitServices != null && splitServices.length > 0) {
                Collections.addAll(arrayList, splitServices);
            }
        }
        return arrayList;
    }
}
