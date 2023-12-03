package miui.cloud.app;

import android.content.Context;

/* loaded from: classes3.dex */
public class ExistingPackageInstaller {

    /* loaded from: classes3.dex */
    public static class InstallPackageFailedException extends Exception {
        public InstallPackageFailedException(String str) {
            throw new RuntimeException("Stub!");
        }
    }

    /* loaded from: classes3.dex */
    public static class InstallPackageNotFoundException extends Exception {
        public InstallPackageNotFoundException(Throwable th) {
            throw new RuntimeException("Stub!");
        }
    }

    public ExistingPackageInstaller() {
        throw new RuntimeException("Stub!");
    }

    public static void installExistingPackage(Context context, String str) throws InstallPackageFailedException, InstallPackageNotFoundException {
        throw new RuntimeException("Stub!");
    }
}
