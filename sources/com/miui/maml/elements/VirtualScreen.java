package com.miui.maml.elements;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.BitmapProvider;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VirtualScreen extends ElementGroup implements BitmapProvider.IBitmapHolder {
    private Bitmap mScreenBitmap;
    private Canvas mScreenCanvas;
    private boolean mTicked;
    private BitmapProvider.VersionedBitmap mVersionedBitmap;

    public VirtualScreen(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        if (this.mTicked) {
            this.mTicked = false;
            this.mScreenCanvas.save();
            this.mScreenCanvas.concat(getMatrix());
            this.mScreenCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            super.doRender(this.mScreenCanvas);
            this.mScreenCanvas.restore();
            this.mVersionedBitmap.updateVersion();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        this.mTicked = true;
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        this.mScreenBitmap.recycle();
        this.mScreenBitmap = null;
        this.mScreenCanvas = null;
    }

    public Bitmap getBitmap() {
        return this.mScreenBitmap;
    }

    @Override // com.miui.maml.elements.BitmapProvider.IBitmapHolder
    public BitmapProvider.VersionedBitmap getBitmap(String str) {
        return this.mVersionedBitmap;
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        float width = getWidth();
        if (width < 0.0f) {
            width = scale(Utils.getVariableNumber("screen_width", getVariables()));
        }
        float height = getHeight();
        if (height < 0.0f) {
            height = scale(Utils.getVariableNumber("screen_height", getVariables()));
        }
        Bitmap createBitmap = Bitmap.createBitmap(Math.round(width), Math.round(height), Bitmap.Config.ARGB_8888);
        this.mScreenBitmap = createBitmap;
        createBitmap.setDensity(this.mRoot.getTargetDensity());
        this.mScreenCanvas = new Canvas(this.mScreenBitmap);
        this.mVersionedBitmap = new BitmapProvider.VersionedBitmap(this.mScreenBitmap);
    }
}
