package com.android.settings.fuelgauge.batterytip.tips;

import android.content.Context;
import com.android.settings.Utils;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import java.util.function.Predicate;

/* loaded from: classes.dex */
public class AppLabelPredicate implements Predicate<AppInfo> {
    private static AppLabelPredicate sInstance;
    private Context mContext;

    private AppLabelPredicate(Context context) {
        this.mContext = context;
    }

    public static AppLabelPredicate getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AppLabelPredicate(context.getApplicationContext());
        }
        return sInstance;
    }

    @Override // java.util.function.Predicate
    public boolean test(AppInfo appInfo) {
        return Utils.getApplicationLabel(this.mContext, appInfo.packageName) == null;
    }
}
