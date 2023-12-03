package miuix.animation;

import java.util.concurrent.atomic.AtomicInteger;
import miui.vip.VipService;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;
import miuix.animation.property.IntValueProperty;
import miuix.animation.property.ValueProperty;
import miuix.animation.property.ValueTargetObject;
import miuix.animation.property.ViewProperty;

/* loaded from: classes5.dex */
public class ValueTarget extends IAnimTarget {
    static ITargetCreator sCreator = new ITargetCreator() { // from class: miuix.animation.ValueTarget.1
        @Override // miuix.animation.ITargetCreator
        public IAnimTarget createTarget(Object obj) {
            return new ValueTarget(obj);
        }
    };
    private AtomicInteger mMaxType;
    private ValueTargetObject mTargetObj;

    public ValueTarget() {
        this(null);
    }

    private ValueTarget(Object obj) {
        this.mMaxType = new AtomicInteger(VipService.VIP_SERVICE_FAILURE);
        this.mTargetObj = new ValueTargetObject(obj == null ? Integer.valueOf(getId()) : obj);
    }

    private boolean isPredefinedProperty(Object obj) {
        return (obj instanceof ValueProperty) || (obj instanceof ViewProperty) || (obj instanceof ColorProperty);
    }

    @Override // miuix.animation.IAnimTarget
    public void clean() {
    }

    public FloatProperty createProperty(String str, Class<?> cls) {
        return (cls == Integer.TYPE || cls == Integer.class) ? new IntValueProperty(str) : new ValueProperty(str);
    }

    @Override // miuix.animation.IAnimTarget
    public float getDefaultMinVisible() {
        return 0.002f;
    }

    public FloatProperty getFloatProperty(String str) {
        return createProperty(str, Float.TYPE);
    }

    @Override // miuix.animation.IAnimTarget
    public int getIntValue(IIntValueProperty iIntValueProperty) {
        if (isPredefinedProperty(iIntValueProperty)) {
            Integer num = (Integer) this.mTargetObj.getPropertyValue(iIntValueProperty.getName(), Integer.TYPE);
            if (num == null) {
                return Integer.MAX_VALUE;
            }
            return num.intValue();
        }
        return iIntValueProperty.getIntValue(this.mTargetObj.getRealObject());
    }

    @Override // miuix.animation.IAnimTarget
    public float getMinVisibleChange(Object obj) {
        if (!(obj instanceof IIntValueProperty) || (obj instanceof ColorProperty)) {
            return super.getMinVisibleChange(obj);
        }
        return 1.0f;
    }

    @Override // miuix.animation.IAnimTarget
    public Object getTargetObject() {
        return this.mTargetObj;
    }

    @Override // miuix.animation.IAnimTarget
    public float getValue(FloatProperty floatProperty) {
        if (isPredefinedProperty(floatProperty)) {
            Float f = (Float) this.mTargetObj.getPropertyValue(floatProperty.getName(), Float.TYPE);
            if (f == null) {
                return Float.MAX_VALUE;
            }
            return f.floatValue();
        }
        return floatProperty.getValue(this.mTargetObj.getRealObject());
    }

    @Override // miuix.animation.IAnimTarget
    public boolean isValid() {
        return this.mTargetObj.isValid();
    }

    @Override // miuix.animation.IAnimTarget
    public void setIntValue(IIntValueProperty iIntValueProperty, int i) {
        if (isPredefinedProperty(iIntValueProperty)) {
            this.mTargetObj.setPropertyValue(iIntValueProperty.getName(), Integer.TYPE, Integer.valueOf(i));
        } else {
            iIntValueProperty.setIntValue(this.mTargetObj.getRealObject(), i);
        }
    }

    @Override // miuix.animation.IAnimTarget
    public void setValue(FloatProperty floatProperty, float f) {
        if (isPredefinedProperty(floatProperty)) {
            this.mTargetObj.setPropertyValue(floatProperty.getName(), Float.TYPE, Float.valueOf(f));
        } else {
            floatProperty.setValue(this.mTargetObj.getRealObject(), f);
        }
    }
}
