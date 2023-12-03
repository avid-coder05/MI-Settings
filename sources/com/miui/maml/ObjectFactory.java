package com.miui.maml;

import com.miui.maml.elements.BitmapProvider;
import com.miui.maml.elements.ScreenElement;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class ObjectFactory {

    /* loaded from: classes2.dex */
    public static abstract class ActionCommandFactory extends ObjectFactoryBase<ActionCommandFactory> {
        protected ActionCommandFactory() {
            super("ActionCommand");
        }

        public final ActionCommand create(ScreenElement screenElement, Element element) {
            ActionCommand doCreate = doCreate(screenElement, element);
            if (doCreate != null) {
                return doCreate;
            }
            T t = this.mOld;
            if (t == 0) {
                return null;
            }
            return ((ActionCommandFactory) t).create(screenElement, element);
        }

        protected abstract ActionCommand doCreate(ScreenElement screenElement, Element element);
    }

    /* loaded from: classes2.dex */
    public static abstract class BitmapProviderFactory extends ObjectFactoryBase<BitmapProviderFactory> {
        protected BitmapProviderFactory() {
            super("BitmapProvider");
        }

        public final BitmapProvider create(ScreenElementRoot screenElementRoot, String str) {
            BitmapProvider doCreate = doCreate(screenElementRoot, str);
            if (doCreate != null) {
                return doCreate;
            }
            T t = this.mOld;
            if (t == 0) {
                return null;
            }
            return ((BitmapProviderFactory) t).create(screenElementRoot, str);
        }

        protected abstract BitmapProvider doCreate(ScreenElementRoot screenElementRoot, String str);
    }

    /* loaded from: classes2.dex */
    public static abstract class ObjectFactoryBase<T extends ObjectFactory> extends ObjectFactory {
        private String mName;
        protected T mOld;

        protected ObjectFactoryBase(String str) {
            this.mName = str;
        }
    }
}
