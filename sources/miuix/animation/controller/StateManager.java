package miuix.animation.controller;

import android.util.ArrayMap;
import java.util.Map;
import miuix.animation.IAnimTarget;
import miuix.animation.base.AnimConfigLink;
import miuix.animation.property.FloatProperty;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public class StateManager {
    Object mCurTag;
    final Map<Object, AnimState> mStateMap = new ArrayMap();
    final AnimState mToState = new AnimState("defaultTo", true);
    final AnimState mSetToState = new AnimState("defaultSetTo", true);
    final AnimState mAutoSetToState = new AnimState("autoSetTo", true);
    StateHelper mStateHelper = new StateHelper();

    private AnimState getState(Object obj, boolean z) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof AnimState) {
            return (AnimState) obj;
        }
        AnimState animState = this.mStateMap.get(obj);
        if (animState == null && z) {
            AnimState animState2 = new AnimState(obj);
            addState(animState2);
            return animState2;
        }
        return animState;
    }

    private AnimState getStateByArgs(Object obj, Object... objArr) {
        AnimState animState;
        if (objArr.length > 0) {
            animState = getState(objArr[0], false);
            if (animState == null) {
                animState = getStateByName(objArr);
            }
        } else {
            animState = null;
        }
        return animState == null ? getState(obj) : animState;
    }

    private AnimState getStateByName(Object... objArr) {
        Object obj = objArr[0];
        Object obj2 = objArr.length > 1 ? objArr[1] : null;
        if ((obj instanceof String) && (obj2 instanceof String)) {
            return getState(obj, true);
        }
        return null;
    }

    private void setAnimState(IAnimTarget iAnimTarget, AnimState animState, AnimConfigLink animConfigLink, Object... objArr) {
        this.mStateHelper.parse(iAnimTarget, animState, animConfigLink, objArr);
    }

    public void add(String str, int i) {
        getCurrentState().add(str, i);
    }

    public void add(FloatProperty floatProperty, float f) {
        getCurrentState().add(floatProperty, f);
    }

    public void addState(AnimState animState) {
        this.mStateMap.put(animState.getTag(), animState);
    }

    public void addTempConfig(AnimState animState, AnimConfigLink animConfigLink) {
        AnimState animState2 = this.mToState;
        if (animState != animState2) {
            animConfigLink.add(animState2.getConfig(), new boolean[0]);
        }
    }

    public void clearTempState(AnimState animState) {
        if (animState == this.mToState || animState == this.mSetToState) {
            animState.clear();
        }
    }

    public AnimState getCurrentState() {
        if (this.mCurTag == null) {
            this.mCurTag = this.mToState;
        }
        return getState(this.mCurTag);
    }

    public AnimState getSetToState(IAnimTarget iAnimTarget, AnimConfigLink animConfigLink, Object... objArr) {
        AnimState stateByArgs = getStateByArgs(this.mSetToState, objArr);
        setAnimState(iAnimTarget, stateByArgs, animConfigLink, objArr);
        return stateByArgs;
    }

    public AnimState getState(Object obj) {
        return getState(obj, true);
    }

    public AnimState getToState(IAnimTarget iAnimTarget, AnimConfigLink animConfigLink, Object... objArr) {
        AnimState stateByArgs = getStateByArgs(getCurrentState(), objArr);
        setAnimState(iAnimTarget, stateByArgs, animConfigLink, objArr);
        return stateByArgs;
    }

    public boolean hasState(Object obj) {
        return this.mStateMap.containsKey(obj);
    }

    public AnimState setup(Object obj) {
        AnimState animState;
        if (obj instanceof AnimState) {
            animState = (AnimState) obj;
        } else {
            AnimState animState2 = this.mStateMap.get(obj);
            if (animState2 == null) {
                animState2 = new AnimState(obj);
                addState(animState2);
            }
            animState = animState2;
        }
        this.mCurTag = animState;
        return animState;
    }
}
