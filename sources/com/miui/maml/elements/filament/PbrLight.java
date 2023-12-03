package com.miui.maml.elements.filament;

import com.google.android.filament.Engine;
import com.google.android.filament.EntityManager;
import com.google.android.filament.LightManager;
import com.miui.maml.data.Expression;

/* loaded from: classes2.dex */
public class PbrLight {
    private boolean mCastShadows;
    private Expression[] mDirectionXYZ;
    private Expression mFalloffExp;
    private Expression mIntensityExp;
    private Expression[] mLinearRGB;
    private Expression[] mPositionXYZ;
    private LightManager.Type mType;
    private float[] mLinearRGBArray = {1.0f, 1.0f, 1.0f};
    private float[] mDirectionXYZArray = {0.0f, -1.0f, 0.0f};
    private float[] mPositionXYZArray = {0.0f, 0.0f, 0.0f};
    private float mIntensity = 100000.0f;
    private float mFalloff = 1.0f;
    private int mLight = 0;

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:10:0x0097, code lost:
    
        if (r4.equals("point") == false) goto L4;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public PbrLight(org.w3c.dom.Element r4, com.miui.maml.data.Variables r5) {
        /*
            r3 = this;
            r3.<init>()
            r0 = 3
            float[] r1 = new float[r0]
            r1 = {x0102: FILL_ARRAY_DATA , data: [1065353216, 1065353216, 1065353216} // fill-array
            r3.mLinearRGBArray = r1
            float[] r1 = new float[r0]
            r1 = {x010c: FILL_ARRAY_DATA , data: [0, -1082130432, 0} // fill-array
            r3.mDirectionXYZArray = r1
            float[] r1 = new float[r0]
            r1 = {x0116: FILL_ARRAY_DATA , data: [0, 0, 0} // fill-array
            r3.mPositionXYZArray = r1
            r1 = 1203982336(0x47c35000, float:100000.0)
            r3.mIntensity = r1
            r1 = 1065353216(0x3f800000, float:1.0)
            r3.mFalloff = r1
            r1 = 0
            r3.mCastShadows = r1
            r3.mLight = r1
            java.lang.String r2 = "lightLinearRGB"
            java.lang.String r2 = r4.getAttribute(r2)
            com.miui.maml.data.Expression[] r2 = com.miui.maml.data.Expression.buildMultiple(r5, r2)
            r3.mLinearRGB = r2
            java.lang.String r2 = "lightDirection"
            java.lang.String r2 = r4.getAttribute(r2)
            com.miui.maml.data.Expression[] r2 = com.miui.maml.data.Expression.buildMultiple(r5, r2)
            r3.mDirectionXYZ = r2
            java.lang.String r2 = "lightPosition"
            java.lang.String r2 = r4.getAttribute(r2)
            com.miui.maml.data.Expression[] r2 = com.miui.maml.data.Expression.buildMultiple(r5, r2)
            r3.mPositionXYZ = r2
            java.lang.String r2 = "lightIntensity"
            java.lang.String r2 = r4.getAttribute(r2)
            com.miui.maml.data.Expression r2 = com.miui.maml.data.Expression.build(r5, r2)
            r3.mIntensityExp = r2
            java.lang.String r2 = "linghtFallOff"
            java.lang.String r2 = r4.getAttribute(r2)
            com.miui.maml.data.Expression r5 = com.miui.maml.data.Expression.build(r5, r2)
            r3.mFalloffExp = r5
            java.lang.String r5 = "lightCastShadows"
            java.lang.String r5 = r4.getAttribute(r5)
            java.lang.String r2 = "true"
            boolean r5 = r2.equals(r5)
            r3.mCastShadows = r5
            java.lang.String r5 = "lightType"
            java.lang.String r4 = r4.getAttribute(r5)
            r4.hashCode()
            int r5 = r4.hashCode()
            r2 = -1
            switch(r5) {
                case -1631834134: goto Lb2;
                case 114252: goto La6;
                case 3537154: goto L9a;
                case 106845584: goto L90;
                case 347125962: goto L85;
                default: goto L83;
            }
        L83:
            r0 = r2
            goto Lbc
        L85:
            java.lang.String r5 = "focused_spot"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto L8e
            goto L83
        L8e:
            r0 = 4
            goto Lbc
        L90:
            java.lang.String r5 = "point"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto Lbc
            goto L83
        L9a:
            java.lang.String r5 = "spot"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto La4
            goto L83
        La4:
            r0 = 2
            goto Lbc
        La6:
            java.lang.String r5 = "sun"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto Lb0
            goto L83
        Lb0:
            r0 = 1
            goto Lbc
        Lb2:
            java.lang.String r5 = "directional"
            boolean r4 = r4.equals(r5)
            if (r4 != 0) goto Lbb
            goto L83
        Lbb:
            r0 = r1
        Lbc:
            switch(r0) {
                case 0: goto Ld8;
                case 1: goto Ld3;
                case 2: goto Lce;
                case 3: goto Lc9;
                case 4: goto Lc4;
                default: goto Lbf;
            }
        Lbf:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.DIRECTIONAL
            r3.mType = r4
            goto Ldc
        Lc4:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.FOCUSED_SPOT
            r3.mType = r4
            goto Ldc
        Lc9:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.POINT
            r3.mType = r4
            goto Ldc
        Lce:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.SPOT
            r3.mType = r4
            goto Ldc
        Ld3:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.SUN
            r3.mType = r4
            goto Ldc
        Ld8:
            com.google.android.filament.LightManager$Type r4 = com.google.android.filament.LightManager.Type.DIRECTIONAL
            r3.mType = r4
        Ldc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.filament.PbrLight.<init>(org.w3c.dom.Element, com.miui.maml.data.Variables):void");
    }

    private void evaluateExpressions(Expression[] expressionArr, float[] fArr) {
        if (expressionArr == null || fArr == null || expressionArr.length != fArr.length) {
            return;
        }
        int length = fArr.length;
        for (int i = 0; i < length; i++) {
            if (expressionArr[i] != null) {
                fArr[i] = (float) expressionArr[i].evaluate();
            }
        }
    }

    public int createLight(Engine engine) {
        evaluateExpressions(this.mLinearRGB, this.mLinearRGBArray);
        evaluateExpressions(this.mDirectionXYZ, this.mDirectionXYZArray);
        evaluateExpressions(this.mPositionXYZ, this.mPositionXYZArray);
        Expression expression = this.mIntensityExp;
        if (expression != null) {
            this.mIntensity = (float) expression.evaluate();
        }
        Expression expression2 = this.mFalloffExp;
        if (expression2 != null) {
            this.mFalloff = (float) expression2.evaluate();
        }
        this.mLight = EntityManager.get().create();
        LightManager.Builder builder = new LightManager.Builder(this.mType);
        float[] fArr = this.mLinearRGBArray;
        LightManager.Builder intensity = builder.color(fArr[0], fArr[1], fArr[2]).intensity(this.mIntensity);
        float[] fArr2 = this.mDirectionXYZArray;
        LightManager.Builder direction = intensity.direction(fArr2[0], fArr2[1], fArr2[2]);
        float[] fArr3 = this.mPositionXYZArray;
        direction.position(fArr3[0], fArr3[1], fArr3[2]).falloff(this.mFalloff).castShadows(this.mCastShadows).build(engine, this.mLight);
        return this.mLight;
    }

    public void onDestroy(Engine engine) {
        int i = this.mLight;
        if (i != 0) {
            engine.destroyEntity(i);
            EntityManager.get().destroy(this.mLight);
            this.mLight = 0;
        }
    }
}
