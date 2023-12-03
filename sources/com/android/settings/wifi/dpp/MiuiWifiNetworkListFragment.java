package com.android.settings.wifi.dpp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.wifi.MiuiAddNetworkFragment;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.AccessPointPreference;
import com.android.settingslib.wifi.WifiSavedConfigUtils;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTrackerFactory;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* loaded from: classes2.dex */
public class MiuiWifiNetworkListFragment extends SettingsPreferenceFragment implements WifiTracker.WifiListener, AccessPoint.AccessPointListener {
    private PreferenceCategory mAccessPointsPreferenceCategory;
    private Preference mAddPreference;
    private Preference mFakeNetworkPreference;
    private WeakReference<MiuiWifiNetworkListFragment> mFragmentRef;
    private boolean mIsTest;
    private OnChooseNetworkListener mOnChooseNetworkListener;
    private WifiManager.ActionListener mSaveListener;
    private AccessPointPreference.UserBadgeCache mUserBadgeCache;
    private WifiManager mWifiManager;
    private WeakReference<Activity> mWifiSettingsActivityRef;
    private WifiTracker mWifiTracker;

    /* loaded from: classes2.dex */
    public interface OnChooseNetworkListener {
        void onChooseNetwork(WifiNetworkConfig wifiNetworkConfig);
    }

    private AccessPointPreference createAccessPointPreference(AccessPoint accessPoint) {
        return new AccessPointPreference(accessPoint, getPrefContext(), this.mUserBadgeCache, R.drawable.ic_wifi_signal_0, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: getScannedAccessPointIfAvailable  reason: merged with bridge method [inline-methods] */
    public AccessPoint lambda$updateAccessPointPreferences$2(AccessPoint accessPoint) {
        List<AccessPoint> accessPoints = this.mWifiTracker.getAccessPoints();
        WifiConfiguration config = accessPoint.getConfig();
        for (AccessPoint accessPoint2 : accessPoints) {
            if (accessPoint2.matches(config)) {
                return accessPoint2;
            }
        }
        return accessPoint;
    }

    private void handleAddNetworkSubmitEvent(Intent intent) {
        WifiConfiguration wifiConfiguration = (WifiConfiguration) intent.getParcelableExtra("wifi_config_key");
        if (wifiConfiguration != null) {
            this.mWifiManager.save(wifiConfiguration, this.mSaveListener);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: isValidForDppConfiguration  reason: merged with bridge method [inline-methods] */
    public boolean lambda$updateAccessPointPreferences$1(AccessPoint accessPoint) {
        int security = accessPoint.getSecurity();
        return security == 2 || security == 5;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onAccessPointChanged$0(AccessPoint accessPoint) {
        Object tag = accessPoint.getTag();
        if (tag != null) {
            ((AccessPointPreference) tag).refresh();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$updateAccessPointPreferences$3(AccessPoint accessPoint, AccessPoint accessPoint2) {
        if (!accessPoint.isReachable() || accessPoint2.isReachable()) {
            if (accessPoint.isReachable() || !accessPoint2.isReachable()) {
                return nullToEmpty(accessPoint.getTitle()).compareToIgnoreCase(nullToEmpty(accessPoint2.getTitle()));
            }
            return 1;
        }
        return -1;
    }

    private void launchMiuiAddNetworkFragment() {
        new SubSettingLauncher(getContext()).setTitleRes(R.string.wifi_add_network).setDestination(MiuiAddNetworkFragment.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setResultListener(this, 1).launch();
    }

    private String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    private void removeAccessPointPreferences() {
        this.mAccessPointsPreferenceCategory.removeAll();
        this.mAccessPointsPreferenceCategory.setVisible(false);
    }

    private void updateAccessPointPreferences() {
        if (this.mWifiManager.isWifiEnabled()) {
            List<AccessPoint> list = (List) WifiSavedConfigUtils.getAllConfigs(getContext(), this.mWifiManager).stream().filter(new Predicate() { // from class: com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updateAccessPointPreferences$1;
                    lambda$updateAccessPointPreferences$1 = MiuiWifiNetworkListFragment.this.lambda$updateAccessPointPreferences$1((AccessPoint) obj);
                    return lambda$updateAccessPointPreferences$1;
                }
            }).map(new Function() { // from class: com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    AccessPoint lambda$updateAccessPointPreferences$2;
                    lambda$updateAccessPointPreferences$2 = MiuiWifiNetworkListFragment.this.lambda$updateAccessPointPreferences$2((AccessPoint) obj);
                    return lambda$updateAccessPointPreferences$2;
                }
            }).sorted(new Comparator() { // from class: com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment$$ExternalSyntheticLambda1
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int lambda$updateAccessPointPreferences$3;
                    lambda$updateAccessPointPreferences$3 = MiuiWifiNetworkListFragment.this.lambda$updateAccessPointPreferences$3((AccessPoint) obj, (AccessPoint) obj2);
                    return lambda$updateAccessPointPreferences$3;
                }
            }).collect(Collectors.toList());
            int i = 0;
            this.mAccessPointsPreferenceCategory.removeAll();
            for (AccessPoint accessPoint : list) {
                AccessPointPreference createAccessPointPreference = createAccessPointPreference(accessPoint);
                createAccessPointPreference.setOrder(i);
                createAccessPointPreference.setEnabled(accessPoint.isReachable());
                accessPoint.setListener(this);
                createAccessPointPreference.refresh();
                this.mAccessPointsPreferenceCategory.addPreference(createAccessPointPreference);
                i++;
            }
            this.mAddPreference.setOrder(i);
            this.mAccessPointsPreferenceCategory.addPreference(this.mAddPreference);
            if (this.mIsTest) {
                this.mAccessPointsPreferenceCategory.addPreference(this.mFakeNetworkPreference);
            }
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1595;
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onAccessPointChanged(final AccessPoint accessPoint) {
        Log.d("MiuiWifiNetworkListFragment", "onAccessPointChanged (singular) callback initiated");
        View view = getView();
        if (view != null) {
            view.post(new Runnable() { // from class: com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    MiuiWifiNetworkListFragment.lambda$onAccessPointChanged$0(AccessPoint.this);
                }
            });
        }
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onAccessPointsChanged() {
        updateAccessPointPreferences();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mFragmentRef = new WeakReference<>(this);
        WeakReference<Activity> weakReference = new WeakReference<>(getActivity());
        this.mWifiSettingsActivityRef = weakReference;
        WifiTracker create = WifiTrackerFactory.create(weakReference.get(), this.mFragmentRef.get(), getSettingsLifecycle(), true, true);
        this.mWifiTracker = create;
        this.mWifiManager = create.getManager();
        Bundle arguments = getArguments();
        if (arguments != null) {
            this.mIsTest = arguments.getBoolean("test", false);
        }
        this.mSaveListener = new WifiManager.ActionListener() { // from class: com.android.settings.wifi.dpp.MiuiWifiNetworkListFragment.1
            public void onFailure(int i) {
                FragmentActivity activity = MiuiWifiNetworkListFragment.this.getActivity();
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_save_message, 0).show();
                }
            }

            public void onSuccess() {
            }
        };
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1) {
            if (i2 == -1) {
                handleAddNetworkSubmitEvent(intent);
            }
            this.mWifiTracker.resumeScanning();
        }
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof OnChooseNetworkListener)) {
            throw new IllegalArgumentException("Invalid context type");
        }
        this.mOnChooseNetworkListener = (OnChooseNetworkListener) context;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onConnectedChanged() {
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        addPreferencesFromResource(R.xml.miui_wifi_dpp_network_list);
        this.mAccessPointsPreferenceCategory = (PreferenceCategory) findPreference("access_points");
        Preference preference = new Preference(getPrefContext());
        this.mFakeNetworkPreference = preference;
        preference.setIcon(R.drawable.ic_wifi_signal_0);
        this.mFakeNetworkPreference.setKey("fake_key");
        this.mFakeNetworkPreference.setTitle("fake network");
        Preference preference2 = new Preference(getPrefContext());
        this.mAddPreference = preference2;
        preference2.setIcon(R.drawable.ic_add_24dp);
        this.mAddPreference.setTitle(R.string.wifi_add_network);
        this.mUserBadgeCache = new AccessPointPreference.UserBadgeCache(getPackageManager());
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        this.mOnChooseNetworkListener = null;
        super.onDetach();
    }

    @Override // com.android.settingslib.wifi.AccessPoint.AccessPointListener
    public void onLevelChanged(AccessPoint accessPoint) {
        ((AccessPointPreference) accessPoint.getTag()).onLevelChanged();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof AccessPointPreference) {
            AccessPoint accessPoint = ((AccessPointPreference) preference).getAccessPoint();
            if (accessPoint == null) {
                return false;
            }
            WifiConfiguration config = accessPoint.getConfig();
            if (config == null) {
                throw new IllegalArgumentException("Invalid access point");
            }
            WifiNetworkConfig validConfigOrNull = WifiNetworkConfig.getValidConfigOrNull(accessPoint.getSecurityString(true), config.getPrintableSsid(), config.preSharedKey, config.hiddenSSID, config.networkId, false);
            OnChooseNetworkListener onChooseNetworkListener = this.mOnChooseNetworkListener;
            if (onChooseNetworkListener != null) {
                onChooseNetworkListener.onChooseNetwork(validConfigOrNull);
            }
        } else if (preference == this.mAddPreference) {
            launchMiuiAddNetworkFragment();
        } else if (preference != this.mFakeNetworkPreference) {
            return super.onPreferenceTreeClick(preference);
        } else {
            OnChooseNetworkListener onChooseNetworkListener2 = this.mOnChooseNetworkListener;
            if (onChooseNetworkListener2 != null) {
                onChooseNetworkListener2.onChooseNetwork(new WifiNetworkConfig("WPA", "fake network", "password", true, -1, false));
            }
        }
        return true;
    }

    @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
    public void onWifiStateChanged(int i) {
        int wifiState = this.mWifiManager.getWifiState();
        if (wifiState == 0 || wifiState == 2) {
            removeAccessPointPreferences();
        } else if (wifiState != 3) {
        } else {
            updateAccessPointPreferences();
        }
    }
}
