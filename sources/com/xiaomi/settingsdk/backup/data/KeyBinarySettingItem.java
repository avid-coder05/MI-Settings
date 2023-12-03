package com.xiaomi.settingsdk.backup.data;

import android.os.Parcelable;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class KeyBinarySettingItem extends SettingItem<byte[]> {
    public static final Parcelable.Creator<KeyBinarySettingItem> CREATOR = null;
    public static final String TYPE = "binary";

    public KeyBinarySettingItem() {
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
    public byte[] stringToValue(String str) {
        throw new RuntimeException("Stub!");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.settingsdk.backup.data.SettingItem
    public String valueToString(byte[] bArr) {
        throw new RuntimeException("Stub!");
    }
}
