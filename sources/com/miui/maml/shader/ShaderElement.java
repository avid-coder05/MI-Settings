package com.miui.maml.shader;

import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.Log;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import com.miui.maml.util.ColorParser;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/* loaded from: classes2.dex */
public abstract class ShaderElement {
    protected ScreenElementRoot mRoot;
    protected Shader mShader;
    protected Shader.TileMode mTileMode;
    protected float mX;
    protected Expression mXExp;
    protected float mY;
    protected Expression mYExp;
    protected Matrix mShaderMatrix = new Matrix();
    protected GradientStops mGradientStops = new GradientStops();

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public final class GradientStop {
        private ColorParser mColorParser;
        private Expression mPositionExp;

        public GradientStop(Element element, ScreenElementRoot screenElementRoot) {
            this.mColorParser = ColorParser.fromElement(ShaderElement.this.mRoot.getVariables(), element);
            Expression build = Expression.build(ShaderElement.this.mRoot.getVariables(), element.getAttribute("position"));
            this.mPositionExp = build;
            if (build == null) {
                Log.e("GradientStop", "lost position attribute.");
            }
        }

        public int getColor() {
            return this.mColorParser.getColor();
        }

        public float getPosition() {
            return (float) this.mPositionExp.evaluate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes2.dex */
    public final class GradientStops {
        private int[] mColors;
        protected ArrayList<GradientStop> mGradientStopArr = new ArrayList<>();
        private float[] mPositions;

        protected GradientStops() {
        }

        public void add(GradientStop gradientStop) {
            this.mGradientStopArr.add(gradientStop);
        }

        public int[] getColors() {
            return this.mColors;
        }

        public float[] getPositions() {
            return this.mPositions;
        }

        public void init() {
            this.mColors = new int[size()];
            this.mPositions = new float[size()];
        }

        public int size() {
            return this.mGradientStopArr.size();
        }

        public void update() {
            boolean z = false;
            for (int i = 0; i < size(); i++) {
                int color = this.mGradientStopArr.get(i).getColor();
                int[] iArr = this.mColors;
                if (color != iArr[i]) {
                    z = true;
                }
                iArr[i] = color;
                float position = this.mGradientStopArr.get(i).getPosition();
                float[] fArr = this.mPositions;
                if (position != fArr[i]) {
                    z = true;
                }
                fArr[i] = position;
            }
            if (z) {
                ShaderElement.this.onGradientStopsChanged();
            }
        }
    }

    public ShaderElement(Element element, ScreenElementRoot screenElementRoot) {
        this.mRoot = screenElementRoot;
        Variables variables = getVariables();
        this.mXExp = Expression.build(variables, element.getAttribute("x"));
        this.mYExp = Expression.build(variables, element.getAttribute("y"));
        this.mTileMode = getTileMode(element.getAttribute("tile"));
        if (element.getTagName().equalsIgnoreCase("BitmapShader")) {
            return;
        }
        loadGradientStops(element, screenElementRoot);
    }

    public static Shader.TileMode getTileMode(String str) {
        return TextUtils.isEmpty(str) ? Shader.TileMode.CLAMP : str.equalsIgnoreCase("mirror") ? Shader.TileMode.MIRROR : str.equalsIgnoreCase("repeat") ? Shader.TileMode.REPEAT : Shader.TileMode.CLAMP;
    }

    private void loadGradientStops(Element element, ScreenElementRoot screenElementRoot) {
        NodeList elementsByTagName = element.getElementsByTagName("GradientStop");
        for (int i = 0; i < elementsByTagName.getLength(); i++) {
            this.mGradientStops.add(new GradientStop((Element) elementsByTagName.item(i), screenElementRoot));
        }
        if (this.mGradientStops.size() <= 0) {
            Log.e("ShaderElement", "lost gradient stop.");
        } else {
            this.mGradientStops.init();
        }
    }

    public Shader getShader() {
        return this.mShader;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Variables getVariables() {
        return this.mRoot.getVariables();
    }

    public float getX() {
        Expression expression = this.mXExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * this.mRoot.getScale());
    }

    public float getY() {
        Expression expression = this.mYExp;
        return (float) ((expression != null ? expression.evaluate() : 0.0d) * this.mRoot.getScale());
    }

    public abstract void onGradientStopsChanged();

    public void updateShader() {
        this.mGradientStops.update();
        if (updateShaderMatrix()) {
            this.mShader.setLocalMatrix(this.mShaderMatrix);
        }
    }

    public abstract boolean updateShaderMatrix();
}
