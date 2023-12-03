package com.android.settings.search.tree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.controller.ScreenMaxAspectRatioController;
import com.android.settings.display.AdaptiveSleepPreferenceController;
import com.android.settings.display.ClassicProtectionFragment;
import com.android.settings.display.DarkModeTimeModeUtil;
import com.android.settings.display.DarkUIPreferenceController;
import com.android.settings.display.PaperProtectionFragment;
import com.android.settings.display.ScreenEffectFragment;
import com.android.settings.gestures.DoubleTapScreenPreferenceController;
import com.android.settings.gestures.PickupGesturePreferenceController;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import java.util.HashSet;
import java.util.LinkedList;
import miui.os.Build;
import miui.util.FeatureParser;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class DisplaySettingsTree extends SettingsTree {
    public static final String ACTION_EXPERT = "miui.intent.action.SCREEN_EXPERT";
    private static final String ACTION_FONTSIZE_KDDI = "android.settings.ACCESSIBILITY_SETTINGS_FOR_SUW";
    private static final String AMBIENT_DISPLAY_PICKUP_TITLE = "ambient_display_pickup_title";
    private static final String AMBIENT_DISPLAY_TITLE = "ambient_display_title";
    private static final String CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW = "com.android.settings.FontSizeSettingsForSetupWizardActivity";
    private static final int FHD_WIDTH = 1080;
    private static final String FONT_SIZE_TITLE = "title_layout_current2";
    private static final String SCREEN_FPS_TITLE = "screen_fps_title";
    public static final String SEARCH_SCREEN_FPS_TITLE = "search_screen_fps_title";
    private static final String TITLE_FONT_SETTINGS = "title_font_settings";
    private boolean IS_NOT_SHOW_FONT;
    final boolean isPrimaryUser;
    final int myUserId;

    protected DisplaySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.IS_NOT_SHOW_FONT = (Build.IS_INTERNATIONAL_BUILD && SettingsFeatures.checkGlobalFontSettingEnable(((SettingsTree) this).mContext)) ? false : true;
        int myUserId = UserHandle.myUserId();
        this.myUserId = myUserId;
        this.isPrimaryUser = myUserId == 0;
    }

    private void addFpsTitleKeywords() {
        String[] keywords = getKeywords();
        HashSet hashSet = new HashSet();
        for (String str : keywords) {
            hashSet.add(str);
        }
        for (int i : FeatureParser.getIntArray("fpsList")) {
            hashSet.add(Integer.toString(i));
        }
        setColumnValue("keywords", SEARCH_SCREEN_FPS_TITLE);
        try {
            Class.forName(SettingsTree.class.getName()).getField("mKeywords").set(this, hashSet.toArray(keywords));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSon(String str, boolean z) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put(FunctionColumns.IS_CHECKBOX, z);
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    private void addSonWithIcon(String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("resource", str);
            jSONObject.put("temporary", true);
            addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
        } catch (JSONException unused) {
        }
    }

    private static boolean isQhdMode() {
        String str = SystemProperties.get("persist.sys.miui_resolution", (String) null);
        return str == null || "".equals(str) || Integer.parseInt(str.split(",")[0]) != FHD_WIDTH;
    }

    public Intent getIntent() {
        super.getIntent();
        String columnValue = getColumnValue("resource");
        if ("display_advanced_mode_title".equals(columnValue)) {
            if (ScreenEffectFragment.getScreenMode(((SettingsTree) this).mContext) == 4) {
                Intent intent = new Intent(ACTION_EXPERT);
                intent.setClassName("com.xiaomi.misettings", "com.xiaomi.misettings.display.ScreenExpertActivity");
                return intent;
            }
        } else if (TITLE_FONT_SETTINGS.equals(columnValue) && (RegionUtils.IS_JP_KDDI || RegionUtils.IS_JP_SB)) {
            Intent intent2 = new Intent(ACTION_FONTSIZE_KDDI);
            intent2.addCategory("android.intent.category.DEFAULT");
            intent2.putExtra("isSetupFlow", true);
            intent2.setComponent(new ComponentName("com.android.settings", CLASS_NAME_FONT_SIZE_SETTINGS_FOR_SUW));
            return intent2;
        }
        return super.getIntent();
    }

    public LinkedList<SettingsTree> getSons() {
        String columnValue = getColumnValue("resource");
        if ("display_settings".equals(columnValue)) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            addSonWithIcon("dark_color_mode");
            addSonWithIcon("light_color_mode");
        }
        if ("screen_effect".equals(columnValue) && FeatureParser.getBoolean("support_display_expert_mode", false)) {
            LinkedList sons2 = super.getSons();
            if (sons2 != null) {
                for (int size2 = sons2.size() - 1; size2 >= 0; size2--) {
                    SettingsTree settingsTree2 = (SettingsTree) sons2.get(size2);
                    if (Boolean.parseBoolean(settingsTree2.getColumnValue("temporary"))) {
                        settingsTree2.removeSelf();
                    }
                }
            }
            addSonWithIcon("display_advanced_mode_title");
        }
        return super.getSons();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if ("paper_mode_auto_twilight_title".equals(columnValue) || "paper_mode_customize_time_title".equals(columnValue)) {
            if (!MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext)) {
                return 1;
            }
        } else if ("paper_mode_start_time_title".equals(columnValue) || "paper_mode_end_time_title".equals(columnValue)) {
            if (Settings.System.getInt(((SettingsTree) this).mContext.getContentResolver(), "paper_mode_scheduler_type", 2) != 2) {
                return 1;
            }
        } else if (!"more_dark_mode_settings".equals(columnValue)) {
            if (AMBIENT_DISPLAY_PICKUP_TITLE.equals(columnValue)) {
                if (new PickupGesturePreferenceController(((SettingsTree) this).mContext, "").getAvailabilityStatus() != 0) {
                    return 0;
                }
            } else if ("night_light_brightness".equals(columnValue)) {
                return 0;
            } else {
                if (AMBIENT_DISPLAY_TITLE.equals(columnValue)) {
                    if (new DoubleTapScreenPreferenceController(((SettingsTree) this).mContext, "").getAvailabilityStatus() != 0) {
                        return 0;
                    }
                } else if ("screen_max_aspect_ratio_title".equals(columnValue)) {
                    if (new ScreenMaxAspectRatioController(((SettingsTree) this).mContext, "screen_max_aspect_ratio").getAvailabilityStatus() != 0) {
                        return 0;
                    }
                } else if ("dark_ui_mode".equals(columnValue)) {
                    if (!new DarkUIPreferenceController(((SettingsTree) this).mContext, "dark_ui_mode_accessibility").isAvailable()) {
                        return 0;
                    }
                } else if ("title_font_current2".equals(columnValue) && Build.IS_GLOBAL_BUILD) {
                    if (!SettingsFeatures.checkGlobalFontSettingEnable(((SettingsTree) this).mContext)) {
                        return 0;
                    }
                } else if ("adaptive_sleep_title".equals(columnValue)) {
                    if (AdaptiveSleepPreferenceController.isControllerAvailable(((SettingsTree) this).mContext) != 0) {
                        return 0;
                    }
                } else if ("screen_paper_texture".equals(columnValue) && MiuiUtils.isSecondSpace(((SettingsTree) this).mContext)) {
                    return 0;
                } else {
                    if ("classic_mode_title".equals(columnValue)) {
                        if (!MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext)) {
                            return 0;
                        }
                        if (MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext)) {
                            setColumnValue(FunctionColumns.FRAGMENT, MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) != 1 ? ClassicProtectionFragment.class.getName() : "");
                        }
                    } else if ("paper_mode_title".equals(columnValue)) {
                        if (!MiuiUtils.supportPaperEyeCare() || !MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext)) {
                            return 0;
                        }
                        if (MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext)) {
                            setColumnValue(FunctionColumns.FRAGMENT, MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) != 0 ? PaperProtectionFragment.class.getName() : "");
                        }
                    } else if ("auto_adjust_effect_title".equals(columnValue) && (!MiuiUtils.supportSmartEyeCare() || !MiuiUtils.isPaperModeEnable(((SettingsTree) this).mContext))) {
                        return 0;
                    } else {
                        if ("adjust_temperature_title".equals(columnValue) && MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) == 1) {
                            return 0;
                        }
                        if ("texture_adjust_temperature_title".equals(columnValue) && MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) == 0) {
                            return 0;
                        }
                        if ("screen_paper_texture".equals(columnValue) && MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) == 0) {
                            return 0;
                        }
                        if ("paper_color_title".equals(columnValue) && MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) == 0) {
                            return 0;
                        }
                        if ("paper_mode_reset".equals(columnValue) && MiuiUtils.getPaperModeType(((SettingsTree) this).mContext) == 0) {
                            return 0;
                        }
                        if ("display_animate_title".equals(columnValue) && !MiuiUtils.supportAnimateCheck()) {
                            return 0;
                        }
                        if ("sunlight_mode".equals(columnValue) && MiuiUtils.isAutoBrightnessModeEnabled(((SettingsTree) this).mContext)) {
                            return 0;
                        }
                    }
                }
            }
        } else if (!DarkModeTimeModeUtil.isDarkModeEnable(((SettingsTree) this).mContext) || !this.isPrimaryUser) {
            return 0;
        }
        if (!"screen_dark_mode_advanced_title".equals(getColumnValue("category_origin")) || DarkModeTimeModeUtil.isDarkModeEnable(((SettingsTree) this).mContext)) {
            if ((RegionUtils.IS_JP_KDDI || RegionUtils.IS_JP_SB) && FONT_SIZE_TITLE.equals(columnValue)) {
                return 0;
            }
            if (!"save_battery_mode".equals(columnValue) || isQhdMode()) {
                if (TITLE_FONT_SETTINGS.equals(columnValue)) {
                    if (!(UserHandle.myUserId() == 0)) {
                        return 0;
                    }
                }
                if (("screen_dark_mode_time_title".equals(columnValue) || "dark_color_mode".equals(columnValue) || "light_color_mode".equals(columnValue) || "dark_mode_time_settings".equals(columnValue)) && !this.isPrimaryUser) {
                    return 0;
                }
                if (TITLE_FONT_SETTINGS.equals(columnValue) && MiuiUtils.isEasyMode(((SettingsTree) this).mContext)) {
                    return 0;
                }
                return super.getStatus();
            }
            return 0;
        }
        return 0;
    }

    protected String getTitle(boolean z) {
        String columnValue = getColumnValue("resource");
        columnValue.hashCode();
        return !columnValue.equals(FONT_SIZE_TITLE) ? (columnValue.equals(TITLE_FONT_SETTINGS) && Build.IS_GLOBAL_BUILD) ? ((SettingsTree) this).mContext.getResources().getString(R.string.title_layout_current2) : super.getTitle(z) : ((SettingsTree) this).mContext.getResources().getString(R.string.title_layout_current2_weight);
    }

    /* JADX WARN: Code restructure failed: missing block: B:36:0x00a3, code lost:
    
        if (((com.android.settingslib.search.SettingsTree) r4).mContext.getPackageManager().queryIntentServices(r1, 0).size() > 0) goto L39;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean initialize() {
        /*
            Method dump skipped, instructions count: 481
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.tree.DisplaySettingsTree.initialize():boolean");
    }
}
