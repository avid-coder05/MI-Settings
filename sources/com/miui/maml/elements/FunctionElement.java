package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.ActionCommand;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class FunctionElement extends ScreenElement {
    private boolean enable;
    private ArrayList<ActionCommand> mCommands;

    public FunctionElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mCommands = new ArrayList<>();
        this.enable = true;
        load(element);
    }

    private void load(Element element) {
        if (element == null) {
            return;
        }
        Utils.traverseXmlElementChildren(element, null, new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.FunctionElement.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                ActionCommand create = ActionCommand.create(element2, FunctionElement.this);
                if (create != null) {
                    FunctionElement.this.mCommands.add(create);
                }
            }
        });
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void doRender(Canvas canvas) {
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().finish();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().init();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().pause();
        }
    }

    public void perform() {
        if (this.enable) {
            try {
                Iterator<ActionCommand> it = this.mCommands.iterator();
                while (it.hasNext()) {
                    it.next().perform();
                }
            } catch (StackOverflowError unused) {
                this.enable = false;
                Log.e("Function", "Function " + this.mName + " was disabled");
            }
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        Iterator<ActionCommand> it = this.mCommands.iterator();
        while (it.hasNext()) {
            it.next().resume();
        }
    }
}
