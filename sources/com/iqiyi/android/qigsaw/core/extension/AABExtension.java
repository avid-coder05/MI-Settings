package com.iqiyi.android.qigsaw.core.extension;

import android.app.Application;
import android.content.Context;
import androidx.annotation.Keep;
import com.iqiyi.android.qigsaw.core.common.SplitAABInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitBaseInfoProvider;
import com.iqiyi.android.qigsaw.core.common.SplitLog;
import com.iqiyi.android.qigsaw.core.extension.fakecomponents.FakeActivity;
import com.iqiyi.android.qigsaw.core.extension.fakecomponents.FakeReceiver;
import com.iqiyi.android.qigsaw.core.extension.fakecomponents.FakeService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@Keep
/* loaded from: classes2.dex */
public class AABExtension {
    private static final String TAG = "Split:AABExtension";
    private static final AtomicReference<AABExtension> sAABCompatReference = new AtomicReference<>(null);
    private final Map<String, List<ContentProviderProxy>> sSplitContentProviderMap = new HashMap();
    private final List<Application> aabApplications = new ArrayList();
    private final AABExtensionManager extensionManager = new AABExtensionManagerImpl(new SplitComponentInfoProvider(getSplitNames()));

    private AABExtension() {
    }

    public static AABExtension getInstance() {
        AtomicReference<AABExtension> atomicReference = sAABCompatReference;
        if (atomicReference.get() == null) {
            atomicReference.set(new AABExtension());
        }
        return atomicReference.get();
    }

    private Set<String> getSplitNames() {
        String[] dynamicFeatures = SplitBaseInfoProvider.getDynamicFeatures();
        HashSet hashSet = new HashSet();
        if (dynamicFeatures != null && dynamicFeatures.length > 0) {
            hashSet.addAll(Arrays.asList(dynamicFeatures));
        }
        return hashSet;
    }

    public void activeApplication(Application application, Context context) throws AABExtensionException {
        this.extensionManager.activeApplication(application, context);
    }

    public final void clear() {
        this.sSplitContentProviderMap.clear();
        this.aabApplications.clear();
    }

    public void createAndActivateSplitProviders(ClassLoader classLoader, String str) throws AABExtensionException {
        List<ContentProviderProxy> list = this.sSplitContentProviderMap.get(str);
        if (list != null) {
            Iterator<ContentProviderProxy> it = list.iterator();
            while (it.hasNext()) {
                it.next().createAndActivateRealContentProvider(classLoader);
            }
        }
    }

    public void createAndActiveSplitApplication(Context context, boolean z) {
        if (z) {
            return;
        }
        Set<String> installedSplitsForAAB = new SplitAABInfoProvider(context).getInstalledSplitsForAAB();
        if (installedSplitsForAAB.isEmpty()) {
            return;
        }
        for (String str : installedSplitsForAAB) {
            try {
                Application createApplication = createApplication(AABExtension.class.getClassLoader(), str);
                if (createApplication != null) {
                    activeApplication(createApplication, context);
                    this.aabApplications.add(createApplication);
                }
            } catch (AABExtensionException e) {
                SplitLog.w(TAG, "Failed to create " + str + " application", e);
            }
        }
    }

    public Application createApplication(ClassLoader classLoader, String str) throws AABExtensionException {
        return this.extensionManager.createApplication(classLoader, str);
    }

    public Class<?> getFakeComponent(String str) {
        if (this.extensionManager.isSplitActivity(str)) {
            return FakeActivity.class;
        }
        if (this.extensionManager.isSplitService(str)) {
            return FakeService.class;
        }
        if (this.extensionManager.isSplitReceiver(str)) {
            return FakeReceiver.class;
        }
        return null;
    }

    public String getSplitNameForActivityName(String str) {
        for (Map.Entry<String, List<String>> entry : this.extensionManager.getSplitActivitiesMap().entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            if (value != null && value.contains(str)) {
                return key;
            }
        }
        return null;
    }

    public void onApplicationCreate() {
        if (this.aabApplications.isEmpty()) {
            return;
        }
        Iterator<Application> it = this.aabApplications.iterator();
        while (it.hasNext()) {
            it.next().onCreate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void put(String str, ContentProviderProxy contentProviderProxy) {
        List<ContentProviderProxy> list = this.sSplitContentProviderMap.get(str);
        if (list == null) {
            list = new ArrayList<>();
            this.sSplitContentProviderMap.put(str, list);
        }
        list.add(contentProviderProxy);
    }
}
