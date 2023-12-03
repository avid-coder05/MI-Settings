package miuix.animation.controller;

import miuix.animation.IAnimTarget;
import miuix.animation.IVarFontStyle;
import miuix.animation.base.AnimConfig;
import miuix.animation.font.FontWeightProperty;
import miuix.animation.utils.CommonUtils;
import miuix.animation.utils.EaseManager;

/* loaded from: classes5.dex */
public class FolmeFont extends FolmeBase implements IVarFontStyle {
    private AnimConfig mDefaultTo;
    private int mInitValue;
    private boolean mIsInitSet;
    private FontWeightProperty mProperty;

    /* loaded from: classes5.dex */
    public enum FontType {
        INIT,
        TARGET
    }

    public FolmeFont() {
        super(new IAnimTarget[0]);
        AnimConfig animConfig = new AnimConfig();
        this.mDefaultTo = animConfig;
        animConfig.setEase(EaseManager.getStyle(0, 350.0f, 0.9f, 0.86f));
    }

    @Override // miuix.animation.controller.FolmeBase, miuix.animation.IStateContainer
    public void clean() {
        super.clean();
        this.mState = null;
        this.mInitValue = 0;
    }

    public IVarFontStyle to(int i, AnimConfig... animConfigArr) {
        IFolmeStateStyle iFolmeStateStyle = this.mState;
        if (iFolmeStateStyle != null) {
            if (!this.mIsInitSet) {
                this.mIsInitSet = true;
                iFolmeStateStyle.setTo(FontType.INIT);
            }
            AnimConfig[] animConfigArr2 = (AnimConfig[]) CommonUtils.mergeArray(animConfigArr, this.mDefaultTo);
            if (this.mInitValue == i) {
                this.mState.to(FontType.INIT, animConfigArr2);
            } else {
                IFolmeStateStyle iFolmeStateStyle2 = this.mState;
                FontType fontType = FontType.TARGET;
                iFolmeStateStyle2.getState(fontType).add(this.mProperty, i);
                this.mState.to(fontType, animConfigArr2);
            }
        }
        return this;
    }
}
