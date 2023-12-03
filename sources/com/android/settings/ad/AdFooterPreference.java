package com.android.settings.ad;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.R$id;
import com.android.settingslib.R$layout;
import com.android.settingslib.R$string;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.util.MiStatInterfaceUtils;
import miui.os.Build;
import miui.settings.commonlib.MemoryOptimizationUtil;

/* loaded from: classes.dex */
public class AdFooterPreference extends Preference {
    private final PreferenceFragmentCompat mFragment;

    public AdFooterPreference(PreferenceFragmentCompat preferenceFragmentCompat, Context context) {
        super(context);
        this.mFragment = preferenceFragmentCompat;
        init();
    }

    private void init() {
        setKey("footer_preference");
        setOrder(2147483646);
        setLayoutResource(R$layout.ad_service_instructions_layout);
        setSelectable(false);
        setSingleLineTitle(false);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(R$id.ad_service_instructions_link_text_view);
        textView.setText(Build.IS_INTERNATIONAL_BUILD ? R$string.about_ad_service_instructions_for_global : R$string.about_ad_service_instructions);
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.ad.AdFooterPreference.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                MiStatInterfaceUtils.trackEvent("AdServiceSettings_click_aaid_instructions");
                Intent intent = new Intent();
                intent.setClassName(MemoryOptimizationUtil.CONTROLLER_PKG, "com.android.settings.ad.AdInstructionsActivity");
                try {
                    AdFooterPreference.this.mFragment.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("AdFooterPreference", "Failed to find activity AdInstructionsActivity", e);
                }
            }
        });
    }
}
