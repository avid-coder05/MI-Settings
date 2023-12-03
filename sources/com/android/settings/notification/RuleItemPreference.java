package com.android.settings.notification;

import android.content.Context;
import android.net.Uri;
import android.service.notification.ZenModeConfig;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.dndmode.Alarm;
import com.android.settingslib.miuisettings.preference.Preference;
import miuix.slidingwidget.widget.SlidingButton;

/* loaded from: classes2.dex */
public class RuleItemPreference extends Preference {
    private CompoundButton.OnCheckedChangeListener check;
    private boolean checked;
    private View.OnClickListener click;
    private SlidingButton mCheckBox;
    private Context mContext;
    private TextView mSummary;
    private TextView mTitle;
    private Uri ruleConditionId;
    private String ruleid;
    private String title;

    public RuleItemPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public RuleItemPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public RuleItemPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public RuleItemPreference(Context context, String str, boolean z, String str2, Uri uri, View.OnClickListener onClickListener, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        this(context);
        this.title = str;
        this.click = onClickListener;
        this.check = onCheckedChangeListener;
        this.checked = z;
        this.ruleid = str2;
        this.ruleConditionId = uri;
    }

    public TextView getmTitle() {
        return this.mTitle;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.mCheckBox = (SlidingButton) view.findViewById(R.id.enabled);
        this.mTitle = (TextView) view.findViewById(R.id.ruletitle);
        this.mSummary = (TextView) view.findViewById(R.id.rulesummary);
        this.mTitle.setText(this.title);
        this.mTitle.setTag(this.ruleid);
        this.mSummary.setTag(this.ruleid);
        this.mSummary.setText(zenRuleAnalysis());
        this.mCheckBox.setTag(this.ruleid);
        this.mTitle.setOnClickListener(this.click);
        this.mSummary.setOnClickListener(this.click);
        this.mCheckBox.setChecked(this.checked);
        this.mCheckBox.setOnCheckedChangeListener(this.check);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.rule_item);
        return super.onCreateView(viewGroup);
    }

    public String zenRuleAnalysis() {
        StringBuilder sb = new StringBuilder();
        ZenModeConfig.ScheduleInfo tryParseScheduleConditionId = ZenModeConfig.tryParseScheduleConditionId(this.ruleConditionId);
        sb.append((((tryParseScheduleConditionId.startHour * 60) + tryParseScheduleConditionId.startMinute) / 60) + ":");
        int i = ((tryParseScheduleConditionId.startHour * 60) + tryParseScheduleConditionId.startMinute) % 60;
        if (i < 10) {
            sb.append("0");
        }
        sb.append(i + "--");
        sb.append((((tryParseScheduleConditionId.endHour * 60) + tryParseScheduleConditionId.endMinute) / 60) + ":");
        int i2 = ((tryParseScheduleConditionId.endHour * 60) + tryParseScheduleConditionId.endMinute) % 60;
        if (i2 < 10) {
            sb.append("0");
        }
        sb.append(i2 + ",");
        sb.append(new Alarm.DaysOfWeek(SilentModeUtils.parseDays(tryParseScheduleConditionId.days)).toString(this.mContext, true));
        return sb.toString();
    }
}
