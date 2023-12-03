package com.android.settings.magicwindow;

/* loaded from: classes.dex */
public class MagicWinAppControlFragment extends AppControlBaseFragment {
    @Override // com.android.settings.magicwindow.AppControlBaseFragment
    public IAppController getAppController() {
        return new MagicWinAppController(getActivity(), getActivity(), "magic_window");
    }
}
