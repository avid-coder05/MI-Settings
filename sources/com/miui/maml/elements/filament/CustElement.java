package com.miui.maml.elements.filament;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import com.google.android.filament.ColorGrading;
import com.google.android.filament.Engine;
import com.google.android.filament.IndirectLight;
import com.google.android.filament.Material;
import com.google.android.filament.MaterialInstance;
import com.google.android.filament.Renderer;
import com.google.android.filament.Scene;
import com.google.android.filament.Skybox;
import com.google.android.filament.View;
import com.google.android.filament.Viewport;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.filament.UniformFactory;
import com.miui.maml.util.Utils;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class CustElement {
    private Context mContext;
    protected Engine mEngine;
    protected EnvironmentLight mEnvLight;
    protected View mFilamentView;
    private ResourceManager mManager;
    private Material mMaterial;
    private MaterialInstance mMaterialInstance;
    private String mMaterialPath;
    protected Mesh mMesh;
    private String mMeshPath;
    private String mName;
    protected PbrCamera mPbrCamera;
    protected PbrLight mPbrLight;
    protected Scene mScene;
    private String mToneMapping;
    private ArrayMap<String, UniformFactory.Uniform> mUniforms = new ArrayMap<>();

    public CustElement(Element element, ResourceManager resourceManager, final ScreenElementRoot screenElementRoot) {
        this.mContext = screenElementRoot.getContext().mContext.getApplicationContext();
        this.mMaterialPath = element.getAttribute("matPath");
        this.mMeshPath = element.getAttribute("meshPath");
        this.mEnvLight = new EnvironmentLight(element, resourceManager);
        this.mPbrLight = new PbrLight(element, screenElementRoot.getVariables());
        this.mPbrCamera = new PbrCamera(element, screenElementRoot.getVariables());
        this.mName = element.getAttribute("name");
        this.mToneMapping = element.getAttribute("toneMapping");
        Utils.traverseXmlElementChildren(element, "Uniform", new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.filament.CustElement.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                UniformFactory.Uniform createUniform = UniformFactory.createUniform(element2, screenElementRoot.getVariables(), CustElement.this.mContext);
                if (createUniform != null) {
                    CustElement.this.mUniforms.put(createUniform.getName(), createUniform);
                }
            }
        });
    }

    private void setToneMapping() {
        if (TextUtils.isEmpty(this.mToneMapping)) {
            return;
        }
        ColorGrading.ToneMapping toneMapping = ColorGrading.ToneMapping.ACES_LEGACY;
        String str = this.mToneMapping;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1503305114:
                if (str.equals("uchimura")) {
                    c = 0;
                    break;
                }
                break;
            case -1468228736:
                if (str.equals("display_range")) {
                    c = 1;
                    break;
                }
                break;
            case -1274498658:
                if (str.equals("filmic")) {
                    c = 2;
                    break;
                }
                break;
            case -1102672091:
                if (str.equals("linear")) {
                    c = 3;
                    break;
                }
                break;
            case -628682877:
                if (str.equals("reinhard")) {
                    c = 4;
                    break;
                }
                break;
            case 2988112:
                if (str.equals("aces")) {
                    c = 5;
                    break;
                }
                break;
            case 1806720664:
                if (str.equals("aces_legacy")) {
                    c = 6;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                toneMapping = ColorGrading.ToneMapping.UCHIMURA;
                break;
            case 1:
                toneMapping = ColorGrading.ToneMapping.DISPLAY_RANGE;
                break;
            case 2:
                toneMapping = ColorGrading.ToneMapping.FILMIC;
                break;
            case 3:
                toneMapping = ColorGrading.ToneMapping.LINEAR;
                break;
            case 4:
                toneMapping = ColorGrading.ToneMapping.REINHARD;
                break;
            case 5:
                toneMapping = ColorGrading.ToneMapping.ACES;
                break;
            case 6:
                toneMapping = ColorGrading.ToneMapping.ACES_LEGACY;
                break;
        }
        this.mFilamentView.setColorGrading(new ColorGrading.Builder().toneMapping(toneMapping).build(this.mEngine));
    }

    public void doFrame(Renderer renderer) {
        int size = this.mUniforms.size();
        for (int i = 0; i < size; i++) {
            UniformFactory.Uniform valueAt = this.mUniforms.valueAt(i);
            if (valueAt.isAutoRefresh()) {
                valueAt.refresh();
            }
        }
        View view = this.mFilamentView;
        if (view == null || renderer == null) {
            return;
        }
        renderer.render(view);
    }

    public String getName() {
        return this.mName;
    }

    public void init(Engine engine, ResourceManager resourceManager, android.view.View view) {
        this.mManager = resourceManager;
        this.mEngine = engine;
        this.mFilamentView = engine.createView();
        this.mScene = engine.createScene();
        this.mFilamentView.setCamera(this.mPbrCamera.createCamera(engine));
        this.mFilamentView.setScene(this.mScene);
        this.mFilamentView.setViewport(new Viewport(0, 0, view.getWidth(), view.getHeight()));
        this.mFilamentView.setSampleCount(1);
        setToneMapping();
        ByteBuffer readAsset = Io.readAsset(resourceManager, this.mMaterialPath);
        if (readAsset != null) {
            Material build = new Material.Builder().payload(readAsset, readAsset.remaining()).build(engine);
            this.mMaterial = build;
            this.mMaterialInstance = build.createInstance();
        }
        if (TextUtils.isEmpty(this.mMeshPath)) {
            this.mMesh = MeshLoaderKt.loadDefaultMesh(this.mMaterialInstance, engine);
        } else {
            HashMap hashMap = new HashMap();
            hashMap.put("DefaultMaterial", this.mMaterialInstance);
            this.mMesh = MeshLoaderKt.loadMesh(resourceManager, this.mMeshPath, hashMap, engine);
        }
        this.mScene.addEntity(this.mMesh.getRenderable());
        this.mScene.setSkybox((Skybox) null);
        this.mScene.setIndirectLight((IndirectLight) null);
        this.mScene.addEntity(this.mPbrLight.createLight(engine));
        int size = this.mUniforms.size();
        for (int i = 0; i < size; i++) {
            this.mUniforms.valueAt(i).init(resourceManager, engine, this.mMaterialInstance);
        }
    }

    public void onDestroy(Engine engine) {
        int size = this.mUniforms.size();
        for (int i = 0; i < size; i++) {
            try {
                this.mUniforms.valueAt(i).finish();
            } catch (Exception unused) {
            }
        }
        this.mEnvLight.onDestroy(engine);
        this.mPbrLight.onDestroy(engine);
        this.mPbrCamera.onDestroy(engine);
        Mesh mesh = this.mMesh;
        if (mesh != null) {
            MeshLoaderKt.destroyMesh(engine, mesh);
            this.mMesh = null;
        }
        MaterialInstance materialInstance = this.mMaterialInstance;
        if (materialInstance != null) {
            engine.destroyMaterialInstance(materialInstance);
            this.mMaterialInstance = null;
        }
        Material material = this.mMaterial;
        if (material != null) {
            engine.destroyMaterial(material);
            this.mMaterial = null;
        }
        Scene scene = this.mScene;
        if (scene != null) {
            engine.destroyScene(scene);
            this.mScene = null;
        }
        View view = this.mFilamentView;
        if (view != null) {
            engine.destroyView(view);
            this.mFilamentView = null;
        }
    }

    public void pause() {
        int size = this.mUniforms.size();
        for (int i = 0; i < size; i++) {
            this.mUniforms.valueAt(i).tryPause();
        }
    }

    public void resume() {
        int size = this.mUniforms.size();
        for (int i = 0; i < size; i++) {
            this.mUniforms.valueAt(i).tryResume();
        }
    }

    public void updateUniform(String str, boolean z, Expression[] expressionArr, ArrayMap<String, CustFrameBuffer> arrayMap) {
        UniformFactory.Uniform uniform = this.mUniforms.get(str);
        if (uniform != null) {
            uniform.updateUniform(z, expressionArr);
        }
        uniform.init(this.mManager, this.mEngine, this.mMaterialInstance);
        if (uniform instanceof UniformFactory.OffscreenUniform) {
            ((UniformFactory.OffscreenUniform) uniform).checkOffscreen(arrayMap);
        }
        uniform.refresh();
    }
}
