package com.android.settings.applications;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.hardware.usb.IUsbManager;
import android.miui.AppOpsUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.MiuiSettings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.applications.AppInfoPreference;
import com.android.settings.applications.PermissionInfoFragment;
import com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd;
import com.android.settings.cust.MiHomeManager;
import com.android.settings.search.FunctionColumns;
import com.android.settings.search.tree.SecuritySettingsTree;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.miuisettings.preference.ButtonPreference;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import miui.content.pm.PreloadedAppPolicy;
import miui.content.res.ThemeResources;
import miui.os.Build;
import miui.payment.PaymentManager;
import miui.provider.ExtraNetwork;
import miui.securityspace.XSpaceUserHandle;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes.dex */
public class InstalledAppDetailsFragment extends SettingsPreferenceFragment implements CompoundButton.OnCheckedChangeListener, ApplicationsState.Callbacks, Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    private HashSet<String> mAlwaysEnabledAppList;
    private ApplicationsState.AppEntry mAppEntry;
    private AppOpsManager mAppOpsManager;
    private AppWidgetManager mAppWidgetManager;
    private ClearCacheObserver mClearCacheObserver;
    private ClearUserDataObserver mClearDataObserver;
    private CharSequence mComputingStr;
    private DevicePolicyManager mDpm;
    private CharSequence mInvalidSizeStr;
    private MenuItem mItemFinish;
    private MenuItem mItemUninstall;
    private MiHomeManager mMiHomeManager;
    private PackageInfo mPackageInfo;
    private IPackageManager mPackageManager;
    private AppInfoPreference mPrefAppInfo;
    private ValuePreference mPrefApplicationSize;
    private ValuePreference mPrefCacheSize;
    private ButtonPreference mPrefClearCache;
    private ButtonPreference mPrefClearData;
    private Preference mPrefClearDefaultSettings;
    private ValuePreference mPrefDataSize;
    private ValuePreference mPrefExternalCodeSize;
    private ValuePreference mPrefExternalDataSize;
    private CheckBoxPreference mPrefFloatingWindowSwitch;
    private ValuePreference mPrefNotificationSwitch;
    private Preference mPrefPermissionDetailInfo;
    private Preference mPrefPermissionManage;
    private Preference mPrefPermissionSettings;
    private ValuePreference mPrefTotalSize;
    private ApplicationsState.Session mSession;
    private ApplicationsState mState;
    private int mUninstallIconRes;
    private int mUninstallTextRes;
    private IUsbManager mUsbManager;
    int mUserId;
    private boolean mMoveInProgress = false;
    private boolean mUpdatedSysApp = false;
    private boolean mCanClearData = true;
    private boolean mNeedRefreshFloatingWindow = true;
    private boolean mFinishEnable = false;
    private boolean mUninstallEnable = true;
    private boolean mShowMenus = true;
    private boolean mHaveSizes = false;
    private long mLastCodeSize = -1;
    private long mLastDataSize = -1;
    private long mLastExternalCodeSize = -1;
    private long mLastExternalDataSize = -1;
    private long mLastCacheSize = -1;
    private long mLastTotalSize = -1;
    private Handler mHandler = new Handler() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (InstalledAppDetailsFragment.this.getActivity() == null || InstalledAppDetailsFragment.this.getView() == null) {
                return;
            }
            int i = message.what;
            if (i == 1) {
                InstalledAppDetailsFragment.this.processClearMsg(message);
            } else if (i == 3) {
                InstalledAppDetailsFragment.this.mState.requestSize(InstalledAppDetailsFragment.this.mAppEntry.info.packageName, InstalledAppDetailsFragment.this.mUserId);
            } else if (i == 4) {
                InstalledAppDetailsFragment.this.processMoveMsg(message);
            } else if (i != 5) {
            } else {
                InstalledAppDetailsFragment.this.setIntentAndFinish(true, true);
            }
        }
    };
    private final BroadcastReceiver mCheckKillProcessesReceiver = new CheckKillProcessesReceiver();

    /* loaded from: classes.dex */
    private static class CheckKillProcessesReceiver extends BroadcastReceiver {
        private WeakReference<InstalledAppDetailsFragment> mFragmentRef;

        private CheckKillProcessesReceiver(InstalledAppDetailsFragment installedAppDetailsFragment) {
            this.mFragmentRef = new WeakReference<>(installedAppDetailsFragment);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (this.mFragmentRef.get() != null) {
                int resultCode = getResultCode();
                Log.v("InstalledAppDetails", "force stop, result code = " + resultCode);
                this.mFragmentRef.get().updateForceStopButton(resultCode != 0);
            }
        }
    }

    /* loaded from: classes.dex */
    class ClearCacheObserver extends IPackageDataObserver.Stub {
        ClearCacheObserver() {
        }

        public void onRemoveCompleted(String str, boolean z) {
            Message obtainMessage = InstalledAppDetailsFragment.this.mHandler.obtainMessage(3);
            obtainMessage.arg1 = z ? 1 : 2;
            InstalledAppDetailsFragment.this.mHandler.sendMessage(obtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class ClearUserDataObserver extends IPackageDataObserver.Stub {
        ClearUserDataObserver() {
        }

        public void onRemoveCompleted(String str, boolean z) {
            Message obtainMessage = InstalledAppDetailsFragment.this.mHandler.obtainMessage(1);
            obtainMessage.arg1 = z ? 1 : 2;
            InstalledAppDetailsFragment.this.mHandler.sendMessage(obtainMessage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DeleteObserver extends IPackageDeleteObserver.Stub {
        private DeleteObserver() {
        }

        public void packageDeleted(String str, int i) {
            Log.v("InstalledAppDetails", "uninstall success, package = " + str + ", return code  = " + i);
            Message obtainMessage = InstalledAppDetailsFragment.this.mHandler.obtainMessage(5);
            obtainMessage.arg1 = i;
            InstalledAppDetailsFragment.this.mHandler.sendMessage(obtainMessage);
        }
    }

    /* loaded from: classes.dex */
    static class DisableChanger extends AsyncTask<Object, Object, Object> {
        final WeakReference<InstalledAppDetailsFragment> mActivity;
        final ApplicationInfo mInfo;
        final IPackageManager mPackageManager;
        final int mState;

        DisableChanger(InstalledAppDetailsFragment installedAppDetailsFragment, ApplicationInfo applicationInfo, int i) {
            this.mPackageManager = installedAppDetailsFragment.mPackageManager;
            this.mActivity = new WeakReference<>(installedAppDetailsFragment);
            this.mInfo = applicationInfo;
            this.mState = i;
        }

        @Override // android.os.AsyncTask
        protected Object doInBackground(Object... objArr) {
            if (this.mActivity.get() != null) {
                try {
                    this.mPackageManager.setApplicationEnabledSetting(this.mInfo.packageName, this.mState, 0, this.mActivity.get().mUserId, this.mActivity.get().getContext().getOpPackageName());
                    return null;
                } catch (RemoteException e) {
                    Log.e("InstalledAppDetails", "Can not setApplicationEnabledSetting for pkg:" + this.mInfo.packageName, e);
                    return null;
                }
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class MyAlertDialogFragment extends DialogFragment {
        private boolean mIsFloatingWindowEnabled;

        public static MyAlertDialogFragment newInstance(int i, int i2, String str, String str2) {
            MyAlertDialogFragment myAlertDialogFragment = new MyAlertDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id", i);
            bundle.putInt("moveError", i2);
            bundle.putString("title", str);
            bundle.putString("content", str2);
            myAlertDialogFragment.setArguments(bundle);
            return myAlertDialogFragment;
        }

        InstalledAppDetailsFragment getOwner() {
            return (InstalledAppDetailsFragment) getTargetFragment();
        }

        @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialogInterface) {
            super.onCancel(dialogInterface);
            if (getArguments().getInt("id") == 9) {
                getOwner().mPrefFloatingWindowSwitch.setChecked(!getOwner().mPrefFloatingWindowSwitch.isChecked());
            }
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // androidx.fragment.app.DialogFragment
        public Dialog onCreateDialog(Bundle bundle) {
            String string;
            String string2;
            int i = getArguments().getInt("id");
            int i2 = getArguments().getInt("moveError");
            String str = "";
            boolean z = true;
            switch (i) {
                case 1:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.clear_data_dlg_title)).setIconAttribute(16843605).setMessage(getActivity().getText(R.string.clear_data_dlg_text)).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.1
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            MyAlertDialogFragment.this.getOwner().initiateClearUserData();
                        }
                    }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
                case 2:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.app_factory_reset_dlg_title)).setIconAttribute(16843605).setMessage(getActivity().getText(R.string.app_factory_reset_dlg_text)).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.2
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i3) {
                            if (MyAlertDialogFragment.this.getOwner().mAppEntry != null) {
                                MyAlertDialogFragment.this.getOwner().uninstallPkg(MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName, MyAlertDialogFragment.this.getOwner().mUserId);
                            }
                        }
                    }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
                case 3:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    FragmentActivity activity = getActivity();
                    int i3 = R.string.app_not_found_dlg_title;
                    return builder.setTitle(activity.getText(i3)).setIconAttribute(16843605).setMessage(getActivity().getText(i3)).setNeutralButton(getActivity().getText(R.string.dlg_ok), new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.3
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            MyAlertDialogFragment.this.getOwner().setIntentAndFinish(true, true);
                        }
                    }).create();
                case 4:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.clear_user_data_text)).setIconAttribute(16843605).setMessage(getActivity().getText(R.string.clear_failed_dlg_text)).setNeutralButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.4
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            MyAlertDialogFragment.this.getOwner().mPrefClearData.setEnabled(false);
                            MyAlertDialogFragment.this.getOwner().setIntentAndFinish(false, false);
                        }
                    }).create();
                case 5:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.force_stop_dlg_title)).setIconAttribute(16843605).setMessage(getActivity().getText(R.string.force_stop_dlg_text)).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.5
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            if (MyAlertDialogFragment.this.getOwner().mAppEntry != null) {
                                MyAlertDialogFragment.this.getOwner().forceStopPackage(MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName);
                            }
                        }
                    }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
                case 6:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.move_app_failed_dlg_title)).setIconAttribute(16843605).setMessage(getActivity().getString(R.string.move_app_failed_dlg_text, new Object[]{getOwner().getMoveErrMsg(i2)})).setNeutralButton(R.string.dlg_ok, (DialogInterface.OnClickListener) null).create();
                case 7:
                    return new AlertDialog.Builder(getActivity()).setTitle(getActivity().getText(R.string.app_disable_dlg_title)).setIconAttribute(16843605).setMessage(getActivity().getText(R.string.app_disable_dlg_text)).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.6
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            if (MyAlertDialogFragment.this.getOwner().mAppEntry != null) {
                                new DisableChanger(MyAlertDialogFragment.this.getOwner(), MyAlertDialogFragment.this.getOwner().mAppEntry.info, 3).execute(null);
                            }
                        }
                    }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
                case 8:
                default:
                    throw new IllegalArgumentException("unknown id " + i);
                case 9:
                    if (getOwner().mAppEntry != null) {
                        str = getOwner().mAppEntry.label;
                        this.mIsFloatingWindowEnabled = getOwner().mPrefFloatingWindowSwitch.isChecked();
                    } else if (bundle != null) {
                        str = bundle.getString("float_window_label");
                        this.mIsFloatingWindowEnabled = bundle.getBoolean("float_window_enable");
                        getOwner().mPrefFloatingWindowSwitch.setChecked(this.mIsFloatingWindowEnabled);
                        getOwner().mNeedRefreshFloatingWindow = false;
                    }
                    if (this.mIsFloatingWindowEnabled) {
                        string = getActivity().getString(R.string.floating_window_switch_label);
                        string2 = getActivity().getString(R.string.app_floating_window_dlg_enable, new Object[]{str});
                    } else {
                        string = getActivity().getString(R.string.floating_window_switch_label_disable);
                        string2 = getActivity().getString(R.string.app_floating_window_dlg_disable, new Object[]{str});
                    }
                    return new AlertDialog.Builder(getActivity()).setTitle(string).setMessage(string2).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.8
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            MyAlertDialogFragment.this.getOwner().setFloatingWindowEnabled(MyAlertDialogFragment.this.mIsFloatingWindowEnabled);
                            if (MyAlertDialogFragment.this.mIsFloatingWindowEnabled || MyAlertDialogFragment.this.getOwner().mAppEntry == null) {
                                return;
                            }
                            MyAlertDialogFragment.this.getOwner().forceStopPackage(MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName);
                        }
                    }).setNegativeButton(R.string.dlg_cancel, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.7
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialogInterface, int i4) {
                            MyAlertDialogFragment.this.getOwner().mPrefFloatingWindowSwitch.setChecked(!MyAlertDialogFragment.this.mIsFloatingWindowEnabled);
                        }
                    }).create();
                case 10:
                    break;
                case 11:
                    String string3 = getArguments().getString("title", "");
                    String string4 = getArguments().getString("content", "");
                    if (!string3.isEmpty() && !string4.isEmpty()) {
                        return new AlertDialog.Builder(getActivity()).setTitle(string3).setMessage(string4).setPositiveButton(R.string.remove_continue, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.9
                            @Override // android.content.DialogInterface.OnClickListener
                            public void onClick(DialogInterface dialogInterface, int i4) {
                                MyAlertDialogFragment.this.getOwner().showDialogInner(10, 0);
                            }
                        }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
                    }
                    break;
            }
            int i4 = R.string.uninstall_dlg_title;
            int i5 = R.string.uninstall_dlg_msg;
            if (XSpaceUserHandle.isXSpaceUserId(getOwner().mUserId)) {
                i4 = R.string.uninstall_xspace_app_dlg_title;
                i5 = R.string.uninstall_xspace_app_dlg_msg;
            } else if (getOwner().mAppEntry != null && getOwner().hasRelativeXSpaceApp(getOwner().mAppEntry.info.packageName)) {
                i5 = R.string.uninstall_with_xspace_app_dlg_msg;
            }
            if (bundle != null) {
                z = bundle.getBoolean("app_need_protected", false);
            } else if (getOwner().noNeedToProtect()) {
                z = false;
            }
            if (z) {
                i4 = R.string.uninstall_protected_dlg_title;
                i5 = R.string.uninstall_protected_dlg_msg;
            }
            return new AlertDialog.Builder(getActivity()).setTitle(i4).setMessage(i5).setPositiveButton(R.string.dlg_ok, new DialogInterface.OnClickListener() { // from class: com.android.settings.applications.InstalledAppDetailsFragment.MyAlertDialogFragment.10
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i6) {
                    if (MyAlertDialogFragment.this.getOwner().mAppEntry != null) {
                        MyAlertDialogFragment.this.getOwner().uninstallPkg(MyAlertDialogFragment.this.getOwner().mAppEntry.info.packageName, MyAlertDialogFragment.this.getOwner().mUserId);
                    }
                }
            }).setNegativeButton(R.string.dlg_cancel, (DialogInterface.OnClickListener) null).create();
        }

        @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putString("float_window_label", getOwner().mAppEntry.label);
            bundle.putBoolean("float_window_enable", this.mIsFloatingWindowEnabled);
            bundle.putBoolean("app_need_protected", !getOwner().noNeedToProtect());
        }
    }

    /* loaded from: classes.dex */
    class PackageMoveObserver extends IPackageMoveObserver.Stub {
        public void onCreated(int i, Bundle bundle) {
        }

        public void onStatusChanged(int i, int i2, long j) {
        }
    }

    private void checkForceStop() {
        if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName) || ThemeResources.FRAMEWORK_PACKAGE.equals(this.mPackageInfo.packageName) || "com.miui".equals(this.mPackageInfo.packageName) || this.mMiHomeManager.isWhiteListPackage(this.mPackageInfo.packageName)) {
            updateForceStopButton(false);
        } else if ((this.mAppEntry.info.flags & 2097152) == 0) {
            updateForceStopButton(true);
        } else {
            Intent intent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts(FunctionColumns.PACKAGE, this.mAppEntry.info.packageName, null));
            intent.putExtra("android.intent.extra.PACKAGES", new String[]{this.mAppEntry.info.packageName});
            intent.putExtra("android.intent.extra.UID", this.mAppEntry.info.uid);
            intent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mAppEntry.info.uid));
            if (getActivity() != null) {
                Log.v("InstalledAppDetails", "force stop, package = " + this.mAppEntry.info.packageName + ", uid = " + this.mAppEntry.info.uid);
                getActivity().sendOrderedBroadcastAsUser(intent, new UserHandle(this.mUserId), null, this.mCheckKillProcessesReceiver, null, 0, null, null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void forceStopPackage(String str) {
        ((ActivityManager) getActivity().getSystemService("activity")).forceStopPackageAsUser(str, this.mUserId);
        this.mState.invalidatePackage(str, this.mUserId);
        ApplicationsState.AppEntry entry = this.mState.getEntry(str, this.mUserId);
        if (entry != null) {
            this.mAppEntry = entry;
        }
        checkForceStop();
    }

    private String getExtraPkgName() {
        Bundle arguments = getArguments();
        String str = null;
        String string = arguments != null ? arguments.getString(FunctionColumns.PACKAGE) : null;
        if (string == null) {
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                string = intent.getData() != null ? intent.getData().getSchemeSpecificPart() : null;
                if (TextUtils.isEmpty(string)) {
                    string = intent.getStringExtra(FunctionColumns.PACKAGE);
                }
            }
            if (arguments == null || !TextUtils.isEmpty(string)) {
                return string;
            }
            Intent intent2 = (Intent) arguments.getParcelable(PaymentManager.KEY_INTENT);
            if (intent2 != null && intent2.getData() != null) {
                str = intent2.getData().getSchemeSpecificPart();
            }
            return str;
        }
        return string;
    }

    private boolean getExtraXspaceApp() {
        boolean booleanExtra = getIntent() != null ? getIntent().getBooleanExtra("is_xspace_app", false) : false;
        return (booleanExtra || !(getActivity() instanceof MiuiSettings) || getArguments() == null) ? booleanExtra : getArguments().getBoolean("is_xspace_app", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public CharSequence getMoveErrMsg(int i) {
        return i != -10 ? i != -5 ? i != -3 ? i != -2 ? i != -1 ? "" : getActivity().getString(R.string.insufficient_storage) : getActivity().getString(R.string.does_not_exist) : getActivity().getString(R.string.system_package) : getActivity().getString(R.string.invalid_location) : getActivity().getString(R.string.app_forward_locked);
    }

    private String getSizeStr(long j) {
        return j == -1 ? this.mInvalidSizeStr.toString() : Formatter.formatFileSize(getActivity(), j);
    }

    private void handlePermissonManagerPreference(PreferenceCategory preferenceCategory) {
        if (!AppOpsUtils.isXOptMode() && Build.IS_TABLET && isSystemApp(getContext(), getExtraPkgName())) {
            preferenceCategory.removePreference(this.mPrefPermissionManage);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean hasRelativeXSpaceApp(String str) {
        if (UserHandle.myUserId() != 0) {
            return false;
        }
        try {
            return this.mPackageManager.getPackageInfo(str, 0, 999) != null;
        } catch (RemoteException unused) {
            Log.e("InstalledAppDetails", "Can't talk packageManager.");
            return false;
        }
    }

    private void initDataButtons() {
        if ((this.mAppEntry.info.flags & 65) == 1 || this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
            this.mPrefClearData.setText(R.string.clear_user_data_text);
            this.mPrefClearData.setEnabled(false);
            this.mCanClearData = false;
            return;
        }
        if (this.mAppEntry.info.manageSpaceActivityName != null) {
            this.mPrefClearData.setText(R.string.manage_space_text);
        } else {
            this.mPrefClearData.setText(R.string.clear_user_data_text);
        }
        this.mPrefClearData.setOnPreferenceClickListener(this);
    }

    private void initFloatingWindowButton() {
        CheckBoxPreference checkBoxPreference = this.mPrefFloatingWindowSwitch;
        if (checkBoxPreference == null) {
            return;
        }
        boolean z = false;
        checkBoxPreference.setEnabled(false);
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry != null) {
            if ((appEntry.info.flags & 1) == 0) {
                this.mPrefFloatingWindowSwitch.setEnabled(true);
                if (this.mNeedRefreshFloatingWindow) {
                    try {
                        AppOpsManager appOpsManager = this.mAppOpsManager;
                        ApplicationInfo applicationInfo = this.mAppEntry.info;
                        if (appOpsManager.checkOp(24, applicationInfo.uid, applicationInfo.packageName) == 0) {
                            z = true;
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                    this.mPrefFloatingWindowSwitch.setChecked(z);
                }
                this.mPrefFloatingWindowSwitch.setOnPreferenceChangeListener(this);
            } else if (this.mNeedRefreshFloatingWindow) {
                this.mPrefFloatingWindowSwitch.setChecked(true);
            }
            this.mNeedRefreshFloatingWindow = true;
        }
    }

    private void initNotificationButton() {
        ValuePreference valuePreference = this.mPrefNotificationSwitch;
        if (valuePreference == null) {
            return;
        }
        valuePreference.setOnPreferenceClickListener(this);
        this.mPrefNotificationSwitch.setValue("");
        this.mPrefNotificationSwitch.setShowRightArrow(true);
    }

    private void initUninstallButtons() {
        int i = this.mAppEntry.info.flags;
        boolean z = true;
        boolean z2 = (i & 1) != 0;
        boolean z3 = (i & 128) != 0;
        this.mUpdatedSysApp = z3;
        if (z3) {
            this.mUninstallTextRes = R.string.app_factory_reset;
        } else if ((i & 1) != 0) {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.setPackage(this.mAppEntry.info.packageName);
            List queryIntentActivitiesAsUser = getPackageManager().queryIntentActivitiesAsUser(intent, 0, this.mUserId);
            this.mUninstallIconRes = R.drawable.action_button_stop;
            if ((queryIntentActivitiesAsUser != null && queryIntentActivitiesAsUser.size() > 0) || isThisASystemPackage()) {
                this.mUninstallTextRes = R.string.disable_text;
            } else if (this.mAppEntry.info.enabled) {
                this.mUninstallTextRes = R.string.disable_text;
            } else {
                this.mUninstallTextRes = R.string.enable_text;
            }
            z = false;
        } else {
            this.mUninstallIconRes = R.drawable.miuix_appcompat_action_button_delete_light;
            this.mUninstallTextRes = R.string.uninstall_text;
        }
        updateUnintallItem(((AppOpsUtils.isXOptMode() && !z2) || !this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) ? z : false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initiateClearUserData() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null || appEntry.info == null) {
            return;
        }
        this.mPrefClearData.setEnabled(false);
        String str = this.mAppEntry.info.packageName;
        Log.i("InstalledAppDetails", "Clearing user data for package : " + str);
        if (this.mClearDataObserver == null) {
            this.mClearDataObserver = new ClearUserDataObserver();
        }
        try {
            ActivityManagerNative.getDefault().clearApplicationUserData(str, true, this.mClearDataObserver, this.mUserId);
            this.mPrefClearData.setText(R.string.recompute_size);
            AppOpsManager appOpsManager = this.mAppOpsManager;
            ApplicationInfo applicationInfo = this.mAppEntry.info;
            appOpsManager.setMode(50, applicationInfo.uid, applicationInfo.packageName, 1);
        } catch (RemoteException e) {
            Log.e("InstalledAppDetails", "Couldnt clear application user data for package:" + str, e);
            showDialogInner(4, 0);
        }
    }

    private boolean isSystemApp(Context context, String str) {
        if (context == null) {
            return false;
        }
        try {
            return (context.getPackageManager().getApplicationInfo(str, 0).flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("InstalledAppDetails", e.toString());
            return false;
        }
    }

    private boolean isThisASystemPackage() {
        if ("com.android.documentsui".equals(this.mPackageInfo.packageName)) {
            return false;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(ThemeResources.FRAMEWORK_PACKAGE, 64, this.mUserId);
            PackageInfo packageInfo2 = this.mPackageInfo;
            if (packageInfo2 != null) {
                Signature[] signatureArr = packageInfo2.signatures;
                if (signatureArr == null || !packageInfo.signatures[0].equals(signatureArr[0])) {
                    if (!this.mAlwaysEnabledAppList.contains(this.mPackageInfo.packageName)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (RemoteException unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setIntentAndFinish$0() {
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean noNeedToProtect() {
        ApplicationInfo applicationInfo;
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        return appEntry == null || (applicationInfo = appEntry.info) == null || (applicationInfo.flags & 1) != 0 || !PreloadedAppPolicy.isProtectedDataApp(getActivity(), this.mPackageInfo.packageName, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processClearMsg(Message message) {
        int i = message.arg1;
        String str = this.mAppEntry.info.packageName;
        this.mPrefClearData.setText(R.string.clear_user_data_text);
        if (i == 1) {
            Log.i("InstalledAppDetails", "Cleared user data for package : " + str);
            this.mState.requestSize(this.mAppEntry.info.packageName, this.mUserId);
        } else {
            this.mPrefClearData.setEnabled(true);
        }
        checkForceStop();
        if ("com.android.settings".equals(this.mAppEntry.info.packageName)) {
            forceStopPackage(this.mAppEntry.info.packageName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processMoveMsg(Message message) {
        int i = message.arg1;
        String str = this.mAppEntry.info.packageName;
        this.mMoveInProgress = false;
        if (i == -100) {
            Log.i("InstalledAppDetails", "Moved resources for " + str);
            this.mState.requestSize(this.mAppEntry.info.packageName, this.mUserId);
        } else {
            showDialogInner(6, i);
        }
        refreshUi();
    }

    private void refreshButtons() {
        if (this.mMoveInProgress) {
            updateUnintallItem(false);
            return;
        }
        initUninstallButtons();
        initDataButtons();
        initNotificationButton();
        initFloatingWindowButton();
    }

    private void refreshPermissions() {
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo == null || Build.IS_TABLET || !this.mShowMenus) {
            return;
        }
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        this.mPrefPermissionManage.setEnabled((applicationInfo.isSystemApp() || applicationInfo.uid == 1000) ? false : true);
    }

    private void refreshSizeInfo() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        long j = appEntry.size;
        if (j == -2 || j == -1) {
            this.mLastTotalSize = -1L;
            this.mLastCacheSize = -1L;
            this.mLastDataSize = -1L;
            this.mLastCodeSize = -1L;
            if (!this.mHaveSizes) {
                this.mPrefApplicationSize.setValue(this.mComputingStr.toString());
                this.mPrefDataSize.setValue(this.mComputingStr.toString());
                this.mPrefCacheSize.setValue(this.mComputingStr.toString());
                this.mPrefTotalSize.setValue(this.mComputingStr.toString());
            }
            this.mPrefClearData.setEnabled(false);
            this.mPrefClearCache.setEnabled(false);
            return;
        }
        this.mHaveSizes = true;
        long j2 = appEntry.codeSize;
        long j3 = appEntry.dataSize;
        if (Environment.isExternalStorageEmulated()) {
            ApplicationsState.AppEntry appEntry2 = this.mAppEntry;
            j2 += appEntry2.externalCodeSize;
            j3 += appEntry2.externalDataSize;
        }
        if (this.mLastCodeSize != j2) {
            this.mLastCodeSize = j2;
            this.mPrefApplicationSize.setValue(getSizeStr(j2));
        }
        if (this.mLastDataSize != j3) {
            this.mLastDataSize = j3;
            this.mPrefDataSize.setValue(getSizeStr(j3));
        }
        long j4 = this.mLastExternalCodeSize;
        long j5 = this.mAppEntry.externalCodeSize;
        if (j4 != j5) {
            this.mLastExternalCodeSize = j5;
            this.mPrefExternalCodeSize.setValue(getSizeStr(j5));
        }
        long j6 = this.mLastExternalDataSize;
        long j7 = this.mAppEntry.externalDataSize;
        if (j6 != j7) {
            this.mLastExternalDataSize = j7;
            this.mPrefExternalDataSize.setValue(getSizeStr(j7));
        }
        ApplicationsState.AppEntry appEntry3 = this.mAppEntry;
        long j8 = appEntry3.cacheSize + appEntry3.externalCacheSize;
        if (this.mLastCacheSize != j8) {
            this.mLastCacheSize = j8;
            this.mPrefCacheSize.setValue(getSizeStr(j8));
        }
        long j9 = this.mLastTotalSize;
        long j10 = this.mAppEntry.size;
        if (j9 != j10) {
            this.mLastTotalSize = j10;
            this.mPrefTotalSize.setValue(getSizeStr(j10));
        }
        ApplicationsState.AppEntry appEntry4 = this.mAppEntry;
        if (appEntry4.dataSize + appEntry4.externalDataSize <= 0 || !this.mCanClearData) {
            this.mPrefClearData.setEnabled(false);
        } else {
            this.mPrefClearData.setEnabled(true);
        }
        if (j8 <= 0) {
            this.mPrefClearCache.setEnabled(false);
            return;
        }
        this.mPrefClearCache.setEnabled(true);
        this.mPrefClearCache.setOnPreferenceClickListener(this);
    }

    private boolean refreshUi() {
        boolean z;
        boolean z2;
        if (this.mMoveInProgress) {
            return true;
        }
        boolean extraXspaceApp = getExtraXspaceApp();
        String extraPkgName = getExtraPkgName();
        int myUserId = extraXspaceApp ? 999 : UserHandle.myUserId();
        this.mUserId = myUserId;
        ApplicationsState.AppEntry entry = this.mState.getEntry(extraPkgName, myUserId);
        this.mAppEntry = entry;
        if (entry == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = this.mPackageManager.getPackageInfo(entry.info.packageName, 8768, this.mUserId);
            this.mPackageInfo = packageInfo;
            if (packageInfo == null) {
                return false;
            }
            ArrayList arrayList = new ArrayList();
            try {
                this.mPackageManager.getPreferredActivities(new ArrayList(), arrayList, extraPkgName);
                try {
                    z = this.mUsbManager.hasDefaults(extraPkgName, this.mUserId);
                } catch (RemoteException e) {
                    Log.e("InstalledAppDetails", "mUsbManager.hasDefaults", e);
                    z = false;
                }
                try {
                    z2 = this.mAppWidgetManager.hasBindAppWidgetPermission(this.mAppEntry.info.packageName);
                } catch (Exception e2) {
                    e2.printStackTrace();
                    z2 = false;
                }
                boolean z3 = arrayList.size() > 0 || z;
                CharSequence charSequence = null;
                if (z3 || z2) {
                    boolean z4 = z2 && z3;
                    int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.installed_app_details_bullet_offset);
                    if (z3) {
                        CharSequence text = getText(R.string.auto_launch_enable_text);
                        SpannableString spannableString = new SpannableString(text);
                        if (z4) {
                            spannableString.setSpan(new BulletSpan(dimensionPixelSize), 0, text.length(), 0);
                        }
                        charSequence = TextUtils.concat(spannableString, "\n");
                    }
                    if (z2) {
                        CharSequence text2 = getText(R.string.always_allow_bind_appwidgets_text);
                        SpannableString spannableString2 = new SpannableString(text2);
                        if (z4) {
                            spannableString2.setSpan(new BulletSpan(dimensionPixelSize), 0, text2.length(), 0);
                        }
                        charSequence = charSequence == null ? TextUtils.concat(spannableString2, "\n") : TextUtils.concat(charSequence, "\n", spannableString2, "\n");
                    }
                    if (this.mShowMenus) {
                        this.mPrefClearDefaultSettings.setSummary(charSequence);
                        this.mPrefClearDefaultSettings.setEnabled(true);
                        this.mPrefClearDefaultSettings.setOnPreferenceClickListener(this);
                    }
                } else {
                    resetLaunchDefaultsUi(null, null);
                }
                checkForceStop();
                setAppLabelAndIcon(this.mPackageInfo);
                refreshButtons();
                refreshSizeInfo();
                refreshPermissions();
                return true;
            } catch (RemoteException e3) {
                Log.e("InstalledAppDetails", "Exception when getPreferredActivities for package:" + this.mAppEntry.info.packageName, e3);
                return false;
            }
        } catch (RemoteException e4) {
            Log.e("InstalledAppDetails", "Exception when retrieving package:" + this.mAppEntry.info.packageName, e4);
            return false;
        }
    }

    private void resetLaunchDefaultsUi(TextView textView, TextView textView2) {
        Preference preference = this.mPrefClearDefaultSettings;
        if (preference == null) {
            return;
        }
        preference.setSummary(R.string.auto_launch_disable_text);
        this.mPrefClearDefaultSettings.setEnabled(false);
    }

    private void setAppLabelAndIcon(PackageInfo packageInfo) {
        String str;
        boolean z = true;
        if (packageInfo == null || packageInfo.versionName == null) {
            str = null;
            z = false;
        } else {
            str = getActivity().getString(R.string.version_text, new Object[]{String.valueOf(packageInfo.versionName)});
        }
        this.mPrefAppInfo.setAppInfo(new AppInfoPreference.AppInfo(this.mAppEntry.getIcon(getActivity()), this.mAppEntry.label, str, z));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFloatingWindowEnabled(boolean z) {
        AppOpsManager appOpsManager = this.mAppOpsManager;
        ApplicationInfo applicationInfo = this.mAppEntry.info;
        appOpsManager.setMode(24, applicationInfo.uid, applicationInfo.packageName, !z ? 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setIntentAndFinish(boolean z, boolean z2) {
        new Handler().post(new Runnable() { // from class: com.android.settings.applications.InstalledAppDetailsFragment$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                InstalledAppDetailsFragment.this.lambda$setIntentAndFinish$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialogInner(int i, int i2) {
        showDialogInner(i, i2, "", "");
    }

    private void showDialogInner(int i, int i2, String str, String str2) {
        if (this.mAppEntry == null) {
            return;
        }
        MyAlertDialogFragment newInstance = MyAlertDialogFragment.newInstance(i, i2, str, str2);
        newInstance.setTargetFragment(this, 0);
        try {
            newInstance.show(getFragmentManager(), "dialog " + i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean startAppManagerDetailActivity() {
        if (!AppOpsUtils.isXOptMode() && this.mShowMenus) {
            String extraPkgName = getExtraPkgName();
            Intent intent = new Intent("miui.intent.action.APP_MANAGER_APPLICATION_DETAIL");
            intent.setPackage(SecuritySettingsTree.SECURITY_CENTER_PACKAGE_NAME);
            intent.putExtra(ExtraNetwork.FIREWALL_PACKAGE_NAME, extraPkgName);
            Intent intent2 = getActivity().getIntent();
            intent.putExtra("miui.intent.extra.USER_ID", intent2.hasExtra("miui.intent.extra.USER_ID") ? intent2.getIntExtra("miui.intent.extra.USER_ID", UserHandle.myUserId()) : intent2.getBooleanExtra("is_xspace_app", false) ? 999 : UserHandle.myUserId());
            PackageManager packageManager = getPackageManager();
            if (packageManager != null && intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 0);
                finish();
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uninstallPkg(String str, int i) {
        Log.v("InstalledAppDetails", "uninstall package, package = " + str + ", user id = " + i);
        PackageManager packageManager = getPackageManager();
        if (packageManager == null) {
            Log.e("InstalledAppDetails", "uninstall package, pm is null.");
            return;
        }
        if (XSpaceUserHandle.isXSpaceUserId(i)) {
            try {
                packageManager.deletePackageAsUser(str, new DeleteObserver(), 0, i);
                return;
            } catch (Exception e) {
                Log.e("InstalledAppDetails", "Error when uninstall package, " + e);
                return;
            }
        }
        try {
            packageManager.deletePackageAsUser(str, new DeleteObserver(), 0, i);
            if (hasRelativeXSpaceApp(str)) {
                packageManager.deletePackageAsUser(str, null, 0, 999);
            }
        } catch (Exception e2) {
            Log.e("InstalledAppDetails", "Error when uninstall package, " + e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateForceStopButton(boolean z) {
        this.mFinishEnable = z;
        MenuItem menuItem = this.mItemFinish;
        if (menuItem != null) {
            menuItem.setEnabled(z);
        }
    }

    private void updateUnintallItem(boolean z) {
        if ((z && this.mMiHomeManager.isWhiteListPackage(this.mPackageInfo.packageName)) || (Build.IS_GLOBAL_BUILD && "com.amazon.appmanager".equals(this.mPackageInfo.packageName))) {
            z = false;
        }
        this.mUninstallEnable = z;
        MenuItem menuItem = this.mItemUninstall;
        if (menuItem != null) {
            menuItem.setEnabled(z);
            this.mItemUninstall.setTitle(this.mUninstallTextRes);
            this.mItemUninstall.setIcon(this.mUninstallIconRes);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return InstalledAppDetailsFragment.class.getName();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mShowMenus = getActivity().getIntent().getBooleanExtra("show_menus", true);
        if (startAppManagerDetailActivity()) {
            return;
        }
        this.mUninstallTextRes = R.string.uninstall_text;
        this.mUninstallIconRes = R.drawable.action_button_stop;
        this.mComputingStr = getActivity().getText(R.string.computing_size);
        String[] stringArray = getActivity().getResources().getStringArray(R.array.always_enabled_app_list);
        this.mAlwaysEnabledAppList = new HashSet<>(stringArray.length);
        for (String str : stringArray) {
            this.mAlwaysEnabledAppList.add(str);
        }
        this.mMiHomeManager = MiHomeManager.getInstance(getActivity());
        ApplicationsState applicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        this.mState = applicationsState;
        this.mSession = applicationsState.newSession(this);
        this.mPackageManager = IPackageManager.Stub.asInterface(ServiceManager.getService(FunctionColumns.PACKAGE));
        this.mAppOpsManager = (AppOpsManager) getActivity().getSystemService("appops");
        this.mUsbManager = IUsbManager.Stub.asInterface(ServiceManager.getService("usb"));
        this.mAppWidgetManager = AppWidgetManager.getInstance(getActivity());
        this.mDpm = (DevicePolicyManager) getActivity().getSystemService("device_policy");
        boolean z = Build.IS_GLOBAL_BUILD;
        addPreferencesFromResource(z ? R.xml.installed_app_details_fragment_global : R.xml.installed_app_details_fragment);
        AppInfoPreference appInfoPreference = (AppInfoPreference) findPreference("applications_info");
        this.mPrefAppInfo = appInfoPreference;
        appInfoPreference.setSelectable(false);
        this.mPrefTotalSize = (ValuePreference) findPreference("total_size");
        this.mPrefApplicationSize = (ValuePreference) findPreference("application_size");
        this.mPrefExternalCodeSize = (ValuePreference) findPreference("external_code_size");
        this.mPrefDataSize = (ValuePreference) findPreference("data_size");
        this.mPrefExternalDataSize = (ValuePreference) findPreference("external_data_size");
        this.mPrefCacheSize = (ValuePreference) findPreference("cache_size");
        this.mPrefClearData = (ButtonPreference) findPreference("clear_data");
        ButtonPreference buttonPreference = (ButtonPreference) findPreference("clear_cache");
        this.mPrefClearCache = buttonPreference;
        buttonPreference.setText(R.string.clear_cache_btn_text);
        this.mPrefNotificationSwitch = (ValuePreference) findPreference("notification_switch");
        this.mPrefFloatingWindowSwitch = (CheckBoxPreference) findPreference("floating_window_switch");
        this.mPrefClearDefaultSettings = findPreference("clear_default_settings");
        Preference findPreference = findPreference("permission_detail_info");
        this.mPrefPermissionDetailInfo = findPreference;
        findPreference.setOnPreferenceClickListener(this);
        this.mPrefPermissionManage = findPreference("permission_manage");
        this.mPrefPermissionSettings = findPreference("permission_settings");
        PreferenceCategory preferenceCategory = (PreferenceCategory) findPreference("notification_label");
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) findPreference("permissions_label");
        PreferenceCategory preferenceCategory3 = (PreferenceCategory) findPreference("auto_launch_label");
        if (!this.mShowMenus) {
            getPreferenceScreen().removePreference(this.mPrefFloatingWindowSwitch);
            this.mPrefFloatingWindowSwitch = null;
            getPreferenceScreen().removePreference(this.mPrefNotificationSwitch);
            this.mPrefNotificationSwitch = null;
            getPreferenceScreen().removePreference(preferenceCategory);
            getPreferenceScreen().removePreference(preferenceCategory3);
            getPreferenceScreen().removePreference(preferenceCategory2);
            setHasOptionsMenu(false);
            return;
        }
        if (!Build.IS_TABLET) {
            getPreferenceScreen().removePreference(this.mPrefFloatingWindowSwitch);
            this.mPrefFloatingWindowSwitch = null;
        }
        if (z) {
            this.mPrefPermissionSettings.setOnPreferenceClickListener(this);
            Preference preference = this.mPrefPermissionManage;
            if (preference != null) {
                preference.setTitle(R.string.permission_manage_global_build_title);
            }
        } else {
            preferenceCategory2.removePreference(this.mPrefPermissionSettings);
            this.mPrefPermissionSettings = null;
        }
        handlePermissonManagerPreference(preferenceCategory2);
        this.mPrefPermissionDetailInfo.setOnPreferenceClickListener(this);
        this.mPrefPermissionManage.setOnPreferenceClickListener(this);
        setHasOptionsMenu(true);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        MenuItem add = menu.add(0, 1, 0, R.string.finish_application);
        this.mItemFinish = add;
        add.setIcon(R.drawable.action_button_discard);
        this.mItemFinish.setEnabled(this.mFinishEnable);
        this.mItemFinish.setShowAsAction(1);
        MenuItem add2 = menu.add(0, 2, 0, this.mUninstallTextRes);
        this.mItemUninstall = add2;
        add2.setIcon(this.mUninstallIconRes);
        this.mItemUninstall.setEnabled(this.mUninstallEnable);
        this.mItemUninstall.setShowAsAction(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onDetach() {
        super.onDetach();
        ApplicationsState.Session session = this.mSession;
        if (session != null) {
            session.onDestroy();
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        CharSequence charSequence;
        Bundle bundle;
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            showDialogInner(5, 0);
        } else if (itemId == 2) {
            if (AppOpsUtils.isXOptMode() && this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
                String str = this.mAppEntry.info.packageName;
                FragmentActivity activity = getActivity();
                Intent intent = new Intent(activity, DeviceAdminAdd.class);
                intent.putExtra("android.app.extra.DEVICE_ADMIN_PACKAGE_NAME", str);
                activity.startActivity(intent);
                return true;
            } else if (this.mUpdatedSysApp) {
                showDialogInner(2, 0);
            } else {
                ApplicationsState.AppEntry appEntry = this.mAppEntry;
                CharSequence charSequence2 = null;
                if (appEntry != null) {
                    ApplicationInfo applicationInfo = appEntry.info;
                    if ((applicationInfo.flags & 1) != 0) {
                        if (applicationInfo.enabled) {
                            showDialogInner(7, 0);
                        } else {
                            new DisableChanger(this, this.mAppEntry.info, 0).execute(null);
                        }
                    }
                }
                if (noNeedToProtect()) {
                    showDialogInner(10, 0);
                } else {
                    ApplicationInfo applicationInfo2 = this.mAppEntry.info;
                    if (applicationInfo2 != null && (bundle = applicationInfo2.metaData) != null) {
                        int i = bundle.getInt("app_description_title");
                        int i2 = this.mAppEntry.info.metaData.getInt("app_description_content");
                        if (i != 0 && i2 != 0) {
                            PackageManager packageManager = getPackageManager();
                            ApplicationInfo applicationInfo3 = this.mAppEntry.info;
                            charSequence2 = packageManager.getText(applicationInfo3.packageName, i, applicationInfo3);
                            PackageManager packageManager2 = getPackageManager();
                            ApplicationInfo applicationInfo4 = this.mAppEntry.info;
                            charSequence = packageManager2.getText(applicationInfo4.packageName, i2, applicationInfo4);
                            if (!TextUtils.isEmpty(charSequence2) || TextUtils.isEmpty(charSequence)) {
                                showDialogInner(10, 0);
                            } else {
                                showDialogInner(11, 0, charSequence2.toString(), charSequence.toString());
                            }
                        }
                    }
                    charSequence = null;
                    if (TextUtils.isEmpty(charSequence2)) {
                    }
                    showDialogInner(10, 0);
                }
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        refreshUi();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
        if (this.mAppEntry == null || TextUtils.isEmpty(str) || !str.equals(this.mAppEntry.info.packageName)) {
            return;
        }
        refreshSizeInfo();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        ApplicationsState.Session session = this.mSession;
        if (session != null) {
            session.onPause();
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (this.mAppEntry != null && preference.getKey().equals("floating_window_switch")) {
            showDialogInner(9, 0);
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null) {
            return false;
        }
        String str = appEntry.info.packageName;
        String key = preference.getKey();
        ApplicationInfo applicationInfo = null;
        if (key.equals("notification_switch")) {
            if (TextUtils.isEmpty(str)) {
                return false;
            }
            try {
                applicationInfo = getPackageManager().getApplicationInfo(str, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (applicationInfo == null) {
                return false;
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.MAIN");
            intent.setClassName("com.android.settings", "com.android.settings.Settings$NotificationFilterActivity");
            intent.putExtra("appName", this.mAppEntry.label);
            intent.putExtra("packageName", str);
            intent.putExtra(":miui:starting_window_label", this.mAppEntry.label);
            startActivity(intent);
        } else if (key.equals("clear_data")) {
            if (this.mAppEntry.info.manageSpaceActivityName == null) {
                showDialogInner(1, 0);
            } else if (!Utils.isMonkeyRunning()) {
                Intent intent2 = new Intent("android.intent.action.VIEW");
                ApplicationInfo applicationInfo2 = this.mAppEntry.info;
                intent2.setClassName(applicationInfo2.packageName, applicationInfo2.manageSpaceActivityName);
                if (getActivity() != null) {
                    getActivity().startActivityForResultAsUser(intent2, -1, new UserHandle(this.mUserId));
                }
            }
        } else if (key.equals("clear_cache")) {
            if (this.mClearCacheObserver == null) {
                this.mClearCacheObserver = new ClearCacheObserver();
            }
            try {
                this.mPackageManager.deleteApplicationCacheFilesAsUser(this.mAppEntry.info.packageName, this.mUserId, this.mClearCacheObserver);
            } catch (RemoteException e2) {
                Log.e("InstalledAppDetails", "Can not clear cache for pkg,", e2);
            }
        } else if (key.equals("clear_default_settings")) {
            try {
                this.mPackageManager.clearPackagePreferredActivities(str);
                this.mUsbManager.clearDefaults(str, this.mUserId);
            } catch (RemoteException e3) {
                Log.e("InstalledAppDetails", "mUsbManager.clearDefaults", e3);
            }
            this.mAppWidgetManager.setBindAppWidgetPermission(str, false);
            resetLaunchDefaultsUi(null, null);
        } else if (key.equals("permission_detail_info")) {
            if (Build.IS_TABLET) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("extra_package_application", this.mAppEntry.info);
                startFragment(this, PermissionInfoFragment.class.getName(), 0, bundle, 0);
            } else {
                Intent intent3 = new Intent(getActivity(), PermissionInfoActivity.class);
                intent3.putExtra("extra_package_application", this.mAppEntry.info);
                startActivity(intent3);
            }
        } else if (key.equals("permission_manage")) {
            try {
                Intent intent4 = new Intent();
                if (AppOpsUtils.isXOptMode()) {
                    intent4.setAction("android.intent.action.MANAGE_APP_PERMISSIONS");
                    intent4.putExtra("android.intent.extra.PACKAGE_NAME", str);
                } else {
                    intent4.setAction("miui.intent.action.APP_PERM_EDITOR_PRIVATE");
                    intent4.putExtra("extra_pkgname", str);
                }
                startActivity(intent4);
            } catch (Exception e4) {
                Log.e("InstalledAppDetails", "No app can handle MANAGE_APP_PERMISSIONS or APP_PERM_EDITOR_PRIVATE", e4);
            }
        } else if (key.equals("permission_settings")) {
            Intent intent5 = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent5.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppEntry.info.packageName);
            try {
                startActivity(intent5);
            } catch (Exception e5) {
                Log.e("InstalledAppDetails", "No app can handle android.intent.action.MANAGE_APP_PERMISSIONS", e5);
            }
        }
        return false;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mSession.onResume();
        if (!refreshUi()) {
            setIntentAndFinish(true, true);
        } else if (this.mPrefPermissionDetailInfo == null) {
        } else {
            PermissionInfoFragment.PermissionSet parsePermission = PermissionInfoFragment.parsePermission(this.mAppEntry.info.uid, getActivity());
            this.mPrefPermissionDetailInfo.setSummary(String.format(getActivity().getString(R.string.permission_detail_info_summary), Integer.valueOf(parsePermission.getSecurityCount()), Integer.valueOf(parsePermission.getPrivacyCount()), Integer.valueOf(parsePermission.getOtherCount())));
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }
}
