package com.miui.maml.elements;

import android.graphics.Canvas;
import android.util.Log;
import com.miui.maml.CommandTrigger;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.VariableAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.VariableType;
import com.miui.maml.data.Variables;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VariableElement extends ScreenElement {
    private VariableAnimation mAnimation;
    private int mArraySize;
    private Expression[] mArrayValues;
    private boolean mConst;
    private Expression mExpression;
    private Expression mIndexExpression;
    private boolean mInited;
    private IndexedVariable mLengthVar;
    private double mOldValue;
    private IndexedVariable mOldVar;
    private double mThreshold;
    private CommandTrigger mTrigger;
    private VariableType mType;
    private IndexedVariable mVar;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.VariableElement$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$VariableType;

        static {
            int[] iArr = new int[VariableType.values().length];
            $SwitchMap$com$miui$maml$data$VariableType = iArr;
            try {
                iArr[VariableType.STR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$VariableType[VariableType.STR_ARR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$VariableType[VariableType.NUM.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    public VariableElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        if (element != null) {
            this.mExpression = Expression.build(getVariables(), element.getAttribute("expression"));
            this.mIndexExpression = Expression.build(getVariables(), element.getAttribute("index"));
            this.mThreshold = Math.abs(Utils.getAttrAsFloat(element, "threshold", 1.0f));
            this.mType = VariableType.parseType(element.getAttribute("type"));
            this.mConst = Boolean.parseBoolean(element.getAttribute("const"));
            this.mArraySize = Utils.getAttrAsInt(element, "size", 0);
            Variables variables = getVariables();
            this.mVar = new IndexedVariable(this.mName, variables, this.mType.isNumber());
            this.mOldVar = new IndexedVariable(this.mName + ".old_value", variables, this.mType.isNumber());
            this.mTrigger = CommandTrigger.fromParentElement(element, screenElementRoot);
            if (this.mType.isArray()) {
                Expression[] buildMultiple = Expression.buildMultiple(variables, element.getAttribute("values"));
                this.mArrayValues = buildMultiple;
                if (buildMultiple != null) {
                    this.mArraySize = buildMultiple.length;
                }
                int i = this.mArraySize;
                if (i <= 0) {
                    Log.e("VariableElement", "array size is 0:" + this.mName);
                } else if (!variables.createArray(this.mName, i, this.mType.mTypeClass)) {
                    Log.e("VariableElement", "fail to create array:" + this.mName);
                    this.mArraySize = 0;
                }
                IndexedVariable indexedVariable = new IndexedVariable(this.mName + ".length", variables, true);
                this.mLengthVar = indexedVariable;
                indexedVariable.set((double) this.mArraySize);
            }
        }
    }

    private double getDouble(boolean z, int i) {
        VariableAnimation variableAnimation = this.mAnimation;
        if (variableAnimation != null) {
            return variableAnimation.getValue();
        }
        Expression expression = this.mExpression;
        if (expression != null) {
            return expression.evaluate();
        }
        IndexedVariable indexedVariable = this.mVar;
        return z ? indexedVariable.getArrDouble(i) : indexedVariable.getDouble();
    }

    private void onValueChange(double d) {
        if (!this.mInited) {
            this.mOldValue = d;
        }
        if (this.mTrigger == null || Math.abs(d - this.mOldValue) < this.mThreshold) {
            return;
        }
        this.mOldVar.set(this.mOldValue);
        this.mOldValue = d;
        this.mTrigger.perform();
    }

    private void update() {
        Expression expression;
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$VariableType[this.mType.ordinal()];
        if (i == 1) {
            Expression expression2 = this.mExpression;
            if (expression2 != null) {
                String evaluateStr = expression2.evaluateStr();
                String string = this.mVar.getString();
                if (Utils.equals(evaluateStr, string)) {
                    return;
                }
                this.mOldVar.set(string);
                this.mVar.set(evaluateStr);
                CommandTrigger commandTrigger = this.mTrigger;
                if (commandTrigger != null) {
                    commandTrigger.perform();
                    return;
                }
                return;
            }
            return;
        }
        int i2 = 0;
        if (i != 2) {
            if (i == 3) {
                double d = getDouble(false, 0);
                this.mVar.set(d);
                onValueChange(d);
            } else if (this.mType.isNumberArray()) {
                Expression expression3 = this.mIndexExpression;
                if (expression3 != null) {
                    int evaluate = (int) expression3.evaluate();
                    double d2 = getDouble(true, evaluate);
                    this.mVar.setArr(evaluate, d2);
                    onValueChange(d2);
                    return;
                }
                Expression[] expressionArr = this.mArrayValues;
                if (expressionArr != null) {
                    int length = expressionArr.length;
                    Object obj = this.mVar.get();
                    while (i2 < length) {
                        Expression expression4 = this.mArrayValues[i2];
                        Variables.putValueToArr(obj, i2, expression4 == null ? 0.0d : expression4.evaluate());
                        i2++;
                    }
                    this.mVar.set(obj);
                }
            }
        } else if (this.mIndexExpression != null && (expression = this.mExpression) != null) {
            String evaluateStr2 = expression.evaluateStr();
            int evaluate2 = (int) this.mIndexExpression.evaluate();
            String arrString = this.mVar.getArrString(evaluate2);
            if (Utils.equals(evaluateStr2, arrString)) {
                return;
            }
            this.mOldVar.set(arrString);
            this.mVar.setArr(evaluate2, evaluateStr2);
            CommandTrigger commandTrigger2 = this.mTrigger;
            if (commandTrigger2 != null) {
                commandTrigger2.perform();
            }
        } else if (this.mArrayValues != null) {
            Object obj2 = this.mVar.get();
            if (obj2 instanceof String[]) {
                String[] strArr = (String[]) obj2;
                int min = Math.min(this.mArrayValues.length, strArr.length);
                while (i2 < min) {
                    Expression expression5 = this.mArrayValues[i2];
                    strArr[i2] = expression5 == null ? null : expression5.evaluateStr();
                    i2++;
                }
                this.mVar.set(strArr);
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
        if (this.mConst) {
            return;
        }
        super.doTick(j);
        update();
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.finish();
        }
        this.mInited = false;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.init();
        }
        update();
        this.mInited = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if ("VariableAnimation".equals(str)) {
            VariableAnimation variableAnimation = new VariableAnimation(element, this);
            this.mAnimation = variableAnimation;
            return variableAnimation;
        }
        return super.onCreateAnimation(str, element);
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void onSetAnimBefore() {
        this.mAnimation = null;
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof VariableAnimation) {
            this.mAnimation = (VariableAnimation) baseAnimation;
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.pause();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        update();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        update();
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        update();
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        CommandTrigger commandTrigger = this.mTrigger;
        if (commandTrigger != null) {
            commandTrigger.resume();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        update();
    }
}
