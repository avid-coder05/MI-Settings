package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.android.settings.MiuiSettings;
import com.android.settings.cust.MiHomeManager;
import com.android.settings.utils.HeaderUtils;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miuix.appcompat.app.Fragment;
import miuix.recyclerview.widget.RecyclerView;

/* loaded from: classes.dex */
public abstract class BasePreferenceFragment extends Fragment {
    protected List<PreferenceActivity.Header> mHeaders;
    private RecyclerView mList;
    private MiHomeManager mMiHomeManager;
    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() { // from class: com.android.settings.BasePreferenceFragment.1
        @Override // android.widget.AdapterView.OnItemClickListener
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            boolean z = Build.IS_MIPAD;
            if (i < 0 || i >= BasePreferenceFragment.this.mHeaders.size()) {
                return;
            }
            BasePreferenceFragment.this.onHeaderClick(BasePreferenceFragment.this.mHeaders.get(i), i);
        }
    };
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.BasePreferenceFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            MiuiSettings.ProxyHeaderViewAdapter headerAdapter;
            String action = intent.getAction();
            if (("android.intent.action.AIRPLANE_MODE".equals(action) || "android.intent.action.SIM_STATE_CHANGED".equals(action)) && (headerAdapter = BasePreferenceFragment.this.getHeaderAdapter()) != null) {
                headerAdapter.notifyDataSetChanged();
            }
        }
    };

    private RecyclerView getListView() {
        View view = getView();
        if (view == null) {
            return null;
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.scroll_headers);
        this.mList = recyclerView;
        if (recyclerView != null) {
            return recyclerView;
        }
        throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHeaderClick(PreferenceActivity.Header header, int i) {
        MiHomeManager miHomeManager = this.mMiHomeManager;
        if (miHomeManager.isMiHomeManagerInstalled && miHomeManager.isForbidden(header.fragment)) {
            Toast.makeText(getActivity(), R.string.settings_forbidden_message, 0).show();
            return;
        }
        try {
            ((MiuiSettings) getActivity()).onHeaderClick(header, i);
        } catch (ActivityNotFoundException unused) {
        }
    }

    public void buildAdapter() {
        buildHeaders();
        if (this.mHeaders == null) {
            this.mHeaders = new ArrayList();
            MiuiSettings.HeaderAdapter baseAdapter = getHeaderAdapter() != null ? getHeaderAdapter().getBaseAdapter() : null;
            if (baseAdapter != null) {
                for (int i = 0; i < baseAdapter.getItemCount(); i++) {
                    this.mHeaders.add(baseAdapter.getItem(i));
                }
            }
        }
        MiuiSettings.ProxyHeaderViewAdapter headerAdapter = getHeaderAdapter();
        if (headerAdapter != null) {
            headerAdapter.pause();
        }
    }

    public void buildHeaders() {
        MiuiSettings miuiSettings = (MiuiSettings) getActivity();
        if (miuiSettings == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        int headersResourceId = getHeadersResourceId();
        if (headersResourceId > 0) {
            HeaderUtils.loadHeadersFromResource(getContext(), headersResourceId, arrayList);
            miuiSettings.updateHeaderList(arrayList);
        }
        this.mHeaders = arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MiuiSettings.ProxyHeaderViewAdapter getHeaderAdapter() {
        return (MiuiSettings.ProxyHeaderViewAdapter) getListView().getAdapter();
    }

    protected int getHeadersResourceId() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.AIRPLANE_MODE");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        getActivity().registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        getActivity().unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    @Override // androidx.fragment.app.Fragment, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        onTrimMemory(80);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MiuiSettings.ProxyHeaderViewAdapter headerAdapter = getHeaderAdapter();
        if (headerAdapter != null) {
            headerAdapter.pause();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        MiuiSettings.ProxyHeaderViewAdapter headerAdapter = getHeaderAdapter();
        if (headerAdapter != null) {
            headerAdapter.resume();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        MiuiSettings.ProxyHeaderViewAdapter headerAdapter = getHeaderAdapter();
        if (headerAdapter != null) {
            headerAdapter.start();
        }
    }

    @Override // miuix.appcompat.app.Fragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        MiuiSettings.ProxyHeaderViewAdapter headerAdapter = getHeaderAdapter();
        if (headerAdapter != null) {
            headerAdapter.stop();
        }
    }

    public void onTrimMemory(int i) {
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        buildAdapter();
        this.mMiHomeManager = MiHomeManager.getInstance(getActivity());
        super.onViewCreated(view, bundle);
    }

    @Override // miuix.appcompat.app.Fragment
    public void setThemeRes(int i) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void startSelectHeader() {
        if (TabletUtils.IS_TABLET) {
            MiuiSettings miuiSettings = (MiuiSettings) getActivity();
            int backStackEntryCount = getFragmentManager().getBackStackEntryCount();
            if (this.mHeaders != null) {
                for (int i = 0; i < this.mHeaders.size(); i++) {
                    PreferenceActivity.Header header = this.mHeaders.get(i);
                    if (TextUtils.equals(header.fragment, miuiSettings.getSelectHeaderFragment())) {
                        getListView();
                        if (backStackEntryCount == 0) {
                            miuiSettings.onHeaderClick(header, i);
                            return;
                        }
                        return;
                    }
                }
            }
        }
    }
}
