package miuix.overscroller.internal.dynamicanimation.animation;

/* loaded from: classes5.dex */
public abstract class FloatPropertyCompat<T> {
    final String mPropertyName;

    public FloatPropertyCompat(String str) {
        this.mPropertyName = str;
    }

    public abstract float getValue(T t);

    public abstract void setValue(T t, float f);
}
