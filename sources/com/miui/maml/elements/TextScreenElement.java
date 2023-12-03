package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.ReflectionHelper;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import miui.R;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class TextScreenElement extends AnimatedScreenElement {
    public static final AnimatedProperty.AnimatedColorProperty TEXT_COLOR;
    public static final AnimatedProperty.AnimatedColorProperty TEXT_SHADOW_COLOR;
    public static final AnimatedProperty TEXT_SIZE;
    private static final Object mLock = new Object();
    private ColorParser mColorParser;
    private boolean mFontScaleEnabled;
    protected TextFormatter mFormatter;
    private float mLayoutWidth;
    private int mMarqueeGap;
    private float mMarqueePos;
    private int mMarqueeSpeed;
    private boolean mMultiLine;
    private TextPaint mPaint;
    private long mPreviousTime;
    private String mSetText;
    private ColorParser mShadowColorParser;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mShouldMarquee;
    private float mSpacingAdd;
    private float mSpacingMult;
    private String mText;
    private PropertyWrapper mTextColorProperty;
    private float mTextHeight;
    private IndexedVariable mTextHeightVar;
    private StaticLayout mTextLayout;
    private PropertyWrapper mTextShadowColorProperty;
    private float mTextSize;
    private PropertyWrapper mTextSizeProperty;
    private float mTextWidth;
    private IndexedVariable mTextWidthVar;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.TextScreenElement$4  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$Align;

        static {
            int[] iArr = new int[ScreenElement.Align.values().length];
            $SwitchMap$com$miui$maml$elements$ScreenElement$Align = iArr;
            try {
                iArr[ScreenElement.Align.LEFT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ScreenElement$Align[ScreenElement.Align.CENTER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ScreenElement$Align[ScreenElement.Align.RIGHT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    static {
        AnimatedProperty.AnimatedColorProperty animatedColorProperty = new AnimatedProperty.AnimatedColorProperty("textColor") { // from class: com.miui.maml.elements.TextScreenElement.1
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    return (int) ((TextScreenElement) animatedScreenElement).mTextColorProperty.getValue();
                }
                return -16777216;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextColorProperty.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextColorProperty.setVelocity(f);
                }
            }
        };
        TEXT_COLOR = animatedColorProperty;
        AnimatedProperty animatedProperty = new AnimatedProperty("textSize") { // from class: com.miui.maml.elements.TextScreenElement.2
            @Override // miuix.animation.property.FloatProperty
            public float getValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    return (float) ((TextScreenElement) animatedScreenElement).mTextSizeProperty.getValue();
                }
                return 18.0f;
            }

            @Override // miuix.animation.property.FloatProperty
            public void setValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextSizeProperty.setValue(f);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextSizeProperty.setVelocity(f);
                }
            }
        };
        TEXT_SIZE = animatedProperty;
        AnimatedProperty.AnimatedColorProperty animatedColorProperty2 = new AnimatedProperty.AnimatedColorProperty("textShadowColor") { // from class: com.miui.maml.elements.TextScreenElement.3
            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public int getIntValue(AnimatedScreenElement animatedScreenElement) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    return (int) ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.getValue();
                }
                return -16777216;
            }

            @Override // miuix.animation.property.ColorProperty, miuix.animation.property.IIntValueProperty
            public void setIntValue(AnimatedScreenElement animatedScreenElement, int i) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.setValue(i);
                }
            }

            @Override // com.miui.maml.folme.IAnimatedProperty
            public void setVelocityValue(AnimatedScreenElement animatedScreenElement, float f) {
                if (animatedScreenElement instanceof TextScreenElement) {
                    ((TextScreenElement) animatedScreenElement).mTextShadowColorProperty.setVelocity(f);
                }
            }
        };
        TEXT_SHADOW_COLOR = animatedColorProperty2;
        AnimatedProperty.sPropertyNameMap.put("textColor", animatedColorProperty);
        AnimatedTarget.sPropertyMap.put(1003, animatedColorProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty, 1003);
        AnimatedProperty.sPropertyNameMap.put("textSize", animatedProperty);
        AnimatedTarget.sPropertyMap.put(1002, animatedProperty);
        AnimatedTarget.sPropertyTypeMap.put(animatedProperty, 1002);
        AnimatedProperty.sPropertyNameMap.put("textShadowColor", animatedColorProperty2);
        AnimatedTarget.sPropertyMap.put(1013, animatedColorProperty2);
        AnimatedTarget.sPropertyTypeMap.put(animatedColorProperty2, 1013);
    }

    public TextScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mPaint = new TextPaint();
        this.mMarqueePos = Float.MAX_VALUE;
        this.mTextSize = scale(18.0d);
        load(element);
    }

    private Layout.Alignment getAlignment() {
        Layout.Alignment alignment;
        Layout.Alignment alignment2 = Layout.Alignment.ALIGN_NORMAL;
        int i = AnonymousClass4.$SwitchMap$com$miui$maml$elements$ScreenElement$Align[this.mAlign.ordinal()];
        if (i == 1) {
            try {
                alignment = (Layout.Alignment) ReflectionHelper.getFieldValue(alignment2.getClass(), alignment2, "ALIGN_LEFT");
            } catch (Exception e) {
                Log.e("TextScreenElement", "Invoke | getAlignment_ALIGN_LEFT occur EXCEPTION: " + e.getMessage());
                return alignment2;
            }
        } else if (i == 2) {
            return Layout.Alignment.ALIGN_CENTER;
        } else {
            if (i != 3) {
                return alignment2;
            }
            try {
                alignment = (Layout.Alignment) ReflectionHelper.getFieldValue(alignment2.getClass(), alignment2, "ALIGN_RIGHT");
            } catch (Exception e2) {
                Log.e("TextScreenElement", "Invoke | getAlignment_ALIGN_RIGHT occur EXCEPTION: " + e2.getMessage());
                return alignment2;
            }
        }
        return alignment;
    }

    private void load(Element element) {
        if (element == null) {
            return;
        }
        Variables variables = getVariables();
        if (this.mHasName) {
            this.mTextWidthVar = new IndexedVariable(this.mName + ".text_width", variables, true);
            this.mTextHeightVar = new IndexedVariable(this.mName + ".text_height", variables, true);
        }
        this.mFormatter = TextFormatter.fromElement(variables, element, this.mStyle);
        this.mColorParser = ColorParser.fromElement(variables, element, this.mStyle);
        this.mMarqueeSpeed = getAttrAsInt(element, "marqueeSpeed", 0);
        this.mSpacingMult = getAttrAsFloat(element, "spacingMult", 1.0f);
        this.mSpacingAdd = getAttrAsFloat(element, "spacingAdd", 0.0f);
        this.mMarqueeGap = getAttrAsInt(element, "marqueeGap", 2);
        this.mMultiLine = Boolean.parseBoolean(getAttr(element, "multiLine"));
        this.mFontScaleEnabled = Boolean.parseBoolean(getAttr(element, "enableFontScale"));
        Expression build = Expression.build(variables, getAttr(element, "size"));
        String attr = getAttr(element, "fontFamily");
        String attr2 = getAttr(element, "fontPath");
        if (!TextUtils.isEmpty(attr)) {
            this.mPaint.setTypeface(Typeface.create(attr, parseFontStyle(getAttr(element, "fontStyle"))));
        } else if (TextUtils.isEmpty(attr2)) {
            this.mPaint.setFakeBoldText(Boolean.parseBoolean(getAttr(element, "bold")));
            ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext().mContext, R.style.Theme_Light);
            TextPaint textPaint = this.mPaint;
            textPaint.setTypeface(HideSdkDependencyUtils.TypefaceUtils_replaceTypeface(contextThemeWrapper, textPaint.getTypeface()));
        } else {
            Typeface typeface = null;
            try {
                typeface = Typeface.createFromAsset(getContext().mContext.getAssets(), attr2);
            } catch (Exception e) {
                Log.e("TextScreenElement", "create typeface from asset fail :" + e);
            }
            if (typeface != null) {
                this.mPaint.setTypeface(typeface);
            }
        }
        this.mPaint.setColor(getColor());
        this.mPaint.setTextSize(scale(18.0d));
        this.mPaint.setAntiAlias(true);
        this.mShadowRadius = getAttrAsFloat(element, "shadowRadius", 0.0f);
        this.mShadowDx = getAttrAsFloat(element, "shadowDx", 0.0f);
        this.mShadowDy = getAttrAsFloat(element, "shadowDy", 0.0f);
        this.mShadowColorParser = ColorParser.fromElement(variables, element, "shadowColor", this.mStyle);
        this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, getShadowColor());
        this.mTextSizeProperty = new PropertyWrapper(this.mName + ".textColor", getVariables(), build, isInFolmeMode(), 18.0d);
        this.mTextColorProperty = new PropertyWrapper(this.mName + ".textSize", getVariables(), null, isInFolmeMode(), this.mColorParser.getColor());
        this.mTextShadowColorProperty = new PropertyWrapper(this.mName + ".textShadowColor", getVariables(), null, isInFolmeMode(), this.mShadowColorParser.getColor());
    }

    private static int parseFontStyle(String str) {
        if (!TextUtils.isEmpty(str) && !"normal".equals(str)) {
            if ("bold".equals(str)) {
                return 1;
            }
            if ("italic".equals(str)) {
                return 2;
            }
            if ("bold_italic".equals(str)) {
                return 3;
            }
        }
        return 0;
    }

    private void updateTextSize() {
        float scale = scale(this.mTextSizeProperty.getValue());
        this.mTextSize = scale;
        if (this.mFontScaleEnabled) {
            this.mTextSize = scale * this.mRoot.getFontScale();
        }
        this.mPaint.setTextSize(this.mTextSize);
    }

    private void updateTextWidth() {
        this.mTextWidth = 0.0f;
        if (!TextUtils.isEmpty(this.mText)) {
            if (this.mMultiLine) {
                for (String str : this.mText.split("\n")) {
                    float measureText = this.mPaint.measureText(str);
                    if (measureText > this.mTextWidth) {
                        this.mTextWidth = measureText;
                    }
                }
            } else {
                this.mTextWidth = this.mPaint.measureText(this.mText);
            }
        }
        if (this.mHasName) {
            this.mTextWidthVar.set(descale(this.mTextWidth));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        if (TextUtils.isEmpty(this.mText)) {
            return;
        }
        this.mPaint.setColor(getColor());
        TextPaint textPaint = this.mPaint;
        textPaint.setAlpha(Utils.mixAlpha(textPaint.getAlpha(), getAlpha()));
        this.mPaint.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, getShadowColor());
        float width = getWidth();
        boolean z = width >= 0.0f;
        if (width < 0.0f || width > this.mTextWidth) {
            width = this.mTextWidth;
        }
        float height = getHeight();
        float textSize = this.mPaint.getTextSize();
        if (height < 0.0f) {
            height = this.mTextHeight;
        }
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        canvas.save();
        float f5 = this.mShadowRadius;
        if (f5 != 0.0f) {
            f = Math.min(0.0f, this.mShadowDx - f5);
            f2 = Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
            f3 = Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
            f4 = Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
        } else {
            f = 0.0f;
            f2 = 0.0f;
            f3 = 0.0f;
            f4 = 0.0f;
        }
        canvas.translate(left, top);
        if (z) {
            f = 0.0f;
        }
        if (z) {
            f2 = 0.0f;
        }
        canvas.clipRect(f, f3, f2 + width, height + f4);
        StaticLayout staticLayout = this.mTextLayout;
        if (staticLayout != null) {
            if (staticLayout.getLineCount() == 1 && this.mShouldMarquee) {
                int lineStart = this.mTextLayout.getLineStart(0);
                int lineEnd = this.mTextLayout.getLineEnd(0);
                int lineTop = this.mTextLayout.getLineTop(0);
                float lineLeft = this.mTextLayout.getLineLeft(0);
                float f6 = textSize + lineTop;
                canvas.drawText(this.mText, lineStart, lineEnd, lineLeft + this.mMarqueePos, f6, (Paint) this.mPaint);
                float f7 = this.mMarqueePos;
                if (f7 != 0.0f) {
                    float f8 = f7 + this.mTextWidth + (this.mTextSize * this.mMarqueeGap);
                    if (f8 < width) {
                        canvas.drawText(this.mText, lineLeft + f8, f6, this.mPaint);
                    }
                }
            } else {
                this.mTextLayout.draw(canvas);
            }
        }
        canvas.restore();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0045 A[Catch: all -> 0x00ef, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x0010, B:8:0x0012, B:10:0x0023, B:11:0x0029, B:13:0x002b, B:15:0x0039, B:21:0x0045, B:22:0x0048, B:24:0x0050, B:26:0x0054, B:30:0x005d, B:33:0x0065, B:36:0x006e, B:43:0x00b3, B:45:0x00b9, B:49:0x00de, B:46:0x00be, B:48:0x00d4, B:50:0x00e2, B:38:0x0074, B:40:0x00a5, B:41:0x00af, B:32:0x0063), top: B:60:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:40:0x00a5 A[Catch: all -> 0x00ef, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x0010, B:8:0x0012, B:10:0x0023, B:11:0x0029, B:13:0x002b, B:15:0x0039, B:21:0x0045, B:22:0x0048, B:24:0x0050, B:26:0x0054, B:30:0x005d, B:33:0x0065, B:36:0x006e, B:43:0x00b3, B:45:0x00b9, B:49:0x00de, B:46:0x00be, B:48:0x00d4, B:50:0x00e2, B:38:0x0074, B:40:0x00a5, B:41:0x00af, B:32:0x0063), top: B:60:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00b3 A[Catch: all -> 0x00ef, TryCatch #0 {, blocks: (B:4:0x0007, B:6:0x0010, B:8:0x0012, B:10:0x0023, B:11:0x0029, B:13:0x002b, B:15:0x0039, B:21:0x0045, B:22:0x0048, B:24:0x0050, B:26:0x0054, B:30:0x005d, B:33:0x0065, B:36:0x006e, B:43:0x00b3, B:45:0x00b9, B:49:0x00de, B:46:0x00be, B:48:0x00d4, B:50:0x00e2, B:38:0x0074, B:40:0x00a5, B:41:0x00af, B:32:0x0063), top: B:60:0x0007 }] */
    /* JADX WARN: Removed duplicated region for block: B:53:0x00e7  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x00ea  */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void doTick(long r19) {
        /*
            Method dump skipped, instructions count: 242
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.TextScreenElement.doTick(long):void");
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        this.mText = null;
        this.mSetText = null;
        this.mMarqueePos = Float.MAX_VALUE;
    }

    protected int getColor() {
        return isInFolmeMode() ? (int) this.mTextColorProperty.getValue() : this.mColorParser.getColor();
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getHeight() {
        float height = super.getHeight();
        return height <= 0.0f ? this.mTextHeight : height;
    }

    protected int getShadowColor() {
        return isInFolmeMode() ? (int) this.mTextShadowColorProperty.getValue() : this.mShadowColorParser.getColor();
    }

    protected String getText() {
        String str = this.mSetText;
        if (str != null) {
            return str;
        }
        String text = this.mFormatter.getText();
        if (text != null) {
            String replace = text.replace("\\n", "\n");
            return !this.mMultiLine ? replace.replace("\n", " ") : replace;
        }
        return text;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getWidth() {
        float width = super.getWidth();
        return width <= 0.0f ? this.mTextWidth : width;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement
    public void initProperties() {
        super.initProperties();
        this.mTextSizeProperty.init();
        this.mTextColorProperty.init();
        this.mTextShadowColorProperty.init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        requestFramerate((this.mShouldMarquee && z) ? 45.0f : 0.0f);
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        TextPaint textPaint = this.mPaint;
        if (textPaint != null) {
            textPaint.setColorFilter(colorFilter);
        }
    }

    public void setParams(Object... objArr) {
        this.mFormatter.setParams(objArr);
    }

    public void setText(String str) {
        this.mSetText = str;
    }
}
