package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes4.dex */
public class PropertyValue implements Parcelable {
    public static final Parcelable.Creator<PropertyValue> CREATOR = new Parcelable.Creator<PropertyValue>() { // from class: miui.upnp.typedef.property.PropertyValue.1
        @Override // android.os.Parcelable.Creator
        public PropertyValue createFromParcel(Parcel parcel) {
            return new PropertyValue(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PropertyValue[] newArray(int i) {
            return new PropertyValue[i];
        }
    };
    private static final String TAG = "PropertyValue";
    private boolean single = true;
    private boolean isChanged = false;
    private boolean isOldValueAvailable = false;
    private volatile Object oldValue = null;
    private volatile Object currentValue = null;
    private volatile List<Object> valueList = new ArrayList();

    public PropertyValue() {
    }

    public PropertyValue(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static PropertyValue create(Object obj) {
        PropertyValue propertyValue = new PropertyValue();
        propertyValue.single = true;
        propertyValue.oldValue = obj;
        propertyValue.isOldValueAvailable = true;
        return propertyValue;
    }

    public void addMultipleValue(Object obj) {
        this.single = false;
        this.valueList.add(obj);
    }

    public void cleanState() {
        this.isChanged = false;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Object getCurrentValue() {
        return this.currentValue;
    }

    public Object getOldValue() {
        return this.oldValue;
    }

    public Object getValue() {
        return this.currentValue != null ? this.currentValue : this.oldValue;
    }

    public List<Object> getValueList() {
        return this.valueList;
    }

    public boolean isChanged() {
        return this.isChanged;
    }

    public boolean isMultiple() {
        return !this.single;
    }

    public boolean isOldValueAvailable() {
        return this.isOldValueAvailable;
    }

    public boolean isSingle() {
        return this.single;
    }

    public void readFromParcel(Parcel parcel) {
        boolean z = parcel.readInt() == 1;
        this.single = z;
        if (!z) {
            int readInt = parcel.readInt();
            for (int i = 0; i < readInt; i++) {
                this.valueList.add(parcel.readValue(Object.class.getClassLoader()));
            }
            return;
        }
        this.isChanged = parcel.readInt() == 1;
        this.isOldValueAvailable = parcel.readInt() == 1;
        if (parcel.readInt() == 1) {
            this.oldValue = parcel.readValue(Object.class.getClassLoader());
        }
        if (parcel.readInt() == 1) {
            this.currentValue = parcel.readValue(Object.class.getClassLoader());
        }
    }

    public void setMultiple(boolean z) {
        this.single = !z;
    }

    public void setSingle(boolean z) {
        this.single = z;
    }

    public void update(Object obj) {
        if (obj == null) {
            Log.e(TAG, "value is null");
            return;
        }
        this.single = true;
        if (this.oldValue != null && !this.oldValue.getClass().equals(obj.getClass())) {
            Log.e(TAG, String.format("invalid: oldValue is %s, new value is %s (%s)", this.oldValue.getClass().getSimpleName(), obj.getClass().getSimpleName(), obj.toString()));
        } else if (this.currentValue == null) {
            this.currentValue = obj;
            this.isChanged = true;
        } else if (this.currentValue.equals(obj)) {
        } else {
            this.oldValue = this.currentValue;
            this.isOldValueAvailable = true;
            this.currentValue = obj;
            this.isChanged = true;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.single ? 1 : 0);
        if (!this.single) {
            int size = this.valueList.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeValue(this.valueList.get(i2));
            }
            return;
        }
        parcel.writeInt(this.isChanged ? 1 : 0);
        parcel.writeInt(this.isOldValueAvailable ? 1 : 0);
        if (this.oldValue == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeValue(this.oldValue);
        }
        if (this.currentValue == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        parcel.writeValue(this.currentValue);
    }
}
