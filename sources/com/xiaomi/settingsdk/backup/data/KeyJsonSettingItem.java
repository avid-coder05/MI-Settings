package com.xiaomi.settingsdk.backup.data;

import android.os.Parcelable;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class KeyJsonSettingItem extends SettingItem<JSONObject> {
    public static final Parcelable.Creator<KeyJsonSettingItem> CREATOR = null;
    public static final String TYPE = "json";

    public KeyJsonSettingItem() {
        throw new RuntimeException("Stub!");
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected Object getJsonValue() {
        throw new RuntimeException("Stub!");
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected String getType() {
        throw new RuntimeException("Stub!");
    }

    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public JSONObject stringToValue(String str) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String valueToString(JSONObject jSONObject) {
        throw new RuntimeException("Stub!");
    }
}
