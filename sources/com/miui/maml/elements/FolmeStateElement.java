package com.miui.maml.elements;

import android.util.ArrayMap;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.folme.AnimatedProperty;
import java.util.Collection;
import java.util.Map;
import miuix.animation.controller.AnimState;
import miuix.animation.property.ColorProperty;
import miuix.animation.property.FloatProperty;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* loaded from: classes2.dex */
public class FolmeStateElement extends StateElement {
    private ArrayMap<String, Expression> mAttrs;

    public FolmeStateElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mAttrs = new ArrayMap<>();
        NamedNodeMap attributes = element.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Node item = attributes.item(i);
            Expression build = Expression.build(getVariables(), item.getNodeValue());
            if (build != null) {
                this.mAttrs.put(item.getNodeName(), build);
            }
        }
    }

    @Override // com.miui.maml.elements.StateElement
    public AnimState getAnimState(String str) {
        AnimState animState = new AnimState(str);
        for (Map.Entry<String, Expression> entry : this.mAttrs.entrySet()) {
            FloatProperty propertyByName = AnimatedProperty.getPropertyByName(entry.getKey());
            if (propertyByName != null) {
                if (propertyByName instanceof ColorProperty) {
                    animState.add(propertyByName, (int) entry.getValue().evaluate(), 2);
                } else {
                    animState.add(propertyByName, (float) entry.getValue().evaluate(), 2);
                }
            }
        }
        return animState;
    }

    @Override // com.miui.maml.elements.StateElement
    public Collection<String> getProperties() {
        return this.mAttrs.keySet();
    }
}
