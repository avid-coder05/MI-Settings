package com.android.settings.search.tree;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TtsEngines;
import com.android.settingslib.search.SettingsTree;
import java.util.LinkedList;
import java.util.List;
import miui.yellowpage.YellowPageStatistic;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class TtsSettingsTree extends SettingsTree {
    private String mTitle;

    protected TtsSettingsTree(Context context, JSONObject jSONObject, SettingsTree settingsTree, boolean z) throws JSONException {
        super(context, jSONObject, settingsTree, z);
        this.mTitle = jSONObject.optString("title");
    }

    public LinkedList<SettingsTree> getSons() {
        if ("tts_settings_title".equals(getColumnValue("resource"))) {
            LinkedList sons = super.getSons();
            if (sons != null) {
                for (int size = sons.size() - 1; size >= 0; size--) {
                    SettingsTree settingsTree = (SettingsTree) sons.get(size);
                    if (Boolean.parseBoolean(settingsTree.getColumnValue("temporary"))) {
                        settingsTree.removeSelf();
                    }
                }
            }
            List engines = new TtsEngines(((SettingsTree) this).mContext).getEngines();
            for (int i = 0; i < engines.size(); i++) {
                TextToSpeech.EngineInfo engineInfo = (TextToSpeech.EngineInfo) engines.get(i);
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("title", engineInfo.label);
                    jSONObject.put("resource", engineInfo.name);
                    jSONObject.put(YellowPageStatistic.Display.CATEGORY, "tts_engine_preference_section_title");
                    jSONObject.put("temporary", true);
                    addSon(i, SettingsTree.newInstance(((SettingsTree) this).mContext, jSONObject, this));
                } catch (JSONException unused) {
                }
            }
        }
        return super.getSons();
    }

    protected String getTitle(boolean z) {
        return Boolean.parseBoolean(getColumnValue("temporary")) ? this.mTitle : super.getTitle(z);
    }
}
