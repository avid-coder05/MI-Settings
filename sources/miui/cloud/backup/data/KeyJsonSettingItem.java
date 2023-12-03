package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class KeyJsonSettingItem extends SettingItem<JSONObject> {
    public static final Parcelable.Creator<KeyJsonSettingItem> CREATOR = new Parcelable.Creator<KeyJsonSettingItem>() { // from class: miui.cloud.backup.data.KeyJsonSettingItem.1
        @Override // android.os.Parcelable.Creator
        public KeyJsonSettingItem createFromParcel(Parcel parcel) {
            KeyJsonSettingItem keyJsonSettingItem = new KeyJsonSettingItem();
            keyJsonSettingItem.fillFromParcel(parcel);
            return keyJsonSettingItem;
        }

        @Override // android.os.Parcelable.Creator
        public KeyJsonSettingItem[] newArray(int i) {
            return new KeyJsonSettingItem[i];
        }
    };
    public static final String TYPE = "json";

    @Override // miui.cloud.backup.data.SettingItem
    protected Object getJsonValue() {
        return getValue();
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected String getType() {
        return "json";
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(jSONObject.optJSONObject("value"));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public JSONObject stringToValue(String str) {
        try {
            return new JSONObject(str);
        } catch (JSONException e) {
            Log.e("SettingsBackup", "JSONException occorred when stringToValue()", e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public String valueToString(JSONObject jSONObject) {
        return jSONObject.toString();
    }
}
