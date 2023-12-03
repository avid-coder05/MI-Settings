package com.miui.maml.elements.filament;

import android.util.ArrayMap;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import com.miui.maml.ResourceManager;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PbrCust extends PbrModel {
    private static final String[] TAGS = {"Offscreen", "Final"};
    private CustFinal mFinal;
    private ArrayMap<String, CustFrameBuffer> mFrameBuffers;
    private CustModelViewer mModelViewer;

    public PbrCust(Element element, ResourceManager resourceManager, ScreenElementRoot screenElementRoot) {
        super(element, resourceManager, screenElementRoot);
        this.mFrameBuffers = new ArrayMap<>();
        FilamentManager.getInstance().load();
        load(element, screenElementRoot);
    }

    private void load(Element element, final ScreenElementRoot screenElementRoot) {
        Utils.traverseXmlElementChildrenTags(element, TAGS, new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.filament.PbrCust.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                String tagName = element2.getTagName();
                tagName.hashCode();
                if (tagName.equals("Offscreen")) {
                    CustFrameBuffer custFrameBuffer = new CustFrameBuffer(element2, PbrCust.this.mResMgr, screenElementRoot);
                    PbrCust.this.mFrameBuffers.put(custFrameBuffer.getName(), custFrameBuffer);
                } else if (tagName.equals("Final") && PbrCust.this.mFinal == null) {
                    PbrCust pbrCust = PbrCust.this;
                    pbrCust.mFinal = new CustFinal(element2, pbrCust.mResMgr, screenElementRoot);
                }
            }
        });
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void finish() {
        if (this.mEngine == null) {
            return;
        }
        int size = this.mFrameBuffers.size();
        for (int i = 0; i < size; i++) {
            try {
                this.mFrameBuffers.valueAt(i).onDestroy(this.mEngine);
            } catch (Exception unused) {
            }
        }
        CustFinal custFinal = this.mFinal;
        if (custFinal != null) {
            custFinal.onDestroy(this.mEngine);
        }
        CustModelViewer custModelViewer = this.mModelViewer;
        if (custModelViewer != null) {
            custModelViewer.destroy();
        }
        FilamentManager.getInstance().releaseEngine();
        this.mEngine = null;
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void init(View view) {
        if (this.mEngine == null) {
            this.mEngine = FilamentManager.getInstance().acquireEngine();
        }
        if (view instanceof SurfaceView) {
            this.mModelViewer = new CustModelViewer((SurfaceView) view, this.mEngine);
        } else if (view instanceof TextureView) {
            this.mModelViewer = new CustModelViewer((TextureView) view, this.mEngine);
        }
        int size = this.mFrameBuffers.size();
        for (int i = 0; i < size; i++) {
            this.mFrameBuffers.valueAt(i).init(this.mEngine, this.mResMgr, view);
        }
        this.mModelViewer.setFrameBuffers(this.mFrameBuffers);
        CustFinal custFinal = this.mFinal;
        if (custFinal != null) {
            custFinal.init(this.mEngine, this.mResMgr, view);
            this.mModelViewer.setFinal(this.mFinal);
        }
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void pause() {
        int size = this.mFrameBuffers.size();
        for (int i = 0; i < size; i++) {
            this.mFrameBuffers.valueAt(i).pause();
        }
        this.mFinal.pause();
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void render(long j) {
        CustModelViewer custModelViewer = this.mModelViewer;
        if (custModelViewer != null) {
            custModelViewer.render(j * 1000000);
        }
    }

    @Override // com.miui.maml.elements.filament.PbrModel
    public void resume() {
        int size = this.mFrameBuffers.size();
        for (int i = 0; i < size; i++) {
            this.mFrameBuffers.valueAt(i).resume();
        }
        this.mFinal.resume();
    }

    public void updateUniform(String str, String str2, boolean z, Expression[] expressionArr) {
        CustElement custElement = this.mFrameBuffers.get(str);
        if (custElement == null) {
            custElement = str.equals(this.mFinal.getName()) ? this.mFinal : null;
        }
        if (custElement != null) {
            custElement.updateUniform(str2, z, expressionArr, this.mFrameBuffers);
        }
    }
}
