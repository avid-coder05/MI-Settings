package miuix.os;

import java.io.File;
import miui.yellowpage.YellowPageContract;

/* loaded from: classes5.dex */
public class Environment extends android.os.Environment {
    private static File EXTERNAL_STORAGE_MIUI_DIRECTORY;
    private static final File MIUI_DATA_DIRECTORY = new File("/data/miui/");
    private static final File MIUI_APP_DIRECTORY = new File(getMiuiDataDirectory(), "apps");
    private static final File MIUI_PRESET_APP_DIRECTORY = new File(getMiuiDataDirectory(), "preset_apps");
    private static final File MIUI_CUSTOMIZED_DIRECTORY = new File(getMiuiDataDirectory(), "current");
    private static int sCpuCount = 0;

    public static File getExternalStorageMiuiDirectory() {
        try {
            if (EXTERNAL_STORAGE_MIUI_DIRECTORY == null) {
                EXTERNAL_STORAGE_MIUI_DIRECTORY = new File(android.os.Environment.getExternalStorageDirectory(), YellowPageContract.Provider.PNAME_DEFAULT);
            }
            if (!EXTERNAL_STORAGE_MIUI_DIRECTORY.exists() && android.os.Environment.getExternalStorageDirectory().exists()) {
                EXTERNAL_STORAGE_MIUI_DIRECTORY.mkdir();
            }
            return EXTERNAL_STORAGE_MIUI_DIRECTORY;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getMiuiDataDirectory() {
        return MIUI_DATA_DIRECTORY;
    }
}
