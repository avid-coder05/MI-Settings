package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class KeyStringSettingItem extends SettingItem<String> {
    public static final Parcelable.Creator<KeyStringSettingItem> CREATOR = new Parcelable.Creator<KeyStringSettingItem>() { // from class: miui.cloud.backup.data.KeyStringSettingItem.1
        @Override // android.os.Parcelable.Creator
        public KeyStringSettingItem createFromParcel(Parcel parcel) {
            KeyStringSettingItem keyStringSettingItem = new KeyStringSettingItem();
            keyStringSettingItem.fillFromParcel(parcel);
            return keyStringSettingItem;
        }

        @Override // android.os.Parcelable.Creator
        public KeyStringSettingItem[] newArray(int i) {
            return new KeyStringSettingItem[i];
        }
    };
    public static final String TYPE = "string";

    @Override // miui.cloud.backup.data.SettingItem
    protected Object getJsonValue() {
        return getValue();
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected String getType() {
        return "string";
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(jSONObject.optString("value"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public String stringToValue(String str) {
        return str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public String valueToString(String str) {
        return str;
    }
}
