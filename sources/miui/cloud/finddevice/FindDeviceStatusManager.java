package miui.cloud.finddevice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/* loaded from: classes3.dex */
public class FindDeviceStatusManager implements ServiceConnection {
    public static final String DEBT_LOCK_USER_ID = "1665249865";
    public static final String LAST_STATUS_CHANGED_ACTION = "com.xiaomi.finddevice.action.LAST_STATUS_CHANGED";
    public static final String LOCK_SYS_SETTING = "com.xiaomi.system.devicelock.locked";
    public static final Intent OPEN_WITH_UI_INTENT = null;
    public static final int OPEN_WITH_UI_RESULT_CANCELED = 0;
    public static final int OPEN_WITH_UI_RESULT_ERROR = 2;
    public static final int OPEN_WITH_UI_RESULT_OK = -1;

    /* loaded from: classes3.dex */
    public static class FindDeviceStatusManagerException extends Exception {
        public int errno;

        public FindDeviceStatusManagerException(int i) {
            throw new RuntimeException("Stub!");
        }

        public FindDeviceStatusManagerException(String str, int i) {
            throw new RuntimeException("Stub!");
        }

        public FindDeviceStatusManagerException(String str, Throwable th, int i) {
            throw new RuntimeException("Stub!");
        }

        public FindDeviceStatusManagerException(Throwable th, int i) {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class FindDeviceStatusManagerOperationFailedException extends Exception {
        public int code;

        public FindDeviceStatusManagerOperationFailedException(int i) {
            throw new RuntimeException("Stub!");
        }

        public FindDeviceStatusManagerOperationFailedException(String str, int i) {
            throw new RuntimeException("Stub!");
        }
    }

    FindDeviceStatusManager() {
        throw new RuntimeException("Stub!");
    }

    public static boolean isDebtLocked(FindDeviceInfo findDeviceInfo) {
        throw new RuntimeException("Stub!");
    }

    public static FindDeviceStatusManager obtain(Context context) {
        throw new RuntimeException("Stub!");
    }

    public static boolean shouldEnforceLockDevicePolicy(Context context) {
        throw new RuntimeException("Stub!");
    }

    public void asyncClose() {
        throw new RuntimeException("Stub!");
    }

    public void asyncOpen() {
        throw new RuntimeException("Stub!");
    }

    public void asyncOpen(boolean z) {
        throw new RuntimeException("Stub!");
    }

    public void close() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public void ft() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public FindDeviceInfo getFindDeviceInfo() throws InterruptedException, RemoteException {
        throw new RuntimeException("Stub!");
    }

    public FindDeviceInfo getFindDeviceInfoFromServer() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public FindDeviceInfoWithLockMessage getFindDeviceInfoWithLockMessageFromServer() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public boolean isLocked() throws InterruptedException, RemoteException {
        throw new RuntimeException("Stub!");
    }

    public boolean isOpen() throws InterruptedException, RemoteException {
        throw new RuntimeException("Stub!");
    }

    @Override // android.content.ServiceConnection
    public synchronized void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        throw new RuntimeException("Stub!");
    }

    @Override // android.content.ServiceConnection
    public synchronized void onServiceDisconnected(ComponentName componentName) {
        throw new RuntimeException("Stub!");
    }

    public void open() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public void openSilently() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public void openWithUI(Activity activity, int i) {
        throw new RuntimeException("Stub!");
    }

    public void purge() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public synchronized void release() {
        throw new RuntimeException("Stub!");
    }

    public void unlock() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }

    public void withdraw() throws InterruptedException, RemoteException, FindDeviceStatusManagerException {
        throw new RuntimeException("Stub!");
    }
}
