package com.android.settings.notification;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class SilentModeNetWorkAlarmSummaryPreference extends Preference {
    private Context mContext;
    private TextView summaryTextView;

    public SilentModeNetWorkAlarmSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        this.summaryTextView = (TextView) view.findViewById(R.id.summary);
        String string = this.mContext.getResources().getString(R.string.network_alarm_summary_reg);
        String string2 = this.mContext.getResources().getString(R.string.network_alarm_summary, string);
        int indexOf = string2.indexOf(string);
        int length = string.length() + indexOf;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string2);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(this.mContext.getResources().getColor(R.color.font_size_view_big_color)), indexOf, length, 33);
        this.summaryTextView.setMovementMethod(LinkMovementMethod.getInstance());
        spannableStringBuilder.setSpan(new ClickableSpan() { // from class: com.android.settings.notification.SilentModeNetWorkAlarmSummaryPreference.1
            @Override // android.text.style.ClickableSpan
            public void onClick(View view2) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.setClassName("com.android.settings", "com.android.settings.SubSettings");
                intent.putExtra(":settings:show_fragment", "com.android.settings.notification.SilentModeNetWorkAlarmAppFragment");
                intent.putExtra(":android:no_headers", true);
                SilentModeNetWorkAlarmSummaryPreference.this.getContext().startActivity(intent);
            }
        }, indexOf, length, 33);
        spannableStringBuilder.setSpan(new UnderlineSpan(), indexOf, length, 33);
        this.summaryTextView.setText(spannableStringBuilder);
        view.setPadding(0, 0, 0, 0);
        view.setBackgroundColor(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.miuisettings.preference.Preference
    public View onCreateView(ViewGroup viewGroup) {
        setLayoutResource(R.layout.silent_mode_network_alarm_summary_preference);
        return null;
    }
}
