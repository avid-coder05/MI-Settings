package miui.cloud.backup.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/* loaded from: classes3.dex */
public class DataPackage implements Parcelable {
    public static final Parcelable.Creator<DataPackage> CREATOR = new Parcelable.Creator<DataPackage>() { // from class: miui.cloud.backup.data.DataPackage.1
        @Override // android.os.Parcelable.Creator
        public DataPackage createFromParcel(Parcel parcel) {
            return DataPackage.parseDataPackageBundle(parcel.readBundle());
        }

        @Override // android.os.Parcelable.Creator
        public DataPackage[] newArray(int i) {
            return new DataPackage[i];
        }
    };
    public static final String KEY_DATA_PACKAGE = "data_package";
    public static final String KEY_VERSION = "version";
    private final Map<String, SettingItem<?>> mDataItems = new HashMap();
    private final Map<String, ParcelFileDescriptor> mFileItems = new HashMap();

    public static DataPackage fromWrappedBundle(Bundle bundle) {
        Bundle bundle2 = (Bundle) bundle.clone();
        bundle2.setClassLoader(SettingItem.class.getClassLoader());
        return parseDataPackageBundle(bundle2.getBundle("data_package"));
    }

    private Bundle getDataPackageBundle() {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, SettingItem<?>> entry : this.mDataItems.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, ParcelFileDescriptor> entry2 : this.mFileItems.entrySet()) {
            bundle.putParcelable(entry2.getKey(), entry2.getValue());
        }
        return bundle;
    }

    public static int getVersionFromBundle(Bundle bundle) {
        return bundle.getInt("version");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static DataPackage parseDataPackageBundle(Bundle bundle) {
        if (bundle == null) {
            return null;
        }
        bundle.setClassLoader(SettingItem.class.getClassLoader());
        DataPackage dataPackage = new DataPackage();
        for (String str : bundle.keySet()) {
            Parcelable parcelable = bundle.getParcelable(str);
            if (parcelable instanceof SettingItem) {
                dataPackage.mDataItems.put(str, (SettingItem) parcelable);
            }
            if (parcelable instanceof ParcelFileDescriptor) {
                dataPackage.mFileItems.put(str, (ParcelFileDescriptor) parcelable);
            }
        }
        return dataPackage;
    }

    public void addAbstractDataItem(String str, SettingItem<?> settingItem) {
        this.mDataItems.put(str, settingItem);
    }

    public void addKeyFile(String str, File file) throws FileNotFoundException {
        this.mFileItems.put(str, ParcelFileDescriptor.open(file, 268435456));
    }

    public void addKeyJson(String str, JSONObject jSONObject) {
        KeyJsonSettingItem keyJsonSettingItem = new KeyJsonSettingItem();
        keyJsonSettingItem.key = str;
        keyJsonSettingItem.setValue(jSONObject);
        this.mDataItems.put(str, keyJsonSettingItem);
    }

    public void addKeyValue(String str, String str2) {
        KeyStringSettingItem keyStringSettingItem = new KeyStringSettingItem();
        keyStringSettingItem.key = str;
        keyStringSettingItem.setValue(str2);
        this.mDataItems.put(str, keyStringSettingItem);
    }

    public void appendToWrappedBundle(Bundle bundle) {
        bundle.putBundle("data_package", getDataPackageBundle());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SettingItem<?> get(String str) {
        return this.mDataItems.get(str);
    }

    public Map<String, SettingItem<?>> getDataItems() {
        return this.mDataItems;
    }

    public ParcelFileDescriptor getFile(String str) {
        return this.mFileItems.get(str);
    }

    public Map<String, ParcelFileDescriptor> getFileItems() {
        return this.mFileItems;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(getDataPackageBundle());
    }
}
