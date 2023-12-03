package com.android.settings.vpn2;

import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import androidx.fragment.app.Fragment;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.search.FunctionColumns;
import com.android.settings.vpn2.AppDialog;
import miui.provider.ExtraContacts;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class AppDialogFragment extends InstrumentedDialogFragment implements AppDialog.Listener {
    private DevicePolicyManager mDevicePolicyManager;
    private Listener mListener;
    private PackageInfo mPackageInfo;
    private UserManager mUserManager;
    private android.net.VpnManager mVpnManager;

    /* loaded from: classes2.dex */
    public interface Listener {
        void onCancel();

        void onForget();
    }

    private int getUserId() {
        return UserHandle.getUserId(this.mPackageInfo.applicationInfo.uid);
    }

    private boolean isUiRestricted() {
        if (this.mUserManager.hasUserRestriction("no_config_vpn", UserHandle.of(getUserId()))) {
            return true;
        }
        return this.mPackageInfo.packageName.equals(this.mDevicePolicyManager.getAlwaysOnVpnPackage());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisconnect(DialogInterface dialogInterface) {
        if (isUiRestricted()) {
            return;
        }
        int userId = getUserId();
        if (this.mPackageInfo.packageName.equals(VpnUtils.getConnectedPackage(this.mVpnManager, userId))) {
            this.mVpnManager.setAlwaysOnVpnPackageForUser(userId, null, false, null);
            this.mVpnManager.prepareVpn(this.mPackageInfo.packageName, "[Legacy VPN]", userId);
        }
    }

    public static void show(Fragment fragment, PackageInfo packageInfo, String str, boolean z, boolean z2) {
        if (z || z2) {
            show(fragment, null, packageInfo, str, z, z2);
        }
    }

    public static void show(Fragment fragment, Listener listener, PackageInfo packageInfo, String str, boolean z, boolean z2) {
        if (fragment.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(FunctionColumns.PACKAGE, packageInfo);
            bundle.putString(ExtraContacts.ConferenceCalls.MembersColumns.LABEL, str);
            bundle.putBoolean("managing", z);
            bundle.putBoolean(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED, z2);
            AppDialogFragment appDialogFragment = new AppDialogFragment();
            appDialogFragment.mListener = listener;
            appDialogFragment.setArguments(bundle);
            appDialogFragment.setTargetFragment(fragment, 0);
            appDialogFragment.show(fragment.getFragmentManager(), "vpnappdialog");
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 546;
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        dismiss();
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onCancel();
        }
        super.onCancel(dialogInterface);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mPackageInfo = (PackageInfo) getArguments().getParcelable(FunctionColumns.PACKAGE);
        this.mUserManager = UserManager.get(getContext());
        this.mDevicePolicyManager = (DevicePolicyManager) getContext().createContextAsUser(UserHandle.of(getUserId()), 0).getSystemService(DevicePolicyManager.class);
        this.mVpnManager = (android.net.VpnManager) getContext().getSystemService(android.net.VpnManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        String string = arguments.getString(ExtraContacts.ConferenceCalls.MembersColumns.LABEL);
        boolean z = arguments.getBoolean("managing");
        boolean z2 = arguments.getBoolean(YellowPageContract.MipubPhoneEvent.EXTRA_DATA_CONNECTED);
        if (z) {
            return new AppDialog(getActivity(), this, this.mPackageInfo, string);
        }
        AlertDialog.Builder negativeButton = new AlertDialog.Builder(getActivity()).setTitle(string).setMessage(getActivity().getString(R.string.vpn_disconnect_confirm)).setNegativeButton(getActivity().getString(R.string.vpn_cancel), (DialogInterface.OnClickListener) null);
        if (z2 && !isUiRestricted()) {
            negativeButton.setPositiveButton(getActivity().getString(R.string.vpn_disconnect), new DialogInterface.OnClickListener() { // from class: com.android.settings.vpn2.AppDialogFragment.1
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    AppDialogFragment.this.onDisconnect(dialogInterface);
                }
            });
        }
        return negativeButton.create();
    }

    @Override // com.android.settings.vpn2.AppDialog.Listener
    public void onForget(DialogInterface dialogInterface) {
        if (isUiRestricted()) {
            return;
        }
        this.mVpnManager.setVpnPackageAuthorization(this.mPackageInfo.packageName, getUserId(), -1);
        onDisconnect(dialogInterface);
        Listener listener = this.mListener;
        if (listener != null) {
            listener.onForget();
        }
    }
}
