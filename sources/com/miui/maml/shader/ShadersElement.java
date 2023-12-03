package com.miui.maml.shader;

import android.graphics.Shader;
import com.miui.maml.ScreenElementRoot;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public final class ShadersElement {
    private ShaderElement mShaderElement;

    public ShadersElement(Element element, ScreenElementRoot screenElementRoot) {
        loadShaderElements(element, screenElementRoot);
    }

    private void loadShaderElements(Element element, ScreenElementRoot screenElementRoot) {
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == 1) {
                Element element2 = (Element) item;
                String tagName = element2.getTagName();
                if (tagName.equalsIgnoreCase("LinearGradient")) {
                    this.mShaderElement = new LinearGradientElement(element2, screenElementRoot);
                } else if (tagName.equalsIgnoreCase("RadialGradient")) {
                    this.mShaderElement = new RadialGradientElement(element2, screenElementRoot);
                } else if (tagName.equalsIgnoreCase("SweepGradient")) {
                    this.mShaderElement = new SweepGradientElement(element2, screenElementRoot);
                } else if (tagName.equalsIgnoreCase("BitmapShader")) {
                    this.mShaderElement = new BitmapShaderElement(element2, screenElementRoot);
                }
                if (this.mShaderElement != null) {
                    return;
                }
            }
        }
    }

    public Shader getShader() {
        ShaderElement shaderElement = this.mShaderElement;
        if (shaderElement != null) {
            return shaderElement.getShader();
        }
        return null;
    }

    public void updateShader() {
        ShaderElement shaderElement = this.mShaderElement;
        if (shaderElement != null) {
            shaderElement.updateShader();
        }
    }
}
