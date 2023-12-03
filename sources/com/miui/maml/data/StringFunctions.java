package com.miui.maml.data;

import android.util.Log;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.util.regex.PatternSyntaxException;

/* loaded from: classes2.dex */
public class StringFunctions extends Expression.FunctionImpl {
    private final Fun mFun;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.data.StringFunctions$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$data$StringFunctions$Fun;

        static {
            int[] iArr = new int[Fun.values().length];
            $SwitchMap$com$miui$maml$data$StringFunctions$Fun = iArr;
            try {
                iArr[Fun.STR_CONTAINS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_STARTWITH.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_ENDSWITH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_MATCHES.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_INDEXOF.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_LASTINDEXOF.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_ISEMPTY.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_REPLACE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_REPLACEALL.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_REPLACEFIRST.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_TOLOWER.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_TOUPPER.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$miui$maml$data$StringFunctions$Fun[Fun.STR_TRIM.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum Fun {
        INVALID,
        STR_TOLOWER,
        STR_TOUPPER,
        STR_TRIM,
        STR_REPLACE,
        STR_REPLACEALL,
        STR_REPLACEFIRST,
        STR_CONTAINS,
        STR_STARTWITH,
        STR_ENDSWITH,
        STR_ISEMPTY,
        STR_MATCHES,
        STR_INDEXOF,
        STR_LASTINDEXOF
    }

    private StringFunctions(Fun fun, int i) {
        super(i);
        this.mFun = fun;
    }

    public static void load() {
        Expression.FunctionExpression.registerFunction("strToLowerCase", new StringFunctions(Fun.STR_TOLOWER, 1));
        Expression.FunctionExpression.registerFunction("strToUpperCase", new StringFunctions(Fun.STR_TOUPPER, 1));
        Expression.FunctionExpression.registerFunction("strTrim", new StringFunctions(Fun.STR_TRIM, 1));
        Expression.FunctionExpression.registerFunction("strReplace", new StringFunctions(Fun.STR_REPLACE, 3));
        Expression.FunctionExpression.registerFunction("strReplaceAll", new StringFunctions(Fun.STR_REPLACEALL, 3));
        Expression.FunctionExpression.registerFunction("strReplaceFirst", new StringFunctions(Fun.STR_REPLACEFIRST, 3));
        Expression.FunctionExpression.registerFunction("strContains", new StringFunctions(Fun.STR_CONTAINS, 2));
        Expression.FunctionExpression.registerFunction("strStartsWith", new StringFunctions(Fun.STR_STARTWITH, 2));
        Expression.FunctionExpression.registerFunction("strEndsWith", new StringFunctions(Fun.STR_ENDSWITH, 2));
        Expression.FunctionExpression.registerFunction("strIsEmpty", new StringFunctions(Fun.STR_ISEMPTY, 1));
        Expression.FunctionExpression.registerFunction("strMatches", new StringFunctions(Fun.STR_MATCHES, 2));
        Expression.FunctionExpression.registerFunction("strIndexOf", new StringFunctions(Fun.STR_INDEXOF, 2));
        Expression.FunctionExpression.registerFunction("strLastIndexOf", new StringFunctions(Fun.STR_LASTINDEXOF, 2));
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public double evaluate(Expression[] expressionArr, Variables variables) {
        int[] iArr = AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun;
        switch (iArr[this.mFun.ordinal()]) {
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
                return Utils.stringToDouble(evaluateStr(expressionArr, variables), 0.0d);
            default:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (iArr[this.mFun.ordinal()] == 7) {
                    return (evaluateStr == null || evaluateStr.isEmpty()) ? 1.0d : 0.0d;
                }
                String evaluateStr2 = expressionArr[1].evaluateStr();
                switch (iArr[this.mFun.ordinal()]) {
                    case 1:
                        return (evaluateStr == null || evaluateStr2 == null || !evaluateStr.contains(evaluateStr2)) ? 0.0d : 1.0d;
                    case 2:
                        return (evaluateStr == 0 || evaluateStr2 == null || !evaluateStr.startsWith(evaluateStr2)) ? 0.0d : 1.0d;
                    case 3:
                        return (evaluateStr == 0 || evaluateStr2 == null || !evaluateStr.endsWith(evaluateStr2)) ? 0.0d : 1.0d;
                    case 4:
                        if (evaluateStr == 0 || evaluateStr2 == null) {
                            return 0.0d;
                        }
                        try {
                            return evaluateStr.matches(evaluateStr2) ? 1.0d : 0.0d;
                        } catch (PatternSyntaxException unused) {
                            Log.w("Expression", "invaid pattern of matches: " + evaluateStr2);
                            return 0.0d;
                        }
                    case 5:
                        if (evaluateStr == 0 || evaluateStr2 == null) {
                            return -1.0d;
                        }
                        return evaluateStr.indexOf(evaluateStr2);
                    case 6:
                        if (evaluateStr == 0 || evaluateStr2 == null) {
                            return -1.0d;
                        }
                        return evaluateStr.lastIndexOf(evaluateStr2);
                    default:
                        Log.e("Expression", "fail to evalute FunctionExpression, invalid function: " + this.mFun.toString());
                        return 0.0d;
                }
        }
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public String evaluateStr(Expression[] expressionArr, Variables variables) {
        int[] iArr = AnonymousClass1.$SwitchMap$com$miui$maml$data$StringFunctions$Fun;
        switch (iArr[this.mFun.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                return Utils.doubleToString(evaluate(expressionArr, variables));
            default:
                String evaluateStr = expressionArr[0].evaluateStr();
                if (evaluateStr == null) {
                    return null;
                }
                switch (iArr[this.mFun.ordinal()]) {
                    case 11:
                        return evaluateStr.toLowerCase();
                    case 12:
                        return evaluateStr.toUpperCase();
                    case 13:
                        return evaluateStr.trim();
                    default:
                        String evaluateStr2 = expressionArr[1].evaluateStr();
                        String evaluateStr3 = expressionArr[2].evaluateStr();
                        if (evaluateStr2 == null || evaluateStr3 == null) {
                            return evaluateStr;
                        }
                        switch (iArr[this.mFun.ordinal()]) {
                            case 8:
                                return evaluateStr.replace(evaluateStr2, evaluateStr3);
                            case 9:
                                try {
                                    return evaluateStr.replaceAll(evaluateStr2, evaluateStr3);
                                } catch (PatternSyntaxException unused) {
                                    Log.w("Expression", "invaid pattern of replaceAll: " + evaluateStr2);
                                    return evaluateStr;
                                }
                            case 10:
                                try {
                                    return evaluateStr.replaceFirst(evaluateStr2, evaluateStr3);
                                } catch (PatternSyntaxException unused2) {
                                    Log.w("Expression", "invaid pattern of replaceFirst:" + evaluateStr2);
                                    return evaluateStr;
                                }
                            default:
                                Log.e("Expression", "fail to evaluteStr FunctionExpression, invalid function: " + this.mFun.toString());
                                return null;
                        }
                }
        }
    }

    @Override // com.miui.maml.data.Expression.FunctionImpl
    public void reset() {
    }
}
