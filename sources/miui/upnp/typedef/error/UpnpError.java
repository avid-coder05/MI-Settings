package miui.upnp.typedef.error;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.settings.search.SearchFeatureProvider;

/* loaded from: classes4.dex */
public class UpnpError implements Comparable<UpnpError>, Parcelable {
    private int code;
    private String message;
    public static final UpnpError OK = new UpnpError(200, "OK");
    public static final UpnpError UPNP_INTERNAL_ERROR = new UpnpError(400, "INTERNAL ERROR");
    public static final UpnpError UPNP_INVALID_ACTION = new UpnpError(401, "INVALID ACTION");
    public static final UpnpError UPNP_INVALID_ARGS = new UpnpError(402, "INVALID ARGS");
    public static final UpnpError UPNP_NOT_FOUND = new UpnpError(404, "NOT FOUND");
    public static final UpnpError UPNP_ACTION_FAILED = new UpnpError(SearchFeatureProvider.REQUEST_CODE, "ACTION FAILED");
    public static final UpnpError UPNP_ARGUMENT_VALUE_INVALID = new UpnpError(600, "ARGUMENT VALUE INVALID");
    public static final UpnpError UPNP_ARGUMENT_VALUE_OUT_OF_RANGE = new UpnpError(601, "ARGUMENT VALUE OUT OF RANGE");
    public static final UpnpError UPNP_ACTION_NOT_IMPLEMENTED = new UpnpError(602, "ACTION NOT IMPLEMENTED");
    public static final UpnpError UPNP_OUT_OF_MEMORY = new UpnpError(603, "OUT OF MEMORY");
    public static final UpnpError UPNP_HUMAN_INTERVENTION_REQUIRED = new UpnpError(604, "HUMAN INTERVENTION REQUIRED");
    public static final UpnpError UPNP_STRING_ARGUMENT_TOO_LONG = new UpnpError(605, "STRING ARGUMENT TOO LONG");
    public static final UpnpError INTERNAL = new UpnpError(800, "internal error");
    public static final UpnpError INTERRUPTED = new UpnpError(801, "interrupted");
    public static final UpnpError NOT_INITIALIZED = new UpnpError(802, "not initialized");
    public static final UpnpError NOT_IMPLEMENTED = new UpnpError(803, "not implemented");
    public static final UpnpError INVALID_ARGUMENT = new UpnpError(804, "invalid argument");
    public static final UpnpError INVALID_OPERATION = new UpnpError(805, "invalid operation");
    public static final UpnpError SERVICE_NOT_BOUND = new UpnpError(806, "service not bound");
    public static final UpnpError SERVICE_BIND_FAILED = new UpnpError(806, "service bind failed");
    public static final UpnpError SERVICE_UNBIND_FAILED = new UpnpError(806, "service unbind failed");
    public static final UpnpError SERVICE_SUBSCRIBE = new UpnpError(810, "subscribe error");
    public static final UpnpError SERVICE_UNSUBSCRIBE = new UpnpError(811, "unsubscribe error");
    public static final UpnpError SERVICE_SUBSCRIBED = new UpnpError(812, "subscribed");
    public static final UpnpError SERVICE_UNSUBSCRIBED = new UpnpError(813, "unsubscribed");
    public static final UpnpError ACTION_EXECUTE = new UpnpError(820, "action execute error");
    public static final UpnpError OBJECT_REGISTERED = new UpnpError(830, "registered");
    public static final UpnpError OBJECT_NOT_REGISTERED = new UpnpError(831, "not registered");
    public static final UpnpError WIFI = new UpnpError(840, "wifi");
    public static final UpnpError WIFI_AP_ENABLED = new UpnpError(841, "WIFI AP enabled");
    public static final UpnpError WIFI_DISABLED = new UpnpError(842, "WIFI disabled");
    public static final UpnpError WIFI_ENABLE = new UpnpError(843, "WIFI enable failed");
    public static final UpnpError WIFI_CONNECT = new UpnpError(844, "WIFI connect failed");
    public static final UpnpError WIFI_CONFIG = new UpnpError(845, "WIFI configuration failed");
    public static final UpnpError WIFI_ADD_NETWORK = new UpnpError(846, "WIFI add network failed");
    public static final UpnpError SESSION_CREATE = new UpnpError(850, "session create failed");
    public static final UpnpError SESSION_DESTROY = new UpnpError(851, "session destroy failed");
    public static final Parcelable.Creator<UpnpError> CREATOR = new Parcelable.Creator<UpnpError>() { // from class: miui.upnp.typedef.error.UpnpError.1
        @Override // android.os.Parcelable.Creator
        public UpnpError createFromParcel(Parcel parcel) {
            return new UpnpError(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public UpnpError[] newArray(int i) {
            return new UpnpError[i];
        }
    };

    public UpnpError() {
    }

    public UpnpError(int i, String str) {
        this.code = i;
        this.message = str;
    }

    public UpnpError(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // java.lang.Comparable
    public int compareTo(UpnpError upnpError) {
        return this.code - upnpError.code;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return (obj instanceof UpnpError) && this.code == ((UpnpError) obj).code;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public int hashCode() {
        return this.code;
    }

    public void readFromParcel(Parcel parcel) {
        this.code = parcel.readInt();
        this.message = parcel.readString();
    }

    public void setMessage(String str) {
        this.message = str;
    }

    public String toString() {
        return String.valueOf(this.code) + ' ' + this.message;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.code);
        String str = this.message;
        if (str == null) {
            str = "";
        }
        parcel.writeString(str);
    }
}
