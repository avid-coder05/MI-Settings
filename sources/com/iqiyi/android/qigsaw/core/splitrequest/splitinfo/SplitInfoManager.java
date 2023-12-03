package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import java.io.File;
import java.util.Collection;
import java.util.List;

/* loaded from: classes2.dex */
public interface SplitInfoManager {
    SplitDetails createSplitDetailsForJsonFile(String str);

    Collection<SplitInfo> getAllSplitInfo(Context context);

    String getBaseAppVersionName(Context context);

    String getCurrentSplitInfoVersion();

    String getQigsawId(Context context);

    List<String> getSplitEntryFragments(Context context);

    SplitInfo getSplitInfo(Context context, String str);

    List<SplitInfo> getSplitInfos(Context context, Collection<String> collection);

    List<String> getUpdateSplits(Context context);

    boolean updateSplitInfoVersion(Context context, String str, File file);
}
