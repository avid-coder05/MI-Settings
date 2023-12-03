package miuix.animation.property;

import android.util.Property;

/* loaded from: classes5.dex */
public abstract class FloatProperty<T> extends Property<T, Float> {
    final String mPropertyName;

    public FloatProperty(String str) {
        super(Float.class, str);
        this.mPropertyName = str;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // android.util.Property
    public Float get(T t) {
        return t == null ? Float.valueOf(0.0f) : Float.valueOf(getValue(t));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.util.Property
    public /* bridge */ /* synthetic */ Float get(Object obj) {
        return get((FloatProperty<T>) obj);
    }

    public abstract float getValue(T t);

    /* renamed from: set  reason: avoid collision after fix types in other method */
    public final void set2(T t, Float f) {
        if (t != null) {
            setValue(t, f.floatValue());
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.util.Property
    public /* bridge */ /* synthetic */ void set(Object obj, Float f) {
        set2((FloatProperty<T>) obj, f);
    }

    public abstract void setValue(T t, float f);

    public String toString() {
        return getClass().getSimpleName() + "{mPropertyName='" + this.mPropertyName + "'}";
    }
}
