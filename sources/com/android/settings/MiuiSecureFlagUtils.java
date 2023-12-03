package com.android.settings;

import android.os.SystemProperties;
import android.util.ArraySet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/* loaded from: classes.dex */
public class MiuiSecureFlagUtils {
    private static final boolean IS_VAB_DEVICE;
    private static final String LAST_EXTENSION_PARAMETERS;
    private static final File LAST_EXTENSION_PARAMETERS_FILE;
    private static boolean deleteFlag;
    private static final ArraySet<String> paramsReadMap;
    private static ArraySet<String> paramsWriteMap;

    static {
        boolean z = SystemProperties.getBoolean("ro.build.ab_update", false);
        IS_VAB_DEVICE = z;
        String str = z ? "/mnt/rescue/recovery/last_extension_parameters" : "/cache/recovery/last_extension_parameters";
        LAST_EXTENSION_PARAMETERS = str;
        LAST_EXTENSION_PARAMETERS_FILE = new File(str);
        paramsReadMap = new ArraySet<>();
        paramsWriteMap = new ArraySet<>();
        deleteFlag = true;
    }

    public static void closeSecureFlag() {
        if (deleteFlag) {
            LAST_EXTENSION_PARAMETERS_FILE.delete();
            return;
        }
        File file = LAST_EXTENSION_PARAMETERS_FILE;
        fileToStringSet(file);
        ArraySet<String> arraySet = paramsReadMap;
        arraySet.remove("secure_password=1");
        paramsWriteMap = arraySet;
        arraySet.add("secure_password=0");
        stringSetToFile(file);
        arraySet.clear();
    }

    private static ArraySet<String> fileToStringSet(File file) {
        if (file == null) {
            return null;
        }
        serFilePermission(file);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                while (true) {
                    try {
                        String readLine = bufferedReader.readLine();
                        if (readLine == null) {
                            bufferedReader.close();
                            fileInputStream.close();
                            return paramsReadMap;
                        }
                        paramsReadMap.add(readLine);
                    } finally {
                    }
                }
            } finally {
            }
        } catch (IOException unused) {
            return null;
        }
    }

    public static void openSecureFlag() {
        if (deleteFlag) {
            LAST_EXTENSION_PARAMETERS_FILE.delete();
            return;
        }
        File file = LAST_EXTENSION_PARAMETERS_FILE;
        fileToStringSet(file);
        ArraySet<String> arraySet = paramsReadMap;
        arraySet.remove("secure_password=0");
        paramsWriteMap = arraySet;
        arraySet.add("secure_password=1");
        stringSetToFile(file);
        arraySet.clear();
    }

    private static void serFilePermission(File file) {
        file.setReadable(true, false);
        file.setWritable(true, false);
    }

    private static boolean stringSetToFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
                serFilePermission(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, false);
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
                try {
                    BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                    try {
                        if (paramsWriteMap.size() < 1) {
                            bufferedWriter.close();
                            outputStreamWriter.close();
                            fileOutputStream.close();
                            return false;
                        }
                        for (int i = 0; i < paramsWriteMap.size(); i++) {
                            bufferedWriter.write(paramsWriteMap.valueAt(i));
                            bufferedWriter.newLine();
                        }
                        bufferedWriter.close();
                        outputStreamWriter.close();
                        fileOutputStream.close();
                        return true;
                    } finally {
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException unused) {
            return false;
        }
    }
}
