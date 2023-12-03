package com.miui.maml.elements.filament;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import com.google.android.filament.Engine;
import com.google.android.filament.IndirectLight;
import com.google.android.filament.Skybox;
import com.google.android.filament.Texture;
import com.google.android.filament.utils.KtxLoader;
import com.miui.maml.ResourceManager;
import com.miui.maml.util.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class EnvironmentLight {
    private IndirectLight mIndirectLight;
    private Texture mIndirectLightTexture;
    private float mLightIntensity;
    private String mPath;
    private ResourceManager mResMgr;
    private Skybox mSkybox;
    private float[] mSkyboxColor;
    private Texture mSkyboxTexture;
    private String mType;

    public EnvironmentLight(Element element, ResourceManager resourceManager) {
        this.mResMgr = resourceManager;
        this.mPath = element.getAttribute("envSrc");
        this.mType = element.getAttribute("envType");
        this.mLightIntensity = Utils.getAttrAsFloat(element, "envIntensity", 30000.0f);
        String attribute = element.getAttribute("skyboxColor");
        if (TextUtils.isEmpty(attribute)) {
            return;
        }
        String[] split = attribute.split(",");
        if (split.length == 4) {
            this.mSkyboxColor = new float[4];
            for (int i = 0; i < 4; i++) {
                this.mSkyboxColor[i] = Float.parseFloat(split[i]);
            }
        }
    }

    private boolean loadCubemap(Texture texture, String str, Engine engine, String str2, int i) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPremultiplied = false;
        int width = texture.getWidth(i) * texture.getHeight(i) * 4;
        int[] iArr = {0, width, width * 2, width * 3, width * 4, width * 5};
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(width * 6);
        String[] strArr = {"px", "nx", "py", "ny", "pz", "nz"};
        for (int i2 = 0; i2 < 6; i2++) {
            InputStream inputStream = this.mResMgr.getInputStream(str + "/" + str2 + strArr[i2] + ".rgb32f");
            if (inputStream != null) {
                Bitmap decodeStream = BitmapFactory.decodeStream(inputStream, null, options);
                if (decodeStream != null) {
                    decodeStream.copyPixelsToBuffer(allocateDirect);
                }
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
        allocateDirect.flip();
        texture.setImage(engine, i, new Texture.PixelBufferDescriptor(allocateDirect, Texture.Format.RGB, Texture.Type.UINT_10F_11F_11F_REV), iArr);
        return true;
    }

    private Pair peekSize(String str) {
        InputStream inputStream = this.mResMgr.getInputStream(str);
        if (inputStream != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            try {
                inputStream.close();
                return new Pair(Integer.valueOf(options.outWidth), Integer.valueOf(options.outHeight));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public IndirectLight createIndirectLight(Engine engine) {
        String str = this.mType;
        str.hashCode();
        if (str.equals("rgb32f")) {
            Pair peekSize = peekSize(this.mPath + "/m0_nx.rgb32f");
            if (peekSize != null) {
                int intValue = ((int) (((Integer) peekSize.first).intValue() / Math.log(2.0d))) + 1;
                this.mIndirectLightTexture = new Texture.Builder().width(((Integer) peekSize.first).intValue()).height(((Integer) peekSize.second).intValue()).levels(intValue).format(Texture.InternalFormat.R11F_G11F_B10F).sampler(Texture.Sampler.SAMPLER_CUBEMAP).build(engine);
                int i = 0;
                while (true) {
                    if (i >= intValue) {
                        break;
                    }
                    if (!loadCubemap(this.mIndirectLightTexture, this.mPath, engine, "m" + i + '_', i)) {
                        Log.e("EnvironmentLight", "Unable to load cubemap.");
                        break;
                    }
                    i++;
                }
                this.mIndirectLight = new IndirectLight.Builder().reflections(this.mIndirectLightTexture).intensity(this.mLightIntensity).build(engine);
            }
        } else if (str.equals("ktx")) {
            ByteBuffer readAsset = Io.readAsset(this.mResMgr, this.mPath + "_ibl.ktx");
            if (readAsset != null) {
                IndirectLight createIndirectLight = KtxLoader.INSTANCE.createIndirectLight(engine, readAsset, new KtxLoader.Options());
                this.mIndirectLight = createIndirectLight;
                createIndirectLight.setIntensity(this.mLightIntensity);
            }
        }
        return this.mIndirectLight;
    }

    public Skybox createSkybox(Engine engine) {
        String str = this.mType;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1081415738:
                if (str.equals("manual")) {
                    c = 0;
                    break;
                }
                break;
            case -933151238:
                if (str.equals("rgb32f")) {
                    c = 1;
                    break;
                }
                break;
            case 106543:
                if (str.equals("ktx")) {
                    c = 2;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.mSkyboxColor != null) {
                    this.mSkybox = new Skybox.Builder().color(this.mSkyboxColor).build(engine);
                    break;
                }
                break;
            case 1:
                Pair peekSize = peekSize(this.mPath + "/nx.rgb32f");
                if (peekSize != null) {
                    Texture build = new Texture.Builder().width(((Integer) peekSize.first).intValue()).height(((Integer) peekSize.second).intValue()).levels(1).format(Texture.InternalFormat.R11F_G11F_B10F).sampler(Texture.Sampler.SAMPLER_CUBEMAP).build(engine);
                    this.mSkyboxTexture = build;
                    if (loadCubemap(build, this.mPath, engine, "", 0)) {
                        this.mSkybox = new Skybox.Builder().environment(this.mSkyboxTexture).build(engine);
                        break;
                    }
                }
                break;
            case 2:
                ByteBuffer readAsset = Io.readAsset(this.mResMgr, this.mPath + "_skybox.ktx");
                if (readAsset != null) {
                    this.mSkybox = KtxLoader.INSTANCE.createSkybox(engine, readAsset, new KtxLoader.Options());
                    break;
                }
                break;
        }
        return this.mSkybox;
    }

    public void onDestroy(Engine engine) {
        IndirectLight indirectLight = this.mIndirectLight;
        if (indirectLight != null) {
            engine.destroyIndirectLight(indirectLight);
            this.mIndirectLight = null;
        }
        Texture texture = this.mIndirectLightTexture;
        if (texture != null) {
            engine.destroyTexture(texture);
            this.mIndirectLightTexture = null;
        }
        Skybox skybox = this.mSkybox;
        if (skybox != null) {
            engine.destroySkybox(skybox);
            this.mSkybox = null;
        }
        Texture texture2 = this.mSkyboxTexture;
        if (texture2 != null) {
            engine.destroyTexture(texture2);
            this.mSkyboxTexture = null;
        }
    }
}
