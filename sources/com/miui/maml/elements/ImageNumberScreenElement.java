package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class ImageNumberScreenElement extends ImageScreenElement {
    private String LOG_TAG;
    private int mBmpHeight;
    private int mBmpWidth;
    private Bitmap mCachedBmp;
    private Canvas mCachedCanvas;
    private ArrayList<CharName> mNameMap;
    private Expression mNumExpression;
    private String mOldSrc;
    private double mPreNumber;
    private String mPreStr;
    private int mSpace;
    private Expression mSpaceExpression;
    private Expression mStrExpression;
    private String mStrValue;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class CharName {
        public char ch;
        public String name;

        public CharName(char c, String str) {
            this.ch = c;
            this.name = str;
        }
    }

    public ImageNumberScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.LOG_TAG = "ImageNumberScreenElement";
        this.mPreNumber = Double.MIN_VALUE;
        this.mNumExpression = Expression.build(getVariables(), getAttr(element, "number"));
        this.mStrExpression = Expression.build(getVariables(), getAttr(element, "string"));
        this.mSpaceExpression = Expression.build(getVariables(), getAttr(element, "space"));
        String attr = getAttr(element, "charNameMap");
        if (TextUtils.isEmpty(attr)) {
            return;
        }
        this.mNameMap = new ArrayList<>();
        for (String str : attr.split(",")) {
            this.mNameMap.add(new CharName(str.charAt(0), str.substring(1)));
        }
    }

    private String charToStr(char c) {
        ArrayList<CharName> arrayList = this.mNameMap;
        if (arrayList != null) {
            Iterator<CharName> it = arrayList.iterator();
            while (it.hasNext()) {
                CharName next = it.next();
                if (next.ch == c) {
                    return next.name;
                }
            }
        }
        return c == '.' ? "dot" : String.valueOf(c);
    }

    private Bitmap getNumberBitmap(String str, String str2) {
        return getContext().mResourceManager.getBitmap(Utils.addFileNameSuffix(str, str2));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        String str;
        super.doTick(j);
        if (this.mNumExpression == null && this.mStrExpression == null && this.mStrValue == null) {
            if (this.mCachedBmp != null) {
                this.mCachedBmp = null;
                this.mPreStr = null;
                this.mCurrentBitmap.setBitmap(null);
                updateBitmapVars();
                return;
            }
            return;
        }
        String src2 = getSrc();
        boolean z = !TextUtils.equals(src2, this.mOldSrc);
        Expression expression = this.mNumExpression;
        if (expression != null) {
            double evaluate = evaluate(expression);
            if (evaluate == this.mPreNumber && !z) {
                return;
            }
            this.mPreNumber = evaluate;
            str = Utils.doubleToString(evaluate);
        } else {
            Expression expression2 = this.mStrExpression;
            if (expression2 == null && this.mStrValue == null) {
                str = null;
            } else {
                String str2 = this.mStrValue;
                String evaluateStr = str2 != null ? str2 : evaluateStr(expression2);
                if (TextUtils.equals(evaluateStr, this.mPreStr) && !z) {
                    return;
                }
                this.mPreStr = evaluateStr;
                str = evaluateStr;
            }
        }
        Bitmap bitmap = this.mCachedBmp;
        if (bitmap != null) {
            bitmap.eraseColor(0);
        }
        this.mOldSrc = src2;
        this.mBmpWidth = 0;
        int length = str != null ? str.length() : 0;
        for (int i = 0; i < length; i++) {
            Bitmap numberBitmap = getNumberBitmap(src2, charToStr(str.charAt(i)));
            if (numberBitmap == null) {
                Log.e(this.LOG_TAG, "Fail to get bitmap for number " + String.valueOf(str.charAt(i)));
                return;
            }
            int width = this.mBmpWidth + numberBitmap.getWidth();
            int height = numberBitmap.getHeight();
            Bitmap bitmap2 = this.mCachedBmp;
            int width2 = bitmap2 == null ? 0 : bitmap2.getWidth();
            Bitmap bitmap3 = this.mCachedBmp;
            int height2 = bitmap3 == null ? 0 : bitmap3.getHeight();
            if (width > width2 || height > height2) {
                Bitmap bitmap4 = this.mCachedBmp;
                if (width > width2) {
                    int i2 = length - i;
                    width2 = this.mBmpWidth + (numberBitmap.getWidth() * i2) + (this.mSpace * (i2 - 1));
                }
                if (height <= height2) {
                    height = height2;
                }
                this.mBmpHeight = height;
                Bitmap createBitmap = Bitmap.createBitmap(width2, height, Bitmap.Config.ARGB_8888);
                this.mCachedBmp = createBitmap;
                createBitmap.setDensity(numberBitmap.getDensity());
                this.mCurrentBitmap.setBitmap(this.mCachedBmp);
                Canvas canvas = new Canvas(this.mCachedBmp);
                this.mCachedCanvas = canvas;
                if (bitmap4 != null) {
                    canvas.drawBitmap(bitmap4, 0.0f, 0.0f, (Paint) null);
                }
            }
            this.mCachedCanvas.drawBitmap(numberBitmap, this.mBmpWidth, 0.0f, (Paint) null);
            int width3 = this.mBmpWidth + numberBitmap.getWidth();
            this.mBmpWidth = width3;
            if (i < length - 1) {
                this.mBmpWidth = width3 + this.mSpace;
            }
        }
        this.mCurrentBitmap.updateVersion();
        updateBitmapVars();
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        this.mPreNumber = Double.MIN_VALUE;
        this.mPreStr = null;
    }

    @Override // com.miui.maml.elements.ImageScreenElement
    protected int getBitmapHeight() {
        return this.mBmpHeight;
    }

    @Override // com.miui.maml.elements.ImageScreenElement
    protected int getBitmapWidth() {
        return this.mBmpWidth;
    }

    @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        Expression expression = this.mSpaceExpression;
        this.mSpace = expression == null ? 0 : (int) scale(expression.evaluate());
        this.mCurrentBitmap.setBitmap(this.mCachedBmp);
    }

    @Override // com.miui.maml.elements.ImageScreenElement
    protected void updateBitmapImpl(boolean z) {
        this.mCurrentBitmap.setBitmap(this.mCachedBmp);
        updateBitmapVars();
    }
}
