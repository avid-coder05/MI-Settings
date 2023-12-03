package com.miui.maml.data;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.util.Utils;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import miui.telephony.phonenumber.CountryCode;

/* loaded from: classes2.dex */
public abstract class Expression {

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.Expression$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$Expression$Ope;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType;

        static {
            int[] iArr = new int[Tokenizer.TokenType.values().length];
            $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType = iArr;
            try {
                iArr[Tokenizer.TokenType.VAR_NUM.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.VAR_STR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.NUM.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.STR.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.BRACKET_ROUND.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.BRACKET_SQUARE.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.OPE.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Tokenizer$TokenType[Tokenizer.TokenType.FUN.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            int[] iArr2 = new int[Ope.values().length];
            $SwitchMap$com$miui$maml$data$Expression$Ope = iArr2;
            try {
                iArr2[Ope.MIN.ordinal()] = 1;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.NOT.ordinal()] = 2;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_NOT.ordinal()] = 3;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.ADD.ordinal()] = 4;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.MUL.ordinal()] = 5;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.DIV.ordinal()] = 6;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.MOD.ordinal()] = 7;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_AND.ordinal()] = 8;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_OR.ordinal()] = 9;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_XOR.ordinal()] = 10;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_LSHIFT.ordinal()] = 11;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.BIT_RSHIFT.ordinal()] = 12;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.EQ.ordinal()] = 13;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.NEQ.ordinal()] = 14;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.AND.ordinal()] = 15;
            } catch (NoSuchFieldError unused23) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.OR.ordinal()] = 16;
            } catch (NoSuchFieldError unused24) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.GT.ordinal()] = 17;
            } catch (NoSuchFieldError unused25) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.GE.ordinal()] = 18;
            } catch (NoSuchFieldError unused26) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.LT.ordinal()] = 19;
            } catch (NoSuchFieldError unused27) {
            }
            try {
                $SwitchMap$com$miui$maml$data$Expression$Ope[Ope.LE.ordinal()] = 20;
            } catch (NoSuchFieldError unused28) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class ArrayVariableExpression extends VariableExpression {
        protected Expression mIndexExp;

        public ArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, false);
            this.mIndexExp = expression;
        }

        @Override // com.miui.maml.data.Expression
        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mIndexExp.accept(expressionVisitor);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class BinaryExpression extends Expression {
        private Expression mExp1;
        private Expression mExp2;
        private Ope mOpe;

        public BinaryExpression(Expression expression, Expression expression2, Ope ope) {
            Ope ope2 = Ope.INVALID;
            this.mOpe = ope2;
            this.mExp1 = expression;
            this.mExp2 = expression2;
            this.mOpe = ope;
            if (ope == ope2) {
                Log.e("Expression", "BinaryExpression: invalid operator:" + ope);
            }
        }

        @Override // com.miui.maml.data.Expression
        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mExp1.accept(expressionVisitor);
            this.mExp2.accept(expressionVisitor);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i != 1) {
                switch (i) {
                    case 4:
                        return this.mExp1.evaluate() + this.mExp2.evaluate();
                    case 5:
                        return this.mExp1.evaluate() * this.mExp2.evaluate();
                    case 6:
                        return this.mExp1.evaluate() / this.mExp2.evaluate();
                    case 7:
                        return this.mExp1.evaluate() % this.mExp2.evaluate();
                    case 8:
                        return ((long) this.mExp1.evaluate()) & ((long) this.mExp2.evaluate());
                    case 9:
                        return ((long) this.mExp1.evaluate()) | ((long) this.mExp2.evaluate());
                    case 10:
                        return ((long) this.mExp1.evaluate()) ^ ((long) this.mExp2.evaluate());
                    case 11:
                        return ((long) this.mExp1.evaluate()) << ((int) this.mExp2.evaluate());
                    case 12:
                        return ((long) this.mExp1.evaluate()) >> ((int) this.mExp2.evaluate());
                    case 13:
                        return this.mExp1.evaluate() == this.mExp2.evaluate() ? 1.0d : 0.0d;
                    case 14:
                        return this.mExp1.evaluate() != this.mExp2.evaluate() ? 1.0d : 0.0d;
                    case 15:
                        return (this.mExp1.evaluate() <= 0.0d || this.mExp2.evaluate() <= 0.0d) ? 0.0d : 1.0d;
                    case 16:
                        return (this.mExp1.evaluate() > 0.0d || this.mExp2.evaluate() > 0.0d) ? 1.0d : 0.0d;
                    case 17:
                        return this.mExp1.evaluate() > this.mExp2.evaluate() ? 1.0d : 0.0d;
                    case 18:
                        return this.mExp1.evaluate() >= this.mExp2.evaluate() ? 1.0d : 0.0d;
                    case 19:
                        return this.mExp1.evaluate() < this.mExp2.evaluate() ? 1.0d : 0.0d;
                    case 20:
                        return this.mExp1.evaluate() <= this.mExp2.evaluate() ? 1.0d : 0.0d;
                    default:
                        Log.e("Expression", "fail to evalute BinaryExpression, invalid operator");
                        return 0.0d;
                }
            }
            return this.mExp1.evaluate() - this.mExp2.evaluate();
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            String evaluateStr = this.mExp1.evaluateStr();
            String evaluateStr2 = this.mExp2.evaluateStr();
            if (AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()] != 4) {
                Log.e("Expression", "fail to evalute string BinaryExpression, invalid operator");
                return null;
            } else if (evaluateStr == null && evaluateStr2 == null) {
                return null;
            } else {
                if (evaluateStr == null) {
                    return evaluateStr2;
                }
                if (evaluateStr2 == null) {
                    return evaluateStr;
                }
                return evaluateStr + evaluateStr2;
            }
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i == 1 || i == 4) {
                return this.mExp1.isNull() && this.mExp2.isNull();
            }
            if (i != 5 && i != 6 && i != 7) {
                switch (i) {
                    case 13:
                    case 14:
                    case 15:
                    case 16:
                        break;
                    default:
                        return true;
                }
            }
            return this.mExp1.isNull() || this.mExp2.isNull();
        }

        @Override // com.miui.maml.data.Expression
        public BigDecimal preciseEvaluate() {
            if (this.mOpe != Ope.INVALID) {
                BigDecimal preciseEvaluate = this.mExp1.preciseEvaluate();
                BigDecimal preciseEvaluate2 = this.mExp2.preciseEvaluate();
                if (preciseEvaluate != null && preciseEvaluate2 != null) {
                    int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
                    if (i == 1) {
                        return preciseEvaluate.subtract(preciseEvaluate2);
                    }
                    if (i == 4) {
                        return preciseEvaluate.add(preciseEvaluate2);
                    }
                    if (i == 5) {
                        return preciseEvaluate.multiply(preciseEvaluate2);
                    }
                    if (i == 6) {
                        try {
                            return preciseEvaluate.divide(preciseEvaluate2, MathContext.DECIMAL128);
                        } catch (Exception unused) {
                            return null;
                        }
                    } else if (i == 7) {
                        try {
                            return preciseEvaluate.remainder(preciseEvaluate2);
                        } catch (Exception unused2) {
                            return null;
                        }
                    }
                }
            }
            Log.e("Expression", "fail to PRECISE evalute BinaryExpression, invalid operator");
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static class FunctionExpression extends Expression {
        protected static HashMap<String, FunctionImpl> sFunMap = new HashMap<>();
        private FunctionImpl mFun;
        private String mFunName;
        private Expression[] mParaExps;
        private Variables mVariables;

        static {
            FunctionsLoader.load();
        }

        public FunctionExpression(Variables variables, Expression[] expressionArr, String str) throws Exception {
            this.mVariables = variables;
            this.mParaExps = expressionArr;
            this.mFunName = str;
            parseFunction(str);
        }

        private void parseFunction(String str) throws Exception {
            FunctionImpl functionImpl = sFunMap.get(str);
            Utils.asserts(functionImpl != null, "invalid function:" + str);
            this.mFun = functionImpl;
            Utils.asserts(this.mParaExps.length >= functionImpl.params, "parameters count not matching for function: " + str);
        }

        public static void registerFunction(String str, FunctionImpl functionImpl) {
            if (sFunMap.put(str, functionImpl) != null) {
                Log.w("Expression", "duplicated function name registation: " + str);
            }
        }

        public static void resetFunctions() {
            Iterator<Map.Entry<String, FunctionImpl>> it = sFunMap.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().reset();
            }
        }

        @Override // com.miui.maml.data.Expression
        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            int i = 0;
            while (true) {
                Expression[] expressionArr = this.mParaExps;
                if (i >= expressionArr.length) {
                    return;
                }
                expressionArr[i].accept(expressionVisitor);
                i++;
            }
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            return this.mFun.evaluate(this.mParaExps, this.mVariables);
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return this.mFun.evaluateStr(this.mParaExps, this.mVariables);
        }

        public String getFunName() {
            return this.mFunName;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class FunctionImpl {
        public int params;

        public FunctionImpl(int i) {
            this.params = i;
        }

        public abstract double evaluate(Expression[] expressionArr, Variables variables);

        public abstract String evaluateStr(Expression[] expressionArr, Variables variables);

        public abstract void reset();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class NumberArrayVariableExpression extends ArrayVariableExpression {
        public NumberArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, expression);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            return this.mIndexedVar.getArrDouble((int) this.mIndexExp.evaluate());
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            return this.mIndexedVar.isNull((int) this.mIndexExp.evaluate());
        }
    }

    /* loaded from: classes2.dex */
    public static class NumberExpression extends Expression {
        private String mString;
        private double mValue;

        public NumberExpression(double d) {
            this.mValue = d;
        }

        public NumberExpression(String str) {
            if (TextUtils.isEmpty(str)) {
                Log.e("Expression", "invalid NumberExpression: null");
                return;
            }
            try {
                if (str.length() <= 2 || str.indexOf("0x") != 0) {
                    this.mValue = Double.parseDouble(str);
                } else {
                    this.mValue = Long.parseLong(str.substring(2), 16);
                }
            } catch (NumberFormatException unused) {
                Log.e("Expression", "invalid NumberExpression:" + str);
            }
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            return this.mValue;
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            if (this.mString == null) {
                this.mString = Utils.doubleToString(this.mValue);
            }
            return this.mString;
        }

        public void setValue(double d) {
            this.mValue = d;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class NumberVariableExpression extends VariableExpression {
        public NumberVariableExpression(Variables variables, String str) {
            super(variables, str, true);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            return this.mIndexedVar.getDouble();
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            return this.mIndexedVar.isNull();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Ope {
        ADD,
        MIN,
        MUL,
        DIV,
        MOD,
        BIT_AND,
        BIT_OR,
        BIT_XOR,
        BIT_NOT,
        BIT_LSHIFT,
        BIT_RSHIFT,
        NOT,
        EQ,
        NEQ,
        AND,
        OR,
        GT,
        GE,
        LT,
        LE,
        INVALID
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class OpeInfo {
        private static final int OPE_SIZE;
        private static final String[] mOpes;
        public int participants;
        public int priority;
        public String str;
        public boolean unary;
        private static final int[] mOpePriority = {4, 4, 3, 3, 3, 8, 9, 10, 2, 5, 5, 2, 7, 7, 11, 12, 6, 6, 6, 6};
        private static final int[] mOpePar = {2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2};

        /* loaded from: classes2.dex */
        public static class Parser {
            private int[] mFlags = new int[OpeInfo.OPE_SIZE];
            private int mMatch;
            private int mStep;

            public boolean accept(char c, boolean z) {
                if (z) {
                    for (int i = 0; i < OpeInfo.OPE_SIZE; i++) {
                        this.mFlags[i] = 0;
                    }
                    this.mStep = 0;
                    this.mMatch = -1;
                }
                boolean z2 = false;
                for (int i2 = 0; i2 < OpeInfo.OPE_SIZE; i2++) {
                    if (this.mFlags[i2] != -1) {
                        String str = OpeInfo.mOpes[i2];
                        int length = str.length();
                        int i3 = this.mStep;
                        if (length <= i3 || str.charAt(i3) != c) {
                            this.mFlags[i2] = -1;
                        } else {
                            boolean z3 = this.mStep == str.length() - 1;
                            this.mFlags[i2] = 0;
                            if (z3) {
                                this.mMatch = i2;
                            }
                            z2 = true;
                        }
                    }
                }
                if (z2) {
                    this.mStep++;
                }
                return z2;
            }

            public Ope getMatch() {
                return this.mMatch == -1 ? Ope.INVALID : Ope.values()[this.mMatch];
            }
        }

        static {
            String[] strArr = {CountryCode.GSM_GENERAL_IDD_CODE, "-", "*", "/", "%", "&", "|", "^", "~", "{{", "}}", "!", "==", "!=", "**", "||", "}", "}=", "{", "{="};
            mOpes = strArr;
            OPE_SIZE = strArr.length;
        }

        private OpeInfo() {
        }

        public static OpeInfo getOpeInfo(int i) {
            OpeInfo opeInfo = new OpeInfo();
            opeInfo.priority = mOpePriority[i];
            opeInfo.participants = mOpePar[i];
            opeInfo.str = mOpes[i];
            return opeInfo;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class StringArrayVariableExpression extends ArrayVariableExpression {
        public StringArrayVariableExpression(Variables variables, String str, Expression expression) {
            super(variables, str, expression);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            String evaluateStr = evaluateStr();
            if (TextUtils.isEmpty(evaluateStr)) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(evaluateStr);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return this.mIndexedVar.getArrString((int) this.mIndexExp.evaluate());
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            return this.mIndexedVar.isNull((int) this.mIndexExp.evaluate());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class StringExpression extends Expression {
        private String mValue;

        public StringExpression(String str) {
            this.mValue = str;
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            if (TextUtils.isEmpty(this.mValue)) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(this.mValue);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return this.mValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class StringVariableExpression extends VariableExpression {
        public StringVariableExpression(Variables variables, String str) {
            super(variables, str, false);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            String evaluateStr = evaluateStr();
            if (TextUtils.isEmpty(evaluateStr)) {
                return 0.0d;
            }
            try {
                return Double.parseDouble(evaluateStr);
            } catch (NumberFormatException unused) {
                return 0.0d;
            }
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return this.mIndexedVar.getString();
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            return this.mIndexedVar.isNull();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class Tokenizer {
        private OpeInfo.Parser mOpeParser = new OpeInfo.Parser();
        private int mPos;
        private String mString;

        /* loaded from: classes2.dex */
        public static class Token {
            public OpeInfo info;
            public Ope op;
            public String token;
            public TokenType type;

            public Token(TokenType tokenType, String str) {
                this.type = TokenType.INVALID;
                this.op = Ope.INVALID;
                this.type = tokenType;
                this.token = str;
            }

            public Token(TokenType tokenType, String str, Ope ope) {
                this.type = TokenType.INVALID;
                this.op = Ope.INVALID;
                this.type = tokenType;
                this.token = str;
                this.op = ope;
                this.info = OpeInfo.getOpeInfo(ope.ordinal());
            }
        }

        /* loaded from: classes2.dex */
        public enum TokenType {
            INVALID,
            VAR_NUM,
            VAR_STR,
            NUM,
            STR,
            OPE,
            FUN,
            BRACKET_ROUND,
            BRACKET_SQUARE
        }

        public Tokenizer(String str) {
            this.mString = str;
            reset();
        }

        /* JADX WARN: Code restructure failed: missing block: B:37:0x008b, code lost:
        
            if (r16.mString.charAt(r1) == 'x') goto L43;
         */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:43:0x009d -> B:39:0x008e). Please submit an issue!!! */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public com.miui.maml.data.Expression.Tokenizer.Token getToken() {
            /*
                Method dump skipped, instructions count: 431
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Expression.Tokenizer.getToken():com.miui.maml.data.Expression$Tokenizer$Token");
        }

        public void reset() {
            this.mPos = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class UnaryExpression extends Expression {
        private Expression mExp;
        private Ope mOpe;

        public UnaryExpression(Expression expression, Ope ope) {
            Ope ope2 = Ope.INVALID;
            this.mOpe = ope2;
            this.mExp = expression;
            this.mOpe = ope;
            if (ope == ope2) {
                Log.e("Expression", "UnaryExpression: invalid operator:" + ope);
            }
        }

        @Override // com.miui.maml.data.Expression
        public void accept(ExpressionVisitor expressionVisitor) {
            expressionVisitor.visit(this);
            this.mExp.accept(expressionVisitor);
        }

        @Override // com.miui.maml.data.Expression
        public double evaluate() {
            int i = AnonymousClass1.$SwitchMap$com$miui$maml$data$Expression$Ope[this.mOpe.ordinal()];
            if (i != 1) {
                if (i == 2) {
                    return this.mExp.evaluate() <= 0.0d ? 1.0d : 0.0d;
                } else if (i != 3) {
                    Log.e("Expression", "fail to evalute UnaryExpression, invalid operator");
                    return this.mExp.evaluate();
                } else {
                    return ~((int) this.mExp.evaluate());
                }
            }
            return 0.0d - this.mExp.evaluate();
        }

        @Override // com.miui.maml.data.Expression
        public String evaluateStr() {
            return Utils.doubleToString(evaluate());
        }

        @Override // com.miui.maml.data.Expression
        public boolean isNull() {
            return this.mExp.isNull();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static abstract class VariableExpression extends Expression {
        protected IndexedVariable mIndexedVar;
        protected String mName;

        public VariableExpression(Variables variables, String str, boolean z) {
            this.mName = str;
            this.mIndexedVar = new IndexedVariable(str, variables, z);
        }

        public int getIndex() {
            return this.mIndexedVar.getIndex();
        }

        public String getName() {
            return this.mName;
        }

        public int getVersion() {
            return this.mIndexedVar.getVersion();
        }
    }

    public static Expression build(Variables variables, String str) {
        Expression buildInner = buildInner(variables, str);
        if (buildInner == null) {
            return null;
        }
        return new RootExpression(variables, buildInner);
    }

    private static Expression buildBracket(Variables variables, Tokenizer.Token token, Stack<Tokenizer.Token> stack) {
        Expression[] buildMultipleInner = buildMultipleInner(variables, token.token);
        if (!checkParams(buildMultipleInner)) {
            Log.e("Expression", "invalid expressions: " + token.token);
            return null;
        }
        try {
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Expression", e.toString());
        }
        if (stack.isEmpty() || stack.peek().type != Tokenizer.TokenType.FUN) {
            if (buildMultipleInner.length == 1) {
                return buildMultipleInner[0];
            }
            Log.e("Expression", "fail to buid: multiple expressions in brackets, but seems no function presents:" + token.token);
            return null;
        }
        return new FunctionExpression(variables, buildMultipleInner, stack.pop().token);
    }

    /* JADX WARN: Removed duplicated region for block: B:93:0x0131 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static com.miui.maml.data.Expression buildInner(com.miui.maml.data.Variables r10, java.lang.String r11) {
        /*
            Method dump skipped, instructions count: 554
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.data.Expression.buildInner(com.miui.maml.data.Variables, java.lang.String):com.miui.maml.data.Expression");
    }

    public static Expression[] buildMultiple(Variables variables, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Expression[] buildMultipleInner = buildMultipleInner(variables, str);
        Expression[] expressionArr = new Expression[buildMultipleInner.length];
        for (int i = 0; i < buildMultipleInner.length; i++) {
            Expression expression = buildMultipleInner[i];
            if (expression == null || (expression instanceof NumberExpression) || (expression instanceof StringExpression)) {
                expressionArr[i] = expression;
            } else {
                expressionArr[i] = new RootExpression(variables, expression);
            }
        }
        return expressionArr;
    }

    private static Expression[] buildMultipleInner(Variables variables, String str) {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        boolean z = false;
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char charAt = str.charAt(i3);
            if (!z) {
                if (charAt == ',' && i2 == 0) {
                    arrayList.add(buildInner(variables, str.substring(i, i3)));
                    i = i3 + 1;
                } else if (charAt == '(') {
                    i2++;
                } else if (charAt == ')') {
                    i2--;
                }
            }
            if (charAt == '\'') {
                z = !z;
            }
        }
        if (i < str.length()) {
            arrayList.add(buildInner(variables, str.substring(i)));
        }
        return (Expression[]) arrayList.toArray(new Expression[arrayList.size()]);
    }

    private static boolean checkParams(Expression[] expressionArr) {
        for (Expression expression : expressionArr) {
            if (expression == null) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isDigitCharRest(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || ((c >= 'A' && c <= 'F') || c == '.');
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isDigitCharStart(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isFunctionCharRest(char c) {
        return isFunctionCharStart(c) || c == '_' || (c >= '0' && c <= '9');
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isFunctionCharStart(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isVariableChar(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || c == '.' || (c >= '0' && c <= '9');
    }

    public void accept(ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }

    public abstract double evaluate();

    public String evaluateStr() {
        return null;
    }

    public boolean isNull() {
        return false;
    }

    public BigDecimal preciseEvaluate() {
        try {
            return BigDecimal.valueOf(evaluate());
        } catch (NumberFormatException unused) {
            return null;
        }
    }
}
