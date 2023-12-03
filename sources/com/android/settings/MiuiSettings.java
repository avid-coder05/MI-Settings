package com.android.settings;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.ActivityClient;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.miui.AppOpsUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Vibrator;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MiuiWindowManager$LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.util.ArrayUtils;
import com.android.settings.Settings;
import com.android.settings.accounts.XiaomiAccountInfoController;
import com.android.settings.accounts.XiaomiAccountStatusController;
import com.android.settings.accounts.XiaomiAccountUtils;
import com.android.settings.applications.SystemAppUpdaterStatusController;
import com.android.settings.bluetooth.BluetoothStatusController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.cust.MiHomeManager;
import com.android.settings.dangerousoptions.DangerousOptionsUtil;
import com.android.settings.device.DeviceStatusController;
import com.android.settings.device.MiuiAboutPhoneUtils;
import com.android.settings.display.FontStatusController;
import com.android.settings.notify.SettingsNotifyHelper;
import com.android.settings.personal.FullScreenDisplayController;
import com.android.settings.report.InternationalCompat;
import com.android.settings.restriction.SimManagementRestrictionController;
import com.android.settings.restriction.TetherRestrictionController;
import com.android.settings.usagestats.utils.CommonUtils;
import com.android.settings.utils.HomeListUtils;
import com.android.settings.utils.SettingsFeatures;
import com.android.settings.utils.TabletUtils;
import com.android.settings.vpn2.MiuiVpnUtils;
import com.android.settings.vpn2.VpnManager;
import com.android.settings.wifi.WifiStatusController;
import com.android.settingslib.OldmanHelper;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.drawer.UserAdapter;
import com.android.settingslib.miuisettings.preference.PreferenceActivity;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import com.miui.enterprise.RestrictionsHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.provider.Notes;
import miui.settings.commonlib.MemoryOptimizationUtil;
import miui.settings.splitlib.SplitUtils;
import miuix.animation.Folme;
import miuix.animation.ITouchStyle;
import miuix.animation.base.AnimConfig;
import miuix.appcompat.app.ActionBar;
import miuix.appcompat.app.AlertDialog;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes.dex */
public class MiuiSettings extends AppCompatActivity implements XiaomiAccountUtils.UpdateAccountListener {
    private static final String TAG = "MiuiSettings";
    private AuthenticatorHelper mAuthenticatorHelper;
    protected ViewGroup mContent;
    private boolean mLastDisallowConfigTetherStatus;
    private MemoryOptimizationUtil mMemoryOptimizationUtil;
    private MiuiCustSplitUtils mMiuiCustSplitUtils;
    private SettingsFragment mSettingsFragment;
    private VpnManager mVpnManager;
    private XiaomiAccountUtils mXiaomiAccountUtils;
    private SplitPageRunnable splitPageRunnable;
    private static final int SELECTOR_COLOR = Color.parseColor("#0D0D84FF");
    private static final Map<String, Integer> CATEGORY_MAP = new HashMap<String, Integer>() { // from class: com.android.settings.MiuiSettings.1
        {
            int i = R.drawable.ic_google_settings;
            put("com.android.settings.category.wireless", Integer.valueOf(i));
            put("com.android.settings.category.device", Integer.valueOf(i));
            put("com.android.settings.category.personal", Integer.valueOf(i));
            put("com.android.settings.category.system", Integer.valueOf(i));
        }
    };
    private int[] MIUI_SETTINGS_FOR_RESTRICTED = {R.id.msim_settings, R.id.operator_settings, R.id.wifi_tether_settings, R.id.font_settings, R.id.vpn_settings};
    private String mSelectHeaderFragment = null;
    private int mCurrentSelectedHeaderIndex = -1;
    private int mLastVpnConfiguredStatus = -1;
    private int mWifiTetherStatus = 0;
    private boolean mIsXoptMode = false;
    private View mSelectedView = null;
    private CharSequence mFeedbackLabel = null;
    private boolean shouldDisableAppTimer = false;
    private int mNormalIconSize = 0;
    private int mAccountIconSize = 0;
    private String mCurrentLanguage = "";
    private boolean mLanguageChange = false;

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public class HeaderAdapter extends RecyclerView.Adapter<HeaderViewHolder> {
        private boolean isNightMode;
        private AuthenticatorHelper mAuthHelper;
        private Context mContext;
        private List<PreferenceActivity.Header> mHeaders;
        private LayoutInflater mInflater;
        private boolean mIsFrequently;
        private boolean mIsMIUILite;
        private Locale mLocale;
        private MiHomeManager mMiHomeManager;
        private HashMap<Long, BaseSettingsController> mSettingsControllerMap;
        private UiModeManager mUiManager;

        public HeaderAdapter(AppCompatActivity appCompatActivity, List<PreferenceActivity.Header> list, AuthenticatorHelper authenticatorHelper, boolean z) {
            this.mContext = appCompatActivity.getApplicationContext();
            this.mHeaders = list;
            this.mAuthHelper = authenticatorHelper;
            this.mInflater = (LayoutInflater) appCompatActivity.getSystemService("layout_inflater");
            this.mIsFrequently = z;
            this.mMiHomeManager = MiHomeManager.getInstance(this.mContext);
            this.mLocale = appCompatActivity.getResources().getConfiguration().locale;
            HashMap<Long, BaseSettingsController> hashMap = new HashMap<>();
            this.mSettingsControllerMap = hashMap;
            hashMap.put(Long.valueOf(R.id.wifi_settings), new WifiStatusController(appCompatActivity, null));
            this.mSettingsControllerMap.put(Long.valueOf(R.id.bluetooth_settings), new BluetoothStatusController(appCompatActivity, null));
            this.mSettingsControllerMap.put(Long.valueOf(R.id.wifi_tether_settings), new TetherRestrictionController(appCompatActivity, null));
            if (Build.IS_INTERNATIONAL_BUILD) {
                this.mSettingsControllerMap.put(Long.valueOf(R.id.micloud_settings), new XiaomiAccountStatusController(appCompatActivity, null));
            } else {
                this.mSettingsControllerMap.put(Long.valueOf(R.id.mi_account_settings), new XiaomiAccountInfoController(appCompatActivity, null));
            }
            this.mSettingsControllerMap.put(Long.valueOf(R.id.font_settings), new FontStatusController(appCompatActivity, null));
            this.mSettingsControllerMap.put(Long.valueOf(R.id.my_device), new DeviceStatusController(appCompatActivity, null));
            this.mSettingsControllerMap.put(Long.valueOf(R.id.system_apps_updater), new SystemAppUpdaterStatusController(appCompatActivity, null, this.mLocale));
            this.mSettingsControllerMap.put(Long.valueOf(R.id.msim_settings), new SimManagementRestrictionController(appCompatActivity, null));
            UiModeManager uiModeManager = (UiModeManager) this.mContext.getSystemService("uimode");
            this.mUiManager = uiModeManager;
            this.isNightMode = uiModeManager != null && uiModeManager.getNightMode() == 2;
            this.mIsMIUILite = MiuiAboutPhoneUtils.getInstance(this.mContext).isMIUILite();
        }

        private int getHeaderType(PreferenceActivity.Header header) {
            if (header.fragment == null && header.intent == null) {
                return 0;
            }
            long j = header.id;
            if (j == R.id.my_device) {
                return 2;
            }
            if (isWirelessHeader(j)) {
                return 3;
            }
            return header.id == ((long) R.id.system_apps_updater) ? 5 : 1;
        }

        private void setEnable(HeaderViewHolder headerViewHolder, PreferenceActivity.Header header) {
            if (headerViewHolder == null) {
                return;
            }
            long j = header.id;
            if (j == R.id.my_device || j == R.id.mi_account_settings) {
                headerViewHolder.summary.setEnabled(false);
                return;
            }
            int i = R.id.msim_settings;
            if (j == i) {
                return;
            }
            if (j == i || j == R.id.mobile_network_settings) {
                headerViewHolder.title.setEnabled(true);
                headerViewHolder.summary.setEnabled(true);
                return;
            }
            TextView textView = headerViewHolder.title;
            if (textView != null) {
                textView.setEnabled(true);
            }
            TextView textView2 = headerViewHolder.summary;
            if (textView2 != null) {
                textView2.setEnabled(true);
            }
        }

        private void setRestrictionEnforced(HeaderViewHolder headerViewHolder, boolean z) {
            if (headerViewHolder == null) {
                return;
            }
            TextView textView = headerViewHolder.value;
            if (textView != null) {
                textView.setTextColor(textView.getTextColors().withAlpha(z ? 77 : 255));
            }
            TextView textView2 = headerViewHolder.title;
            if (textView2 != null) {
                textView2.setTextColor(textView2.getTextColors().withAlpha(z ? 77 : 255));
            }
            ImageView imageView = headerViewHolder.icon;
            if (imageView != null) {
                imageView.setImageAlpha(z ? 77 : 255);
            }
        }

        private void setSelectedHeaderView(HeaderViewHolder headerViewHolder, int i) {
            if (headerViewHolder != null) {
                if (TabletUtils.IS_TABLET || SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                    if (MiuiSettings.this.mCurrentSelectedHeaderIndex != i) {
                        headerViewHolder.itemView.setSelected(false);
                        return;
                    }
                    setSelectorColor(0);
                    setSelectedView(headerViewHolder.itemView);
                    setSelectorColor(MiuiSettings.SELECTOR_COLOR);
                }
            }
        }

        private void setSelectedView(View view) {
            if (MiuiSettings.this.mSelectedView != null) {
                MiuiSettings.this.mSelectedView.setSelected(false);
            }
            if (view != null) {
                MiuiSettings.this.mSelectedView = view;
            }
        }

        private void setSelectorColor(int i) {
            if (MiuiSettings.this.mSelectedView != null) {
                MiuiSettings.this.mSelectedView.setSelected(true);
            }
        }

        private void updateAdminDisallowItem(int i, boolean z) {
            Bundle bundle = this.mHeaders.get(i).extras;
            if (bundle == null) {
                bundle = new Bundle();
            }
            this.mHeaders.get(i).extras = bundle;
            this.mHeaders.get(i).extras.putBoolean("admin_disallow", z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateAdminDisallowedConfig(ProxyHeaderViewAdapter proxyHeaderViewAdapter) {
            boolean z = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_mobile_networks", UserHandle.myUserId()) != null;
            boolean z2 = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_tethering", UserHandle.myUserId()) != null;
            List<PreferenceActivity.Header> list = this.mHeaders;
            if (list == null || list.isEmpty() || proxyHeaderViewAdapter == null) {
                return;
            }
            for (int i = 0; i < this.mHeaders.size(); i++) {
                PreferenceActivity.Header header = this.mHeaders.get(i);
                if (header != null) {
                    long j = header.id;
                    if (j == R.id.msim_settings) {
                        updateAdminDisallowItem(i, z);
                        proxyHeaderViewAdapter.updateItem(i);
                    } else if (j == R.id.wifi_tether_settings) {
                        updateAdminDisallowItem(i, z2);
                        proxyHeaderViewAdapter.updateItem(i);
                    }
                }
            }
        }

        public PreferenceActivity.Header getItem(int i) {
            return this.mHeaders.get(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            List<PreferenceActivity.Header> list = this.mHeaders;
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return getHeaderType(getItem(i));
        }

        public boolean isWirelessHeader(long j) {
            return this.mSettingsControllerMap.keySet().contains(Long.valueOf(j));
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0021, code lost:
        
            if (r4 != 5) goto L68;
         */
        /* JADX WARN: Removed duplicated region for block: B:37:0x0160  */
        /* JADX WARN: Removed duplicated region for block: B:38:0x016b  */
        /* JADX WARN: Removed duplicated region for block: B:41:0x0173  */
        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onBindViewHolder(com.android.settings.MiuiSettings.HeaderViewHolder r17, int r18) {
            /*
                Method dump skipped, instructions count: 552
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MiuiSettings.HeaderAdapter.onBindViewHolder(com.android.settings.MiuiSettings$HeaderViewHolder, int):void");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public HeaderViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View inflate;
            if (i == 0) {
                inflate = this.mInflater.inflate(R.layout.miuix_preference_category_layout, viewGroup, false);
            } else if (i == 1 || i == 2 || i == 3 || i == 5) {
                View inflate2 = SettingsFeatures.isSplitTablet(MiuiSettings.this) ? this.mInflater.inflate(R.layout.miuix_preference_navigation_item, viewGroup, false) : this.mInflater.inflate(R.layout.miuix_preference_main_layout, viewGroup, false);
                ViewGroup viewGroup2 = (ViewGroup) inflate2.findViewById(16908312);
                if (viewGroup2 != null) {
                    LayoutInflater from = LayoutInflater.from(viewGroup.getContext());
                    if (SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                        from.inflate(R.layout.miuix_preference_widget_navigation_item_text, viewGroup2, true);
                    } else {
                        from.inflate(R.layout.miuix_preference_widget_text, viewGroup2, true);
                        ((TextView) viewGroup2.findViewById(R.id.text_right)).setMaxWidth(MiuiSettings.this.getResources().getDimensionPixelSize(R.dimen.preference_text_right_max_width));
                    }
                }
                View findViewById = inflate2.findViewById(R.id.arrow_right);
                if (findViewById != null) {
                    findViewById.setVisibility(0);
                }
                if (!SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                    Folme.useAt(inflate2).touch().setScale(1.0f, new ITouchStyle.TouchType[0]).setBackgroundColor(MiuiSettings.this.getResources().getColor(R.color.miuisettings_item_touch_color, MiuiSettings.this.getTheme())).setTintMode(1).handleTouchOf(inflate2, new AnimConfig[0]);
                }
                inflate = inflate2;
            } else {
                inflate = SettingsFeatures.isSplitTablet(MiuiSettings.this) ? this.mInflater.inflate(R.layout.miuix_preference_navigation_item, viewGroup, false) : this.mInflater.inflate(R.layout.miuix_preference_main_layout, viewGroup, false);
                ViewGroup viewGroup3 = (ViewGroup) inflate.findViewById(16908312);
                if (viewGroup3 != null) {
                    LayoutInflater from2 = LayoutInflater.from(viewGroup.getContext());
                    if (SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                        from2.inflate(R.layout.miuix_preference_widget_navigation_item_text, viewGroup3, true);
                    } else {
                        from2.inflate(R.layout.miuix_preference_widget_text, viewGroup3, true);
                        ((TextView) viewGroup3.findViewById(R.id.text_right)).setMaxWidth(MiuiSettings.this.getResources().getDimensionPixelSize(R.dimen.preference_text_right_max_width));
                    }
                }
            }
            inflate.setTag(Integer.valueOf(i));
            return new HeaderViewHolder(inflate);
        }

        public void pause() {
            Iterator<BaseSettingsController> it = this.mSettingsControllerMap.values().iterator();
            while (it.hasNext()) {
                it.next().pause();
            }
        }

        public void resume() {
            Iterator<BaseSettingsController> it = this.mSettingsControllerMap.values().iterator();
            while (it.hasNext()) {
                it.next().resume();
            }
        }

        public void setClick(final HeaderViewHolder headerViewHolder, final PreferenceActivity.Header header, final int i) {
            headerViewHolder.itemView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.MiuiSettings.HeaderAdapter.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    boolean z = Build.IS_MIPAD;
                    if (HeaderAdapter.this.mMiHomeManager.isMiHomeManagerInstalled && HeaderAdapter.this.mMiHomeManager.isForbidden(header.fragment)) {
                        Toast.makeText(HeaderAdapter.this.mContext, R.string.settings_forbidden_message, 0).show();
                        return;
                    }
                    int i2 = i;
                    if (i2 < 0 || i2 >= HeaderAdapter.this.mHeaders.size()) {
                        return;
                    }
                    if (SettingsFeatures.isSplitTablet(MiuiSettings.this) && i != MiuiSettings.this.mCurrentSelectedHeaderIndex) {
                        if (MiuiSettings.this.mSelectedView != null) {
                            MiuiSettings.this.mSelectedView.setSelected(false);
                        }
                        MiuiSettings.this.mSelectedView = headerViewHolder.itemView;
                        if (MiuiSettings.this.mSelectedView != null) {
                            MiuiSettings.this.mSelectedView.setSelected(true);
                        }
                    }
                    try {
                        MiuiSettings.this.onHeaderClick((PreferenceActivity.Header) HeaderAdapter.this.mHeaders.get(i2), i2);
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        public void setIcon(HeaderViewHolder headerViewHolder, PreferenceActivity.Header header) {
            ImageView imageView;
            if (headerViewHolder == null || (imageView = headerViewHolder.icon) == null || imageView.getVisibility() == 8) {
                return;
            }
            Bundle bundle = header.fragmentArguments;
            if (bundle != null && header.id == R.id.micloud_settings && bundle.containsKey(Notes.Account.ACCOUNT_TYPE)) {
                headerViewHolder.icon.setImageResource(R.drawable.xiaomi_account);
            } else if (header.id != R.id.mi_account_settings) {
                if (header.iconRes == 0) {
                    headerViewHolder.icon.setVisibility(4);
                    return;
                }
                headerViewHolder.icon.setVisibility(0);
                headerViewHolder.icon.setImageResource(header.iconRes);
            }
        }

        public void start() {
            Iterator<BaseSettingsController> it = this.mSettingsControllerMap.values().iterator();
            while (it.hasNext()) {
                it.next().start();
            }
        }

        public void stop() {
            Iterator<BaseSettingsController> it = this.mSettingsControllerMap.values().iterator();
            while (it.hasNext()) {
                it.next().stop();
            }
        }

        public void updateHeaderViewInfo() {
            BaseSettingsController baseSettingsController;
            HashMap<Long, BaseSettingsController> hashMap = this.mSettingsControllerMap;
            if (hashMap == null || (baseSettingsController = hashMap.get(Long.valueOf(R.id.mi_account_settings))) == null) {
                return;
            }
            baseSettingsController.updateStatus();
        }
    }

    /* loaded from: classes.dex */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView arrowRight;
        public ImageView icon;
        public TextView summary;
        public TextView title;
        public TextView value;

        HeaderViewHolder(View view) {
            super(view);
            int intValue = ((Integer) view.getTag()).intValue();
            if (intValue == 0) {
                this.title = (TextView) view.findViewById(16908310);
            } else if (intValue != 1 && intValue != 2 && intValue != 3 && intValue != 5) {
                ImageView imageView = (ImageView) view.findViewById(R.id.arrow_right);
                this.arrowRight = imageView;
                if (imageView == null || !SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                    return;
                }
                this.arrowRight.setVisibility(4);
            } else {
                ImageView imageView2 = (ImageView) view.findViewById(R.id.arrow_right);
                this.arrowRight = imageView2;
                if (imageView2 != null) {
                    if (SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                        this.arrowRight.setVisibility(4);
                    } else {
                        this.arrowRight.setVisibility(0);
                    }
                }
                this.icon = (ImageView) view.findViewById(16908294);
                this.title = (TextView) view.findViewById(16908310);
                this.summary = (TextView) view.findViewById(16908304);
                this.value = (TextView) view.findViewById(R.id.text_right);
            }
        }
    }

    /* loaded from: classes.dex */
    public class ProxyHeaderViewAdapter extends RecyclerView.Adapter {
        final RecyclerView.Adapter mBaseAdapter;
        private HashMap<Integer, View> mHeaderViews = new HashMap<>();
        private boolean isRemovableViewExist = false;

        /* loaded from: classes.dex */
        public class FixedViewHolder extends RecyclerView.ViewHolder {
            public FixedViewHolder(View view) {
                super(view);
                setIsRecyclable(false);
            }

            public void onBind() {
            }
        }

        public ProxyHeaderViewAdapter(RecyclerView.Adapter adapter) {
            this.mBaseAdapter = adapter;
            setHasStableIds(adapter.hasStableIds());
        }

        public void addDeferedSetupView(View view) {
            addHeaderView(1, 512, view);
        }

        public void addHeaderView(int i, int i2, View view) {
            if (this.mHeaderViews.containsValue(view)) {
                return;
            }
            this.mHeaderViews.put(Integer.valueOf(i2), view);
            notifyDataSetChanged();
        }

        public void addRemovableHintView(View view) {
            this.isRemovableViewExist = true;
            addHeaderView(0, 256, view);
        }

        public HeaderAdapter getBaseAdapter() {
            return (HeaderAdapter) this.mBaseAdapter;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.mHeaderViews.size() + this.mBaseAdapter.getItemCount();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            int size = i - this.mHeaderViews.size();
            if (size < 0 || size >= this.mBaseAdapter.getItemCount()) {
                return -1L;
            }
            return this.mBaseAdapter.getItemId(size);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            int size = this.mHeaderViews.size();
            if (i < size) {
                return (i == 0 && this.isRemovableViewExist) ? 256 : 512;
            }
            return this.mBaseAdapter.getItemViewType(i - size);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder instanceof FixedViewHolder) {
                ((FixedViewHolder) viewHolder).onBind();
                return;
            }
            this.mBaseAdapter.onBindViewHolder(viewHolder, i - this.mHeaderViews.size());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return i == 256 ? new FixedViewHolder(this.mHeaderViews.get(256)) : i == 512 ? new FixedViewHolder(this.mHeaderViews.get(512)) : this.mBaseAdapter.onCreateViewHolder(viewGroup, i);
        }

        public void pause() {
            ((HeaderAdapter) this.mBaseAdapter).pause();
        }

        public void removeDeferedSetupView(View view) {
            removeHeaderView(1, 512, view);
        }

        public void removeHeaderView(int i, int i2, View view) {
            if (this.mHeaderViews.containsValue(view)) {
                this.mHeaderViews.remove(Integer.valueOf(i2));
                notifyDataSetChanged();
            }
        }

        public void removeRemovableHintView(View view) {
            this.isRemovableViewExist = false;
            removeHeaderView(0, 256, view);
        }

        public void resume() {
            ((HeaderAdapter) this.mBaseAdapter).resume();
            notifyDataSetChanged();
        }

        public void start() {
            ((HeaderAdapter) this.mBaseAdapter).start();
        }

        public void stop() {
            ((HeaderAdapter) this.mBaseAdapter).stop();
        }

        public void updateHeaderViewInfo() {
            ((HeaderAdapter) this.mBaseAdapter).updateHeaderViewInfo();
        }

        public void updateItem(int i) {
            notifyItemChanged(i + this.mHeaderViews.size());
        }
    }

    /* loaded from: classes.dex */
    private static class SplitPageRunnable implements Runnable {
        private WeakReference<Activity> activityWeakReference;
        private String cloudDataString;

        public SplitPageRunnable(Activity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        private boolean isLegalApp(Context context, String str) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            return Build.IS_INTERNATIONAL_BUILD ? MiuiUtils.INTERNATIONAL_WHITE_LIST.contains(str) || isWhiteFromCloud(context, str) : MiuiUtils.DOMESTIC_WHITE_LIST.contains(str) || isWhiteFromCloud(context, str);
        }

        private boolean isWhiteFromCloud(Context context, String str) {
            if (TextUtils.isEmpty(this.cloudDataString)) {
                this.cloudDataString = MiuiSettings.SettingsCloudData.getCloudDataString(context.getContentResolver(), "SplitWhitelist", Build.IS_INTERNATIONAL_BUILD ? "international_split_white_list" : "split_white_list", "");
            }
            return !TextUtils.isEmpty(this.cloudDataString) && this.cloudDataString.contains(str);
        }

        @Override // java.lang.Runnable
        public void run() {
            Activity activity = this.activityWeakReference.get();
            if (activity == null || !SettingsFeatures.isSplitTablet(activity)) {
                return;
            }
            String reflectGetReferrer = MiuiUtils.reflectGetReferrer(activity);
            if (!isLegalApp(activity, reflectGetReferrer)) {
                Log.e(MiuiSettings.TAG, "PackageName: " + reflectGetReferrer);
                return;
            }
            Intent splitActivityIntent = SplitUtils.getSplitActivityIntent(activity.getIntent());
            if (splitActivityIntent != null && !activity.getIntent().getBooleanExtra("splitpage_started", false)) {
                activity.startActivity(splitActivityIntent);
                activity.getIntent().putExtra("splitpage_started", true);
                if (activity instanceof MiuiSettings) {
                    ((MiuiSettings) activity).disableSelectedPosition();
                }
            }
            String stringExtra = activity.getIntent().getStringExtra("SHORTCUT_ACTION");
            if (TextUtils.isEmpty(stringExtra) || activity.getIntent().getBooleanExtra("shortcut_started", false)) {
                return;
            }
            activity.startActivity(new Intent(stringExtra));
            activity.getIntent().putExtra("shortcut_started", true);
            if (activity instanceof MiuiSettings) {
                ((MiuiSettings) activity).disableSelectedPosition();
            }
        }
    }

    private void AddGoogleSettingsHeaders(List<PreferenceActivity.Header> list) {
        int i;
        ActivityInfo activityInfo;
        Bundle bundle;
        Iterator<PreferenceActivity.Header> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                i = 0;
                break;
            }
            PreferenceActivity.Header next = it.next();
            if (R.id.account_list == ((int) next.id)) {
                i = list.indexOf(next);
                break;
            }
        }
        HashMap hashMap = new HashMap();
        for (UserHandle userHandle : UserManager.get(this).getUserProfiles()) {
            if (userHandle.getIdentifier() != 999) {
                PackageManager packageManager = getPackageManager();
                for (ResolveInfo resolveInfo : packageManager.queryIntentActivitiesAsUser(new Intent("com.android.settings.action.EXTRA_SETTINGS"), 128, userHandle.getIdentifier())) {
                    if (resolveInfo.system && (bundle = (activityInfo = resolveInfo.activityInfo).metaData) != null && bundle.containsKey("com.android.settings.category")) {
                        String string = bundle.getString("com.android.settings.category");
                        Map<String, Integer> map = CATEGORY_MAP;
                        if (map.get(string) != null) {
                            Pair pair = new Pair(activityInfo.packageName, activityInfo.name);
                            PreferenceActivity.Header header = (PreferenceActivity.Header) hashMap.get(pair);
                            if (header == null) {
                                PreferenceActivity.Header header2 = new PreferenceActivity.Header();
                                header2.id = R.id.header_google_settings;
                                header2.intent = new Intent().setClassName(activityInfo.packageName, activityInfo.name);
                                header2.iconRes = map.get(string).intValue();
                                header2.title = activityInfo.loadLabel(packageManager);
                                Bundle bundle2 = new Bundle();
                                ArrayList<? extends Parcelable> arrayList = new ArrayList<>();
                                arrayList.add(userHandle);
                                bundle2.putParcelableArrayList("header_user", arrayList);
                                header2.extras = bundle2;
                                hashMap.put(pair, header2);
                                list.add(i, header2);
                            } else {
                                header.extras.getParcelableArrayList("header_user").add(userHandle);
                            }
                        }
                    }
                }
            }
        }
    }

    private void createPhoneMainFragment(Bundle bundle) {
        setContentView(R.layout.settings_main);
        int i = R.id.main_content;
        this.mContent = (ViewGroup) findViewById(i);
        if (!Build.IS_TABLET && !SettingsFeatures.isFoldDevice()) {
            this.mContent.setPadding(0, 0, 0, 0);
        }
        if (SettingsFeatures.isSplitTablet(this)) {
            splitIfNeeded(bundle);
            this.mContent.setBackgroundResource(R.drawable.pad_right_frame_line_bg);
        }
        this.mSettingsFragment = new SettingsFragment();
        getSupportFragmentManager().beginTransaction().replace(i, this.mSettingsFragment).commitAllowingStateLoss();
    }

    private ComponentName getDefaultCompName() {
        return new ComponentName(this, Settings.WifiSettingsActivity.class);
    }

    private void getFeedbackSettingsLabel() {
        int feedbackSettingsTitle;
        Context baseContext = getBaseContext();
        if (SettingsFeatures.isVipServiceNeeded(baseContext)) {
            this.mFeedbackLabel = baseContext.getResources().getText(R.string.bug_report_settings);
            return;
        }
        Context context = null;
        try {
            context = createPackageContext("com.miui.miservice", 2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context == null || (feedbackSettingsTitle = TitleManager.getFeedbackSettingsTitle(context)) == -1) {
            return;
        }
        this.mFeedbackLabel = context.getResources().getText(feedbackSettingsTitle);
    }

    private ComponentName getFirstExistMenu() {
        return getPackageManager() == null ? getDefaultCompName() : getDefaultCompName();
    }

    private MiuiCustSplitUtils getMiuiCustSplitUtils() {
        if (this.mMiuiCustSplitUtils == null) {
            this.mMiuiCustSplitUtils = new MiuiCustSplitUtilsImpl(this);
        }
        return this.mMiuiCustSplitUtils;
    }

    private void hideActionBar() {
        ActionBar appCompatActionBar = getAppCompatActionBar();
        if (appCompatActionBar != null) {
            appCompatActionBar.hide();
        }
    }

    private void initData(Bundle bundle) {
        initSplitFlag();
    }

    private void initSplitFlag() {
        if (getIntent() != null) {
            getIntent().addMiuiFlags(4);
        }
    }

    private void invalidateSelectHeader() {
        if (TabletUtils.IS_TABLET && getIntent() != null) {
            String stringExtra = getIntent().getStringExtra("com.android.settings.FRAGMENT_CLASS");
            if (TextUtils.isEmpty(stringExtra)) {
                return;
            }
            getIntent().putExtra("com.android.settings.FRAGMENT_CLASS", "");
            this.mSelectHeaderFragment = stringExtra;
            popupBackStack();
        }
    }

    private boolean isTopOfTask() {
        try {
            return ActivityClient.getInstance().isTopOfTask(getActivityToken());
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onHeaderClick$0(PreferenceActivity.Header header, ArrayList arrayList, DialogInterface dialogInterface, int i) {
        startSplitActivityAsUserIfNeed(header.intent, (UserHandle) arrayList.get(i));
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0022 A[LOOP:0: B:9:0x0020->B:10:0x0022, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:12:0x002a  */
    /* JADX WARN: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean popBackStack(com.android.settingslib.miuisettings.preference.PreferenceActivity.Header r5) {
        /*
            r4 = this;
            androidx.fragment.app.FragmentManager r4 = r4.getSupportFragmentManager()
            int r0 = r4.getBackStackEntryCount()
            r1 = 0
            r2 = 1
            if (r0 <= 0) goto L1e
            androidx.fragment.app.FragmentManager$BackStackEntry r3 = r4.getBackStackEntryAt(r1)
            java.lang.String r5 = r5.fragment
            java.lang.String r3 = r3.getName()
            boolean r5 = r5.equals(r3)
            if (r5 == 0) goto L1e
            r5 = r2
            goto L1f
        L1e:
            r5 = r1
        L1f:
            r3 = r5
        L20:
            if (r3 >= r0) goto L28
            r4.popBackStackImmediate()
            int r3 = r3 + 1
            goto L20
        L28:
            if (r5 != r2) goto L2b
            r1 = r2
        L2b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.MiuiSettings.popBackStack(com.android.settingslib.miuisettings.preference.PreferenceActivity$Header):boolean");
    }

    private void popupBackStack() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        int backStackEntryCount = supportFragmentManager.getBackStackEntryCount();
        for (int i = 0; i < backStackEntryCount; i++) {
            supportFragmentManager.popBackStackImmediate();
        }
    }

    private void rebuildViews() {
        popupBackStack();
        Bundle bundle = new Bundle();
        bundle.putString("select_header", this.mSelectHeaderFragment);
        initializeViews(bundle);
    }

    private void resetPosition(int i) {
        if (TabletUtils.IS_TABLET) {
            this.mCurrentSelectedHeaderIndex = i;
        }
    }

    private boolean restrictedDisabled(PreferenceActivity.Header header) {
        int i = (int) header.id;
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = i == R.id.wifi_tether_settings ? RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_config_tethering", UserHandle.myUserId()) : i == R.id.msim_settings ? RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this, "no_config_mobile_networks", UserHandle.myUserId()) : null;
        if (checkIfRestrictionEnforced != null) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this, checkIfRestrictionEnforced);
            return true;
        }
        return false;
    }

    private void startSplitActivityAsUserIfNeed(Intent intent, UserHandle userHandle) {
        if (SettingsFeatures.isFoldDevice()) {
            intent.addMiuiFlags(4);
        }
        startActivityAsUser(intent, userHandle);
    }

    private void startSplitActivityForResultIfNeed(Intent intent, int i) {
        if (SettingsFeatures.isFoldDevice()) {
            intent.addMiuiFlags(4);
        }
        startActivityForResult(intent, i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startSplitActivityIfNeed(Intent intent) {
        if (SettingsFeatures.isFoldDevice()) {
            intent.addMiuiFlags(4);
        }
        try {
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableSelectedPosition() {
        if (SettingsFeatures.isSplitTablet(this)) {
            int i = this.mCurrentSelectedHeaderIndex;
            this.mCurrentSelectedHeaderIndex = -2;
            this.mSettingsFragment.getHeaderAdapter().notifyItemChanged(i);
            this.mSettingsFragment.getHeaderAdapter().notifyItemChanged(this.mCurrentSelectedHeaderIndex);
        }
    }

    public AuthenticatorHelper getAuthenticatorHelper() {
        return this.mAuthenticatorHelper;
    }

    public String getSelectHeaderFragment() {
        return this.mSelectHeaderFragment;
    }

    protected void initializeViews(Bundle bundle) {
        String stringExtra = getIntent().getStringExtra(":settings:show_fragment");
        if (!TextUtils.isEmpty(stringExtra)) {
            this.mSelectHeaderFragment = stringExtra;
        }
        if (bundle != null) {
            this.mSelectHeaderFragment = bundle.getString("select_header", this.mSelectHeaderFragment);
            this.mCurrentSelectedHeaderIndex = bundle.getInt("select_header_index", this.mCurrentSelectedHeaderIndex);
        }
        createPhoneMainFragment(bundle);
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (!TabletUtils.IS_TABLET) {
            finish();
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentById = supportFragmentManager.findFragmentById(R.id.content);
        if (findFragmentById != null && (findFragmentById instanceof OnBackPressedListener) && ((OnBackPressedListener) findFragmentById).onBackPressed()) {
            return;
        }
        if (supportFragmentManager.getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else if (!this.mLanguageChange) {
            finish();
        } else {
            this.mLanguageChange = false;
            rebuildViews();
        }
    }

    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (SettingsFeatures.isAlienTablet()) {
            setRequestedOrientation(!TabletUtils.IS_TABLET ? 1 : -1);
            if (TabletUtils.IS_TABLET && !this.mCurrentLanguage.equals(configuration.locale.toString())) {
                this.mCurrentLanguage = configuration.locale.toString();
                this.mLanguageChange = true;
                this.mSelectHeaderFragment = "com.android.settings.language.MiuiLanguageAndInputSettings";
                rebuildViews();
                this.mSelectHeaderFragment = "com.android.settings.personal.OtherPersonalSettings";
                return;
            }
            if (!getSupportFragmentManager().isStateSaved()) {
                popupBackStack();
            }
            Bundle bundle = new Bundle();
            bundle.putInt("select_header_index", this.mCurrentSelectedHeaderIndex);
            bundle.putString("select_header", this.mSelectHeaderFragment);
            initializeViews(bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        if (SettingsFeatures.isAlienTablet()) {
            setRequestedOrientation(!TabletUtils.IS_TABLET ? 1 : -1);
        }
        AuthenticatorHelper authenticatorHelper = new AuthenticatorHelper(this, UserHandle.ALL, null);
        this.mAuthenticatorHelper = authenticatorHelper;
        authenticatorHelper.updateAuthDescriptions(this);
        if (SettingsFeatures.isSplitTablet(this)) {
            initData(bundle);
        }
        super.onCreate(bundle);
        this.mCurrentLanguage = Locale.getDefault().toString();
        initializeViews(bundle);
        invalidateSelectHeader();
        this.mVpnManager = new VpnManager(getApplicationContext());
        this.mMemoryOptimizationUtil = new MemoryOptimizationUtil();
        if (DangerousOptionsUtil.isDangerousOptionsHintEnabled(this)) {
            DangerousOptionsUtil.checkDangerousOptions(this, true);
        }
        boolean z = Build.IS_INTERNATIONAL_BUILD;
        if (!z) {
            XiaomiAccountUtils xiaomiAccountUtils = XiaomiAccountUtils.getInstance(this);
            this.mXiaomiAccountUtils = xiaomiAccountUtils;
            xiaomiAccountUtils.init(this);
        }
        boolean shouldDisableAppTimer = MiuiUtils.shouldDisableAppTimer(getApplicationContext());
        this.shouldDisableAppTimer = shouldDisableAppTimer;
        if (!shouldDisableAppTimer) {
            CommonUtils.preloadUsageStats(getApplicationContext());
        }
        FullScreenDisplayController.initInfinityDisplaySettings(getApplicationContext());
        if (MiuiShortcut$System.isSupportNewVersionKeySettings(getApplicationContext())) {
            Settings.Secure.putInt(getApplicationContext().getContentResolver(), "support_gesture_shortcut_settings", 1);
        }
        hideActionBar();
        getFeedbackSettingsLabel();
        MiuiUtils.setNavigationBackground(this, MiuiUtils.isInFullWindowGestureMode(getApplicationContext()));
        XiaomiAccountUtils xiaomiAccountUtils2 = this.mXiaomiAccountUtils;
        if (xiaomiAccountUtils2 != null && !z) {
            xiaomiAccountUtils2.resume(this);
        }
        this.splitPageRunnable = new SplitPageRunnable(this);
        getMainThreadHandler().postDelayed(this.splitPageRunnable, 100L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        XiaomiAccountUtils xiaomiAccountUtils = this.mXiaomiAccountUtils;
        if (xiaomiAccountUtils != null && !Build.IS_INTERNATIONAL_BUILD) {
            xiaomiAccountUtils.destroy(this);
        }
        if (this.shouldDisableAppTimer) {
            return;
        }
        CommonUtils.releasePreloadStats(getApplicationContext());
    }

    public void onFinishEdit() {
    }

    public void onHeaderClick(final PreferenceActivity.Header header, int i) {
        Bundle call;
        try {
            MiStatInterfaceUtils.trackPreferenceClick(MiuiSettings.class.getName(), MiuiUtils.getResourceName(this, header.titleRes));
            OneTrackInterfaceUtils.trackPreferenceClick(TAG, MiuiUtils.getResourceName(this, header.titleRes));
            String resourceName = MiuiUtils.getResourceName(this, header.titleRes);
            if (resourceName == null) {
                CharSequence charSequence = header.title;
                resourceName = charSequence == null ? "" : charSequence.toString();
            }
            HashMap hashMap = new HashMap();
            hashMap.put("page_title", resourceName);
            InternationalCompat.trackReportEvent("setting_homepage_click", hashMap);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        SettingsNotifyHelper.ensureSettingsModification(getApplicationContext(), (int) header.id);
        this.mSelectHeaderFragment = header.fragment;
        int i2 = this.mCurrentSelectedHeaderIndex;
        this.mCurrentSelectedHeaderIndex = i;
        if (restrictedDisabled(header)) {
            return;
        }
        long j = header.id;
        if (j == R.id.mimoney_settings) {
            resetPosition(i2);
            PaymentManager.get(this).gotoMiliCenter(this);
        } else if (j == R.id.micloud_settings || j == R.id.mi_account_settings) {
            resetPosition(i2);
            if (SettingsNotifyHelper.isPhoneRecycledToNotify(getApplicationContext())) {
                SettingsNotifyHelper.setPhoneRecycledAndUserOp(getApplicationContext(), true);
            }
            SettingsNotifyHelper.resetXiaomiAccountCachedStatus();
            if (AccountManager.get(this).getAccountsByType("com.xiaomi").length != 1) {
                AccountManager.get(this).addAccount("com.xiaomi", null, null, null, null, new AccountManagerCallback<Bundle>() { // from class: com.android.settings.MiuiSettings.2
                    @Override // android.accounts.AccountManagerCallback
                    public void run(AccountManagerFuture<Bundle> accountManagerFuture) {
                        try {
                            Bundle result = accountManagerFuture.getResult();
                            if (result == null || !result.containsKey(PaymentManager.KEY_INTENT)) {
                                return;
                            }
                            Intent intent = (Intent) result.getParcelable(PaymentManager.KEY_INTENT);
                            if (SettingsFeatures.isSplitTablet(MiuiSettings.this)) {
                                intent.addMiuiFlags(16);
                            }
                            MiuiSettings.this.startSplitActivityIfNeed(intent);
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }, null);
                return;
            }
            if (SettingsFeatures.isSplitTablet(this)) {
                header.intent.addMiuiFlags(16);
            }
            startSplitActivityIfNeed(header.intent);
        } else if (j == R.id.font_settings) {
            resetPosition(i2);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            intent.setData(Uri.parse("theme://zhuti.xiaomi.com/list?S.REQUEST_RESOURCE_CODE=fonts&miback=true&miref=" + getPackageName()));
            intent.putExtra(":miui:starting_window_label", "");
            if (MiuiUtils.getInstance().canFindActivity(this, intent)) {
                startSplitActivityIfNeed(intent);
            }
        } else if (j == R.id.launcher_settings) {
            resetPosition(i2);
            startSplitActivityIfNeed(MiuiUtils.buildLauncherSettingsIntent());
        } else if (j == R.id.voice_assist) {
            resetPosition(i2);
            Intent buildXiaoAiSettingsIntent = MiuiUtils.buildXiaoAiSettingsIntent();
            if (buildXiaoAiSettingsIntent != null) {
                startSplitActivityIfNeed(buildXiaoAiSettingsIntent);
            }
        } else if (j == R.id.header_google_settings) {
            resetPosition(i2);
            final ArrayList parcelableArrayList = header.extras.getParcelableArrayList("header_user");
            if (parcelableArrayList.size() == 1) {
                startSplitActivityIfNeed(header.intent);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(com.android.settingslib.R$string.choose_profile);
            builder.setItems(UserAdapter.getUserItem(this, parcelableArrayList), new DialogInterface.OnClickListener() { // from class: com.android.settings.MiuiSettings$$ExternalSyntheticLambda0
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    MiuiSettings.this.lambda$onHeaderClick$0(header, parcelableArrayList, dialogInterface, i3);
                }
            });
            builder.create().show();
        } else if (j == R.id.wallpaper_settings) {
            resetPosition(i2);
            try {
                Intent intent2 = new Intent();
                ContentProviderClient acquireUnstableContentProviderClient = getContentResolver().acquireUnstableContentProviderClient(Uri.parse("content://com.miui.miwallpaper.wallpaper"));
                if (acquireUnstableContentProviderClient != null && (call = acquireUnstableContentProviderClient.call("GET_SUPPORT_SUPER_WALLPAPER", null, null)) != null && call.getBoolean("support_super_wallpaper")) {
                    intent2.setComponent(new ComponentName("com.android.thememanager", "com.android.thememanager.settings.superwallpaper.activity.WallpaperSettingSupportSuperWallpaperActivity"));
                }
                if (intent2.getComponent() == null) {
                    intent2.setComponent(new ComponentName("com.android.thememanager", "com.android.thememanager.settings.WallpaperSettingsActivity"));
                }
                startSplitActivityIfNeed(intent2);
            } catch (Exception e2) {
                e2.printStackTrace();
                Intent intent3 = new Intent();
                intent3.setComponent(new ComponentName("com.android.thememanager", "com.android.thememanager.settings.WallpaperSettingsActivity"));
                startSplitActivityIfNeed(intent3);
            }
        } else if (j == R.id.system_apps_updater) {
            resetPosition(i2);
            startSplitActivityIfNeed(header.intent);
        } else if (j == R.id.personalize_title) {
            Intent intent4 = new Intent();
            intent4.setAction("android.intent.action.VIEW");
            intent4.addCategory("android.intent.category.DEFAULT");
            intent4.setData(Uri.parse("theme://zhuti.xiaomi.com/personalize?miback=true&miref=settings"));
            if (MiuiUtils.getInstance().canFindActivity(this, intent4)) {
                startSplitActivityIfNeed(intent4);
            }
        } else if (j == R.id.mi_wallet_payment) {
            Intent intent5 = new Intent("com.mipay.action.MIPAYINFO");
            intent5.setPackage("com.mipay.wallet");
            if (getPackageManager().queryIntentActivities(intent5, 0).size() <= 0) {
                intent5.setAction("android.intent.action.VIEW");
                intent5.addCategory("com.mipay.wallet.MIPAYINFO");
                intent5.setData(Uri.parse("mipay://walletapp?id=mipay.info"));
            }
            if (SettingsFeatures.isSplitTablet(this)) {
                intent5.addMiuiFlags(16);
            }
            startSplitActivityForResultIfNeed(intent5, 1);
        } else if (header.fragment == null) {
            if (header.intent != null) {
                resetPosition(i2);
                if (header.id == R.id.msim_settings) {
                    header.intent.putExtra(":miui:starting_window_label", "");
                }
                if (SettingsFeatures.isSplitTablet(this)) {
                    header.intent.addMiuiFlags(16);
                }
                startSplitActivityIfNeed(header.intent);
            }
        } else {
            this.mLanguageChange = false;
            if (TabletUtils.IS_TABLET) {
                this.mSettingsFragment.getHeaderAdapter().notifyItemChanged(i2);
                this.mSettingsFragment.getHeaderAdapter().notifyItemChanged(this.mCurrentSelectedHeaderIndex);
            }
            if (TabletUtils.IS_TABLET && popBackStack(header)) {
                return;
            }
            String str = header.fragment;
            Bundle bundle = header.fragmentArguments;
            int i3 = header.titleRes;
            startWithFragment(str, bundle, null, 0, i3, i3);
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        Fragment findFragmentById;
        if (TabletUtils.IS_TABLET && (findFragmentById = getSupportFragmentManager().findFragmentById(R.id.content)) != null && (findFragmentById instanceof BaseFragment) && ((BaseFragment) findFragmentById).onKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        Fragment findFragmentById;
        if (TabletUtils.IS_TABLET && (findFragmentById = getSupportFragmentManager().findFragmentById(R.id.content)) != null && (findFragmentById instanceof BaseFragment) && ((BaseFragment) findFragmentById).onKeyUp(i, keyEvent)) {
            return true;
        }
        return super.onKeyUp(i, keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        invalidateSelectHeader();
        if (this.splitPageRunnable == null || getMainThreadHandler().hasCallbacks(this.splitPageRunnable)) {
            return;
        }
        getMainThreadHandler().postDelayed(this.splitPageRunnable, 100L);
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == 16908332) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, android.app.Activity
    public void onRestoreInstanceState(Bundle bundle) {
        int i = this.mCurrentSelectedHeaderIndex;
        if (i != -1 && i != -2) {
            this.mCurrentSelectedHeaderIndex = bundle.getInt("select_header_index", i);
        }
        this.mSelectHeaderFragment = bundle.getString("select_header", this.mSelectHeaderFragment);
        super.onRestoreInstanceState(bundle);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        boolean hasUserRestriction;
        super.onResume();
        XiaomiAccountUtils xiaomiAccountUtils = this.mXiaomiAccountUtils;
        if (xiaomiAccountUtils != null && !Build.IS_INTERNATIONAL_BUILD) {
            xiaomiAccountUtils.resume(this);
        }
        boolean z = false;
        int configuredVpnStatus = MiuiVpnUtils.getConfiguredVpnStatus(this);
        int i = this.mLastVpnConfiguredStatus;
        boolean z2 = true;
        if (i == -1) {
            this.mLastVpnConfiguredStatus = configuredVpnStatus;
        } else if (configuredVpnStatus != i) {
            this.mLastVpnConfiguredStatus = configuredVpnStatus;
            z = true;
        }
        if (this.mWifiTetherStatus != SettingsFeatures.getWifiTetherPlacement(getApplicationContext())) {
            z = true;
        }
        boolean isXOptMode = AppOpsUtils.isXOptMode();
        boolean z3 = this.mIsXoptMode;
        if (isXOptMode != z3) {
            this.mIsXoptMode = !z3;
            z = true;
        }
        if (!TabletUtils.IS_TABLET || this.mLastDisallowConfigTetherStatus == (hasUserRestriction = UserManager.get(this).hasUserRestriction("no_config_tethering"))) {
            z2 = z;
        } else {
            this.mLastDisallowConfigTetherStatus = hasUserRestriction;
        }
        Log.i(TAG, "onResume: needUpdateHeader: " + z2 + ", and vpnConfiguredStatus = : " + configuredVpnStatus);
        if (z2) {
            this.mSettingsFragment.buildAdapter();
        }
        ProxyHeaderViewAdapter headerAdapter = this.mSettingsFragment.getHeaderAdapter();
        if (headerAdapter != null) {
            RecyclerView.Adapter adapter = headerAdapter.mBaseAdapter;
            if (adapter instanceof HeaderAdapter) {
                ((HeaderAdapter) adapter).updateAdminDisallowedConfig(headerAdapter);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putInt("select_header_index", this.mCurrentSelectedHeaderIndex);
        bundle.putString("select_header", this.mSelectHeaderFragment);
        super.onSaveInstanceState(bundle);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        MemoryOptimizationUtil memoryOptimizationUtil = this.mMemoryOptimizationUtil;
        if (memoryOptimizationUtil != null) {
            memoryOptimizationUtil.bindMemoryOptimizationService(this);
        }
    }

    public void onStartEdit() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        MemoryOptimizationUtil memoryOptimizationUtil = this.mMemoryOptimizationUtil;
        if (memoryOptimizationUtil != null) {
            memoryOptimizationUtil.startMemoryOptimization(this);
        }
    }

    @Override // com.android.settings.accounts.XiaomiAccountUtils.UpdateAccountListener
    public void onXiaomiAccountUpdate() {
        ProxyHeaderViewAdapter headerAdapter;
        SettingsFragment settingsFragment = this.mSettingsFragment;
        if (settingsFragment == null || settingsFragment.getView() == null || (headerAdapter = this.mSettingsFragment.getHeaderAdapter()) == null) {
            return;
        }
        headerAdapter.updateHeaderViewInfo();
    }

    protected void splitIfNeeded(Bundle bundle) {
        splitIfNeeded(getMiuiCustSplitUtils(), this.mContent, bundle);
    }

    public void splitIfNeeded(MiuiCustSplitUtils miuiCustSplitUtils, ViewGroup viewGroup, Bundle bundle) {
        if (miuiCustSplitUtils == null || viewGroup == null || !com.android.settingslib.utils.SplitUtils.isSplitAllowed()) {
            return;
        }
        miuiCustSplitUtils.setSplit(viewGroup);
        Intent intent = new Intent();
        intent.putExtra("show_drawer_menu", true);
        intent.addFlags(MiuiWindowManager$LayoutParams.EXTRA_FLAG_DISABLE_FOD_ICON);
        intent.setComponent(getFirstExistMenu());
        miuiCustSplitUtils.setFirstIntent(intent);
        if (isTopOfTask()) {
            this.mCurrentSelectedHeaderIndex = -1;
        } else {
            this.mCurrentSelectedHeaderIndex = -2;
        }
    }

    public void startPreferencePanel(String str, Bundle bundle, int i, CharSequence charSequence, Fragment fragment, int i2) {
        startWithFragment(str, bundle, fragment, i2, i, charSequence, 0);
    }

    public void startWithFragment(String str, Bundle bundle, Fragment fragment, int i, int i2, int i3) {
        startWithFragment(str, bundle, fragment, i, i2, null, i3);
    }

    public void startWithFragment(String str, Bundle bundle, Fragment fragment, int i, int i2, CharSequence charSequence, int i3) {
        if (!TabletUtils.IS_TABLET) {
            new SubSettingLauncher(this).setDestination(str).setTitleRes(i2).setArguments(bundle).setResultListener(fragment, i).launch();
            return;
        }
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        Fragment findFragmentByTag = supportFragmentManager.findFragmentByTag(str);
        if (findFragmentByTag == null) {
            if (i2 > 0) {
                if (bundle == null) {
                    bundle = new Bundle();
                }
                bundle.putInt(":android:show_fragment_title", i2);
            }
            if (!TextUtils.isEmpty(charSequence)) {
                bundle.putString(":settings:show_fragment_title", (String) charSequence);
            }
            findFragmentByTag = Fragment.instantiate(this, str, bundle);
        }
        if (fragment != null) {
            findFragmentByTag.setTargetFragment(fragment, i);
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        beginTransaction.addToBackStack(str);
        beginTransaction.setCustomAnimations(R.animator.fragment_slide_enter, R.animator.fragment_slide_exit, R.animator.fragment_pop_slide_enter, R.animator.fragment_pop_slide_exit);
        beginTransaction.replace(R.id.content, findFragmentByTag, str);
        beginTransaction.commitAllowingStateLoss();
    }

    public void updateHeaderList(List<PreferenceActivity.Header> list) {
        this.mIsXoptMode = AppOpsUtils.isXOptMode();
        Context baseContext = getBaseContext();
        OldmanHelper.isStatusBarSettingsHidden(this);
        int configuredVpnStatus = MiuiVpnUtils.getConfiguredVpnStatus(baseContext);
        this.mLastVpnConfiguredStatus = configuredVpnStatus;
        this.mWifiTetherStatus = SettingsFeatures.getWifiTetherPlacement(baseContext);
        Resources resources = getResources();
        boolean isOldmanMode = OldmanHelper.isOldmanMode();
        PackageManager packageManager = getPackageManager();
        int myUserId = UserHandle.myUserId();
        int i = 0;
        while (i < list.size()) {
            PreferenceActivity.Header header = list.get(i);
            int i2 = (int) header.id;
            if (i2 == R.id.android_beam_settings) {
                list.remove(header);
            } else {
                if (i2 == R.id.msim_settings) {
                    if (SystemProperties.getInt("ro.miui.singlesim", 0) == 1 || TelephonyManager.from(baseContext).getSimCount() < 2) {
                        int i3 = R.string.sim_management_title_singlesim;
                        header.titleRes = i3;
                        header.title = resources.getText(i3);
                    }
                    if (com.android.settingslib.Utils.isWifiOnly(this)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.operator_settings || i2 == R.id.manufacturer_settings) {
                    Utils.updateHeaderToSpecificActivityFromMetaDataOrRemove(this, list, header);
                } else if (i2 == R.id.wifi_settings) {
                    if (!packageManager.hasSystemFeature("android.hardware.wifi")) {
                        list.remove(i);
                    }
                } else if (i2 == R.id.bluetooth_settings) {
                    if (!packageManager.hasSystemFeature("android.hardware.bluetooth")) {
                        list.remove(i);
                    }
                } else if (i2 == R.id.user_settings) {
                    if (!this.mIsXoptMode) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.mimoney_settings) {
                    if (PaymentManager.get(this).isMibiServiceDisabled()) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.wallpaper_settings) {
                    if (isOldmanMode || !MiuiUtils.needRemovePersonalize(baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.theme_settings) {
                    if (SettingsFeatures.IS_NEED_REMOVE_THEME || !MiuiUtils.needRemovePersonalize(baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.sound_settings) {
                    Vibrator vibrator = (Vibrator) getSystemService("vibrator");
                    if (SettingsFeatures.isSupportSettingsHaptic(baseContext)) {
                        header.titleRes = R.string.sound_haptic_settings;
                    } else {
                        header.titleRes = vibrator.hasVibrator() ? R.string.sound_vibrate_settings : R.string.sound_settings;
                    }
                    header.title = resources.getText(header.titleRes);
                } else if (i2 == R.id.system_apps_updater) {
                    if (Build.IS_TABLET || MiuiUtils.needRemoveSystemAppsUpdater(baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.wifi_tether_settings) {
                    if (this.mWifiTetherStatus != 1) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.vpn_settings_multiple) {
                    boolean hasRestriction = RestrictionsHelper.hasRestriction(baseContext, "disallow_vpn");
                    if (configuredVpnStatus < 1 || hasRestriction || SettingsFeatures.isSplitTablet(this)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.security_status) {
                    if (!Build.IS_INTERNATIONAL_BUILD) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.voice_assist) {
                    if (MiuiUtils.excludeXiaoAi(baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.dynamic_item) {
                    if (!new DynamicItemUtils().shouldShow(header, baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.micloud_settings) {
                    if (!Build.IS_INTERNATIONAL_BUILD || (MiuiUtils.isDeviceManaged(baseContext) && !MiuiUtils.isDeviceFinanceOwner(baseContext))) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.mi_account_settings) {
                    if (Build.IS_INTERNATIONAL_BUILD || MiuiUtils.isMaintenanceMode(baseContext)) {
                        list.remove(header);
                    }
                } else if (i2 == R.id.screen_settings) {
                    int screenTitle = TitleManager.getScreenTitle(baseContext);
                    header.titleRes = screenTitle;
                    header.title = resources.getText(screenTitle);
                } else if (i2 == R.id.app_timer) {
                    if (this.shouldDisableAppTimer) {
                        list.remove(header);
                    } else {
                        HomeListUtils.ensureReplaceTimer(baseContext, header);
                    }
                } else if (i2 != R.id.battery_settings_new) {
                    if (i2 == R.id.privacy_settings || i2 == R.id.location_settings) {
                        if (!Build.IS_INTERNATIONAL_BUILD) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.global_feedback_category) {
                        if (!Build.IS_INTERNATIONAL_BUILD || !SettingsFeatures.isFeedbackNeeded(baseContext)) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.feedback_services_settings) {
                        if (!SettingsFeatures.isFeedbackNeeded(baseContext)) {
                            list.remove(header);
                        }
                        CharSequence charSequence = this.mFeedbackLabel;
                        if (charSequence != null) {
                            header.titleRes = 0;
                            header.title = charSequence;
                        }
                    } else if (i2 == R.id.vip_service_settings) {
                        if (!SettingsFeatures.isVipServiceNeeded(baseContext)) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.my_device) {
                        if (!SettingsFeatures.isShowMyDevice()) {
                            Resources resources2 = getResources();
                            int i4 = R.string.about_settings;
                            header.title = resources2.getString(i4);
                            header.titleRes = i4;
                        }
                    } else if (i2 == R.id.launcher_settings) {
                        if (Settings.System.getInt(getContentResolver(), "elderly_mode", 0) == 1) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.privacy_protection_settings) {
                        if (!SettingsFeatures.isPrivacyProtectionNeeded(baseContext)) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.other_special_feature_settings) {
                        if (MiuiUtils.isLowMemoryMachine() && Build.IS_INTERNATIONAL_BUILD) {
                            list.remove(header);
                        }
                    } else if (i2 == R.id.personalize_title && MiuiUtils.needRemovePersonalize(baseContext)) {
                        list.remove(header);
                    } else if (i2 == R.id.mi_wallet_payment && MiuiUtils.needRemoveWalletEntrance(baseContext)) {
                        list.remove(header);
                    } else if (i2 == R.id.security_center_settings && !MiuiUtils.isSupportSecuritySettings(baseContext)) {
                        list.remove(header);
                    } else if (i2 == R.id.safety_emergency_settings && !MiuiUtils.isSupportSafetyEmergencySettings(baseContext)) {
                        list.remove(header);
                    } else if (i2 == R.id.security_settings) {
                        int passwordTypes = SettingsFeatures.getPasswordTypes(baseContext);
                        header.titleRes = passwordTypes;
                        header.title = resources.getText(passwordTypes);
                    }
                }
            }
            if (myUserId != 0 && ArrayUtils.contains(this.MIUI_SETTINGS_FOR_RESTRICTED, i2)) {
                Log.i(TAG, "updateHeaderList remove header,  myUserId = " + myUserId);
                list.remove(header);
            }
            if (i < list.size() && list.get(i) == header) {
                i++;
            }
        }
        if (Build.IS_GLOBAL_BUILD) {
            AddGoogleSettingsHeaders(list);
        }
        HomeListUtils.addAmazonAlexa(baseContext, list, R.id.other_special_feature_settings);
    }
}
