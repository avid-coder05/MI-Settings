package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class KeyBinarySettingItem extends SettingItem<byte[]> {
    public static final Parcelable.Creator<KeyBinarySettingItem> CREATOR = new Parcelable.Creator<KeyBinarySettingItem>() { // from class: miui.cloud.backup.data.KeyBinarySettingItem.1
        @Override // android.os.Parcelable.Creator
        public KeyBinarySettingItem createFromParcel(Parcel parcel) {
            KeyBinarySettingItem keyBinarySettingItem = new KeyBinarySettingItem();
            keyBinarySettingItem.fillFromParcel(parcel);
            return keyBinarySettingItem;
        }

        @Override // android.os.Parcelable.Creator
        public KeyBinarySettingItem[] newArray(int i) {
            return new KeyBinarySettingItem[i];
        }
    };
    public static final String TYPE = "binary";

    @Override // miui.cloud.backup.data.SettingItem
    protected Object getJsonValue() {
        return valueToString(getValue());
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected String getType() {
        return "binary";
    }

    @Override // miui.cloud.backup.data.SettingItem
    protected void setValueFromJson(JSONObject jSONObject) {
        setValue(stringToValue(jSONObject.optString("value")));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public byte[] stringToValue(String str) {
        return Base64.decode(str, 2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miui.cloud.backup.data.SettingItem
    public String valueToString(byte[] bArr) {
        return Base64.encodeToString(bArr, 2);
    }
}
