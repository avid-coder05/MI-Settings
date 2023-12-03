package com.miui.maml;

import android.content.res.Configuration;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.miui.maml.RendererController;
import com.miui.maml.data.Variables;
import com.miui.maml.util.MamlAccessHelper;

/* loaded from: classes2.dex */
public class MiAdvancedView extends View implements RendererController.IRenderable {
    private boolean mLoggedHardwareRender;
    private MamlAccessHelper mMamlAccessHelper;
    protected boolean mNeedDisallowInterceptTouchEvent;
    private boolean mPaused;
    private int mPivotX;
    private int mPivotY;
    protected ScreenElementRoot mRoot;
    private float mScale;

    @Override // android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        MamlAccessHelper mamlAccessHelper = this.mMamlAccessHelper;
        if (mamlAccessHelper == null || !mamlAccessHelper.dispatchHoverEvent(motionEvent)) {
            return super.dispatchHoverEvent(motionEvent);
        }
        return true;
    }

    @Override // com.miui.maml.RendererController.IRenderable
    public void doRender() {
        postInvalidate();
    }

    public final ScreenElementRoot getRoot() {
        return this.mRoot;
    }

    @Override // android.view.View
    protected int getSuggestedMinimumHeight() {
        return (int) this.mRoot.getHeight();
    }

    @Override // android.view.View
    protected int getSuggestedMinimumWidth() {
        return (int) this.mRoot.getWidth();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mRoot.onConfigurationChanged(configuration);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (!this.mLoggedHardwareRender) {
            Log.d("MiAdvancedView", "canvas hardware render: " + canvas.isHardwareAccelerated());
            this.mLoggedHardwareRender = true;
        }
        if (this.mScale == 0.0f) {
            this.mRoot.render(canvas);
            return;
        }
        int save = canvas.save();
        float f = this.mScale;
        canvas.scale(f, f, this.mPivotX, this.mPivotY);
        this.mRoot.render(canvas);
        canvas.restoreToCount(save);
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

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        Variables variables = this.mRoot.getContext().mVariables;
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

    public void onPause() {
        this.mPaused = true;
        this.mRoot.selfPause();
    }

    public void onResume() {
        this.mPaused = false;
        this.mRoot.selfResume();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ScreenElementRoot screenElementRoot = this.mRoot;
        if (screenElementRoot != null) {
            boolean needDisallowInterceptTouchEvent = screenElementRoot.needDisallowInterceptTouchEvent();
            if (this.mNeedDisallowInterceptTouchEvent != needDisallowInterceptTouchEvent) {
                getParent().requestDisallowInterceptTouchEvent(needDisallowInterceptTouchEvent);
                this.mNeedDisallowInterceptTouchEvent = needDisallowInterceptTouchEvent;
            }
            this.mRoot.onTouch(motionEvent);
        }
        return super.onTouchEvent(motionEvent);
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
}
