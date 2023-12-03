package com.miui.maml.elements;

import android.graphics.Canvas;
import androidx.collection.ArraySet;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.ConfigValue;
import com.miui.maml.folme.MamlTransitionListener;
import com.miui.maml.folme.TransitionListenerWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import miuix.animation.base.AnimConfig;
import miuix.animation.property.FloatProperty;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class ConfigElement extends ScreenElement {
    protected ArrayList<AnimConfig> mTempAnimConfigs;
    protected ArrayList<ConfigValue> mTempValueList;

    public ConfigElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mTempAnimConfigs = new ArrayList<>();
        this.mTempValueList = new ArrayList<>();
    }

    private void setupCallbacks(Collection<FunctionElement> collection, ArraySet<String> arraySet) {
        collection.clear();
        Iterator<String> it = arraySet.iterator();
        while (it.hasNext()) {
            ScreenElement findElement = getRoot().findElement(it.next());
            if (findElement instanceof FunctionElement) {
                collection.add((FunctionElement) findElement);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
    }

    protected abstract void evaluateConfigValue();

    public AnimConfig[] getAnimConfig(TransitionListenerWrapper transitionListenerWrapper) {
        MamlTransitionListener listener = transitionListenerWrapper.getListener();
        if (listener == null) {
            return new AnimConfig[0];
        }
        this.mTempAnimConfigs.clear();
        evaluateConfigValue();
        Iterator<ConfigValue> it = this.mTempValueList.iterator();
        while (it.hasNext()) {
            ConfigValue next = it.next();
            AnimConfig animConfig = null;
            if (next.mRelatedProperty != null) {
                ArrayList arrayList = new ArrayList();
                Iterator<String> it2 = next.mRelatedProperty.iterator();
                while (it2.hasNext()) {
                    FloatProperty propertyByName = AnimatedProperty.getPropertyByName(it2.next());
                    if (propertyByName != null) {
                        arrayList.add(propertyByName);
                    }
                }
                animConfig = new AnimConfig((FloatProperty[]) arrayList.toArray(new FloatProperty[arrayList.size()]));
            }
            if (animConfig == null) {
                animConfig = new AnimConfig();
            }
            setupCallbacks(listener.mBeginCallback, next.mOnBeginCallback);
            setupCallbacks(listener.mUpdateCallback, next.mOnUpdateCallback);
            setupCallbacks(listener.mCompleteCallback, next.mOnCompleteCallback);
            if (next.mHasFromSpeed) {
                animConfig.setFromSpeed(next.mFromSpeed);
            }
            animConfig.setEase(next.mEase);
            animConfig.setDelay(next.mDelay);
            animConfig.addListeners(transitionListenerWrapper);
            this.mTempAnimConfigs.add(animConfig);
        }
        ArrayList<AnimConfig> arrayList2 = this.mTempAnimConfigs;
        return (AnimConfig[]) arrayList2.toArray(new AnimConfig[arrayList2.size()]);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public boolean isVisible() {
        return false;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void tick(long j) {
    }
}
