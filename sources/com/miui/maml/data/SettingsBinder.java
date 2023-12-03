package com.miui.maml.data;

import android.content.ContentResolver;
import android.provider.Settings;
import com.android.settings.search.provider.SettingsProvider;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.VariableBinder;
import java.util.Iterator;
import miui.yellowpage.YellowPageStatistic;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class SettingsBinder extends VariableBinder {
    private boolean mConst;
    private ContentResolver mContentResolver;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.SettingsBinder$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$SettingsBinder$Category;

        static {
            int[] iArr = new int[Category.values().length];
            $SwitchMap$com$miui$maml$data$SettingsBinder$Category = iArr;
            try {
                iArr[Category.System.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$SettingsBinder$Category[Category.Secure.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Category {
        Secure,
        System
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Variable extends VariableBinder.Variable {
        public Category mCategory;
        public String mKey;

        public Variable(Element element, Variables variables) {
            super(element, variables);
            this.mCategory = "secure".equals(element.getAttribute(YellowPageStatistic.Display.CATEGORY)) ? Category.Secure : Category.System;
            this.mKey = element.getAttribute(SettingsProvider.ARGS_KEY);
        }

        public void query() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$SettingsBinder$Category[this.mCategory.ordinal()];
            if (i == 1) {
                int i2 = this.mType;
                if (i2 == 2) {
                    String string = Settings.System.getString(SettingsBinder.this.mContentResolver, this.mKey);
                    if (string == null) {
                        string = this.mDefStringValue;
                    }
                    set(string);
                } else if (i2 == 3) {
                    set(Settings.System.getInt(SettingsBinder.this.mContentResolver, this.mKey, (int) this.mDefNumberValue));
                } else if (i2 == 4) {
                    set(Settings.System.getLong(SettingsBinder.this.mContentResolver, this.mKey, (long) this.mDefNumberValue));
                } else if (i2 == 5 || i2 == 6) {
                    set(Settings.System.getFloat(SettingsBinder.this.mContentResolver, this.mKey, (float) this.mDefNumberValue));
                }
            } else if (i != 2) {
            } else {
                int i3 = this.mType;
                if (i3 == 2) {
                    String string2 = Settings.Secure.getString(SettingsBinder.this.mContentResolver, this.mKey);
                    if (string2 == null) {
                        string2 = this.mDefStringValue;
                    }
                    set(string2);
                } else if (i3 == 3) {
                    set(Settings.Secure.getInt(SettingsBinder.this.mContentResolver, this.mKey, (int) this.mDefNumberValue));
                } else if (i3 == 4) {
                    set(Settings.Secure.getLong(SettingsBinder.this.mContentResolver, this.mKey, (long) this.mDefNumberValue));
                } else if (i3 == 5 || i3 == 6) {
                    set(Settings.Secure.getFloat(SettingsBinder.this.mContentResolver, this.mKey, (float) this.mDefNumberValue));
                }
            }
        }
    }

    public SettingsBinder(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mContentResolver = this.mRoot.getContext().mContext.getContentResolver();
        if (element != null) {
            loadVariables(element);
            this.mConst = !"false".equalsIgnoreCase(element.getAttribute("const"));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.data.VariableBinder
    public Variable onLoadVariable(Element element) {
        return new Variable(element, getContext().mVariables);
    }

    @Override // com.miui.maml.data.VariableBinder
    public void refresh() {
        super.refresh();
        startQuery();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void resume() {
        super.resume();
        if (this.mConst) {
            return;
        }
        startQuery();
    }

    @Override // com.miui.maml.data.VariableBinder
    public void startQuery() {
        Iterator<VariableBinder.Variable> it = this.mVariables.iterator();
        while (it.hasNext()) {
            ((Variable) it.next()).query();
        }
        onUpdateComplete();
    }
}
