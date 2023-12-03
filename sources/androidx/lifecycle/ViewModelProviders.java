package androidx.lifecycle;

import androidx.fragment.app.FragmentActivity;

@Deprecated
/* loaded from: classes.dex */
public class ViewModelProviders {
    @Deprecated
    public static ViewModelProvider of(FragmentActivity fragmentActivity) {
        return new ViewModelProvider(fragmentActivity);
    }
}
