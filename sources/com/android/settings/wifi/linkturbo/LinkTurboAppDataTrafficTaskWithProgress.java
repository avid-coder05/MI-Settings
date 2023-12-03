package com.android.settings.wifi.linkturbo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Process;
import android.text.TextUtils;
import com.android.settings.wifi.linkturbo.WifiLinkTurboSettings;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import miuix.util.Log;

/* loaded from: classes2.dex */
public class LinkTurboAppDataTrafficTaskWithProgress extends AsyncTask<Void, Void, List<WifiLinkTurboSettings.AppItem>> {
    private WeakReference<LinkTurboClient> mWeakRefLinkTurboClient;
    private WeakReference<WifiLinkTurboSettings> mWeakSettings;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public List<WifiLinkTurboSettings.AppItem> doInBackground(Void... voidArr) {
        LinkTurboClient linkTurboClient;
        String str;
        List<WifiLinkTurboSettings.AppItem> list = null;
        if (isCancelled()) {
            return null;
        }
        Log.d("LinkTurboAppDataTrafficTaskWithProgress", "time is thisDay --" + LinkTurboUtils.getTimesmorning().getTime() + " --thisMonth- " + LinkTurboUtils.getTimesMonthmorning().getTime() + " --thisTime- " + System.currentTimeMillis());
        WifiLinkTurboSettings wifiLinkTurboSettings = this.mWeakSettings.get();
        if (wifiLinkTurboSettings == null || (linkTurboClient = this.mWeakRefLinkTurboClient.get()) == null) {
            return null;
        }
        linkTurboClient.checkServiceIsConnected();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = wifiLinkTurboSettings.getPackageManager().queryIntentActivities(intent, 128);
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        int identifier = Process.myUserHandle().getIdentifier();
        String linkTurboWhiteList = wifiLinkTurboSettings.mFlag == 0 ? linkTurboClient.getLinkTurboWhiteList() : "";
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            if (isCancelled()) {
                return list;
            }
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                String str2 = activityInfo.packageName;
                String str3 = activityInfo.name;
                int uid = LinkTurboUtils.getUid(wifiLinkTurboSettings, identifier, str2);
                if (wifiLinkTurboSettings.mFlag == 0) {
                    if (linkTurboWhiteList == null || linkTurboWhiteList.isEmpty()) {
                        break;
                    } else if (!linkTurboWhiteList.contains(String.valueOf(uid))) {
                    }
                }
                if (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3) || hashSet.contains(str2) || hashSet2.contains(Integer.valueOf(uid)) || uid < 10000 || !LinkTurboUtils.hasInternetAccess(wifiLinkTurboSettings, str2)) {
                    str = linkTurboWhiteList;
                } else {
                    Log.d("WifiLinkTurboSettings", "the app is  uid = " + uid + "  packageName = " + str2);
                    str = linkTurboWhiteList;
                    arrayList.add(wifiLinkTurboSettings.getAppItem(activityInfo.applicationInfo, uid, linkTurboClient.isUidInLinkTurboWhiteList(uid), linkTurboClient.getLinkTurboAppDayTraffic(uid), linkTurboClient.getLinkTurboAppMonthTraffic(uid)));
                    hashSet.add(str2);
                    hashSet2.add(Integer.valueOf(uid));
                }
                linkTurboWhiteList = str;
                list = null;
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public void onPostExecute(List<WifiLinkTurboSettings.AppItem> list) {
        super.onPostExecute((LinkTurboAppDataTrafficTaskWithProgress) list);
        WifiLinkTurboSettings wifiLinkTurboSettings = this.mWeakSettings.get();
        if (wifiLinkTurboSettings == null || this.mWeakRefLinkTurboClient.get() == null) {
            return;
        }
        Collections.sort(list, new Comparator<WifiLinkTurboSettings.AppItem>() { // from class: com.android.settings.wifi.linkturbo.LinkTurboAppDataTrafficTaskWithProgress.1
            @Override // java.util.Comparator
            public int compare(WifiLinkTurboSettings.AppItem appItem, WifiLinkTurboSettings.AppItem appItem2) {
                boolean z = appItem2.mIsChecked;
                return z != appItem.mIsChecked ? z ? 1 : -1 : Long.valueOf(appItem2.mMobileDataTrafficOfThisMonth).compareTo(Long.valueOf(appItem.mMobileDataTrafficOfThisMonth));
            }
        });
        wifiLinkTurboSettings.setAllApps(list);
        wifiLinkTurboSettings.mAdapter.setItems(list);
    }

    public void setContext(WifiLinkTurboSettings wifiLinkTurboSettings) {
        this.mWeakSettings = new WeakReference<>(wifiLinkTurboSettings);
    }

    public void setLinkTurboClient(LinkTurboClient linkTurboClient) {
        this.mWeakRefLinkTurboClient = new WeakReference<>(linkTurboClient);
    }
}
