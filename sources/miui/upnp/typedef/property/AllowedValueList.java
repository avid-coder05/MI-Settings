package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import java.util.ArrayList;
import java.util.Iterator;
import miui.upnp.typedef.datatype.DataType;

/* loaded from: classes4.dex */
public class AllowedValueList implements Parcelable {
    private ArrayList<Object> allowedValues = new ArrayList<>();
    private DataType dataType;
    private static final String TAG = AllowedValueList.class.getSimpleName();
    public static final Parcelable.Creator<AllowedValueList> CREATOR = new Parcelable.Creator<AllowedValueList>() { // from class: miui.upnp.typedef.property.AllowedValueList.1
        @Override // android.os.Parcelable.Creator
        public AllowedValueList createFromParcel(Parcel parcel) {
            return new AllowedValueList(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public AllowedValueList[] newArray(int i) {
            return new AllowedValueList[i];
        }
    };

    private AllowedValueList() {
    }

    public AllowedValueList(Parcel parcel) {
        readFromParcel(parcel);
    }

    public AllowedValueList(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean appendAllowedValue(Object obj) {
        if (this.dataType.getJavaDataType().isInstance(obj)) {
            this.allowedValues.add(obj);
            return true;
        }
        Log.d(TAG, "append dataType invalid");
        return false;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ArrayList<Object> getAllowedValues() {
        return this.allowedValues;
    }

    public boolean isValid(Object obj) {
        if (!this.dataType.getJavaDataType().isInstance(obj)) {
            Log.d(TAG, "dataType invalid");
            return false;
        }
        boolean contains = this.allowedValues.contains(obj);
        if (!contains) {
            if ((obj instanceof String) && ((String) obj).equalsIgnoreCase("NONE")) {
                Log.e(TAG, "value is \"NONE\", As a legal value to process!");
                return true;
            }
            StringBuilder sb = new StringBuilder((int) MiuiWindowManager$LayoutParams.EXTRA_FLAG_LAYOUT_NOTCH_LANDSCAPE);
            sb.append(contains);
            sb.append("invalid, ");
            sb.append("value must be one of these: ");
            Iterator<Object> it = this.allowedValues.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                sb.append(",");
            }
            Log.e(TAG, sb.toString());
        }
        return contains;
    }

    public void readFromParcel(Parcel parcel) {
        this.dataType = DataType.valueOf(parcel.readString());
        parcel.readList(this.allowedValues, Object.class.getClassLoader());
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.dataType.toString());
        parcel.writeList(this.allowedValues);
    }
}
