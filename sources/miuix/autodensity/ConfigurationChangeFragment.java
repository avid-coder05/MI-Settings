package miuix.autodensity;

import android.app.Fragment;
import android.content.res.Configuration;

/* loaded from: classes5.dex */
public class ConfigurationChangeFragment extends Fragment {
    @Override // android.app.Fragment, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        DebugUtil.printDensityLog("ConfigChangeFragment activity: " + getActivity());
        DensityUtil.updateCustomDensity(getActivity());
        super.onConfigurationChanged(configuration);
    }
}
