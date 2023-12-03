package com.android.settings.search.tree;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.settings.R;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.InstalledAccessibilityService;
import com.android.settings.accessibility.ToggleAccessibilityServicePreferenceFragment;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SearchUtils;
import com.android.settingslib.search.SettingsTree;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class AccessibilityServiceSettingsTree extends SettingsTree {
    private ComponentName mComponentName;
    private Bundle mExtras;
    private String mFragmentName;
    private String mGeneralTab;
    private String mHearingTab;
    private String mPhysicalTab;
    private String mTitle;
    private String mVisualTab;

    protected AccessibilityServiceSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mGeneralTab = context.getResources().getString(R.string.accessibility_settings_tabs_general);
        this.mVisualTab = context.getResources().getString(R.string.accessibility_settings_tabs_visual);
        this.mHearingTab = context.getResources().getString(R.string.accessibility_settings_tabs_hearing);
        this.mPhysicalTab = context.getResources().getString(R.string.accessibility_settings_tabs_physical);
        this.mTitle = jSONObject.optString("title");
        this.mExtras = new Bundle();
        JSONObject optJSONObject = jSONObject.optJSONObject("accessibility_extra");
        this.mExtras.putString("preference_key", optJSONObject.optString("preference_key"));
        this.mExtras.putBoolean("checked", optJSONObject.optBoolean("checked"));
        this.mExtras.putString("title", optJSONObject.optString("title"));
        this.mExtras.putString(FunctionColumns.SUMMARY, optJSONObject.optString(FunctionColumns.SUMMARY));
        this.mExtras.putInt("animated_image_res", optJSONObject.optInt("animated_image_res"));
        this.mExtras.putString("html_description", optJSONObject.optString("html_description"));
        this.mFragmentName = optJSONObject.optString("fragment_name");
        String optString = optJSONObject.optString("settings_title");
        if (!TextUtils.isEmpty(optString)) {
            this.mExtras.putString("settings_title", optString);
        }
        String optString2 = optJSONObject.optString("settings_component_name");
        if (!TextUtils.isEmpty(optString2)) {
            this.mExtras.putString("settings_component_name", optString2);
        }
        ComponentName unflattenFromString = ComponentName.unflattenFromString(optJSONObject.optString("component_name"));
        this.mComponentName = unflattenFromString;
        this.mExtras.putParcelable("component_name", unflattenFromString);
    }

    private boolean inMoreInstalledServices(ComponentName componentName) {
        return (AccessibilitySettings.COMMON_SERVICES_LIST.contains(componentName.getPackageName()) || InstalledAccessibilityService.buildNoNeedDisplayServices(((SettingsTree) this).mContext).contains(componentName)) ? false : true;
    }

    public String addTabsLabelToPath(Context context, String str, String str2, String str3, String str4) {
        String string;
        int indexOf;
        StringBuilder sb = new StringBuilder();
        if (TextUtils.isEmpty(str2) || (indexOf = str.indexOf((string = SearchUtils.getString(context, str2)))) <= 0) {
            return str;
        }
        sb.append(str.substring(0, indexOf));
        sb.append(str3);
        sb.append("/");
        String columnValue = getColumnValue("resource");
        if (!TextUtils.isEmpty(str4)) {
            sb.append(str4);
            sb.append("/");
        } else if (!AccessibilitySettingsTree.SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU.equals(columnValue)) {
            sb.append(string);
            sb.append("/");
        }
        if (!TextUtils.equals(columnValue, AccessibilitySettingsTree.SELECT_TO_SPEAK) && TextUtils.equals(this.mVisualTab, str3) && SettingsFeatures.isSupportAccessibilityHaptic(((SettingsTree) this).mContext)) {
            sb.append(context.getString(R.string.accessibility_screen_reader_haptic_title));
            sb.append("/");
        }
        sb.append(this.mTitle);
        return sb.toString();
    }

    public Intent getIntent() {
        if (getStatus() == 1) {
            if (getParent() == null) {
                return null;
            }
            return getParent().getIntent();
        } else if (getStatus() != 3) {
            return null;
        } else {
            String title = getTitle(true);
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
            intent.putExtra(":settings:show_fragment", TextUtils.isEmpty(this.mFragmentName) ? ToggleAccessibilityServicePreferenceFragment.class.getName() : this.mFragmentName);
            intent.putExtra(":settings:show_fragment_args", this.mExtras);
            intent.putExtra(":settings:show_fragment_title_res_package_name", (String) null);
            intent.putExtra(":settings:show_fragment_title_resid", 0);
            intent.putExtra(":miui:starting_window_label", title);
            intent.putExtra(":settings:show_fragment_title", title);
            return intent;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    protected String getPath(boolean z, boolean z2) {
        char c;
        String str = super.getPath(z, z2) + "/" + getTitle(z);
        String columnValue = getColumnValue("resource");
        String columnValue2 = getColumnValue("category_origin");
        switch (columnValue.hashCode()) {
            case -2013065275:
                if (columnValue.equals("com.bjbyhd.voiceback/com.bjbyhd.voiceback.BoyhoodVoiceBackService")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1428300187:
                if (columnValue.equals(AccessibilitySettingsTree.SWITCHACCESS_SWITCH)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1105807818:
                if (columnValue.equals("com.nirenr.talkman/com.nirenr.talkman.TalkManAccessibilityService")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1024409802:
                if (columnValue.equals("com.dianming.phoneapp/com.dianming.phoneapp.MyAccessibilityService")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -3446520:
                if (columnValue.equals(AccessibilitySettingsTree.SETTINGS_ACCESSIBILITY_ACCESSIBILITYMENU)) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case 100825666:
                if (columnValue.equals(AccessibilitySettingsTree.SELECT_TO_SPEAK)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 162685333:
                if (columnValue.equals("com.android.tback/net.tatans.soundback.SoundBackService")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case 1558487747:
                if (columnValue.equals("com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                Context context = ((SettingsTree) this).mContext;
                return addTabsLabelToPath(context, str, columnValue2, this.mVisualTab, SearchUtils.getString(context, AccessibilitySettingsTree.SCREEN_READER_CATEGORY_TITLE));
            case 6:
                Context context2 = ((SettingsTree) this).mContext;
                return addTabsLabelToPath(context2, str, columnValue2, this.mPhysicalTab, SearchUtils.getString(context2, AccessibilitySettingsTree.INTERACTION_CONTROL_CATEGORY_TITLE));
            default:
                return addTabsLabelToPath(((SettingsTree) this).mContext, str, columnValue2, this.mGeneralTab, "");
        }
    }

    protected String getTitle(boolean z) {
        return z ? this.mTitle : super.getTitle(false);
    }
}
