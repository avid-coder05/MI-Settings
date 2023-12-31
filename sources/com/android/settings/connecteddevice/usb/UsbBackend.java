package com.android.settings.connecteddevice.usb;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbPort;
import android.hardware.usb.UsbPortStatus;
import android.net.TetheringManager;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.List;

/* loaded from: classes.dex */
public class UsbBackend {
    private static TetheringManager sTM;
    private final boolean mFileTransferRestricted;
    private final boolean mFileTransferRestrictedBySystem;
    private final boolean mIsAdminUser;
    private final boolean mMidiSupported;
    private UsbPort mPort;
    private UsbPortStatus mPortStatus;
    private final boolean mTetheringRestricted;
    private final boolean mTetheringRestrictedBySystem;
    private final boolean mTetheringSupported;
    private UsbManager mUsbManager;

    public UsbBackend(Context context) {
        this(context, (UserManager) context.getSystemService("user"));
    }

    public UsbBackend(Context context, UserManager userManager) {
        this.mUsbManager = (UsbManager) context.getSystemService(UsbManager.class);
        this.mFileTransferRestricted = isUsbFileTransferRestricted(userManager);
        this.mFileTransferRestrictedBySystem = isUsbFileTransferRestrictedBySystem(userManager);
        this.mTetheringRestricted = isUsbTetheringRestricted(userManager);
        this.mTetheringRestrictedBySystem = isUsbTetheringRestrictedBySystem(userManager);
        this.mIsAdminUser = userManager.isAdminUser();
        this.mMidiSupported = context.getPackageManager().hasSystemFeature("android.software.midi");
        if (sTM == null) {
            sTM = (TetheringManager) context.getSystemService(TetheringManager.class);
        }
        this.mTetheringSupported = sTM.isTetheringSupported();
        updatePorts();
    }

    private boolean areFunctionDisallowed(long j) {
        return (this.mFileTransferRestricted && !((4 & j) == 0 && (16 & j) == 0)) || (this.mTetheringRestricted && (j & 32) != 0);
    }

    private boolean areFunctionsDisallowedBySystem(long j) {
        return (this.mFileTransferRestrictedBySystem && !((4 & j) == 0 && (16 & j) == 0)) || (this.mTetheringRestrictedBySystem && (j & 32) != 0);
    }

    public static int dataRoleFromString(String str) {
        return Integer.parseInt(str);
    }

    public static String dataRoleToString(int i) {
        return Integer.toString(i);
    }

    private static boolean isUsbFileTransferRestricted(UserManager userManager) {
        return userManager.hasUserRestriction("no_usb_file_transfer");
    }

    private static boolean isUsbFileTransferRestrictedBySystem(UserManager userManager) {
        return userManager.hasBaseUserRestriction("no_usb_file_transfer", UserHandle.of(UserHandle.myUserId()));
    }

    private static boolean isUsbTetheringRestricted(UserManager userManager) {
        return userManager.hasUserRestriction("no_config_tethering");
    }

    private static boolean isUsbTetheringRestrictedBySystem(UserManager userManager) {
        return userManager.hasBaseUserRestriction("no_config_tethering", UserHandle.of(UserHandle.myUserId()));
    }

    private void updatePorts() {
        this.mPort = null;
        this.mPortStatus = null;
        List ports = this.mUsbManager.getPorts();
        int size = ports.size();
        for (int i = 0; i < size; i++) {
            UsbPortStatus status = ((UsbPort) ports.get(i)).getStatus();
            if (status.isConnected()) {
                this.mPort = (UsbPort) ports.get(i);
                this.mPortStatus = status;
                return;
            }
        }
    }

    public static long usbFunctionsFromString(String str) {
        return Long.parseLong(str, 2);
    }

    public static String usbFunctionsToString(long j) {
        return Long.toBinaryString(j);
    }

    public boolean areAllRolesSupported() {
        UsbPortStatus usbPortStatus;
        return this.mPort != null && (usbPortStatus = this.mPortStatus) != null && usbPortStatus.isRoleCombinationSupported(2, 2) && this.mPortStatus.isRoleCombinationSupported(2, 1) && this.mPortStatus.isRoleCombinationSupported(1, 2) && this.mPortStatus.isRoleCombinationSupported(1, 1);
    }

    boolean areFunctionsDisallowedByNonAdminUser(long j) {
        return (this.mIsAdminUser || (j & 32) == 0) ? false : true;
    }

    public boolean areFunctionsSupported(long j) {
        return (this.mMidiSupported || (8 & j) == 0) && !((!this.mTetheringSupported && (32 & j) != 0) || areFunctionDisallowed(j) || areFunctionsDisallowedBySystem(j) || areFunctionsDisallowedByNonAdminUser(j));
    }

    public long getCurrentFunctions() {
        return this.mUsbManager.getCurrentFunctions();
    }

    public int getDataRole() {
        updatePorts();
        UsbPortStatus usbPortStatus = this.mPortStatus;
        if (usbPortStatus == null) {
            return 0;
        }
        return usbPortStatus.getCurrentDataRole();
    }

    public long getDefaultUsbFunctions() {
        return this.mUsbManager.getScreenUnlockedFunctions();
    }

    public int getPowerRole() {
        updatePorts();
        UsbPortStatus usbPortStatus = this.mPortStatus;
        if (usbPortStatus == null) {
            return 0;
        }
        return usbPortStatus.getCurrentPowerRole();
    }

    public void setCurrentFunctions(long j) {
        this.mUsbManager.setCurrentFunctions(j);
    }

    public void setDataRole(int i) {
        int powerRole = getPowerRole();
        if (!areAllRolesSupported()) {
            powerRole = i != 1 ? i != 2 ? 0 : 2 : 1;
        }
        UsbPort usbPort = this.mPort;
        if (usbPort != null) {
            usbPort.setRoles(powerRole, i);
        }
    }

    public void setDefaultUsbFunctions(long j) {
        this.mUsbManager.setScreenUnlockedFunctions(j);
    }

    public void setPowerRole(int i) {
        int dataRole = getDataRole();
        if (!areAllRolesSupported()) {
            dataRole = i != 1 ? i != 2 ? 0 : 2 : 1;
        }
        UsbPort usbPort = this.mPort;
        if (usbPort != null) {
            usbPort.setRoles(i, dataRole);
        }
    }
}
