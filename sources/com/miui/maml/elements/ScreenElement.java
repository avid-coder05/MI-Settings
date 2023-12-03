package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import com.miui.maml.CommandTriggers;
import com.miui.maml.FramerateTokenList;
import com.miui.maml.RendererController;
import com.miui.maml.ScreenContext;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.StylesManager;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.util.StyleHelper;
import com.miui.maml.util.Utils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import miui.yellowpage.Tag;
import miui.yellowpage.YellowPageStatistic;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class ScreenElement {
    private IndexedVariable mActualHeightVar;
    private IndexedVariable mActualWidthVar;
    protected Align mAlign;
    protected AlignV mAlignV;
    protected ArrayList<BaseAnimation> mAnimations;
    protected String mCategory;
    private float mCurFramerate;
    private FramerateTokenList.FramerateToken mFramerateToken;
    protected boolean mHasName;
    protected String mName;
    protected ElementGroup mParent;
    protected boolean mResumed;
    protected ScreenElementRoot mRoot;
    protected StylesManager.Style mStyle;
    protected CommandTriggers mTriggers;
    private Expression mVisibilityExpression;
    private IndexedVariable mVisibilityVar;
    private boolean mInitShow = true;
    private boolean mShow = true;
    private boolean mIsVisible = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.ScreenElement$2  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$Align;
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV;

        static {
            int[] iArr = new int[Align.values().length];
            $SwitchMap$com$miui$maml$elements$ScreenElement$Align = iArr;
            try {
                iArr[Align.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ScreenElement$Align[Align.RIGHT.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            int[] iArr2 = new int[AlignV.values().length];
            $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV = iArr2;
            try {
                iArr2[AlignV.CENTER.ordinal()] = 1;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ScreenElement$AlignV[AlignV.BOTTOM.ordinal()] = 2;
            } catch (NoSuchFieldError unused4) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public enum Align {
        LEFT,
        CENTER,
        RIGHT
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public enum AlignV {
        TOP,
        CENTER,
        BOTTOM
    }

    public ScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        if (element != null && screenElementRoot != null) {
            this.mStyle = screenElementRoot.getStyle(element.getAttribute(Tag.TagServicesData.GROUP_STYLE));
        }
        load(element);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static boolean isTagEnable(String[] strArr, String str) {
        return strArr == null ? TextUtils.isEmpty(str) : Utils.arrayContains(strArr, str);
    }

    private void load(Element element) {
        if (element == null) {
            return;
        }
        this.mCategory = getAttr(element, YellowPageStatistic.Display.CATEGORY);
        String attr = getAttr(element, "name");
        this.mName = attr;
        boolean z = !TextUtils.isEmpty(attr);
        this.mHasName = z;
        if (z) {
            String attr2 = getAttr(element, "namesSuffix");
            if (!TextUtils.isEmpty(attr2)) {
                this.mName += attr2;
            }
            if (!Boolean.parseBoolean(getAttr(element, "dontAddToMap"))) {
                getRoot().addElement(this.mName, new WeakReference(this));
            }
        }
        String attr3 = getAttr(element, "visibility");
        if (!TextUtils.isEmpty(attr3)) {
            if (attr3.equalsIgnoreCase("false")) {
                this.mInitShow = false;
            } else if (attr3.equalsIgnoreCase("true")) {
                this.mInitShow = true;
            } else {
                this.mVisibilityExpression = Expression.build(getVariables(), attr3);
            }
        }
        Align align = Align.LEFT;
        this.mAlign = align;
        String attr4 = getAttr(element, "align");
        if (TextUtils.isEmpty(attr4)) {
            attr4 = getAttr(element, "alignH");
        }
        if (attr4.equalsIgnoreCase("right")) {
            this.mAlign = Align.RIGHT;
        } else if (attr4.equalsIgnoreCase("left")) {
            this.mAlign = align;
        } else if (attr4.equalsIgnoreCase("center")) {
            this.mAlign = Align.CENTER;
        }
        AlignV alignV = AlignV.TOP;
        this.mAlignV = alignV;
        String attr5 = getAttr(element, "alignV");
        if (attr5.equalsIgnoreCase("bottom")) {
            this.mAlignV = AlignV.BOTTOM;
        } else if (attr5.equalsIgnoreCase("top")) {
            this.mAlignV = alignV;
        } else if (attr5.equalsIgnoreCase("center")) {
            this.mAlignV = AlignV.CENTER;
        }
        loadTriggers(element);
        loadAnimations(element);
    }

    private void loadAnimations(Element element) {
        Utils.traverseXmlElementChildren(element, null, new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.ScreenElement.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                BaseAnimation onCreateAnimation;
                String nodeName = element2.getNodeName();
                if (!nodeName.endsWith("Animation") || (onCreateAnimation = ScreenElement.this.onCreateAnimation(nodeName, element2)) == null) {
                    return;
                }
                ScreenElement screenElement = ScreenElement.this;
                if (screenElement.mAnimations == null) {
                    screenElement.mAnimations = new ArrayList<>();
                }
                ScreenElement.this.mAnimations.add(onCreateAnimation);
            }
        });
    }

    private void setVisibilityVar(boolean z) {
        if (this.mHasName) {
            if (this.mVisibilityVar == null) {
                this.mVisibilityVar = new IndexedVariable(this.mName + ".visibility", getContext().mVariables, true);
            }
            this.mVisibilityVar.set(z ? 1.0d : 0.0d);
        }
    }

    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        screenElementVisitor.visit(this);
    }

    public FramerateTokenList.FramerateToken createToken(String str) {
        RendererController rendererController = getRendererController();
        if (rendererController == null) {
            return null;
        }
        return rendererController.createToken(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final double descale(double d) {
        return d / this.mRoot.getScale();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract void doRender(Canvas canvas);

    /* JADX INFO: Access modifiers changed from: protected */
    public void doTick(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).tick(j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final double evaluate(Expression expression) {
        if (expression == null) {
            return 0.0d;
        }
        return expression.evaluate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final String evaluateStr(Expression expression) {
        if (expression == null) {
            return null;
        }
        return expression.evaluateStr();
    }

    public ScreenElement findElement(String str) {
        String str2 = this.mName;
        if (str2 == null || !str2.equals(str)) {
            return null;
        }
        return this;
    }

    public void finish() {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.finish();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).finish();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public String getAttr(Element element, String str) {
        return StyleHelper.getAttr(element, str, this.mStyle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getAttrAsFloat(Element element, String str, float f) {
        String attr = getAttr(element, str);
        if (!TextUtils.isEmpty(attr)) {
            try {
                return Float.parseFloat(attr);
            } catch (NumberFormatException unused) {
            }
        }
        return f;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getAttrAsInt(Element element, String str, int i) {
        String attr = getAttr(element, str);
        if (!TextUtils.isEmpty(attr)) {
            try {
                return Integer.parseInt(attr);
            } catch (NumberFormatException unused) {
            }
        }
        return i;
    }

    public ScreenContext getContext() {
        return this.mRoot.getContext();
    }

    protected final float getFramerate() {
        FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
        if (framerateToken == null) {
            return 0.0f;
        }
        return framerateToken.getFramerate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getLeft(float f, float f2) {
        if (f2 <= 0.0f) {
            return f;
        }
        int i = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ScreenElement$Align[this.mAlign.ordinal()];
        if (i == 1) {
            f2 /= 2.0f;
        } else if (i != 2) {
            return f;
        }
        return f - f2;
    }

    public String getName() {
        return this.mName;
    }

    public RendererController getRendererController() {
        ElementGroup elementGroup = this.mParent;
        if (elementGroup != null) {
            return elementGroup.getRendererController();
        }
        return null;
    }

    public ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getTop(float f, float f2) {
        if (f2 <= 0.0f) {
            return f;
        }
        int i = AnonymousClass2.$SwitchMap$com$miui$maml$elements$ScreenElement$AlignV[this.mAlignV.ordinal()];
        if (i == 1) {
            f2 /= 2.0f;
        } else if (i != 2) {
            return f;
        }
        return f - f2;
    }

    public final Variables getVariables() {
        return getContext().mVariables;
    }

    public void init() {
        this.mShow = this.mInitShow;
        FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
        if (framerateToken != null) {
            removeToken(framerateToken);
        }
        this.mFramerateToken = null;
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.init();
        }
        setAnim(null);
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).init();
            }
        }
        updateVisibility();
        setVisibilityVar(isVisible());
        performAction("init");
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    protected boolean isVisibleInner() {
        Expression expression;
        ElementGroup elementGroup;
        return this.mShow && ((expression = this.mVisibilityExpression) == null || expression.evaluate() > 0.0d) && ((elementGroup = this.mParent) == null || elementGroup.isVisible());
    }

    protected void loadTriggers(Element element) {
        Element child = Utils.getChild(element, "Triggers");
        if (child != null) {
            this.mTriggers = new CommandTriggers(child, this);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public BaseAnimation onCreateAnimation(String str, Element element) {
        return null;
    }

    public boolean onHover(MotionEvent motionEvent) {
        return false;
    }

    protected void onSetAnimBefore() {
    }

    protected void onSetAnimEnable(BaseAnimation baseAnimation) {
    }

    public boolean onTouch(MotionEvent motionEvent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onVisibilityChange(boolean z) {
        setVisibilityVar(z);
        if (z) {
            requestFramerate(this.mCurFramerate);
            return;
        }
        this.mCurFramerate = getFramerate();
        requestFramerate(0.0f);
    }

    public void pause() {
        this.mResumed = false;
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.pause();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).pause();
            }
        }
    }

    public final void pauseAnim() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        pauseAnim(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void pauseAnim(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).pauseAnim(j);
            }
        }
    }

    public void performAction(String str) {
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers == null || str == null) {
            return;
        }
        commandTriggers.onAction(str);
        requestUpdate();
    }

    public final void playAnim() {
        playAnim(0L, -1L, true, true);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).playAnim(j, j2, j3, z, z2);
            }
        }
    }

    public final void playAnim(long j, long j2, boolean z, boolean z2) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        playAnim(elapsedRealtime, j, j2, z, z2);
        doTick(elapsedRealtime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void postInMainThread(Runnable runnable) {
        getContext().getHandler().post(runnable);
    }

    public void postRunnable(Runnable runnable) {
        RendererController rendererController = this.mRoot.getRendererController();
        if (rendererController != null) {
            rendererController.postRunnable(runnable);
        }
    }

    public void postRunnableAtFrontOfQueue(Runnable runnable) {
        RendererController rendererController = this.mRoot.getRendererController();
        if (rendererController != null) {
            rendererController.postRunnableAtFrontOfQueue(runnable);
        }
    }

    public void removeToken(FramerateTokenList.FramerateToken framerateToken) {
        RendererController rendererController = getRendererController();
        if (rendererController != null) {
            rendererController.removeToken(framerateToken);
        }
    }

    public void render(Canvas canvas) {
        updateVisibility();
        if (isVisible()) {
            doRender(canvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void requestFramerate(float f) {
        if (f < 0.0f) {
            return;
        }
        if (this.mFramerateToken == null) {
            if (f == 0.0f) {
                return;
            }
            this.mFramerateToken = createToken(toString());
        }
        if (this.mFramerateToken != null) {
            float systemFrameRate = this.mRoot.getSystemFrameRate();
            FramerateTokenList.FramerateToken framerateToken = this.mFramerateToken;
            if (f > systemFrameRate) {
                f = systemFrameRate;
            }
            framerateToken.requestFramerate(f);
        }
    }

    public void requestUpdate() {
        RendererController rendererController = getRendererController();
        if (rendererController != null) {
            rendererController.forceUpdate();
        }
    }

    public final void reset() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        reset(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    public void reset(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).reset(j);
            }
        }
    }

    public void resume() {
        this.mResumed = true;
        CommandTriggers commandTriggers = this.mTriggers;
        if (commandTriggers != null) {
            commandTriggers.resume();
        }
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).resume();
            }
        }
    }

    public final void resumeAnim() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        resumeAnim(elapsedRealtime);
        doTick(elapsedRealtime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resumeAnim(long j) {
        ArrayList<BaseAnimation> arrayList = this.mAnimations;
        if (arrayList != null) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.mAnimations.get(i).resumeAnim(j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final float scale(double d) {
        return (float) (d * this.mRoot.getScale());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setActualHeight(double d) {
        if (this.mHasName) {
            if (this.mActualHeightVar == null) {
                this.mActualHeightVar = new IndexedVariable(this.mName + ".actual_h", getVariables(), true);
            }
            this.mActualHeightVar.set(d);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setActualWidth(double d) {
        if (this.mHasName) {
            if (this.mActualWidthVar == null) {
                this.mActualWidthVar = new IndexedVariable(this.mName + ".actual_w", getVariables(), true);
            }
            this.mActualWidthVar.set(d);
        }
    }

    public void setAnim(String[] strArr) {
        if (this.mAnimations != null) {
            onSetAnimBefore();
            int size = this.mAnimations.size();
            for (int i = 0; i < size; i++) {
                BaseAnimation baseAnimation = this.mAnimations.get(i);
                boolean isTagEnable = isTagEnable(strArr, baseAnimation.getTag());
                baseAnimation.setDisable(!isTagEnable);
                if (isTagEnable) {
                    onSetAnimEnable(baseAnimation);
                }
            }
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void setName(String str) {
        getRoot().removeElement(this.mName);
        this.mName = str;
        getRoot().addElement(str, new WeakReference(this));
    }

    public void setParent(ElementGroup elementGroup) {
        this.mParent = elementGroup;
    }

    public void show(boolean z) {
        this.mShow = z;
        updateVisibility();
        requestUpdate();
    }

    public void tick(long j) {
        updateVisibility();
        if (isVisible()) {
            doTick(j);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateVisibility() {
        boolean isVisibleInner = isVisibleInner();
        if (this.mIsVisible != isVisibleInner) {
            this.mIsVisible = isVisibleInner;
            onVisibilityChange(isVisibleInner);
            if (isVisibleInner) {
                doTick(SystemClock.elapsedRealtime());
            }
        }
    }
}
