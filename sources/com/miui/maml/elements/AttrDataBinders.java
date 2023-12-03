package com.miui.maml.elements;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.data.ContextVariables;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.provider.Weather;
import miui.yellowpage.YellowPageContract;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class AttrDataBinders {
    private ArrayList<AttrDataBinder> mBinders = new ArrayList<>();
    protected ContextVariables mVars;

    /* loaded from: classes2.dex */
    public static class AttrDataBinder {
        protected String mAttr;
        private Binder mBinder;
        protected String mData;
        protected String mTarget;
        protected ContextVariables mVars;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public abstract class Binder {
            private Binder() {
            }

            public abstract void bind(ScreenElement screenElement);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class BitmapBinder extends Binder {
            private BitmapBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                ((ImageScreenElement) screenElement).setBitmap(attrDataBinder.mVars.getBmp(attrDataBinder.mData));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class NameBinder extends Binder {
            private NameBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                screenElement.setName(attrDataBinder.mVars.getString(attrDataBinder.mData));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class ParamsBinder extends Binder {
            private ParamsBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                ((TextScreenElement) screenElement).setParams(attrDataBinder.mVars.getVar(attrDataBinder.mData));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class SrcBinder extends Binder {
            private SrcBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                ((ImageScreenElement) screenElement).setSrc(attrDataBinder.mVars.getString(attrDataBinder.mData));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class SrcIdBinder extends Binder {
            private SrcIdBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                Double d = attrDataBinder.mVars.getDouble(attrDataBinder.mData);
                ((ImageScreenElement) screenElement).setSrcId(d == null ? 0.0d : d.doubleValue());
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public class TextBinder extends Binder {
            private TextBinder() {
                super();
            }

            @Override // com.miui.maml.elements.AttrDataBinders.AttrDataBinder.Binder
            public void bind(ScreenElement screenElement) {
                AttrDataBinder attrDataBinder = AttrDataBinder.this;
                ((TextScreenElement) screenElement).setText(attrDataBinder.mVars.getString(attrDataBinder.mData));
            }
        }

        public AttrDataBinder(Element element, ContextVariables contextVariables) {
            this.mTarget = element.getAttribute("target");
            this.mAttr = element.getAttribute("attr");
            this.mData = element.getAttribute("data");
            this.mVars = contextVariables;
            this.mBinder = createBinder(this.mAttr);
            if (TextUtils.isEmpty(this.mTarget) || TextUtils.isEmpty(this.mAttr) || TextUtils.isEmpty(this.mData) || this.mBinder == null) {
                throw new IllegalArgumentException("invalid AttrDataBinder");
            }
        }

        private Binder createBinder(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            if ("text".equals(str)) {
                return new TextBinder();
            }
            if ("paras".equals(str) || YellowPageContract.HttpRequest.PARAMS.equals(str)) {
                return new ParamsBinder();
            }
            if ("name".equals(str)) {
                return new NameBinder();
            }
            if ("bitmap".equals(str)) {
                return new BitmapBinder();
            }
            if (Weather.AQIInfo.SRC.equals(str)) {
                return new SrcBinder();
            }
            if ("srcid".equals(str)) {
                return new SrcIdBinder();
            }
            return null;
        }

        public boolean bind(ElementGroup elementGroup) {
            try {
                ScreenElement findElement = elementGroup.findElement(this.mTarget);
                if (findElement != null) {
                    this.mBinder.bind(findElement);
                    return true;
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public AttrDataBinders(Element element, ContextVariables contextVariables) {
        this.mVars = contextVariables;
        Utils.traverseXmlElementChildren(element, "AttrDataBinder", new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.AttrDataBinders.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                try {
                    AttrDataBinders.this.mBinders.add(new AttrDataBinder(element2, AttrDataBinders.this.mVars));
                } catch (IllegalArgumentException e) {
                    Log.e("AttrDataBinders", e.toString());
                }
            }
        });
    }

    public void bind(ElementGroup elementGroup) {
        Iterator<AttrDataBinder> it = this.mBinders.iterator();
        while (it.hasNext()) {
            it.next().bind(elementGroup);
        }
    }
}
