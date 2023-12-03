package com.miui.maml.folme;

import com.miui.maml.elements.AnimatedScreenElement;
import java.util.concurrent.ConcurrentHashMap;
import miui.provider.MiCloudSmsCmd;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;

/* loaded from: classes2.dex */
public abstract class AnimatedProperty extends FloatProperty<AnimatedScreenElement> implements IAnimatedProperty<AnimatedScreenElement> {
    public static final AnimatedProperty ALPHA;
    public static final AnimatedProperty HEIGHT;
    public static final AnimatedProperty PIVOT_X;
    public static final AnimatedProperty PIVOT_Y;
    public static final AnimatedProperty PIVOT_Z;
    public static final AnimatedProperty ROTATION;
    public static final AnimatedProperty ROTATION_X;
    public static final AnimatedProperty ROTATION_Y;
    public static final AnimatedProperty ROTATION_Z;
    public static final AnimatedProperty SCALE_X;
    public static final AnimatedProperty SCALE_Y;
    public static final AnimatedColorProperty TINT_COLOR;
    public static final AnimatedProperty WIDTH;
    public static final AnimatedProperty X;
    public static final AnimatedProperty Y;
    public static ConcurrentHashMap<String, FloatProperty> sPropertyNameMap = new ConcurrentHashMap<>();

    /* loaded from: classes2.dex */
    public static abstract class AnimatedColorProperty extends ColorProperty<AnimatedScreenElement> implements IAnimatedProperty<AnimatedScreenElement> {
        public AnimatedColorProperty(String str) {
            super(str);
        }
    }

    static {
        AnimatedProperty animatedProperty = new AnimatedProperty("x") { // from class: com.miui.maml.folme.AnimatedProperty.1
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mXProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mXProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mXProperty.setVelocity(f);
            }
        };
        X = animatedProperty;
        AnimatedProperty animatedProperty2 = new AnimatedProperty("y") { // from class: com.miui.maml.folme.AnimatedProperty.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mYProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mYProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mYProperty.setVelocity(f);
            }
        };
        Y = animatedProperty2;
        AnimatedProperty animatedProperty3 = new AnimatedProperty("scaleX") { // from class: com.miui.maml.folme.AnimatedProperty.3
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mScaleXProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mScaleXProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mScaleXProperty.setVelocity(f);
            }
        };
        SCALE_X = animatedProperty3;
        AnimatedProperty animatedProperty4 = new AnimatedProperty("scaleY") { // from class: com.miui.maml.folme.AnimatedProperty.4
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mScaleYProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mScaleYProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mScaleYProperty.setVelocity(f);
            }
        };
        SCALE_Y = animatedProperty4;
        AnimatedProperty animatedProperty5 = new AnimatedProperty("rotation") { // from class: com.miui.maml.folme.AnimatedProperty.5
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mRotationProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationProperty.setVelocity(f);
            }
        };
        ROTATION = animatedProperty5;
        AnimatedProperty animatedProperty6 = new AnimatedProperty("rotationX") { // from class: com.miui.maml.folme.AnimatedProperty.6
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mRotationXProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationXProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationXProperty.setVelocity(f);
            }
        };
        ROTATION_X = animatedProperty6;
        AnimatedProperty animatedProperty7 = new AnimatedProperty("rotationY") { // from class: com.miui.maml.folme.AnimatedProperty.7
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mRotationYProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationYProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationYProperty.setVelocity(f);
            }
        };
        ROTATION_Y = animatedProperty7;
        AnimatedProperty animatedProperty8 = new AnimatedProperty("rotationZ") { // from class: com.miui.maml.folme.AnimatedProperty.8
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mRotationZProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationZProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mRotationZProperty.setVelocity(f);
            }
        };
        ROTATION_Z = animatedProperty8;
        AnimatedProperty animatedProperty9 = new AnimatedProperty("h") { // from class: com.miui.maml.folme.AnimatedProperty.9
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mHeightProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mHeightProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mHeightProperty.setVelocity(f);
            }
        };
        HEIGHT = animatedProperty9;
        AnimatedProperty animatedProperty10 = new AnimatedProperty(MiCloudSmsCmd.TYPE_WIPE) { // from class: com.miui.maml.folme.AnimatedProperty.10
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mWidthProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mWidthProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mHeightProperty.setVelocity(f);
            }
        };
        WIDTH = animatedProperty10;
        AnimatedProperty animatedProperty11 = new AnimatedProperty("alpha") { // from class: com.miui.maml.folme.AnimatedProperty.11
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mAlphaProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mAlphaProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mAlphaProperty.setVelocity(f);
            }
        };
        ALPHA = animatedProperty11;
        AnimatedColorProperty animatedColorProperty = new AnimatedColorProperty("tintColor") { // from class: com.miui.maml.folme.AnimatedProperty.12
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                return (int) animatedScreenElement.mTintColorProperty.getValue();
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                animatedScreenElement.mTintColorProperty.setValue(i);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mTintColorProperty.setVelocity(f);
            }
        };
        TINT_COLOR = animatedColorProperty;
        AnimatedProperty animatedProperty12 = new AnimatedProperty("pivotX") { // from class: com.miui.maml.folme.AnimatedProperty.13
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mPivotXProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotXProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotXProperty.setVelocity(f);
            }
        };
        PIVOT_X = animatedProperty12;
        AnimatedProperty animatedProperty13 = new AnimatedProperty("pivotY") { // from class: com.miui.maml.folme.AnimatedProperty.14
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mPivotYProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotYProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotYProperty.setVelocity(f);
            }
        };
        PIVOT_Y = animatedProperty13;
        AnimatedProperty animatedProperty14 = new AnimatedProperty("pivotZ") { // from class: com.miui.maml.folme.AnimatedProperty.15
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                return (float) animatedScreenElement.mPivotZProperty.getValue();
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotZProperty.setValue(f);
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                animatedScreenElement.mPivotZProperty.setVelocity(f);
            }
        };
        PIVOT_Z = animatedProperty14;
        sPropertyNameMap.put("x", animatedProperty);
        sPropertyNameMap.put("y", animatedProperty2);
        sPropertyNameMap.put("scaleX", animatedProperty3);
        sPropertyNameMap.put("scaleY", animatedProperty4);
        sPropertyNameMap.put("alpha", animatedProperty11);
        sPropertyNameMap.put("h", animatedProperty9);
        sPropertyNameMap.put(MiCloudSmsCmd.TYPE_WIPE, animatedProperty10);
        sPropertyNameMap.put("rotation", animatedProperty5);
        sPropertyNameMap.put("rotationX", animatedProperty6);
        sPropertyNameMap.put("rotationY", animatedProperty7);
        sPropertyNameMap.put("rotationZ", animatedProperty8);
        sPropertyNameMap.put("tintColor", animatedColorProperty);
        sPropertyNameMap.put("pivotX", animatedProperty12);
        sPropertyNameMap.put("pivotY", animatedProperty13);
        sPropertyNameMap.put("pivotZ", animatedProperty14);
    }

    public AnimatedProperty(String str) {
        super(str);
    }

    public static FloatProperty getPropertyByName(String str) {
        return sPropertyNameMap.get(str);
    }
}
