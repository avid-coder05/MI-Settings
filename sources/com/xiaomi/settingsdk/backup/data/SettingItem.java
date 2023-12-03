package com.xiaomi.settingsdk.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public abstract class SettingItem<T> implements Parcelable, Comparable<SettingItem<?>> {
    protected static final String KEY_VALUE = "value";
    protected static final String TAG = "SettingsBackup";
    public String key;

    public SettingItem() {
        throw new RuntimeException("Stub!");
    }

    public static SettingItem<?> fromJson(JSONObject jSONObject) {
        throw new RuntimeException("Stub!");
    }

    @Override // java.lang.Comparable
    public int compareTo(SettingItem<?> settingItem) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    protected void fillFromParcel(Parcel parcel) {
        throw new RuntimeException("Stub!");
    }

    protected abstract Object getJsonValue();

    protected abstract String getType();

    public T getValue() {
        throw new RuntimeException("Stub!");
    }

    public void setValue(T t) {
        throw new RuntimeException("Stub!");
    }

    protected abstract void setValueFromJson(JSONObject jSONObject);

    protected abstract T stringToValue(String str);

    public JSONObject toJson() {
        throw new RuntimeException("Stub!");
    }

    protected abstract String valueToString(T t);

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        throw new RuntimeException("Stub!");
    }
}
