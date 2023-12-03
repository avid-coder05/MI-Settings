package com.miui.maml;

import com.miui.maml.util.Utils;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* loaded from: classes2.dex */
public class StylesManager {
    private HashMap<String, Style> mStyles = new HashMap<>();

    /* loaded from: classes2.dex */
    public class Style {
        private Style base;
        private HashMap<String, String> mAttrs = new HashMap<>();
        public String name;

        public Style(Element element) {
            NamedNodeMap attributes = element.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                String nodeName = item.getNodeName();
                String nodeValue = item.getNodeValue();
                if (nodeName.equals("name")) {
                    this.name = nodeValue;
                } else if (nodeName.equals("base")) {
                    this.base = (Style) StylesManager.this.mStyles.get(nodeValue);
                } else {
                    this.mAttrs.put(nodeName, nodeValue);
                }
            }
        }

        public String getAttr(String str) {
            String str2 = this.mAttrs.get(str);
            if (str2 != null) {
                return str2;
            }
            Style style = this.base;
            if (style != null) {
                return style.getAttr(str);
            }
            return null;
        }
    }

    public StylesManager(Element element) {
        Utils.traverseXmlElementChildren(element, "Style", new Utils.XmlTraverseListener() { // from class: com.miui.maml.StylesManager.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                Style style = new Style(element2);
                StylesManager.this.mStyles.put(style.name, style);
            }
        });
    }

    public Style getStyle(String str) {
        return this.mStyles.get(str);
    }
}
