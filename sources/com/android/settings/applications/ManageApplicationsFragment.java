package com.android.settings.applications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.settings.BaseFragment;
import com.android.settings.applications.ApplicationsContainer;
import java.util.Iterator;

/* loaded from: classes.dex */
public class ManageApplicationsFragment extends BaseFragment {
    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.BaseFragment, miuix.appcompat.app.Fragment, miuix.appcompat.app.IFragment
    public View onInflateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        int i = getArguments().getInt("filter_app_key");
        ApplicationsContainer applicationsContainer = (ApplicationsContainer) getActivity().getSupportFragmentManager().findFragmentByTag(ApplicationsContainer.class.getName());
        if (applicationsContainer == null) {
            applicationsContainer = (ApplicationsContainer) getParentFragment();
        }
        if (applicationsContainer != null) {
            Iterator<ApplicationsContainer.TabInfo> it = applicationsContainer.getTabs().iterator();
            while (it.hasNext()) {
                ApplicationsContainer.TabInfo next = it.next();
                if (next.mListType == i) {
                    return next.build(layoutInflater);
                }
            }
            return null;
        }
        return null;
    }
}
