package com.android.settings;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/* loaded from: classes.dex */
public class MiuiSearchDrawable extends MiuiAnimationController {
    public MiuiSearchDrawable(Context context) {
        this(context, R.drawable.action_button_search);
    }

    public MiuiSearchDrawable(Context context, int i) {
        super(context, i);
    }

    @Override // com.android.settings.MiuiAnimationController
    protected Animatable getAnimationDrawable(Drawable drawable) {
        StateListDrawable stateListDrawable = (StateListDrawable) drawable;
        return stateListDrawable.getStateDrawable(stateListDrawable.findStateDrawableIndex(new int[]{16842910}));
    }

    public Drawable getSearchIcon() {
        return getAnimationIcon();
    }

    @Override // com.android.settings.MiuiAnimationController
    public void stopAnimation() {
        super.stopAnimation();
        getAnimationDrawable().stop();
    }
}
