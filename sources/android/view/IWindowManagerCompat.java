package android.view;

import android.os.RemoteException;

/* loaded from: classes.dex */
public class IWindowManagerCompat {
    public static boolean hasNavigationBar(IWindowManager iWindowManager, int i) throws RemoteException {
        return iWindowManager.hasNavigationBar(i);
    }
}
