package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public abstract class SettingItem<T> implements Parcelable, Comparable<SettingItem<?>> {
    private static final String KEY_KEY = "key";
    private static final String KEY_TYPE = "type";
    protected static final String KEY_VALUE = "value";
    protected static final String TAG = "SettingsBackup";
    public String key;
    private T value;

    private static SettingItem<?> createByType(String str) {
        if ("string".equals(str)) {
            return new KeyStringSettingItem();
        }
        if ("binary".equals(str)) {
            return new KeyBinarySettingItem();
        }
        if ("json".equals(str)) {
            return new KeyJsonSettingItem();
        }
        Log.w("SettingsBackup", "type: " + str + " are not handled!");
        return null;
    }

    public static SettingItem<?> fromJson(JSONObject jSONObject) {
        if (jSONObject != null) {
            SettingItem<?> createByType = createByType(jSONObject.optString("type"));
            if (createByType == null) {
                return null;
            }
            createByType.key = jSONObject.optString("key");
            createByType.setValueFromJson(jSONObject);
            return createByType;
        }
        throw new IllegalArgumentException("json cannot be null");
    }

    @Override // java.lang.Comparable
    public int compareTo(SettingItem<?> settingItem) {
        if (settingItem == null) {
            return 1;
        }
        String str = this.key;
        if (str != null || settingItem.key == null) {
            return str.compareTo(settingItem.key);
        }
        return -1;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void fillFromParcel(Parcel parcel) {
        String readString = parcel.readString();
        String readString2 = parcel.readString();
        this.key = readString;
        setValue(stringToValue(readString2));
    }

    protected abstract Object getJsonValue();

    protected abstract String getType();

    public T getValue() {
        return this.value;
    }

    public void setValue(T t) {
        this.value = t;
    }

    protected abstract void setValueFromJson(JSONObject jSONObject);

    protected abstract T stringToValue(String str);

    public JSONObject toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("key", this.key);
            jSONObject.put("type", getType());
            jSONObject.put("value", getJsonValue());
        } catch (JSONException e) {
            Log.e("SettingsBackup", "JSONException occorred when toJson()", e);
        }
        return jSONObject;
    }

    protected abstract String valueToString(T t);

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        String valueToString = valueToString(getValue());
        parcel.writeString(this.key);
        parcel.writeString(valueToString);
    }
}
