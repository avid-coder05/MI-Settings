package com.miui.maml.elements.filament;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import com.google.android.filament.Box;
import com.google.android.filament.Camera;
import com.google.android.filament.Colors;
import com.google.android.filament.Engine;
import com.google.android.filament.Entity;
import com.google.android.filament.EntityManager;
import com.google.android.filament.LightManager;
import com.google.android.filament.Renderer;
import com.google.android.filament.Scene;
import com.google.android.filament.SwapChain;
import com.google.android.filament.TransformManager;
import com.google.android.filament.android.DisplayHelper;
import com.google.android.filament.android.UiHelper;
import com.google.android.filament.gltfio.Animator;
import com.google.android.filament.gltfio.AssetLoader;
import com.google.android.filament.gltfio.FilamentAsset;
import com.google.android.filament.gltfio.MaterialProvider;
import com.google.android.filament.gltfio.ResourceLoader;
import com.google.android.filament.utils.Float3;
import com.google.android.filament.utils.GestureDetector;
import com.google.android.filament.utils.Manipulator;
import com.google.android.filament.utils.MatrixKt;
import java.nio.Buffer;
import java.util.List;
import java.util.concurrent.CancellationException;
import kotlin.Unit;
import kotlin.collections.ArraysKt___ArraysKt;
import kotlin.collections.CollectionsKt___CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$IntRef;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: GltfModelViewer.kt */
/* loaded from: classes2.dex */
public final class GltfModelViewer implements View.OnTouchListener {
    public static final Companion Companion = new Companion(null);
    private static final Float3 kDefaultObjectPosition = new Float3(0.0f, 0.0f, -4.0f);
    @Nullable
    private Animator animator;
    @Nullable
    private FilamentAsset asset;
    private AssetLoader assetLoader;
    @NotNull
    private final Camera camera;
    private Manipulator cameraManipulator;
    private DisplayHelper displayHelper;
    @NotNull
    private final Engine engine;
    private final double[] eyePos;
    private Job fetchResourcesJob;
    private GestureDetector gestureDetector;
    @Entity
    private final int light;
    private boolean normalizeSkinningWeights;
    private final int[] readyRenderables;
    private boolean recomputeBoundingBoxes;
    private final Renderer renderer;
    private ResourceLoader resourceLoader;
    @NotNull
    private final Scene scene;
    private SurfaceView surfaceView;
    private SwapChain swapChain;
    private final double[] target;
    private TextureView textureView;
    private final UiHelper uiHelper;
    private final double[] upward;
    @NotNull
    private final com.google.android.filament.View view;

    /* compiled from: GltfModelViewer.kt */
    /* loaded from: classes2.dex */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }

    /* compiled from: GltfModelViewer.kt */
    /* loaded from: classes2.dex */
    public final class SurfaceCallback implements UiHelper.RendererCallback {
        public SurfaceCallback() {
        }
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public GltfModelViewer(@NotNull SurfaceView surfaceView, @NotNull Engine engine, @Nullable Manipulator manipulator) {
        this(engine);
        Intrinsics.checkNotNullParameter(surfaceView, "surfaceView");
        Intrinsics.checkNotNullParameter(engine, "engine");
        if (manipulator == null) {
            Manipulator.Builder builder = new Manipulator.Builder();
            Float3 float3 = kDefaultObjectPosition;
            manipulator = builder.targetPosition(float3.getX(), float3.getY(), float3.getZ()).viewport(surfaceView.getWidth(), surfaceView.getHeight()).build(Manipulator.Mode.ORBIT);
            Intrinsics.checkNotNullExpressionValue(manipulator, "Manipulator.Builder()\n  …d(Manipulator.Mode.ORBIT)");
        }
        this.cameraManipulator = manipulator;
        this.surfaceView = surfaceView;
        Manipulator manipulator2 = this.cameraManipulator;
        if (manipulator2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cameraManipulator");
        }
        this.gestureDetector = new GestureDetector(surfaceView, manipulator2);
        this.displayHelper = new DisplayHelper(surfaceView.getContext());
        this.uiHelper.setRenderCallback(new SurfaceCallback());
        this.uiHelper.attachTo(surfaceView);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public GltfModelViewer(@NotNull TextureView textureView, @NotNull Engine engine, @Nullable Manipulator manipulator) {
        this(engine);
        Intrinsics.checkNotNullParameter(textureView, "textureView");
        Intrinsics.checkNotNullParameter(engine, "engine");
        if (manipulator == null) {
            Manipulator.Builder builder = new Manipulator.Builder();
            Float3 float3 = kDefaultObjectPosition;
            manipulator = builder.targetPosition(float3.getX(), float3.getY(), float3.getZ()).viewport(textureView.getWidth(), textureView.getHeight()).build(Manipulator.Mode.ORBIT);
            Intrinsics.checkNotNullExpressionValue(manipulator, "Manipulator.Builder()\n  …d(Manipulator.Mode.ORBIT)");
        }
        this.cameraManipulator = manipulator;
        this.textureView = textureView;
        Manipulator manipulator2 = this.cameraManipulator;
        if (manipulator2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cameraManipulator");
        }
        this.gestureDetector = new GestureDetector(textureView, manipulator2);
        this.displayHelper = new DisplayHelper(textureView.getContext());
        this.uiHelper.setRenderCallback(new SurfaceCallback());
        this.uiHelper.attachTo(textureView);
    }

    public GltfModelViewer(@NotNull Engine engine) {
        Intrinsics.checkNotNullParameter(engine, "engine");
        this.engine = engine;
        this.normalizeSkinningWeights = true;
        this.uiHelper = new UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK);
        this.readyRenderables = new int[128];
        this.eyePos = new double[3];
        this.target = new double[3];
        this.upward = new double[3];
        Renderer createRenderer = engine.createRenderer();
        Intrinsics.checkNotNullExpressionValue(createRenderer, "engine.createRenderer()");
        this.renderer = createRenderer;
        Scene createScene = engine.createScene();
        Intrinsics.checkNotNullExpressionValue(createScene, "engine.createScene()");
        this.scene = createScene;
        Camera createCamera = engine.createCamera();
        Intrinsics.checkNotNullExpressionValue(createCamera, "engine.createCamera()");
        createCamera.setExposure(16.0f, 0.008f, 100.0f);
        Unit unit = Unit.INSTANCE;
        this.camera = createCamera;
        com.google.android.filament.View createView = engine.createView();
        Intrinsics.checkNotNullExpressionValue(createView, "engine.createView()");
        this.view = createView;
        createView.setScene(createScene);
        createView.setCamera(createCamera);
        this.assetLoader = new AssetLoader(engine, new MaterialProvider(engine), EntityManager.get());
        this.resourceLoader = new ResourceLoader(engine, this.normalizeSkinningWeights, this.recomputeBoundingBoxes);
        int create = EntityManager.get().create();
        this.light = create;
        float[] cct = Colors.cct(6500.0f);
        new LightManager.Builder(LightManager.Type.DIRECTIONAL).color(cct[0], cct[1], cct[2]).intensity(100000.0f).direction(0.0f, -1.0f, 0.0f).castShadows(true).build(engine, create);
        createScene.addEntity(create);
    }

    private final void populateScene(final FilamentAsset filamentAsset) {
        List<Integer> take;
        int[] intArray;
        final Ref$IntRef ref$IntRef = new Ref$IntRef();
        ref$IntRef.element = 0;
        Function0<Boolean> function0 = new Function0<Boolean>() { // from class: com.miui.maml.elements.filament.GltfModelViewer$populateScene$popRenderables$1
            /* JADX INFO: Access modifiers changed from: package-private */
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super(0);
            }

            @Override // kotlin.jvm.functions.Function0
            public /* bridge */ /* synthetic */ Boolean invoke() {
                return Boolean.valueOf(invoke2());
            }

            /* renamed from: invoke  reason: avoid collision after fix types in other method */
            public final boolean invoke2() {
                int[] iArr;
                Ref$IntRef ref$IntRef2 = ref$IntRef;
                FilamentAsset filamentAsset2 = filamentAsset;
                iArr = GltfModelViewer.this.readyRenderables;
                ref$IntRef2.element = filamentAsset2.popRenderables(iArr);
                return ref$IntRef.element != 0;
            }
        };
        while (function0.invoke().booleanValue()) {
            Scene scene = this.scene;
            take = ArraysKt___ArraysKt.take(this.readyRenderables, ref$IntRef.element);
            intArray = CollectionsKt___CollectionsKt.toIntArray(take);
            scene.addEntities(intArray);
        }
        this.scene.addEntities(filamentAsset.getLightEntities());
    }

    public final void destroy() {
        this.uiHelper.detach();
        destroyModel();
        this.assetLoader.destroy();
        this.resourceLoader.destroy();
        this.engine.destroyEntity(this.light);
        this.engine.destroyRenderer(this.renderer);
        this.engine.destroyView(this.view);
        this.engine.destroyScene(this.scene);
        this.engine.destroyCamera(this.camera);
        EntityManager.get().destroy(this.light);
    }

    public final void destroyModel() {
        Job job = this.fetchResourcesJob;
        if (job != null) {
            Job.DefaultImpls.cancel$default(job, (CancellationException) null, 1, (Object) null);
        }
        FilamentAsset filamentAsset = this.asset;
        if (filamentAsset != null) {
            this.scene.removeEntities(filamentAsset.getEntities());
            this.assetLoader.destroyAsset(filamentAsset);
            this.asset = null;
            this.animator = null;
        }
    }

    @Nullable
    public final Animator getAnimator() {
        return this.animator;
    }

    @NotNull
    public final Scene getScene() {
        return this.scene;
    }

    @NotNull
    public final com.google.android.filament.View getView() {
        return this.view;
    }

    public final void loadModelGlb(@NotNull Buffer buffer) {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        destroyModel();
        FilamentAsset createAssetFromBinary = this.assetLoader.createAssetFromBinary(buffer);
        this.asset = createAssetFromBinary;
        if (createAssetFromBinary != null) {
            this.resourceLoader.asyncBeginLoad(createAssetFromBinary);
            this.animator = createAssetFromBinary.getAnimator();
            createAssetFromBinary.releaseSourceData();
        }
    }

    public final void loadModelGltfAsync(@NotNull Buffer buffer, @NotNull final Function1<? super String, ? extends Buffer> callback) {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        Intrinsics.checkNotNullParameter(callback, "callback");
        destroyModel();
        this.asset = this.assetLoader.createAssetFromJson(buffer);
        final Continuation continuation = null;
        this.fetchResourcesJob = BuildersKt.launch$default(CoroutineScopeKt.CoroutineScope(Dispatchers.getIO()), (CoroutineContext) null, (CoroutineStart) null, new SuspendLambda(continuation) { // from class: com.miui.maml.elements.filament.GltfModelViewer$loadModelGltfAsync$1
            Object L$0;
            int label;
            private CoroutineScope p$;
        }, 3, (Object) null);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(@NotNull View view, @NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(view, "view");
        Intrinsics.checkNotNullParameter(event, "event");
        onTouchEvent(event);
        return true;
    }

    public final void onTouchEvent(@NotNull MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        GestureDetector gestureDetector = this.gestureDetector;
        if (gestureDetector == null) {
            Intrinsics.throwUninitializedPropertyAccessException("gestureDetector");
        }
        gestureDetector.onTouchEvent(event);
    }

    public final void render(long j) {
        if (this.uiHelper.isReadyToRender()) {
            this.resourceLoader.asyncUpdateLoad();
            FilamentAsset filamentAsset = this.asset;
            if (filamentAsset != null) {
                populateScene(filamentAsset);
            }
            Manipulator manipulator = this.cameraManipulator;
            if (manipulator == null) {
                Intrinsics.throwUninitializedPropertyAccessException("cameraManipulator");
            }
            manipulator.getLookAt(this.eyePos, this.target, this.upward);
            Camera camera = this.camera;
            double[] dArr = this.eyePos;
            double d = dArr[0];
            double d2 = dArr[1];
            double d3 = dArr[2];
            double[] dArr2 = this.target;
            double d4 = dArr2[0];
            double d5 = dArr2[1];
            double d6 = dArr2[2];
            double[] dArr3 = this.upward;
            camera.lookAt(d, d2, d3, d4, d5, d6, dArr3[0], dArr3[1], dArr3[2]);
            Renderer renderer = this.renderer;
            SwapChain swapChain = this.swapChain;
            Intrinsics.checkNotNull(swapChain);
            if (renderer.beginFrame(swapChain, j)) {
                this.renderer.render(this.view);
                this.renderer.endFrame();
            }
        }
    }

    public final void transformToUnitCube(@NotNull Float3 centerPoint) {
        Intrinsics.checkNotNullParameter(centerPoint, "centerPoint");
        FilamentAsset filamentAsset = this.asset;
        if (filamentAsset != null) {
            TransformManager transformManager = this.engine.getTransformManager();
            Intrinsics.checkNotNullExpressionValue(transformManager, "engine.transformManager");
            Box boundingBox = filamentAsset.getBoundingBox();
            Intrinsics.checkNotNullExpressionValue(boundingBox, "asset.boundingBox");
            float[] center = boundingBox.getCenter();
            Float3 float3 = new Float3(center[0], center[1], center[2]);
            Box boundingBox2 = filamentAsset.getBoundingBox();
            Intrinsics.checkNotNullExpressionValue(boundingBox2, "asset.boundingBox");
            float[] halfExtent = boundingBox2.getHalfExtent();
            Float3 float32 = new Float3(halfExtent[0], halfExtent[1], halfExtent[2]);
            float max = 2.0f / (Math.max(float32.getX(), Math.max(float32.getY(), float32.getZ())) * 2.0f);
            Float3 float33 = new Float3(centerPoint.getX() / max, centerPoint.getY() / max, centerPoint.getZ() / max);
            transformManager.setTransform(transformManager.getInstance(filamentAsset.getRoot()), MatrixKt.transpose(MatrixKt.scale(new Float3(max)).times(MatrixKt.translation(new Float3(float3.getX() - float33.getX(), float3.getY() - float33.getY(), float3.getZ() - float33.getZ()).unaryMinus()))).toFloatArray());
        }
    }
}
