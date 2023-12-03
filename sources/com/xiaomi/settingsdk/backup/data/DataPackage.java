package com.xiaomi.settingsdk.backup.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import org.json.JSONObject;

/* loaded from: classes2.dex */
public class DataPackage implements Parcelable {
    public static final Parcelable.Creator<DataPackage> CREATOR = null;
    public static final String KEY_DATA_PACKAGE = "data_package";
    public static final String KEY_VERSION = "version";

    public DataPackage() {
        throw new RuntimeException("Stub!");
    }

    public static DataPackage fromWrappedBundle(Bundle bundle) {
        throw new RuntimeException("Stub!");
    }

    public static int getVersionFromBundle(Bundle bundle) {
        throw new RuntimeException("Stub!");
    }

    public void addAbstractDataItem(String str, SettingItem<?> settingItem) {
        throw new RuntimeException("Stub!");
    }

    public void addKeyFile(String str, File file) throws FileNotFoundException {
        throw new RuntimeException("Stub!");
    }

    public void addKeyJson(String str, JSONObject jSONObject) {
        throw new RuntimeException("Stub!");
    }

    public void addKeyValue(String str, String str2) {
        throw new RuntimeException("Stub!");
    }

    public void appendToWrappedBundle(Bundle bundle) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    public SettingItem<?> get(String str) {
        throw new RuntimeException("Stub!");
    }

    public Map<String, SettingItem<?>> getDataItems() {
        throw new RuntimeException("Stub!");
    }

    public ParcelFileDescriptor getFile(String str) {
        throw new RuntimeException("Stub!");
    }

    public Map<String, ParcelFileDescriptor> getFileItems() {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        throw new RuntimeException("Stub!");
    }
}
