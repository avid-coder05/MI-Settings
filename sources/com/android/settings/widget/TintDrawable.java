package com.android.settings.widget;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import com.android.settings.R$styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes2.dex */
public class TintDrawable extends DrawableWrapper {
    private int[] mThemeAttrs;
    private ColorStateList mTint;

    public TintDrawable() {
        super(null);
    }

    private void applyTint() {
        if (getDrawable() == null || this.mTint == null) {
            return;
        }
        getDrawable().mutate().setTintList(this.mTint);
    }

    private void updateStateFromTypedArray(TypedArray typedArray) {
        int i = R$styleable.TintDrawable_android_drawable;
        if (typedArray.hasValue(i)) {
            setDrawable(typedArray.getDrawable(i));
        }
        int i2 = R$styleable.TintDrawable_android_tint;
        if (typedArray.hasValue(i2)) {
            this.mTint = typedArray.getColorStateList(i2);
        }
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        int[] iArr = this.mThemeAttrs;
        if (iArr != null) {
            TypedArray resolveAttributes = theme.resolveAttributes(iArr, R$styleable.TintDrawable);
            updateStateFromTypedArray(resolveAttributes);
            resolveAttributes.recycle();
        }
        applyTint();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        int[] iArr = this.mThemeAttrs;
        return (iArr != null && iArr.length > 0) || super.canApplyTheme();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        TypedArray obtainAttributes = DrawableWrapper.obtainAttributes(resources, theme, attributeSet, R$styleable.TintDrawable);
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        this.mThemeAttrs = obtainAttributes.extractThemeAttrs();
        updateStateFromTypedArray(obtainAttributes);
        obtainAttributes.recycle();
        applyTint();
    }
}
