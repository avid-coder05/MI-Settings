package com.iqiyi.android.qigsaw.core.splitload;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.splitload.listener.OnSplitLoadListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: classes2.dex */
public abstract class SplitLoadManager {
    protected static final String TAG = "SplitLoadManager";
    private final Context context;
    final String currentProcessName;
    private final Set<Split> loadedSplits = Collections.newSetFromMap(new ConcurrentHashMap());
    private final int splitLoadMode;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SplitLoadManager(Context context, String str, int i) {
        this.context = context;
        this.currentProcessName = str;
        this.splitLoadMode = i;
    }

    public final void clear() {
        this.loadedSplits.clear();
    }

    public abstract Runnable createSplitLoadTask(List<Intent> list, OnSplitLoadListener onSplitLoadListener);

    /* JADX INFO: Access modifiers changed from: package-private */
    public Context getContext() {
        return this.context;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Set<String> getLoadedSplitApkPaths() {
        HashSet hashSet = new HashSet(0);
        for (Split split : this.loadedSplits) {
            if (new File(split.splitApkPath).exists()) {
                hashSet.add(split.splitApkPath);
            } else {
                SplitLog.w(TAG, "Split has been loaded, but its file %s is not exist!", split.splitApkPath);
            }
        }
        return hashSet;
    }

    public Set<String> getLoadedSplitNames() {
        HashSet hashSet = new HashSet(0);
        Iterator<Split> it = this.loadedSplits.iterator();
        while (it.hasNext()) {
            hashSet.add(it.next().splitName);
        }
        return hashSet;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final Set<Split> getLoadedSplits() {
        return this.loadedSplits;
    }

    public abstract void getResources(Resources resources);

    public abstract void injectPathClassloader();

    public abstract void loadInstalledSplits();

    public abstract void preloadInstalledSplits(Collection<String> collection);

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void putSplits(Collection<Split> collection) {
        this.loadedSplits.addAll(collection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int splitLoadMode() {
        return this.splitLoadMode;
    }
}
