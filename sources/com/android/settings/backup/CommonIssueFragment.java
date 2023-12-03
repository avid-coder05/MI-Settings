package com.android.settings.backup;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.provider.SettingsProvider;
import com.android.settings.utils.TabletUtils;
import com.android.settingslib.util.MiStatInterfaceUtils;
import com.android.settingslib.util.OneTrackInterfaceUtils;
import miui.os.Build;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes.dex */
public class CommonIssueFragment extends SettingsPreferenceFragment {
    private Button feedBackBtn;
    private View mRootView;

    private void traceEvent(String str) {
        String str2;
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2125259402:
                if (str.equals("how_encrypt")) {
                    c = 0;
                    break;
                }
                break;
            case -2007578662:
                if (str.equals("backup_difference")) {
                    c = 1;
                    break;
                }
                break;
            case -547228574:
                if (str.equals("how_restore_backup")) {
                    c = 2;
                    break;
                }
                break;
            case 291151794:
                if (str.equals("datasize_decrease")) {
                    c = 3;
                    break;
                }
                break;
            case 526279825:
                if (str.equals("how_backup")) {
                    c = 4;
                    break;
                }
                break;
            case 1565658385:
                if (str.equals("backup_restore")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                str2 = "backup_help_q3";
                break;
            case 1:
                str2 = "backup_help_q4";
                break;
            case 2:
                str2 = "backup_help_q2";
                break;
            case 3:
                str2 = "backup_help_q6";
                break;
            case 4:
                str2 = "backup_help_q1";
                break;
            case 5:
                str2 = "backup_help_q5";
                break;
            default:
                str2 = "";
                break;
        }
        if (TextUtils.equals(str2, "")) {
            return;
        }
        MiStatInterfaceUtils.trackEvent(str2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R.xml.common_issue_headers;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        if (this.mRootView == null) {
            View inflate = layoutInflater.inflate(R.layout.common_issue_lyt, viewGroup, false);
            this.mRootView = inflate;
            ((ViewGroup) inflate.findViewById(R.id.prefs_container)).addView(super.onCreateView(layoutInflater, viewGroup, bundle));
            View view = (View) getListView().getParent();
            if (view instanceof SpringBackLayout) {
                view.setEnabled(false);
            }
        } else {
            super.onCreateView(layoutInflater, viewGroup, bundle);
        }
        return this.mRootView;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String key = preference.getKey();
        if (key != null) {
            Bundle bundle = new Bundle();
            bundle.putString(SettingsProvider.ARGS_KEY, key);
            if (TabletUtils.IS_TABLET) {
                new SubSettingLauncher(getActivity()).setDestination(DetailQuestionFragment.class.getName()).setArguments(bundle).setSourceMetricsCategory(0).launch();
                traceEvent(key);
                return true;
            }
            Intent intent = getIntent();
            intent.setClass(getActivity(), DetailQuestionActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            traceEvent(key);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Button button = (Button) view.findViewById(R.id.feedback_btn);
        this.feedBackBtn = button;
        button.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.backup.CommonIssueFragment.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                Intent intent = new Intent();
                intent.setComponent(Build.IS_INTERNATIONAL_BUILD ? new ComponentName("com.miui.miservice", "com.miui.miservice.main.MainActivity") : new ComponentName("com.miui.bugreport", "com.miui.bugreport.ui.FeedbackActivity"));
                Bundle bundle2 = new Bundle();
                bundle2.putInt(":settings:show_fragment_title_resid", R.string.feedback_btn_text);
                intent.putExtras(bundle2);
                CommonIssueFragment.this.startActivity(intent);
                MiStatInterfaceUtils.trackEvent("backup_help_button");
                OneTrackInterfaceUtils.track("backup_help_button", null);
            }
        });
        if (MiuiUtils.isEasyMode(getContext())) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.feedBackBtn.getLayoutParams();
            marginLayoutParams.setMargins(marginLayoutParams.getMarginStart(), 0, marginLayoutParams.getMarginEnd(), 0);
            this.feedBackBtn.setLayoutParams(marginLayoutParams);
        }
    }
}
