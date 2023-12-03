package com.android.settings.credentials;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiCredentialsFragment extends SettingsPreferenceFragment {
    private Drawable queryDrawable(String str) {
        int identifier;
        Resources resources = getActivity().getResources();
        if (resources == null || (identifier = resources.getIdentifier(str, "drawable", getActivity().getPackageName())) == 0) {
            return null;
        }
        return resources.getDrawable(identifier);
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiCredentialsFragment.class.getName();
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        String carrierName;
        Drawable queryDrawable;
        String str;
        String str2;
        String str3;
        View inflate = layoutInflater.inflate(R.layout.credentials_settings, viewGroup, false);
        inflate.setImportantForAccessibility(4);
        ImageView imageView = (ImageView) inflate.findViewById(R.id.credentials_image);
        MiuiCredentialsUpdater.getInstance().getCacheDrawable(getActivity());
        if (Build.IS_GLOBAL_BUILD) {
            StringBuilder sb = new StringBuilder();
            if (TextUtils.isEmpty(MiuiCredentialsUpdater.getFactoryId())) {
                str = "";
            } else {
                str = "_" + MiuiCredentialsUpdater.getFactoryId();
            }
            if (TextUtils.isEmpty(MiuiCredentialsUpdater.getVendorFactoryId())) {
                str2 = "";
            } else {
                str2 = "_" + MiuiCredentialsUpdater.getVendorFactoryId();
            }
            if (TextUtils.isEmpty(MiuiCredentialsUpdater.getBootHWC())) {
                str3 = "";
            } else {
                str3 = "_" + MiuiCredentialsUpdater.getBootHWC();
            }
            carrierName = TextUtils.isEmpty(MiuiCredentialsUpdater.getCertNumber()) ? "" : MiuiCredentialsUpdater.getCertNumber();
            sb.append("credentials_image_");
            sb.append(carrierName);
            sb.append(str3);
            sb.append(str);
            queryDrawable = queryDrawable(sb.toString());
            if (queryDrawable == null) {
                sb.delete(0, sb.length());
                sb.append("credentials_image_");
                sb.append(carrierName);
                sb.append(str3);
                sb.append(str2);
                queryDrawable = queryDrawable(sb.toString());
            }
            if (queryDrawable == null) {
                sb.delete(0, sb.length());
                sb.append("credentials_image_");
                sb.append(carrierName);
                sb.append(str3);
                queryDrawable = queryDrawable(sb.toString());
            }
            if (queryDrawable == null) {
                sb.delete(0, sb.length());
                sb.append("credentials_image_");
                sb.append(carrierName);
                sb.append(str);
                queryDrawable = queryDrawable(sb.toString());
            }
            if (queryDrawable == null) {
                sb.delete(0, sb.length());
                sb.append("credentials_image_");
                sb.append(carrierName);
                sb.append(str2);
                queryDrawable = queryDrawable(sb.toString());
            }
        } else {
            StringBuilder sb2 = new StringBuilder();
            String certNumber = TextUtils.isEmpty(MiuiCredentialsUpdater.getCertNumber()) ? "" : MiuiCredentialsUpdater.getCertNumber();
            carrierName = TextUtils.isEmpty(MiuiCredentialsUpdater.getCarrierName()) ? "" : MiuiCredentialsUpdater.getCarrierName();
            sb2.append("credentials_image_");
            sb2.append(certNumber);
            sb2.append("_");
            sb2.append(carrierName);
            queryDrawable = queryDrawable(sb2.toString());
            if (queryDrawable == null) {
                sb2.delete(0, sb2.length());
                sb2.append("credentials_image_");
                sb2.append(carrierName);
                queryDrawable = queryDrawable(sb2.toString());
            }
        }
        if (queryDrawable == null) {
            queryDrawable = queryDrawable("credentials_image_" + MiuiCredentialsUpdater.getCertNumber());
        }
        if (queryDrawable == null) {
            queryDrawable = queryDrawable("credentials_image_default");
        }
        if (queryDrawable != null) {
            imageView.setImageDrawable(queryDrawable);
        } else {
            finish();
        }
        return inflate;
    }
}
