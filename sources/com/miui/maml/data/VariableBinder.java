package com.miui.maml.data;

import android.text.TextUtils;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.ContentProviderBinder;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Objects;
import miui.provider.ExtraContacts;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class VariableBinder {
    private String mDependency;
    protected boolean mFinished;
    protected String mName;
    protected boolean mPaused;
    protected boolean mQueryAtStart;
    private ContentProviderBinder.QueryCompleteListener mQueryCompletedListener;
    protected ScreenElementRoot mRoot;
    protected CommandTrigger mTrigger;
    protected ArrayList<Variable> mVariables = new ArrayList<>();

    /* loaded from: classes2.dex */
    public static class TypedValue {
        public String mName;
        public int mType;
        public String mTypeStr;

        public TypedValue(Element element) {
            Objects.requireNonNull(element, "node is null");
            initInner(element.getAttribute("name"), element.getAttribute("type"));
        }

        private void initInner(String str, String str2) {
            this.mName = str;
            this.mTypeStr = str2;
            this.mType = parseType(str2);
        }

        public boolean isNumber() {
            int i = this.mType;
            return i >= 3 && i <= 6;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public int parseType(String str) {
            if ("string".equalsIgnoreCase(str)) {
                return 2;
            }
            if ("double".equalsIgnoreCase(str) || "number".equalsIgnoreCase(str)) {
                return 6;
            }
            if ("float".equalsIgnoreCase(str)) {
                return 5;
            }
            if ("int".equalsIgnoreCase(str) || "integer".equalsIgnoreCase(str)) {
                return 3;
            }
            if ("long".equalsIgnoreCase(str)) {
                return 4;
            }
            if ("bitmap".equalsIgnoreCase(str)) {
                return 7;
            }
            if ("number[]".equalsIgnoreCase(str)) {
                return 8;
            }
            return "string[]".equalsIgnoreCase(str) ? 9 : 6;
        }
    }

    /* loaded from: classes2.dex */
    public static class Variable extends TypedValue {
        private Expression mArrayIndex;
        protected double mDefNumberValue;
        protected String mDefStringValue;
        protected IndexedVariable mVar;

        public Variable(Element element, Variables variables) {
            super(element);
            this.mArrayIndex = Expression.build(variables, element.getAttribute("arrIndex"));
            this.mVar = new IndexedVariable(this.mName, variables, isNumber() && this.mArrayIndex == null);
            this.mDefStringValue = element.getAttribute(ExtraContacts.DefaultAccount.NAME);
            if (isNumber()) {
                if (TextUtils.isEmpty(this.mDefStringValue)) {
                    this.mDefStringValue = null;
                    this.mDefNumberValue = 0.0d;
                    return;
                }
                try {
                    this.mDefNumberValue = Double.parseDouble(this.mDefStringValue);
                } catch (NumberFormatException unused) {
                    this.mDefStringValue = null;
                    this.mDefNumberValue = 0.0d;
                }
            }
        }

        public double getNumber() {
            if (isNumber()) {
                Expression expression = this.mArrayIndex;
                return expression != null ? this.mVar.getArrDouble((int) expression.evaluate()) : this.mVar.getDouble();
            }
            Expression expression2 = this.mArrayIndex;
            return expression2 != null ? Utils.stringToDouble(this.mVar.getArrString((int) expression2.evaluate()), 0.0d) : Utils.stringToDouble(this.mVar.getString(), 0.0d);
        }

        public void set(double d) {
            Expression expression = this.mArrayIndex;
            if (expression != null) {
                this.mVar.setArr((int) expression.evaluate(), d);
            } else {
                this.mVar.set(d);
            }
        }

        public void set(Object obj) {
            if (!isNumber()) {
                if (obj instanceof Number) {
                    obj = Utils.numberToString((Number) obj);
                }
                Expression expression = this.mArrayIndex;
                if (expression != null) {
                    this.mVar.setArr((int) expression.evaluate(), obj);
                    return;
                } else {
                    this.mVar.set(obj);
                    return;
                }
            }
            double d = 0.0d;
            if ((obj instanceof String) && !TextUtils.isEmpty((String) obj)) {
                try {
                    d = Utils.parseDouble((String) obj);
                } catch (NumberFormatException unused) {
                }
            } else if (obj instanceof Number) {
                d = ((Number) obj).doubleValue();
            }
            Expression expression2 = this.mArrayIndex;
            if (expression2 != null) {
                this.mVar.setArr((int) expression2.evaluate(), d);
            } else {
                this.mVar.set(d);
            }
        }
    }

    public VariableBinder(Element element, ScreenElementRoot screenElementRoot) {
        this.mQueryAtStart = true;
        this.mRoot = screenElementRoot;
        if (element != null) {
            this.mName = element.getAttribute("name");
            this.mDependency = element.getAttribute("dependency");
            this.mQueryAtStart = !"false".equalsIgnoreCase(element.getAttribute("queryAtStart"));
            this.mTrigger = CommandTrigger.fromParentElement(element, this.mRoot);
        }
    }

    public void finish() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.finish();
        }
        this.mFinished = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ScreenContext getContext() {
        return this.mRoot.getContext();
    }

    public String getDependency() {
        return this.mDependency;
    }

    public String getName() {
        return this.mName;
    }

    public Variables getVariables() {
        return this.mRoot.getVariables();
    }

    public void init() {
        this.mFinished = false;
        this.mPaused = false;
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.init();
        }
        if (TextUtils.isEmpty(getDependency()) && this.mQueryAtStart) {
            startQuery();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void loadVariables(Element element) {
        Utils.traverseXmlElementChildren(element, "Variable", new Utils.XmlTraverseListener() { // from class: com.miui.maml.data.VariableBinder.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                Variable onLoadVariable = VariableBinder.this.onLoadVariable(element2);
                if (onLoadVariable != null) {
                    VariableBinder.this.mVariables.add(onLoadVariable);
                }
            }
        });
    }

    protected Variable onLoadVariable(Element element) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void onUpdateComplete() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.perform();
        }
        if (this.mQueryCompletedListener != null && !TextUtils.isEmpty(this.mName)) {
            this.mQueryCompletedListener.onQueryCompleted(this.mName);
        }
        this.mRoot.requestUpdate();
    }

    public void pause() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.pause();
        }
        this.mPaused = true;
    }

    public void refresh() {
    }

    public void resume() {
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.resume();
        }
        this.mPaused = false;
    }

    public void setQueryCompleteListener(ContentProviderBinder.QueryCompleteListener queryCompleteListener) {
        this.mQueryCompletedListener = queryCompleteListener;
    }

    public void startQuery() {
    }

    public void tick() {
    }
}
