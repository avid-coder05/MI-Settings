package com.miui.maml.elements.video;

import android.content.Context;
import android.view.SurfaceHolder;

/* loaded from: classes2.dex */
class NormalVideoView extends BaseVideoView {
    public NormalVideoView(Context context) {
        super(context);
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void addSurfaceHolderCallback() {
        getHolder().addCallback(this.mSHCallback);
    }

    /* JADX WARN: Code restructure failed: missing block: B:28:0x0062, code lost:
    
        if (r1 > r6) goto L29;
     */
    @Override // android.view.SurfaceView, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r6, int r7) {
        /*
            r5 = this;
            int r0 = r5.mScaleMode
            r1 = 3
            if (r0 != r1) goto L83
            int r0 = r5.mVideoWidth
            int r0 = android.view.SurfaceView.getDefaultSize(r0, r6)
            int r1 = r5.mVideoHeight
            int r1 = android.view.SurfaceView.getDefaultSize(r1, r7)
            int r2 = r5.mVideoWidth
            if (r2 <= 0) goto L7f
            int r2 = r5.mVideoHeight
            if (r2 <= 0) goto L7f
            int r0 = android.view.View.MeasureSpec.getMode(r6)
            int r6 = android.view.View.MeasureSpec.getSize(r6)
            int r1 = android.view.View.MeasureSpec.getMode(r7)
            int r7 = android.view.View.MeasureSpec.getSize(r7)
            r2 = 1073741824(0x40000000, float:2.0)
            if (r0 != r2) goto L46
            if (r1 != r2) goto L46
            int r0 = r5.mVideoWidth
            int r1 = r0 * r7
            int r2 = r5.mVideoHeight
            int r3 = r6 * r2
            if (r1 >= r3) goto L3c
            int r0 = r0 * r7
            int r0 = r0 / r2
            goto L67
        L3c:
            int r1 = r0 * r7
            int r3 = r6 * r2
            if (r1 <= r3) goto L64
            int r2 = r2 * r6
            int r1 = r2 / r0
            goto L56
        L46:
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r0 != r2) goto L58
            int r0 = r5.mVideoHeight
            int r0 = r0 * r6
            int r2 = r5.mVideoWidth
            int r0 = r0 / r2
            if (r1 != r3) goto L55
            if (r0 <= r7) goto L55
            goto L64
        L55:
            r1 = r0
        L56:
            r0 = r6
            goto L7f
        L58:
            if (r1 != r2) goto L69
            int r1 = r5.mVideoWidth
            int r1 = r1 * r7
            int r2 = r5.mVideoHeight
            int r1 = r1 / r2
            if (r0 != r3) goto L66
            if (r1 <= r6) goto L66
        L64:
            r0 = r6
            goto L67
        L66:
            r0 = r1
        L67:
            r1 = r7
            goto L7f
        L69:
            int r2 = r5.mVideoWidth
            int r4 = r5.mVideoHeight
            if (r1 != r3) goto L75
            if (r4 <= r7) goto L75
            int r1 = r7 * r2
            int r1 = r1 / r4
            goto L77
        L75:
            r1 = r2
            r7 = r4
        L77:
            if (r0 != r3) goto L66
            if (r1 <= r6) goto L66
            int r4 = r4 * r6
            int r1 = r4 / r2
            goto L56
        L7f:
            r5.setMeasuredDimension(r0, r1)
            goto L86
        L83:
            super.onMeasure(r6, r7)
        L86:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.elements.video.NormalVideoView.onMeasure(int, int):void");
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void onSurfaceCreated(SurfaceHolder surfaceHolder) {
        this.mSurface = surfaceHolder.getSurface();
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void onSurfaceDestroyed() {
        this.mSurface = null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.miui.maml.elements.video.BaseVideoView
    public void setFormat(int i) {
        getHolder().setFormat(i);
    }

    @Override // com.miui.maml.elements.video.BaseVideoView
    protected void updateVideoSize() {
        requestLayout();
    }
}
