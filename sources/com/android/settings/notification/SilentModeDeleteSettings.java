package com.android.settings.notification;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.ZenModeConfig;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.notification.SilentModeSettingsBase;
import com.android.settingslib.miuisettings.preference.Preference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes2.dex */
public class SilentModeDeleteSettings extends SilentModeSettingsBase {
    private PreferenceCategory mCategory;
    private List<String> mDeletedRuleId;
    OnClickDeleteBtnListener mOnClickDelBtnListener = new OnClickDeleteBtnListener() { // from class: com.android.settings.notification.SilentModeDeleteSettings.1
        @Override // com.android.settings.notification.SilentModeDeleteSettings.OnClickDeleteBtnListener
        public void onClick(String str) {
            SilentModeDeleteSettings.this.mDeletedRuleId.add(str);
            SilentModeDeleteSettings.this.updateControls();
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public class CustomRuleItemPreference extends Preference {
        private Context mContext;
        private ImageView mDeleteBtn;
        View.OnClickListener mDeleteClickListener;
        private String mId;
        private OnClickDeleteBtnListener mOnClickDeleteBtnListener;
        private TextView mTitleView;

        public CustomRuleItemPreference(Context context) {
            super(context);
            this.mDeleteClickListener = new View.OnClickListener() { // from class: com.android.settings.notification.SilentModeDeleteSettings.CustomRuleItemPreference.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    if (CustomRuleItemPreference.this.mOnClickDeleteBtnListener != null) {
                        CustomRuleItemPreference.this.mOnClickDeleteBtnListener.onClick(CustomRuleItemPreference.this.mId);
                    }
                }
            };
            this.mContext = context;
            setLayoutResource(R.xml.dndm_custom_rule_item);
        }

        @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
        public void onBindView(View view) {
            super.onBindView(view);
            ImageView imageView = (ImageView) view.findViewById(R.id.delete_btn);
            this.mDeleteBtn = imageView;
            imageView.setClickable(true);
            this.mDeleteBtn.setFocusable(true);
            this.mDeleteBtn.setOnClickListener(this.mDeleteClickListener);
            this.mDeleteBtn.setContentDescription(this.mContext.getResources().getString(R.string.delete_rule));
            TextView textView = (TextView) view.findViewById(R.id.title);
            this.mTitleView = textView;
            textView.setText(getTitle());
        }

        public void setData(String str, String str2) {
            this.mId = str;
            setTitle(str2);
        }

        public void setOnDeleteBtnClickListener(OnClickDeleteBtnListener onClickDeleteBtnListener) {
            this.mOnClickDeleteBtnListener = onClickDeleteBtnListener;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface OnClickDeleteBtnListener {
        void onClick(String str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateControls() {
        this.mCategory.removeAll();
        if (this.mConfig == null) {
            return;
        }
        SilentModeSettingsBase.ZenRuleInfo[] sortedRules = sortedRules();
        for (int i = 0; i < sortedRules.length; i++) {
            if (!this.mDeletedRuleId.contains(sortedRules[i].id)) {
                String str = sortedRules[i].id;
                ZenModeConfig.ZenRule zenRule = sortedRules[i].rule;
                CustomRuleItemPreference customRuleItemPreference = new CustomRuleItemPreference(getThemedContext());
                customRuleItemPreference.setData(str, zenRule.name);
                customRuleItemPreference.setOnDeleteBtnClickListener(this.mOnClickDelBtnListener);
                this.mCategory.addPreference(customRuleItemPreference);
            }
        }
    }

    public boolean commitRules() {
        List<String> list = this.mDeletedRuleId;
        boolean z = false;
        if (list != null) {
            Iterator<String> it = list.iterator();
            while (it.hasNext()) {
                z |= removeZenRule(it.next());
            }
        }
        return z;
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.automation_rules_settings);
        this.mCategory = (PreferenceCategory) findPreference("key_auto_rules");
        this.mDeletedRuleId = new ArrayList();
        updateControls();
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeChanged() {
    }

    @Override // com.android.settings.notification.SilentModeSettingsBase
    protected void onZenModeConfigChanged() {
        updateControls();
    }
}
