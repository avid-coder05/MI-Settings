package com.android.settings.recommend;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.util.Log;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.recommend.bean.RecommendItem;
import com.android.settings.search.SearchUpdater;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class RecommendUIHelper {
    private static final String RECOMMEND_REF_KEY = "miui_settings_recommendref_key";
    public static final String TAG = "RecommendUI";
    private Activity mActivity;
    private SettingsPreferenceFragment mFragment;
    private RecommendPreference recommendPreference;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class RecommendItemLinkData {
        private Intent intent;
        private SpannableString string;

        public RecommendItemLinkData(SpannableString spannableString, Intent intent) {
            this.string = spannableString;
            this.intent = intent;
        }

        public Intent getIntent() {
            return this.intent;
        }

        public SpannableString getString() {
            return this.string;
        }
    }

    public RecommendUIHelper(SettingsPreferenceFragment settingsPreferenceFragment) {
        this.mFragment = settingsPreferenceFragment;
        this.mActivity = settingsPreferenceFragment.getActivity();
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0072, code lost:
    
        if (android.text.TextUtils.isEmpty(r6) != false) goto L29;
     */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0062 A[Catch: NotFoundException -> 0x0032, URISyntaxException -> 0x0035, TryCatch #2 {NotFoundException -> 0x0032, URISyntaxException -> 0x0035, blocks: (B:4:0x0021, B:6:0x0027, B:15:0x003b, B:18:0x0042, B:24:0x0062, B:27:0x006a, B:30:0x0078, B:32:0x007e, B:35:0x0085, B:38:0x0093, B:40:0x009d, B:42:0x00ab, B:44:0x00bc, B:46:0x00c2, B:47:0x00c6, B:49:0x00ce, B:50:0x00d3, B:29:0x0074, B:19:0x0047, B:21:0x0051), top: B:56:0x0021 }] */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x006a A[Catch: NotFoundException -> 0x0032, URISyntaxException -> 0x0035, TryCatch #2 {NotFoundException -> 0x0032, URISyntaxException -> 0x0035, blocks: (B:4:0x0021, B:6:0x0027, B:15:0x003b, B:18:0x0042, B:24:0x0062, B:27:0x006a, B:30:0x0078, B:32:0x007e, B:35:0x0085, B:38:0x0093, B:40:0x009d, B:42:0x00ab, B:44:0x00bc, B:46:0x00c2, B:47:0x00c6, B:49:0x00ce, B:50:0x00d3, B:29:0x0074, B:19:0x0047, B:21:0x0051), top: B:56:0x0021 }] */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00ab A[Catch: NotFoundException -> 0x0032, URISyntaxException -> 0x0035, TryCatch #2 {NotFoundException -> 0x0032, URISyntaxException -> 0x0035, blocks: (B:4:0x0021, B:6:0x0027, B:15:0x003b, B:18:0x0042, B:24:0x0062, B:27:0x006a, B:30:0x0078, B:32:0x007e, B:35:0x0085, B:38:0x0093, B:40:0x009d, B:42:0x00ab, B:44:0x00bc, B:46:0x00c2, B:47:0x00c6, B:49:0x00ce, B:50:0x00d3, B:29:0x0074, B:19:0x0047, B:21:0x0051), top: B:56:0x0021 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private com.android.settings.recommend.RecommendUIHelper.RecommendItemLinkData getLinkData(com.android.settings.recommend.bean.RecommendItem r9) {
        /*
            Method dump skipped, instructions count: 274
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.recommend.RecommendUIHelper.getLinkData(com.android.settings.recommend.bean.RecommendItem):com.android.settings.recommend.RecommendUIHelper$RecommendItemLinkData");
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, SearchUpdater.GOOGLE).size() > 0;
    }

    public boolean addRecommendItem(RecommendPreference recommendPreference, RecommendItem recommendItem) {
        RecommendItemLinkData linkData = getLinkData(recommendItem);
        if (linkData != null) {
            recommendPreference.addRecommendView(linkData.getString(), linkData.getIntent());
            return true;
        }
        return false;
    }

    public void buildRecommendLayout(List<RecommendItem> list) {
        buildRecommendLayout(list, 0, false);
    }

    public void buildRecommendLayout(List<RecommendItem> list, int i, boolean z) {
        if (list == null || list.isEmpty()) {
            return;
        }
        if (this.mActivity == null) {
            Log.w(TAG, "mActivity is null");
            return;
        }
        Log.d(TAG, "[buildRecommendLayout] will build recommend layout...");
        boolean z2 = false;
        if (this.recommendPreference != null) {
            this.mFragment.getPreferenceScreen().removePreference(this.recommendPreference);
        }
        RecommendPreference recommendPreference = new RecommendPreference(this.mActivity, i, z);
        this.recommendPreference = recommendPreference;
        recommendPreference.setKey(RECOMMEND_REF_KEY);
        Iterator<RecommendItem> it = list.iterator();
        while (it.hasNext()) {
            if (addRecommendItem(this.recommendPreference, it.next())) {
                z2 = true;
            }
        }
        if (z2) {
            this.mFragment.getPreferenceScreen().addPreference(this.recommendPreference);
            this.recommendPreference.setOrder(Integer.MAX_VALUE);
        }
    }

    public boolean hasRecommendLayout() {
        SettingsPreferenceFragment settingsPreferenceFragment = this.mFragment;
        return (settingsPreferenceFragment == null || settingsPreferenceFragment.getPreferenceScreen() == null || this.mFragment.getPreferenceScreen().findPreference(RECOMMEND_REF_KEY) == null) ? false : true;
    }

    public void removeRecommendLayout() {
        SettingsPreferenceFragment settingsPreferenceFragment = this.mFragment;
        if (settingsPreferenceFragment != null) {
            settingsPreferenceFragment.getPreferenceScreen();
        }
    }
}
