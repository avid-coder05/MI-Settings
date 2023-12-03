package com.miui.maml.util;

import android.graphics.Color;
import android.util.Log;
import com.miui.maml.StylesManager;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ColorParser {
    private int mColor;
    private String mColorExpression;
    private String mCurColorString;
    private IndexedVariable mIndexedColorVar;
    private Expression[] mRGBExpression;
    private ExpressionType mType;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.util.ColorParser$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType;

        static {
            int[] iArr = new int[ExpressionType.values().length];
            $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType = iArr;
            try {
                iArr[ExpressionType.CONST.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType[ExpressionType.VARIABLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$util$ColorParser$ExpressionType[ExpressionType.ARGB.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum ExpressionType {
        CONST,
        VARIABLE,
        ARGB,
        INVALID
    }

    public ColorParser(Variables variables, String str) {
        this.mColor = -16777216;
        String trim = str.trim();
        this.mColorExpression = trim;
        if (trim.startsWith("#")) {
            this.mType = ExpressionType.CONST;
            try {
                this.mColor = Color.parseColor(this.mColorExpression);
            } catch (IllegalArgumentException unused) {
                this.mColor = -16777216;
            }
        } else if (this.mColorExpression.startsWith("@")) {
            this.mType = ExpressionType.VARIABLE;
            this.mIndexedColorVar = new IndexedVariable(this.mColorExpression.substring(1), variables, false);
        } else if (!this.mColorExpression.startsWith("argb(") || !this.mColorExpression.endsWith(")")) {
            this.mType = ExpressionType.INVALID;
        } else {
            this.mType = ExpressionType.ARGB;
            String str2 = this.mColorExpression;
            Expression[] buildMultiple = Expression.buildMultiple(variables, str2.substring(5, str2.length() - 1));
            this.mRGBExpression = buildMultiple;
            if (buildMultiple == null || buildMultiple.length == 4) {
                return;
            }
            Log.e("ColorParser", "bad expression format");
            throw new IllegalArgumentException("bad expression format.");
        }
    }

    public static ColorParser fromElement(Variables variables, Element element) {
        return new ColorParser(variables, element.getAttribute("color"));
    }

    public static ColorParser fromElement(Variables variables, Element element, StylesManager.Style style) {
        return new ColorParser(variables, StyleHelper.getAttr(element, "color", style));
    }

    public static ColorParser fromElement(Variables variables, Element element, String str, StylesManager.Style style) {
        return new ColorParser(variables, StyleHelper.getAttr(element, str, style));
    }

    public int getColor() {
        int i = AnonymousClass1.$SwitchMap$com$miui$maml$util$ColorParser$ExpressionType[this.mType.ordinal()];
        if (i != 1) {
            if (i == 2) {
                String string = this.mIndexedColorVar.getString();
                if (!Utils.equals(string, this.mCurColorString)) {
                    this.mColor = string != null ? Color.parseColor(string) : -16777216;
                    this.mCurColorString = string;
                }
            } else if (i != 3) {
                this.mColor = -16777216;
            } else {
                this.mColor = Color.argb((int) this.mRGBExpression[0].evaluate(), (int) this.mRGBExpression[1].evaluate(), (int) this.mRGBExpression[2].evaluate(), (int) this.mRGBExpression[3].evaluate());
            }
        }
        return this.mColor;
    }
}
