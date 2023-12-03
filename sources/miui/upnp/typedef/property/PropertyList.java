package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes4.dex */
public class PropertyList implements Parcelable {
    private List<Property> propertyList = new ArrayList();
    private static final String TAG = PropertyList.class.getSimpleName();
    public static final Parcelable.Creator<PropertyList> CREATOR = new Parcelable.Creator<PropertyList>() { // from class: miui.upnp.typedef.property.PropertyList.1
        @Override // android.os.Parcelable.Creator
        public PropertyList createFromParcel(Parcel parcel) {
            return new PropertyList(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public PropertyList[] newArray(int i) {
            return new PropertyList[i];
        }
    };

    public PropertyList() {
    }

    public PropertyList(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void cleanState() {
        Iterator<Property> it = this.propertyList.iterator();
        while (it.hasNext()) {
            it.next().getPropertyValue().cleanState();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<Property> getChangedPropertyList() {
        ArrayList arrayList = null;
        for (Property property : this.propertyList) {
            if (property.getPropertyValue().isChanged()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(property);
            }
        }
        return arrayList;
    }

    public List<Property> getList() {
        return this.propertyList;
    }

    public Property getProperty(String str) {
        Property property = null;
        for (Property property2 : this.propertyList) {
            if (property2.getDefinition().getName().equals(str)) {
                property = property2;
            }
        }
        return property;
    }

    public Property getProperty(PropertyDefinition propertyDefinition) {
        for (Property property : this.propertyList) {
            if (property.getDefinition().equals(propertyDefinition)) {
                return property;
            }
        }
        return null;
    }

    public Object getPropertyDataValue(String str) {
        Property property = getProperty(str);
        if (property != null) {
            return property.getCurrentValue();
        }
        return null;
    }

    public Object getPropertyDataValue(PropertyDefinition propertyDefinition) {
        for (Property property : this.propertyList) {
            if (property.getDefinition().equals(propertyDefinition)) {
                return property.getCurrentValue();
            }
        }
        return null;
    }

    public PropertyDefinition getPropertyDefinition(String str) {
        for (Property property : this.propertyList) {
            if (property.getDefinition().getName().equals(str)) {
                return property.getDefinition();
            }
        }
        return null;
    }

    public PropertyValue getPropertyValue(PropertyDefinition propertyDefinition) {
        for (Property property : this.propertyList) {
            if (property.getDefinition().equals(propertyDefinition)) {
                return property.getPropertyValue();
            }
        }
        return null;
    }

    public void initProperty(Property property) {
        this.propertyList.add(property);
    }

    public void initProperty(PropertyDefinition propertyDefinition, Object obj) {
        this.propertyList.add(new Property(propertyDefinition, obj));
    }

    public boolean isChanged() {
        Iterator<Property> it = this.propertyList.iterator();
        while (it.hasNext()) {
            if (it.next().getPropertyValue().isChanged()) {
                return true;
            }
        }
        return false;
    }

    public void readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.propertyList.add((Property) parcel.readParcelable(Property.class.getClassLoader()));
        }
    }

    public boolean setPropertyDataValue(String str, Object obj) {
        PropertyDefinition propertyDefinition;
        if (obj == null || (propertyDefinition = getPropertyDefinition(str)) == null) {
            return false;
        }
        return setPropertyDataValue(propertyDefinition, obj);
    }

    public boolean setPropertyDataValue(PropertyDefinition propertyDefinition, Object obj) {
        if (obj != null) {
            if (propertyDefinition.validate(obj)) {
                PropertyValue propertyValue = getPropertyValue(propertyDefinition);
                if (propertyValue != null) {
                    propertyValue.update(obj);
                    return true;
                }
            } else {
                Log.d(TAG, String.format("invalid value: %s", obj));
            }
        }
        return false;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.propertyList.size());
        Iterator<Property> it = this.propertyList.iterator();
        while (it.hasNext()) {
            parcel.writeParcelable(it.next(), i);
        }
    }
}
