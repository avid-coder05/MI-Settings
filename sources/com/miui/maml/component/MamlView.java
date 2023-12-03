package com.miui.maml.component;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;
import com.miui.maml.R;
import com.miui.maml.RendererController;
import com.miui.maml.ResourceLoader;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Variables;
import com.miui.maml.util.AssetsResourceLoader;
import com.miui.maml.util.FolderResourceLoader;
import com.miui.maml.util.HideSdkDependencyUtils;
import com.miui.maml.util.MamlAccessHelper;
import com.miui.maml.util.MamlViewManager;
import com.miui.maml.util.ZipResourceLoader;
import java.lang.ref.WeakReference;
import java.util.Objects;

/* loaded from: classes2.dex */
public class MamlView extends FrameLayout implements RendererController.IRenderable, MamlViewManager {
    private boolean mAutoFinishRoot;
    private boolean mAutoRemoveCache;
    private boolean mCanvasParamsChanged;
    private final ScreenElementRoot.OnExternCommandListener mCommandListener;
    private WeakReference<OnExternCommandListener> mExternCommandListener;
    private volatile boolean mFinished;
    private int mLastBlurRatio;
    private WindowManager.LayoutParams mLp;
    private MamlAccessHelper mMamlAccessHelper;
    protected boolean mNeedDisallowInterceptTouchEvent;
    private int mPivotX;
    private int mPivotY;
    protected ScreenElementRoot mRoot;
    private float mScale;
    private InnerView mView;
    private WindowManager mWindowManager;
    private float mX;
    private float mY;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class InnerView extends View {
        public InnerView(Context context) {
            super(context);
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            MamlView mamlView = MamlView.this;
            if (mamlView.mRoot == null) {
                return;
            }
            if (!mamlView.mCanvasParamsChanged) {
                MamlView.this.mRoot.render(canvas);
                return;
            }
            int save = canvas.save();
            canvas.translate(MamlView.this.mX, MamlView.this.mY);
            if (MamlView.this.mScale != 0.0f) {
                canvas.scale(MamlView.this.mScale, MamlView.this.mScale, MamlView.this.mPivotX, MamlView.this.mPivotY);
            }
            MamlView.this.mRoot.render(canvas);
            canvas.restoreToCount(save);
        }
    }

    /* loaded from: classes2.dex */
    public interface OnExternCommandListener {
        void onCommand(String str, Double d, String str2);
    }

    public MamlView(Context context, AttributeSet attributeSet) {
        super(context);
        this.mAutoFinishRoot = true;
        this.mCommandListener = new ScreenElementRoot.OnExternCommandListener() { // from class: com.miui.maml.component.MamlView.1
            @Override // com.miui.maml.ScreenElementRoot.OnExternCommandListener
            public void onCommand(String str, Double d, String str2) {
                OnExternCommandListener onExternCommandListener;
                if (MamlView.this.mExternCommandListener == null || (onExternCommandListener = (OnExternCommandListener) MamlView.this.mExternCommandListener.get()) == null) {
                    return;
                }
                onExternCommandListener.onCommand(str, d, str2);
            }
        };
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.MamlView);
        String string = obtainStyledAttributes.getString(R.styleable.MamlView_path);
        String string2 = obtainStyledAttributes.getString(R.styleable.MamlView_innerPath);
        int i = obtainStyledAttributes.getInt(R.styleable.MamlView_resMode, 2);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.MamlView_autoRemoveCache, false);
        boolean z2 = obtainStyledAttributes.getBoolean(R.styleable.MamlView_touchable, true);
        load(context, getResourceLoader(context, string, string2, i), z);
        setTouchable(z2);
    }

    private void blurBackground() {
        try {
            ScreenElementRoot screenElementRoot = this.mRoot;
            if (screenElementRoot == null || !screenElementRoot.isMamlBlurWindow() || this.mLp == null || !this.mRoot.getVariables().existsDouble("__blur_ratio")) {
                return;
            }
            int i = (int) this.mRoot.getVariables().getDouble("__blur_ratio");
            if (i < 0) {
                i = 0;
            } else if (i > 100) {
                i = 100;
            }
            if (i != this.mLastBlurRatio) {
                this.mLastBlurRatio = i;
                if (i == 0) {
                    this.mLp.flags &= -5;
                } else {
                    HideSdkDependencyUtils.WindowManager_LayoutParams_setLayoutParamsBlurRatio(this.mLp, (i * 1.0f) / 100.0f);
                    this.mLp.flags |= 4;
                }
                this.mWindowManager.updateViewLayout(this, this.mLp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void finish() {
        if (this.mFinished || !this.mAutoFinishRoot) {
            return;
        }
        this.mFinished = true;
        setOnTouchListener(null);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.selfFinish();
            this.mRoot.detachFromVsync();
            this.mRoot.getVariables().reset();
            removeAccessHelperRef();
            if (this.mAutoRemoveCache) {
                removeRelatedBitmapsRef();
            }
            this.mRoot = null;
        }
    }

    private ResourceLoader getResourceLoader(Context context, String str, String str2, int i) {
        if (i != 1) {
            if (i != 2) {
                if (i != 3) {
                    return null;
                }
                return new FolderResourceLoader(str);
            }
            return new AssetsResourceLoader(context, str);
        }
        return new ZipResourceLoader(str, str2);
    }

    private void initMamlview(Context context, ScreenElementRoot screenElementRoot) {
        setClickable(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
        Objects.requireNonNull(screenElementRoot);
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mView = new InnerView(context);
        addView(this.mView, new ViewGroup.LayoutParams(-1, -1));
        this.mRoot = screenElementRoot;
        screenElementRoot.setViewManager(this);
        this.mRoot.setOnHoverChangeListener(new ScreenElementRoot.OnHoverChangeListener() { // from class: com.miui.maml.component.MamlView.2
            @Override // com.miui.maml.ScreenElementRoot.OnHoverChangeListener
            public void onHoverChange(String str) {
                MamlView.this.setContentDescription(str);
                MamlView.this.sendAccessibilityEvent(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
            }
        });
        init();
    }

    private void load(Context context, ResourceLoader resourceLoader, boolean z) {
        this.mAutoRemoveCache = z;
        if (resourceLoader != null) {
            ScreenElementRoot screenElementRoot = new ScreenElementRoot(new ScreenContext(context.getApplicationContext(), new ResourceManager(resourceLoader)));
            if (screenElementRoot.load()) {
                screenElementRoot.setKeepResource(true);
                initMamlview(context, screenElementRoot);
            }
        }
    }

    private void removeAccessHelperRef() {
        this.mRoot.setMamlAccessHelper(null);
        MamlAccessHelper mamlAccessHelper = this.mMamlAccessHelper;
        if (mamlAccessHelper != null) {
            mamlAccessHelper.removeRoot();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        MamlAccessHelper mamlAccessHelper = this.mMamlAccessHelper;
        if (mamlAccessHelper == null || !mamlAccessHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public void doRender() {
        this.mView.postInvalidate();
        blurBackground();
    }

    protected void finalize() throws Throwable {
        finish();
        super.finalize();
    }

    public int getMamlVersionCode() {
        return 1;
    }

    public String getMamlVersionName() {
        return "1.0.0";
    }

    @Override // android.view.View
    protected int getSuggestedMinimumHeight() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return (int) screenElementRoot.getHeight();
        }
        return -1;
    }

    @Override // android.view.View
    protected int getSuggestedMinimumWidth() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            return (int) screenElementRoot.getWidth();
        }
        return -1;
    }

    public void init() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setConfiguration(getResources().getConfiguration());
            this.mRoot.setMamlViewOnExternCommandListener(this.mCommandListener);
            this.mRoot.setRenderControllerRenderable(this);
            this.mRoot.attachToVsync();
            this.mRoot.selfInit();
            if (Build.VERSION.SDK_INT >= 23) {
                MamlAccessHelper mamlAccessHelper = new MamlAccessHelper(this.mRoot, this);
                this.mMamlAccessHelper = mamlAccessHelper;
                ViewCompat.setAccessibilityDelegate(this, mamlAccessHelper);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onResume();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.onConfigurationChanged(configuration);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onPause();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.onHover(motionEvent);
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            accessibilityNodeInfo.setText(screenElementRoot.getRawAttr("accessibilityText"));
        }
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            Variables variables = screenElementRoot.getContext().mVariables;
            variables.put("view_width", (i3 - i) / this.mRoot.getScale());
            variables.put("view_height", (i4 - i2) / this.mRoot.getScale());
            ViewParent parent = getParent();
            while (parent instanceof View) {
                View view = (View) parent;
                i += view.getLeft() - view.getScrollX();
                i2 += view.getTop() - view.getScrollY();
                parent = view.getParent();
            }
            variables.put("view_x", i / this.mRoot.getScale());
            variables.put("view_y", i2 / this.mRoot.getScale());
            this.mRoot.requestUpdate();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        ScreenElementRoot screenElementRoot = this.mRoot;
        int width = screenElementRoot != null ? (int) screenElementRoot.getWidth() : 0;
        ScreenElementRoot screenElementRoot2 = this.mRoot;
        int height = screenElementRoot2 != null ? (int) screenElementRoot2.getHeight() : 0;
        if (mode == Integer.MIN_VALUE && width > 0) {
            size = Math.min(size, width);
        }
        if (mode2 == Integer.MIN_VALUE && height > 0) {
            size2 = Math.min(size2, height);
        }
        setMeasuredDimension(size, size2);
    }

    public void onPause() {
        Log.d("MamlView", "onPause");
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.selfPause();
        }
    }

    public void onResume() {
        Log.d("MamlView", "onResume");
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.selfResume();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            boolean needDisallowInterceptTouchEvent = screenElementRoot.needDisallowInterceptTouchEvent();
            if (this.mNeedDisallowInterceptTouchEvent != needDisallowInterceptTouchEvent) {
                getParent().requestDisallowInterceptTouchEvent(needDisallowInterceptTouchEvent);
                this.mNeedDisallowInterceptTouchEvent = needDisallowInterceptTouchEvent;
            }
            z = this.mRoot.onTouch(motionEvent);
        } else {
            z = false;
        }
        return z || super.onTouchEvent(motionEvent);
    }

    public void removeRelatedBitmapsRef() {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.getContext().mResourceManager.clearByKeys();
        }
    }

    public void setAutoDarkenWallpaper(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setAutoDarkenWallpaper(true);
        }
    }

    public void setAutoFinishRoot(boolean z) {
        this.mAutoFinishRoot = z;
    }

    public void setCacheSize(int i) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.getContext().mResourceManager.setCacheSize(i);
        }
    }

    @Deprecated
    public final void setKeepResource(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setKeepResource(z);
        }
    }

    public void setOnExternCommandListener(OnExternCommandListener onExternCommandListener) {
        this.mExternCommandListener = onExternCommandListener == null ? null : new WeakReference<>(onExternCommandListener);
    }

    public void setSaveConfigOnlyInPause(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setSaveConfigViaProvider(z);
        }
    }

    public void setSaveConfigViaProvider(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setSaveConfigViaProvider(z);
        }
    }

    public void setTouchable(boolean z) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            screenElementRoot.setTouchable(z);
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 0) {
            onResume();
        } else if (i == 4 || i == 8) {
            onPause();
        }
    }

    @Deprecated
    public void setWindowLayoutParams(WindowManager.LayoutParams layoutParams) {
        this.mLp = layoutParams;
    }
}
