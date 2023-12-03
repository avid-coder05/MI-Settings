package com.android.settings.search.appseparate;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.settings.MiuiUtils;
import com.android.settings.search.SearchResultItem;
import com.android.settings.search.appseparate.AppSearchResultItem;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.KeywordsCloudConfigHelper;
import com.android.settingslib.search.SearchUtils;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import miui.provider.ExtraContacts;

/* loaded from: classes2.dex */
public class CollectResultProcessor {
    private static final String COM_MIUI_VOICEASSIST = "com.miui.voiceassist";
    private static final String DELIMITER = " ";
    private static final String EXTRAS_KEY_VALUE_SEPARATOR = ":";
    private static final String PACKAGE_MISOUND = "com.miui.misound";
    private static final String TAG = "CollectResultProcessor";
    private static final List<String> filterIntentClass;
    private static final List<String> filterPackage;
    public static final Set<String> sInvalidBadgedIconPackageSet;
    private static final List<String> spaceFilterIntentClass;
    private static final List<String> spaceFilterPackage;
    private final Context mContext;

    /* loaded from: classes2.dex */
    public static class StatusProcessor {
        private static final String INTENT_ACTION_TOUCH_ASSISTANT_SETTINGS = "miui.intent.action.TOUCH_ASSISTANT_SETTINGS";
        static final String PACKAGE_TOUCHASSISTANT = "com.miui.touchassistant";
        private static final int STATE_CLOSE = 0;
        private static final int STATE_OPEN = 1;
        private static final String TOUCH_ASSISTANT_ENABLED = "touch_assistant_enabled";

        private static boolean isEnableTouchAssistant(Context context, String str) {
            int identifier;
            try {
                Resources resources = context.createPackageContext(PACKAGE_TOUCHASSISTANT, 0).getResources();
                if (resources != null && (identifier = resources.getIdentifier("enable_touch_assistant", "string", PACKAGE_TOUCHASSISTANT)) != 0) {
                    return str.equals(resources.getString(identifier));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        private static boolean isTouchAssistantEnabled(Context context) {
            return Settings.System.getInt(context.getContentResolver(), TOUCH_ASSISTANT_ENABLED, 0) == 1;
        }

        public static void process(Context context, String str, AppSearchResultItem.Builder builder, String str2) {
            if (!PACKAGE_TOUCHASSISTANT.equals(str) || isEnableTouchAssistant(context, str2) || isTouchAssistantEnabled(context)) {
                return;
            }
            builder.setStatus(1);
            Intent intent = new Intent(INTENT_ACTION_TOUCH_ASSISTANT_SETTINGS);
            intent.setPackage(PACKAGE_TOUCHASSISTANT);
            builder.setIntent(intent);
        }
    }

    static {
        HashSet hashSet = new HashSet();
        sInvalidBadgedIconPackageSet = hashSet;
        ArrayList arrayList = new ArrayList();
        spaceFilterPackage = arrayList;
        ArrayList arrayList2 = new ArrayList();
        spaceFilterIntentClass = arrayList2;
        ArrayList arrayList3 = new ArrayList();
        filterPackage = arrayList3;
        ArrayList arrayList4 = new ArrayList();
        filterIntentClass = arrayList4;
        hashSet.add("com.miui.touchassistant");
        hashSet.add(PACKAGE_MISOUND);
        arrayList.add("com.xiaomi.misettings");
        arrayList2.add("com.xiaomi.misettings.usagestats.UsageStatsMainActivity");
        arrayList2.add("com.xiaomi.misettings.usagestats.moresettings.MoreSettingsActivity");
        arrayList3.add("com.miui.home");
        arrayList4.add("com.miui.home.settings.MiuiHomeSettingActivity");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CollectResultProcessor(Context context) {
        this.mContext = context;
    }

    private Intent buildDirectSearchResultIntent(String str, String str2, String str3, String str4, String str5) {
        Intent intent = new Intent(str);
        if (!TextUtils.isEmpty(str2) && !TextUtils.isEmpty(str3)) {
            intent.setComponent(new ComponentName(str2, str3));
        }
        if (!TextUtils.isEmpty(str4)) {
            intent.setData(Uri.parse(str4));
        }
        if (!TextUtils.isEmpty(str5)) {
            Bundle bundle = new Bundle();
            String[] split = str5.split(DELIMITER);
            for (String str6 : split) {
                try {
                    String str7 = str6.split(EXTRAS_KEY_VALUE_SEPARATOR)[0];
                    bundle.putString(str7, str6.substring(str7.length() + 1));
                } catch (Exception e) {
                    Log.e(TAG, "Parse extras:" + str6 + " fail!", e);
                }
            }
            intent.putExtras(bundle);
        }
        return intent;
    }

    private String getAppName(String str, ApplicationInfo applicationInfo) {
        CharSequence charSequence;
        try {
            charSequence = this.mContext.createPackageContext(str, 0).getResources().getText(applicationInfo.labelRes);
        } catch (Exception e) {
            Log.w(TAG, "Could not get AppName for " + str + ": " + e);
            charSequence = null;
        }
        return charSequence == null ? "" : charSequence.toString();
    }

    private static double getKeywordsScore(double d, String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            for (String str3 : str.split(ExtraContacts.ConferenceCalls.SPLIT_EXPRESSION)) {
                d = Math.max(d, SearchUtils.doSimpleMatch(str3.toLowerCase().replace(DELIMITER, "").replace("‑", "-"), str2) - 0.1d);
                if (d >= 1.0d) {
                    return 1.0d;
                }
            }
        }
        return d;
    }

    private String getPinyin(String str) {
        if ("zh".equals(Locale.getDefault().getLanguage())) {
            return SearchUtils.getPinyin(this.mContext, str);
        }
        return null;
    }

    private double match(SearchRawData searchRawData, String str) {
        String replace = str.toLowerCase().replace(DELIMITER, "").replace("‑", "-");
        String str2 = searchRawData.title;
        if (str2 == null) {
            return 0.0d;
        }
        double max = Math.max(0.0d, SearchUtils.doSimpleMatch(replace, str2.toLowerCase().replace(DELIMITER, "").replace("‑", "-")));
        if (max >= 1.0d) {
            return 1.0d;
        }
        double keywordsScore = getKeywordsScore(max, KeywordsCloudConfigHelper.getInstance(this.mContext).getKeywords(searchRawData.title), replace);
        if (keywordsScore >= 1.0d) {
            return 1.0d;
        }
        if (!TextUtils.isEmpty(searchRawData.keywords)) {
            keywordsScore = Math.max(keywordsScore, getKeywordsScore(keywordsScore, searchRawData.keywords, replace));
            if (keywordsScore >= 1.0d) {
                return 1.0d;
            }
        }
        double max2 = Math.max(keywordsScore, SearchUtils.doPinyinMatch(replace, getPinyin(searchRawData.title)) - 0.4d);
        if (max2 < 0.0d) {
            return 0.0d;
        }
        return max2;
    }

    public List<SearchResultItem> getMatchData(PreMatchData preMatchData, String str) {
        Iterator<Map.Entry<String, List<SearchRawData>>> it;
        Iterator<SearchRawData> it2;
        ApplicationInfo applicationInfo;
        double d;
        Intent parseUri;
        long currentTimeMillis = System.currentTimeMillis();
        Map<String, List<SearchRawData>> preMatchDataMap = preMatchData.getPreMatchDataMap();
        LinkedList linkedList = new LinkedList();
        Iterator<Map.Entry<String, List<SearchRawData>>> it3 = preMatchDataMap.entrySet().iterator();
        while (it3.hasNext()) {
            Map.Entry<String, List<SearchRawData>> next = it3.next();
            String key = next.getKey();
            if (this.mContext.getPackageManager().isPackageAvailable(key)) {
                Iterator<SearchRawData> it4 = next.getValue().iterator();
                while (it4.hasNext()) {
                    SearchRawData next2 = it4.next();
                    if (!spaceFilterPackage.contains(key) || !spaceFilterIntentClass.contains(next2.intentTargetClass) || !MiuiUtils.isSecondSpace(this.mContext)) {
                        if (!filterPackage.contains(key) || !filterIntentClass.contains(next2.intentTargetClass) || !MiuiUtils.isEasyMode(this.mContext)) {
                            if (!COM_MIUI_VOICEASSIST.equals(key) || !MiuiUtils.isLowMemoryMachine() || !MiuiUtils.isLower3GB()) {
                                double match = match(next2, str);
                                if (match > 0.0d) {
                                    AppSearchResultItem.Builder builder = new AppSearchResultItem.Builder(2);
                                    try {
                                        ApplicationInfo applicationInfo2 = this.mContext.getPackageManager().getApplicationInfo(next2.packageName, 0);
                                        if (TextUtils.isEmpty(next2.intentUri)) {
                                            it = it3;
                                            applicationInfo = applicationInfo2;
                                            it2 = it4;
                                            d = match;
                                            try {
                                                parseUri = buildDirectSearchResultIntent(next2.intentAction, next2.intentTargetPackage, next2.intentTargetClass, next2.uriString, next2.extras);
                                            } catch (PackageManager.NameNotFoundException | URISyntaxException e) {
                                                e = e;
                                                e.printStackTrace();
                                                it3 = it;
                                                it4 = it2;
                                            }
                                        } else {
                                            it = it3;
                                            it2 = it4;
                                            applicationInfo = applicationInfo2;
                                            d = match;
                                            parseUri = Intent.parseUri(next2.intentUri, 0);
                                        }
                                        if (SettingsFeatures.isSplitTablet(this.mContext)) {
                                            parseUri.addMiuiFlags(16);
                                        }
                                        Context context = this.mContext;
                                        if (MiuiUtils.isIntentActivityExistAsUser(context, parseUri, context.getUserId())) {
                                            builder.setAppInfo(applicationInfo).setAppName(getAppName(key, applicationInfo)).setIconResId(next2.iconResId).setScore(d).setTitle(next2.title).setSummary(PathProcessor.process(this.mContext, next2, parseUri)).setIntent(parseUri);
                                            StatusProcessor.process(this.mContext, next2.packageName, builder, next2.title);
                                            linkedList.add(builder.build());
                                        }
                                    } catch (PackageManager.NameNotFoundException | URISyntaxException e2) {
                                        e = e2;
                                        it = it3;
                                        it2 = it4;
                                    }
                                } else {
                                    it = it3;
                                    it2 = it4;
                                }
                                it3 = it;
                                it4 = it2;
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(linkedList);
        SearchUtils.logCost(currentTimeMillis, System.currentTimeMillis(), "-");
        return linkedList;
    }
}
