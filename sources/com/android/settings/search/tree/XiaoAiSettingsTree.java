package com.android.settings.search.tree;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.android.settings.MiuiShortcut$Key;
import com.android.settings.MiuiUtils;
import com.android.settings.search.FunctionColumns;
import com.android.settingslib.search.SettingsTree;
import com.android.settingslib.search.TinyIntent;
import miui.payment.PaymentManager;
import miuix.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class XiaoAiSettingsTree extends SettingsTree {
    public static final String GESTURE_SHORTCUT_SETTINGS_SELECT_FRAGMENT = "com.android.settings.GestureShortcutSettingsSelectFragment";
    private boolean mExcludeXiaoAi;

    protected XiaoAiSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mExcludeXiaoAi = false;
        this.mExcludeXiaoAi = MiuiUtils.excludeXiaoAi(context);
    }

    private void addSettingsSonItem(String str) {
        MiuiShortcut$Key.setGestureMap(((SettingsTree) this).mContext);
        if (!MiuiShortcut$Key.getResoureceNameForKey("launch_voice_assistant", ((SettingsTree) this).mContext).equals(str) || MiuiShortcut$Key.sGestureMap.get("launch_voice_assistant") == null) {
            return;
        }
        for (String str2 : MiuiShortcut$Key.sGestureMap.get("launch_voice_assistant")) {
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("resource", str2);
                jSONObject.put(FunctionColumns.FRAGMENT, "com.android.settings.GestureShortcutSettingsSelectFragment");
                Intent intent = getIntent();
                intent.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResoureceNameForKey("launch_voice_assistant", ((SettingsTree) this).mContext));
                intent.putExtra(":settings:show_fragment", "com.android.settings.GestureShortcutSettingsSelectFragment");
                jSONObject.put(PaymentManager.KEY_INTENT, new TinyIntent(intent).toJSONObject());
                addSon(SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this, false));
            } catch (Exception unused) {
                Log.e("XiaoAiSettingsTree", "add son fail");
            }
        }
    }

    public Intent getIntent() {
        if ("com.android.settings.GestureShortcutSettingsSelectFragment".equals(getColumnValue(FunctionColumns.FRAGMENT))) {
            Intent intent = super.getIntent();
            if (intent != null) {
                String stringExtra = intent.getStringExtra(":settings:show_fragment_title");
                if (!TextUtils.isEmpty(stringExtra)) {
                    intent.putExtra(":settings:show_fragment_title", MiuiShortcut$Key.getResourceForKey(stringExtra, ((SettingsTree) this).mContext));
                }
            }
            return intent;
        }
        return MiuiUtils.buildXiaoAiSettingsIntent();
    }

    protected int getStatus() {
        String columnValue = getColumnValue("resource");
        if (this.mExcludeXiaoAi || "ai_button_title".equals(columnValue) || "ai_button_title_global".equals(columnValue)) {
            return 0;
        }
        return super.getStatus();
    }

    public boolean initialize() {
        addSettingsSonItem(getColumnValue("resource"));
        return super.initialize();
    }
}
