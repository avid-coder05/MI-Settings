package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes4.dex */
public class Action implements Parcelable {
    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() { // from class: miui.upnp.typedef.device.Action.1
        @Override // android.os.Parcelable.Creator
        public Action createFromParcel(Parcel parcel) {
            return new Action(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public Action[] newArray(int i) {
            return new Action[i];
        }
    };
    private List<Argument> arguments = new ArrayList();
    private String name;
    private Service service;

    public Action() {
    }

    public Action(Parcel parcel) {
        readFromParcel(parcel);
    }

    public Action(String str) {
        this.name = str;
    }

    public void addArgument(Argument argument) {
        this.arguments.add(argument);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<Argument> getArguments() {
        return this.arguments;
    }

    public String getName() {
        return this.name;
    }

    public Service getService() {
        return this.service;
    }

    public void readFromParcel(Parcel parcel) {
        this.name = parcel.readString();
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.arguments.add(new Argument(parcel.readString(), parcel.readString(), parcel.readString()));
        }
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.name);
        parcel.writeInt(this.arguments.size());
        for (Argument argument : this.arguments) {
            parcel.writeString(argument.getName());
            parcel.writeString(argument.getDirection().toString());
            parcel.writeString(argument.getRelatedProperty());
        }
    }
}
