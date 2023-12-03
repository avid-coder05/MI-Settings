package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.SourcesAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.BitmapProvider;
import com.miui.maml.util.TextFormatter;
import com.miui.maml.util.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import miui.graphics.BitmapFactory;
import miui.provider.MiCloudSmsCmd;
import miui.provider.Weather;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class ImageScreenElement extends AnimatedScreenElement implements BitmapProvider.IBitmapHolder {
    private boolean mAntiAlias;
    protected BitmapProvider.VersionedBitmap mBitmap;
    private BitmapProvider mBitmapProvider;
    private Bitmap mBlurBitmap;
    private int mBlurRadius;
    private IndexedVariable mBmpSizeHeightVar;
    private IndexedVariable mBmpSizeWidthVar;
    protected BitmapProvider.VersionedBitmap mCurrentBitmap;
    private Rect mDesRect;
    private Expression mExpH;
    private Expression mExpSrcH;
    private Expression mExpSrcW;
    private Expression mExpSrcX;
    private Expression mExpSrcY;
    private Expression mExpW;
    private int mH;
    private IndexedVariable mHasBitmapVar;
    private boolean mHasSrcRect;
    private boolean mHasWidthAndHeight;
    private boolean mIsLoadAsyncSet;
    private boolean mLoadAsync;
    private Paint mMaskPaint;
    private ArrayList<Mask> mMasks;
    private int mMeshHeight;
    private float[] mMeshVerts;
    private int mMeshWidth;
    protected Paint mPaint;
    private boolean mPendingBlur;
    private int mRawBlurRadius;
    private boolean mRetainWhenInvisible;
    private pair<Double, Double> mRotateXYpair;
    private SourcesAnimation mSources;
    private String mSrc;
    private TextFormatter mSrcFormatter;
    private int mSrcH;
    private Expression mSrcIdExpression;
    private Rect mSrcRect;
    private int mSrcW;
    private int mSrcX;
    private int mSrcY;
    private int mW;
    private Expression mXfermodeNumExp;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class Mask extends ImageScreenElement {
        private boolean mAlignAbsolute;

        public Mask(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
            if (getAttr(element, "align").equalsIgnoreCase("absolute")) {
                this.mAlignAbsolute = true;
            }
        }

        @Override // com.miui.maml.elements.ImageScreenElement, com.miui.maml.elements.ScreenElement
        protected void doRender(Canvas canvas) {
        }

        public final boolean isAlignAbsolute() {
            return this.mAlignAbsolute;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class pair<T1, T2> {
        public T1 p1;
        public T2 p2;

        private pair() {
        }
    }

    public ImageScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mBitmap = new BitmapProvider.VersionedBitmap(null);
        this.mCurrentBitmap = new BitmapProvider.VersionedBitmap(null);
        this.mPaint = new Paint();
        this.mMaskPaint = new Paint();
        this.mDesRect = new Rect();
        this.mW = -1;
        this.mH = -1;
        load(element);
    }

    private void load(Element element) {
        if (element == null) {
            return;
        }
        Variables variables = getVariables();
        this.mSrcFormatter = TextFormatter.fromElement(variables, element, Weather.AQIInfo.SRC, "srcFormat", "srcParas", "srcExp", "srcFormatExp");
        this.mSrcIdExpression = Expression.build(variables, getAttr(element, "srcid"));
        boolean z = !getAttr(element, "antiAlias").equals("false");
        this.mAntiAlias = z;
        this.mPaint.setFilterBitmap(z);
        this.mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mMaskPaint.setFilterBitmap(this.mAntiAlias);
        this.mExpSrcX = Expression.build(variables, getAttr(element, "srcX"));
        this.mExpSrcY = Expression.build(variables, getAttr(element, "srcY"));
        this.mExpSrcW = Expression.build(variables, getAttr(element, "srcW"));
        this.mExpSrcH = Expression.build(variables, getAttr(element, "srcH"));
        this.mExpW = Expression.build(variables, getAttr(element, MiCloudSmsCmd.TYPE_WIPE));
        this.mExpH = Expression.build(variables, getAttr(element, "h"));
        if (this.mExpSrcW != null && this.mExpSrcH != null) {
            this.mHasSrcRect = true;
            this.mSrcRect = new Rect();
        }
        if (this.mExpH != null && this.mExpW != null) {
            this.mHasWidthAndHeight = true;
        }
        this.mRawBlurRadius = getAttrAsInt(element, "blur", 0);
        loadMesh(element);
        Expression build = Expression.build(variables, getAttr(element, "xfermodeNum"));
        this.mXfermodeNumExp = build;
        if (build == null) {
            this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode(getAttr(element, "xfermode"))));
        }
        boolean parseBoolean = Boolean.parseBoolean(getAttr(element, "useVirtualScreen"));
        String attr = getAttr(element, "srcType");
        if (parseBoolean) {
            attr = "VirtualScreen";
        }
        setSrcType(attr);
        String attr2 = getAttr(element, "loadAsync");
        if (!TextUtils.isEmpty(attr2)) {
            this.mLoadAsync = Boolean.parseBoolean(attr2);
            this.mIsLoadAsyncSet = true;
        }
        this.mRetainWhenInvisible = Boolean.parseBoolean(getAttr(element, "retainWhenInvisible"));
        if (this.mHasName) {
            this.mBmpSizeWidthVar = new IndexedVariable(this.mName + ".bmp_width", variables, true);
            this.mBmpSizeHeightVar = new IndexedVariable(this.mName + ".bmp_height", variables, true);
            this.mHasBitmapVar = new IndexedVariable(this.mName + ".has_bitmap", variables, true);
        }
        loadMask(element);
    }

    private void loadMask(Element element) {
        if (this.mMasks == null) {
            this.mMasks = new ArrayList<>();
        }
        this.mMasks.clear();
        NodeList elementsByTagName = element.getElementsByTagName("Mask");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            this.mMasks.add(new Mask((Element) elementsByTagName.item(i), this.mRoot));
        }
    }

    private void renderWithMask(Canvas canvas, Mask mask, int i, int i2) {
        double sin;
        double cos;
        Bitmap bitmap = getContext().mResourceManager.getBitmap(mask.getSrc());
        if (bitmap == null) {
            return;
        }
        canvas.save();
        double x = mask.getX();
        double y = mask.getY();
        float rotation = mask.getRotation();
        if (mask.isAlignAbsolute()) {
            float rotation2 = getRotation();
            if (rotation2 == 0.0f) {
                sin = x - i;
                cos = y - i2;
            } else {
                float f = rotation - rotation2;
                double d = (rotation2 * 3.1415926535897d) / 180.0d;
                float pivotX = getPivotX();
                float pivotY = getPivotY();
                if (this.mRotateXYpair == null) {
                    this.mRotateXYpair = new pair<>();
                }
                rotateXY(pivotX, pivotY, d, this.mRotateXYpair);
                double doubleValue = i + this.mRotateXYpair.p1.doubleValue();
                double doubleValue2 = i2 + this.mRotateXYpair.p2.doubleValue();
                rotateXY(descale(mask.getPivotX()), descale(mask.getPivotY()), (mask.getRotation() * 3.1415926535897d) / 180.0d, this.mRotateXYpair);
                double scale = (x + scale(this.mRotateXYpair.p1.doubleValue())) - doubleValue;
                double scale2 = (y + scale(this.mRotateXYpair.p2.doubleValue())) - doubleValue2;
                double sqrt = Math.sqrt((scale * scale) + (scale2 * scale2));
                double asin = Math.asin(scale / sqrt);
                double d2 = scale2 > 0.0d ? d + asin : (d + 3.1415926535897d) - asin;
                sin = sqrt * Math.sin(d2);
                cos = sqrt * Math.cos(d2);
                rotation = f;
            }
            x = sin - getX();
            y = cos - getY();
        }
        canvas.rotate(rotation, (float) (mask.getPivotX() + x + i), (float) (mask.getPivotY() + y + i2));
        int i3 = (int) x;
        int i4 = (int) y;
        int round = Math.round(mask.getWidth());
        if (round < 0) {
            round = bitmap.getWidth();
        }
        int round2 = Math.round(mask.getHeight());
        if (round2 < 0) {
            round2 = bitmap.getHeight();
        }
        int i5 = i3 + i;
        int i6 = i4 + i2;
        this.mDesRect.set(i5, i6, round + i5, round2 + i6);
        this.mMaskPaint.setAlpha(mask.getAlpha());
        canvas.drawBitmap(bitmap, (Rect) null, this.mDesRect, this.mMaskPaint);
        canvas.restore();
    }

    /* JADX WARN: Type inference failed for: r6v1, types: [java.lang.Double, T1, T2] */
    /* JADX WARN: Type inference failed for: r6v2, types: [java.lang.Double, T1] */
    /* JADX WARN: Type inference failed for: r6v4, types: [java.lang.Double, T2] */
    private void rotateXY(double d, double d2, double d3, pair<Double, Double> pairVar) {
        double sqrt = Math.sqrt((d * d) + (d2 * d2));
        ?? valueOf = Double.valueOf(0.0d);
        if (sqrt <= 0.0d) {
            pairVar.p1 = valueOf;
            pairVar.p2 = valueOf;
            return;
        }
        double acos = (3.1415926535897d - Math.acos(d / sqrt)) - d3;
        pairVar.p1 = Double.valueOf(d + (Math.cos(acos) * sqrt));
        pairVar.p2 = Double.valueOf(d2 - (sqrt * Math.sin(acos)));
    }

    private void updateBitmap(boolean z) {
        updateBitmapImpl(z);
        if (this.mIsLoadAsyncSet || z) {
            return;
        }
        this.mLoadAsync = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        int i;
        int i2;
        int i3;
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap == null) {
            return;
        }
        if (this.mPendingBlur) {
            if (this.mBlurBitmap == null || bitmap.getWidth() != this.mBlurBitmap.getWidth() || bitmap.getHeight() != this.mBlurBitmap.getHeight()) {
                this.mBlurBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            }
            this.mPendingBlur = false;
            this.mBlurBitmap = BitmapFactory.fastBlur(bitmap, this.mBlurBitmap, this.mBlurRadius);
        }
        Bitmap bitmap2 = this.mBlurBitmap;
        Bitmap bitmap3 = (bitmap2 == null || this.mBlurRadius <= 0) ? bitmap : bitmap2;
        this.mPaint.setAlpha(getAlpha());
        int density = canvas.getDensity();
        canvas.setDensity(0);
        float width = getWidth();
        float height = getHeight();
        float width2 = super.getWidth();
        float height2 = super.getHeight();
        if (width == 0.0f || height == 0.0f) {
            return;
        }
        int left = (int) getLeft(0.0f, width);
        int top = (int) getTop(0.0f, height);
        canvas.save();
        if (this.mMasks.size() != 0) {
            float f = left;
            float f2 = top;
            canvas.saveLayer(f, f2, ((int) Math.ceil(width)) + left, ((int) Math.ceil(height)) + top, this.mPaint, 31);
            if (width2 > 0.0f || height2 > 0.0f || this.mSrcRect != null) {
                i = left;
                i2 = top;
                this.mDesRect.set(i, i2, i + ((int) width), i2 + ((int) height));
                Rect rect = this.mSrcRect;
                if (rect != null) {
                    int i4 = this.mSrcX;
                    int i5 = this.mSrcY;
                    rect.set(i4, i5, this.mSrcW + i4, this.mSrcH + i5);
                }
                canvas.drawBitmap(bitmap3, this.mSrcRect, this.mDesRect, this.mPaint);
            } else {
                canvas.drawBitmap(bitmap3, f, f2, this.mPaint);
                i2 = top;
                i = left;
            }
            Iterator<Mask> it = this.mMasks.iterator();
            while (it.hasNext()) {
                renderWithMask(canvas, it.next(), i, i2);
            }
            canvas.restore();
        } else if (bitmap3.getNinePatchChunk() != null) {
            NinePatch ninePatch = getContext().mResourceManager.getNinePatch(getSrc());
            if (ninePatch != null) {
                this.mDesRect.set(left, top, (int) (left + width), (int) (top + height));
                ninePatch.draw(canvas, this.mDesRect, this.mPaint);
            } else {
                Log.e("ImageScreenElement", "the image contains ninepatch chunk but couldn't get NinePatch object: " + getSrc());
            }
        } else if (width2 > 0.0f || height2 > 0.0f || this.mSrcRect != null) {
            this.mDesRect.set(left, top, (int) (left + width), (int) (top + height));
            Rect rect2 = this.mSrcRect;
            if (rect2 != null) {
                int i6 = this.mSrcX;
                int i7 = this.mSrcY;
                rect2.set(i6, i7, this.mSrcW + i6, this.mSrcH + i7);
            }
            canvas.drawBitmap(bitmap3, this.mSrcRect, this.mDesRect, this.mPaint);
        } else {
            int i8 = this.mMeshWidth;
            if (i8 <= 0 || (i3 = this.mMeshHeight) <= 0) {
                canvas.drawBitmap(bitmap3, left, top, this.mPaint);
            } else {
                canvas.drawBitmapMesh(bitmap3, i8, i3, this.mMeshVerts, 0, null, 0, this.mPaint);
            }
        }
        canvas.restore();
        canvas.setDensity(density);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        if (isVisible()) {
            TextFormatter textFormatter = this.mSrcFormatter;
            this.mSrc = textFormatter != null ? textFormatter.getText() : null;
            ArrayList<Mask> arrayList = this.mMasks;
            if (arrayList != null) {
                Iterator<Mask> it = arrayList.iterator();
                while (it.hasNext()) {
                    it.next().doTick(j);
                }
            }
            Expression expression = this.mXfermodeNumExp;
            if (expression != null) {
                this.mPaint.setXfermode(new PorterDuffXfermode(Utils.getPorterDuffMode((int) expression.evaluate())));
            }
            if (this.mHasSrcRect) {
                this.mSrcX = (int) scale(evaluate(this.mExpSrcX));
                this.mSrcY = (int) scale(evaluate(this.mExpSrcY));
                this.mSrcW = (int) scale(evaluate(this.mExpSrcW));
                this.mSrcH = (int) scale(evaluate(this.mExpSrcH));
            }
            if (this.mHasWidthAndHeight) {
                this.mW = (int) scale(evaluate(this.mExpW));
                this.mH = (int) scale(evaluate(this.mExpH));
            }
            if (this.mTintChanged) {
                this.mPaint.setColorFilter(this.mTintFilter);
            }
            updateBitmap(this.mLoadAsync);
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.finish();
        }
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().finish();
            }
        }
        this.mBitmap.reset();
        this.mCurrentBitmap.reset();
        this.mBlurBitmap = null;
    }

    @Override // com.miui.maml.elements.BitmapProvider.IBitmapHolder
    public BitmapProvider.VersionedBitmap getBitmap(String str) {
        return this.mCurrentBitmap;
    }

    protected BitmapProvider.VersionedBitmap getBitmap(boolean z) {
        if (this.mBitmap.getBitmap() != null) {
            return this.mBitmap;
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            return bitmapProvider.getBitmap(getSrc(), !z, this.mW, this.mH);
        }
        return null;
    }

    protected int getBitmapHeight() {
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap != null) {
            return bitmap.getHeight();
        }
        return 0;
    }

    protected int getBitmapWidth() {
        Bitmap bitmap = this.mCurrentBitmap.getBitmap();
        if (bitmap != null) {
            return bitmap.getWidth();
        }
        return 0;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getHeight() {
        float height = super.getHeight();
        if (height >= 0.0f) {
            return height;
        }
        return this.mHasSrcRect ? this.mSrcH : getBitmapHeight();
    }

    public final String getSrc() {
        Expression expression;
        SourcesAnimation sourcesAnimation = this.mSources;
        String src2 = sourcesAnimation != null ? sourcesAnimation.getSrc() : this.mSrc;
        return (src2 == null || (expression = this.mSrcIdExpression) == null) ? src2 : Utils.addFileNameSuffix(src2, String.valueOf((long) expression.evaluate()));
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getWidth() {
        float width = super.getWidth();
        if (width >= 0.0f) {
            return width;
        }
        return this.mHasSrcRect ? this.mSrcW : getBitmapWidth();
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getX() {
        float x = super.getX();
        SourcesAnimation sourcesAnimation = this.mSources;
        return sourcesAnimation != null ? x + scale(sourcesAnimation.getX()) : x;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement
    public float getY() {
        float y = super.getY();
        SourcesAnimation sourcesAnimation = this.mSources;
        return sourcesAnimation != null ? y + scale(sourcesAnimation.getY()) : y;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        TextFormatter textFormatter = this.mSrcFormatter;
        this.mSrc = textFormatter != null ? textFormatter.getText() : null;
        this.mBitmap.reset();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().init();
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.init(getSrc());
        }
        if (isVisible()) {
            updateBitmap(this.mLoadAsync);
        }
        int scale = (int) scale(this.mRawBlurRadius);
        this.mBlurRadius = scale;
        if (scale > 0) {
            this.mPendingBlur = true;
        }
    }

    protected void loadMesh(Element element) {
        String attr = getAttr(element, "mesh");
        int indexOf = attr.indexOf(",");
        if (indexOf != -1) {
            try {
                this.mMeshWidth = Integer.parseInt(attr.substring(0, indexOf));
                this.mMeshHeight = Integer.parseInt(attr.substring(indexOf + 1));
            } catch (NumberFormatException unused) {
                Log.w("ImageScreenElement", "Invalid mesh format:" + attr);
            }
            if (this.mMeshWidth == 0 || this.mMeshHeight == 0) {
                return;
            }
            String attr2 = getAttr(element, "meshVertsArr");
            Object obj = getVariables().get(attr2);
            if (obj != null && (obj instanceof float[])) {
                this.mMeshVerts = (float[]) obj;
                return;
            }
            this.mMeshHeight = 0;
            this.mMeshWidth = 0;
            Log.w("ImageScreenElement", "Invalid meshVertsArr:" + attr2 + "  undifined or not float[] type");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if ("SourcesAnimation".equals(str)) {
            SourcesAnimation sourcesAnimation = new SourcesAnimation(element, this);
            this.mSources = sourcesAnimation;
            return sourcesAnimation;
        }
        return super.onCreateAnimation(str, element);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onSetAnimBefore() {
        super.onSetAnimBefore();
        this.mSources = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof SourcesAnimation) {
            this.mSources = (SourcesAnimation) baseAnimation;
        } else {
            super.onSetAnimEnable(baseAnimation);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        if (z) {
            updateBitmap(this.mLoadAsync);
        } else if (this.mRetainWhenInvisible) {
        } else {
            BitmapProvider bitmapProvider = this.mBitmapProvider;
            if (bitmapProvider != null) {
                bitmapProvider.finish();
            }
            this.mCurrentBitmap.reset();
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().pauseAnim(j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().playAnim(j, j2, j3, z, z2);
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.reset();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().reset(j);
            }
        }
        BitmapProvider bitmapProvider = this.mBitmapProvider;
        if (bitmapProvider != null) {
            bitmapProvider.reset();
        }
        if (this.mBlurRadius > 0) {
            this.mPendingBlur = true;
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        ArrayList<Mask> arrayList = this.mMasks;
        if (arrayList != null) {
            Iterator<Mask> it = arrayList.iterator();
            while (it.hasNext()) {
                it.next().resumeAnim(j);
            }
        }
    }

    public void setBitmap(Bitmap bitmap) {
        if (bitmap != this.mBitmap.getBitmap()) {
            this.mBitmap.setBitmap(bitmap);
            updateBitmap(this.mLoadAsync);
            requestUpdate();
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }

    public void setSrc(String str) {
        TextFormatter textFormatter = this.mSrcFormatter;
        if (textFormatter != null) {
            textFormatter.setText(str);
        }
    }

    public void setSrcId(double d) {
        Expression expression = this.mSrcIdExpression;
        if (expression == null || !(expression instanceof Expression.NumberExpression)) {
            this.mSrcIdExpression = new Expression.NumberExpression(String.valueOf(d));
        } else {
            ((Expression.NumberExpression) expression).setValue(d);
        }
    }

    public void setSrcType(String str) {
        this.mBitmapProvider = BitmapProvider.create(this.mRoot, str);
    }

    protected void updateBitmapImpl(boolean z) {
        BitmapProvider.VersionedBitmap bitmap = getBitmap(z);
        if (this.mBlurRadius > 0 && !BitmapProvider.VersionedBitmap.equals(bitmap, this.mCurrentBitmap)) {
            this.mPendingBlur = true;
        }
        this.mCurrentBitmap.set(bitmap);
        updateBitmapVars();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateBitmapVars() {
        if (this.mHasName) {
            this.mBmpSizeWidthVar.set(descale(getBitmapWidth()));
            this.mBmpSizeHeightVar.set(descale(getBitmapHeight()));
            this.mHasBitmapVar.set(this.mCurrentBitmap.getBitmap() != null ? 1.0d : 0.0d);
        }
    }
}
