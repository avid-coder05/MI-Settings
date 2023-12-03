package com.miui.maml;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.miui.maml.RendererController;
import com.miui.maml.SoundManager;
import com.miui.maml.StylesManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.component.MamlSurface;
import com.miui.maml.data.DarkModeVariableUpdater;
import com.miui.maml.data.DateTimeVariableUpdater;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.VariableBinder;
import com.miui.maml.data.VariableBinderManager;
import com.miui.maml.data.VariableUpdaterManager;
import com.miui.maml.data.Variables;
import com.miui.maml.elements.AnimatedScreenElement;
import com.miui.maml.elements.ElementGroup;
import com.miui.maml.elements.ElementGroupRC;
import com.miui.maml.elements.FramerateController;
import com.miui.maml.elements.ITicker;
import com.miui.maml.elements.ScreenElement;
import com.miui.maml.elements.ScreenElementVisitor;
import com.miui.maml.util.ConfigFile;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.MamlAccessHelper;
import com.miui.maml.util.MamlViewManager;
import com.miui.maml.util.Task;
import com.miui.maml.util.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import miui.os.SystemProperties;
import miui.vip.VipService;
import miui.yellowpage.Tag;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* loaded from: classes2.dex */
public class ScreenElementRoot extends ScreenElement {
    protected float DEFAULT_FRAME_RATE;
    private List<AnimatedScreenElement> mAccessibleElements;
    private boolean mAllowScreenRotation;
    private ArrayMap<String, ArrayList<BaseAnimation.AnimationItem>> mAnimationItems;
    private boolean mAutoDarkenWallpaper;
    private boolean mBlurWindow;
    private int mCapability;
    private long mCheckPoint;
    private boolean mClearCanvas;
    private ConfigFile mConfig;
    private String mConfigPath;
    protected ScreenContext mContext;
    protected RendererController mController;
    private boolean mDarkWallpaperMode;
    private int mDefaultResourceDensity;
    private int mDefaultScreenWidth;
    private ArrayMap<String, WeakReference<ScreenElement>> mElements;
    private WeakReference<OnExternCommandListener> mExternCommandListener;
    private CommandTriggers mExternalCommandManager;
    private boolean mFinished;
    private float mFontScale;
    protected float mFrameRate;
    private IndexedVariable mFrameRateVar;
    private FramerateHelper mFramerateHelper;
    private int mFrames;
    private float mHeight;
    private WeakReference<OnHoverChangeListener> mHoverChangeListenerRef;
    private AnimatedScreenElement mHoverElement;
    private Matrix mHoverMatrix;
    protected ElementGroup mInnerGroup;
    private boolean mKeepResource;
    private boolean mLoaded;
    private MamlAccessHelper mMamlAccessHelper;
    private WeakReference<OnExternCommandListener> mMamlSurfaceExternCommandListener;
    private WeakReference<MamlSurface> mMamlSurfaceRef;
    private WeakReference<OnExternCommandListener> mMamlViewExternCommandListener;
    private boolean mNeedDisallowInterceptTouchEvent;
    private IndexedVariable mNeedDisallowInterceptTouchEventVar;
    private boolean mNeedReset;
    private ArrayList<ITicker> mPreTickers;
    protected HashMap<String, String> mRawAttrs;
    private int mRawDefaultResourceDensity;
    private int mRawHeight;
    private int mRawTargetDensity;
    private int mRawWidth;
    private ArrayList<RendererController> mRendererControllers;
    private String mRootTag;
    private boolean mSaveConfigOnlyInPause;
    private boolean mSaveConfigViaProvider;
    private float mScale;
    private boolean mScaleByDensity;
    public boolean mShowDebugLayout;
    private boolean mShowFramerate;
    private SoundManager mSoundManager;
    private StylesManager mStylesManager;
    private boolean mSupportAccessibilityService;
    private OnExternCommandListener mSystemExternCommandListener;
    private int mTargetDensity;
    protected int mTargetScreenHeight;
    protected int mTargetScreenWidth;
    private IndexedVariable mTouchBeginTime;
    private IndexedVariable mTouchBeginX;
    private IndexedVariable mTouchBeginY;
    private IndexedVariable mTouchX;
    private IndexedVariable mTouchY;
    private boolean mTouchable;
    private boolean mTransparentSurface;
    private boolean mUseCustomizedDarkModeWallpaper;
    protected VariableBinderManager mVariableBinderManager;
    private VariableUpdaterManager mVariableUpdaterManager;
    private int mVersion;
    private WeakReference<MamlViewManager> mViewManagerRef;
    private float mWidth;

    /* renamed from: com.miui.maml.ScreenElementRoot$4  reason: invalid class name */
    /* loaded from: classes2.dex */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType;

        static {
            int[] iArr = new int[ExtraResource.MetricsType.values().length];
            $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType = iArr;
            try {
                iArr[ExtraResource.MetricsType.DEN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[ExtraResource.MetricsType.SW.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[ExtraResource.MetricsType.SW_DEN.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ExtraResource {
        private ArrayList<ScaleMetrics> mResources = new ArrayList<>();
        private ArrayList<ScaleMetrics> mScales = new ArrayList<>();

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public enum MetricsType {
            DEN,
            SW,
            SW_DEN
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public class Resource extends ScaleMetrics {
            String mPath;

            public Resource() {
                super();
            }

            public Resource(String str, MetricsType metricsType) {
                super(str, metricsType);
                int i = AnonymousClass4.$SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[metricsType.ordinal()];
                if (i == 1) {
                    this.mPath = "den" + this.mDensity;
                } else if (i != 2) {
                } else {
                    this.mPath = "sw" + this.mScreenWidth;
                }
            }

            @Override // com.miui.maml.ScreenElementRoot.ExtraResource.ScaleMetrics
            protected void onParseInfo(String[] strArr) {
                this.mPath = strArr[strArr.length <= 2 ? (char) 0 : (char) 1];
            }

            @Override // com.miui.maml.ScreenElementRoot.ExtraResource.ScaleMetrics
            public String toString() {
                return super.toString() + " path:" + this.mPath;
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes2.dex */
        public class ScaleMetrics {
            int mDensity;
            float mScale;
            int mScreenWidth;
            int mSizeType;

            public ScaleMetrics() {
                this.mScale = 1.0f;
            }

            public ScaleMetrics(String str, MetricsType metricsType) {
                this.mScale = 1.0f;
                try {
                    String[] split = str.split(":");
                    int i = AnonymousClass4.$SwitchMap$com$miui$maml$ScreenElementRoot$ExtraResource$MetricsType[metricsType.ordinal()];
                    char c = 1;
                    if (i == 1) {
                        int parseInt = Integer.parseInt(split[0]);
                        this.mDensity = parseInt;
                        this.mScreenWidth = (ResourceManager.translateDensity(parseInt) * 480) / 240;
                        if (split.length > 1) {
                            this.mScale = Float.parseFloat(split[1]);
                        }
                    } else if (i == 2) {
                        int parseInt2 = Integer.parseInt(split[0]);
                        this.mScreenWidth = parseInt2;
                        this.mDensity = ResourceManager.retranslateDensity((parseInt2 * 240) / 480);
                        if (split.length > 1) {
                            this.mScale = Float.parseFloat(split[1]);
                        }
                    } else if (i != 3) {
                    } else {
                        String[] split2 = split[0].split("-");
                        this.mSizeType = 0;
                        if (split2.length == 1) {
                            if (split2[0].startsWith("sw")) {
                                int parseInt3 = Integer.parseInt(split2[0].substring(2));
                                this.mScreenWidth = parseInt3;
                                this.mDensity = ResourceManager.retranslateDensity((parseInt3 * 240) / 480);
                            } else if (!split2[0].startsWith("den")) {
                                throw new IllegalArgumentException("invalid format: " + str);
                            } else {
                                int parseInt4 = Integer.parseInt(split2[0].substring(3));
                                this.mDensity = parseInt4;
                                this.mScreenWidth = (ResourceManager.translateDensity(parseInt4) * 480) / 240;
                            }
                        } else if (split2.length < 2) {
                            throw new IllegalArgumentException("invalid format: " + str);
                        } else {
                            this.mScreenWidth = Integer.parseInt(split2[0].substring(2));
                            this.mDensity = Integer.parseInt(split2[1].substring(3));
                            if (split2.length == 3) {
                                this.mSizeType = ExtraResource.parseSizeType(split2[2]);
                            }
                        }
                        if (split.length > 1) {
                            if (split.length != 2) {
                                c = 2;
                            }
                            this.mScale = Float.parseFloat(split[c]);
                        }
                        onParseInfo(split);
                    }
                } catch (NumberFormatException unused) {
                    Log.w("ScreenElementRoot", "format error of string: " + str);
                    throw new IllegalArgumentException("invalid format");
                }
            }

            protected void onParseInfo(String[] strArr) {
            }

            public String toString() {
                return "ScaleMetrics sw:" + this.mScreenWidth + " den:" + this.mDensity + " sizeType:" + this.mSizeType + " scale:" + this.mScale;
            }
        }

        public ExtraResource(Element element, int i) {
            Resource resource = new Resource();
            resource.mDensity = i;
            resource.mScreenWidth = (ResourceManager.translateDensity(i) * 480) / 240;
            resource.mSizeType = 0;
            resource.mPath = null;
            resource.mScale = 1.0f;
            this.mResources.add(resource);
            ArrayList<ScaleMetrics> arrayList = this.mResources;
            String attribute = element.getAttribute("extraResourcesDensity");
            MetricsType metricsType = MetricsType.DEN;
            inflateMetrics(arrayList, attribute, metricsType);
            ArrayList<ScaleMetrics> arrayList2 = this.mResources;
            String attribute2 = element.getAttribute("extraResourcesScreenWidth");
            MetricsType metricsType2 = MetricsType.SW;
            inflateMetrics(arrayList2, attribute2, metricsType2);
            ArrayList<ScaleMetrics> arrayList3 = this.mResources;
            String attribute3 = element.getAttribute("extraResources");
            MetricsType metricsType3 = MetricsType.SW_DEN;
            inflateMetrics(arrayList3, attribute3, metricsType3);
            ScaleMetrics scaleMetrics = new ScaleMetrics();
            scaleMetrics.mDensity = i;
            scaleMetrics.mScreenWidth = (ResourceManager.translateDensity(i) * 480) / 240;
            scaleMetrics.mSizeType = 0;
            scaleMetrics.mScale = -1.0f;
            this.mScales.add(scaleMetrics);
            inflateMetrics(this.mScales, element.getAttribute("extraScaleByDensity"), metricsType);
            inflateMetrics(this.mScales, element.getAttribute("extraScaleByScreenWidth"), metricsType2);
            inflateMetrics(this.mScales, element.getAttribute("extraScales"), metricsType3);
        }

        private void inflateMetrics(ArrayList<ScaleMetrics> arrayList, String str, MetricsType metricsType) {
            if (TextUtils.isEmpty(str)) {
                return;
            }
            for (String str2 : str.split(",")) {
                try {
                    if (arrayList == this.mResources) {
                        arrayList.add(new Resource(str2.trim(), metricsType));
                    } else if (arrayList == this.mScales) {
                        arrayList.add(new ScaleMetrics(str2.trim(), metricsType));
                    }
                } catch (IllegalArgumentException unused) {
                    Log.w("ScreenElementRoot", "format error of attribute: " + str);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static int parseSizeType(String str) {
            if ("small".equals(str)) {
                return 1;
            }
            if ("normal".equals(str)) {
                return 2;
            }
            if ("large".equals(str)) {
                return 3;
            }
            return "xlarge".equals(str) ? 4 : 0;
        }

        ScaleMetrics findMetrics(int i, int i2, int i3, ArrayList<ScaleMetrics> arrayList) {
            ArrayList arrayList2 = new ArrayList();
            Iterator<ScaleMetrics> it = arrayList.iterator();
            int i4 = Integer.MAX_VALUE;
            int i5 = Integer.MAX_VALUE;
            while (it.hasNext()) {
                ScaleMetrics next = it.next();
                int i6 = next.mSizeType;
                if (i6 == 0 || i6 == i3) {
                    int abs = Math.abs(i - next.mDensity);
                    if (abs < i4) {
                        int abs2 = Math.abs(i2 - next.mScreenWidth);
                        arrayList2.clear();
                        arrayList2.add(next);
                        i5 = abs2;
                        i4 = abs;
                    } else if (abs == i4) {
                        int abs3 = Math.abs(i2 - next.mScreenWidth);
                        if (abs3 < i5) {
                            arrayList2.clear();
                            arrayList2.add(next);
                            i5 = abs3;
                        } else if (abs3 == i5) {
                            arrayList2.add(next);
                        }
                    }
                }
            }
            Iterator it2 = arrayList2.iterator();
            ScaleMetrics scaleMetrics = null;
            while (it2.hasNext()) {
                ScaleMetrics scaleMetrics2 = (ScaleMetrics) it2.next();
                int i7 = scaleMetrics2.mSizeType;
                if (i7 == i3) {
                    return scaleMetrics2;
                }
                if (i7 == 0) {
                    scaleMetrics = scaleMetrics2;
                }
            }
            return scaleMetrics;
        }

        public Resource findResource(int i, int i2, int i3) {
            return (Resource) findMetrics(i, i2, i3, this.mResources);
        }

        public ScaleMetrics findScale(int i, int i2, int i3) {
            return findMetrics(i, i2, i3, this.mScales);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class FramerateHelper {
        private String mFramerateText;
        private TextPaint mPaint;
        private int mRealFrameRate;
        private int mShowingFramerate;
        private int mTextX;
        private int mTextY;

        public FramerateHelper() {
            this(-65536, 14, 10, 10);
        }

        public FramerateHelper(int i, int i2, int i3, int i4) {
            TextPaint textPaint = new TextPaint();
            this.mPaint = textPaint;
            textPaint.setColor(i);
            this.mPaint.setTextSize(i2);
            this.mTextX = i3;
            this.mTextY = i4;
        }

        public void draw(Canvas canvas) {
            if (this.mFramerateText == null || this.mShowingFramerate != this.mRealFrameRate) {
                int i = this.mRealFrameRate;
                this.mShowingFramerate = i;
                this.mFramerateText = String.format("FPS %d", Integer.valueOf(i));
            }
            canvas.drawText(this.mFramerateText, this.mTextX, this.mTextY, this.mPaint);
        }

        public void set(int i) {
            this.mRealFrameRate = i;
        }
    }

    /* loaded from: classes2.dex */
    private static class InnerGroup extends ElementGroup {
        public InnerGroup(Element element, ScreenElementRoot screenElementRoot) {
            super(element, screenElementRoot);
        }

        @Override // com.miui.maml.elements.ScreenElement
        public final RendererController getRendererController() {
            return this.mRoot.getRendererController();
        }
    }

    /* loaded from: classes2.dex */
    public interface OnExternCommandListener {
        void onCommand(String str, Double d, String str2);
    }

    /* loaded from: classes2.dex */
    public interface OnHoverChangeListener {
        void onHoverChange(String str);
    }

    public ScreenElementRoot(ScreenContext screenContext) {
        super(null, null);
        this.DEFAULT_FRAME_RATE = 30.0f;
        this.mPreTickers = new ArrayList<>();
        this.mRawAttrs = new HashMap<>();
        this.mSupportAccessibilityService = false;
        this.mBlurWindow = false;
        this.mTouchable = true;
        this.mHoverMatrix = new Matrix();
        this.mAnimationItems = new ArrayMap<>();
        this.mElements = new ArrayMap<>();
        this.mFramerateHelper = new FramerateHelper();
        this.mRendererControllers = new ArrayList<>();
        this.mCapability = -1;
        this.mAccessibleElements = new ArrayList();
        this.mRoot = this;
        this.mContext = screenContext;
        this.mVariableUpdaterManager = new VariableUpdaterManager(this);
        this.mTouchX = new IndexedVariable("touch_x", getContext().mVariables, true);
        this.mTouchY = new IndexedVariable("touch_y", getContext().mVariables, true);
        this.mTouchBeginX = new IndexedVariable("touch_begin_x", getContext().mVariables, true);
        this.mTouchBeginY = new IndexedVariable("touch_begin_y", getContext().mVariables, true);
        this.mTouchBeginTime = new IndexedVariable("touch_begin_time", getContext().mVariables, true);
        this.mNeedDisallowInterceptTouchEventVar = new IndexedVariable("intercept_sys_touch", getContext().mVariables, true);
        this.mSoundManager = new SoundManager(this.mContext);
        this.mSystemExternCommandListener = new SystemCommandListener(this);
    }

    private void loadConfig(String str) {
        if (str == null) {
            return;
        }
        ConfigFile configFile = new ConfigFile();
        this.mConfig = configFile;
        configFile.setSaveViaProvider(this.mSaveConfigViaProvider);
        if (!this.mConfig.load(str)) {
            this.mConfig.loadDefaultSettings(this.mContext.mResourceManager.getConfigRoot());
        }
        for (ConfigFile.Variable variable : this.mConfig.getVariables()) {
            if (TextUtils.equals(variable.type, "string")) {
                Utils.putVariableString(variable.name, this.mContext.mVariables, variable.value);
            } else if (TextUtils.equals(variable.type, "number")) {
                try {
                    Utils.putVariableNumber(variable.name, this.mContext.mVariables, Double.parseDouble(variable.value));
                } catch (NumberFormatException unused) {
                }
            }
        }
        for (Task task : this.mConfig.getTasks()) {
            this.mContext.mVariables.put(task.id + ".name", task.name);
            this.mContext.mVariables.put(task.id + ".package", task.packageName);
            this.mContext.mVariables.put(task.id + ".class", task.className);
        }
    }

    private void loadRawAttrs(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node item = attributes.item(i);
            this.mRawAttrs.put(item.getNodeName(), item.getNodeValue());
        }
    }

    private void processUseVariableUpdater(Element element) {
        String attribute = element.getAttribute("useVariableUpdater");
        if (TextUtils.isEmpty(attribute)) {
            onAddVariableUpdater(this.mVariableUpdaterManager);
        } else {
            this.mVariableUpdaterManager.addFromTag(attribute);
        }
        this.mVariableUpdaterManager.add(new DarkModeVariableUpdater(this.mVariableUpdaterManager));
    }

    private void setupScale(Element element) {
        String attribute = element.getAttribute("scaleByDensity");
        if (!TextUtils.isEmpty(attribute)) {
            this.mScaleByDensity = Boolean.parseBoolean(attribute);
        }
        int attrAsInt = Utils.getAttrAsInt(element, "defaultScreenWidth", 0);
        this.mDefaultScreenWidth = attrAsInt;
        if (attrAsInt == 0) {
            this.mDefaultScreenWidth = Utils.getAttrAsInt(element, "screenWidth", 0);
        }
        int attrAsInt2 = Utils.getAttrAsInt(element, "defaultResourceDensity", 0);
        this.mRawDefaultResourceDensity = attrAsInt2;
        if (attrAsInt2 == 0) {
            this.mRawDefaultResourceDensity = Utils.getAttrAsInt(element, "resDensity", 0);
        }
        int translateDensity = ResourceManager.translateDensity(this.mRawDefaultResourceDensity);
        this.mDefaultResourceDensity = translateDensity;
        int i = this.mDefaultScreenWidth;
        if (i == 0 && translateDensity == 0) {
            this.mDefaultScreenWidth = 480;
            this.mDefaultResourceDensity = 240;
        } else if (translateDensity == 0) {
            this.mDefaultResourceDensity = (i * 240) / 480;
        } else if (i == 0) {
            this.mDefaultScreenWidth = (translateDensity * 480) / 240;
        }
        this.mContext.mResourceManager.setDefaultResourceDensity(this.mDefaultResourceDensity);
        Display defaultDisplay = ((WindowManager) this.mContext.mContext.getSystemService("window")).getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        int rotation = defaultDisplay.getRotation();
        boolean z = true;
        if (rotation != 1 && rotation != 3) {
            z = false;
        }
        this.mTargetScreenWidth = z ? point.y : point.x;
        this.mTargetScreenHeight = z ? point.x : point.y;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        this.mRawTargetDensity = displayMetrics.densityDpi;
        int i2 = this.mContext.mContext.getResources().getConfiguration().screenLayout & 15;
        int i3 = this.mRawDefaultResourceDensity;
        if (i3 == 0) {
            i3 = (this.mDefaultScreenWidth * 240) / 480;
        }
        ExtraResource extraResource = new ExtraResource(element, i3);
        ExtraResource.Resource findResource = extraResource.findResource(this.mRawTargetDensity, this.mTargetScreenWidth, i2);
        Log.d("ScreenElementRoot", "findResource: " + findResource.toString());
        this.mContext.mResourceManager.setExtraResource(findResource.mPath, (int) (((float) ResourceManager.translateDensity(findResource.mDensity)) / findResource.mScale));
        ExtraResource.ScaleMetrics findScale = extraResource.findScale(this.mRawTargetDensity, this.mTargetScreenWidth, i2);
        Log.d("ScreenElementRoot", "findScale: " + findScale.toString());
        if (this.mScaleByDensity) {
            int translateDensity2 = ResourceManager.translateDensity(this.mRawTargetDensity);
            this.mTargetDensity = translateDensity2;
            float f = findScale.mScale;
            if (f <= 0.0f) {
                this.mScale = translateDensity2 / this.mDefaultResourceDensity;
            } else {
                this.mScale = f * ((this.mRawTargetDensity * 1.0f) / findScale.mDensity);
            }
        } else {
            int i4 = this.mTargetScreenWidth;
            float f2 = i4 / this.mDefaultScreenWidth;
            this.mScale = f2;
            this.mTargetDensity = (int) (this.mDefaultResourceDensity * f2);
            float f3 = findScale.mScale;
            if (f3 > 0.0f) {
                this.mScale = f3 * ((i4 * 1.0f) / findScale.mScreenWidth);
            }
        }
        Log.i("ScreenElementRoot", "set scale: " + this.mScale);
        this.mContext.mResourceManager.setTargetDensity(this.mTargetDensity);
        this.mRawWidth = Utils.getAttrAsInt(element, Tag.TagWebService.ContentGetImage.PARAM_IMAGE_WIDTH, 0);
        this.mRawHeight = Utils.getAttrAsInt(element, "height", 0);
        this.mWidth = Math.round(this.mRawWidth * this.mScale);
        this.mHeight = Math.round(this.mRawHeight * this.mScale);
    }

    private void traverseElements() {
        this.mRendererControllers.clear();
        acceptVisitor(new ScreenElementVisitor() { // from class: com.miui.maml.ScreenElementRoot.2
            @Override // com.miui.maml.elements.ScreenElementVisitor
            public void visit(ScreenElement screenElement) {
                RendererController rendererController;
                if ((screenElement instanceof FramerateController) && (rendererController = screenElement.getRendererController()) != null) {
                    rendererController.addFramerateController((FramerateController) screenElement);
                }
                if ((screenElement instanceof ElementGroupRC) || (screenElement instanceof ScreenElementRoot)) {
                    ScreenElementRoot.this.mRendererControllers.add(screenElement.getRendererController());
                }
            }
        });
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        super.acceptVisitor(screenElementVisitor);
        this.mInnerGroup.acceptVisitor(screenElementVisitor);
    }

    public void addAccessibleElements(AnimatedScreenElement animatedScreenElement) {
        animatedScreenElement.setVirtualViewId(this.mAccessibleElements.size());
        this.mAccessibleElements.add(animatedScreenElement);
    }

    public void addAnimationItem(String str, BaseAnimation.AnimationItem animationItem) {
        if (this.mAnimationItems.containsKey(str)) {
            this.mAnimationItems.get(str).add(animationItem);
            return;
        }
        ArrayList<BaseAnimation.AnimationItem> arrayList = new ArrayList<>();
        arrayList.add(animationItem);
        this.mAnimationItems.put(str, arrayList);
    }

    public void addElement(String str, WeakReference weakReference) {
        this.mElements.put(str, weakReference);
    }

    public void addPreTicker(ITicker iTicker) {
        this.mPreTickers.add(iTicker);
    }

    public void attachToVsync() {
        Objects.requireNonNull(this.mController, "VsyncUpdater or controller is null, MUST load before attaching");
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            RenderVsyncUpdater.getInstance().addRendererController(this.mRendererControllers.get(i));
        }
    }

    public void detachFromVsync() {
        Objects.requireNonNull(this.mController, "VsyncUpdater or controller is null, MUST load before detaching");
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            RenderVsyncUpdater.getInstance().removeRendererController(this.mRendererControllers.get(i));
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void doRender(Canvas canvas) {
        if (this.mFinished || !this.mLoaded) {
            return;
        }
        if (this.mClearCanvas) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
        try {
            this.mInnerGroup.render(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("ScreenElementRoot", e2.toString());
        }
        if (this.mShowFramerate) {
            this.mFramerateHelper.draw(canvas);
        }
        this.mFrames++;
        this.mController.doneRender();
        if (this.mDarkWallpaperMode && !this.mUseCustomizedDarkModeWallpaper && this.mAutoDarkenWallpaper) {
            canvas.drawColor(419430400);
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void doTick(long j) {
        if (this.mFinished || !this.mLoaded) {
            return;
        }
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            variableBinderManager.tick();
        }
        this.mVariableUpdaterManager.tick(j);
        int size = this.mPreTickers.size();
        for (int i = 0; i < size; i++) {
            this.mPreTickers.get(i).tick(j);
        }
        this.mInnerGroup.tick(j);
        this.mNeedDisallowInterceptTouchEvent = this.mNeedDisallowInterceptTouchEventVar.getDouble() > 0.0d;
        if (this.mFrameRateVar == null) {
            this.mFrameRateVar = new IndexedVariable("frame_rate", this.mContext.mVariables, true);
            this.mCheckPoint = 0L;
        }
        long j2 = this.mCheckPoint;
        if (j2 == 0) {
            this.mCheckPoint = j;
            return;
        }
        long j3 = j - j2;
        if (j3 >= 1000) {
            int i2 = (int) ((this.mFrames * VipService.VIP_SERVICE_FAILURE) / j3);
            this.mFramerateHelper.set(i2);
            this.mFrameRateVar.set(i2);
            this.mFrames = 0;
            this.mCheckPoint = j;
        }
    }

    public void doneRender() {
        this.mController.doneRender();
    }

    public VariableBinder findBinder(String str) {
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            return variableBinderManager.findBinder(str);
        }
        return null;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public ScreenElement findElement(String str) {
        return "__root".equals(str) ? this : getElement(str);
    }

    public Task findTask(String str) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            return null;
        }
        return configFile.getTask(str);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void finish() {
        if (this.mFinished || !this.mLoaded) {
            return;
        }
        super.finish();
        Log.d("ScreenElementRoot", "finish");
        this.mInnerGroup.performAction("preFinish");
        this.mInnerGroup.finish();
        this.mInnerGroup.performAction("finish");
        ConfigFile configFile = this.mConfig;
        if (configFile != null && !this.mSaveConfigOnlyInPause) {
            configFile.save(this.mContext.mContext.getApplicationContext());
        }
        VariableBinderManager variableBinderManager = this.mVariableBinderManager;
        if (variableBinderManager != null) {
            variableBinderManager.finish();
        }
        CommandTriggers commandTriggers = this.mExternalCommandManager;
        if (commandTriggers != null) {
            commandTriggers.finish();
        }
        VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
        if (variableUpdaterManager != null) {
            variableUpdaterManager.finish();
        }
        this.mSoundManager.release();
        this.mContext.mResourceManager.finish(this.mKeepResource);
        this.mFinished = true;
        this.mKeepResource = false;
        Expression.FunctionExpression.resetFunctions();
    }

    public List<AnimatedScreenElement> getAccessibleElements() {
        return this.mAccessibleElements;
    }

    public ArrayList<BaseAnimation.AnimationItem> getAnimationItems(String str) {
        return this.mAnimationItems.get(str);
    }

    public boolean getCapability(int i) {
        return (this.mCapability & i) != 0;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public ScreenContext getContext() {
        return this.mContext;
    }

    public ScreenElement getElement(String str) {
        WeakReference<ScreenElement> weakReference = this.mElements.get(str);
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public final float getFontScale() {
        return this.mFontScale;
    }

    public float getHeight() {
        return this.mHeight;
    }

    public AnimatedScreenElement getHoverElement() {
        return this.mHoverElement;
    }

    public MamlAccessHelper getMamlAccessHelper() {
        return this.mMamlAccessHelper;
    }

    public MamlSurface getMamlSurface() {
        WeakReference<MamlSurface> weakReference = this.mMamlSurfaceRef;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public String getRawAttr(String str) {
        return this.mRawAttrs.get(str);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public RendererController getRendererController() {
        return this.mController;
    }

    public int getResourceDensity() {
        return this.mDefaultResourceDensity;
    }

    public String getRootTag() {
        return this.mRootTag;
    }

    public final float getScale() {
        float f = this.mScale;
        if (f == 0.0f) {
            Log.w("ScreenElementRoot", "scale not initialized!");
            return 1.0f;
        }
        return f;
    }

    public StylesManager.Style getStyle(String str) {
        StylesManager stylesManager;
        if (TextUtils.isEmpty(str) || (stylesManager = this.mStylesManager) == null) {
            return null;
        }
        return stylesManager.getStyle(str);
    }

    public float getSystemFrameRate() {
        return ((WindowManager) this.mContext.mContext.getSystemService("window")).getDefaultDisplay().getRefreshRate();
    }

    public int getTargetDensity() {
        return this.mTargetDensity;
    }

    public MamlViewManager getViewManager() {
        WeakReference<MamlViewManager> weakReference = this.mViewManagerRef;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public float getWidth() {
        return this.mWidth;
    }

    public void haptic(int i) {
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void init() {
        Context context;
        PackageManager packageManager;
        PackageInfo packageInfo;
        if (this.mLoaded) {
            Variables variables = this.mContext.mVariables;
            variables.put("__objRoot", this);
            variables.put("__objContext", this.mContext);
            super.init();
            Log.d("ScreenElementRoot", "init");
            requestFramerate(this.mFrameRate);
            this.mCapability = -1;
            this.mShowDebugLayout = HideSdkDependencyUtils.isShowDebugLayout();
            int i = 0;
            this.mFinished = false;
            this.mContext.mResourceManager.init();
            float f = getContext().mContext.getResources().getConfiguration().fontScale;
            this.mFontScale = f;
            variables.put("__fontScale", f);
            Locale locale = this.mContext.mContext.getResources().getConfiguration().locale;
            ScreenContext screenContext = this.mContext;
            LanguageHelper.load(locale, screenContext.mResourceManager, screenContext.mVariables);
            variables.put("raw_screen_width", this.mTargetScreenWidth);
            variables.put("raw_screen_height", this.mTargetScreenHeight);
            variables.put("screen_width", this.mTargetScreenWidth / this.mScale);
            variables.put("screen_height", this.mTargetScreenHeight / this.mScale);
            int i2 = this.mRawWidth;
            if (i2 > 0) {
                variables.put("view_width", i2);
            }
            int i3 = this.mRawHeight;
            if (i3 > 0) {
                variables.put("view_height", i3);
            }
            variables.put("view_width", this.mTargetScreenWidth / this.mScale);
            variables.put("view_height", this.mTargetScreenHeight / this.mScale);
            variables.put("__raw_density", this.mRawTargetDensity);
            variables.put("__scale_factor", this.mScale);
            variables.put("__maml_version", 6.0d);
            try {
                ScreenContext screenContext2 = this.mContext;
                if (screenContext2 != null && (context = screenContext2.mContext) != null && (packageManager = context.getPackageManager()) != null && (packageInfo = packageManager.getPackageInfo("com.android.thememanager", 0)) != null) {
                    i = packageInfo.versionCode;
                }
            } catch (Exception unused) {
                Log.e("ScreenElementRoot", "thememanager not found");
            }
            variables.put("__thememanager_version", i);
            variables.put("__miui_version_name", SystemProperties.get("ro.miui.ui.version.name"));
            variables.put("__miui_version_code", SystemProperties.get("ro.miui.ui.version.code"));
            variables.put("__android_version", Build.VERSION.RELEASE);
            variables.put("__system_version", Build.VERSION.INCREMENTAL);
            loadConfig();
            VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
            if (variableUpdaterManager != null) {
                variableUpdaterManager.init();
            }
            VariableBinderManager variableBinderManager = this.mVariableBinderManager;
            if (variableBinderManager != null) {
                variableBinderManager.init();
            }
            CommandTriggers commandTriggers = this.mExternalCommandManager;
            if (commandTriggers != null) {
                commandTriggers.init();
            }
            this.mInnerGroup.performAction("init");
            this.mInnerGroup.init();
            this.mInnerGroup.performAction("postInit");
            this.mRoot.mHoverElement = null;
            this.mNeedReset = true;
            this.mController.setNeedReset(true);
            requestUpdate();
        }
    }

    public boolean isMamlBlurWindow() {
        return this.mBlurWindow;
    }

    public void issueExternCommand(String str, Double d, String str2) {
        OnExternCommandListener onExternCommandListener;
        OnExternCommandListener onExternCommandListener2;
        OnExternCommandListener onExternCommandListener3;
        this.mSystemExternCommandListener.onCommand(str, d, str2);
        WeakReference<OnExternCommandListener> weakReference = this.mExternCommandListener;
        if (weakReference != null && (onExternCommandListener3 = weakReference.get()) != null) {
            onExternCommandListener3.onCommand(str, d, str2);
            Log.d("ScreenElementRoot", "issueExternCommand: " + str + " " + d + " " + str2);
        }
        WeakReference<OnExternCommandListener> weakReference2 = this.mMamlViewExternCommandListener;
        if (weakReference2 != null && (onExternCommandListener2 = weakReference2.get()) != null) {
            onExternCommandListener2.onCommand(str, d, str2);
            Log.d("ScreenElementRoot", "issueExternCommand to MamlView: " + str + " " + d + " " + str2);
        }
        WeakReference<OnExternCommandListener> weakReference3 = this.mMamlSurfaceExternCommandListener;
        if (weakReference3 == null || (onExternCommandListener = weakReference3.get()) == null) {
            return;
        }
        onExternCommandListener.onCommand(str, d, str2);
        Log.d("ScreenElementRoot", "issueExternCommand to MamlSurface: " + str + " " + d + " " + str2);
    }

    public final boolean load() {
        try {
            this.mLoaded = false;
            long elapsedRealtime = SystemClock.elapsedRealtime();
            Element manifestRoot = this.mContext.mResourceManager.getManifestRoot();
            if (manifestRoot == null) {
                Log.e("ScreenElementRoot", "load error, manifest root is null");
                return false;
            }
            this.mRootTag = manifestRoot.getNodeName();
            loadRawAttrs(manifestRoot);
            processUseVariableUpdater(manifestRoot);
            setupScale(manifestRoot);
            this.mVariableBinderManager = new VariableBinderManager(Utils.getChild(manifestRoot, "VariableBinders"), this);
            Element child = Utils.getChild(manifestRoot, "ExternalCommands");
            if (child != null) {
                this.mExternalCommandManager = new CommandTriggers(child, this);
            }
            Element child2 = Utils.getChild(manifestRoot, "Styles");
            if (child2 != null) {
                this.mStylesManager = new StylesManager(child2);
            }
            this.mTransparentSurface = Boolean.parseBoolean(manifestRoot.getAttribute("transparentSurface"));
            this.mFrameRate = Utils.getAttrAsFloat(manifestRoot, "frameRate", this.DEFAULT_FRAME_RATE);
            this.mUseCustomizedDarkModeWallpaper = Boolean.parseBoolean(manifestRoot.getAttribute("customizedDarkModeWallpaper"));
            this.mClearCanvas = Boolean.parseBoolean(manifestRoot.getAttribute("clearCanvas"));
            this.mSupportAccessibilityService = Boolean.parseBoolean(manifestRoot.getAttribute("supportAccessibityService"));
            this.mAllowScreenRotation = Boolean.parseBoolean(manifestRoot.getAttribute("allowScreenRotation"));
            this.mBlurWindow = Boolean.parseBoolean(manifestRoot.getAttribute("blurWindow"));
            this.mController = new RendererController();
            InnerGroup innerGroup = new InnerGroup(manifestRoot, this);
            this.mInnerGroup = innerGroup;
            if (innerGroup.getElements().size() <= 0) {
                Log.e("ScreenElementRoot", "load error, no element loaded");
                return false;
            }
            this.mVersion = Utils.getAttrAsInt(manifestRoot, "version", 1);
            if (!onLoad(manifestRoot)) {
                Log.e("ScreenElementRoot", "load error, onLoad fail");
                return false;
            }
            traverseElements();
            Log.d("ScreenElementRoot", "load finished, spent " + (SystemClock.elapsedRealtime() - elapsedRealtime) + " ms");
            this.mLoaded = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadConfig() {
        loadConfig(this.mConfigPath);
    }

    public boolean needDisallowInterceptTouchEvent() {
        return this.mNeedDisallowInterceptTouchEvent;
    }

    protected void onAddVariableUpdater(VariableUpdaterManager variableUpdaterManager) {
        variableUpdaterManager.add(new DateTimeVariableUpdater(variableUpdaterManager));
    }

    public void onCommand(final String str) {
        if (this.mExternalCommandManager != null) {
            postRunnable(new Runnable() { // from class: com.miui.maml.ScreenElementRoot.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        ScreenElementRoot.this.mExternalCommandManager.onAction(str);
                    } catch (Exception e) {
                        Log.e("ScreenElementRoot", e.toString());
                        e.printStackTrace();
                    }
                }
            });
            requestUpdate();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        if (this.mAllowScreenRotation) {
            setConfiguration(configuration);
            onCommand("orientationChange");
            requestUpdate();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public boolean onHover(MotionEvent motionEvent) {
        try {
            if (!this.mFinished && this.mLoaded && this.mTouchable) {
                return this.mInnerGroup.onHover(motionEvent);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ScreenElementRoot", e.toString());
            return false;
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("ScreenElementRoot", e2.toString());
            return false;
        }
    }

    public void onHoverChange(AnimatedScreenElement animatedScreenElement, String str) {
        this.mHoverElement = animatedScreenElement;
        WeakReference<OnHoverChangeListener> weakReference = this.mHoverChangeListenerRef;
        OnHoverChangeListener onHoverChangeListener = weakReference != null ? weakReference.get() : null;
        if (onHoverChangeListener != null) {
            onHoverChangeListener.onHoverChange(str);
        }
    }

    protected boolean onLoad(Element element) {
        return true;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public boolean onTouch(MotionEvent motionEvent) {
        try {
            if (!this.mFinished && this.mLoaded && this.mTouchable) {
                AnimatedScreenElement animatedScreenElement = this.mHoverElement;
                if (animatedScreenElement != null) {
                    this.mHoverMatrix.setTranslate((this.mHoverElement.getAbsoluteLeft() + (animatedScreenElement.getWidth() / 2.0f)) - motionEvent.getX(), (this.mHoverElement.getAbsoluteTop() + (this.mHoverElement.getHeight() / 2.0f)) - motionEvent.getY());
                    motionEvent.transform(this.mHoverMatrix);
                    this.mHoverElement.onTouch(motionEvent);
                    if (motionEvent.getActionMasked() == 1 || motionEvent.getActionMasked() == 3) {
                        this.mHoverElement = null;
                    }
                    return true;
                }
                double descale = descale(motionEvent.getX());
                double descale2 = descale(motionEvent.getY());
                this.mTouchX.set(descale);
                this.mTouchY.set(descale2);
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    this.mTouchBeginX.set(descale);
                    this.mTouchBeginY.set(descale2);
                    this.mTouchBeginTime.set(System.currentTimeMillis());
                    this.mNeedDisallowInterceptTouchEvent = false;
                } else if (actionMasked == 1) {
                    this.mNeedDisallowInterceptTouchEvent = false;
                }
                boolean onTouch = this.mInnerGroup.onTouch(motionEvent);
                if (!onTouch) {
                    this.mController.requestUpdate();
                }
                return onTouch;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ScreenElementRoot", e.toString());
            return false;
        } catch (OutOfMemoryError e2) {
            e2.printStackTrace();
            Log.e("ScreenElementRoot", e2.toString());
            return false;
        }
    }

    public void onUIInteractive(ScreenElement screenElement, String str) {
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void pause() {
        if (this.mLoaded) {
            super.pause();
            Log.d("ScreenElementRoot", "pause");
            this.mInnerGroup.performAction("pause");
            this.mInnerGroup.pause();
            this.mSoundManager.pause();
            VariableBinderManager variableBinderManager = this.mVariableBinderManager;
            if (variableBinderManager != null) {
                variableBinderManager.pause();
            }
            CommandTriggers commandTriggers = this.mExternalCommandManager;
            if (commandTriggers != null) {
                commandTriggers.pause();
            }
            VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
            if (variableUpdaterManager != null) {
                variableUpdaterManager.pause();
            }
            this.mContext.mResourceManager.pause();
            onHoverChange(null, null);
            ConfigFile configFile = this.mConfig;
            if (configFile != null) {
                configFile.save(this.mContext.mContext.getApplicationContext());
            }
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void pauseAnim(long j) {
        super.pauseAnim(j);
        this.mInnerGroup.pauseAnim(j);
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        this.mInnerGroup.playAnim(j, j2, j3, z, z2);
    }

    public int playSound(String str) {
        return playSound(str, new SoundManager.SoundOptions(false, false, 1.0f));
    }

    public int playSound(String str, SoundManager.SoundOptions soundOptions) {
        if (!TextUtils.isEmpty(str) && shouldPlaySound()) {
            return this.mSoundManager.playSound(str, soundOptions);
        }
        return 0;
    }

    public void playSound(int i, SoundManager.Command command) {
        try {
            this.mSoundManager.playSound(i, command);
        } catch (Exception e) {
            Log.e("ScreenElementRoot", e.toString());
        }
    }

    public boolean postDelayed(Runnable runnable, long j) {
        if (this.mFinished || !this.mLoaded) {
            return false;
        }
        return this.mContext.postDelayed(runnable, j);
    }

    public void removeCallbacks(Runnable runnable) {
        this.mContext.removeCallbacks(runnable);
    }

    public void removeElement(String str) {
        this.mElements.remove(str);
    }

    public void requestFrameRateByCommand(float f) {
        this.mFrameRate = f;
        requestFramerate(f);
        if (f > 0.0f) {
            requestUpdate();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void requestUpdate() {
        if (this.mLoaded) {
            int size = this.mRendererControllers.size();
            for (int i = 0; i < size; i++) {
                this.mRendererControllers.get(i).requestUpdate();
            }
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        this.mInnerGroup.reset(j);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        if (this.mLoaded) {
            super.resume();
            Log.d("ScreenElementRoot", "resume");
            this.mShowDebugLayout = HideSdkDependencyUtils.isShowDebugLayout();
            this.mInnerGroup.performAction("resume");
            this.mInnerGroup.resume();
            VariableBinderManager variableBinderManager = this.mVariableBinderManager;
            if (variableBinderManager != null) {
                variableBinderManager.resume();
            }
            CommandTriggers commandTriggers = this.mExternalCommandManager;
            if (commandTriggers != null) {
                commandTriggers.resume();
            }
            VariableUpdaterManager variableUpdaterManager = this.mVariableUpdaterManager;
            if (variableUpdaterManager != null) {
                variableUpdaterManager.resume();
            }
            this.mContext.mResourceManager.resume();
            setSyncInterval();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    protected void resumeAnim(long j) {
        super.resumeAnim(j);
        this.mInnerGroup.resumeAnim(j);
    }

    public void saveVar(String str, Double d) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            Log.w("ScreenElementRoot", "fail to saveVar, config file is null");
        } else if (d == null) {
            configFile.putNumber(str, "null");
        } else {
            configFile.putNumber(str, d.doubleValue());
        }
    }

    public void saveVar(String str, String str2) {
        ConfigFile configFile = this.mConfig;
        if (configFile == null) {
            Log.w("ScreenElementRoot", "fail to saveVar, config file is null");
        } else {
            configFile.putString(str, str2);
        }
    }

    public void selfFinish() {
        if (this.mLoaded) {
            this.mController.finish();
        }
    }

    public void selfInit() {
        if (this.mLoaded) {
            this.mController.init();
        }
    }

    public void selfPause() {
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            this.mRendererControllers.get(i).selfPause();
        }
    }

    public void selfResume() {
        int size = this.mRendererControllers.size();
        for (int i = 0; i < size; i++) {
            this.mRendererControllers.get(i).selfResume();
        }
    }

    public void setAutoDarkenWallpaper(boolean z) {
        this.mAutoDarkenWallpaper = z;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        ElementGroup elementGroup = this.mInnerGroup;
        if (elementGroup != null) {
            elementGroup.setColorFilter(colorFilter);
        }
    }

    public void setConfiguration(Configuration configuration) {
        if (this.mAllowScreenRotation) {
            Variables variables = this.mContext.mVariables;
            Utils.putVariableNumber("orientation", variables, Double.valueOf(configuration.orientation));
            int i = configuration.orientation;
            if (i == 1) {
                variables.put("raw_screen_width", this.mTargetScreenWidth);
                variables.put("raw_screen_height", this.mTargetScreenHeight);
                variables.put("screen_width", this.mTargetScreenWidth / this.mScale);
                variables.put("screen_height", this.mTargetScreenHeight / this.mScale);
            } else if (i != 2) {
            } else {
                variables.put("raw_screen_width", this.mTargetScreenHeight);
                variables.put("raw_screen_height", this.mTargetScreenWidth);
                variables.put("screen_width", this.mTargetScreenHeight / this.mScale);
                variables.put("screen_height", this.mTargetScreenWidth / this.mScale);
            }
        }
    }

    public void setDarkWallpaperMode(boolean z) {
        this.mDarkWallpaperMode = z;
    }

    public void setDefaultFramerate(float f) {
        this.DEFAULT_FRAME_RATE = f;
    }

    public final void setKeepResource(boolean z) {
        this.mKeepResource = z;
    }

    public void setMamlAccessHelper(MamlAccessHelper mamlAccessHelper) {
        this.mMamlAccessHelper = mamlAccessHelper;
    }

    public void setMamlViewOnExternCommandListener(OnExternCommandListener onExternCommandListener) {
        this.mMamlViewExternCommandListener = onExternCommandListener == null ? null : new WeakReference<>(onExternCommandListener);
    }

    public void setOnHoverChangeListener(OnHoverChangeListener onHoverChangeListener) {
        this.mHoverChangeListenerRef = new WeakReference<>(onHoverChangeListener);
    }

    public void setRenderControllerListener(RendererController.Listener listener) {
        if (this.mLoaded) {
            this.mController.setListener(listener);
        }
    }

    public void setRenderControllerRenderable(RendererController.IRenderable iRenderable) {
        if (this.mLoaded) {
            setRenderControllerListener(new SingleRootListener(this, iRenderable));
        }
    }

    public void setSaveConfigViaProvider(boolean z) {
        this.mSaveConfigViaProvider = z;
        ConfigFile configFile = this.mConfig;
        if (configFile != null) {
            configFile.setSaveViaProvider(z);
        }
    }

    public void setScaleByDensity(boolean z) {
        this.mScaleByDensity = z;
    }

    public void setSyncInterval() {
        int systemFrameRate = (int) getSystemFrameRate();
        int i = systemFrameRate != 0 ? VipService.VIP_SERVICE_FAILURE / systemFrameRate : 16;
        if (i < 1) {
            i = 1;
        }
        RenderVsyncUpdater.getInstance().setSyncInterval(i);
    }

    public void setTouchable(boolean z) {
        this.mTouchable = z;
    }

    public void setViewManager(MamlViewManager mamlViewManager) {
        this.mViewManagerRef = new WeakReference<>(mamlViewManager);
    }

    protected boolean shouldPlaySound() {
        return true;
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void tick(final long j) {
        if (this.mNeedReset) {
            postRunnableAtFrontOfQueue(new Runnable() { // from class: com.miui.maml.ScreenElementRoot.1
                @Override // java.lang.Runnable
                public void run() {
                    ScreenElementRoot.this.reset(j);
                }
            });
            onCommand("init");
            this.mNeedReset = false;
            this.mController.setNeedReset(false);
        }
        doTick(j);
    }

    public final int version() {
        return this.mVersion;
    }
}
