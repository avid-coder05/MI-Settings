package android.hardware.vibrator;

import android.os.Parcel;
import android.os.Parcelable;

/* loaded from: classes.dex */
public class CompositeEffect implements Parcelable {
    public static final Parcelable.Creator<CompositeEffect> CREATOR = new Parcelable.Creator<CompositeEffect>() { // from class: android.hardware.vibrator.CompositeEffect.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompositeEffect createFromParcel(Parcel parcel) {
            CompositeEffect compositeEffect = new CompositeEffect();
            compositeEffect.readFromParcel(parcel);
            return compositeEffect;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CompositeEffect[] newArray(int i) {
            return new CompositeEffect[i];
        }
    };
    public int delayMs;
    public int primitive;
    public float scale;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public final void readFromParcel(Parcel parcel) {
        int dataPosition = parcel.dataPosition();
        int readInt = parcel.readInt();
        if (readInt < 0) {
            return;
        }
        try {
            this.delayMs = parcel.readInt();
            if (parcel.dataPosition() - dataPosition < readInt) {
                this.primitive = parcel.readInt();
                if (parcel.dataPosition() - dataPosition < readInt) {
                    this.scale = parcel.readFloat();
                    int dataPosition2 = parcel.dataPosition() - dataPosition;
                }
            }
        } finally {
            parcel.setDataPosition(dataPosition + readInt);
        }
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i) {
        int dataPosition = parcel.dataPosition();
        parcel.writeInt(0);
        parcel.writeInt(this.delayMs);
        parcel.writeInt(this.primitive);
        parcel.writeFloat(this.scale);
        int dataPosition2 = parcel.dataPosition();
        parcel.setDataPosition(dataPosition);
        parcel.writeInt(dataPosition2 - dataPosition);
        parcel.setDataPosition(dataPosition2);
    }
}
