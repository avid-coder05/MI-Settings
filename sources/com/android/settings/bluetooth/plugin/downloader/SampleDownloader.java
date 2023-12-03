package com.android.settings.bluetooth.plugin.downloader;

import android.util.Log;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadCallback;
import com.iqiyi.android.qigsaw.core.splitdownload.DownloadRequest;
import com.iqiyi.android.qigsaw.core.splitdownload.Downloader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/* loaded from: classes.dex */
public class SampleDownloader implements Downloader {
    private String mDownloadTempFolder;

    public SampleDownloader(String str) {
        this.mDownloadTempFolder = str;
    }

    private void cleanFolder(File file, boolean z) {
        File[] listFiles;
        if (file == null) {
            return;
        }
        try {
            if (file.isDirectory() && (listFiles = file.listFiles()) != null) {
                for (File file2 : listFiles) {
                    Log.i("Split:SampleDownloader", "cleanFolder file = " + file2);
                    cleanFolder(file2, true);
                }
            }
            if (z) {
                file.delete();
            }
        } catch (Exception e) {
            Log.e("Split:SampleDownloader", "delete folder failed " + e);
        }
    }

    private synchronized boolean moveSrcFileToLocal(String str, String str2, String str3) {
        File file = new File(str);
        File file2 = new File(str2, str3);
        if (file.isFile()) {
            try {
                Files.copy(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
                try {
                    file.delete();
                } catch (Exception e) {
                    Log.w("Split:SampleDownloader", "moveSrcFileToLocal delete failed", e);
                    e.printStackTrace();
                }
                return true;
            } catch (Exception e2) {
                Log.e("Split:SampleDownloader", "moveSrcFileToLocal copy failed", e2);
                e2.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public long calculateDownloadSize(List<DownloadRequest> list, long j) {
        return j;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public boolean cancelDownloadSync(int i) {
        return true;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public void deferredDownload(int i, List<DownloadRequest> list, DownloadCallback downloadCallback, boolean z) {
        String[] strArr = new String[list.size()];
        String[] strArr2 = new String[list.size()];
        String[] strArr3 = new String[list.size()];
        int i2 = 0;
        for (DownloadRequest downloadRequest : list) {
            if (i2 == list.size()) {
                break;
            } else if (!downloadRequest.getUrl().startsWith("assets")) {
                strArr2[i2] = downloadRequest.getFileDir();
                strArr3[i2] = downloadRequest.getFileName();
                strArr[i2] = downloadRequest.getUrl();
                i2++;
            }
        }
        if (strArr[0] == null) {
            downloadCallback.onCompleted();
            return;
        }
        downloadCallback.onError(-100);
        Log.d("Split:SampleDownloader", "startDownload:......");
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public long getDownloadSizeThresholdWhenUsingMobileData() {
        return 10485760L;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public boolean isDeferredDownloadOnlyWhenUsingWifiData() {
        return true;
    }

    @Override // com.iqiyi.android.qigsaw.core.splitdownload.Downloader
    public void startDownload(int i, List<DownloadRequest> list, DownloadCallback downloadCallback) {
        int size = list.size();
        String[] strArr = new String[size];
        String[] strArr2 = new String[list.size()];
        String[] strArr3 = new String[list.size()];
        int i2 = 0;
        for (DownloadRequest downloadRequest : list) {
            if (i2 == list.size()) {
                break;
            } else if (!downloadRequest.getUrl().startsWith(SplitConstants.URL_ASSETS) || !downloadRequest.getUrl().startsWith(SplitConstants.URL_NATIVE)) {
                strArr2[i2] = downloadRequest.getFileDir();
                strArr3[i2] = downloadRequest.getFileName();
                strArr[i2] = downloadRequest.getUrl();
                i2++;
            }
        }
        if (strArr[0] == null) {
            downloadCallback.onCompleted();
            return;
        }
        Log.d("Split:SampleDownloader", "startMove from bluetooth folder:......");
        int i3 = 0;
        int i4 = 0;
        while (true) {
            if (i3 >= size) {
                break;
            }
            String str = strArr[i3];
            String str2 = strArr[i3];
            String str3 = File.separator;
            if (!moveSrcFileToLocal(this.mDownloadTempFolder + str3 + str.substring(str2.lastIndexOf(str3) + 1), strArr2[i3], strArr3[i3])) {
                Log.e("Split:SampleDownloader", "Move fileName:" + strArr3[i3] + " failed, it's url is :" + strArr[i3]);
                downloadCallback.onError(-10);
                break;
            }
            i4++;
            i3++;
        }
        if (i4 == size) {
            downloadCallback.onCompleted();
        } else {
            Log.e("Split:SampleDownloader", "Move files count is not correct: move_cnt = " + i4 + " urls.length = " + size);
            downloadCallback.onError(-10);
        }
        cleanFolder(new File(this.mDownloadTempFolder), false);
        Log.d("Split:SampleDownloader", "startDownload:......");
    }
}
