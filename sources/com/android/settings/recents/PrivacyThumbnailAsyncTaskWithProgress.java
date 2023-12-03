package com.android.settings.recents;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import androidx.fragment.app.FragmentManager;
import com.android.settings.R;
import com.android.settings.recents.PrivacyThumbnailBlurSettings;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import miuix.os.AsyncTaskWithProgress;

/* loaded from: classes2.dex */
public class PrivacyThumbnailAsyncTaskWithProgress extends AsyncTaskWithProgress<Void, Void> {
    private WeakReference<PrivacyThumbnailBlurSettings> mWeakSettings;

    public PrivacyThumbnailAsyncTaskWithProgress(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.os.AsyncTask
    public Void doInBackground(Void... voidArr) {
        PrivacyThumbnailBlurSettings privacyThumbnailBlurSettings = this.mWeakSettings.get();
        if (privacyThumbnailBlurSettings == null) {
            return null;
        }
        privacyThumbnailBlurSettings.mThumbnailBlurEnableApps.clear();
        privacyThumbnailBlurSettings.mThumbnailBlurDisableApps.clear();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = privacyThumbnailBlurSettings.getPackageManager().queryIntentActivities(intent, 131072);
        HashSet hashSet = new HashSet();
        String string = MiuiSettings.System.getString(privacyThumbnailBlurSettings.getContentResolver(), "miui_recents_privacy_thumbnail_blur", "");
        for (int i = 0; i < queryIntentActivities.size(); i++) {
            ResolveInfo resolveInfo = queryIntentActivities.get(i);
            String str = resolveInfo.activityInfo.packageName;
            if (!TextUtils.isEmpty(str) && !hashSet.contains(str)) {
                if (string.contains(str)) {
                    privacyThumbnailBlurSettings.mThumbnailBlurEnableApps.add(privacyThumbnailBlurSettings.getAppItem(resolveInfo.activityInfo.applicationInfo, true));
                } else {
                    privacyThumbnailBlurSettings.mThumbnailBlurDisableApps.add(privacyThumbnailBlurSettings.getAppItem(resolveInfo.activityInfo.applicationInfo, false));
                }
            }
            hashSet.add(str);
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
    public void onPostExecute(Void r4) {
        super.onPostExecute((PrivacyThumbnailAsyncTaskWithProgress) r4);
        PrivacyThumbnailBlurSettings privacyThumbnailBlurSettings = this.mWeakSettings.get();
        if (privacyThumbnailBlurSettings == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Collections.sort(privacyThumbnailBlurSettings.mThumbnailBlurEnableApps, new Comparator<PrivacyThumbnailBlurSettings.AppItem>() { // from class: com.android.settings.recents.PrivacyThumbnailAsyncTaskWithProgress.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(PrivacyThumbnailBlurSettings.AppItem appItem, PrivacyThumbnailBlurSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        Collections.sort(privacyThumbnailBlurSettings.mThumbnailBlurDisableApps, new Comparator<PrivacyThumbnailBlurSettings.AppItem>() { // from class: com.android.settings.recents.PrivacyThumbnailAsyncTaskWithProgress.2
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(PrivacyThumbnailBlurSettings.AppItem appItem, PrivacyThumbnailBlurSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        if (privacyThumbnailBlurSettings.mThumbnailBlurEnableApps.size() > 0) {
            arrayList.add(privacyThumbnailBlurSettings.getAppItem(privacyThumbnailBlurSettings.getResources().getString(R.string.open_privacy_thumbnail_blur_title)));
            arrayList.addAll(privacyThumbnailBlurSettings.mThumbnailBlurEnableApps);
        }
        if (privacyThumbnailBlurSettings.mThumbnailBlurDisableApps.size() > 0) {
            arrayList.add(privacyThumbnailBlurSettings.getAppItem(privacyThumbnailBlurSettings.getResources().getString(R.string.close_privacy_thumbnail_blur_title)));
            arrayList.addAll(privacyThumbnailBlurSettings.mThumbnailBlurDisableApps);
        }
        privacyThumbnailBlurSettings.mAdapter.setItems(arrayList);
        privacyThumbnailBlurSettings.mAdapter.notifyDataSetChanged();
    }

    public void setContext(PrivacyThumbnailBlurSettings privacyThumbnailBlurSettings) {
        this.mWeakSettings = new WeakReference<>(privacyThumbnailBlurSettings);
    }
}
