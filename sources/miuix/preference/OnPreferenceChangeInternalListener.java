package miuix.preference;

import androidx.preference.Preference;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes5.dex */
public interface OnPreferenceChangeInternalListener {
    void notifyPreferenceChangeInternal(Preference preference);

    boolean onPreferenceChangeInternal(Preference preference, Object obj);
}
