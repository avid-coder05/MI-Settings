package com.android.settings.applications;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.ArrayMap;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.DumpUtils;
import com.android.internal.app.procstats.IProcessStats;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.util.MemInfoReader;
import com.android.settings.R;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/* loaded from: classes.dex */
public class ProcStatsData {
    static final Comparator<ProcStatsEntry> sEntryCompare = new Comparator<ProcStatsEntry>() { // from class: com.android.settings.applications.ProcStatsData.1
        @Override // java.util.Comparator
        public int compare(ProcStatsEntry procStatsEntry, ProcStatsEntry procStatsEntry2) {
            double d = procStatsEntry.mRunWeight;
            double d2 = procStatsEntry2.mRunWeight;
            if (d < d2) {
                return 1;
            }
            if (d > d2) {
                return -1;
            }
            long j = procStatsEntry.mRunDuration;
            long j2 = procStatsEntry2.mRunDuration;
            if (j < j2) {
                return 1;
            }
            return j > j2 ? -1 : 0;
        }
    };
    private static ProcessStats sStatsXfer;
    private Context mContext;
    private long mDuration;
    private MemInfo mMemInfo;
    private PackageManager mPm;
    private ProcessStats mStats;
    private boolean mUseUss;
    private long memTotalTime;
    private ArrayList<ProcStatsPackageEntry> pkgEntries;
    private IProcessStats mProcessStats = IProcessStats.Stub.asInterface(ServiceManager.getService("procstats"));
    private int[] mMemStates = ProcessStats.ALL_MEM_ADJ;
    private int[] mStates = ProcessStats.BACKGROUND_PROC_STATES;

    /* loaded from: classes.dex */
    public static class MemInfo {
        long baseCacheRam;
        double freeWeight;
        double[] mMemStateWeights;
        long memTotalTime;
        public double realFreeRam;
        public double realTotalRam;
        public double realUsedRam;
        double totalRam;
        double totalScale;
        double usedWeight;
        double weightToRam;

        private MemInfo(Context context, ProcessStats.TotalMemoryUseCollection totalMemoryUseCollection, long j) {
            this.mMemStateWeights = new double[16];
            this.memTotalTime = j;
            calculateWeightInfo(context, totalMemoryUseCollection, j);
            double d = j;
            double d2 = (this.usedWeight * 1024.0d) / d;
            double d3 = (this.freeWeight * 1024.0d) / d;
            double d4 = d2 + d3;
            this.totalRam = d4;
            double d5 = this.realTotalRam / d4;
            this.totalScale = d5;
            this.weightToRam = (d5 / d) * 1024.0d;
            this.realUsedRam = d2 * d5;
            this.realFreeRam = d5 * d3;
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            ((ActivityManager) context.getSystemService("activity")).getMemoryInfo(memoryInfo);
            long j2 = memoryInfo.hiddenAppThreshold;
            double d6 = j2;
            double d7 = this.realFreeRam;
            if (d6 >= d7) {
                this.realUsedRam = d3;
                this.realFreeRam = 0.0d;
                this.baseCacheRam = (long) 0.0d;
                return;
            }
            this.realUsedRam += j2;
            this.realFreeRam = d7 - j2;
            this.baseCacheRam = j2;
        }

        private void calculateWeightInfo(Context context, ProcessStats.TotalMemoryUseCollection totalMemoryUseCollection, long j) {
            new MemInfoReader().readMemInfo();
            this.realTotalRam = r3.getTotalSize();
            this.freeWeight = totalMemoryUseCollection.sysMemFreeWeight + totalMemoryUseCollection.sysMemCachedWeight;
            double d = totalMemoryUseCollection.sysMemKernelWeight + totalMemoryUseCollection.sysMemNativeWeight;
            this.usedWeight = d;
            if (!totalMemoryUseCollection.hasSwappedOutPss) {
                this.usedWeight = d + totalMemoryUseCollection.sysMemZRamWeight;
            }
            for (int i = 0; i < 16; i++) {
                if (i == 8) {
                    this.mMemStateWeights[i] = 0.0d;
                } else {
                    double[] dArr = this.mMemStateWeights;
                    double[] dArr2 = totalMemoryUseCollection.processStateWeight;
                    dArr[i] = dArr2[i];
                    if (i >= 11) {
                        this.freeWeight += dArr2[i];
                    } else {
                        this.usedWeight += dArr2[i];
                    }
                }
            }
        }

        public double getWeightToRam() {
            return this.weightToRam;
        }
    }

    public ProcStatsData(Context context, boolean z) {
        this.mContext = context;
        this.mPm = context.getPackageManager();
        if (z) {
            this.mStats = sStatsXfer;
        }
    }

    private ProcStatsPackageEntry createOsEntry(ProcessStats.ProcessDataCollection processDataCollection, ProcessStats.ProcessDataCollection processDataCollection2, ProcessStats.TotalMemoryUseCollection totalMemoryUseCollection, long j) {
        ProcStatsPackageEntry procStatsPackageEntry = new ProcStatsPackageEntry("os", this.memTotalTime);
        if (totalMemoryUseCollection.sysMemNativeWeight > 0.0d) {
            String string = this.mContext.getString(R.string.process_stats_os_native);
            long j2 = this.memTotalTime;
            ProcStatsEntry procStatsEntry = new ProcStatsEntry("os", 0, string, j2, (long) (totalMemoryUseCollection.sysMemNativeWeight / j2), j2);
            procStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, processDataCollection, processDataCollection2, sEntryCompare, this.mUseUss);
            procStatsPackageEntry.addEntry(procStatsEntry);
        }
        if (totalMemoryUseCollection.sysMemKernelWeight > 0.0d) {
            String string2 = this.mContext.getString(R.string.process_stats_os_kernel);
            long j3 = this.memTotalTime;
            ProcStatsEntry procStatsEntry2 = new ProcStatsEntry("os", 0, string2, j3, (long) (totalMemoryUseCollection.sysMemKernelWeight / j3), j3);
            procStatsEntry2.evaluateTargetPackage(this.mPm, this.mStats, processDataCollection, processDataCollection2, sEntryCompare, this.mUseUss);
            procStatsPackageEntry.addEntry(procStatsEntry2);
        }
        if (j > 0) {
            String string3 = this.mContext.getString(R.string.process_stats_os_cache);
            long j4 = this.memTotalTime;
            ProcStatsEntry procStatsEntry3 = new ProcStatsEntry("os", 0, string3, j4, j / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID, j4);
            procStatsEntry3.evaluateTargetPackage(this.mPm, this.mStats, processDataCollection, processDataCollection2, sEntryCompare, this.mUseUss);
            procStatsPackageEntry.addEntry(procStatsEntry3);
        }
        return procStatsPackageEntry;
    }

    private void createPkgMap(ArrayList<ProcStatsEntry> arrayList, ProcessStats.ProcessDataCollection processDataCollection, ProcessStats.ProcessDataCollection processDataCollection2) {
        ArrayMap arrayMap = new ArrayMap();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ProcStatsEntry procStatsEntry = arrayList.get(size);
            procStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, processDataCollection, processDataCollection2, sEntryCompare, this.mUseUss);
            ProcStatsPackageEntry procStatsPackageEntry = (ProcStatsPackageEntry) arrayMap.get(procStatsEntry.mBestTargetPackage);
            if (procStatsPackageEntry == null) {
                procStatsPackageEntry = new ProcStatsPackageEntry(procStatsEntry.mBestTargetPackage, this.memTotalTime);
                arrayMap.put(procStatsEntry.mBestTargetPackage, procStatsPackageEntry);
                this.pkgEntries.add(procStatsPackageEntry);
            }
            procStatsPackageEntry.addEntry(procStatsEntry);
        }
    }

    private void distributeZRam(double d) {
        long j = (long) (d / this.memTotalTime);
        long j2 = 0;
        for (int size = this.pkgEntries.size() - 1; size >= 0; size--) {
            ProcStatsPackageEntry procStatsPackageEntry = this.pkgEntries.get(size);
            for (int size2 = procStatsPackageEntry.mEntries.size() - 1; size2 >= 0; size2--) {
                j2 += procStatsPackageEntry.mEntries.get(size2).mRunDuration;
            }
        }
        int size3 = this.pkgEntries.size() - 1;
        for (long j3 = 0; size3 >= 0 && j2 > j3; j3 = 0) {
            ProcStatsPackageEntry procStatsPackageEntry2 = this.pkgEntries.get(size3);
            long j4 = j3;
            long j5 = j4;
            for (int size4 = procStatsPackageEntry2.mEntries.size() - 1; size4 >= 0; size4--) {
                long j6 = procStatsPackageEntry2.mEntries.get(size4).mRunDuration;
                j4 += j6;
                if (j6 > j5) {
                    j5 = j6;
                }
            }
            long j7 = (j * j4) / j2;
            if (j7 > j3) {
                j -= j7;
                j2 -= j4;
                ProcStatsEntry procStatsEntry = new ProcStatsEntry(procStatsPackageEntry2.mPackage, 0, this.mContext.getString(R.string.process_stats_os_zram), j5, j7, this.memTotalTime);
                procStatsEntry.evaluateTargetPackage(this.mPm, this.mStats, null, null, sEntryCompare, this.mUseUss);
                procStatsPackageEntry2.addEntry(procStatsEntry);
            }
            size3--;
        }
    }

    private ArrayList<ProcStatsEntry> getProcs(ProcessStats.ProcessDataCollection processDataCollection, ProcessStats.ProcessDataCollection processDataCollection2) {
        ProcStatsData procStatsData = this;
        ArrayList<ProcStatsEntry> arrayList = new ArrayList<>();
        ProcessMap processMap = new ProcessMap();
        int size = procStatsData.mStats.mPackages.getMap().size();
        for (int i = 0; i < size; i++) {
            SparseArray sparseArray = (SparseArray) procStatsData.mStats.mPackages.getMap().valueAt(i);
            for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                LongSparseArray longSparseArray = (LongSparseArray) sparseArray.valueAt(i2);
                for (int i3 = 0; i3 < longSparseArray.size(); i3++) {
                    ProcessStats.PackageState packageState = (ProcessStats.PackageState) longSparseArray.valueAt(i3);
                    int i4 = 0;
                    while (i4 < packageState.mProcesses.size()) {
                        ProcessState processState = (ProcessState) packageState.mProcesses.valueAt(i4);
                        int i5 = size;
                        ProcessState processState2 = (ProcessState) procStatsData.mStats.mProcesses.get(processState.getName(), processState.getUid());
                        if (processState2 == null) {
                            Log.w("ProcStatsManager", "No process found for pkg " + packageState.mPackageName + "/" + packageState.mUid + " proc name " + processState.getName());
                        } else {
                            ProcStatsEntry procStatsEntry = (ProcStatsEntry) processMap.get(processState2.getName(), processState2.getUid());
                            if (procStatsEntry == null) {
                                ProcStatsEntry procStatsEntry2 = new ProcStatsEntry(processState2, packageState.mPackageName, processDataCollection, processDataCollection2, procStatsData.mUseUss);
                                if (procStatsEntry2.mRunWeight > 0.0d) {
                                    processMap.put(processState2.getName(), processState2.getUid(), procStatsEntry2);
                                    arrayList.add(procStatsEntry2);
                                }
                            } else {
                                procStatsEntry.addPackage(packageState.mPackageName);
                            }
                        }
                        i4++;
                        size = i5;
                    }
                }
            }
        }
        int size2 = procStatsData.mStats.mPackages.getMap().size();
        int i6 = 0;
        while (i6 < size2) {
            SparseArray sparseArray2 = (SparseArray) procStatsData.mStats.mPackages.getMap().valueAt(i6);
            for (int i7 = 0; i7 < sparseArray2.size(); i7++) {
                LongSparseArray longSparseArray2 = (LongSparseArray) sparseArray2.valueAt(i7);
                for (int i8 = 0; i8 < longSparseArray2.size(); i8++) {
                    ProcessStats.PackageState packageState2 = (ProcessStats.PackageState) longSparseArray2.valueAt(i8);
                    int size3 = packageState2.mServices.size();
                    for (int i9 = 0; i9 < size3; i9++) {
                        ServiceState serviceState = (ServiceState) packageState2.mServices.valueAt(i9);
                        if (serviceState.getProcessName() != null) {
                            ProcStatsEntry procStatsEntry3 = (ProcStatsEntry) processMap.get(serviceState.getProcessName(), sparseArray2.keyAt(i7));
                            if (procStatsEntry3 != null) {
                                procStatsEntry3.addService(serviceState);
                            } else {
                                Log.w("ProcStatsManager", "No process " + serviceState.getProcessName() + "/" + sparseArray2.keyAt(i7) + " for service " + serviceState.getName());
                            }
                        }
                    }
                }
            }
            i6++;
            procStatsData = this;
        }
        return arrayList;
    }

    private void load() {
        try {
            ParcelFileDescriptor statsOverTime = this.mProcessStats.getStatsOverTime(this.mDuration);
            this.mStats = new ProcessStats(false);
            ParcelFileDescriptor.AutoCloseInputStream autoCloseInputStream = new ParcelFileDescriptor.AutoCloseInputStream(statsOverTime);
            this.mStats.read(autoCloseInputStream);
            try {
                autoCloseInputStream.close();
            } catch (IOException unused) {
            }
            if (this.mStats.mReadError != null) {
                Log.w("ProcStatsManager", "Failure reading process stats: " + this.mStats.mReadError);
            }
        } catch (RemoteException e) {
            Log.e("ProcStatsManager", "RemoteException:", e);
        }
    }

    public long getDuration() {
        return this.mDuration;
    }

    public List<ProcStatsPackageEntry> getEntries() {
        return this.pkgEntries;
    }

    public MemInfo getMemInfo() {
        return this.mMemInfo;
    }

    public int getMemState() {
        int i = this.mStats.mMemFactor;
        if (i == -1) {
            return 0;
        }
        return i >= 4 ? i - 4 : i;
    }

    public void refreshStats(boolean z) {
        if (this.mStats == null || z) {
            load();
        }
        this.pkgEntries = new ArrayList<>();
        long uptimeMillis = SystemClock.uptimeMillis();
        ProcessStats processStats = this.mStats;
        this.memTotalTime = DumpUtils.dumpSingleTime((PrintWriter) null, (String) null, processStats.mMemFactorDurations, processStats.mMemFactor, processStats.mStartTime, uptimeMillis);
        ProcessStats.TotalMemoryUseCollection totalMemoryUseCollection = new ProcessStats.TotalMemoryUseCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates);
        this.mStats.computeTotalMemoryUse(totalMemoryUseCollection, uptimeMillis);
        this.mMemInfo = new MemInfo(this.mContext, totalMemoryUseCollection, this.memTotalTime);
        ProcessStats.ProcessDataCollection processDataCollection = new ProcessStats.ProcessDataCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates, this.mStates);
        ProcessStats.ProcessDataCollection processDataCollection2 = new ProcessStats.ProcessDataCollection(ProcessStats.ALL_SCREEN_ADJ, this.mMemStates, ProcessStats.NON_CACHED_PROC_STATES);
        createPkgMap(getProcs(processDataCollection, processDataCollection2), processDataCollection, processDataCollection2);
        double d = totalMemoryUseCollection.sysMemZRamWeight;
        if (d > 0.0d && !totalMemoryUseCollection.hasSwappedOutPss) {
            distributeZRam(d);
        }
        this.pkgEntries.add(createOsEntry(processDataCollection, processDataCollection2, totalMemoryUseCollection, this.mMemInfo.baseCacheRam));
    }

    public void setDuration(long j) {
        if (j != this.mDuration) {
            this.mDuration = j;
            refreshStats(true);
        }
    }

    public void xferStats() {
        sStatsXfer = this.mStats;
    }
}
