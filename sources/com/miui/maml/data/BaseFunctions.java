package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.math.BigDecimal;
import miui.util.HashUtils;

/* loaded from: classes2.dex */
public class BaseFunctions extends Expression.FunctionImpl {
    private Fun fun;
    private Expression mEvalExp;
    private String mEvalExpStr;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.BaseFunctions$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$BaseFunctions$Fun;

        static {
            int[] iArr = new int[Fun.values().length];
            $SwitchMap$com$miui$maml$data$BaseFunctions$Fun = iArr;
            try {
                iArr[Fun.RAND.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.SIN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.COS.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.TAN.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ASIN.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ACOS.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ATAN.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.SINH.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.COSH.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.SQRT.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ABS.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.LEN.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.EVAL.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.PRECISE_EVAL.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ROUND.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.INT.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.NUM.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.ISNULL.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.NOT.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.MIN.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.MAX.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.POW.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.LOG.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.LOG10.ordinal()] = 24;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.DIGIT.ordinal()] = 25;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.EQ.ordinal()] = 26;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.NE.ordinal()] = 27;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.GE.ordinal()] = 28;
            } catch (NoSuchFieldError unused28) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.GT.ordinal()] = 29;
            } catch (NoSuchFieldError unused29) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.LE.ordinal()] = 30;
            } catch (NoSuchFieldError unused30) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.LT.ordinal()] = 31;
            } catch (NoSuchFieldError unused31) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.IFELSE.ordinal()] = 32;
            } catch (NoSuchFieldError unused32) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.AND.ordinal()] = 33;
            } catch (NoSuchFieldError unused33) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.OR.ordinal()] = 34;
            } catch (NoSuchFieldError unused34) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.EQS.ordinal()] = 35;
            } catch (NoSuchFieldError unused35) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.SUBSTR.ordinal()] = 36;
            } catch (NoSuchFieldError unused36) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.FLOOR.ordinal()] = 37;
            } catch (NoSuchFieldError unused37) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.CEIL.ordinal()] = 38;
            } catch (NoSuchFieldError unused38) {
            }
            try {
                $SwitchMap$com$miui$maml$data$BaseFunctions$Fun[Fun.HASH.ordinal()] = 39;
            } catch (NoSuchFieldError unused39) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Fun {
        INVALID,
        RAND,
        SIN,
        COS,
        TAN,
        ASIN,
        ACOS,
        ATAN,
        SINH,
        COSH,
        SQRT,
        ABS,
        LEN,
        EVAL,
        PRECISE_EVAL,
        ROUND,
        INT,
        NUM,
        MIN,
        MAX,
        POW,
        LOG,
        LOG10,
        DIGIT,
        EQ,
        NE,
        GE,
        GT,
        LE,
        LT,
        ISNULL,
        NOT,
        IFELSE,
        AND,
        OR,
        EQS,
        SUBSTR,
        HASH,
        FLOOR,
        CEIL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class NullObjFunction extends Expression.FunctionImpl {
        private String mObjName;
        private int mVarIndex;

        public NullObjFunction() {
            super(1);
            this.mVarIndex = -1;
        }

        @Override // com.miui.maml.data.Expression.FunctionImpl
        public double evaluate(Expression[] expressionArr, Variables variables) {
            String evaluateStr = expressionArr[0].evaluateStr();
            if (evaluateStr != this.mObjName) {
                this.mObjName = evaluateStr;
                if (TextUtils.isEmpty(evaluateStr) || !variables.existsObj(this.mObjName)) {
                    this.mVarIndex = -1;
                } else {
                    this.mVarIndex = variables.registerVariable(this.mObjName);
                }
            }
            int i = this.mVarIndex;
            return (i == -1 || variables.get(i) == null) ? 1.0d : 0.0d;
        }

        @Override // com.miui.maml.data.Expression.FunctionImpl
        public String evaluateStr(Expression[] expressionArr, Variables variables) {
            return null;
        }

        @Override // com.miui.maml.data.Expression.FunctionImpl
        public void reset() {
            this.mObjName = null;
        }
    }

    private BaseFunctions(Fun fun, int i) {
        super(i);
        this.fun = fun;
    }

    private int digit(int i, int i2) {
        if (i2 <= 0) {
            return -1;
        }
        if (i == 0 && i2 == 1) {
            return 0;
        }
        for (int i3 = 0; i > 0 && i3 < i2 - 1; i3++) {
            i /= 10;
        }
        if (i > 0) {
            return i % 10;
        }
        return -1;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("rand", new BaseFunctions(Fun.RAND, 0));
        Expression.FunctionExpression.registerFunction("sin", new BaseFunctions(Fun.SIN, 1));
        Expression.FunctionExpression.registerFunction("cos", new BaseFunctions(Fun.COS, 1));
        Expression.FunctionExpression.registerFunction("tan", new BaseFunctions(Fun.TAN, 1));
        Expression.FunctionExpression.registerFunction("asin", new BaseFunctions(Fun.ASIN, 1));
        Expression.FunctionExpression.registerFunction("acos", new BaseFunctions(Fun.ACOS, 1));
        Expression.FunctionExpression.registerFunction("atan", new BaseFunctions(Fun.ATAN, 1));
        Expression.FunctionExpression.registerFunction("sinh", new BaseFunctions(Fun.SINH, 1));
        Expression.FunctionExpression.registerFunction("cosh", new BaseFunctions(Fun.COSH, 1));
        Expression.FunctionExpression.registerFunction("sqrt", new BaseFunctions(Fun.SQRT, 1));
        Expression.FunctionExpression.registerFunction("abs", new BaseFunctions(Fun.ABS, 1));
        Expression.FunctionExpression.registerFunction("len", new BaseFunctions(Fun.LEN, 1));
        Expression.FunctionExpression.registerFunction("eval", new BaseFunctions(Fun.EVAL, 1));
        Expression.FunctionExpression.registerFunction("preciseeval", new BaseFunctions(Fun.PRECISE_EVAL, 2));
        Expression.FunctionExpression.registerFunction("round", new BaseFunctions(Fun.ROUND, 1));
        Expression.FunctionExpression.registerFunction("int", new BaseFunctions(Fun.INT, 1));
        Expression.FunctionExpression.registerFunction("num", new BaseFunctions(Fun.NUM, 1));
        Expression.FunctionExpression.registerFunction("isnull", new BaseFunctions(Fun.ISNULL, 1));
        Expression.FunctionExpression.registerFunction("not", new BaseFunctions(Fun.NOT, 1));
        Expression.FunctionExpression.registerFunction("min", new BaseFunctions(Fun.MIN, 2));
        Expression.FunctionExpression.registerFunction("max", new BaseFunctions(Fun.MAX, 2));
        Expression.FunctionExpression.registerFunction("pow", new BaseFunctions(Fun.POW, 2));
        Expression.FunctionExpression.registerFunction("log", new BaseFunctions(Fun.LOG, 1));
        Expression.FunctionExpression.registerFunction("log10", new BaseFunctions(Fun.LOG10, 1));
        Expression.FunctionExpression.registerFunction("digit", new BaseFunctions(Fun.DIGIT, 2));
        Expression.FunctionExpression.registerFunction("eq", new BaseFunctions(Fun.EQ, 2));
        Expression.FunctionExpression.registerFunction("ne", new BaseFunctions(Fun.NE, 2));
        Expression.FunctionExpression.registerFunction("ge", new BaseFunctions(Fun.GE, 2));
        Expression.FunctionExpression.registerFunction("gt", new BaseFunctions(Fun.GT, 2));
        Expression.FunctionExpression.registerFunction("le", new BaseFunctions(Fun.LE, 2));
        Expression.FunctionExpression.registerFunction("lt", new BaseFunctions(Fun.LT, 2));
        Expression.FunctionExpression.registerFunction("ifelse", new BaseFunctions(Fun.IFELSE, 3));
        Expression.FunctionExpression.registerFunction("and", new BaseFunctions(Fun.AND, 2));
        Expression.FunctionExpression.registerFunction("or", new BaseFunctions(Fun.OR, 2));
        Expression.FunctionExpression.registerFunction("eqs", new BaseFunctions(Fun.EQS, 2));
        Expression.FunctionExpression.registerFunction("substr", new BaseFunctions(Fun.SUBSTR, 2));
        Expression.FunctionExpression.registerFunction("hash", new BaseFunctions(Fun.HASH, 2));
        Expression.FunctionExpression.registerFunction("nullobj", new NullObjFunction());
        Expression.FunctionExpression.registerFunction("floor", new BaseFunctions(Fun.FLOOR, 1));
        Expression.FunctionExpression.registerFunction("ceil", new BaseFunctions(Fun.CEIL, 1));
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public double evaluate(Expression[] expressionArr, Variables variables) {
        int[] iArr = AnonymousClass1.$SwitchMap$com$miui$maml$data$BaseFunctions$Fun;
        if (iArr[this.fun.ordinal()] == 1) {
            return Math.random();
        }
        int i = 0;
        double evaluate = expressionArr[0].evaluate();
        switch (iArr[this.fun.ordinal()]) {
            case 2:
                return Math.sin(evaluate);
            case 3:
                return Math.cos(evaluate);
            case 4:
                return Math.tan(evaluate);
            case 5:
                return Math.asin(evaluate);
            case 6:
                return Math.acos(evaluate);
            case 7:
                return Math.atan(evaluate);
            case 8:
                return Math.sinh(evaluate);
            case 9:
                return Math.cosh(evaluate);
            case 10:
                return Math.sqrt(evaluate);
            case 11:
                return Math.abs(evaluate);
            case 12:
                if (expressionArr[0].evaluateStr() == null) {
                    return 0.0d;
                }
                return r10.length();
            case 13:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (evaluateStr == null) {
                    return 0.0d;
                }
                if (!evaluateStr.equals(this.mEvalExpStr)) {
                    this.mEvalExpStr = evaluateStr;
                    this.mEvalExp = Expression.build(variables, evaluateStr);
                }
                Expression expression = this.mEvalExp;
                if (expression == null) {
                    return 0.0d;
                }
                return expression.evaluate();
            case 14:
                String evaluateStr2 = expressionArr[0].evaluateStr();
                if (evaluateStr2 == null) {
                    return 0.0d;
                }
                if (!evaluateStr2.equals(this.mEvalExpStr)) {
                    this.mEvalExpStr = evaluateStr2;
                    this.mEvalExp = Expression.build(variables, evaluateStr2);
                }
                Expression expression2 = this.mEvalExp;
                BigDecimal preciseEvaluate = expression2 != null ? expression2.preciseEvaluate() : null;
                if (preciseEvaluate != null) {
                    int scale = preciseEvaluate.scale();
                    int evaluate2 = (int) expressionArr[1].evaluate();
                    if (evaluate2 > 0 && scale > evaluate2) {
                        preciseEvaluate = preciseEvaluate.setScale(evaluate2, 4);
                    }
                    return preciseEvaluate.doubleValue();
                }
                return Double.NaN;
            case 15:
                return Math.round(evaluate);
            case 16:
                return (int) evaluate;
            case 17:
                return evaluate;
            case 18:
                return expressionArr[0].isNull() ? 1.0d : 0.0d;
            case 19:
                return evaluate > 0.0d ? 0.0d : 1.0d;
            case 20:
                return Math.min(evaluate, expressionArr[1].evaluate());
            case 21:
                return Math.max(evaluate, expressionArr[1].evaluate());
            case 22:
                return Math.pow(evaluate, expressionArr[1].evaluate());
            case 23:
                return Math.log(evaluate);
            case 24:
                return Math.log10(evaluate);
            case 25:
                return digit((int) evaluate, (int) expressionArr[1].evaluate());
            case 26:
                return evaluate == expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 27:
                return evaluate != expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 28:
                return evaluate >= expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 29:
                return evaluate > expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 30:
                return evaluate <= expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 31:
                return evaluate < expressionArr[1].evaluate() ? 1.0d : 0.0d;
            case 32:
                int length = expressionArr.length;
                if (length % 2 != 1) {
                    Log.e("Expression", "function parameter number should be 2*n+1: " + this.fun.toString());
                    return 0.0d;
                }
                while (true) {
                    int i2 = length - 1;
                    if (i >= i2 / 2) {
                        return expressionArr[i2].evaluate();
                    }
                    int i3 = i * 2;
                    if (expressionArr[i3].evaluate() > 0.0d) {
                        return expressionArr[i3 + 1].evaluate();
                    }
                    i++;
                }
            case 33:
                int length2 = expressionArr.length;
                while (i < length2) {
                    if (expressionArr[i].evaluate() <= 0.0d) {
                        return 0.0d;
                    }
                    i++;
                }
                return 1.0d;
            case 34:
                int length3 = expressionArr.length;
                while (i < length3) {
                    if (expressionArr[i].evaluate() > 0.0d) {
                        return 1.0d;
                    }
                    i++;
                }
                return 0.0d;
            case 35:
                return TextUtils.equals(expressionArr[0].evaluateStr(), expressionArr[1].evaluateStr()) ? 1.0d : 0.0d;
            case 36:
                return Utils.stringToDouble(evaluateStr(expressionArr, variables), 0.0d);
            case 37:
                return Math.floor(evaluate);
            case 38:
                return Math.ceil(evaluate);
            default:
                Log.e("Expression", "fail to evalute FunctionExpression, invalid function: " + this.fun.toString());
                return 0.0d;
        }
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$BaseFunctions$Fun[this.fun.ordinal()];
        int i2 = 0;
        if (i == 13) {
            String evaluateStr = expressionArr[0].evaluateStr();
            if (evaluateStr == null) {
                return null;
            }
            if (!evaluateStr.equals(this.mEvalExpStr)) {
                this.mEvalExpStr = evaluateStr;
                this.mEvalExp = Expression.build(variables, evaluateStr);
            }
            Expression expression = this.mEvalExp;
            if (expression == null) {
                return null;
            }
            return expression.evaluateStr();
        } else if (i != 32) {
            if (i != 36) {
                if (i != 39) {
                    return Utils.doubleToString(evaluate(expressionArr, variables));
                }
                String evaluateStr2 = expressionArr[0].evaluateStr();
                String evaluateStr3 = expressionArr[1].evaluateStr();
                if (evaluateStr2 == null || evaluateStr3 == null) {
                    return null;
                }
                return HashUtils.getHash(evaluateStr2, evaluateStr3);
            }
            String evaluateStr4 = expressionArr[0].evaluateStr();
            if (evaluateStr4 == null) {
                return null;
            }
            int length = expressionArr.length;
            int evaluate = (int) expressionArr[1].evaluate();
            try {
                if (length >= 3) {
                    int evaluate2 = (int) expressionArr[2].evaluate();
                    int length2 = evaluateStr4.length();
                    if (evaluate2 > length2) {
                        evaluate2 = length2;
                    }
                    return evaluateStr4.substring(evaluate, evaluate2 + evaluate);
                }
                return evaluateStr4.substring(evaluate);
            } catch (IndexOutOfBoundsException unused) {
                return null;
            }
        } else {
            int length3 = expressionArr.length;
            if (length3 % 2 != 1) {
                Log.e("Expression", "function parameter number should be 2*n+1: " + this.fun.toString());
                return null;
            }
            while (true) {
                int i3 = length3 - 1;
                if (i2 >= i3 / 2) {
                    return expressionArr[i3].evaluateStr();
                }
                int i4 = i2 * 2;
                if (expressionArr[i4].evaluate() > 0.0d) {
                    return expressionArr[i4 + 1].evaluateStr();
                }
                i2++;
            }
        }
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public void reset() {
        this.mEvalExpStr = null;
        this.mEvalExp = null;
    }
}
