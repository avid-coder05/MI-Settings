package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.applications.manageapplications.ManageApplications;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class ProfileSelectManageApplications extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle deepCopy = getArguments() != null ? getArguments().deepCopy() : new Bundle();
        deepCopy.putInt(YellowPageContract.Profile.DIRECTORY, 2);
        ManageApplications manageApplications = new ManageApplications();
        manageApplications.setArguments(deepCopy);
        Bundle arguments = getArguments() != null ? getArguments() : new Bundle();
        arguments.putInt(YellowPageContract.Profile.DIRECTORY, 1);
        ManageApplications manageApplications2 = new ManageApplications();
        manageApplications2.setArguments(arguments);
        return new Fragment[]{manageApplications2, manageApplications};
    }
}
