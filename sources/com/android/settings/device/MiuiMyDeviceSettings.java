package com.android.settings.device;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.aidl.IRemoteGetDeviceInfoService;
import com.android.settings.aidl.IRequestCallback;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.device.controller.MaintenanceModeController;
import com.android.settings.device.controller.MiuiAllSpecsController;
import com.android.settings.device.controller.MiuiBackupController;
import com.android.settings.device.controller.MiuiCredentialsController;
import com.android.settings.device.controller.MiuiDeviceSecurityPatchController;
import com.android.settings.device.controller.MiuiFactoryResetController;
import com.android.settings.device.controller.MiuiFirmwareVersionController;
import com.android.settings.device.controller.MiuiInstructionController;
import com.android.settings.device.controller.MiuiOneKeyMirgrateController;
import com.android.settings.device.controller.MiuiPreInstallController;
import com.android.settings.device.controller.MiuiSafetylegalController;
import com.android.settings.device.controller.MiuiTransferRecordController;
import com.android.settings.device.controller.MiuiVersionController;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes.dex */
public class MiuiMyDeviceSettings extends DashboardFragment {
    public static final int DEVICE_MODEL_ORDER;
    public static final int DEVICE_NAME_ORDER;
    private DeviceBasicInfoPresenter mDeviceBasicInfoPresenter;
    private UpdateInfoCallback mDeviceInfoCallback;
    MiuiDeviceNameCard mDeviceNameCardView;
    private View mGridViewRoot;
    private DeviceParamsInitHelper mHelper;
    private boolean mIsOwnerUser;
    MiuiMemoryCard mMemoryCardView;
    private IRemoteGetDeviceInfoService mRemoteService;
    private RemoteServiceConn mRemoteServiceConn;
    private View mRootView;
    MiuiVersionCard mVersionCardView;
    private List<ViewGroup> mCards = new ArrayList();
    private Handler mHandler = new Handler() { // from class: com.android.settings.device.MiuiMyDeviceSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (MiuiMyDeviceSettings.this.mDeviceBasicInfoPresenter == null || MiuiMyDeviceSettings.this.mGridViewRoot == null) {
                Log.e("MiuiMyDeviceSettings", "Presenter or RootView is null");
                return;
            }
            int i = message.what;
            if (i == 0) {
                MiuiMyDeviceSettings.this.mDeviceBasicInfoPresenter.showBasicInfoGridView(MiuiMyDeviceSettings.this.mGridViewRoot, (String) message.obj, false, null);
            } else if (i != 1) {
            } else {
                MiuiMyDeviceSettings.this.mDeviceBasicInfoPresenter.updateCameraInfo((String) message.obj);
            }
        }
    };

    /* loaded from: classes.dex */
    private class RemoteServiceConn implements ServiceConnection {
        private RemoteServiceConn() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiuiMyDeviceSettings.this.mRemoteService = IRemoteGetDeviceInfoService.Stub.asInterface(iBinder);
            FragmentActivity activity = MiuiMyDeviceSettings.this.getActivity();
            if (activity == null) {
                return;
            }
            MiuiMyDeviceSettings miuiMyDeviceSettings = MiuiMyDeviceSettings.this;
            miuiMyDeviceSettings.mHelper = new DeviceParamsInitHelper(activity, miuiMyDeviceSettings.mRemoteService);
            try {
                MiuiMyDeviceSettings.this.mRemoteService.registerCallback(MiuiMyDeviceSettings.this.mDeviceInfoCallback);
                MiuiMyDeviceSettings.this.mHelper.initDeviceParams(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("MiuiMyDeviceSettings", "onServiceDisconnected");
        }
    }

    /* loaded from: classes.dex */
    private static class UpdateInfoCallback extends IRequestCallback.Stub {
        private WeakReference<MiuiMyDeviceSettings> mFragmentRef;
        private boolean mIsInitDeviceUseModel = true;
        private boolean mIsInitCameraUseModel = true;

        public UpdateInfoCallback(MiuiMyDeviceSettings miuiMyDeviceSettings) {
            this.mFragmentRef = new WeakReference<>(miuiMyDeviceSettings);
        }

        @Override // com.android.settings.aidl.IRequestCallback
        public void onRequestComplete(int i, String str) {
            MiuiMyDeviceSettings miuiMyDeviceSettings = this.mFragmentRef.get();
            if (miuiMyDeviceSettings == null || miuiMyDeviceSettings.getActivity() == null) {
                return;
            }
            Message obtain = Message.obtain();
            DeviceParamsInitHelper initHelper = miuiMyDeviceSettings.getInitHelper();
            Handler handler = miuiMyDeviceSettings.getHandler();
            if (initHelper == null || handler == null) {
                Log.e("MiuiMyDeviceSettings", "deal response error");
            } else if (i != 0) {
                if (i != 1) {
                    return;
                }
                obtain.obj = str;
                obtain.what = 1;
                handler.sendMessage(obtain);
                if ((TextUtils.isEmpty(str) || !ParseMiShopDataUtils.getDataSuccess(str)) && this.mIsInitCameraUseModel) {
                    this.mIsInitCameraUseModel = false;
                    initHelper.initDeviceParams(false);
                }
            } else {
                obtain.obj = str;
                obtain.what = 0;
                handler.sendMessage(obtain);
                if (!TextUtils.isEmpty(str) && ParseMiShopDataUtils.showBasicItems(str)) {
                    initHelper.initCameraParams(true);
                } else if (this.mIsInitDeviceUseModel) {
                    this.mIsInitDeviceUseModel = false;
                    initHelper.initDeviceParams(false);
                }
            }
        }
    }

    static {
        int i = 2;
        DEVICE_MODEL_ORDER = (SettingsFeatures.hasMarketName() || 0 != 0) ? 3 : 2;
        if (!SettingsFeatures.hasMarketName() && 0 == 0) {
            i = 3;
        }
        DEVICE_NAME_ORDER = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Handler getHandler() {
        return this.mHandler;
    }

    private void initCardView() {
        this.mVersionCardView.setFragment(this);
        this.mDeviceNameCardView.setFragment(this);
        this.mMemoryCardView.setFragment(this);
        this.mCards.add(this.mVersionCardView);
        this.mCards.add(this.mDeviceNameCardView);
        this.mCards.add(this.mMemoryCardView);
        for (ViewGroup viewGroup : this.mCards) {
            Folme.useAt(viewGroup).touch().handleTouchOf(viewGroup, new AnimConfig[0]);
        }
        this.mVersionCardView.refreshUpdateStatus();
        this.mDeviceNameCardView.refreshDeviceName();
    }

    private void initMallCard() {
        View findViewById = this.mRootView.findViewById(R.id.mall_card);
        if (SettingsFeatures.isNeedHideShopEntrance(getContext(), 2678400000L)) {
            findViewById.setVisibility(8);
            return;
        }
        Folme.useAt(findViewById).touch().handleTouchOf(findViewById, new AnimConfig[0]);
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.device.MiuiMyDeviceSettings$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MiuiMyDeviceSettings.this.lambda$initMallCard$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initMallCard$0(View view) {
        Intent intent = new Intent();
        if (MiuiUtils.isAppInstalledAndEnabled(getContext(), "com.xiaomi.shop")) {
            intent.setPackage("com.xiaomi.shop");
            intent.setData(Uri.parse("https://m.mi.com/p?pid=111&root=com.xiaomi.shop2.plugin.webview.RootFragment&cid=3007.0001&url=https%3A%2F%2Fm.mi.com%2Fw%2Fmishop_activity%3F_rt%3Dweex%26pageid%3D556%26sign%3D8aa44926bc0707f203c7ed7aeb606e78%26pdl%3Djianyu&fallback=https%3A%2F%2Fm.mi.com%2Fw%2Fmishop_activity%3F_rt%3Dweex%26pageid%3D556%26sign%3D8aa44926bc0707f203c7ed7aeb606e78%26pdl%3Djianyu&masid=3007.0001"));
            OneTrackInterfaceUtils.trackPreferenceClick("my_device", "native_equity");
        } else {
            intent.setData(Uri.parse("mimarket://details/detailcard?id=com.xiaomi.shop&ref=shezhi&launchWhenInstalled=true"));
        }
        startActivity(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        MiuiVersionController miuiVersionController = new MiuiVersionController(context);
        miuiVersionController.setIsAvailable(false);
        arrayList.add(miuiVersionController);
        MiuiFirmwareVersionController miuiFirmwareVersionController = new MiuiFirmwareVersionController(context);
        miuiFirmwareVersionController.setIsAvailable(false);
        arrayList.add(miuiFirmwareVersionController);
        MiuiDeviceSecurityPatchController miuiDeviceSecurityPatchController = new MiuiDeviceSecurityPatchController(context);
        miuiDeviceSecurityPatchController.setIsAvailable(false);
        arrayList.add(miuiDeviceSecurityPatchController);
        arrayList.add(new MiuiAllSpecsController(context, this));
        arrayList.add(new MiuiBackupController(context));
        arrayList.add(new MiuiOneKeyMirgrateController(context, getActivity()));
        arrayList.add(new MiuiPreInstallController(context));
        arrayList.add(new MiuiSafetylegalController(context));
        arrayList.add(new MiuiInstructionController(context));
        arrayList.add(new MiuiCredentialsController(context));
        arrayList.add(new MiuiFactoryResetController(context));
        arrayList.add(new MiuiTransferRecordController(context));
        arrayList.add(new MaintenanceModeController(context));
        return arrayList;
    }

    public DeviceParamsInitHelper getInitHelper() {
        return this.mHelper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return MiuiMyDeviceSettings.class.getSimpleName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiMyDeviceSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.my_device_settings;
    }

    public DeviceBasicInfoPresenter getPresenter() {
        return this.mDeviceBasicInfoPresenter;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        boolean z = UserHandle.myUserId() == 0;
        this.mIsOwnerUser = z;
        setHasOptionsMenu(z);
        if (SettingsFeatures.isShowMyDevice()) {
            getPreferenceScreen().setTitle(R.string.my_device);
        } else {
            getPreferenceScreen().setTitle(R.string.about_settings);
        }
        if (0 == 0) {
            this.mDeviceBasicInfoPresenter = new DeviceBasicInfoPresenter(getActivity());
            this.mDeviceInfoCallback = new UpdateInfoCallback(this);
            this.mRemoteServiceConn = new RemoteServiceConn();
            RemoteServiceUtil.bindRemoteService(getActivity(), this.mRemoteServiceConn);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (viewGroup != null && 0 == 0) {
            MiuiUtils.updateFragmentView(getActivity(), viewGroup);
        }
        if (this.mRootView == null) {
            View inflate = layoutInflater.inflate(R.layout.my_device_settings, viewGroup, false);
            this.mRootView = inflate;
            ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
            View view = (View) getListView().getParent();
            if (view instanceof SpringBackLayout) {
                view.setEnabled(false);
            }
            View findViewById = this.mRootView.findViewById(R.id.device_params);
            this.mGridViewRoot = findViewById;
            findViewById.setVisibility(8);
            this.mVersionCardView = (MiuiVersionCard) this.mRootView.findViewById(R.id.miui_version_card_view);
            this.mDeviceNameCardView = (MiuiDeviceNameCard) this.mRootView.findViewById(R.id.device_name_card_view);
            this.mMemoryCardView = (MiuiMemoryCard) this.mRootView.findViewById(R.id.device_memory_card_view);
        }
        return this.mRootView;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        IRemoteGetDeviceInfoService iRemoteGetDeviceInfoService = this.mRemoteService;
        if (iRemoteGetDeviceInfoService != null) {
            try {
                UpdateInfoCallback updateInfoCallback = this.mDeviceInfoCallback;
                if (updateInfoCallback != null) {
                    iRemoteGetDeviceInfoService.unregisteCallback(updateInfoCallback);
                    this.mDeviceInfoCallback = null;
                }
                this.mRemoteService = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        FragmentActivity activity = getActivity();
        RemoteServiceConn remoteServiceConn = this.mRemoteServiceConn;
        if (remoteServiceConn == null || activity == null) {
            return;
        }
        activity.unbindService(remoteServiceConn);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        this.mRootView = null;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        MiStatInterfaceUtils.trackPageEnd("provision_about_page_v85x");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        initCardView();
        MiStatInterfaceUtils.trackPageStart("provision_about_page_v85x");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        initMallCard();
    }
}
