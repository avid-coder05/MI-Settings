package com.iqiyi.android.qigsaw.core.splitrequest.splitinfo;

import android.content.Context;
import com.iqiyi.android.qigsaw.core.common.AbiUtil;
import com.iqiyi.android.qigsaw.core.common.SplitConstants;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/* loaded from: classes2.dex */
public class SplitInfo {
    private final List<ApkData> apkDataList;
    private final String appVersion;
    private final boolean builtIn;
    private final List<String> dependencies;
    private final int dexNumber;
    private final boolean isMultiDex;
    private final List<LibData> libDataList;
    private final int minSdkVersion;
    private List<ApkData> primaryApkDataList;
    private AtomicReference<LibData> primaryLibData = new AtomicReference<>();
    private final String splitName;
    private final String splitVersion;
    private final List<String> workProcesses;

    /* loaded from: classes2.dex */
    public static class ApkData {
        private String abi;
        private String md5;
        private long size;
        private String url;

        /* JADX INFO: Access modifiers changed from: package-private */
        public ApkData(String str, String str2, String str3, long j) {
            this.abi = str;
            this.url = str2;
            this.md5 = str3;
            this.size = j;
        }

        public String getAbi() {
            return this.abi;
        }

        public String getMd5() {
            return this.md5;
        }

        public long getSize() {
            return this.size;
        }

        public String getUrl() {
            return this.url;
        }
    }

    /* loaded from: classes2.dex */
    public static class LibData {
        private final String abi;
        private final List<Lib> libs;

        /* loaded from: classes2.dex */
        public static class Lib {
            private final String md5;
            private final String name;
            private final long size;

            /* JADX INFO: Access modifiers changed from: package-private */
            public Lib(String str, String str2, long j) {
                this.name = str;
                this.md5 = str2;
                this.size = j;
            }

            public String getMd5() {
                return this.md5;
            }

            public String getName() {
                return this.name;
            }

            public long getSize() {
                return this.size;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public LibData(String str, List<Lib> list) {
            this.abi = str;
            this.libs = list;
        }

        public String getAbi() {
            return this.abi;
        }

        public List<Lib> getLibs() {
            return this.libs;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitInfo(String str, String str2, String str3, boolean z, int i, int i2, List<String> list, List<String> list2, List<ApkData> list3, List<LibData> list4) {
        this.splitName = str;
        this.appVersion = str2;
        this.splitVersion = str3;
        this.builtIn = z;
        this.minSdkVersion = i;
        this.isMultiDex = i2 > 1;
        this.dexNumber = i2;
        this.workProcesses = list;
        this.dependencies = list2;
        this.apkDataList = list3;
        this.libDataList = list4;
    }

    public ApkData getApkDataForMaster() {
        for (ApkData apkData : this.apkDataList) {
            if (apkData.abi.equals(SplitConstants.MASTER)) {
                return apkData;
            }
        }
        throw new RuntimeException("Unable to find master apk for " + this.splitName);
    }

    public synchronized List<ApkData> getApkDataList(Context context) throws IOException {
        List<ApkData> list = this.primaryApkDataList;
        if (list != null) {
            return list;
        }
        this.primaryApkDataList = new ArrayList();
        LibData primaryLibData = getPrimaryLibData(context);
        for (ApkData apkData : this.apkDataList) {
            if (apkData.abi.equals(SplitConstants.MASTER)) {
                this.primaryApkDataList.add(apkData);
            }
            if (primaryLibData != null && primaryLibData.abi.equals(apkData.abi)) {
                this.primaryApkDataList.add(apkData);
            }
        }
        if (primaryLibData != null && this.primaryApkDataList.size() <= 1) {
            throw new RuntimeException("Unable to find split config apk for abi" + primaryLibData.abi);
        }
        return this.primaryApkDataList;
    }

    public long getApkTotalSize(Context context) throws IOException {
        Iterator<ApkData> it = getApkDataList(context).iterator();
        long j = 0;
        while (it.hasNext()) {
            j += it.next().size;
        }
        return j;
    }

    public String getAppVersion() {
        return this.appVersion;
    }

    public List<String> getDependencies() {
        return this.dependencies;
    }

    public int getMinSdkVersion() {
        return this.minSdkVersion;
    }

    public LibData getPrimaryLibData(Context context) throws IOException {
        if (this.primaryLibData.get() != null) {
            return this.primaryLibData.get();
        }
        String basePrimaryAbi = AbiUtil.getBasePrimaryAbi(context);
        if (this.libDataList == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<LibData> it = this.libDataList.iterator();
        while (it.hasNext()) {
            arrayList.add(it.next().abi);
        }
        String findSplitPrimaryAbi = AbiUtil.findSplitPrimaryAbi(basePrimaryAbi, arrayList);
        if (findSplitPrimaryAbi == null) {
            throw new IOException("No supported abi for split " + this.splitName);
        }
        Iterator<LibData> it2 = this.libDataList.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            LibData next = it2.next();
            if (next.abi.equals(findSplitPrimaryAbi)) {
                this.primaryLibData.compareAndSet(null, next);
                break;
            }
        }
        return this.primaryLibData.get();
    }

    public String getSplitName() {
        return this.splitName;
    }

    public String getSplitVersion() {
        return this.splitVersion;
    }

    public List<String> getWorkProcesses() {
        return this.workProcesses;
    }

    public boolean hasDex() {
        return this.dexNumber > 0;
    }

    public boolean isBuiltIn() {
        return this.builtIn;
    }

    public boolean isMultiDex() {
        return this.isMultiDex;
    }

    public String obtainInstalledMark(Context context) throws IOException {
        String str = null;
        long j = 0;
        for (ApkData apkData : getApkDataList(context)) {
            if (SplitConstants.MASTER.equals(apkData.getAbi())) {
                str = apkData.md5;
            } else {
                j = apkData.size;
            }
        }
        return str + "." + j;
    }
}
