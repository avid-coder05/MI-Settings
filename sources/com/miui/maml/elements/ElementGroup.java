package com.miui.maml.elements;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.util.Log;
import android.view.MotionEvent;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.IndexedVariable;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public class ElementGroup extends AnimatedScreenElement {
    protected boolean mClip;
    protected ArrayList<ScreenElement> mElements;
    private boolean mHovered;
    private IndexedVariable mIndexVar;
    private boolean mLayered;
    private LinearDirection mLinearDirection;
    private boolean mTouched;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.miui.maml.elements.ElementGroup$1  reason: invalid class name */
    /* loaded from: classes2.dex */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection;

        static {
            int[] iArr = new int[LinearDirection.values().length];
            $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection = iArr;
            try {
                iArr[LinearDirection.Horizontal.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection[LinearDirection.Vertical.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public enum LinearDirection {
        None,
        Horizontal,
        Vertical
    }

    private ElementGroup(ScreenElementRoot screenElementRoot, IndexedVariable indexedVariable) {
        super(null, screenElementRoot);
        this.mElements = new ArrayList<>();
        this.mLinearDirection = LinearDirection.None;
        this.mIndexVar = indexedVariable;
    }

    public ElementGroup(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mElements = new ArrayList<>();
        this.mLinearDirection = LinearDirection.None;
        load(element);
    }

    public static ElementGroup createArrayGroup(ScreenElementRoot screenElementRoot, IndexedVariable indexedVariable) {
        return new ElementGroup(screenElementRoot, indexedVariable);
    }

    public static boolean isArrayGroup(ScreenElement screenElement) {
        return (screenElement instanceof ElementGroup) && ((ElementGroup) screenElement).isArray();
    }

    private void load(Element element) {
        if (element == null) {
            return;
        }
        this.mClip = Boolean.parseBoolean(getAttr(element, "clip"));
        this.mLayered = Boolean.parseBoolean(getAttr(element, "layered"));
        String attr = getAttr(element, "linear");
        if ("h".equalsIgnoreCase(attr)) {
            this.mLinearDirection = LinearDirection.Horizontal;
        } else if ("v".equalsIgnoreCase(attr)) {
            this.mLinearDirection = LinearDirection.Vertical;
        }
        NodeList childNodes = element.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            if (childNodes.item(i).getNodeType() == 1) {
                addElement(onCreateChild((Element) childNodes.item(i)));
            }
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void acceptVisitor(ScreenElementVisitor screenElementVisitor) {
        super.acceptVisitor(screenElementVisitor);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).acceptVisitor(screenElementVisitor);
        }
    }

    public void addElement(ScreenElement screenElement) {
        if (screenElement != null) {
            screenElement.setParent(this);
            this.mElements.add(screenElement);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doRender(Canvas canvas) {
        float width = getWidth();
        float height = getHeight();
        float left = getLeft(0.0f, width);
        float top = getTop(0.0f, height);
        int save = (!this.mLayered || width <= 0.0f || height <= 0.0f) ? canvas.save() : canvas.saveLayerAlpha(left, top, left + width, top + height, getAlpha(), 31);
        canvas.translate(left, top);
        if (width > 0.0f && height > 0.0f && this.mClip) {
            canvas.clipRect(0.0f, 0.0f, width, height);
        }
        doRenderChildren(canvas);
        canvas.restoreToCount(save);
    }

    protected void doRenderChildren(Canvas canvas) {
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            ScreenElement screenElement = this.mElements.get(i);
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set(i);
            }
            screenElement.render(canvas);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        doTickChildren(j);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doTickChildren(long j) {
        float height;
        int size = this.mElements.size();
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i = 0; i < size; i++) {
            ScreenElement screenElement = this.mElements.get(i);
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set(i);
            }
            screenElement.tick(j);
            if (this.mLinearDirection != LinearDirection.None && (screenElement instanceof AnimatedScreenElement) && screenElement.isVisible()) {
                AnimatedScreenElement animatedScreenElement = (AnimatedScreenElement) screenElement;
                int i2 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection[this.mLinearDirection.ordinal()];
                if (i2 == 1) {
                    float marginLeft = f + animatedScreenElement.getMarginLeft();
                    animatedScreenElement.setX(marginLeft);
                    f = marginLeft + animatedScreenElement.getWidth() + animatedScreenElement.getMarginRight();
                    height = animatedScreenElement.getHeight();
                    if (f2 >= height) {
                    }
                    f2 = height;
                } else if (i2 == 2) {
                    float marginTop = f + animatedScreenElement.getMarginTop();
                    animatedScreenElement.setY(marginTop);
                    f = marginTop + animatedScreenElement.getHeight() + animatedScreenElement.getMarginBottom();
                    height = animatedScreenElement.getWidth();
                    if (f2 >= height) {
                    }
                    f2 = height;
                }
            }
        }
        int i3 = AnonymousClass1.$SwitchMap$com$miui$maml$elements$ElementGroup$LinearDirection[this.mLinearDirection.ordinal()];
        if (i3 == 1) {
            double d = f;
            setWidth(d);
            double d2 = f2;
            setHeight(d2);
            setActualWidth(descale(d));
            setActualHeight(descale(d2));
        } else if (i3 == 2) {
            double d3 = f;
            setHeight(d3);
            double d4 = f2;
            setWidth(d4);
            setActualHeight(descale(d3));
            setActualWidth(descale(d4));
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public ScreenElement findElement(String str) {
        ScreenElement findElement = super.findElement(str);
        if (findElement != null) {
            return findElement;
        }
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            ScreenElement findElement2 = this.mElements.get(i).findElement(str);
            if (findElement2 != null) {
                return findElement2;
            }
        }
        return null;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            try {
                this.mElements.get(i).finish();
            } catch (Exception e) {
                Log.e("MAML_ElementGroup", e.toString());
                e.printStackTrace();
            }
        }
    }

    public ScreenElement getChild(int i) {
        if (i < 0 || i >= this.mElements.size()) {
            return null;
        }
        return this.mElements.get(i);
    }

    public ArrayList<ScreenElement> getElements() {
        return this.mElements;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getParentLeft() {
        float left = getLeft();
        ElementGroup elementGroup = this.mParent;
        return left + (elementGroup == null ? 0.0f : elementGroup.getParentLeft());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getParentTop() {
        float top = getTop();
        ElementGroup elementGroup = this.mParent;
        return top + (elementGroup == null ? 0.0f : elementGroup.getParentTop());
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set(i);
            }
            this.mElements.get(i).init();
        }
    }

    public boolean isArray() {
        return this.mIndexVar != null;
    }

    public boolean isLayered() {
        return this.mLayered;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ScreenElement onCreateChild(Element element) {
        return getContext().mFactory.createInstance(element, this.mRoot);
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public boolean onHover(MotionEvent motionEvent) {
        boolean z = false;
        if (isVisible()) {
            boolean z2 = touched(motionEvent.getX(), motionEvent.getY());
            if (this.mClip && !z2) {
                if (!this.mHovered) {
                    return false;
                }
                motionEvent.setAction(10);
            }
            int size = this.mElements.size() - 1;
            while (true) {
                if (size < 0) {
                    break;
                }
                ScreenElement screenElement = this.mElements.get(size);
                IndexedVariable indexedVariable = this.mIndexVar;
                if (indexedVariable != null) {
                    indexedVariable.set(size);
                }
                if (screenElement.onHover(motionEvent)) {
                    z = true;
                    break;
                }
                size--;
            }
            boolean onHover = z ? true : super.onHover(motionEvent);
            this.mHovered = onHover;
            return onHover;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:38:0x007d  */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onTouch(android.view.MotionEvent r10) {
        /*
            r9 = this;
            boolean r0 = r9.isVisible()
            r1 = 0
            if (r0 != 0) goto L8
            return r1
        L8:
            int r0 = r10.getAction()
            float r2 = r10.getX()
            float r3 = r10.getY()
            boolean r2 = r9.touched(r2, r3)
            boolean r3 = r9.mClip
            if (r3 == 0) goto L27
            if (r2 != 0) goto L27
            boolean r2 = r9.mTouched
            if (r2 != 0) goto L23
            return r1
        L23:
            r2 = 3
            r10.setAction(r2)
        L27:
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r2 = r9.mElements
            int r2 = r2.size()
            com.miui.maml.ScreenElementRoot r3 = r9.mRoot
            int r3 = r3.version()
            r4 = 2
            r5 = 1
            if (r3 < r4) goto L39
            r3 = r5
            goto L3a
        L39:
            r3 = r1
        L3a:
            if (r3 == 0) goto L59
            int r2 = r2 - r5
        L3d:
            if (r2 < 0) goto L77
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r3 = r9.mElements
            java.lang.Object r3 = r3.get(r2)
            com.miui.maml.elements.ScreenElement r3 = (com.miui.maml.elements.ScreenElement) r3
            com.miui.maml.data.IndexedVariable r4 = r9.mIndexVar
            if (r4 == 0) goto L4f
            double r6 = (double) r2
            r4.set(r6)
        L4f:
            boolean r3 = r3.onTouch(r10)
            if (r3 == 0) goto L56
            goto L72
        L56:
            int r2 = r2 + (-1)
            goto L3d
        L59:
            r3 = r1
        L5a:
            if (r3 >= r2) goto L77
            java.util.ArrayList<com.miui.maml.elements.ScreenElement> r4 = r9.mElements
            java.lang.Object r4 = r4.get(r3)
            com.miui.maml.elements.ScreenElement r4 = (com.miui.maml.elements.ScreenElement) r4
            com.miui.maml.data.IndexedVariable r6 = r9.mIndexVar
            if (r6 == 0) goto L6c
            double r7 = (double) r3
            r6.set(r7)
        L6c:
            boolean r4 = r4.onTouch(r10)
            if (r4 == 0) goto L74
        L72:
            r1 = r5
            goto L77
        L74:
            int r3 = r3 + 1
            goto L5a
        L77:
            r10.setAction(r0)
            if (r1 == 0) goto L7d
            goto L81
        L7d:
            boolean r5 = super.onTouch(r10)
        L81:
            r9.mTouched = r5
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.ElementGroup.onTouch(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).updateVisibility();
        }
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).pause();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void pauseAnim(long j) {
        super.pauseAnim(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).pauseAnim(j);
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void playAnim(long j, long j2, long j3, boolean z, boolean z2) {
        super.playAnim(j, j2, j3, z, z2);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set(i);
            }
            this.mElements.get(i).playAnim(j, j2, j3, z, z2);
        }
    }

    public void removeAllElements() {
        this.mElements.clear();
        requestUpdate();
    }

    public void removeElement(ScreenElement screenElement) {
        this.mElements.remove(screenElement);
        requestUpdate();
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void reset(long j) {
        super.reset(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).reset(j);
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).resume();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void resumeAnim(long j) {
        super.resumeAnim(j);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).resumeAnim(j);
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void setAnim(String[] strArr) {
        super.setAnim(strArr);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            IndexedVariable indexedVariable = this.mIndexVar;
            if (indexedVariable != null) {
                indexedVariable.set(i);
            }
            this.mElements.get(i).setAnim(strArr);
        }
    }

    public void setClip(boolean z) {
        this.mClip = z;
    }

    @Override // com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        int size = this.mElements.size();
        for (int i = 0; i < size; i++) {
            this.mElements.get(i).setColorFilter(colorFilter);
        }
    }
}
