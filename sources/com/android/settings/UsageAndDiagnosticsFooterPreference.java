package com.android.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.util.MiStatInterfaceUtils;

/* loaded from: classes.dex */
public class UsageAndDiagnosticsFooterPreference extends Preference {
    private final PreferenceFragmentCompat mFragment;

    public UsageAndDiagnosticsFooterPreference(PreferenceFragmentCompat preferenceFragmentCompat, Context context) {
        super(context);
        this.mFragment = preferenceFragmentCompat;
        init();
    }

    private void init() {
        setKey("footer_preference");
        setOrder(2147483646);
        setLayoutResource(com.android.settingslib.R$layout.usage_and_diagnostic_layout);
        setSelectable(false);
        setSingleLineTitle(false);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.itemView.findViewById(com.android.settingslib.R$id.ad_service_instructions_link_text_view)).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.UsageAndDiagnosticsFooterPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiStatInterfaceUtils.trackEvent("AdServiceSettings_click_aaid_instructions");
                Intent intent = new Intent(MiuiUtils.getInstance().getViewLicenseAction());
                intent.putExtra("android.intent.extra.LICENSE_TYPE", 1);
                try {
                    UsageAndDiagnosticsFooterPreference.this.mFragment.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("AdFooterPreference", "Failed to find activity AdInstructionsActivity", e);
                }
            }
        });
    }
}
