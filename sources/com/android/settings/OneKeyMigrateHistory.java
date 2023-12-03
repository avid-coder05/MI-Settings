package com.android.settings;

import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

/* loaded from: classes.dex */
public class OneKeyMigrateHistory extends SettingsPreferenceFragment {
    private ArrayList<HistoryRecord> historyRecords;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class HistoryRecord {
        String deviceName;
        String recordTime;

        public HistoryRecord(String str, String str2) {
            this.deviceName = str;
            this.recordTime = str2;
        }

        public String getDeviceName() {
            return this.deviceName;
        }

        public String getRecordTime() {
            return this.recordTime;
        }
    }

    private void addPreference() {
        ArrayList<HistoryRecord> arrayList = this.historyRecords;
        if (arrayList == null || arrayList.size() == 0) {
            return;
        }
        for (int i = 0; i < this.historyRecords.size(); i++) {
            Preference preference = new Preference(getPrefContext());
            preference.setTitle(String.format(getContext().getResources().getString(R.string.one_key_migrate_record), this.historyRecords.get(i).getDeviceName()));
            preference.setSummary(this.historyRecords.get(i).getRecordTime());
            getPreferenceScreen().addPreference(preference);
        }
    }

    private ArrayList<HistoryRecord> parseData() {
        String string = Settings.Secure.getString(getContext().getContentResolver(), "data_trans_history");
        ArrayList<HistoryRecord> arrayList = new ArrayList<>();
        if (string == null || TextUtils.isEmpty(string)) {
            return new ArrayList<>();
        }
        try {
            JSONArray jSONArray = new JSONArray(string);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date();
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = (JSONObject) jSONArray.get(i);
                String string2 = jSONObject.getString("device");
                date.setTime(Long.parseLong(jSONObject.getString("time")));
                arrayList.add(new HistoryRecord(string2, simpleDateFormat.format(date)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return OneKeyMigrateHistory.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        addPreferencesFromResource(R.xml.mi_transfer_history);
        this.historyRecords = parseData();
        addPreference();
    }
}
