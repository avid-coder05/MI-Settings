package miuix.animation.property;

import java.util.Objects;

/* loaded from: classes5.dex */
public class ValueProperty extends FloatProperty {
    private volatile String mName;

    public ValueProperty(String str) {
        super(str);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !ValueProperty.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        return Objects.equals(getName(), ((ValueProperty) obj).getName());
    }

    @Override // android.util.Property
    public String getName() {
        return this.mName != null ? this.mName : super.getName();
    }

    @Override // miuix.animation.property.FloatProperty
    public float getValue(Object obj) {
        Float f;
        if (!(obj instanceof ValueTargetObject) || (f = (Float) ((ValueTargetObject) obj).getPropertyValue(getName(), Float.TYPE)) == null) {
            return 0.0f;
        }
        return f.floatValue();
    }

    public int hashCode() {
        return Objects.hash(getName());
    }

    public void setName(String str) {
        this.mName = str;
    }

    @Override // miuix.animation.property.FloatProperty
    public void setValue(Object obj, float f) {
        if (obj instanceof ValueTargetObject) {
            ((ValueTargetObject) obj).setPropertyValue(getName(), Float.TYPE, Float.valueOf(f));
        }
    }

    @Override // miuix.animation.property.FloatProperty
    public String toString() {
        return "ValueProperty{name=" + getName() + '}';
    }
}
