package com.android.settings.emergency.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import com.android.settings.R;
import com.android.settings.emergency.util.UrlSpan;
import java.util.Locale;
import miui.os.Build;
import miuix.util.Log;

/* loaded from: classes.dex */
public class LicenseHelper {
    public static SpannableStringBuilder buildPolicyRevoke(final Context context) {
        Resources resources = context.getResources();
        String string = resources.getString(R.string.privacy_authorize_privacy_policy);
        String string2 = resources.getString(R.string.sos_privacy_policy_revoke_message_reject, string);
        UrlSpan.UrlSpanOnClickListener urlSpanOnClickListener = new UrlSpan.UrlSpanOnClickListener() { // from class: com.android.settings.emergency.util.LicenseHelper.5
            @Override // com.android.settings.emergency.util.UrlSpan.UrlSpanOnClickListener
            public void onClick() {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(LicenseHelper.getSosPrivacyUrl()));
                intent.putExtra("com.android.browser.application_id", context.getPackageName());
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("LicenseHelper", "Actvity was not found for intent, " + intent.toString(), e);
                }
            }
        };
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string2);
        int indexOf = string2.indexOf(string);
        spannableStringBuilder.setSpan(new UrlSpan(urlSpanOnClickListener), indexOf, string.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder buildPrivacyPolicyNotice(final Context context) {
        Resources resources = context.getResources();
        String string = resources.getString(R.string.privacy_authorize_privacy_policy);
        String string2 = CommonUtils.isSosNewFeatureSupport(context) ? resources.getString(R.string.sos_privacy_dialog_message_new_version, string) : resources.getString(R.string.sos_privacy_dialog_message_old_version, string);
        UrlSpan.UrlSpanOnClickListener urlSpanOnClickListener = new UrlSpan.UrlSpanOnClickListener() { // from class: com.android.settings.emergency.util.LicenseHelper.2
            @Override // com.android.settings.emergency.util.UrlSpan.UrlSpanOnClickListener
            public void onClick() {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(LicenseHelper.getSosPrivacyUrl()));
                intent.putExtra("com.android.browser.application_id", context.getPackageName());
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("LicenseHelper", "Actvity was not found for intent, " + intent.toString(), e);
                }
            }
        };
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string2);
        int indexOf = string2.indexOf(string);
        spannableStringBuilder.setSpan(new UrlSpan(urlSpanOnClickListener), indexOf, string.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder buildPrivacyPolicyNoticeDisagree(final Context context) {
        Resources resources = context.getResources();
        String string = resources.getString(R.string.privacy_authorize_privacy_policy);
        String string2 = resources.getString(R.string.sos_privacy_policy_change_message_reject, string);
        UrlSpan.UrlSpanOnClickListener urlSpanOnClickListener = new UrlSpan.UrlSpanOnClickListener() { // from class: com.android.settings.emergency.util.LicenseHelper.4
            @Override // com.android.settings.emergency.util.UrlSpan.UrlSpanOnClickListener
            public void onClick() {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(LicenseHelper.getSosPrivacyUrl()));
                intent.putExtra("com.android.browser.application_id", context.getPackageName());
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("LicenseHelper", "Actvity was not found for intent, " + intent.toString(), e);
                }
            }
        };
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string2);
        int indexOf = string2.indexOf(string);
        spannableStringBuilder.setSpan(new UrlSpan(urlSpanOnClickListener), indexOf, string.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder buildPrivacyPolicyNoticeUpdate(final Context context, String str) {
        Resources resources = context.getResources();
        String string = resources.getString(R.string.privacy_authorize_privacy_policy);
        String str2 = str + resources.getString(R.string.sos_privacy_policy_change_endtitle, string);
        UrlSpan.UrlSpanOnClickListener urlSpanOnClickListener = new UrlSpan.UrlSpanOnClickListener() { // from class: com.android.settings.emergency.util.LicenseHelper.3
            @Override // com.android.settings.emergency.util.UrlSpan.UrlSpanOnClickListener
            public void onClick() {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(LicenseHelper.getSosPrivacyUrl()));
                intent.putExtra("com.android.browser.application_id", context.getPackageName());
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Log.e("LicenseHelper", "Actvity was not found for intent, " + intent.toString(), e);
                }
            }
        };
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
        int indexOf = str2.indexOf(string);
        spannableStringBuilder.setSpan(new UrlSpan(urlSpanOnClickListener), indexOf, string.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public static String getSosPrivacyUrl() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        if (Build.IS_INTERNATIONAL_BUILD) {
            return "https://privacy.mi.com/sos-International/" + language + "_" + country;
        }
        return "https://privacy.mi.com/SOS/" + language + "_" + country;
    }
}
