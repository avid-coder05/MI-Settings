package com.miui.maml.elements;

import android.graphics.Canvas;
import android.text.TextUtils;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.HashSet;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VariableArrayElement extends ScreenElement {
    private ArrayList<Item> mArray;
    Object[] mData;
    private int mItemCount;
    private IndexedVariable mItemCountVar;
    Type mType;
    HashSet<VarObserver> mVarObserver;
    private ArrayList<Var> mVars;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Item {
        public Expression mExpression;
        public Object mValue;

        public Item(Variables variables, Element element) {
            if (element != null) {
                this.mExpression = Expression.build(variables, element.getAttribute("expression"));
                String attribute = element.getAttribute("value");
                if (VariableArrayElement.this.mType != Type.DOUBLE || TextUtils.isEmpty(attribute)) {
                    this.mValue = attribute;
                    return;
                }
                try {
                    this.mValue = Double.valueOf(Double.parseDouble(attribute));
                } catch (NumberFormatException unused) {
                }
            }
        }

        public Double evaluate() {
            Expression expression = this.mExpression;
            if (expression != null) {
                if (expression.isNull()) {
                    return null;
                }
                return Double.valueOf(this.mExpression.evaluate());
            }
            Object obj = this.mValue;
            if (obj instanceof Number) {
                return Double.valueOf(((Number) obj).doubleValue());
            }
            return null;
        }

        public String evaluateStr() {
            Expression expression = this.mExpression;
            if (expression != null) {
                return expression.evaluateStr();
            }
            Object obj = this.mValue;
            if (obj instanceof String) {
                return (String) obj;
            }
            return null;
        }

        public boolean isExpression() {
            return this.mExpression != null;
        }
    }

    /* loaded from: classes2.dex */
    public enum Type {
        DOUBLE,
        STRING
    }

    /* loaded from: classes2.dex */
    private class Var {
        private boolean mConst;
        private boolean mCurrentItemIsExpression;
        private int mIndex = -1;
        private Expression mIndexExpression;
        private String mName;
        private IndexedVariable mVar;

        public Var(Variables variables, Element element) {
            if (element != null) {
                this.mName = element.getAttribute("name");
                this.mIndexExpression = Expression.build(variables, element.getAttribute("index"));
                this.mConst = Boolean.parseBoolean(element.getAttribute("const"));
                this.mVar = new IndexedVariable(this.mName, VariableArrayElement.this.getVariables(), VariableArrayElement.this.mType != Type.STRING);
            }
        }

        private void update() {
            Expression expression = this.mIndexExpression;
            if (expression == null) {
                return;
            }
            int evaluate = (int) expression.evaluate();
            if (evaluate < 0 || evaluate >= VariableArrayElement.this.mArray.size()) {
                Type type = VariableArrayElement.this.mType;
                if (type == Type.STRING) {
                    this.mVar.set((Object) null);
                } else if (type == Type.DOUBLE) {
                    this.mVar.set(0.0d);
                }
            } else if (this.mIndex != evaluate || this.mCurrentItemIsExpression) {
                Item item = (Item) VariableArrayElement.this.mArray.get(evaluate);
                if (this.mIndex != evaluate) {
                    this.mIndex = evaluate;
                    this.mCurrentItemIsExpression = item.isExpression();
                }
                Type type2 = VariableArrayElement.this.mType;
                if (type2 == Type.STRING) {
                    this.mVar.set(item.evaluateStr());
                } else if (type2 == Type.DOUBLE) {
                    this.mVar.set(item.evaluate());
                }
            }
        }

        public void init() {
            this.mIndex = -1;
            update();
        }

        public void tick() {
            if (this.mConst) {
                return;
            }
            update();
        }
    }

    /* loaded from: classes2.dex */
    public interface VarObserver {
        void onDataChange(Object[] objArr);
    }

    public VariableArrayElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mArray = new ArrayList<>();
        this.mVars = new ArrayList<>();
        Type type = Type.DOUBLE;
        this.mType = type;
        this.mVarObserver = new HashSet<>();
        if (element != null) {
            if ("string".equalsIgnoreCase(element.getAttribute("type"))) {
                this.mType = Type.STRING;
            } else {
                this.mType = type;
            }
            final Variables variables = getVariables();
            Utils.traverseXmlElementChildren(Utils.getChild(element, "Vars"), "Var", new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.VariableArrayElement.1
                @Override // com.miui.maml.util.Utils.XmlTraverseListener
                public void onChild(Element element2) {
                    VariableArrayElement.this.mVars.add(new Var(variables, element2));
                }
            });
            Utils.traverseXmlElementChildren(Utils.getChild(element, "Items"), "Item", new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.VariableArrayElement.2
                @Override // com.miui.maml.util.Utils.XmlTraverseListener
                public void onChild(Element element2) {
                    VariableArrayElement.this.mArray.add(new Item(variables, element2));
                }
            });
            if (this.mHasName) {
                this.mItemCountVar = new IndexedVariable(this.mName + ".count", variables, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        int size = this.mVars.size();
        for (int i = 0; i < size; i++) {
            this.mVars.get(i).tick();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        int size = this.mVars.size();
        for (int i = 0; i < size; i++) {
            this.mVars.get(i).init();
        }
        int size2 = this.mArray.size();
        this.mItemCount = size2;
        IndexedVariable indexedVariable = this.mItemCountVar;
        if (indexedVariable != null) {
            indexedVariable.set(size2);
        }
        if (this.mData == null) {
            this.mData = new Object[this.mItemCount];
            for (int i2 = 0; i2 < this.mItemCount; i2++) {
                this.mData[i2] = this.mArray.get(i2).mValue;
            }
        }
    }

    public void registerVarObserver(VarObserver varObserver, boolean z) {
        if (varObserver == null) {
            return;
        }
        if (!z) {
            this.mVarObserver.remove(varObserver);
            return;
        }
        this.mVarObserver.add(varObserver);
        varObserver.onDataChange(this.mData);
    }
}
