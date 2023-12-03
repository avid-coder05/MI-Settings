package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.field.FieldList;

/* loaded from: classes4.dex */
public class Icon implements Parcelable {
    public static final Parcelable.Creator<Icon> CREATOR = new Parcelable.Creator<Icon>() { // from class: miui.upnp.typedef.device.Icon.1
        @Override // android.os.Parcelable.Creator
        public Icon createFromParcel(Parcel parcel) {
            return new Icon(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Icon[] newArray(int i) {
            return new Icon[i];
        }
    };
    private FieldList fields = new FieldList();

    public Icon() {
        initialize();
    }

    public Icon(Parcel parcel) {
        readFromParcel(parcel);
    }

    private void initialize() {
        this.fields.initField(IconDefinition.MimeType, null);
        this.fields.initField(IconDefinition.Width, null);
        this.fields.initField(IconDefinition.Height, null);
        this.fields.initField(IconDefinition.Depth, null);
        this.fields.initField(IconDefinition.Url, null);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getDepth() {
        return ((Integer) this.fields.getValue(IconDefinition.Depth)).intValue();
    }

    public int getHeight() {
        return ((Integer) this.fields.getValue(IconDefinition.Height)).intValue();
    }

    public String getMimeType() {
        return (String) this.fields.getValue(IconDefinition.MimeType);
    }

    public String getUrl() {
        return (String) this.fields.getValue(IconDefinition.Url);
    }

    public int getWidth() {
        return ((Integer) this.fields.getValue(IconDefinition.Width)).intValue();
    }

    public void readFromParcel(Parcel parcel) {
        this.fields = (FieldList) parcel.readParcelable(FieldList.class.getClassLoader());
    }

    public boolean setDepth(int i) {
        return this.fields.setValue(IconDefinition.Depth, Integer.valueOf(i));
    }

    public boolean setHeight(int i) {
        return this.fields.setValue(IconDefinition.Height, Integer.valueOf(i));
    }

    public boolean setMimeType(String str) {
        return this.fields.setValue(IconDefinition.MimeType, str);
    }

    public boolean setUrl(String str) {
        return this.fields.setValue(IconDefinition.Url, str);
    }

    public boolean setWidth(int i) {
        return this.fields.setValue(IconDefinition.Width, Integer.valueOf(i));
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.fields, i);
    }
}
