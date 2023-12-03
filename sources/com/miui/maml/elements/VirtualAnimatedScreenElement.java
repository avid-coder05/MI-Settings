package com.miui.maml.elements;

import android.graphics.Canvas;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VirtualAnimatedScreenElement extends AnimatedScreenElement {
    public static final AnimatedProperty.AnimatedColorProperty COLOR_1;
    public static final AnimatedProperty.AnimatedColorProperty COLOR_2;
    public static final AnimatedProperty FLOAT_1;
    public static final AnimatedProperty FLOAT_2;
    public static final AnimatedProperty FLOAT_3;
    public static final AnimatedProperty FLOAT_4;
    private PropertyWrapper mColor1Property;
    private PropertyWrapper mColor2Property;
    private PropertyWrapper mFloat1Property;
    private PropertyWrapper mFloat2Property;
    private PropertyWrapper mFloat3Property;
    private PropertyWrapper mFloat4Property;

    static {
        String str = "color1";
        AnimatedProperty.AnimatedColorProperty animatedColorProperty = new AnimatedProperty.AnimatedColorProperty(str) { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.1
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (int) ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.getValue();
                }
                return 0;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mColor1Property.setVelocity(f);
                }
            }
        };
        COLOR_1 = animatedColorProperty;
        AnimatedProperty.AnimatedColorProperty animatedColorProperty2 = new AnimatedProperty.AnimatedColorProperty(str) { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.2
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (int) ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.getValue();
                }
                return 0;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mColor2Property.setVelocity(f);
                }
            }
        };
        COLOR_2 = animatedColorProperty2;
        AnimatedProperty animatedProperty = new AnimatedProperty("float1") { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.3
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat1Property.setVelocity(f);
                }
            }
        };
        FLOAT_1 = animatedProperty;
        AnimatedProperty animatedProperty2 = new AnimatedProperty("float2") { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.4
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat2Property.setVelocity(f);
                }
            }
        };
        FLOAT_2 = animatedProperty2;
        AnimatedProperty animatedProperty3 = new AnimatedProperty("float3") { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.5
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat3Property.setVelocity(f);
                }
            }
        };
        FLOAT_3 = animatedProperty3;
        AnimatedProperty animatedProperty4 = new AnimatedProperty("float4") { // from class: com.miui.maml.elements.VirtualAnimatedScreenElement.6
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    return (float) ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.getValue();
                }
                return 0.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof VirtualAnimatedScreenElement) {
                    ((VirtualAnimatedScreenElement) animatedScreenElement).mFloat4Property.setVelocity(f);
                }
            }
        };
        FLOAT_4 = animatedProperty4;
        AnimatedProperty.sPropertyNameMap.put("color1", animatedColorProperty);
        AnimatedProperty.sPropertyNameMap.put("color1", animatedColorProperty2);
        AnimatedProperty.sPropertyNameMap.put("float1", animatedProperty);
        AnimatedProperty.sPropertyNameMap.put("float2", animatedProperty2);
        AnimatedProperty.sPropertyNameMap.put("float3", animatedProperty3);
        AnimatedProperty.sPropertyNameMap.put("float4", animatedProperty4);
        AnimatedTarget.sPropertyMap.put(1101, animatedColorProperty);
        AnimatedTarget.sPropertyMap.put(1102, animatedColorProperty2);
        AnimatedTarget.sPropertyMap.put(1103, animatedProperty);
        AnimatedTarget.sPropertyMap.put(1104, animatedProperty2);
        AnimatedTarget.sPropertyMap.put(1105, animatedProperty3);
        AnimatedTarget.sPropertyMap.put(1106, animatedProperty4);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty, 1101);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty2, 1102);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty, 1103);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty2, 1104);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty3, 1105);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty4, 1106);
    }

    public VirtualAnimatedScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mColor1Property = new PropertyWrapper(this.mName + ".color1", getVariables(), null, isInFolmeMode(), 0.0d);
        this.mColor2Property = new PropertyWrapper(this.mName + ".color2", getVariables(), null, isInFolmeMode(), 0.0d);
        this.mFloat1Property = new PropertyWrapper(this.mName + ".float1", getVariables(), null, isInFolmeMode(), 0.0d);
        this.mFloat2Property = new PropertyWrapper(this.mName + ".float2", getVariables(), null, isInFolmeMode(), 0.0d);
        this.mFloat3Property = new PropertyWrapper(this.mName + ".float3", getVariables(), null, isInFolmeMode(), 0.0d);
        this.mFloat4Property = new PropertyWrapper(this.mName + ".float4", getVariables(), null, isInFolmeMode(), 0.0d);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public boolean isInFolmeMode() {
        return this.mHasName;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public boolean isVisible() {
        return false;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void tick(long j) {
    }
}
