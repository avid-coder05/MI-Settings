package com.android.settings.recommend;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.TitleManager;
import com.android.settings.accessibility.InvisibleToggleAccessibilityServicePreferenceFragment;
import com.android.settings.accessibility.ScreenReaderController;
import com.android.settings.device.controller.MiuiBackupController;
import com.android.settings.device.controller.MiuiCloudController;
import com.android.settings.device.controller.MiuiFactoryResetController;
import com.android.settings.device.controller.MiuiFindDeviceController;
import com.android.settings.device.controller.MiuiOneKeyMirgrateController;
import com.android.settings.personal.FullScreenDisplayController;
import com.android.settings.personal.FullScreenDisplayRecommendController;
import com.android.settings.recommend.bean.IndexDetail;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.AccessibilitySettingsTree;
import com.android.settings.sound.HeadsetSettingsController;
import com.android.settings.special.SecondSpaceController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.HashMap;

/* loaded from: classes2.dex */
public class PageIndexManager {
    private static final int BASE_OTHER_APP = 9000;
    private static final int BASE_PAGE_LEVEL_FIRST = 0;
    private static final int BASE_PAGE_LEVEL_FOURTH = 3000;
    private static final int BASE_PAGE_LEVEL_SECOND = 1000;
    private static final int BASE_PAGE_LEVEL_THIRD = 2000;
    private static volatile PageIndexManager INSTANCE = null;
    public static final String KEY_HEADSET_SETTINGS = "headset_settings";
    public static final String KEY_INFINITY_DISPLAY = "infinity_display";
    public static final String KEY_SECOND_SPACE = "second_space";
    public static final int PAGE_ACCESSIBILITY_HAPTIC_SETTINGS = 2004;
    public static final int PAGE_ACCESSIBILITY_PHYSICAL = 1010;
    public static final int PAGE_ACCESSIBILITY_VISUAL = 1009;
    public static final int PAGE_ACCOUNT_SETTINGS = 6;
    public static final int PAGE_BACKUP_AND_RESET = 1007;
    public static final int PAGE_DISPLAY = 1;
    public static final int PAGE_FACTORY_RESET = 1008;
    public static final int PAGE_FIND_DEVICE = 9001;
    public static final int PAGE_FONT_SIZE_WEIGHT_SETTINGS = 2003;
    public static final int PAGE_GESTURE_FUNCTION_SETTINGS = 2001;
    public static final int PAGE_HAPTIC_SETTINGS = 7;
    public static final int PAGE_HEADSET_SETTINGS = 1003;
    public static final int PAGE_INFINITY_DISPLAY = 1001;
    public static final int PAGE_INFINITY_DISPLAY_NEW = 9002;
    public static final int PAGE_KEY_FUNCTION_SETTINGS = 2002;
    public static final int PAGE_MI_CLOUD = 1011;
    public static final int PAGE_ONE_KEY_MIGRATE = 1004;
    public static final int PAGE_OTHER_PERSONAL_SETTINGS = 4;
    public static final int PAGE_PHYSICAL_KEYBOARD = 2008;
    public static final int PAGE_PRIVACY_PROTECTION_SETTINGS = 5;
    public static final int PAGE_SCREEN = 2;
    public static final int PAGE_SECOND_SPACE = 1006;
    public static final int PAGE_SECURITY_PRIVACY = 3;
    public static final int PAGE_SETTINGS_ACCESSIBILITY_MENU = 2009;
    public static final int PAGE_SHOW_FONT_SIZE = 3001;
    public static final int PAGE_STATUS_BAR = 1005;
    public static final int PAGE_SUBSCREEN_SETTINGS = 2010;
    public static final int PAGE_SYSTEM_SECURITY = 1002;
    public static final int PAGE_WAKE_UP_XIAOAI_SETTINGS = 2005;
    public static final int SOS_EMERGENCY_HELP = 9003;
    public static final String TAG = "PageIndexManager";
    private Context mContext;
    public final HashMap<Integer, IndexDetail> mPageIndexMapping = new HashMap<>();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class HuanjiIndexDetail extends IndexDetail {
        public HuanjiIndexDetail(int i) {
            super(i);
        }

        public HuanjiIndexDetail(int i, String str, AbstractPreferenceController abstractPreferenceController) {
            super(i, str, abstractPreferenceController);
        }

        @Override // com.android.settings.recommend.bean.IndexDetail
        public String getIntent() {
            Intent intent = new Intent("com.intent.action.Huanji");
            intent.setPackage("com.miui.huanji");
            if (!MiuiUtils.isIntentActivityExistAsUser(PageIndexManager.this.mContext, intent, UserHandle.myUserId())) {
                intent = new Intent("android.intent.action.VIEW", Uri.parse(MiuiOneKeyMirgrateController.APP_STORE_URL));
            }
            intent.setFlags(268435456);
            intent.putExtra("request_from", "com.android.settings");
            return intent.toUri(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class M11yMenuIndexDetail extends IndexDetail {
        public M11yMenuIndexDetail(int i) {
            super(i);
        }

        public M11yMenuIndexDetail(int i, String str, AbstractPreferenceController abstractPreferenceController) {
            super(i, str, abstractPreferenceController);
        }

        @Override // com.android.settings.recommend.bean.IndexDetail
        public Intent getRawIntent() {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(AccessibilitySettingsTree.SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU);
            AccessibilityServiceInfo installedServiceInfoWithComponentName = AccessibilityManager.getInstance(PageIndexManager.this.mContext).getInstalledServiceInfoWithComponentName(unflattenFromString);
            Bundle bundle = new Bundle();
            bundle.putString("preference_key", AccessibilitySettingsTree.SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU);
            bundle.putString(FunctionColumns.SUMMARY, installedServiceInfoWithComponentName.loadDescription(PageIndexManager.this.mContext.getPackageManager()));
            bundle.putString("settings_title", PageIndexManager.this.mContext.getString(R.string.accessibility_menu_item_settings));
            bundle.putParcelable("component_name", unflattenFromString);
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent.putExtra(":settings:show_fragment", InvisibleToggleAccessibilityServicePreferenceFragment.class.getName());
            intent.putExtra(":settings:show_fragment_args", bundle);
            intent.putExtra(":settings:show_fragment_title_res_package_name", (String) null);
            intent.putExtra(":settings:show_fragment_title_resid", R.string.accessibility_menu_service_name);
            return intent;
        }
    }

    private PageIndexManager(Context context) {
        this.mContext = context;
        init();
    }

    public static PageIndexManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (PageIndexManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PageIndexManager(context);
                }
            }
        }
        return INSTANCE;
    }

    private int getTitleByResourceName(Context context, String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                Resources resources = context.createPackageContext("com.android.settings", 0).getResources();
                if (resources != null) {
                    return resources.getIdentifier(str, "string", "com.android.settings");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private void init() {
        long currentTimeMillis = System.currentTimeMillis();
        update(1, new IndexDetail(R.string.display_settings));
        update(2, new IndexDetail(TitleManager.getScreenTitle(this.mContext)));
        update(3, new IndexDetail(SettingsFeatures.getPasswordTypes(this.mContext)));
        update(4, new IndexDetail(R.string.other_advanced_settings));
        update(5, new IndexDetail(R.string.privacy_protection));
        FullScreenDisplayController fullScreenDisplayController = new FullScreenDisplayController(this.mContext, KEY_INFINITY_DISPLAY);
        int i = R.string.infinity_display_title;
        update(1001, new IndexDetail(i, null, fullScreenDisplayController));
        update(1002, new IndexDetail(R.string.security_privacy_settings_title));
        update(1003, new IndexDetail(R.string.headset_settings_title, null, new HeadsetSettingsController(this.mContext, KEY_HEADSET_SETTINGS)));
        update(1004, new HuanjiIndexDetail(R.string.mi_transfer, null, new MiuiOneKeyMirgrateController(this.mContext)));
        update(1005, new IndexDetail(TitleManager.getStatusBarTitle()));
        update(1006, new IndexDetail(R.string.second_space, null, new SecondSpaceController(this.mContext, "second_space")));
        update(PAGE_BACKUP_AND_RESET, new IndexDetail(R.string.privacy_settings_new, null, new MiuiBackupController(this.mContext)));
        update(PAGE_FACTORY_RESET, new IndexDetail(R.string.master_clear_title_new, null, new MiuiFactoryResetController(this.mContext)));
        update(PAGE_PHYSICAL_KEYBOARD, new IndexDetail(R.string.keyboard_mouse_touch, null));
        update(PAGE_SETTINGS_ACCESSIBILITY_MENU, new M11yMenuIndexDetail(R.string.accessibility_menu_service_name));
        update(PAGE_ACCESSIBILITY_HAPTIC_SETTINGS, new IndexDetail(R.string.accessibility_haptic, null, new ScreenReaderController(this.mContext, "accessibility_screen_reader_haptic")));
        update(PAGE_KEY_FUNCTION_SETTINGS, new IndexDetail(R.string.key_shortcut_settings_title));
        update(PAGE_FONT_SIZE_WEIGHT_SETTINGS, new IndexDetail(R.string.font_settings_jump));
        if (MiuiUtils.isSupportSubScreen()) {
            update(PAGE_SUBSCREEN_SETTINGS, new IndexDetail(getTitleByResourceName(this.mContext, "subscreen_title")));
        }
        update(PAGE_INFINITY_DISPLAY_NEW, new IndexDetail(i, null, new FullScreenDisplayRecommendController(this.mContext, KEY_INFINITY_DISPLAY)));
        update(PAGE_FIND_DEVICE, new IndexDetail(R.string.xiaomi_cloud_find_device, null, new MiuiFindDeviceController(this.mContext)));
        if (SettingsFeatures.IS_NEED_REMOVE_SOS && !SettingsFeatures.isNeedHideSosForCarrier()) {
            update(SOS_EMERGENCY_HELP, new IndexDetail(R.string.emergency_sos_title));
        }
        update(PAGE_MI_CLOUD, new IndexDetail(R.string.xiaomi_cloud_service, null, new MiuiCloudController(this.mContext)));
        Log.d(TAG, "init end, total time" + (System.currentTimeMillis() - currentTimeMillis) + "ms");
    }

    public IndexDetail getIndexDetail(int i) {
        return this.mPageIndexMapping.get(Integer.valueOf(i));
    }

    public void update(int i, IndexDetail indexDetail) {
        this.mPageIndexMapping.put(Integer.valueOf(i), indexDetail);
    }
}
