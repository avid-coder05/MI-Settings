package com.android.settings.backup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.provider.SettingsProvider;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class DetailQuestionFragment extends SettingsPreferenceFragment {
    private TextView summaryView;
    private TextView titleView;
    private Map<String, Integer> titleMap = new HashMap();
    private Map<String, Integer> summaryMap = new HashMap();

    public DetailQuestionFragment() {
        this.titleMap.put("how_backup", Integer.valueOf(R.string.how_backup_title));
        this.titleMap.put("how_restore_backup", Integer.valueOf(R.string.how_restore_backup_title));
        this.titleMap.put("how_encrypt", Integer.valueOf(R.string.how_encrypt_title));
        this.titleMap.put("backup_difference", Integer.valueOf(R.string.backup_difference_title));
        this.titleMap.put("backup_restore", Integer.valueOf(R.string.backup_restore_data_title));
        this.titleMap.put("datasize_decrease", Integer.valueOf(R.string.datasize_decrease_title));
        this.summaryMap.put("how_backup", Integer.valueOf(R.string.how_backup_content));
        this.summaryMap.put("how_restore_backup", Integer.valueOf(R.string.how_restore_backup_content));
        this.summaryMap.put("how_encrypt", Integer.valueOf(R.string.how_encrypt_content));
        this.summaryMap.put("backup_difference", Integer.valueOf(R.string.backup_difference_content));
        this.summaryMap.put("backup_restore", Integer.valueOf(R.string.backup_restore_data_content));
        this.summaryMap.put("datasize_decrease", Integer.valueOf(R.string.datasize_decrease_content));
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.detail_question_lyt, viewGroup, false);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.titleView = (TextView) view.findViewById(R.id.detail_title);
        this.summaryView = (TextView) view.findViewById(R.id.detail_description);
        String string = getIntent().getExtras().getString(SettingsProvider.ARGS_KEY, null);
        if (string == null) {
            string = getArguments().getString(SettingsProvider.ARGS_KEY);
        }
        if (string != null) {
            this.titleView.setText(getResources().getString(this.titleMap.get(string).intValue()));
            this.summaryView.setText(getResources().getString(this.summaryMap.get(string).intValue()));
        }
    }
}
