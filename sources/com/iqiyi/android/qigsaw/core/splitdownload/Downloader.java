package com.iqiyi.android.qigsaw.core.splitdownload;

import androidx.annotation.Keep;
import java.util.List;

@Keep
/* loaded from: classes2.dex */
public interface Downloader {
    long calculateDownloadSize(List<DownloadRequest> list, long j);

    boolean cancelDownloadSync(int i);

    void deferredDownload(int i, List<DownloadRequest> list, DownloadCallback downloadCallback, boolean z);

    long getDownloadSizeThresholdWhenUsingMobileData();

    boolean isDeferredDownloadOnlyWhenUsingWifiData();

    void startDownload(int i, List<DownloadRequest> list, DownloadCallback downloadCallback);
}
