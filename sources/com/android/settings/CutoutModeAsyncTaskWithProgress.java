package com.android.settings;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import androidx.fragment.app.FragmentManager;
import com.android.settings.CutoutModeSettings;
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
public class CutoutModeAsyncTaskWithProgress<Params, Result> extends AsyncTaskWithProgress<Params, Result> {
    private WeakReference<CutoutModeSettings> mWeakSettings;

    public CutoutModeAsyncTaskWithProgress(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override // android.os.AsyncTask
    protected Result doInBackground(Params... paramsArr) {
        CutoutModeSettings cutoutModeSettings = this.mWeakSettings.get();
        if (cutoutModeSettings == null) {
            return null;
        }
        cutoutModeSettings.mSupportApps.clear();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        List<ResolveInfo> queryIntentActivities = cutoutModeSettings.getPackageManager().queryIntentActivities(intent, 128);
        HashSet hashSet = new HashSet();
        for (ResolveInfo resolveInfo : queryIntentActivities) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            String str = activityInfo.packageName;
            String str2 = activityInfo.name;
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !hashSet.contains(str) && !cutoutModeSettings.mContext.getPackageName().equals(str)) {
                cutoutModeSettings.mSupportApps.add(cutoutModeSettings.getAppItem(resolveInfo.activityInfo.applicationInfo, MiuiInit.getCutoutMode(str)));
                hashSet.add(str);
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.os.AsyncTaskWithProgress, android.os.AsyncTask
    public void onPostExecute(Result result) {
        super.onPostExecute(result);
        CutoutModeSettings cutoutModeSettings = this.mWeakSettings.get();
        if (cutoutModeSettings == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        Collections.sort(cutoutModeSettings.mSupportApps, new Comparator<CutoutModeSettings.AppItem>() { // from class: com.android.settings.CutoutModeAsyncTaskWithProgress.1
            private final Collator sCollator = Collator.getInstance();

            @Override // java.util.Comparator
            public int compare(CutoutModeSettings.AppItem appItem, CutoutModeSettings.AppItem appItem2) {
                return this.sCollator.compare(appItem.mLabel, appItem2.mLabel);
            }
        });
        if (cutoutModeSettings.mSupportApps.size() > 0) {
            arrayList.add(cutoutModeSettings.getAppItem(cutoutModeSettings.mContext.getResources().getString(R.string.cutout_mode_settings_title)));
            arrayList.addAll(cutoutModeSettings.mSupportApps);
        }
        cutoutModeSettings.mAdapter.setItems(arrayList);
        cutoutModeSettings.mAdapter.notifyDataSetChanged();
    }

    public void setContext(CutoutModeSettings cutoutModeSettings) {
        this.mWeakSettings = new WeakReference<>(cutoutModeSettings);
    }
}
