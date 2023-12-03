package com.android.settings.display;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settings.utils.TabletUtils;

/* loaded from: classes.dex */
public class PaperRadioButtonPreference extends com.android.settingslib.miuisettings.preference.RadioButtonPreference implements View.OnClickListener {
    private Activity mActivity;
    private Context mContext;
    private Intent targetIntent;

    public PaperRadioButtonPreference(Context context) {
        this(context, null);
    }

    public PaperRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setWidgetLayoutResource(R.layout.preference_widget_detail);
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((ImageView) preferenceViewHolder.itemView.findViewById(R.id.detail_arrow)).setOnClickListener(this);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.targetIntent == null || !isChecked()) {
            return;
        }
        if (TabletUtils.IS_TABLET) {
            MiuiUtils.startPreferencePanel(this.mActivity, getFragment(), getExtras(), 0, getTitle(), null, 0);
        } else {
            this.mContext.startActivity(this.targetIntent);
        }
    }

    public void setTargetIntent(Activity activity, Intent intent) {
        this.mActivity = activity;
        this.targetIntent = intent;
    }
}
