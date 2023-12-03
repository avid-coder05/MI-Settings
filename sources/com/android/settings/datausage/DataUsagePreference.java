package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.R$attr;
import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.datausage.TemplatePreference;
import com.android.settingslib.miuisettings.preference.Preference;
import com.android.settingslib.net.DataUsageController;
import miui.telephony.MiuiHeDuoHaoUtil;

/* loaded from: classes.dex */
public class DataUsagePreference extends Preference implements TemplatePreference {
    private int mSubId;
    private NetworkTemplate mTemplate;
    private int mTitleRes;

    public DataUsagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{16843233}, TypedArrayUtils.getAttr(context, R$attr.preferenceStyle, 16842894), 0);
        this.mTitleRes = obtainStyledAttributes.getResourceId(0, 0);
        obtainStyledAttributes.recycle();
    }

    DataUsageController getDataUsageController() {
        return new DataUsageController(getContext());
    }

    @Override // androidx.preference.Preference
    public Intent getIntent() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("network_template", this.mTemplate);
        bundle.putInt(MiuiHeDuoHaoUtil.SUB_ID, this.mSubId);
        bundle.putInt("network_type", !this.mTemplate.isMatchRuleMobile() ? 1 : 0);
        SubSettingLauncher sourceMetricsCategory = new SubSettingLauncher(getContext()).setArguments(bundle).setDestination(DataUsageList.class.getName()).setSourceMetricsCategory(0);
        if (this.mTemplate.isMatchRuleMobile()) {
            sourceMetricsCategory.setTitleRes(R.string.app_cellular_data_usage);
        } else {
            sourceMetricsCategory.setTitleRes(this.mTitleRes);
        }
        return sourceMetricsCategory.toIntent();
    }

    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
        this.mTemplate = networkTemplate;
        this.mSubId = i;
        DataUsageController dataUsageController = getDataUsageController();
        if (this.mTemplate.isMatchRuleMobile()) {
            setTitle(R.string.app_cellular_data_usage);
        } else {
            DataUsageController.DataUsageInfo dataUsageInfo = dataUsageController.getDataUsageInfo(this.mTemplate);
            setTitle(this.mTitleRes);
            setSummary(getContext().getString(R.string.data_usage_template, DataUsageUtils.formatDataUsage(getContext(), dataUsageInfo.usageLevel), dataUsageInfo.period));
        }
        if (dataUsageController.getHistoricalUsageLevel(networkTemplate) > 0) {
            setIntent(getIntent());
            return;
        }
        setIntent(null);
        setEnabled(false);
    }
}
