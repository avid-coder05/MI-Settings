package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import com.android.settings.FingerprintHelper;
import com.android.settings.MiuiShortcut$Key;
import com.android.settings.MiuiShortcut$System;
import com.android.settings.R;
import com.android.settings.search.FunctionColumns;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import java.util.List;
import java.util.Map;
import miui.os.Build;
import miui.payment.PaymentManager;
import miuix.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class GestureSettingsTree extends SettingsTree {
    public static final String GESTURE_KNOCK_V_SETTINGS_FRAGMENT = "com.android.settings.knock.KnockGestureVSelectFragment";
    public static final String GESTURE_SHORTCUT_SETTINGS_SELECT_FRAGMENT = "com.android.settings.GestureShortcutSettingsSelectFragment";
    public static final String GESTURE_SHORTCUT_SETTINGS_TITLE = "gesture_settings_title";
    public static final String KNOCK_GESTURE_V_TITLE = "knock_slide_v";
    private static final String REGIONAL_SCREEN_SHOT = "regional_screen_shot";
    private static final String SHORTCUT_AI_SETTINGS = "ai_button_title";
    private static final String SHORTCUT_AI_SETTINGS_GLOBAL = "ai_button_title_global";
    private static final String TAG = "GestureSettingsTree";
    private final Context mContext;
    private final FingerprintHelper mFingerprintHelper;

    protected GestureSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mContext = context;
        this.mFingerprintHelper = new FingerprintHelper(context);
        MiuiShortcut$Key.setGestureMap(context);
    }

    private void addSettingsSonItem(String str) {
        for (Map.Entry<String, List<String>> entry : MiuiShortcut$Key.sGestureMap.entrySet()) {
            if (MiuiShortcut$Key.getResoureceNameForKey(entry.getKey(), this.mContext).equals(str)) {
                for (String str2 : entry.getValue()) {
                    try {
                        JSONObject jSONObject = new JSONObject();
                        jSONObject.put("resource", str2);
                        jSONObject.put(FunctionColumns.FRAGMENT, "com.android.settings.GestureShortcutSettingsSelectFragment");
                        Intent intent = getIntent();
                        intent.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResoureceNameForKey(entry.getKey(), this.mContext));
                        intent.putExtra(":settings:show_fragment", "com.android.settings.GestureShortcutSettingsSelectFragment");
                        jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(intent).toJSONObject());
                        addSon(SettingsTree.newInstance(this.mContext, jSONObject, this, false));
                    } catch (Exception unused) {
                        Log.e(TAG, "add son fail");
                    }
                }
            }
        }
        if ("gesture_settings_title".equals(str) && MiuiShortcut$System.hasKnockFeature(this.mContext)) {
            try {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("resource", KNOCK_GESTURE_V_TITLE);
                Intent intent2 = getIntent();
                intent2.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResoureceNameForKey(KNOCK_GESTURE_V_TITLE, this.mContext));
                intent2.putExtra(":settings:show_fragment", GESTURE_KNOCK_V_SETTINGS_FRAGMENT);
                jSONObject2.put(PaymentManager.KEY_INTENT, new TinyIntent(intent2).toJSONObject());
                addSon(SettingsTree.newInstance(this.mContext, jSONObject2, this, true));
            } catch (Exception unused2) {
                Log.e(TAG, "add son fail for knock_slide_v");
            }
        }
    }

    public Intent getIntent() {
        Intent intent = super.getIntent();
        String resourceForKey = MiuiShortcut$Key.getResourceForKey(getColumnValue("resource"), this.mContext);
        if (intent != null) {
            String stringExtra = intent.getStringExtra(":settings:show_fragment_title");
            if (intent.getStringExtra(":settings:show_fragment_title") == null) {
                intent.putExtra(":settings:show_fragment_title", resourceForKey);
            } else {
                intent.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResourceForKey(stringExtra, this.mContext));
            }
        }
        return intent;
    }

    protected String getTitle(boolean z) {
        return (z && "three_gesture_long_press".equals(getColumnValue("resource"))) ? this.mContext.getString(R.string.three_gesture_long_press, 3) : super.getTitle(true);
    }

    public boolean initialize() {
        String columnValue = getColumnValue("resource");
        if (MiuiShortcut$System.isSupportNewVersionKeySettings(this.mContext)) {
            addSettingsSonItem(columnValue);
            String keyForResourceName = MiuiShortcut$Key.getKeyForResourceName(columnValue);
            if (MiuiShortcut$Key.mHidenPreferenceList.contains(keyForResourceName)) {
                return true;
            }
            keyForResourceName.hashCode();
            char c = 65535;
            switch (keyForResourceName.hashCode()) {
                case -304503603:
                    if (keyForResourceName.equals(REGIONAL_SCREEN_SHOT)) {
                        c = 0;
                        break;
                    }
                    break;
                case 320799936:
                    if (keyForResourceName.equals(SHORTCUT_AI_SETTINGS_GLOBAL)) {
                        c = 1;
                        break;
                    }
                    break;
                case 381730133:
                    if (keyForResourceName.equals("knock_gesture_v")) {
                        c = 2;
                        break;
                    }
                    break;
                case 1139265648:
                    if (keyForResourceName.equals("fingerprint_double_tap")) {
                        c = 3;
                        break;
                    }
                    break;
                case 1352535838:
                    if (keyForResourceName.equals("turn_on_torch")) {
                        c = 4;
                        break;
                    }
                    break;
                case 2121279563:
                    if (keyForResourceName.equals("back_tap")) {
                        c = 5;
                        break;
                    }
                    break;
                case 2144034242:
                    if (keyForResourceName.equals(SHORTCUT_AI_SETTINGS)) {
                        c = 6;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    if (!MiuiShortcut$System.supportPartialScreenShot()) {
                        return true;
                    }
                    break;
                case 1:
                    if (!Build.IS_INTERNATIONAL_BUILD || !MiuiShortcut$System.shouldShowAiButton()) {
                        return true;
                    }
                    break;
                case 2:
                    if (!MiuiShortcut$System.hasKnockFeature(this.mContext)) {
                        return true;
                    }
                    break;
                case 3:
                    if (!SettingsFeatures.IS_SUPPORT_FINGERPRINT_TAP || !this.mFingerprintHelper.isHardwareDetected()) {
                        return true;
                    }
                    break;
                case 4:
                    if (!Build.hasCameraFlash(this.mContext)) {
                        return true;
                    }
                    break;
                case 5:
                    if (!SettingsFeatures.hasBackTapSensorFeature(this.mContext)) {
                        return true;
                    }
                    break;
                case 6:
                    if (Build.IS_INTERNATIONAL_BUILD || !MiuiShortcut$System.shouldShowAiButton()) {
                        return true;
                    }
                    break;
            }
            return super.initialize();
        }
        return true;
    }
}
