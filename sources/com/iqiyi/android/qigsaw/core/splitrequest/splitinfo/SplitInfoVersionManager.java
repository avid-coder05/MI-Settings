package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import java.io.File;

/* loaded from: classes2.dex */
interface SplitInfoVersionManager {
    public static final String SPLIT_ROOT_DIR_NAME = "split_info_version";

    String getCurrentVersion();

    String getDefaultVersion();

    File getRootDir();

    boolean updateVersion(Context context, String str, File file);
}
