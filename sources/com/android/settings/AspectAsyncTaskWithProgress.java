package com.android.settings;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import androidx.fragment.app.FragmentManager;
import com.android.settings.MaxAspectRatioSettings;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import miui.os.MiuiInit;
import miuix.os.AsyncTaskWithProgress;

/* loaded from: classes.dex */
public class AspectAsyncTaskWithProgress<Params, Result> extends AsyncTaskWithProgress<Params, Result> {
    private WeakReference<MaxAspectRatioSettings> mWeakSettings;

    public AspectAsyncTaskWithProgress(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override // android.os.AsyncTask
    protected Result doInBackground(Params... paramsArr) {
        MaxAspectRatioSettings maxAspectRatioSettings = this.mWeakSettings.get();
        if (maxAspectRatioSettings == null) {
            return null;
        }
        maxAspectRatioSettings.mSupportApps.clear();
        maxAspectRatioSettings.mSuggestApps.clear();
        maxAspectRatioSettings.mRestrictApps.clear();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = maxAspectRatioSettings.getPackageManager().queryIntentActivities(intent, 128);
        HashSet hashSet = new HashSet();
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String str = activityInfo.packageName;
            String str2 = activityInfo.name;
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !hashSet.contains(str) && !maxAspectRatioSettings.mContext.getPackageName().equals(str)) {
                boolean isRestrictAspect = MiuiInit.isRestrictAspect(str);
                int defaultAspectType = MiuiInit.getDefaultAspectType(str);
                MaxAspectRatioSettings.AppItem appItem = maxAspectRatioSettings.getAppItem(resolveInfo.activityInfo.applicationInfo, isRestrictAspect, defaultAspectType);
                if (defaultAspectType == 1) {
                    maxAspectRatioSettings.mSupportApps.add(appItem);
                } else if (defaultAspectType == 2 || defaultAspectType == 3 || defaultAspectType == 0) {
                    maxAspectRatioSettings.mSuggestApps.add(appItem);
                } else {
                    maxAspectRatioSettings.mRestrictApps.add(appItem);
                }
                hashSet.add(str);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
    public void onPostExecute(Result result) {
        super.onPostExecute(result);
        MaxAspectRatioSettings maxAspectRatioSettings = this.mWeakSettings.get();
        if (maxAspectRatioSettings == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Collections.sort(maxAspectRatioSettings.mSupportApps, new Comparator<MaxAspectRatioSettings.AppItem>() { // from class: com.android.settings.AspectAsyncTaskWithProgress.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(MaxAspectRatioSettings.AppItem appItem, MaxAspectRatioSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        Collections.sort(maxAspectRatioSettings.mSuggestApps, new Comparator<MaxAspectRatioSettings.AppItem>() { // from class: com.android.settings.AspectAsyncTaskWithProgress.2
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(MaxAspectRatioSettings.AppItem appItem, MaxAspectRatioSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        Collections.sort(maxAspectRatioSettings.mRestrictApps, new Comparator<MaxAspectRatioSettings.AppItem>() { // from class: com.android.settings.AspectAsyncTaskWithProgress.3
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(MaxAspectRatioSettings.AppItem appItem, MaxAspectRatioSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        if (maxAspectRatioSettings.mSuggestApps.size() > 0) {
            arrayList.add(maxAspectRatioSettings.getAppItem(maxAspectRatioSettings.mContext.getResources().getString(R.string.suggest_unrestrict_max_aspect_ratio_title)));
            arrayList.addAll(maxAspectRatioSettings.mSuggestApps);
        }
        if (maxAspectRatioSettings.mRestrictApps.size() > 0) {
            arrayList.add(maxAspectRatioSettings.getAppItem(maxAspectRatioSettings.mContext.getResources().getString(R.string.restrict_max_aspect_ratio_title)));
            arrayList.addAll(maxAspectRatioSettings.mRestrictApps);
        }
        if (maxAspectRatioSettings.mSupportApps.size() > 0) {
            arrayList.add(maxAspectRatioSettings.getAppItem(maxAspectRatioSettings.mContext.getResources().getString(R.string.unrestrict_max_aspect_ratio_title)));
            arrayList.addAll(maxAspectRatioSettings.mSupportApps);
        }
        maxAspectRatioSettings.mAdapter.setItems(arrayList);
        maxAspectRatioSettings.mAdapter.notifyDataSetChanged();
    }

    public void setContext(MaxAspectRatioSettings maxAspectRatioSettings) {
        this.mWeakSettings = new WeakReference<>(maxAspectRatioSettings);
    }
}
