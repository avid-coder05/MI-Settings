package com.android.settings.vpn2;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.LegacyVpnProfileStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnProfile;
import com.android.settings.R;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.vpn2.ConfirmLockdownFragment;
import miui.yellowpage.YellowPageContract;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class ConfigDialogFragment extends InstrumentedDialogFragment implements DialogInterface.OnClickListener, DialogInterface.OnShowListener, View.OnClickListener, ConfirmLockdownFragment.ConfirmLockdownListener {
    private Context mContext;
    private android.net.VpnManager mService;

    private void connect(VpnProfile vpnProfile, boolean z) {
        save(vpnProfile, z);
        if (VpnUtils.isVpnLockdown(vpnProfile.key)) {
            return;
        }
        VpnUtils.clearLockdownVpn(this.mContext);
        try {
            this.mService.startLegacyVpn(vpnProfile);
        } catch (IllegalStateException unused) {
            Toast.makeText(this.mContext, R.string.vpn_no_network, 1).show();
        }
    }

    private boolean disconnect(VpnProfile vpnProfile) {
        try {
            if (isConnected(vpnProfile)) {
                return VpnUtils.disconnectLegacyVpn(getContext());
            }
            return true;
        } catch (RemoteException e) {
            Log.e("ConfigDialogFragment", "Failed to disconnect", e);
            return false;
        }
    }

    private boolean isConnected(VpnProfile vpnProfile) throws RemoteException {
        LegacyVpnInfo legacyVpnInfo = this.mService.getLegacyVpnInfo(UserHandle.myUserId());
        return legacyVpnInfo != null && vpnProfile.key.equals(legacyVpnInfo.key);
    }

    private void save(VpnProfile vpnProfile, boolean z) {
        LegacyVpnProfileStore.put("VPN_" + vpnProfile.key, vpnProfile.encode());
        disconnect(vpnProfile);
        updateLockdownVpn(z, vpnProfile);
    }

    public static void show(VpnSettings vpnSettings, VpnProfile vpnProfile, boolean z, boolean z2) {
        if (vpnSettings.isAdded()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(YellowPageContract.Profile.DIRECTORY, vpnProfile);
            bundle.putBoolean("editing", z);
            bundle.putBoolean("exists", z2);
            ConfigDialogFragment configDialogFragment = new ConfigDialogFragment();
            configDialogFragment.setArguments(bundle);
            configDialogFragment.setTargetFragment(vpnSettings, 0);
            configDialogFragment.show(vpnSettings.getFragmentManager(), "vpnconfigdialog");
        }
    }

    private void updateLockdownVpn(boolean z, VpnProfile vpnProfile) {
        if (!z) {
            if (VpnUtils.isVpnLockdown(vpnProfile.key)) {
                VpnUtils.clearLockdownVpn(this.mContext);
            }
        } else if (!vpnProfile.isValidLockdownProfile()) {
            Toast.makeText(this.mContext, R.string.vpn_lockdown_config_error, 1).show();
        } else {
            this.mService.setAlwaysOnVpnPackageForUser(UserHandle.myUserId(), null, false, null);
            VpnUtils.setLockdownVpn(this.mContext, vpnProfile.key);
        }
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 545;
    }

    @Override // com.android.settings.core.instrumentation.InstrumentedDialogFragment, com.android.settingslib.core.lifecycle.ObservableDialogFragment, androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
        this.mService = (android.net.VpnManager) context.getSystemService(android.net.VpnManager.class);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(DialogInterface dialogInterface) {
        dismiss();
        super.onCancel(dialogInterface);
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        ConfigDialog configDialog = (ConfigDialog) getDialog();
        if (configDialog == null) {
            Log.e("ConfigDialogFragment", "ConfigDialog object is null");
            return;
        }
        Parcelable profile = configDialog.getProfile();
        if (i == -1) {
            boolean isVpnAlwaysOn = configDialog.isVpnAlwaysOn();
            boolean z = isVpnAlwaysOn || !configDialog.isEditing();
            boolean isAnyLockdownActive = VpnUtils.isAnyLockdownActive(this.mContext);
            try {
                boolean isVpnActive = VpnUtils.isVpnActive(this.mContext);
                if (z && !isConnected(profile) && ConfirmLockdownFragment.shouldShow(isVpnActive, isAnyLockdownActive, isVpnAlwaysOn)) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(YellowPageContract.Profile.DIRECTORY, profile);
                    ConfirmLockdownFragment.show(this, isVpnActive, isVpnAlwaysOn, isAnyLockdownActive, isVpnAlwaysOn, bundle);
                } else if (z) {
                    connect(profile, isVpnAlwaysOn);
                } else {
                    save(profile, false);
                }
            } catch (RemoteException e) {
                Log.w("ConfigDialogFragment", "Failed to check active VPN state. Skipping.", e);
            }
        } else if (i == -3) {
            if (!disconnect(profile)) {
                Log.e("ConfigDialogFragment", "Failed to disconnect VPN. Leaving profile in keystore.");
                return;
            }
            LegacyVpnProfileStore.remove("VPN_" + ((VpnProfile) profile).key);
            updateLockdownVpn(false, profile);
        }
        dismiss();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        onClick(getDialog(), -1);
    }

    @Override // com.android.settings.vpn2.ConfirmLockdownFragment.ConfirmLockdownListener
    public void onConfirmLockdown(Bundle bundle, boolean z, boolean z2) {
        connect((VpnProfile) bundle.getParcelable(YellowPageContract.Profile.DIRECTORY), z);
        dismiss();
    }

    @Override // androidx.fragment.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        Bundle arguments = getArguments();
        ConfigDialog configDialog = new ConfigDialog(getActivity(), this, arguments.getParcelable(YellowPageContract.Profile.DIRECTORY), arguments.getBoolean("editing"), arguments.getBoolean("exists"));
        configDialog.setOnShowListener(this);
        return configDialog;
    }

    @Override // android.content.DialogInterface.OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        ((AlertDialog) getDialog()).getButton(-1).setOnClickListener(this);
    }
}
