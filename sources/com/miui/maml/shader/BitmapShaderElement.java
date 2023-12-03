package com.miui.maml.shader;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import com.miui.maml.ScreenElementRoot;
import miui.provider.Weather;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class BitmapShaderElement extends ShaderElement {
    private Bitmap mBitmap;
    private Shader.TileMode mTileModeX;
    private Shader.TileMode mTileModeY;

    public BitmapShaderElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mBitmap = this.mRoot.getContext().mResourceManager.getBitmap(element.getAttribute(Weather.AQIInfo.SRC));
        resolveTileMode(element);
        this.mShader = new BitmapShader(this.mBitmap, this.mTileModeX, this.mTileModeY);
    }

    private void resolveTileMode(Element element) {
        String[] split = element.getAttribute("tile").split(",");
        if (split.length > 1) {
            this.mTileModeX = ShaderElement.getTileMode(split[0]);
            this.mTileModeY = ShaderElement.getTileMode(split[1]);
            return;
        }
        Shader.TileMode tileMode = this.mTileMode;
        this.mTileModeY = tileMode;
        this.mTileModeX = tileMode;
    }

    @Override // com.miui.maml.shader.ShaderElement
    public void onGradientStopsChanged() {
    }

    @Override // com.miui.maml.shader.ShaderElement
    public void updateShader() {
    }

    @Override // com.miui.maml.shader.ShaderElement
    public boolean updateShaderMatrix() {
        return false;
    }
}
