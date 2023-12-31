package com.android.settings.network.telephony;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;

/* loaded from: classes2.dex */
public abstract class BaseDialogFragment extends DialogFragment {
    private static <T> void checkValidity(Activity activity, Fragment fragment, Class<T> cls) {
        if (fragment != null) {
            if (!cls.isInstance(fragment)) {
                throw new IllegalArgumentException("The listener fragment should implement the callback function.");
            }
        } else if (!cls.isInstance(activity)) {
            throw new IllegalArgumentException("The caller activity should implement the callback function.");
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static <T> void setListener(Activity activity, Fragment fragment, Class<T> cls, int i, Bundle bundle) {
        checkValidity(activity, fragment, cls);
        if (fragment != null && fragment.getParentFragment() != null) {
            throw new IllegalArgumentException("The listener must be attached to an activity.");
        }
        bundle.putInt("in_caller_tag", i);
        if (fragment != null) {
            bundle.putString("listener_tag", fragment.getTag());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public <T> T getListener(Class<T> cls) {
        String string = getArguments().getString("listener_tag");
        T t = string == null ? (T) getActivity() : (T) getActivity().getFragmentManager().findFragmentByTag(string);
        if (cls.isInstance(t)) {
            return t;
        }
        throw new IllegalArgumentException("The caller should implement the callback function.");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getTagInCaller() {
        return getArguments().getInt("in_caller_tag");
    }
}
