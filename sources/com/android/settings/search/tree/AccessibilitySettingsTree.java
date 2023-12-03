package com.android.settings.search.tree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.accessibility.CaptioningManager;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.RegionUtils;
import com.android.settings.accessibility.MiuiAccessibilityAsrController;
import com.android.settings.accessibility.MiuiAccessibilitySettingsActivity;
import com.android.settings.accessibility.ToggleScreenMagnificationPreferenceFragment;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import java.util.LinkedList;
import miui.os.Build;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AccessibilitySettingsTree extends SettingsTree {
    public static final String ACCESSIBILITY_DISPLAY_DALTONIZER_PREFERENCE_TITLE = "accessibility_display_daltonizer_preference_title";
    public static final String ACCESSIBILITY_DISPLAY_INVERSION_PREFERENCE_TITLE = "accessibility_display_inversion_preference_title";
    static final String ACCESSIBILITY_EXTRA = "accessibility_extra";
    public static final String ACCESSIBILITY_HEARINGAID_TITLE = "accessibility_hearingaid_title";
    public static final String ACCESSIBILITY_SCREEN_MAGNIFICATION_GESTURES_TITLE = "accessibility_screen_magnification_gestures_title";
    public static final String ACCESSIBILITY_SCREEN_MAGNIFICATION_NAVBAR_TITLE = "accessibility_screen_magnification_navbar_title";
    public static final String ACCESSIBILITY_SCREEN_READER_HAPTIC_TITLE = "accessibility_screen_reader_haptic_title";
    public static final String ACCESSIBILITY_VOICE_ACCESS_CATEGORY = "accessibility_voice_access_category";
    public static final String ACCESSIBILITY_VOICE_ACCESS_TITLE = "accessibility_voice_access_title";
    public static final String ENVIRONMENTAL_SPEECH_RECOGNITION_KEY = "environment_sound_recognition";
    public static final String ENVIRONMENTAL_SPEECH_RECOGNITION_TITLE = "environmental_speech_recognition";
    static final String EXTRA_ANIMATED_IMAGE_RES = "animated_image_res";
    static final String EXTRA_CHECKED = "checked";
    static final String EXTRA_COMPONENT_NAME = "component_name";
    static final String EXTRA_FRAGMENT_NAME = "fragment_name";
    static final String EXTRA_HTML_DESCRIPTION = "html_description";
    static final String EXTRA_LAUNCHED_FROM_SUW = "from_suw";
    static final String EXTRA_PREFERENCE_KEY = "preference_key";
    static final String EXTRA_SETTINGS_COMPONENT_NAME = "settings_component_name";
    static final String EXTRA_SETTINGS_TITLE = "settings_title";
    static final String EXTRA_SUMMARY = "summary";
    static final String EXTRA_TITLE = "title";
    static final String EXTRA_TITLE_RES = "title_res";
    static final String EXTRA_VIDEO_RAW_RESOURCE_ID = "video_resource";
    public static final String HEADPHONE_ASSISTED_TITLE = "headphone_assisted_title";
    public static final String INTERACTION_CONTROL_CATEGORY_TITLE = "interaction_control_category_title";
    public static final String MIUI_ACCESSIBILITY_ASR_PREFERENCE = "miui_accessibility_asr_preference";
    public static final String MIUI_ACCESSIBILITY_ASR_TITLE = "miui_accessibility_asr_title";
    public static final String SCREEN_READER_CATEGORY_TITLE = "screen_reader_category_title";
    public static final String SELECT_TO_SPEAK = "com.google.android.marvin.talkback/com.google.android.accessibility.selecttospeak.SelectToSpeakService";
    public static final String SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU = "com.android.settings/com.android.settings.accessibility.accessibilitymenu.AccessibilityMenuService";
    public static final String SWITCHACCESS_SWITCH = "com.google.android.marvin.talkback/com.android.switchaccess.SwitchAccessService";
    private static final String TOGGLE_AUTO_SPEAKER_PREFERENCE_TITLE = "toggle_auto_speaker_preference_title";
    public static final String USER_INSTALLED_SERVICES_CATEGORY_TITLE = "user_installed_services_category_title";

    protected AccessibilitySettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
    }

    private Intent buildMiuiAccessibilitySettingsActivityIntent(int i) {
        Intent intent = new Intent(((SettingsTree) this).mContext, MiuiAccessibilitySettingsActivity.class);
        intent.putExtra("extra_tab_position", i);
        return intent;
    }

    private String getExtraHtmlDescription() {
        String string = ((SettingsTree) this).mContext.getString(R.string.accessibility_screen_magnification_summary);
        try {
            return MessageFormat.format(string, 1, 2, 3, 4, 5);
        } catch (Exception e) {
            e.printStackTrace();
            return string;
        }
    }

    private static boolean hasColor(int i) {
        return (CaptioningManager.CaptionStyle.hasColor(i) && (i >>> 24) == 0) ? false : true;
    }

    private static boolean hasOpacity(int i) {
        return (CaptioningManager.CaptionStyle.hasColor(i) && (i >>> 24) != 0) || ((i & 255) << 24) != 0;
    }

    public void buildCategory(ComponentName componentName, JSONObject jSONObject) throws JSONException {
        char c;
        String flattenToString = componentName.flattenToString();
        int hashCode = flattenToString.hashCode();
        if (hashCode == -1428300187) {
            if (flattenToString.equals(SWITCHACCESS_SWITCH)) {
                c = 0;
            }
            c = 65535;
        } else if (hashCode != -3446520) {
            if (hashCode == 100825666 && flattenToString.equals(SELECT_TO_SPEAK)) {
                c = 1;
            }
            c = 65535;
        } else {
            if (flattenToString.equals(SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU)) {
                c = 2;
            }
            c = 65535;
        }
        if (c == 0) {
            jSONObject.put(YellowPageStatistic.Display.CATEGORY, INTERACTION_CONTROL_CATEGORY_TITLE);
        } else if (c != 1) {
            jSONObject.put(YellowPageStatistic.Display.CATEGORY, USER_INSTALLED_SERVICES_CATEGORY_TITLE);
        } else {
            jSONObject.put(YellowPageStatistic.Display.CATEGORY, SCREEN_READER_CATEGORY_TITLE);
        }
    }

    public Intent getIntent() {
        String columnValue = getColumnValue("resource");
        Intent intent = super.getIntent();
        if ("accessibility_screen_magnification_title".equals(columnValue)) {
            Bundle extras = intent.getExtras();
            extras.putString(EXTRA_PREFERENCE_KEY, "accessibility_display_magnification_enabled");
            extras.putString("title", ((SettingsTree) this).mContext.getString(R.string.accessibility_screen_magnification_title));
            extras.putCharSequence(EXTRA_HTML_DESCRIPTION, getExtraHtmlDescription());
            extras.putBoolean(EXTRA_CHECKED, Settings.Secure.getInt(((SettingsTree) this).mContext.getContentResolver(), "accessibility_display_magnification_enabled", 0) == 1);
            extras.putInt(EXTRA_VIDEO_RAW_RESOURCE_ID, R.raw.accessibility_screen_magnification);
            extras.putBoolean(EXTRA_LAUNCHED_FROM_SUW, false);
            intent.putExtra(":settings:show_fragment_args", extras);
        } else if (MIUI_ACCESSIBILITY_ASR_TITLE.equals(columnValue)) {
            if (MiuiUtils.isApplicationInstalled(((SettingsTree) this).mContext, MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME)) {
                Intent intent2 = new Intent();
                intent2.setClassName(MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_PACKAGE_NAME, MiuiAccessibilityAsrController.MIUI_ACCESSIBILITY_ASR_CLASS_NAME);
                MiuiUtils.cancelSplit(((SettingsTree) this).mContext, intent2);
                return intent2;
            }
            Intent intent3 = new Intent("android.intent.action.VIEW", Uri.parse("market://comments?id=com.miui.accessibility"));
            intent3.setPackage("com.xiaomi.market");
            intent3.addFlags(268435456);
            if (intent3.resolveActivity(((SettingsTree) this).mContext.getPackageManager()) != null) {
                return intent3;
            }
            Intent intent4 = new Intent("android.intent.action.VIEW", Uri.parse(MiuiAccessibilityAsrController.APP_STORE_URI));
            if (intent4.resolveActivity(((SettingsTree) this).mContext.getPackageManager()) != null) {
                return intent4;
            }
        }
        String columnValue2 = getColumnValue(FunctionColumns.FRAGMENT);
        columnValue2.hashCode();
        char c = 65535;
        switch (columnValue2.hashCode()) {
            case -884410277:
                if (columnValue2.equals("com.android.settings.accessibility.PhysicalAccessibilitySettings")) {
                    c = 0;
                    break;
                }
                break;
            case -488200388:
                if (columnValue2.equals("com.android.settings.accessibility.HearingAccessibilitySettings")) {
                    c = 1;
                    break;
                }
                break;
            case 198234632:
                if (columnValue2.equals("com.android.settings.accessibility.GeneralAccessibilitySettings")) {
                    c = 2;
                    break;
                }
                break;
            case 1858175442:
                if (columnValue2.equals("com.android.settings.accessibility.VisualAccessibilitySettings")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return buildMiuiAccessibilitySettingsActivityIntent(3);
            case 1:
                return buildMiuiAccessibilitySettingsActivityIntent(2);
            case 2:
                return buildMiuiAccessibilitySettingsActivityIntent(0);
            case 3:
                return buildMiuiAccessibilitySettingsActivityIntent(1);
            default:
                return intent;
        }
    }

    /* JADX WARN: Can't wrap try/catch for region: R(21:28|(1:68)(1:32)|(1:67)(1:36)|37|(1:39)(1:66)|40|41|42|(1:44)|45|46|47|48|(7:52|53|54|55|56|58|27)|60|53|54|55|56|58|27) */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.util.LinkedList<com.android.settingslib.search.SettingsTree> getSons() {
        /*
            Method dump skipped, instructions count: 530
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.tree.AccessibilitySettingsTree.getSons():java.util.LinkedList");
    }

    /* JADX WARN: Code restructure failed: missing block: B:106:0x01b7, code lost:
    
        if (new com.android.settings.accessibility.EnvironmentSoundRecognitionController(((com.android.settingslib.search.SettingsTree) r9).mContext, "environment_sound_recognition").getAvailabilityStatus() != 0) goto L107;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected int getStatus() {
        /*
            Method dump skipped, instructions count: 465
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.search.tree.AccessibilitySettingsTree.getStatus():int");
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if ("accessibility_screen_magnification_title".equals(columnValue)) {
            LinkedList<SettingsTree> sons = getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    sons.get(size).removeSelf();
                }
            }
            setColumnValue(FunctionColumns.FRAGMENT, ToggleScreenMagnificationPreferenceFragment.class.getName());
        } else if (TOGGLE_AUTO_SPEAKER_PREFERENCE_TITLE.equals(columnValue)) {
            if (Build.IS_TABLET || RegionUtils.IS_JP_KDDI) {
                return true;
            }
        } else if (ACCESSIBILITY_SCREEN_READER_HAPTIC_TITLE.equals(columnValue) && !SettingsFeatures.isSupportAccessibilityHaptic(((SettingsTree) this).mContext)) {
            return true;
        }
        return super.initialize();
    }
}
