package miui.cloud.backup.data;

import android.content.SharedPreferences;
import android.util.Log;
import java.util.Map;

/* loaded from: classes3.dex */
public class PrefsBackupHelper {
    private static final String TAG = "PrefsBackupHelper";

    /* loaded from: classes3.dex */
    public static class PrefEntry {
        private String mCloudKey;
        private Object mDefaultValue;
        private String mLocalKey;
        private Class<?> mValueClass;

        private PrefEntry(String str, String str2, Class<?> cls, Object obj) {
            this.mCloudKey = str;
            this.mLocalKey = str2;
            this.mValueClass = cls;
            this.mDefaultValue = obj;
        }

        public static PrefEntry createBoolEntry(String str, String str2) {
            return new PrefEntry(str, str2, Boolean.class, null);
        }

        public static PrefEntry createBoolEntry(String str, String str2, boolean z) {
            return new PrefEntry(str, str2, Boolean.class, Boolean.valueOf(z));
        }

        public static PrefEntry createIntEntry(String str, String str2) {
            return new PrefEntry(str, str2, Integer.class, null);
        }

        public static PrefEntry createIntEntry(String str, String str2, int i) {
            return new PrefEntry(str, str2, Integer.class, Integer.valueOf(i));
        }

        public static PrefEntry createLongEntry(String str, String str2) {
            return new PrefEntry(str, str2, Long.class, null);
        }

        public static PrefEntry createLongEntry(String str, String str2, long j) {
            return new PrefEntry(str, str2, Long.class, Long.valueOf(j));
        }

        public static PrefEntry createStringEntry(String str, String str2) {
            return new PrefEntry(str, str2, String.class, null);
        }

        public static PrefEntry createStringEntry(String str, String str2, String str3) {
            return new PrefEntry(str, str2, String.class, str3);
        }

        public String getCloudKey() {
            return this.mCloudKey;
        }

        public Object getDefaultValue() {
            return this.mDefaultValue;
        }

        public String getLocalKey() {
            return this.mLocalKey;
        }

        public Class<?> getValueClass() {
            return this.mValueClass;
        }
    }

    private PrefsBackupHelper() {
    }

    public static void backup(SharedPreferences sharedPreferences, DataPackage dataPackage, PrefEntry[] prefEntryArr) {
        Map<String, ?> all = sharedPreferences.getAll();
        for (PrefEntry prefEntry : prefEntryArr) {
            Object obj = all.get(prefEntry.getLocalKey());
            if (obj != null) {
                if (obj.getClass() != prefEntry.getValueClass()) {
                    throw new IllegalStateException("Preference type of " + prefEntry.getLocalKey() + " mismatched. actual type = " + obj.getClass().getSimpleName() + ", expected type = " + prefEntry.getValueClass().getSimpleName());
                }
                dataPackage.addKeyValue(prefEntry.getCloudKey(), obj.toString());
            } else if (prefEntry.getDefaultValue() != null) {
                dataPackage.addKeyValue(prefEntry.getCloudKey(), prefEntry.getDefaultValue().toString());
            }
        }
    }

    public static void restore(SharedPreferences sharedPreferences, DataPackage dataPackage, PrefEntry[] prefEntryArr) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        for (PrefEntry prefEntry : prefEntryArr) {
            try {
                KeyStringSettingItem keyStringSettingItem = (KeyStringSettingItem) dataPackage.get(prefEntry.getCloudKey());
                if (keyStringSettingItem != null) {
                    String value = keyStringSettingItem.getValue();
                    if (prefEntry.getValueClass() == Integer.class) {
                        edit.putInt(prefEntry.getLocalKey(), Integer.parseInt(value));
                    } else if (prefEntry.getValueClass() == Long.class) {
                        edit.putLong(prefEntry.getLocalKey(), Long.parseLong(value));
                    } else if (prefEntry.getValueClass() == Boolean.class) {
                        edit.putBoolean(prefEntry.getLocalKey(), Boolean.parseBoolean(value));
                    } else if (prefEntry.getValueClass() == String.class) {
                        edit.putString(prefEntry.getLocalKey(), value);
                    }
                } else {
                    edit.remove(prefEntry.getLocalKey());
                }
            } catch (ClassCastException unused) {
                Log.e(TAG, "entry " + prefEntry.getCloudKey() + " is not KeyStringSettingItem");
            }
        }
        edit.commit();
    }
}
