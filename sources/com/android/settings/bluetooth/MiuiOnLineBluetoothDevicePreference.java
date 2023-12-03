package com.android.settings.bluetooth;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.List;
import miuix.appcompat.app.AlertDialog;
import miuix.preference.ConnectPreferenceHelper;

/* loaded from: classes.dex */
public class MiuiOnLineBluetoothDevicePreference extends Preference implements View.OnClickListener {
    private static int sDimAlpha = Integer.MIN_VALUE;
    private String contentDescription;
    public String mAccountKeyCloud;
    public String mAddress;
    private BluetoothClass mCod;
    private DeviceListPreferenceFragment mDeviceListPreferenceFragment;
    public String mDeviceName;
    private AlertDialog mDisconnectDialog;
    private ServiceMessageHandler mHandler;
    private Object mHandlerLock;
    private ConnectPreferenceHelper mHelper;
    private boolean mHideSecondTarget;
    private boolean mIsUserRestriction;
    private MiuiFastConnectV2 mMiuiFastConnectV2;
    boolean mNeedNotifyHierarchyChanged;
    private View.OnClickListener mOnSettingsClickListener;
    private List<LocalBluetoothProfile> mProfiles;
    Resources mResources;
    private final UserManager mUserManager;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ServiceMessageHandler extends Handler {
        private ServiceMessageHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            Log.d("MiuiOnLineBluetoothDevicePreference", "what= " + i);
            if (i != 3) {
                return;
            }
            MiuiOnLineBluetoothDevicePreference.this.BondFailed();
        }
    }

    public MiuiOnLineBluetoothDevicePreference(Context context, String str, String str2, String str3, BluetoothClass bluetoothClass, List<LocalBluetoothProfile> list, DeviceListPreferenceFragment deviceListPreferenceFragment) {
        super(context, (AttributeSet) null);
        this.mDeviceListPreferenceFragment = null;
        this.contentDescription = null;
        this.mHideSecondTarget = false;
        this.mNeedNotifyHierarchyChanged = false;
        this.mProfiles = null;
        this.mCod = null;
        this.mHandlerLock = new Object();
        this.mCod = bluetoothClass;
        this.mDeviceListPreferenceFragment = deviceListPreferenceFragment;
        this.mProfiles = list;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUserManager = userManager;
        init(context, str, str2, str3, userManager.hasUserRestriction("no_config_bluetooth"));
        this.mMiuiFastConnectV2 = MiuiFastConnectV2.make(context, this);
        this.mHandler = new ServiceMessageHandler(Looper.getMainLooper());
    }

    private void askRemoveInAccount() {
        Intent intent = new Intent();
        if (Log.isLoggable("HeadsetPluginDefault", 2)) {
            intent.setClassName("com.android.settings", "com.android.settings.bluetooth.MiuiHeadsetActivityPlugin");
        } else {
            intent.setClassName("com.android.settings", "com.android.settings.bluetooth.MiuiHeadsetActivity");
        }
        intent.putExtra("COME_FROM", "MIUI_HEADSET_VIRTUAL_DEVICE_INFO");
        intent.putExtra("VIRTUAL_DEVICE_ADDRESS", this.mAddress);
        intent.putExtra("VIRTUAL_DEVICE_NAME", this.mDeviceName);
        Bundle bundle = new Bundle();
        bundle.putString("devicename", this.mDeviceName);
        bundle.putString("devicemac", this.mAddress);
        intent.putExtras(bundle);
        this.mDeviceListPreferenceFragment.getActivity().startActivityForResult(intent, 0);
    }

    public void BondFailed() {
        Log.d("MiuiOnLineBluetoothDevicePreference", "bonded failed");
        setConnectState(0);
        setSummary(this.mResources.getString(R.string.headset_unsaved_devices));
        setEnabled(true);
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof MiuiOnLineBluetoothDevicePreference)) {
            return false;
        }
        return this.mAddress.equals(((MiuiOnLineBluetoothDevicePreference) obj).mAddress);
    }

    protected Pair<Drawable, String> getBtClassDrawableWithDescription(BluetoothClass bluetoothClass, List<LocalBluetoothProfile> list) {
        return BluetoothUtils.getBtClassDrawableWithDescription(getContext(), bluetoothClass, list);
    }

    public int hashCode() {
        return this.mAddress.hashCode();
    }

    public void hideSecondTarget(boolean z) {
        this.mHideSecondTarget = z;
    }

    public void init(Context context, String str, String str2, String str3, boolean z) {
        this.mResources = getContext().getResources();
        this.mAccountKeyCloud = str3;
        this.mIsUserRestriction = z;
        if (sDimAlpha == Integer.MIN_VALUE) {
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16842803, typedValue, true);
            sDimAlpha = (int) (typedValue.getFloat() * 255.0f);
        }
        this.mAddress = str;
        this.mDeviceName = str2;
        setWidgetLayoutResource(R.layout.miuix_preference_connect_widget_layout);
        setLayoutResource(R.layout.preference_bt_icon_corner);
        this.mHelper = new ConnectPreferenceHelper(context, this);
        setConnectState(0);
        setOrder(1);
        onDeviceAttributesChanged();
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        ConnectPreferenceHelper connectPreferenceHelper;
        super.onBindViewHolder(preferenceViewHolder);
        if (findPreferenceInHierarchy("bt_checkbox") != null) {
            setDependency("bt_checkbox");
        }
        View view = preferenceViewHolder.itemView;
        ImageView imageView = (ImageView) view.findViewById(R.id.preference_detail);
        if (imageView != null) {
            imageView.setOnClickListener(this);
            imageView.setTag(this.mAddress);
        }
        ImageView imageView2 = (ImageView) view.findViewById(16908294);
        if (imageView2 != null) {
            imageView2.setContentDescription(this.contentDescription);
            imageView2.setImportantForAccessibility(2);
            imageView2.setElevation(getContext().getResources().getDimension(R.dimen.bt_icon_elevation));
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        View findViewById = view.findViewById(R.id.view_corner);
        View findViewById2 = view.findViewById(R.id.view_high_light_root);
        if (findViewById == null || findViewById2 == null) {
            view.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_start), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.miuix_preference_item_padding_end), 0);
        } else {
            layoutParams.setMargins(0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_top), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_bottom));
            findViewById.setLayoutParams(layoutParams);
            findViewById2.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_padding_start), 0, 0, 0);
            view.setPaddingRelative(getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_start), 0, getContext().getResources().getDimensionPixelOffset(R.dimen.preference_bt_custom_margin_end), 0);
        }
        preferenceViewHolder.setIsRecyclable(false);
        if (findViewById2 == null || (connectPreferenceHelper = this.mHelper) == null) {
            return;
        }
        connectPreferenceHelper.setIconAnimEnabled(true);
        this.mHelper.onBindViewHolder(preferenceViewHolder, findViewById2);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        askRemoveInAccount();
        View.OnClickListener onClickListener = this.mOnSettingsClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onClicked() {
        setSummary(this.mResources.getString(R.string.headset_connectiong));
        setEnabled(false);
        setConnectState(2);
        MiuiFastConnectV2 miuiFastConnectV2 = this.mMiuiFastConnectV2;
        if (miuiFastConnectV2 != null) {
            miuiFastConnectV2.startPair(this.mAccountKeyCloud);
        }
    }

    public void onDeviceAttributesChanged() {
        setTitle(this.mDeviceName);
        setSummary(this.mResources.getString(R.string.headset_unsaved_devices));
        Pair<Drawable, String> btClassDrawableWithDescription = getBtClassDrawableWithDescription(this.mCod, this.mProfiles);
        Object obj = btClassDrawableWithDescription.first;
        if (obj != null) {
            setIcon(((Drawable) obj).mutate());
            this.contentDescription = (String) btClassDrawableWithDescription.second;
        }
        setOrder(0);
        setEnabled(true);
        setVisible(true);
        if (this.mNeedNotifyHierarchyChanged) {
            notifyHierarchyChanged();
        }
        Log.d("MiuiOnLineBluetoothDevicePreference", "device: " + this.mDeviceName + " onDeviceAttributesChanged()");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        AlertDialog alertDialog = this.mDisconnectDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mDisconnectDialog = null;
        }
        MiuiFastConnectV2 miuiFastConnectV2 = this.mMiuiFastConnectV2;
        if (miuiFastConnectV2 != null) {
            miuiFastConnectV2.cleanup();
        }
        if (this.mHandler.hasMessages(3)) {
            this.mHandler.removeMessages(3);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void rebind() {
        onDeviceAttributesChanged();
    }

    public boolean sendMessageDelay(int i, long j) {
        synchronized (this.mHandlerLock) {
            ServiceMessageHandler serviceMessageHandler = this.mHandler;
            if (serviceMessageHandler == null) {
                Log.e("MiuiOnLineBluetoothDevicePreference", "sendMessageDelay handler null");
                return false;
            }
            if (serviceMessageHandler.hasMessages(i)) {
                this.mHandler.removeMessages(i);
            }
            if (j >= 0) {
                this.mHandler.sendEmptyMessageDelayed(i, j);
            }
            return true;
        }
    }

    public void setConnectState(int i) {
        this.mHelper.setConnectState(i);
    }
}
