package miuix.animation.base;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import miuix.animation.listener.TransitionListener;
import miuix.animation.property.FloatProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;

/* loaded from: classes5.dex */
public class AnimConfig {
    public static final EaseManager.EaseStyle sDefEase = EaseManager.getStyle(-2, 0.85f, 0.3f);
    public long delay;
    public EaseManager.EaseStyle ease;
    public long flags;
    public float fromSpeed;
    public final HashSet<TransitionListener> listeners;
    private final Map<String, AnimSpecialConfig> mSpecialNameMap;
    @Deprecated
    public long minDuration;
    public Object tag;
    public int tintMode;

    public AnimConfig() {
        this(false);
    }

    public AnimConfig(AnimConfig animConfig) {
        this(false);
        copy(animConfig);
    }

    public AnimConfig(boolean z) {
        this.fromSpeed = Float.MAX_VALUE;
        this.tintMode = -1;
        if (z) {
            this.mSpecialNameMap = null;
            this.listeners = null;
            return;
        }
        this.mSpecialNameMap = new HashMap();
        this.listeners = new HashSet<>();
    }

    private AnimSpecialConfig queryAndCreateSpecial(String str, boolean z) {
        AnimSpecialConfig animSpecialConfig = this.mSpecialNameMap.get(str);
        if (animSpecialConfig == null && z) {
            AnimSpecialConfig animSpecialConfig2 = new AnimSpecialConfig();
            this.mSpecialNameMap.put(str, animSpecialConfig2);
            return animSpecialConfig2;
        }
        return animSpecialConfig;
    }

    private AnimSpecialConfig queryAndCreateSpecial(FloatProperty floatProperty, boolean z) {
        if (floatProperty == null) {
            return null;
        }
        return queryAndCreateSpecial(floatProperty.getName(), z);
    }

    public AnimConfig addListeners(TransitionListener... transitionListenerArr) {
        Collections.addAll(this.listeners, transitionListenerArr);
        return this;
    }

    public void addSpecialConfigs(AnimConfig animConfig) {
        this.mSpecialNameMap.putAll(animConfig.mSpecialNameMap);
    }

    public void clear() {
        this.delay = 0L;
        this.ease = null;
        this.listeners.clear();
        this.tag = null;
        this.flags = 0L;
        this.fromSpeed = Float.MAX_VALUE;
        this.minDuration = 0L;
        this.tintMode = -1;
        Map<String, AnimSpecialConfig> map = this.mSpecialNameMap;
        if (map != null) {
            map.clear();
        }
    }

    public void copy(AnimConfig animConfig) {
        if (animConfig == null || animConfig == this) {
            return;
        }
        this.delay = animConfig.delay;
        this.ease = animConfig.ease;
        this.listeners.addAll(animConfig.listeners);
        this.tag = animConfig.tag;
        this.flags = animConfig.flags;
        this.fromSpeed = animConfig.fromSpeed;
        this.minDuration = animConfig.minDuration;
        this.tintMode = animConfig.tintMode;
        Map<String, AnimSpecialConfig> map = this.mSpecialNameMap;
        if (map != null) {
            map.clear();
            this.mSpecialNameMap.putAll(animConfig.mSpecialNameMap);
        }
    }

    public AnimSpecialConfig getSpecialConfig(String str) {
        return queryAndCreateSpecial(str, false);
    }

    public AnimSpecialConfig queryAndCreateSpecial(String str) {
        return queryAndCreateSpecial(str, true);
    }

    public AnimConfig removeListeners(TransitionListener... transitionListenerArr) {
        if (transitionListenerArr.length == 0) {
            this.listeners.clear();
        } else {
            this.listeners.removeAll(Arrays.asList(transitionListenerArr));
        }
        return this;
    }

    public AnimConfig setDelay(long j) {
        this.delay = j;
        return this;
    }

    public AnimConfig setEase(int i, float... fArr) {
        this.ease = EaseManager.getStyle(i, fArr);
        return this;
    }

    public AnimConfig setEase(EaseManager.EaseStyle easeStyle) {
        this.ease = easeStyle;
        return this;
    }

    public AnimConfig setFromSpeed(float f) {
        this.fromSpeed = f;
        return this;
    }

    public AnimConfig setMinDuration(long j) {
        this.minDuration = j;
        return this;
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, long j, float... fArr) {
        return setSpecial(floatProperty, (EaseManager.EaseStyle) null, j, fArr);
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, AnimSpecialConfig animSpecialConfig) {
        if (animSpecialConfig != null) {
            this.mSpecialNameMap.put(floatProperty.getName(), animSpecialConfig);
        } else {
            this.mSpecialNameMap.remove(floatProperty.getName());
        }
        return this;
    }

    public AnimConfig setSpecial(FloatProperty floatProperty, EaseManager.EaseStyle easeStyle, long j, float... fArr) {
        setSpecial(queryAndCreateSpecial(floatProperty, true), easeStyle, j, fArr);
        return this;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpecial(AnimSpecialConfig animSpecialConfig, EaseManager.EaseStyle easeStyle, long j, float... fArr) {
        if (easeStyle != null) {
            animSpecialConfig.setEase(easeStyle);
        }
        if (j > 0) {
            animSpecialConfig.setDelay(j);
        }
        if (fArr.length > 0) {
            animSpecialConfig.setFromSpeed(fArr[0]);
        }
    }

    public AnimConfig setTintMode(int i) {
        this.tintMode = i;
        return this;
    }

    public String toString() {
        return "AnimConfig{delay=" + this.delay + ", minDuration=" + this.minDuration + ", ease=" + this.ease + ", fromSpeed=" + this.fromSpeed + ", tintMode=" + this.tintMode + ", tag=" + this.tag + ", flags=" + this.flags + ", listeners=" + this.listeners + ", specialNameMap = " + ((Object) CommonUtils.mapToString(this.mSpecialNameMap, "    ")) + '}';
    }
}
