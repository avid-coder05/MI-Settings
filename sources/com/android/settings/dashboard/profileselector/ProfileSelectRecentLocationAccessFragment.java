package com.android.settings.dashboard.profileselector;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.android.settings.location.RecentLocationAccessSeeAllFragment;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes.dex */
public class ProfileSelectRecentLocationAccessFragment extends ProfileSelectFragment {
    @Override // com.android.settings.dashboard.profileselector.ProfileSelectFragment
    public Fragment[] getFragments() {
        Bundle bundle = new Bundle();
        bundle.putInt(YellowPageContract.Profile.DIRECTORY, 2);
        RecentLocationAccessSeeAllFragment recentLocationAccessSeeAllFragment = new RecentLocationAccessSeeAllFragment();
        recentLocationAccessSeeAllFragment.setArguments(bundle);
        Bundle bundle2 = new Bundle();
        bundle2.putInt(YellowPageContract.Profile.DIRECTORY, 1);
        RecentLocationAccessSeeAllFragment recentLocationAccessSeeAllFragment2 = new RecentLocationAccessSeeAllFragment();
        recentLocationAccessSeeAllFragment2.setArguments(bundle2);
        return new Fragment[]{recentLocationAccessSeeAllFragment2, recentLocationAccessSeeAllFragment};
    }
}
