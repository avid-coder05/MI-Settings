package com.miui.maml.elements.filament;

import android.os.SystemClock;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import com.google.android.filament.Engine;
import com.google.android.filament.Scene;
import com.google.android.filament.View;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.utils.Float3;
import com.google.android.filament.utils.Manipulator;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import java.nio.Buffer;
import kotlin.jvm.functions.Function1;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PbrGltf extends PbrModel {
    private String mAssetPath;
    private EnvironmentLight mEnvLight;
    private String mFilePath;
    private GltfModelViewer mModelViewer;
    private long startTime;

    public PbrGltf(Element element, ResourceManager resourceManager, ScreenElementRoot screenElementRoot) {
        super(element, resourceManager, screenElementRoot);
        this.startTime = SystemClock.elapsedRealtime();
        FilamentManager.getInstance().loadAll();
        load(element);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Buffer lambda$init$0(String str) {
        return Io.readAsset(this.mResMgr, this.mAssetPath + str);
    }

    private void load(Element element) {
        this.mFilePath = element.getAttribute("gltfPath");
        this.mAssetPath = element.getAttribute("assetPath");
        this.mEnvLight = new EnvironmentLight(element, this.mResMgr);
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void finish() {
        Engine engine;
        EnvironmentLight environmentLight = this.mEnvLight;
        if (environmentLight != null && (engine = this.mEngine) != null) {
            environmentLight.onDestroy(engine);
            this.mEnvLight = null;
        }
        GltfModelViewer gltfModelViewer = this.mModelViewer;
        if (gltfModelViewer != null) {
            gltfModelViewer.destroy();
            FilamentManager.getInstance().releaseEngine();
            this.mEngine = null;
        }
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void init(View view) {
        this.mEngine = FilamentManager.getInstance().acquireEngine();
        if (view instanceof SurfaceView) {
            this.mModelViewer = new GltfModelViewer((SurfaceView) view, this.mEngine, (Manipulator) null);
        } else if (view instanceof TextureView) {
            this.mModelViewer = new GltfModelViewer((TextureView) view, this.mEngine, (Manipulator) null);
        }
        if (this.mModelViewer == null || TextUtils.isEmpty(this.mFilePath)) {
            return;
        }
        if (this.mFilePath.endsWith(".glb")) {
            this.mModelViewer.loadModelGlb(Io.readAsset(this.mResMgr, this.mFilePath));
        } else if (this.mFilePath.endsWith(".gltf") && !TextUtils.isEmpty(this.mAssetPath)) {
            this.mModelViewer.loadModelGltfAsync(Io.readAsset(this.mResMgr, this.mFilePath), new Function1() { // from class: com.miui.maml.elements.filament.PbrGltf$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    Buffer lambda$init$0;
                    lambda$init$0 = PbrGltf.this.lambda$init$0((String) obj);
                    return lambda$init$0;
                }
            });
        }
        this.mModelViewer.transformToUnitCube(new Float3(0.0f, 0.0f, -4.0f));
        Scene scene = this.mModelViewer.getScene();
        scene.setIndirectLight(this.mEnvLight.createIndirectLight(this.mEngine));
        scene.setSkybox(this.mEnvLight.createSkybox(this.mEngine));
        View.DynamicResolutionOptions dynamicResolutionOptions = this.mModelViewer.getView().getDynamicResolutionOptions();
        dynamicResolutionOptions.enabled = false;
        this.mModelViewer.getView().setDynamicResolutionOptions(dynamicResolutionOptions);
        this.mModelViewer.getView().setAmbientOcclusion(View.AmbientOcclusion.NONE);
        this.mModelViewer.getView().getBloomOptions().enabled = false;
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void render(long j) {
        GltfModelViewer gltfModelViewer = this.mModelViewer;
        if (gltfModelViewer != null) {
            Animator animator = gltfModelViewer.getAnimator();
            if (animator != null) {
                if (animator.getAnimationCount() > 0) {
                    animator.applyAnimation(0, (float) ((j - this.startTime) / 1000.0d));
                }
                animator.updateBoneMatrices();
            }
            this.mModelViewer.render(j * 1000000);
        }
    }
}
