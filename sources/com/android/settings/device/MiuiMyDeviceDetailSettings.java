package com.android.settings.device;

import android.app.AppGlobals;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.MiuiUtils;
import com.android.settings.PlatformUtils;
import com.android.settings.R;
import com.android.settings.aidl.IRemoteGetDeviceInfoService;
import com.android.settings.aidl.IRequestCallback;
import com.android.settings.credentials.MiuiCredentialsUpdater;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.device.MemoryInfoHelper;
import com.android.settings.device.controller.MiuiDeviceCpuInfoController$CallBack;
import com.android.settings.device.controller.MiuiDeviceStatusInfoController;
import com.android.settings.device.controller.MiuiLegalInfoController;
import com.android.settings.report.InternationalCompat;
import com.android.settings.utils.SettingsFeatures;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miuix.animation.Folme;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class MiuiMyDeviceDetailSettings extends DashboardFragment {
    private static final String CPU_CLOUD_CONFIG_KEY = Build.DEVICE;
    private MemoryInfoHelper.Callback mCallback;
    private View.OnClickListener mClickListener;
    private Context mContext;
    private UpdateInfoCallback mDeviceInfoCallback;
    private View mGridView;
    private DeviceParamsInitHelper mHelper;
    private BorderedBaseDeviceCardItem mMemoryCardItem;
    private DeviceBasicInfoPresenter mPresenter;
    private IRemoteGetDeviceInfoService mRemoteService;
    private RemoteServiceConn mRemoteServiceConn;
    private View mRootView;
    private boolean mIsNeedUpdateCpu = true;
    private boolean mIsNeedAddBasicCpu = true;
    private List<DeviceCardInfo> mVersionlist = new ArrayList();
    private List<DeviceCardInfo> mHardwareList = new ArrayList();
    private Handler mHandler = new Handler() { // from class: com.android.settings.device.MiuiMyDeviceDetailSettings.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (MiuiMyDeviceDetailSettings.this.mPresenter == null || MiuiMyDeviceDetailSettings.this.mGridView == null) {
                Log.e("MiuiMyDeviceDetail", "Presenter or RootView is null");
                return;
            }
            int i = message.what;
            if (i != 0) {
                if (i != 1) {
                    return;
                }
                MiuiMyDeviceDetailSettings.this.mPresenter.updateCameraInfo((String) message.obj);
                return;
            }
            Bundle data = message.getData();
            String string = data.getString("result");
            boolean z = data.getBoolean("needUpdateCpu");
            MiuiMyDeviceDetailSettings.this.mPresenter.showBasicInfoGridView(MiuiMyDeviceDetailSettings.this.mGridView, string, true, MiuiMyDeviceDetailSettings.this.mClickListener);
            if (z) {
                MiuiMyDeviceDetailSettings.this.startUpdateInfoAsync();
            }
        }
    };

    /* loaded from: classes.dex */
    public static class ReadCpuInfoTask extends AsyncTask<Void, Void, Void> {
        private String mCpuInfo;
        private WeakReference<MiuiMyDeviceDetailSettings> mOuterRef;
        private MiuiDeviceCpuInfoController$CallBack<String> mPreCpuInfo;

        public ReadCpuInfoTask(MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings, MiuiDeviceCpuInfoController$CallBack miuiDeviceCpuInfoController$CallBack) {
            this.mOuterRef = new WeakReference<>(miuiMyDeviceDetailSettings);
            this.mPreCpuInfo = miuiDeviceCpuInfoController$CallBack;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Void... voidArr) {
            Context context;
            String str;
            Log.i("MiuiMyDeviceDetail", "begin getCpu");
            this.mCpuInfo = MiuiAboutPhoneUtils.getInstance(AppGlobals.getInitialApplication()).getCpuInfo();
            if (this.mOuterRef.get() != null && miui.os.Build.IS_INTERNATIONAL_BUILD && (context = this.mOuterRef.get().getContext()) != null) {
                List cloudDataList = MiuiSettings.SettingsCloudData.getCloudDataList(context.getContentResolver(), "CpuParameters");
                if (cloudDataList != null && !cloudDataList.isEmpty()) {
                    Iterator it = cloudDataList.iterator();
                    while (it.hasNext()) {
                        str = ((MiuiSettings.SettingsCloudData.CloudData) it.next()).getString(MiuiMyDeviceDetailSettings.CPU_CLOUD_CONFIG_KEY, (String) null);
                        if (str != null) {
                            break;
                        }
                    }
                }
                str = null;
                if (!TextUtils.isEmpty(str)) {
                    ParseMiShopDataUtils.setCpuInfo(str);
                }
            }
            return null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void r2) {
            if (this.mOuterRef.get() != null) {
                Log.i("MiuiMyDeviceDetail", "getCpu success:" + this.mCpuInfo);
                this.mPreCpuInfo.onRequestComplete(this.mCpuInfo);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class RemoteServiceConn implements ServiceConnection {
        private RemoteServiceConn() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MiuiMyDeviceDetailSettings.this.mRemoteService = IRemoteGetDeviceInfoService.Stub.asInterface(iBinder);
            FragmentActivity activity = MiuiMyDeviceDetailSettings.this.getActivity();
            if (activity == null) {
                return;
            }
            MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings = MiuiMyDeviceDetailSettings.this;
            miuiMyDeviceDetailSettings.mHelper = new DeviceParamsInitHelper(activity, miuiMyDeviceDetailSettings.mRemoteService);
            try {
                MiuiMyDeviceDetailSettings.this.mRemoteService.registerCallback(MiuiMyDeviceDetailSettings.this.mDeviceInfoCallback);
                MiuiMyDeviceDetailSettings.this.mHelper.initDeviceParams(true);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            Log.w("MiuiMyDeviceDetail", "onServiceDisconnected");
        }
    }

    /* loaded from: classes.dex */
    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        public SpaceItemDecoration() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            if (recyclerView.getChildAdapterPosition(view) != state.getItemCount() - 1) {
                rect.bottom = MiuiMyDeviceDetailSettings.this.mContext.getResources().getDimensionPixelSize(R.dimen.card_item_bottom);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpdateCpuCallBack implements MiuiDeviceCpuInfoController$CallBack<String> {
        private WeakReference<MiuiMyDeviceDetailSettings> mFragmentRef;

        public UpdateCpuCallBack(MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings) {
            this.mFragmentRef = new WeakReference<>(miuiMyDeviceDetailSettings);
        }

        @Override // com.android.settings.device.controller.MiuiDeviceCpuInfoController$CallBack
        public void onRequestComplete(String str) {
            DeviceCardInfo cardByIndex;
            MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings = this.mFragmentRef.get();
            if (miuiMyDeviceDetailSettings == null || miuiMyDeviceDetailSettings.getActivity() == null || miuiMyDeviceDetailSettings.mPresenter == null) {
                return;
            }
            if (miuiMyDeviceDetailSettings.mPresenter.isCardsInitComplete()) {
                if (TextUtils.isEmpty(str) || !miuiMyDeviceDetailSettings.mIsNeedUpdateCpu || (cardByIndex = miuiMyDeviceDetailSettings.mPresenter.getCardByIndex(0)) == null) {
                    return;
                }
                String cpuInfo = ParseMiShopDataUtils.getCpuInfo();
                if (!TextUtils.isEmpty(cpuInfo)) {
                    cardByIndex.setValue(cpuInfo + "\n" + str);
                } else if (!TextUtils.isEmpty(cardByIndex.getValue()) && cardByIndex.getValue().split("\n").length < 2) {
                    cardByIndex.setValue(cardByIndex.getValue() + "\n" + str);
                }
                miuiMyDeviceDetailSettings.mPresenter.updateCardByIndex(0, cardByIndex);
                miuiMyDeviceDetailSettings.mIsNeedUpdateCpu = false;
                return;
            }
            DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
            String cpuInfo2 = ParseMiShopDataUtils.getCpuInfo();
            if (miuiMyDeviceDetailSettings.mIsNeedAddBasicCpu) {
                if (TextUtils.isEmpty(cpuInfo2) && TextUtils.isEmpty(str)) {
                    return;
                }
                if (TextUtils.isEmpty(cpuInfo2)) {
                    deviceCardInfo.setValue(str.trim());
                } else if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
                    deviceCardInfo.setValue(cpuInfo2 + "\n" + str.trim());
                } else {
                    deviceCardInfo.setValue(cpuInfo2 + " " + str.trim());
                }
                deviceCardInfo.setTitle(miuiMyDeviceDetailSettings.getActivity().getResources().getString(R.string.device_cpu));
                deviceCardInfo.setType(1);
                deviceCardInfo.setIconResId(DeviceBasicInfoPresenter.ICON_MAP.get(0).intValue());
                deviceCardInfo.setListener(miuiMyDeviceDetailSettings.mClickListener);
                deviceCardInfo.setKey("cpu_item");
                miuiMyDeviceDetailSettings.mPresenter.addBasicInfoCard(deviceCardInfo);
                miuiMyDeviceDetailSettings.mIsNeedAddBasicCpu = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class UpdateInfoCallback extends IRequestCallback.Stub {
        private WeakReference<MiuiMyDeviceDetailSettings> mFragmentRef;
        private boolean mIsInitDeviceUseModel = true;
        private boolean mIsInitCameraUseModel = true;

        public UpdateInfoCallback(MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings) {
            this.mFragmentRef = new WeakReference<>(miuiMyDeviceDetailSettings);
        }

        @Override // com.android.settings.aidl.IRequestCallback
        public void onRequestComplete(int i, String str) {
            MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings = this.mFragmentRef.get();
            if (miuiMyDeviceDetailSettings == null || miuiMyDeviceDetailSettings.getActivity() == null) {
                return;
            }
            Message obtain = Message.obtain();
            DeviceParamsInitHelper initHelper = miuiMyDeviceDetailSettings.getInitHelper();
            Handler handler = miuiMyDeviceDetailSettings.getHandler();
            if (initHelper == null || handler == null) {
                Log.e("MiuiMyDeviceDetail", "deal response error");
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
                Bundle bundle = new Bundle();
                bundle.putString("result", str);
                bundle.putBoolean("needUpdateCpu", this.mIsInitDeviceUseModel);
                obtain.setData(bundle);
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

    /* loaded from: classes.dex */
    private static class UpdateMemoryCallBack implements MemoryInfoHelper.Callback {
        private WeakReference<MiuiMyDeviceDetailSettings> mOuterRef;

        public UpdateMemoryCallBack(MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings) {
            this.mOuterRef = new WeakReference<>(miuiMyDeviceDetailSettings);
        }

        @Override // com.android.settings.device.MemoryInfoHelper.Callback
        public void handleTaskResult(long j) {
            FragmentActivity activity;
            MiuiMyDeviceDetailSettings miuiMyDeviceDetailSettings = this.mOuterRef.get();
            if (miuiMyDeviceDetailSettings == null || (activity = miuiMyDeviceDetailSettings.getActivity()) == null) {
                return;
            }
            miuiMyDeviceDetailSettings.mMemoryCardItem.setValue((activity.getResources().getString(R.string.device_available_memory) + " " + MiuiUtils.formatSize(activity, j)) + " / " + (activity.getResources().getString(R.string.device_total_memory) + " " + MiuiAboutPhoneUtils.getInstance(activity).getTotalMemory()));
        }
    }

    /* loaded from: classes.dex */
    public class VerisonSpaceItemDecoration extends RecyclerView.ItemDecoration {
        public VerisonSpaceItemDecoration() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            rect.bottom = MiuiMyDeviceDetailSettings.this.mContext.getResources().getDimensionPixelSize(R.dimen.card_margin_top);
            boolean z = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
            if (recyclerView.getChildLayoutPosition(view) == 1) {
                if (z) {
                    rect.left = 18;
                } else {
                    rect.right = 18;
                }
            }
            if (recyclerView.getChildLayoutPosition(view) == 2) {
                if (z) {
                    rect.right = 18;
                } else {
                    rect.left = 18;
                }
            }
        }
    }

    private void disableRecyclerViewScrollDispatch() {
        View view = this.mRootView;
        if (view == null) {
            return;
        }
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.description_grid);
        RecyclerView recyclerView2 = (RecyclerView) this.mRootView.findViewById(R.id.verison_info);
        RecyclerView recyclerView3 = (RecyclerView) this.mRootView.findViewById(R.id.hardware_info_list);
        if (recyclerView != null) {
            recyclerView.setNestedScrollingEnabled(false);
        }
        if (recyclerView2 != null) {
            recyclerView2.setNestedScrollingEnabled(false);
        }
        if (recyclerView3 != null) {
            recyclerView3.setNestedScrollingEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Handler getHandler() {
        return this.mHandler;
    }

    private void initGridView() {
        Parcelable[] parcelableArray;
        View findViewById = this.mRootView.findViewById(R.id.device_params);
        this.mGridView = findViewById;
        findViewById.setVisibility(8);
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            DeviceBasicInfoPresenter deviceBasicInfoPresenter = new DeviceBasicInfoPresenter(this.mContext);
            this.mPresenter = deviceBasicInfoPresenter;
            deviceBasicInfoPresenter.showBasicInfoGridView(this.mGridView, "", true, this.mClickListener);
            startUpdateInfoAsync();
            return;
        }
        Bundle bundleExtra = getIntent().getBundleExtra(":settings:show_fragment_args");
        DeviceCardInfo[] deviceCardInfoArr = (bundleExtra == null || bundleExtra.getParcelableArray("cards_data") == null || (parcelableArray = bundleExtra.getParcelableArray("cards_data")) == null) ? null : (DeviceCardInfo[]) Arrays.copyOf(parcelableArray, parcelableArray.length, DeviceCardInfo[].class);
        if (deviceCardInfoArr == null || deviceCardInfoArr.length <= 0) {
            this.mPresenter = new DeviceBasicInfoPresenter(this.mContext);
            this.mDeviceInfoCallback = new UpdateInfoCallback(this);
            RemoteServiceConn remoteServiceConn = new RemoteServiceConn();
            this.mRemoteServiceConn = remoteServiceConn;
            RemoteServiceUtil.bindRemoteService(this.mContext, remoteServiceConn);
            return;
        }
        for (DeviceCardInfo deviceCardInfo : deviceCardInfoArr) {
            deviceCardInfo.setListener(this.mClickListener);
        }
        DeviceBasicInfoPresenter deviceBasicInfoPresenter2 = new DeviceBasicInfoPresenter(this.mContext, deviceCardInfoArr);
        this.mPresenter = deviceBasicInfoPresenter2;
        deviceBasicInfoPresenter2.showBasicInfoGridView(this.mGridView);
        startUpdateInfoAsync();
    }

    private void initHardWareVersion() {
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
            deviceCardInfo.setTitle(this.mContext.getResources().getString(R.string.model_name));
            deviceCardInfo.setValue(MiuiCredentialsUpdater.getGlobalCertNumber().toUpperCase());
            this.mHardwareList.add(deviceCardInfo);
        } else if (!TextUtils.isEmpty(MiuiAboutPhoneUtils.getCTANumble())) {
            DeviceCardInfo deviceCardInfo2 = new DeviceCardInfo();
            deviceCardInfo2.setTitle(this.mContext.getResources().getString(R.string.model_name));
            deviceCardInfo2.setValue(MiuiAboutPhoneUtils.getCTANumble());
            this.mHardwareList.add(deviceCardInfo2);
        }
        if (!Utils.isWifiOnly(this.mContext)) {
            DeviceCardInfo deviceCardInfo3 = new DeviceCardInfo();
            deviceCardInfo3.setValue(PlatformUtils.getTelephonyProperty("gsm.version.baseband", 0, this.mContext.getResources().getString(R.string.device_info_default)));
            deviceCardInfo3.setTitle(this.mContext.getResources().getString(R.string.baseband_version));
            this.mHardwareList.add(deviceCardInfo3);
        }
        DeviceCardInfo deviceCardInfo4 = new DeviceCardInfo();
        deviceCardInfo4.setValue(MiuiAboutPhoneUtils.getFormattedKernelVersion());
        deviceCardInfo4.setTitle(this.mContext.getResources().getString(R.string.kernel_version));
        deviceCardInfo4.setKey("kernel_version");
        this.mHardwareList.add(deviceCardInfo4);
        String str = SystemProperties.get("ro.miui.cust_hardware", "");
        if (!TextUtils.isEmpty(str)) {
            DeviceCardInfo deviceCardInfo5 = new DeviceCardInfo();
            deviceCardInfo5.setTitle(this.mContext.getResources().getString(R.string.hardware_version));
            deviceCardInfo5.setValue(str);
            this.mHardwareList.add(deviceCardInfo5);
        }
        for (DeviceCardInfo deviceCardInfo6 : this.mHardwareList) {
            deviceCardInfo6.setListener(this.mClickListener);
            deviceCardInfo6.setType(3);
        }
        DeviceInfoAdapter deviceInfoAdapter = new DeviceInfoAdapter(this.mContext);
        deviceInfoAdapter.closeValueTextLineLimit();
        deviceInfoAdapter.setType(1);
        RecyclerView recyclerView = (RecyclerView) this.mRootView.findViewById(R.id.hardware_info_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
        linearLayoutManager.setOrientation(1);
        List<DeviceCardInfo> list = this.mHardwareList;
        deviceInfoAdapter.setDataList((DeviceCardInfo[]) list.toArray(new DeviceCardInfo[list.size()]));
        recyclerView.setAdapter(deviceInfoAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (recyclerView.getItemDecorationCount() <= 0 || recyclerView.getItemDecorationAt(0) == null) {
            recyclerView.addItemDecoration(new SpaceItemDecoration());
        }
    }

    private void initMemoryInfo() {
        this.mMemoryCardItem = (BorderedBaseDeviceCardItem) this.mRootView.findViewById(R.id.memory);
        this.mMemoryCardItem.setValue(this.mContext.getResources().getString(R.string.device_total_memory) + MiuiAboutPhoneUtils.getInstance(this.mContext).getTotalMemory());
        this.mMemoryCardItem.setTitle(this.mContext.getResources().getString(R.string.device_internal_memory));
        this.mMemoryCardItem.setKey("device_internal_memory");
        Folme.useAt(this.mMemoryCardItem).touch().handleTouchOf(this.mMemoryCardItem, new AnimConfig[0]);
        this.mMemoryCardItem.setOnClickListener(this.mClickListener);
    }

    private void initSoftWareVersion() {
        DeviceCardInfo deviceCardInfo = new DeviceCardInfo();
        DeviceCardInfo deviceCardInfo2 = new DeviceCardInfo();
        DeviceCardInfo deviceCardInfo3 = new DeviceCardInfo();
        if (miui.os.Build.IS_CU_CUSTOMIZATION_TEST || !miui.os.Build.IS_INTERNATIONAL_BUILD) {
            deviceCardInfo.setValue(Build.VERSION.RELEASE);
        } else {
            deviceCardInfo.setValue(Build.VERSION.RELEASE + " " + Build.ID);
        }
        deviceCardInfo.setTitle(this.mContext.getResources().getString(R.string.firmware_version));
        deviceCardInfo.setType(1);
        deviceCardInfo.setKey("firmware_version");
        deviceCardInfo2.setTitle(this.mContext.getResources().getString(R.string.security_patch));
        deviceCardInfo2.setValue(Build.VERSION.SECURITY_PATCH);
        deviceCardInfo2.setType(2);
        deviceCardInfo2.setKey("Android security patch");
        deviceCardInfo3.setTitle(this.mContext.getResources().getString(R.string.device_miui_version));
        deviceCardInfo3.setValue(MiuiAboutPhoneUtils.getMiuiVersionInCard(this.mContext, false, true));
        deviceCardInfo3.setType(2);
        deviceCardInfo3.setKey("miui_version");
        this.mVersionlist.add(deviceCardInfo);
        this.mVersionlist.add(deviceCardInfo2);
        this.mVersionlist.add(deviceCardInfo3);
        if (SettingsFeatures.IS_NEED_OPCUST_VERSION) {
            DeviceCardInfo deviceCardInfo4 = new DeviceCardInfo();
            deviceCardInfo4.setTitle(this.mContext.getResources().getString(R.string.device_opcust_version));
            String str = "";
            String str2 = SystemProperties.get("ro.miui.opcust.version", "");
            String opconfigVersion = MiuiAboutPhoneUtils.getOpconfigVersion();
            StringBuilder sb = new StringBuilder();
            sb.append(str2);
            if (opconfigVersion != null) {
                str = "\n" + opconfigVersion;
            }
            sb.append(str);
            deviceCardInfo4.setValue(sb.toString());
            deviceCardInfo4.setType(1);
            deviceCardInfo4.setKey("device_opcust_version");
            this.mVersionlist.add(deviceCardInfo4);
        }
        Iterator<DeviceCardInfo> it = this.mVersionlist.iterator();
        while (it.hasNext()) {
            it.next().setListener(this.mClickListener);
        }
        RecyclerView recyclerView = (RecyclerView) this.mRootView.findViewById(R.id.verison_info);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.mContext, 2);
        gridLayoutManager.setOrientation(1);
        DeviceInfoAdapter deviceInfoAdapter = new DeviceInfoAdapter(this.mContext);
        deviceInfoAdapter.setType(1);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { // from class: com.android.settings.device.MiuiMyDeviceDetailSettings.2
            @Override // androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
            public int getSpanSize(int i) {
                return ((DeviceCardInfo) MiuiMyDeviceDetailSettings.this.mVersionlist.get(i)).getType() == 1 ? 2 : 1;
            }
        });
        List<DeviceCardInfo> list = this.mVersionlist;
        deviceInfoAdapter.setDataList((DeviceCardInfo[]) list.toArray(new DeviceCardInfo[list.size()]));
        recyclerView.setAdapter(deviceInfoAdapter);
        if (recyclerView.getItemDecorationCount() <= 0 || recyclerView.getItemDecorationAt(0) == null) {
            recyclerView.addItemDecoration(new VerisonSpaceItemDecoration());
        }
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startUpdateInfoAsync() {
        if (this.mIsNeedUpdateCpu) {
            new ReadCpuInfoTask(this, new UpdateCpuCallBack(this)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
        MemoryInfoHelper.getAvailableMemorySize(this.mCallback);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new MiuiLegalInfoController(context));
        arrayList.add(new MiuiDeviceStatusInfoController(context));
        return arrayList;
    }

    public DeviceParamsInitHelper getInitHelper() {
        return this.mHelper;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "MiuiMyDeviceDetail";
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiMyDeviceDetailSettings.class.getName();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.my_device_detail_settings;
    }

    public void initView() {
        initGridView();
        initSoftWareVersion();
        initHardWareVersion();
        initMemoryInfo();
        disableRecyclerViewScrollDispatch();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(UserHandle.myUserId() == 0);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mClickListener = new DeviceDetailOnClickListener(activity);
        this.mCallback = new UpdateMemoryCallBack(this);
        InternationalCompat.trackReportEvent("setting_About_phone_device");
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        if (i == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setPositiveButton(17039370, (DialogInterface.OnClickListener) null);
            AlertDialog create = builder.create();
            create.setView(LayoutInflater.from(getActivity()).inflate(R.layout.type_approved_content, (ViewGroup) null));
            return create;
        }
        return super.onCreateDialog(i);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.miui_all_specs, viewGroup, false);
        this.mRootView = inflate;
        ViewGroup viewGroup2 = (ViewGroup) inflate.findViewById(R.id.prefs_container);
        viewGroup2.addView(super.onCreateView(layoutInflater, viewGroup2, bundle));
        initView();
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
        this.mVersionlist.clear();
        this.mHardwareList.clear();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if ("wifi_type_approval".equals(preference.getKey())) {
            showDialog(1);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
