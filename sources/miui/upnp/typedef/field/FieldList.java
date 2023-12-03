package miui.upnp.typedef.field;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes4.dex */
public class FieldList implements Parcelable {
    public static final Parcelable.Creator<FieldList> CREATOR = new Parcelable.Creator<FieldList>() { // from class: miui.upnp.typedef.field.FieldList.1
        @Override // android.os.Parcelable.Creator
        public FieldList createFromParcel(Parcel parcel) {
            return new FieldList(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public FieldList[] newArray(int i) {
            return new FieldList[i];
        }
    };
    private Map<String, Field> fields = new HashMap();

    public FieldList() {
    }

    public FieldList(Parcel parcel) {
        readFromParcel(parcel);
    }

    private Field getField(String str) {
        return this.fields.get(str);
    }

    private Field getField(FieldDefinition fieldDefinition) {
        return this.fields.get(fieldDefinition.getName());
    }

    private FieldDefinition getFieldDefinition(String str) {
        Field field = getField(str);
        if (field == null) {
            return null;
        }
        return field.getDefinition();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Object getValue(String str) {
        Field field = getField(str);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    public Object getValue(FieldDefinition fieldDefinition) {
        Field field = getField(fieldDefinition);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    public void initField(Field field) {
        this.fields.put(field.getDefinition().getName(), field);
    }

    public void initField(FieldDefinition fieldDefinition, Object obj) {
        this.fields.put(fieldDefinition.getName(), new Field(fieldDefinition, obj));
    }

    public void readFromParcel(Parcel parcel) {
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            Field field = (Field) parcel.readParcelable(Field.class.getClassLoader());
            this.fields.put(field.getDefinition().getName(), field);
        }
    }

    public boolean setValue(String str, Object obj) {
        Field field = getField(str);
        if (field == null) {
            return false;
        }
        field.setValue(obj);
        return true;
    }

    public boolean setValue(FieldDefinition fieldDefinition, Object obj) {
        Field field = getField(fieldDefinition);
        if (field == null) {
            return false;
        }
        field.setValue(obj);
        return true;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.fields.size());
        Iterator<Field> it = this.fields.values().iterator();
        while (it.hasNext()) {
            parcel.writeParcelable(it.next(), i);
        }
    }
}
