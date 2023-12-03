package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import java.io.Closeable;

/* loaded from: classes2.dex */
interface SplitInfoVersionDataStorage extends Closeable {
    SplitInfoVersionData readVersionData();

    boolean updateVersionData(SplitInfoVersionData splitInfoVersionData);
}
