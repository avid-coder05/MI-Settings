package com.miui.maml.data;

import com.miui.maml.data.Expression;
import java.util.IllegalFormatException;

/* loaded from: classes2.dex */
public class FormatFunctions extends Expression.FunctionImpl {
    private final Fun mFun;

    /* renamed from: com.miui.maml.data.FormatFunctions$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$FormatFunctions$Fun;

        static {
            int[] iArr = new int[Fun.values().length];
            $SwitchMap$com$miui$maml$data$FormatFunctions$Fun = iArr;
            try {
                iArr[Fun.FORMAT_DATE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$FormatFunctions$Fun[Fun.FORMAT_FLOAT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$FormatFunctions$Fun[Fun.FORMAT_INT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Fun {
        INVALID,
        FORMAT_DATE,
        FORMAT_FLOAT,
        FORMAT_INT
    }

    private FormatFunctions(Fun fun, int i) {
        super(i);
        this.mFun = fun;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("formatDate", new FormatFunctions(Fun.FORMAT_DATE, 2));
        Expression.FunctionExpression.registerFunction("formatFloat", new FormatFunctions(Fun.FORMAT_FLOAT, 2));
        Expression.FunctionExpression.registerFunction("formatInt", new FormatFunctions(Fun.FORMAT_INT, 2));
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public double evaluate(Expression[] expressionArr, Variables variables) {
        return 0.0d;
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        String evaluateStr = expressionArr[0].evaluateStr();
        if (evaluateStr == null) {
            return null;
        }
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$FormatFunctions$Fun[this.mFun.ordinal()];
        if (i != 1) {
            try {
            } catch (IllegalFormatException unused) {
                return null;
            }
            if (i != 2) {
                if (i == 3) {
                    return String.format(evaluateStr, Integer.valueOf((int) expressionArr[1].evaluate()));
                }
                return null;
            }
            return String.format(evaluateStr, Double.valueOf(expressionArr[1].evaluate()));
        }
        return DateTimeVariableUpdater.formatDate(evaluateStr, (long) expressionArr[1].evaluate());
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public void reset() {
    }
}
