package com.miui.maml.elements;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import androidx.collection.ArraySet;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.animation.AlphaAnimation;
import com.miui.maml.animation.BaseAnimation;
import com.miui.maml.animation.PositionAnimation;
import com.miui.maml.animation.RotationAnimation;
import com.miui.maml.animation.ScaleAnimation;
import com.miui.maml.animation.SizeAnimation;
import com.miui.maml.data.Expression;
import com.miui.maml.data.IndexedVariable;
import com.miui.maml.data.Variables;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.maml.folme.AnimatedTarget;
import com.miui.maml.folme.MamlTransitionListener;
import com.miui.maml.folme.PropertyWrapper;
import com.miui.maml.folme.TransitionListenerWrapper;
import com.miui.maml.util.ColorParser;
import com.miui.maml.util.Utils;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import miui.provider.MiCloudSmsCmd;
import miui.yellowpage.Tag;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.animation.controller.AnimState;
import miuix.animation.property.FloatProperty;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public abstract class AnimatedScreenElement extends ScreenElement {
    private IndexedVariable mActualXVar;
    private IndexedVariable mActualYVar;
    protected int mAlpha;
    public PropertyWrapper mAlphaProperty;
    private AlphaAnimation mAlphas;
    private AnimatedTarget mAnimTarget;
    private Camera mCamera;
    protected String mContentDescription;
    protected Expression mContentDescriptionExp;
    private boolean mFolmeMode;
    protected boolean mHasContentDescription;
    public PropertyWrapper mHeightProperty;
    protected boolean mInterceptTouch;
    private boolean mIsHaptic;
    private MamlTransitionListener mListener;
    private TransitionListenerWrapper mListenerWrapper;
    private float mMarginBottom;
    private float mMarginLeft;
    private float mMarginRight;
    private float mMarginTop;
    private Matrix mMatrix;
    private Paint mPaint;
    public PropertyWrapper mPivotXProperty;
    public PropertyWrapper mPivotYProperty;
    public PropertyWrapper mPivotZProperty;
    private PositionAnimation mPositions;
    protected boolean mPressed;
    public PropertyWrapper mRotationProperty;
    public PropertyWrapper mRotationXProperty;
    public PropertyWrapper mRotationYProperty;
    public PropertyWrapper mRotationZProperty;
    private RotationAnimation mRotations;
    private Expression mScaleExpression;
    public PropertyWrapper mScaleXProperty;
    public PropertyWrapper mScaleYProperty;
    private ScaleAnimation mScales;
    private SizeAnimation mSizes;
    private FunctionElement mTickListener;
    protected boolean mTintChanged;
    protected int mTintColor;
    protected ColorParser mTintColorParser;
    public PropertyWrapper mTintColorProperty;
    protected PorterDuffColorFilter mTintFilter;
    protected PorterDuff.Mode mTintMode;
    protected Expression mTintModeExp;
    public CopyOnWriteArraySet<FloatProperty> mToProperties;
    protected boolean mTouchable;
    private int mVirtualViewId;
    public PropertyWrapper mWidthProperty;
    public PropertyWrapper mXProperty;
    public PropertyWrapper mYProperty;

    public AnimatedScreenElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        this.mToProperties = new CopyOnWriteArraySet<>();
        this.mTintMode = PorterDuff.Mode.SRC_IN;
        this.mTintChanged = true;
        this.mMatrix = new Matrix();
        this.mPaint = new Paint();
        this.mVirtualViewId = Integer.MIN_VALUE;
        this.mListener = new MamlTransitionListener(this);
        load(element);
        if (this.mHasContentDescription) {
            this.mRoot.addAccessibleElements(this);
        }
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(1.0f);
        this.mPaint.setColor(-4982518);
        this.mListenerWrapper = new TransitionListenerWrapper(this.mListener);
    }

    private Expression createExp(Variables variables, Element element, String str, String str2) {
        Expression build = Expression.build(variables, getAttr(element, str));
        return (build != null || TextUtils.isEmpty(str2)) ? build : Expression.build(variables, getAttr(element, str2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void folmeFromToImpl(String str, String str2, String str3) {
        ScreenElement findElement = getRoot().findElement(str);
        ScreenElement findElement2 = getRoot().findElement(str2);
        ScreenElement findElement3 = getRoot().findElement(str3);
        if (!(findElement instanceof StateElement) || !(findElement2 instanceof StateElement)) {
            Log.w("AnimatedScreenElement", "folmeFromTo: wrong state name " + str + " " + str2);
            return;
        }
        StateElement stateElement = (StateElement) findElement;
        StateElement stateElement2 = (StateElement) findElement2;
        ConfigElement configElement = findElement3 instanceof ConfigElement ? (ConfigElement) findElement3 : null;
        try {
            AnimConfig[] animConfig = configElement != null ? configElement.getAnimConfig(this.mListenerWrapper) : new AnimConfig[0];
            AnimState animState = stateElement.getAnimState("from");
            AnimState animState2 = stateElement2.getAnimState("to");
            setupToProperties(stateElement2);
            Folme.useAt(getAnimTarget()).state().fromTo(animState, animState2, animConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void folmeSetToImpl(String str) {
        ScreenElement findElement = getRoot().findElement(str);
        if (!(findElement instanceof StateElement)) {
            Log.w("AnimatedScreenElement", "folmeSetTo: wrong state name " + str);
            return;
        }
        try {
            Folme.useAt(getAnimTarget()).state().setTo(((StateElement) findElement).getAnimState("setTo"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void folmeToImpl(String str, String str2) {
        ScreenElement findElement = getRoot().findElement(str);
        ScreenElement findElement2 = getRoot().findElement(str2);
        if (!(findElement instanceof StateElement)) {
            Log.w("AnimatedScreenElement", "folmeTo: wrong state name " + str);
            return;
        }
        StateElement stateElement = (StateElement) findElement;
        ConfigElement configElement = findElement2 instanceof ConfigElement ? (ConfigElement) findElement2 : null;
        try {
            AnimConfig[] animConfig = configElement != null ? configElement.getAnimConfig(this.mListenerWrapper) : new AnimConfig[0];
            AnimState animState = stateElement.getAnimState("to");
            setupToProperties(stateElement);
            Folme.useAt(getAnimTarget()).state().to(animState, animConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleCancel() {
        if (this.mTouchable && this.mPressed) {
            this.mPressed = false;
            performAction("cancel");
            onActionCancel();
        }
    }

    private void load(Element element) {
        Expression expression;
        Expression expression2;
        Expression expression3;
        Expression expression4;
        Expression expression5;
        Expression expression6;
        Expression expression7;
        Expression expression8;
        Expression expression9;
        Expression expression10;
        Expression expression11;
        Expression expression12;
        Expression expression13;
        Expression expression14;
        Expression expression15;
        boolean z;
        Variables variables = getVariables();
        if (element != null) {
            this.mScaleExpression = createExp(variables, element, "scale", null);
            Expression createExp = createExp(variables, element, "x", "left");
            Expression createExp2 = createExp(variables, element, "y", "top");
            Expression createExp3 = createExp(variables, element, MiCloudSmsCmd.TYPE_WIPE, Tag.TagWebService.ContentGetImage.PARAM_IMAGE_WIDTH);
            Expression createExp4 = createExp(variables, element, "h", "height");
            Expression createExp5 = createExp(variables, element, "angle", "rotation");
            Expression createExp6 = createExp(variables, element, "centerX", "pivotX");
            Expression createExp7 = createExp(variables, element, "centerY", "pivotY");
            Expression createExp8 = createExp(variables, element, "alpha", null);
            Expression createExp9 = createExp(variables, element, "scaleX", null);
            Expression createExp10 = createExp(variables, element, "scaleY", null);
            expression6 = createExp(variables, element, "angleX", "rotationX");
            expression7 = createExp(variables, element, "angleY", "rotationY");
            expression9 = createExp10;
            expression10 = createExp(variables, element, "angleZ", "rotationZ");
            Expression createExp11 = createExp(variables, element, "centerZ", "pivotZ");
            if (this.mHasName) {
                StringBuilder sb = new StringBuilder();
                expression15 = createExp;
                sb.append(this.mName);
                sb.append(".");
                expression14 = createExp2;
                sb.append("actual_x");
                this.mActualXVar = new IndexedVariable(sb.toString(), variables, true);
                z = true;
                this.mActualYVar = new IndexedVariable(this.mName + ".actual_y", variables, true);
            } else {
                expression15 = createExp;
                expression14 = createExp2;
                z = true;
            }
            this.mTouchable = Boolean.parseBoolean(getAttr(element, "touchable"));
            this.mInterceptTouch = Boolean.parseBoolean(getAttr(element, "interceptTouch"));
            this.mIsHaptic = Boolean.parseBoolean(getAttr(element, "haptic"));
            this.mContentDescription = getAttr(element, "contentDescription");
            this.mContentDescriptionExp = Expression.build(variables, getAttr(element, "contentDescriptionExp"));
            this.mHasContentDescription = (TextUtils.isEmpty(this.mContentDescription) && this.mContentDescriptionExp == null) ? false : z;
            this.mMarginLeft = Utils.getAttrAsFloat(element, "marginLeft", 0.0f);
            this.mMarginRight = Utils.getAttrAsFloat(element, "marginRight", 0.0f);
            this.mMarginTop = Utils.getAttrAsFloat(element, "marginTop", 0.0f);
            this.mMarginBottom = Utils.getAttrAsFloat(element, "marginBottom", 0.0f);
            String attr = getAttr(element, "tint");
            if (!TextUtils.isEmpty(attr)) {
                this.mTintColorParser = new ColorParser(variables, attr);
            }
            this.mTintModeExp = Expression.build(variables, getAttr(element, "tintmode"));
            ColorParser colorParser = this.mTintColorParser;
            this.mTintColor = colorParser != null ? colorParser.getColor() : 0;
            this.mFolmeMode = Boolean.parseBoolean(getAttr(element, "folmeMode"));
            expression12 = createExp7;
            expression8 = createExp9;
            expression = expression15;
            expression3 = createExp4;
            expression5 = createExp8;
            expression13 = createExp11;
            expression4 = createExp5;
            expression11 = createExp6;
            expression2 = createExp3;
        } else {
            expression = null;
            expression2 = null;
            expression3 = null;
            expression4 = null;
            expression5 = null;
            expression6 = null;
            expression7 = null;
            expression8 = null;
            expression9 = null;
            expression10 = null;
            expression11 = null;
            expression12 = null;
            expression13 = null;
            expression14 = null;
        }
        this.mXProperty = new PropertyWrapper(this.mName + ".x", variables, expression, isInFolmeMode(), 0.0d);
        this.mYProperty = new PropertyWrapper(this.mName + ".y", variables, expression14, isInFolmeMode(), 0.0d);
        this.mWidthProperty = new PropertyWrapper(this.mName + ".w", variables, expression2, isInFolmeMode(), -1.0d);
        this.mHeightProperty = new PropertyWrapper(this.mName + ".h", variables, expression3, isInFolmeMode(), -1.0d);
        this.mRotationProperty = new PropertyWrapper(this.mName + ".rotation", variables, expression4, isInFolmeMode(), 0.0d);
        this.mAlphaProperty = new PropertyWrapper(this.mName + ".alpha", variables, expression5, isInFolmeMode(), 255.0d);
        this.mRotationXProperty = new PropertyWrapper(this.mName + ".rotationX", variables, expression6, isInFolmeMode(), 0.0d);
        this.mRotationYProperty = new PropertyWrapper(this.mName + ".rotationY", variables, expression7, isInFolmeMode(), 0.0d);
        this.mRotationZProperty = new PropertyWrapper(this.mName + ".rotationZ", variables, expression10, isInFolmeMode(), 0.0d);
        this.mScaleXProperty = new PropertyWrapper(this.mName + ".scaleX", variables, expression8, isInFolmeMode(), 1.0d);
        this.mScaleYProperty = new PropertyWrapper(this.mName + ".scaleY", variables, expression9, isInFolmeMode(), 1.0d);
        this.mTintColorProperty = new PropertyWrapper(this.mName + ".tintColor", variables, null, isInFolmeMode(), this.mTintColor);
        this.mPivotXProperty = new PropertyWrapper(this.mName + ".pivotX", variables, expression11, isInFolmeMode(), 0.0d);
        this.mPivotYProperty = new PropertyWrapper(this.mName + ".pivotY", variables, expression12, isInFolmeMode(), 0.0d);
        this.mPivotZProperty = new PropertyWrapper(this.mName + ".pivotZ", variables, expression13, isInFolmeMode(), 0.0d);
    }

    private void setupToProperties(StateElement stateElement) {
        Iterator<String> it = stateElement.getProperties().iterator();
        while (it.hasNext()) {
            FloatProperty propertyByName = AnimatedProperty.getPropertyByName(it.next());
            if (propertyByName != null) {
                this.mToProperties.add(propertyByName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void doRenderWithTranslation(Canvas canvas) {
        int save = canvas.save();
        this.mMatrix.reset();
        float rotationX = getRotationX();
        float rotationY = getRotationY();
        float rotationZ = getRotationZ();
        if (rotationX != 0.0f || rotationY != 0.0f || rotationZ != 0.0f) {
            if (this.mCamera == null) {
                this.mCamera = new Camera();
            }
            this.mCamera.save();
            this.mCamera.rotate(rotationX, rotationY, rotationZ);
            float pivotZ = getPivotZ();
            if (pivotZ != 0.0f) {
                this.mCamera.translate(0.0f, 0.0f, pivotZ);
            }
            this.mCamera.getMatrix(this.mMatrix);
            this.mCamera.restore();
        }
        float rotation = getRotation();
        if (rotation != 0.0f) {
            this.mMatrix.preRotate(rotation);
        }
        float scaleX = getScaleX();
        float scaleY = getScaleY();
        if (scaleX != 1.0f || scaleY != 1.0f) {
            this.mMatrix.preScale(scaleX, scaleY);
        }
        float x = getX();
        float y = getY();
        float pivotX = getPivotX() - (x - getLeft());
        float pivotY = getPivotY() - (y - getTop());
        this.mMatrix.preTranslate(-pivotX, -pivotY);
        this.mMatrix.postTranslate(pivotX + x, pivotY + y);
        canvas.concat(this.mMatrix);
        doRender(canvas);
        if (this.mRoot.mShowDebugLayout) {
            float width = getWidth();
            float height = getHeight();
            if (width > 0.0f && height > 0.0f) {
                float left = getLeft(0.0f, width);
                float top = getTop(0.0f, height);
                canvas.drawRect(left, top, left + width, top + height, this.mPaint);
            }
        }
        canvas.restoreToCount(save);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void doTick(long j) {
        super.doTick(j);
        if (this.mHasName) {
            this.mActualXVar.set(descale(getX()));
            this.mActualYVar.set(descale(getY()));
        }
        int evaluateAlpha = evaluateAlpha();
        this.mAlpha = evaluateAlpha;
        if (evaluateAlpha < 0) {
            evaluateAlpha = 0;
        }
        this.mAlpha = evaluateAlpha;
        this.mTintChanged = false;
        int tintColor = getTintColor();
        if (tintColor != this.mTintColor) {
            this.mTintChanged = true;
            this.mTintColor = tintColor;
        }
        if (this.mTintColor != 0) {
            PorterDuff.Mode mode = this.mTintMode;
            Expression expression = this.mTintModeExp;
            if (expression != null) {
                mode = Utils.getPorterDuffMode((int) expression.evaluate(), this.mTintMode);
            }
            if (this.mTintMode != mode) {
                this.mTintMode = mode;
                this.mTintChanged = true;
            }
            if (this.mTintFilter == null) {
                this.mTintChanged = true;
            }
            if (this.mTintChanged) {
                this.mTintFilter = new PorterDuffColorFilter(this.mTintColor, mode);
            }
        } else {
            this.mTintFilter = null;
        }
        FunctionElement functionElement = this.mTickListener;
        if (functionElement != null) {
            functionElement.perform();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int evaluateAlpha() {
        int value = (int) this.mAlphaProperty.getValue();
        if (!isInFolmeMode()) {
            AlphaAnimation alphaAnimation = this.mAlphas;
            value = Utils.mixAlpha(value, alphaAnimation != null ? alphaAnimation.getAlpha() : 255);
        }
        ElementGroup elementGroup = this.mParent;
        return (elementGroup == null || (elementGroup instanceof LayerScreenElement)) ? value : ((elementGroup instanceof ElementGroup) && elementGroup.isLayered()) ? value : Utils.mixAlpha(value, this.mParent.getAlpha());
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        getContext().getHandler().removeCallbacksAndMessages(this);
        try {
            if (this.mAnimTarget != null) {
                Folme.clean(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void folmeCancel(Expression[] expressionArr) {
        try {
            if (expressionArr == null) {
                Folme.useAt(getAnimTarget()).state().cancel();
                this.mToProperties.clear();
                return;
            }
            ArraySet arraySet = new ArraySet();
            for (Expression expression : expressionArr) {
                FloatProperty propertyByName = AnimatedProperty.getPropertyByName(expression.evaluateStr());
                if (propertyByName != null) {
                    arraySet.add(propertyByName);
                    this.mToProperties.remove(propertyByName);
                }
            }
            Folme.useAt(getAnimTarget()).state().cancel((FloatProperty[]) arraySet.toArray(new FloatProperty[arraySet.size()]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void folmeFromTo(final String str, final String str2, final String str3) {
        getContext().getHandler().postAtTime(new Runnable() { // from class: com.miui.maml.elements.AnimatedScreenElement.3
            @Override // java.lang.Runnable
            public void run() {
                AnimatedScreenElement.this.folmeFromToImpl(str, str2, str3);
            }
        }, this, 0L);
    }

    public void folmeSetTo(final String str) {
        getContext().getHandler().postAtTime(new Runnable() { // from class: com.miui.maml.elements.AnimatedScreenElement.2
            @Override // java.lang.Runnable
            public void run() {
                AnimatedScreenElement.this.folmeSetToImpl(str);
            }
        }, this, 0L);
    }

    public void folmeTo(final String str, final String str2) {
        getContext().getHandler().postAtTime(new Runnable() { // from class: com.miui.maml.elements.AnimatedScreenElement.1
            @Override // java.lang.Runnable
            public void run() {
                AnimatedScreenElement.this.folmeToImpl(str, str2);
            }
        }, this, 0L);
    }

    public float getAbsoluteLeft() {
        float left = getLeft();
        ElementGroup elementGroup = this.mParent;
        return left + (elementGroup == null ? 0.0f : elementGroup.getParentLeft());
    }

    public float getAbsoluteTop() {
        float top = getTop();
        ElementGroup elementGroup = this.mParent;
        return top + (elementGroup == null ? 0.0f : elementGroup.getParentTop());
    }

    public int getAlpha() {
        return this.mAlpha;
    }

    protected AnimatedTarget getAnimTarget() {
        if (this.mAnimTarget == null) {
            this.mAnimTarget = (AnimatedTarget) Folme.getTarget(this, AnimatedTarget.sCreator);
        }
        return this.mAnimTarget;
    }

    public String getContentDescription() {
        Expression expression = this.mContentDescriptionExp;
        if (expression != null) {
            String evaluateStr = expression.evaluateStr();
            if (evaluateStr == null) {
                Log.e("AnimatedScreenElement", "element.getContentDescription() == null " + this.mName);
                return "";
            }
            return evaluateStr;
        }
        return this.mContentDescription;
    }

    public float getHeight() {
        return scale(getHeightRaw());
    }

    public float getHeightRaw() {
        SizeAnimation sizeAnimation;
        float value = (float) this.mHeightProperty.getValue();
        return (isInFolmeMode() || (sizeAnimation = this.mSizes) == null) ? value : (float) sizeAnimation.getHeight();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getLeft() {
        return getLeft(getX(), getWidth());
    }

    public final float getMarginBottom() {
        return scale(this.mMarginBottom);
    }

    public final float getMarginLeft() {
        return scale(this.mMarginLeft);
    }

    public final float getMarginRight() {
        return scale(this.mMarginRight);
    }

    public final float getMarginTop() {
        return scale(this.mMarginTop);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Matrix getMatrix() {
        return this.mMatrix;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getPivotX() {
        return scale(this.mPivotXProperty.getValue());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getPivotY() {
        return scale(this.mPivotYProperty.getValue());
    }

    protected float getPivotZ() {
        return scale(this.mPivotZProperty.getValue());
    }

    public float getRotation() {
        RotationAnimation rotationAnimation;
        float value = (float) this.mRotationProperty.getValue();
        return (isInFolmeMode() || (rotationAnimation = this.mRotations) == null) ? value : value + rotationAnimation.getAngle();
    }

    public float getRotationX() {
        return (float) this.mRotationXProperty.getValue();
    }

    public float getRotationY() {
        return (float) this.mRotationYProperty.getValue();
    }

    public float getRotationZ() {
        return (float) this.mRotationZProperty.getValue();
    }

    public float getScaleX() {
        float value = (float) this.mScaleXProperty.getValue();
        if (isInFolmeMode()) {
            return value;
        }
        Expression expression = this.mScaleExpression;
        if (expression != null) {
            value = (float) expression.evaluate();
        }
        ScaleAnimation scaleAnimation = this.mScales;
        return scaleAnimation != null ? (float) (value * scaleAnimation.getScaleX()) : value;
    }

    public float getScaleY() {
        float value = (float) this.mScaleYProperty.getValue();
        if (isInFolmeMode()) {
            return value;
        }
        Expression expression = this.mScaleExpression;
        if (expression != null) {
            value = (float) expression.evaluate();
        }
        ScaleAnimation scaleAnimation = this.mScales;
        return scaleAnimation != null ? (float) (value * scaleAnimation.getScaleY()) : value;
    }

    protected int getTintColor() {
        ColorParser colorParser;
        int value = (int) this.mTintColorProperty.getValue();
        return (isInFolmeMode() || (colorParser = this.mTintColorParser) == null) ? value : colorParser.getColor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getTop() {
        return getTop(getY(), getHeight());
    }

    public float getWidth() {
        return scale(getWidthRaw());
    }

    public float getWidthRaw() {
        SizeAnimation sizeAnimation;
        float value = (float) this.mWidthProperty.getValue();
        return (isInFolmeMode() || (sizeAnimation = this.mSizes) == null) ? value : (float) sizeAnimation.getWidth();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getX() {
        PositionAnimation positionAnimation;
        float value = (float) this.mXProperty.getValue();
        if (!isInFolmeMode() && (positionAnimation = this.mPositions) != null) {
            value = (float) (value + positionAnimation.getX());
        }
        return scale(value);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public float getY() {
        PositionAnimation positionAnimation;
        float value = (float) this.mYProperty.getValue();
        if (!isInFolmeMode() && (positionAnimation = this.mPositions) != null) {
            value = (float) (value + positionAnimation.getY());
        }
        return scale(value);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        if (isInFolmeMode()) {
            initProperties();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void initProperties() {
        this.mXProperty.init();
        this.mYProperty.init();
        this.mWidthProperty.init();
        this.mHeightProperty.init();
        this.mRotationProperty.init();
        this.mAlphaProperty.init();
        this.mRotationXProperty.init();
        this.mRotationYProperty.init();
        this.mRotationZProperty.init();
        this.mScaleXProperty.init();
        this.mScaleYProperty.init();
        this.mTintColorProperty.init();
        this.mPivotXProperty.init();
        this.mPivotYProperty.init();
        this.mPivotZProperty.init();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isInFolmeMode() {
        return this.mFolmeMode && this.mHasName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionCancel() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionDown(float f, float f2) {
        this.mRoot.onUIInteractive(this, "down");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionMove(float f, float f2) {
        this.mRoot.onUIInteractive(this, "move");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActionUp() {
        this.mRoot.onUIInteractive(this, "up");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public BaseAnimation onCreateAnimation(String str, Element element) {
        if ("AlphaAnimation".equals(str)) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(element, this);
            this.mAlphas = alphaAnimation;
            return alphaAnimation;
        } else if ("PositionAnimation".equals(str)) {
            PositionAnimation positionAnimation = new PositionAnimation(element, this);
            this.mPositions = positionAnimation;
            return positionAnimation;
        } else if ("RotationAnimation".equals(str)) {
            RotationAnimation rotationAnimation = new RotationAnimation(element, this);
            this.mRotations = rotationAnimation;
            return rotationAnimation;
        } else if ("SizeAnimation".equals(str)) {
            SizeAnimation sizeAnimation = new SizeAnimation(element, this);
            this.mSizes = sizeAnimation;
            return sizeAnimation;
        } else if ("ScaleAnimation".equals(str)) {
            ScaleAnimation scaleAnimation = new ScaleAnimation(element, this);
            this.mScales = scaleAnimation;
            return scaleAnimation;
        } else {
            return super.onCreateAnimation(str, element);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x004c  */
    @Override // com.miui.maml.elements.ScreenElement
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean onHover(android.view.MotionEvent r8) {
        /*
            r7 = this;
            boolean r0 = r7.isVisible()
            r1 = 0
            if (r0 == 0) goto L56
            boolean r0 = r7.mHasContentDescription
            if (r0 != 0) goto Lc
            goto L56
        Lc:
            java.lang.String r0 = r7.getContentDescription()
            float r2 = r8.getX()
            float r3 = r8.getY()
            boolean r4 = super.onHover(r8)
            int r8 = r8.getActionMasked()
            r5 = 7
            r6 = 1
            if (r8 == r5) goto L36
            r5 = 9
            if (r8 == r5) goto L29
            goto L4a
        L29:
            boolean r8 = r7.touched(r2, r3)
            if (r8 == 0) goto L4a
            com.miui.maml.ScreenElementRoot r8 = r7.mRoot
            r8.onHoverChange(r7, r0)
        L34:
            r4 = r6
            goto L4a
        L36:
            boolean r8 = r7.touched(r2, r3)
            if (r8 == 0) goto L4a
            com.miui.maml.ScreenElementRoot r8 = r7.mRoot
            com.miui.maml.elements.AnimatedScreenElement r8 = r8.getHoverElement()
            if (r8 == r7) goto L34
            com.miui.maml.ScreenElementRoot r8 = r7.mRoot
            r8.onHoverChange(r7, r0)
            goto L34
        L4a:
            if (r4 == 0) goto L4f
            r7.requestUpdate()
        L4f:
            if (r4 == 0) goto L56
            boolean r7 = r7.mInterceptTouch
            if (r7 == 0) goto L56
            r1 = r6
        L56:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.AnimatedScreenElement.onHover(android.view.MotionEvent):boolean");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void onSetAnimBefore() {
        this.mAlphas = null;
        this.mPositions = null;
        this.mRotations = null;
        this.mSizes = null;
        this.mScales = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void onSetAnimEnable(BaseAnimation baseAnimation) {
        if (baseAnimation instanceof AlphaAnimation) {
            this.mAlphas = (AlphaAnimation) baseAnimation;
        } else if (baseAnimation instanceof PositionAnimation) {
            this.mPositions = (PositionAnimation) baseAnimation;
        } else if (baseAnimation instanceof RotationAnimation) {
            this.mRotations = (RotationAnimation) baseAnimation;
        } else if (baseAnimation instanceof SizeAnimation) {
            this.mSizes = (SizeAnimation) baseAnimation;
        } else if (baseAnimation instanceof ScaleAnimation) {
            this.mScales = (ScaleAnimation) baseAnimation;
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public boolean onTouch(MotionEvent motionEvent) {
        if (isVisible() && this.mTouchable) {
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            boolean onTouch = super.onTouch(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 0) {
                if (actionMasked != 1) {
                    if (actionMasked != 2) {
                        if (actionMasked == 3) {
                            handleCancel();
                        }
                    } else if (this.mPressed) {
                        onTouch = touched(x, y);
                        performAction("move");
                        onActionMove(x, y);
                    }
                } else if (this.mPressed) {
                    this.mPressed = false;
                    if (touched(x, y)) {
                        if (this.mIsHaptic) {
                            this.mRoot.haptic(1);
                        }
                        performAction("up");
                        onActionUp();
                    } else {
                        performAction("cancel");
                        onActionCancel();
                    }
                    onTouch = true;
                }
            } else if (touched(x, y)) {
                this.mPressed = true;
                if (this.mIsHaptic) {
                    this.mRoot.haptic(1);
                }
                performAction("down");
                onActionDown(x, y);
                onTouch = true;
            }
            if (onTouch) {
                requestUpdate();
            }
            return onTouch && this.mInterceptTouch;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.ScreenElement
    public void onVisibilityChange(boolean z) {
        super.onVisibilityChange(z);
        if (z) {
            return;
        }
        handleCancel();
        if (this.mVirtualViewId == Integer.MIN_VALUE || getRoot().getMamlAccessHelper() == null || getRoot().getMamlAccessHelper().getFocusedVirtualView() != this.mVirtualViewId) {
            return;
        }
        getRoot().getMamlAccessHelper().performAccessibilityAction(this.mVirtualViewId, 128);
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        handleCancel();
        try {
            if (this.mAnimTarget == null || this.mToProperties.size() <= 0) {
                return;
            }
            Folme.useAt(this.mAnimTarget).state().end(this.mToProperties.toArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void render(Canvas canvas) {
        updateVisibility();
        if (isVisible()) {
            doRenderWithTranslation(canvas);
        }
    }

    @Override // com.miui.maml.elements.ScreenElement
    public void setColorFilter(ColorFilter colorFilter) {
        Paint paint = this.mPaint;
        if (paint != null) {
            paint.setColorFilter(colorFilter);
        }
    }

    public void setHeight(double d) {
        this.mHeightProperty.setValue(descale(d));
    }

    public void setOnTickListener(FunctionElement functionElement) {
        this.mTickListener = functionElement;
    }

    public void setVirtualViewId(int i) {
        this.mVirtualViewId = i;
    }

    public void setWidth(double d) {
        this.mWidthProperty.setValue(descale(d));
    }

    public void setX(double d) {
        this.mXProperty.setValue(descale(d));
    }

    public void setY(double d) {
        this.mYProperty.setValue(descale(d));
    }

    public boolean touched(float f, float f2) {
        return touched(f, f2, true);
    }

    public boolean touched(float f, float f2, boolean z) {
        if (z) {
            ElementGroup elementGroup = this.mParent;
            float parentLeft = elementGroup == null ? 0.0f : elementGroup.getParentLeft();
            ElementGroup elementGroup2 = this.mParent;
            f -= parentLeft;
            f2 -= elementGroup2 != null ? elementGroup2.getParentTop() : 0.0f;
        }
        float left = getLeft();
        float top = getTop();
        return f >= left && f <= left + getWidth() && f2 >= top && f2 <= top + getHeight();
    }

    public void unsetOnTickListener() {
        this.mTickListener = null;
    }
}
