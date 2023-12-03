package com.miui.maml.util;

import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.StylesManager;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import miui.yellowpage.YellowPageContract;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class TextFormatter {
    private String mFormat;
    private Expression mFormatExpression;
    private IndexedVariable mIndexedFormatVar;
    private IndexedVariable mIndexedTextVar;
    private FormatPara[] mParas;
    private Object[] mParasValue;
    private String mText;
    private Expression mTextExpression;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ExpressioPara extends FormatPara {
        private Expression mExp;

        public ExpressioPara(Expression expression) {
            super();
            this.mExp = expression;
        }

        @Override // com.miui.maml.util.TextFormatter.FormatPara
        public Object evaluate() {
            return Long.valueOf((long) this.mExp.evaluate());
        }
    }

    /* loaded from: classes2.dex */
    private static abstract class FormatPara {
        private FormatPara() {
        }

        public static FormatPara build(Variables variables, String str) {
            String trim = str.trim();
            if (trim.startsWith("@")) {
                return new StringVarPara(variables, trim.substring(1));
            }
            Expression build = Expression.build(variables, trim);
            if (build == null) {
                Log.e("TextFormatter", "invalid parameter expression:" + str);
                return null;
            }
            return new ExpressioPara(build);
        }

        public static FormatPara[] buildArray(Variables variables, String str) {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < str.length(); i3++) {
                char charAt = str.charAt(i3);
                if (i2 == 0 && charAt == ',') {
                    FormatPara build = build(variables, str.substring(i, i3));
                    if (build == null) {
                        return null;
                    }
                    arrayList.add(build);
                    i = i3 + 1;
                }
                if (charAt == '(') {
                    i2++;
                } else if (charAt == ')') {
                    i2--;
                }
            }
            FormatPara build2 = build(variables, str.substring(i));
            if (build2 == null) {
                return null;
            }
            arrayList.add(build2);
            return (FormatPara[]) arrayList.toArray(new FormatPara[arrayList.size()]);
        }

        public abstract Object evaluate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class StringVarPara extends FormatPara {
        private IndexedVariable mVar;
        private String mVariable;

        public StringVarPara(Variables variables, String str) {
            super();
            this.mVariable = str;
            this.mVar = new IndexedVariable(str, variables, false);
        }

        @Override // com.miui.maml.util.TextFormatter.FormatPara
        public Object evaluate() {
            String string = this.mVar.getString();
            return string == null ? "" : string;
        }
    }

    public TextFormatter(Variables variables, String str, Expression expression) {
        this(variables, str, "", "", expression, null);
    }

    public TextFormatter(Variables variables, String str, String str2, String str3) {
        this.mText = str;
        if (str.startsWith("@")) {
            String substring = this.mText.substring(1);
            this.mText = substring;
            if (!substring.startsWith("@")) {
                this.mIndexedTextVar = new IndexedVariable(this.mText, variables, false);
            }
        }
        this.mFormat = str2;
        if (str2.startsWith("@")) {
            String substring2 = this.mFormat.substring(1);
            this.mFormat = substring2;
            if (!substring2.startsWith("@")) {
                this.mIndexedFormatVar = new IndexedVariable(this.mFormat, variables, false);
            }
        }
        if (TextUtils.isEmpty(str3)) {
            return;
        }
        FormatPara[] buildArray = FormatPara.buildArray(variables, str3);
        this.mParas = buildArray;
        if (buildArray != null) {
            this.mParasValue = new Object[buildArray.length];
        }
    }

    public TextFormatter(Variables variables, String str, String str2, String str3, Expression expression, Expression expression2) {
        this(variables, str, str2, str3);
        this.mTextExpression = expression;
        this.mFormatExpression = expression2;
    }

    public static TextFormatter fromElement(Variables variables, Element element, StylesManager.Style style) {
        String attr = StyleHelper.getAttr(element, "paras", style);
        if (TextUtils.isEmpty(attr)) {
            attr = StyleHelper.getAttr(element, YellowPageContract.HttpRequest.PARAMS, style);
        }
        return new TextFormatter(variables, StyleHelper.getAttr(element, "text", style), StyleHelper.getAttr(element, "format", style), attr, Expression.build(variables, StyleHelper.getAttr(element, "textExp", style)), Expression.build(variables, StyleHelper.getAttr(element, "formatExp", style)));
    }

    public static TextFormatter fromElement(Variables variables, Element element, String str, String str2, String str3, String str4, String str5) {
        return new TextFormatter(variables, element.getAttribute(str), element.getAttribute(str2), element.getAttribute(str3), Expression.build(variables, element.getAttribute(str4)), Expression.build(variables, element.getAttribute(str5)));
    }

    public String getFormat() {
        Expression expression = this.mFormatExpression;
        if (expression != null) {
            return expression.evaluateStr();
        }
        IndexedVariable indexedVariable = this.mIndexedFormatVar;
        return indexedVariable != null ? indexedVariable.getString() : this.mFormat;
    }

    public String getText() {
        Expression expression = this.mTextExpression;
        if (expression != null) {
            return expression.evaluateStr();
        }
        String format = getFormat();
        if (!TextUtils.isEmpty(format)) {
            if (this.mParas != null) {
                int i = 0;
                while (true) {
                    FormatPara[] formatParaArr = this.mParas;
                    if (i >= formatParaArr.length) {
                        break;
                    }
                    this.mParasValue[i] = formatParaArr[i].evaluate();
                    i++;
                }
            }
            Object[] objArr = this.mParasValue;
            if (objArr != null) {
                try {
                    return String.format(format, objArr);
                } catch (IllegalFormatException unused) {
                    return "Format error: " + format;
                }
            }
        }
        IndexedVariable indexedVariable = this.mIndexedTextVar;
        return indexedVariable != null ? indexedVariable.getString() : this.mText;
    }

    public void setParams(Object... objArr) {
        if (objArr != null) {
            this.mParas = null;
            int length = objArr.length;
            if (this.mParasValue == null) {
                this.mParasValue = new Object[length];
            }
            Object[] objArr2 = this.mParasValue;
            if (objArr2.length < length) {
                length = objArr2.length;
            }
            this.mParasValue = Arrays.copyOf(objArr, length);
        }
    }

    public void setText(String str) {
        this.mText = str;
        this.mFormat = "";
    }
}
