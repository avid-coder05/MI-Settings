package com.android.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.connection.MiMirrorController;
import com.android.settings.connection.MiPrintController;
import com.android.settings.connection.MiShareController;
import com.android.settings.connection.ScreenProjectionController;
import com.android.settings.connection.UWBSettingsController;
import com.android.settings.connection.UarScreenSettingsController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.network.AirplaneModePreferenceController;
import com.android.settings.network.MobileNetworkPreferenceController;
import com.android.settings.network.NetworkResetPreferenceController;
import com.android.settings.network.PrivateDnsPreferenceController;
import com.android.settings.nfc.AndroidBeamPreferenceController;
import com.android.settings.notification.EmergencyBroadcastPreferenceController;
import com.android.settings.search.tree.MiuiSecurityAndPrivacySettingsTree;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.wireless.DataUsageController;
import com.android.settings.wireless.MiuiNFCCategoryController;
import com.android.settings.wireless.MiuiNFCController;
import com.android.settings.wireless.MiuiNfcDndModeToggleController;
import com.android.settings.wireless.MiuiNfcPaymentPreferenceController;
import com.android.settings.wireless.MiuiNfcRepairController;
import com.android.settings.wireless.MiuiNfcToggleController;
import com.android.settings.wireless.MiuiWifiDisplayController;
import com.android.settings.wireless.TetherEntryController;
import com.android.settings.wireless.VpnEntryController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class MiuiWirelessSettings extends DashboardFragment {
    private RecyclerView mRecyclerView;
    private boolean mScroll2Nfc = false;
    private final String INTENT_MIUI_NFC = "android.settings.MIUI_NFC_DETIL";

    private void log(String str) {
        Log.d("MiuiWirelessSettings", str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        AirplaneModePreferenceController airplaneModePreferenceController = new AirplaneModePreferenceController(context, "toggle_airplane");
        getLifecycle().addObserver(airplaneModePreferenceController);
        arrayList.add(airplaneModePreferenceController);
        arrayList.add(new VpnEntryController(context));
        PrivateDnsPreferenceController privateDnsPreferenceController = new PrivateDnsPreferenceController(context);
        getLifecycle().addObserver(privateDnsPreferenceController);
        arrayList.add(privateDnsPreferenceController);
        TetherEntryController tetherEntryController = new TetherEntryController(context);
        arrayList.add(tetherEntryController);
        getLifecycle().addObserver(tetherEntryController);
        arrayList.add(new MiuiWifiDisplayController(context));
        arrayList.add(new DataUsageController(context));
        arrayList.add(new MiuiNFCCategoryController(context, getLifecycle()));
        arrayList.add(new MiuiNFCController(context, MiuiNFCController.KEY_MIUI_NFC, getLifecycle()));
        arrayList.add(new MiuiNfcToggleController(context, getLifecycle()));
        arrayList.add(new MiuiNfcRepairController(context, getLifecycle()));
        arrayList.add(new MiuiNfcDndModeToggleController(context, getLifecycle()));
        arrayList.add(new AndroidBeamPreferenceController(context, AndroidBeamPreferenceController.KEY_ANDROID_BEAM_SETTINGS));
        arrayList.add(new MiuiNfcPaymentPreferenceController(context));
        arrayList.add(new NetworkResetPreferenceController(context));
        arrayList.add(new MobileNetworkPreferenceController(context));
        arrayList.add(new EmergencyBroadcastPreferenceController(context, MiuiSecurityAndPrivacySettingsTree.CELL_BROADCAST_SETTINGS));
        arrayList.add(new MiShareController(context, "mishare_settings"));
        arrayList.add(new ScreenProjectionController(context, "screen_projection"));
        arrayList.add(new MiPrintController(context, "miprint_settings"));
        arrayList.add(new MiMirrorController(context, "mimirror_settings"));
        arrayList.add(new UWBSettingsController(context, "uwb_settings"));
        arrayList.add(new UarScreenSettingsController(context, "ucar_screen_settings"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.wireless_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onBindPreferences() {
        super.onBindPreferences();
        removePreference("mobile_network_settings");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            this.mScroll2Nfc = "android.settings.NFC_SETTINGS".equals(getIntent().getAction());
        } catch (Exception unused) {
            this.mScroll2Nfc = false;
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        RecyclerView listView = getListView();
        this.mRecyclerView = listView;
        listView.postDelayed(new Runnable() { // from class: com.android.settings.MiuiWirelessSettings.1
            @Override // java.lang.Runnable
            public void run() {
                if (MiuiWirelessSettings.this.mRecyclerView.getAdapter() != null) {
                    MiuiWirelessSettings.this.mRecyclerView.getAdapter().notifyDataSetChanged();
                }
            }
        }, 50L);
        return onCreateView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mScroll2Nfc) {
            this.mScroll2Nfc = false;
            if (SettingsFeatures.isNeedShowMiuiNFC()) {
                try {
                    startActivity(new Intent("android.settings.MIUI_NFC_DETIL"));
                } catch (Exception e) {
                    log("jump to MIUI_NFC error: " + e.getMessage());
                    e.printStackTrace();
                }
                finish();
                return;
            }
            try {
                RecyclerView listView = getListView();
                int itemCount = listView.getLayoutManager().getItemCount();
                Log.i("MiuiWirelessSettings", "getItemCount: " + itemCount);
                if (itemCount > 0) {
                    listView.smoothScrollToPosition(itemCount - 1);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                Log.e("MiuiWirelessSettings", "Exception: " + e2);
            }
        }
    }
}
