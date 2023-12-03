package com.miui.maml;

import android.util.Log;
import com.miui.maml.ResourceManager;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes2.dex */
public class LifecycleResourceManager extends ResourceManager {
    private static long mLastCheckCacheTime;
    private long mCheckTime;
    private long mInactiveTime;

    public LifecycleResourceManager(ResourceLoader resourceLoader, long j, long j2) {
        super(resourceLoader);
        this.mInactiveTime = j;
        this.mCheckTime = j2;
    }

    public void checkCache() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - mLastCheckCacheTime < this.mCheckTime) {
            return;
        }
        Log.d("LifecycleResourceManager", "begin check cache... ");
        ArrayList arrayList = new ArrayList();
        synchronized (this.mBitmapKeysLock) {
            Iterator<String> it = this.mBitmapKeys.iterator();
            while (it.hasNext()) {
                String next = it.next();
                ResourceManager.BitmapInfo bitmapInfo = ResourceManager.sBitmapsCache.get(next);
                if (bitmapInfo != null && currentTimeMillis - bitmapInfo.mLastVisitTime > this.mInactiveTime) {
                    arrayList.add(next);
                }
            }
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                String str = (String) it2.next();
                Log.d("LifecycleResourceManager", "remove cache: " + str);
                ResourceManager.sBitmapsCache.remove(str);
                this.mBitmapKeys.remove(str);
            }
        }
        mLastCheckCacheTime = currentTimeMillis;
    }

    @Override // com.miui.maml.ResourceManager
    public void finish(boolean z) {
        if (z) {
            checkCache();
        }
        super.finish(z);
    }

    @Override // com.miui.maml.ResourceManager
    public void pause() {
        checkCache();
    }
}
