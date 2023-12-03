package com.android.settings.usagestats.utils;

import com.android.settings.R;
import com.android.settings.search.tree.SecuritySettingsTree;
import java.util.HashMap;
import miui.cloud.Constants;
import miui.cloud.sync.providers.CalendarSyncInfoProvider;
import miui.cloud.sync.providers.ContactsSyncInfoProvider;

/* loaded from: classes2.dex */
public class AppCategory {
    public static final HashMap<Integer, Integer> CATEGORY_RESID = new HashMap<Integer, Integer>() { // from class: com.android.settings.usagestats.utils.AppCategory.1
        {
            put(110329, Integer.valueOf(R.string.usagestats_app_category_miui_life));
            put(110330, Integer.valueOf(R.string.usagestats_app_category_miui_financial));
            put(110331, Integer.valueOf(R.string.usagestats_app_category_miui_shopping));
            put(110332, Integer.valueOf(R.string.usagestats_app_category_miui_reading));
            put(110333, Integer.valueOf(R.string.usagestats_app_category_miui_system));
            put(110334, Integer.valueOf(R.string.usagestats_app_category_miui_tools));
            put(110335, Integer.valueOf(R.string.usagestats_app_category_miui_game));
            put(110336, Integer.valueOf(R.string.usagestats_app_category_miui_social));
            put(110337, Integer.valueOf(R.string.usagestats_app_category_miui_video_etc));
            put(110338, Integer.valueOf(R.string.usagestats_app_category_miui_productivity));
            put(-1, Integer.valueOf(R.string.usagestats_app_category_miui_undefined));
        }
    };
    public static final HashMap<String, Integer> PRE_DEFINED_CATEGORY = new HashMap<String, Integer>() { // from class: com.android.settings.usagestats.utils.AppCategory.2
        {
            put("com.miui.weather2", 110329);
            put("com.miui.gallery", 110333);
            put("com.miui.player", 110337);
            put("com.android.thememanager", 110333);
            put("com.android.settings", 110333);
            put("com.xiaomi.market", 110333);
            put(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME, 110333);
            put(ContactsSyncInfoProvider.AUTHORITY, 110333);
            put("com.miui.calculator", 110334);
            put("com.miui.calculator2", 110334);
            put("com.android.fileexplorer", 110333);
            put("com.android.email", 110338);
            put("com.android.deskclock", 110334);
            put("com.xiaomi.vipaccount", 110334);
            put("com.android.soundrecorder", 110334);
            put("com.miui.screenrecorder", 110333);
            put("com.miui.compass", 110334);
            put("com.duokan.phone.remotecontroller", 110334);
            put("com.android.providers.downloads.ui", 110333);
            put("com.miui.voiceassist", 110334);
            put("com.miui.virtualsim", 110334);
            put("com.miui.bugreport", 110333);
            put("com.miui.fm", 110333);
            put("com.android.phone", 110333);
            put("com.android.mms", 110333);
            put("com.android.browser", 110334);
            put("com.android.camera", 110333);
            put(CalendarSyncInfoProvider.AUTHORITY, 110329);
            put("com.miui.notes", 110338);
            put("com.mipay.wallet", 110330);
            put("com.xiaomi.jr", 110330);
            put("com.xiaomi.shop", 110331);
            put("com.xiaomi.youpin", 110331);
            put("com.xiaomi.gamecenter", 110334);
            put("com.miui.video", 110337);
            put("com.duokan.reader", 110332);
            put(Constants.CLOUDSERVICE_PACKAGE_NAME, 110333);
            put("com.tencent.mm", 110336);
            put("com.tencent.mobileqq", 110336);
            put("com.ss.android.ugc.aweme", 110336);
            put("com.sina.weibo", 110336);
            put("com.sina.weibolite", 110336);
            put("com.taobao.taobao", 110331);
            put("com.jingdong.app.mall", 110331);
            put("com.suning.mobile.ebuy", 110331);
            put("com.archievo.vipshop", 110331);
        }
    };

    public static int transferCategory(String str, int i) {
        HashMap<String, Integer> hashMap = PRE_DEFINED_CATEGORY;
        if (hashMap.containsKey(str)) {
            return hashMap.get(str).intValue();
        }
        switch (i) {
            case 0:
                return 110335;
            case 1:
            case 2:
            case 3:
                return 110337;
            case 4:
                return 110336;
            case 5:
            case 6:
                return 110334;
            case 7:
                return 110338;
            default:
                return -1;
        }
    }
}
