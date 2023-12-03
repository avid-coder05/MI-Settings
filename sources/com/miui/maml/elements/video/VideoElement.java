package com.miui.maml.elements.video;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.elements.ViewHolderScreenElement;
import com.miui.maml.util.MamlMediaDataSource;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class VideoElement extends ViewHolderScreenElement {
    private MamlMediaDataSource mSource;
    private BaseVideoView mView;

    public VideoElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load();
    }

    private void load() {
        int i = Build.VERSION.SDK_INT;
        if (i < 23) {
            return;
        }
        if (getRoot().getMamlSurface() == null) {
            this.mView = new NormalVideoView(this.mRoot.getContext().mContext);
        } else if (i < 26) {
            return;
        } else {
            SurfaceVideoView surfaceVideoView = new SurfaceVideoView(this.mRoot.getContext().mContext, getRoot().getMamlSurface());
            this.mView = surfaceVideoView;
            if (this.mLayer == 1) {
                surfaceVideoView.setZOrderOnTop(true);
            }
        }
        this.mView.setName(this.mName);
        this.mView.setFormat(-2);
        this.mView.setBackgroundColor(0);
    }

    public void config(boolean z, int i, String str) {
        Log.d("VideoElement", "config: path " + str + " looping " + z + " scaleMode " + i);
        if (Build.VERSION.SDK_INT < 23 || this.mView == null) {
            return;
        }
        MamlMediaDataSource mamlMediaDataSource = this.mSource;
        if (mamlMediaDataSource == null || !TextUtils.equals(mamlMediaDataSource.getPath(), str)) {
            this.mSource = new MamlMediaDataSource(getContext().mContext, getContext().mResourceManager, str);
        }
        this.mView.setLooping(z);
        this.mView.setScaleMode(i);
        this.mView.setVideoDataSource(this.mSource);
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    protected void doTick(long j) {
        super.doTick(j);
        BaseVideoView baseVideoView = this.mView;
        if (baseVideoView != null) {
            baseVideoView.doTick();
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        MamlMediaDataSource mamlMediaDataSource;
        super.finish();
        if (Build.VERSION.SDK_INT >= 23 && (mamlMediaDataSource = this.mSource) != null) {
            mamlMediaDataSource.close();
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected View getView() {
        return this.mView;
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroupRC, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        BaseVideoView baseVideoView;
        super.init();
        if (Build.VERSION.SDK_INT >= 23 && (baseVideoView = this.mView) != null) {
            baseVideoView.init(getVariables());
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected void onViewAdded(View view) {
        super.onViewAdded(view);
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected void onViewRemoved(View view) {
        super.onViewRemoved(view);
        stop();
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        BaseVideoView baseVideoView;
        Log.d("VideoElement", "pause");
        if (Build.VERSION.SDK_INT >= 23 && (baseVideoView = this.mView) != null) {
            baseVideoView.pause();
        }
    }

    public void play() {
        Log.d("VideoElement", "play");
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        MamlMediaDataSource mamlMediaDataSource = this.mSource;
        if (mamlMediaDataSource != null) {
            mamlMediaDataSource.tryToGenerateMemoryFile();
        }
        BaseVideoView baseVideoView = this.mView;
        if (baseVideoView != null) {
            baseVideoView.start();
        }
    }

    public void seekTo(int i) {
        BaseVideoView baseVideoView;
        Log.d("VideoElement", "seekTo " + i);
        if (Build.VERSION.SDK_INT >= 23 && (baseVideoView = this.mView) != null) {
            baseVideoView.seekTo(i);
        }
    }

    public void setVolume(float f) {
        BaseVideoView baseVideoView;
        Log.d("VideoElement", "setVolume " + f);
        if (Build.VERSION.SDK_INT >= 23 && (baseVideoView = this.mView) != null) {
            baseVideoView.setVolume(f);
        }
    }

    public void stop() {
        Log.d("VideoElement", "stop");
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        BaseVideoView baseVideoView = this.mView;
        if (baseVideoView != null) {
            baseVideoView.stopPlayback();
        }
        MamlMediaDataSource mamlMediaDataSource = this.mSource;
        if (mamlMediaDataSource != null) {
            mamlMediaDataSource.releaseMemoryFile();
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected void updateView() {
        BaseVideoView baseVideoView = this.mView;
        if (baseVideoView != null) {
            if (this.mUpdatePosition || this.mUpdateTranslation || this.mUpdateSize) {
                onUpdateView(baseVideoView);
            }
        }
    }
}
