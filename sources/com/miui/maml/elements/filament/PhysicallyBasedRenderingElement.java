package com.miui.maml.elements.filament;

import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import com.miui.maml.ScreenElementRoot;
import com.miui.maml.data.Expression;
import com.miui.maml.elements.ViewHolderScreenElement;
import com.miui.maml.util.Utils;
import org.w3c.dom.Element;

/* loaded from: classes2.dex */
public class PhysicallyBasedRenderingElement extends ViewHolderScreenElement {
    private static final String[] TAGS = {"Cust", "Gltf"};
    private PbrModel mModel;
    private View mView;
    private String mViewType;

    public PhysicallyBasedRenderingElement(Element element, ScreenElementRoot screenElementRoot) {
        super(element, screenElementRoot);
        load(element, screenElementRoot);
    }

    private void load(Element element, final ScreenElementRoot screenElementRoot) {
        if (element == null) {
            return;
        }
        if (this.mView == null) {
            String attribute = element.getAttribute("viewType");
            this.mViewType = attribute;
            if (TextUtils.isEmpty(attribute)) {
                this.mViewType = "texture";
            }
            String str = this.mViewType;
            str.hashCode();
            if (str.equals("surface")) {
                this.mView = new SurfaceView(screenElementRoot.getContext().mContext);
            } else if (!str.equals("texture")) {
                Log.w("PhysicallyBasedRendering", "wrong view type: " + this.mViewType);
                return;
            } else {
                this.mView = new TextureView(screenElementRoot.getContext().mContext);
            }
        }
        Utils.traverseXmlElementChildrenTags(element, TAGS, new Utils.XmlTraverseListener() { // from class: com.miui.maml.elements.filament.PhysicallyBasedRenderingElement.1
            @Override // com.miui.maml.util.Utils.XmlTraverseListener
            public void onChild(Element element2) {
                if (PhysicallyBasedRenderingElement.this.mModel == null) {
                    String tagName = element2.getTagName();
                    tagName.hashCode();
                    if (tagName.equals("Cust")) {
                        PhysicallyBasedRenderingElement.this.mModel = new PbrCust(element2, PhysicallyBasedRenderingElement.this.getContext().mResourceManager, screenElementRoot);
                    } else if (tagName.equals("Gltf")) {
                        PhysicallyBasedRenderingElement physicallyBasedRenderingElement = PhysicallyBasedRenderingElement.this;
                        physicallyBasedRenderingElement.mModel = new PbrGltf(element2, physicallyBasedRenderingElement.getContext().mResourceManager, screenElementRoot);
                    }
                }
            }
        });
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    protected void doTick(long j) {
        super.doTick(j);
        PbrModel pbrModel = this.mModel;
        if (pbrModel != null) {
            pbrModel.render(j);
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void finish() {
        super.finish();
        PbrModel pbrModel = this.mModel;
        if (pbrModel != null) {
            pbrModel.finish();
        }
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement
    protected View getView() {
        return this.mView;
    }

    @Override // com.miui.maml.elements.ViewHolderScreenElement, com.miui.maml.elements.ElementGroupRC, com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void init() {
        super.init();
        PbrModel pbrModel = this.mModel;
        if (pbrModel != null) {
            pbrModel.init(this.mView);
        }
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.AnimatedScreenElement, com.miui.maml.elements.ScreenElement
    public void pause() {
        super.pause();
        PbrModel pbrModel = this.mModel;
        if (pbrModel != null) {
            pbrModel.pause();
        }
    }

    @Override // com.miui.maml.elements.ElementGroup, com.miui.maml.elements.ScreenElement
    public void resume() {
        super.resume();
        PbrModel pbrModel = this.mModel;
        if (pbrModel != null) {
            pbrModel.resume();
        }
    }

    public void updateUniform(String str, String str2, boolean z, Expression[] expressionArr) {
        PbrModel pbrModel = this.mModel;
        if (pbrModel == null || !(pbrModel instanceof PbrCust)) {
            return;
        }
        ((PbrCust) pbrModel).updateUniform(str, str2, z, expressionArr);
    }
}
