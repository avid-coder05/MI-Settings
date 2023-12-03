package com.miui.maml.elements;

import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.util.Utils;
import miui.yellowpage.Tag;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ScreenElementArray extends ElementGroup {
    public ScreenElementArray(Element element, final ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        final int attrAsInt = Utils.getAttrAsInt(element, Tag.TagPhone.MARKED_COUNT, 0);
        String attribute = element.getAttribute("indexName");
        final IndexedVariable indexedVariable = new IndexedVariable(TextUtils.isEmpty(attribute) ? "__i" : attribute, getVariables(), true);
        Utils.traverseXmlElementChildren(element, null, new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.ScreenElementArray.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                String attr = ScreenElementArray.this.getAttr(element2, "name");
                boolean startsWith = element2.getTagName().startsWith("Var");
                ElementGroup elementGroup = null;
                for (int i = 0; i < attrAsInt; i++) {
                    if (startsWith) {
                        element2.setAttribute("dontAddToMap", "true");
                    } else {
                        element2.setAttribute("namesSuffix", "[" + i + "]");
                    }
                    ScreenElement onCreateChild = ScreenElementArray.super.onCreateChild(element2);
                    if (onCreateChild != null) {
                        if (elementGroup == null) {
                            elementGroup = ElementGroup.createArrayGroup(screenElementRoot, indexedVariable);
                            elementGroup.setName(attr);
                            ScreenElementArray.this.addElement(elementGroup);
                        }
                        elementGroup.addElement(onCreateChild);
                    }
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup
    public ScreenElement onCreateChild(Element element) {
        return null;
    }
}
