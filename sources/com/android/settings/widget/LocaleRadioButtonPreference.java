package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.android.internal.app.LocalePicker;
import com.android.settings.MiuiUtils;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;
import java.util.Locale;
import miui.os.Build;

/* loaded from: classes2.dex */
public class LocaleRadioButtonPreference extends RadioButtonPreference {
    private final String LOCALE_TAIWAN;
    private LocalePicker.LocaleInfo mLocaleInfo;

    public LocaleRadioButtonPreference(Context context) {
        this(context, null);
    }

    public LocaleRadioButtonPreference(Context context, AttributeSet attributeSet) {
        super(context, null);
        this.LOCALE_TAIWAN = "zh_TW";
    }

    public LocalePicker.LocaleInfo getLocaleInfo() {
        return this.mLocaleInfo;
    }

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        TextView textView = (TextView) view.findViewById(16908310);
        if (Build.IS_GLOBAL_BUILD) {
            LocalePicker.LocaleInfo localeInfo = getLocaleInfo();
            Locale locale = localeInfo.getLocale();
            String overlayLocaleLanguageLabel = MiuiUtils.overlayLocaleLanguageLabel(getContext(), locale.toString(), localeInfo.toString());
            if (MiuiUtils.needOverlayTwLocale() && locale.toString().equals("zh_TW") && overlayLocaleLanguageLabel.length() >= 5) {
                overlayLocaleLanguageLabel = overlayLocaleLanguageLabel.substring(0, 5);
            }
            textView.setText(overlayLocaleLanguageLabel);
            textView.setTextLocale(localeInfo.getLocale());
        }
    }

    public void setLocaleInfo(LocalePicker.LocaleInfo localeInfo) {
        this.mLocaleInfo = localeInfo;
    }
}
