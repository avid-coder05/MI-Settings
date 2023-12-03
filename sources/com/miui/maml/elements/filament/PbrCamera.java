package com.miui.maml.elements.filament;

import android.text.TextUtils;
import com.google.android.filament.Camera;
import com.google.android.filament.Engine;
import com.miui.maml.data.Expression;
import com.miui.maml.data.Variables;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PbrCamera {
    private Camera mCamera;
    private Expression[] mExposure;
    private double[] mExposureArray;
    private Expression[] mFovProjection;
    private double[] mFovProjectionArray;
    private Expression[] mFrustumProjection;
    private double[] mFrustumProjectionArray;
    private Expression[] mLookAt;
    private double[] mLookAtArray;
    private Camera.Projection mProjectionType = Camera.Projection.ORTHO;
    private Camera.Fov mFov = Camera.Fov.VERTICAL;

    public PbrCamera(Element element, Variables variables) {
        this.mLookAt = Expression.buildMultiple(variables, element.getAttribute("cameraLookAt"));
        this.mExposure = Expression.buildMultiple(variables, element.getAttribute("cameraExposure"));
        this.mFovProjection = Expression.buildMultiple(variables, element.getAttribute("cameraFovProjection"));
        this.mFrustumProjection = Expression.buildMultiple(variables, element.getAttribute("cameraFrustumProjection"));
    }

    private void evaluateExpressions(Expression[] expressionArr, double[] dArr) {
        if (expressionArr == null || expressionArr.length < dArr.length) {
            return;
        }
        int length = dArr.length;
        for (int i = 0; i < length; i++) {
            if (expressionArr[i] != null) {
                dArr[i] = expressionArr[i].evaluate();
            } else {
                dArr[i] = 0.0d;
            }
        }
    }

    private Camera.Fov getFov(String str) {
        if (!TextUtils.isEmpty(str)) {
            str.hashCode();
            if (str.equals("vertical")) {
                return Camera.Fov.VERTICAL;
            }
            if (str.equals("horizontal")) {
                return Camera.Fov.HORIZONTAL;
            }
        }
        return Camera.Fov.VERTICAL;
    }

    private Camera.Projection getProjectionType(String str) {
        if (!TextUtils.isEmpty(str)) {
            str.hashCode();
            if (str.equals("ortho")) {
                return Camera.Projection.ORTHO;
            }
            if (str.equals("perspective")) {
                return Camera.Projection.PERSPECTIVE;
            }
        }
        return Camera.Projection.ORTHO;
    }

    public Camera createCamera(Engine engine) {
        this.mCamera = engine.createCamera();
        Expression[] expressionArr = this.mLookAt;
        if (expressionArr != null) {
            double[] dArr = new double[9];
            this.mLookAtArray = dArr;
            evaluateExpressions(expressionArr, dArr);
            Camera camera = this.mCamera;
            double[] dArr2 = this.mLookAtArray;
            camera.lookAt(dArr2[0], dArr2[1], dArr2[2], dArr2[3], dArr2[4], dArr2[5], dArr2[6], dArr2[7], dArr2[8]);
        }
        Expression[] expressionArr2 = this.mExposure;
        if (expressionArr2 != null) {
            double[] dArr3 = new double[3];
            this.mExposureArray = dArr3;
            evaluateExpressions(expressionArr2, dArr3);
            Camera camera2 = this.mCamera;
            double[] dArr4 = this.mExposureArray;
            camera2.setExposure((float) dArr4[0], (float) dArr4[1], (float) dArr4[2]);
        }
        Expression[] expressionArr3 = this.mFovProjection;
        if (expressionArr3 != null) {
            double[] dArr5 = new double[4];
            this.mFovProjectionArray = dArr5;
            evaluateExpressions(expressionArr3, dArr5);
            Expression[] expressionArr4 = this.mFovProjection;
            if (expressionArr4.length > 4) {
                this.mFov = getFov(expressionArr4[4].evaluateStr());
            }
            Camera camera3 = this.mCamera;
            double[] dArr6 = this.mFovProjectionArray;
            camera3.setProjection(dArr6[0], dArr6[1], dArr6[2], dArr6[3], this.mFov);
        }
        Expression[] expressionArr5 = this.mFrustumProjection;
        if (expressionArr5 != null) {
            double[] dArr7 = new double[6];
            this.mFrustumProjectionArray = dArr7;
            evaluateExpressions(expressionArr5, dArr7);
            Expression[] expressionArr6 = this.mFrustumProjection;
            if (expressionArr6.length > 6) {
                this.mProjectionType = getProjectionType(expressionArr6[6].evaluateStr());
            }
            Camera camera4 = this.mCamera;
            Camera.Projection projection = this.mProjectionType;
            double[] dArr8 = this.mFrustumProjectionArray;
            camera4.setProjection(projection, dArr8[0], dArr8[1], dArr8[2], dArr8[3], dArr8[4], dArr8[5]);
        }
        return this.mCamera;
    }

    public void onDestroy(Engine engine) {
        Camera camera = this.mCamera;
        if (camera != null) {
            engine.destroyCamera(camera);
        }
    }
}
