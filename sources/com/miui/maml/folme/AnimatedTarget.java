package com.miui.maml.folme;

import com.android.settings.recommend.PageIndexManager;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import miuix.animation.IAnimTarget;
import miuix.animation.ITargetCreator;
import miuix.animation.property.FloatProperty;

/* loaded from: classes2.dex */
public class AnimatedTarget extends IAnimTarget<AnimatedScreenElement> {
    public static ITargetCreator<AnimatedScreenElement> sCreator;
    private WeakReference<AnimatedScreenElement> mElementRef;
    public static ConcurrentHashMap<FloatProperty, Integer> sPropertyTypeMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, FloatProperty> sPropertyMap = new ConcurrentHashMap<>();

    static {
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap = sPropertyTypeMap;
        AnimatedProperty animatedProperty = AnimatedProperty.X;
        concurrentHashMap.put(animatedProperty, 0);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap2 = sPropertyTypeMap;
        AnimatedProperty animatedProperty2 = AnimatedProperty.Y;
        concurrentHashMap2.put(animatedProperty2, 1);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap3 = sPropertyTypeMap;
        AnimatedProperty animatedProperty3 = AnimatedProperty.SCALE_X;
        concurrentHashMap3.put(animatedProperty3, 2);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap4 = sPropertyTypeMap;
        AnimatedProperty animatedProperty4 = AnimatedProperty.SCALE_Y;
        concurrentHashMap4.put(animatedProperty4, 3);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap5 = sPropertyTypeMap;
        AnimatedProperty animatedProperty5 = AnimatedProperty.ALPHA;
        concurrentHashMap5.put(animatedProperty5, 4);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap6 = sPropertyTypeMap;
        AnimatedProperty animatedProperty6 = AnimatedProperty.HEIGHT;
        concurrentHashMap6.put(animatedProperty6, 5);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap7 = sPropertyTypeMap;
        AnimatedProperty animatedProperty7 = AnimatedProperty.WIDTH;
        concurrentHashMap7.put(animatedProperty7, 6);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap8 = sPropertyTypeMap;
        AnimatedProperty animatedProperty8 = AnimatedProperty.ROTATION;
        concurrentHashMap8.put(animatedProperty8, 9);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap9 = sPropertyTypeMap;
        AnimatedProperty animatedProperty9 = AnimatedProperty.ROTATION_X;
        concurrentHashMap9.put(animatedProperty9, 10);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap10 = sPropertyTypeMap;
        AnimatedProperty animatedProperty10 = AnimatedProperty.ROTATION_Y;
        concurrentHashMap10.put(animatedProperty10, 11);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap11 = sPropertyTypeMap;
        AnimatedProperty.AnimatedColorProperty animatedColorProperty = AnimatedProperty.TINT_COLOR;
        Integer valueOf = Integer.valueOf((int) PageIndexManager.PAGE_FACTORY_RESET);
        concurrentHashMap11.put(animatedColorProperty, valueOf);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap12 = sPropertyTypeMap;
        AnimatedProperty animatedProperty11 = AnimatedProperty.PIVOT_X;
        Integer valueOf2 = Integer.valueOf((int) PageIndexManager.PAGE_ACCESSIBILITY_VISUAL);
        concurrentHashMap12.put(animatedProperty11, valueOf2);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap13 = sPropertyTypeMap;
        AnimatedProperty animatedProperty12 = AnimatedProperty.PIVOT_Y;
        Integer valueOf3 = Integer.valueOf((int) PageIndexManager.PAGE_ACCESSIBILITY_PHYSICAL);
        concurrentHashMap13.put(animatedProperty12, valueOf3);
        ConcurrentHashMap<FloatProperty, Integer> concurrentHashMap14 = sPropertyTypeMap;
        AnimatedProperty animatedProperty13 = AnimatedProperty.PIVOT_Z;
        Integer valueOf4 = Integer.valueOf((int) PageIndexManager.PAGE_MI_CLOUD);
        concurrentHashMap14.put(animatedProperty13, valueOf4);
        sPropertyMap.put(0, animatedProperty);
        sPropertyMap.put(1, animatedProperty2);
        sPropertyMap.put(2, animatedProperty3);
        sPropertyMap.put(3, animatedProperty4);
        sPropertyMap.put(4, animatedProperty5);
        sPropertyMap.put(5, animatedProperty6);
        sPropertyMap.put(6, animatedProperty7);
        sPropertyMap.put(9, animatedProperty8);
        sPropertyMap.put(10, animatedProperty9);
        sPropertyMap.put(11, animatedProperty10);
        sPropertyMap.put(valueOf, animatedColorProperty);
        sPropertyMap.put(valueOf2, animatedProperty11);
        sPropertyMap.put(valueOf3, animatedProperty12);
        sPropertyMap.put(valueOf4, animatedProperty13);
        sCreator = new ITargetCreator<AnimatedScreenElement>() { // from class: com.miui.maml.folme.AnimatedTarget.1
            @Override // miuix.animation.ITargetCreator
            public IAnimTarget createTarget(AnimatedScreenElement animatedScreenElement) {
                return new AnimatedTarget(animatedScreenElement);
            }
        };
    }

    public AnimatedTarget(AnimatedScreenElement animatedScreenElement) {
        setMinVisibleChange(0.00390625f, new int[]{1003, 1004, 1005, PageIndexManager.PAGE_FACTORY_RESET, 1013, 1101, 1102});
        this.mElementRef = new WeakReference<>(animatedScreenElement);
    }

    @Override // miuix.animation.IAnimTarget
    public void executeOnInitialized(Runnable runnable) {
        if (this.mElementRef.get() != null) {
            runnable.run();
        }
    }

    @Override // miuix.animation.IAnimTarget
    public float getDefaultMinVisible() {
        return 1.0f;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // miuix.animation.IAnimTarget
    public AnimatedScreenElement getTargetObject() {
        return this.mElementRef.get();
    }

    @Override // miuix.animation.IAnimTarget
    public boolean isValid() {
        return this.mElementRef.get() != null;
    }
}
