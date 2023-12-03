package com.android.settings.search.tree;

import android.content.Context;
import android.text.TextUtils;
import android.view.InputDevice;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.textservice.SpellCheckerInfo;
import android.view.textservice.TextServicesManager;
import com.android.settings.inputmethod.InputMethodFunctionSelectUtils;
import com.android.settings.language.MiuiLanguageAndInputSettings;
import com.android.settings.widget.WorkOnlyCategory;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import java.util.LinkedList;
import java.util.List;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class LanguageSettingsTree extends SettingsTree {
    private String mTitle;

    protected LanguageSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
    }

    public LinkedList<SettingsTree> getSons() {
        int i;
        if ("language_settings".equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                i = sons.size();
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                        i--;
                    }
                    if ("current_input_method".equals(settingsTree.getColumnValue("resource"))) {
                        i = size;
                    }
                }
            } else {
                i = 0;
            }
            List<InputMethodInfo> enabledInputMethodList = ((InputMethodManager) ((SettingsTree) this).mContext.getSystemService("input_method")).getEnabledInputMethodList();
            int size2 = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
            for (int i2 = 0; i2 < size2; i2++) {
                InputMethodInfo inputMethodInfo = enabledInputMethodList.get(i2);
                JSONObject jSONObject = new JSONObject();
                try {
                    String settingsActivity = inputMethodInfo.getSettingsActivity();
                    jSONObject.put("title", inputMethodInfo.loadLabel(((SettingsTree) this).mContext.getPackageManager()).toString());
                    if (!TextUtils.isEmpty(settingsActivity)) {
                        jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent("android.intent.action.MAIN").setClassName(inputMethodInfo.getPackageName(), settingsActivity).toJSONObject());
                    }
                    jSONObject.put("resource", inputMethodInfo.getPackageName());
                    jSONObject.put(YellowPageStatistic.Display.CATEGORY, "keyboard_settings_category");
                    jSONObject.put("temporary", true);
                    addSon(i + i2, SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                } catch (JSONException unused) {
                }
            }
        }
        return super.getSons();
    }

    protected int getStatus() {
        SpellCheckerInfo[] enabledSpellCheckers;
        String columnValue = getColumnValue("resource");
        if ("spellcheckers_settings_title".equals(columnValue)) {
            TextServicesManager textServicesManager = (TextServicesManager) ((SettingsTree) this).mContext.getSystemService("textservices");
            if (!textServicesManager.isSpellCheckerEnabled() || (enabledSpellCheckers = textServicesManager.getEnabledSpellCheckers()) == null || enabledSpellCheckers.length == 0) {
                return 0;
            }
        } else if ("user_dict_settings_title".equals(columnValue)) {
            List<InputMethodInfo> enabledInputMethodList = ((InputMethodManager) ((SettingsTree) this).mContext.getSystemService("input_method")).getEnabledInputMethodList();
            int size = enabledInputMethodList == null ? 0 : enabledInputMethodList.size();
            for (int i = 0; i < size; i++) {
                CharSequence loadLabel = enabledInputMethodList.get(i).loadLabel(((SettingsTree) this).mContext.getPackageManager());
                if (loadLabel != null && loadLabel.toString().contains("AOSP")) {
                    return 3;
                }
            }
            return 0;
        } else if ("vibrate_input_devices".equals(columnValue)) {
            for (int i2 : InputDevice.getDeviceIds()) {
                InputDevice device = InputDevice.getDevice(i2);
                if (device != null && !device.isVirtual() && device.getVibrator().hasVibrator()) {
                    return 3;
                }
            }
            return 0;
        } else if ("security_input".equals(columnValue)) {
            return MiuiLanguageAndInputSettings.supportMiuiSecInputMethod() ? 3 : 0;
        } else if ("full_screen_keyboard_optimization".equals(columnValue)) {
            return InputMethodFunctionSelectUtils.isMiuiImeBottomSupport() ? 3 : 0;
        } else if ("keyboard_skin_follow_system_enable".equals(columnValue)) {
            return Build.IS_INTERNATIONAL_BUILD ? 0 : 3;
        } else if ("mechanical_ime".equals(columnValue)) {
            return InputMethodFunctionSelectUtils.isSupportMechKeyboard(((SettingsTree) this).mContext) ? 3 : 0;
        }
        if (!"language_and_input_for_work_category_title".equals(getColumnValue("category_origin")) || WorkOnlyCategory.virtualKeyboardsForWorkAvailable(((SettingsTree) this).mContext)) {
            return super.getStatus();
        }
        return 0;
    }

    protected String getTitle(boolean z) {
        return !TextUtils.isEmpty(this.mTitle) ? this.mTitle : super.getTitle(z);
    }
}
